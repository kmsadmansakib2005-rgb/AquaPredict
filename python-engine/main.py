
import json
import sys

from tank_model import euler_method as tank_euler

from numerical_methods.root_finding import (
    bisection, false_position, newton_raphson
)

from numerical_methods.linear_equations import (
    gauss_elimination, gauss_jordan
)

from numerical_methods.ode_methods import (
    euler_method, modified_euler_method, rk4_method
)


def process_request(data):

    operation = data["operation"]

    if operation == "tank_simulation":

        times, levels = tank_euler(
            data["capacity"],
            data["initial_water"],
            data["inflow_rate"],
            data["outflow_rate"],
            data["simulation_time"],
            data["time_step"]
        )

        return {
            "times": times,
            "water_levels": levels,
            "final_volume": levels[-1]
        }

    elif operation == "root_finding":

        method = data["method"].lower()

        initial_water = data["initial_water"]
        net_rate = data["inflow_rate"] - data["outflow_rate"]
        target_water = data["target_water"]

        if method == "bisection":
            root, iterations = bisection(
                initial_water, net_rate, target_water,
                data["a"], data["b"]
            )

        elif method == "false_position":
            root, iterations = false_position(
                initial_water, net_rate, target_water,
                data["a"], data["b"]
            )

        elif method == "newton_raphson":
            root, iterations = newton_raphson(
                initial_water, net_rate, target_water,
                data["initial_guess"]
            )

        else:
            raise ValueError("Unknown root finding method.")

        return {
            "method": method,
            "root": root,
            "iterations": iterations
        }

    elif operation == "linear_equations":

        method = data["method"].lower()
        A = data["A"]
        B = data["B"]

        if method == "gauss":
            solution = gauss_elimination(A, B)

        elif method == "gauss_jordan":
            solution = gauss_jordan(A, B)

        else:
            raise ValueError("Unknown linear equation method.")

        return {
            "method": method,
            "solution": solution
        }

    elif operation == "ode_methods":

        method = data["method"].lower()

        initial_water = data["initial_water"]
        simulation_time = data["simulation_time"]
        time_step = data["time_step"]
        capacity = data["capacity"]

        inflow = lambda t: (
            data["inflow_base"] + data["inflow_increase"] * t
        )

        outflow = lambda t: data["outflow_rate"]

        if method == "euler":
            times, levels = euler_method(
                initial_water, simulation_time, time_step,
                capacity, inflow, outflow
            )

        elif method == "modified_euler":
            times, levels = modified_euler_method(
                initial_water, simulation_time, time_step,
                capacity, inflow, outflow
            )

        elif method == "rk4":
            times, levels = rk4_method(
                initial_water, simulation_time, time_step,
                capacity, inflow, outflow
            )

        else:
            raise ValueError("Unknown ODE method.")

        return {
            "method": method,
            "times": times,
            "water_levels": levels,
            "final_volume": levels[-1]
        }

    else:
        raise ValueError("Unknown operation.")


if __name__ == "__main__":

    try:
        request = json.loads(sys.stdin.read())
        result = process_request(request)

        response = {
            "success": True,
            "result": result
        }

    except Exception as error:
        response = {
            "success": False,
            "error": str(error)
        }

    print(json.dumps(response))

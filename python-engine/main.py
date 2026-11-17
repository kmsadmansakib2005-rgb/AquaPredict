
import json
import sys

from tank_model import euler_method as tank_euler, automatic_pump_simulation

from numerical_methods.root_finding import (
    bisection, false_position, newton_raphson
)

from numerical_methods.ode_methods import (
    euler_method, modified_euler_method, rk4_method
)


def process_request(data):

    operation = data["operation"]

    if operation == "tank_simulation":

        if data.get("automatic_pump", False):
            return automatic_pump_simulation(
                data["capacity"],
                data["initial_water"],
                data["inflow_rate"],
                data["outflow_rate"],
                data["simulation_time"],
                data["time_step"],
                data.get("pump_on_below_percent", 30),
                data.get("pump_off_at_percent", 90)
            )

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
            "final_volume": levels[-1],
            "pump_enabled": False,
            "pump_on": False,
            "pump_events": [],
            "pump_states": []
        }

    elif operation == "root_finding":

        method = data["method"].lower()

        initial_water = data["initial_water"]
        net_rate = data["inflow_rate"] - data["outflow_rate"]
        target_water = data["target_water"]

        if method == "bisection":
            root, iterations = bisection(
                initial_water, net_rate, 0, target_water,
                data["a"], data["b"]
            )

        elif method == "false_position":
            root, iterations = false_position(
                initial_water, net_rate, 0, target_water,
                data["a"], data["b"]
            )

        elif method == "newton_raphson":
            root, iterations = newton_raphson(
                initial_water, net_rate, 0, target_water,
                data["initial_guess"]
            )

        else:
            raise ValueError("Unknown root finding method.")

        return {
            "method": method,
            "root": root,
            "iterations": iterations
        }

    elif operation == "predictor":

        method = data["method"].lower()
        capacity = float(data["capacity"])
        initial_water = float(data["initial_water"])
        target_percent = float(data["target_percent"])
        target_water = capacity * target_percent / 100.0
        inflow_base = float(data["inflow_base"])
        inflow_increase = float(data["inflow_increase"])
        outflow_rate = float(data["outflow_rate"])

        if capacity <= 0 or not 0 <= initial_water <= capacity:
            raise ValueError("Initial water and tank capacity are invalid.")
        if not 0 <= target_percent <= 100:
            raise ValueError("Target level must be between 0 and 100 percent.")
        if min(inflow_base, inflow_increase, outflow_rate) < 0:
            raise ValueError("Flow rates cannot be negative.")
        if target_water <= initial_water:
            return {"method": method, "target_percent": target_percent,
                    "target_water": target_water, "predicted_time": 0.0, "iterations": 0}

        if method in ("bisection", "false_position", "newton_raphson"):
            a, b = float(data["a"]), float(data["b"])
            guess = float(data["initial_guess"])
            if method == "bisection":
                predicted_time, iterations = bisection(initial_water, inflow_base, outflow_rate,
                    target_water, a, b, inflow_increase=inflow_increase)
            elif method == "false_position":
                predicted_time, iterations = false_position(initial_water, inflow_base, outflow_rate,
                    target_water, a, b, inflow_increase=inflow_increase)
            else:
                predicted_time, iterations = newton_raphson(initial_water, inflow_base, outflow_rate,
                    target_water, guess, inflow_increase=inflow_increase)
            return {"method": method, "target_percent": target_percent, "target_water": target_water,
                    "predicted_time": predicted_time, "iterations": iterations}

        simulation_time, time_step = float(data["max_time"]), float(data["time_step"])
        if simulation_time <= 0 or time_step <= 0:
            raise ValueError("Maximum prediction time and time step must be positive.")
        inflow = lambda t: inflow_base + inflow_increase * t
        outflow = lambda t: outflow_rate
        if method == "euler":
            times, levels = euler_method(initial_water, simulation_time, time_step, capacity, inflow, outflow)
        elif method == "modified_euler":
            times, levels = modified_euler_method(initial_water, simulation_time, time_step, capacity, inflow, outflow)
        elif method == "rk4":
            times, levels = rk4_method(initial_water, simulation_time, time_step, capacity, inflow, outflow)
        else:
            raise ValueError("Unknown prediction method.")

        for index in range(1, len(levels)):
            previous_level, current_level = levels[index - 1], levels[index]
            if previous_level < target_water <= current_level:
                change = current_level - previous_level
                fraction = 0.0 if change == 0 else (target_water - previous_level) / change
                predicted_time = times[index - 1] + fraction * (times[index] - times[index - 1])
                return {"method": method, "target_percent": target_percent, "target_water": target_water,
                        "predicted_time": predicted_time, "steps": index}
        raise ValueError("The target level was not reached within the maximum prediction time. Increase the maximum time or adjust the flow rates.")

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

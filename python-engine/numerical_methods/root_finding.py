# AquaPredict - Root Finding Methods

# Volume model: inflow changes linearly with time.
def water_equation(time, initial_water, inflow_rate, outflow_rate, target_water, inflow_increase=0):
    return (initial_water + (inflow_rate - outflow_rate) * time
            + 0.5 * inflow_increase * time * time - target_water)


def bisection(initial_water, inflow_rate, outflow_rate, target_water,
              a, b, tolerance=0.0001, max_iterations=1000, inflow_increase=0):
    fa = water_equation(a, initial_water, inflow_rate, outflow_rate, target_water, inflow_increase)
    fb = water_equation(b, initial_water, inflow_rate, outflow_rate, target_water, inflow_increase)
    if fa * fb > 0:
        raise ValueError("Root is not bracketed between the lower and upper time bounds.")
    iterations = 0
    while iterations < max_iterations:
        mid = (a + b) / 2
        fm = water_equation(mid, initial_water, inflow_rate, outflow_rate, target_water, inflow_increase)
        iterations += 1
        if abs(fm) < tolerance or abs(b - a) < tolerance:
            return mid, iterations
        if fa * fm < 0:
            b, fb = mid, fm
        else:
            a, fa = mid, fm
    raise ValueError("Bisection method did not converge.")


def false_position(initial_water, inflow_rate, outflow_rate, target_water,
                   a, b, tolerance=0.0001, max_iterations=1000, inflow_increase=0):
    fa = water_equation(a, initial_water, inflow_rate, outflow_rate, target_water, inflow_increase)
    fb = water_equation(b, initial_water, inflow_rate, outflow_rate, target_water, inflow_increase)
    if fa * fb > 0:
        raise ValueError("Root is not bracketed between the lower and upper time bounds.")
    iterations = 0
    while iterations < max_iterations:
        denominator = fb - fa
        if denominator == 0:
            raise ValueError("Division by zero in False Position.")
        x = (a * fb - b * fa) / denominator
        fx = water_equation(x, initial_water, inflow_rate, outflow_rate, target_water, inflow_increase)
        iterations += 1
        if abs(fx) < tolerance:
            return x, iterations
        if fa * fx < 0:
            b, fb = x, fx
        else:
            a, fa = x, fx
    raise ValueError("False Position method did not converge.")


def newton_raphson(initial_water, inflow_rate, outflow_rate, target_water,
                   initial_guess, tolerance=0.0001, max_iterations=1000, inflow_increase=0):
    x = initial_guess
    for iterations in range(1, max_iterations + 1):
        fx = water_equation(x, initial_water, inflow_rate, outflow_rate, target_water, inflow_increase)
        derivative = inflow_rate - outflow_rate + inflow_increase * x
        if abs(derivative) < 1e-12:
            raise ValueError("The water-level derivative is zero; Newton-Raphson cannot continue.")
        new_x = x - fx / derivative
        if new_x < 0:
            raise ValueError("Newton-Raphson produced a negative time estimate. Try another initial guess.")
        if abs(new_x - x) < tolerance:
            return new_x, iterations
        x = new_x
    raise ValueError("Newton-Raphson method did not converge.")


# AquaPredict - Root Finding Methods


# Water Level Equation
def water_equation(time, initial_water, net_rate, target_water):

    return initial_water + net_rate * time - target_water


# Bisection Method
def bisection(
    initial_water, net_rate, target_water,
    a, b, tolerance=0.0001, max_iterations=1000
):

    fa = water_equation(a, initial_water, net_rate, target_water)
    fb = water_equation(b, initial_water, net_rate, target_water)

    if fa * fb > 0:
        raise ValueError("Root is not bracketed between a and b.")

    iterations = 0

    while iterations < max_iterations:

        mid = (a + b) / 2

        fm = water_equation(
            mid, initial_water, net_rate, target_water
        )

        iterations += 1

        if abs(fm) < tolerance or abs(b - a) < tolerance:
            return mid, iterations

        if fa * fm < 0:
            b = mid
            fb = fm
        else:
            a = mid
            fa = fm

    raise ValueError("Bisection method did not converge.")


# False Position Method
def false_position(
    initial_water, net_rate, target_water,
    a, b, tolerance=0.0001, max_iterations=1000
):

    fa = water_equation(a, initial_water, net_rate, target_water)
    fb = water_equation(b, initial_water, net_rate, target_water)

    if fa * fb > 0:
        raise ValueError("Root is not bracketed between a and b.")

    iterations = 0

    while iterations < max_iterations:

        denominator = fb - fa

        if denominator == 0:
            raise ValueError("Division by zero in False Position.")

        x = (a * fb - b * fa) / denominator

        fx = water_equation(
            x, initial_water, net_rate, target_water
        )

        iterations += 1

        if abs(fx) < tolerance:
            return x, iterations

        if fa * fx < 0:
            b = x
            fb = fx
        else:
            a = x
            fa = fx

    raise ValueError("False Position method did not converge.")


# Newton-Raphson Method
def newton_raphson(
    initial_water, net_rate, target_water,
    initial_guess, tolerance=0.0001, max_iterations=1000
):

    if net_rate == 0:
        raise ValueError("Net water rate cannot be zero.")

    x = initial_guess

    for iterations in range(1, max_iterations + 1):

        fx = water_equation(
            x, initial_water, net_rate, target_water
        )

        derivative = net_rate

        new_x = x - fx / derivative

        if abs(new_x - x) < tolerance:
            return new_x, iterations

        x = new_x

    raise ValueError("Newton-Raphson method did not converge.")

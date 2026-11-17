
# AquaPredict - ODE Numerical Methods


# Calculate the rate of change of water
def water_rate(time, inflow_rate, outflow_rate):

    return inflow_rate(time) - outflow_rate(time)


# Keep water volume within tank limits
def check_capacity(water_volume, capacity):

    if water_volume > capacity:
        water_volume = capacity

    if water_volume < 0:
        water_volume = 0

    return water_volume


# Euler Method
def euler_method(
    initial_water, simulation_time, time_step,
    capacity, inflow_rate, outflow_rate
):

    water_volume = initial_water

    times = [0]
    water_levels = [water_volume]

    time = 0

    while time < simulation_time:

        rate = water_rate(time, inflow_rate, outflow_rate)

        water_volume = water_volume + rate * time_step

        water_volume = check_capacity(water_volume, capacity)

        time = time + time_step

        times.append(time)
        water_levels.append(water_volume)

    return times, water_levels


# Modified Euler Method
def modified_euler_method(
    initial_water, simulation_time, time_step,
    capacity, inflow_rate, outflow_rate
):

    water_volume = initial_water

    times = [0]
    water_levels = [water_volume]

    time = 0

    while time < simulation_time:

        rate1 = water_rate(time, inflow_rate, outflow_rate)

        predicted_water = water_volume + rate1 * time_step

        rate2 = water_rate(
            time + time_step, inflow_rate, outflow_rate
        )

        average_rate = (rate1 + rate2) / 2

        water_volume = water_volume + average_rate * time_step

        water_volume = check_capacity(water_volume, capacity)

        time = time + time_step

        times.append(time)
        water_levels.append(water_volume)

    return times, water_levels


# Runge-Kutta 4th Order Method
def rk4_method(
    initial_water, simulation_time, time_step,
    capacity, inflow_rate, outflow_rate
):

    water_volume = initial_water

    times = [0]
    water_levels = [water_volume]

    time = 0

    while time < simulation_time:

        k1 = water_rate(time, inflow_rate, outflow_rate)

        k2 = water_rate(
            time + time_step / 2, inflow_rate, outflow_rate
        )

        k3 = water_rate(
            time + time_step / 2, inflow_rate, outflow_rate
        )

        k4 = water_rate(
            time + time_step, inflow_rate, outflow_rate
        )

        average_rate = (k1 + 2*k2 + 2*k3 + k4) / 6

        water_volume = water_volume + average_rate * time_step

        water_volume = check_capacity(water_volume, capacity)

        time = time + time_step

        times.append(time)
        water_levels.append(water_volume)

    return times, water_levels

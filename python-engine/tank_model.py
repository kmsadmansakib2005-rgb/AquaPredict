
# AquaPredict - Water Tank Simulation

def euler_method(
    capacity,
    initial_water,
    inflow_rate,
    outflow_rate,
    simulation_time,
    time_step
):

    # Calculate net water rate
    net_rate = inflow_rate - outflow_rate

    water_volume = initial_water

    times = [0]
    water_levels = [water_volume]

    # Euler Simulation
    for time in range(0, simulation_time, time_step):

        water_volume = water_volume + net_rate * time_step

        # Check tank capacity
        if water_volume > capacity:
            water_volume = capacity

        # Check empty tank
        if water_volume < 0:
            water_volume = 0

        times.append(time + time_step)
        water_levels.append(water_volume)

    return times, water_levels

# AquaPredict - Water Tank Simulation and Virtual Pump Control

def euler_method(
    capacity,
    initial_water,
    inflow_rate,
    outflow_rate,
    simulation_time,
    time_step
):
    """Original open-loop tank simulation (kept for compatibility)."""
    net_rate = inflow_rate - outflow_rate
    water_volume = initial_water
    times = [0]
    water_levels = [water_volume]

    for time in range(0, simulation_time, time_step):
        water_volume += net_rate * time_step
        water_volume = max(0, min(capacity, water_volume))
        times.append(time + time_step)
        water_levels.append(water_volume)

    return times, water_levels


def automatic_pump_simulation(
    capacity,
    initial_water,
    inflow_rate,
    outflow_rate,
    simulation_time,
    time_step,
    pump_on_below_percent=30,
    pump_off_at_percent=90
):
    """
    Simulate a tank with hysteresis-based automatic pump control.

    The pump starts ON below the upper threshold. It switches OFF at the
    upper threshold and switches ON again at the lower threshold. Each
    time interval is split at a switching point so the pump changes state
    at the threshold rather than overshooting it.
    """
    if capacity <= 0:
        raise ValueError("Tank capacity must be greater than zero.")
    if not 0 <= initial_water <= capacity:
        raise ValueError("Initial water must be between 0 and tank capacity.")
    if inflow_rate < 0 or outflow_rate < 0:
        raise ValueError("Inflow and outflow rates cannot be negative.")
    if simulation_time <= 0 or time_step <= 0:
        raise ValueError("Simulation duration and time step must be positive.")
    if not 0 <= pump_on_below_percent < pump_off_at_percent <= 100:
        raise ValueError("Pump thresholds must satisfy 0 <= ON threshold < OFF threshold <= 100.")

    low_level = capacity * pump_on_below_percent / 100
    high_level = capacity * pump_off_at_percent / 100
    water_volume = float(initial_water)
    pump_on = water_volume < high_level

    times = [0]
    water_levels = [water_volume]
    pump_states = [pump_on]
    pump_events = [{
        "time": 0,
        "pump_on": pump_on,
        "reason": "Simulation started"
    }]

    for interval_start in range(0, simulation_time, time_step):
        remaining = min(time_step, simulation_time - interval_start)
        elapsed = 0.0
        safety_counter = 0

        while remaining > 1e-10:
            safety_counter += 1
            if safety_counter > 100:
                raise RuntimeError("Pump simulation could not advance safely.")

            if pump_on and water_volume >= high_level - 1e-9:
                pump_on = False
                pump_events.append({
                    "time": interval_start + elapsed,
                    "pump_on": False,
                    "reason": f"Upper threshold reached ({pump_off_at_percent:g}%)"
                })
                continue

            if not pump_on and water_volume <= low_level + 1e-9:
                pump_on = True
                pump_events.append({
                    "time": interval_start + elapsed,
                    "pump_on": True,
                    "reason": f"Lower threshold reached ({pump_on_below_percent:g}%)"
                })
                continue

            net_rate = (inflow_rate if pump_on else 0) - outflow_rate
            segment = remaining

            if pump_on and net_rate > 0 and water_volume < high_level:
                segment = min(segment, (high_level - water_volume) / net_rate)
            elif not pump_on and net_rate < 0 and water_volume > low_level:
                segment = min(segment, (low_level - water_volume) / net_rate)

            if segment <= 1e-10:
                # A threshold was reached within floating-point tolerance.
                water_volume = high_level if pump_on else low_level
                continue

            water_volume += net_rate * segment
            water_volume = max(0.0, min(capacity, water_volume))
            elapsed += segment
            remaining -= segment

        times.append(interval_start + min(time_step, simulation_time - interval_start))
        water_levels.append(water_volume)
        pump_states.append(pump_on)

    return {
        "times": times,
        "water_levels": water_levels,
        "final_volume": water_levels[-1],
        "pump_enabled": True,
        "pump_states": pump_states,
        "pump_on": pump_on,
        "pump_events": pump_events
    }

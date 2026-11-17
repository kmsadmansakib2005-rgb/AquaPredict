# AquaPredict

**AquaPredict – Smart Water Tank Simulation and Filling-Time Prediction System**

AquaPredict is a desktop application built with Java Swing and Python. The Java application provides the graphical interface, while the Python engine performs the numerical calculations. The two components communicate through JSON over standard input/output.

## Download

**Latest Windows release: [AquaPredict 1.0.0](https://github.com/kmsadmansakib2005-rgb/AquaPredict/releases/tag/v1.0.0)**

Download `AquaPredict-1.0.0.exe) from the release assets, run the installer, and follow the installation instructions. The Windows installer includes the Java application and a bundled Python numerical engine, so a separate Python installation is not required to run the installed application.

## Features

- Dashboard with simulation summary
- Virtual tank visualization and water-level chart
- Tank simulation with a virtual automatic pump
- **Predictor:** estimates the time required to reach a selected tank level
- Root-finding methods: Bisection, False Position and Newton-Raphson
- ODE methods: Euler, Modified Euler and fourth-order Runge-Kutta (RK4)
- Java Swing ↔ Python JSON integration
- Custom AquaPredict application icon

## Project structure

```text
AquaPredict/
├── java-app/
│   ├── pom.xml
│   └── src/main/java/com/aquapredict/
│       └── AquaPredictApp.java
└── python-engine/
    ├── main.py
    ├── tank_model.py
    └── numerical_methods/
        ├── root_finding.py
        └── ode_methods.py
```

## Requirements

For running from source:

- JDK 26
- Python 3
- Maven
- Gson (downloaded automatically by Maven)

The packaged Windows installer includes the Python engine.

## Run from source

### Run the Python engine

Open a terminal in `python-engine` and run a request, for example:

```powershell
'{"operation":"tank_simulation","capacity":1000,"initial_water":300,"inflow_rate":20,"outflow_rate":5,"simulation_time":10,"time_step":1}' | python main.py
```

### Run the Java desktop application

Open a terminal in `java-app` and run:

```powershell
mvn clean compile exec:java
```

Or open `java-app/pom.xml` in IntelliJ IDEA and run `com.aquapredict.AquaPredictApp`.

When running from source, the application looks for the sibling `python-engine` directory automatically. Run it from the project root or the `java-app` directory so that it can locate the Python engine.

## How Predictor works

Enter the tank capacity, current water volume, target water-level percentage, inflow and outflow rates, then choose one of the six numerical methods.

The model allows the inflow rate to increase linearly with time:

`Q_in(t) = Q_0 + kt`

The tank volume model is:

`V(t) = V_0 + (Q_0-Q_out)t + ½kt²`

- **Bisection, False Position and Newton-Raphson** solve for the time at which the target volume is reached.
- **Euler, Modified Euler and RK4** numerically integrate the water-volume differential equation. Predictor estimates the target-crossing time from the computed time steps.

For the root-finding methods, provide a time interval that brackets the target time; Newton-Raphson also uses an initial guess. For the ODE methods, the time step and maximum prediction time control the numerical simulation.

## Build the Windows installer

The Windows installer is built with Java's `jpackage` tool. The Python engine is first packaged as a standalone executable with PyInstaller and placed beside the application JAR in the expected `AquaPredictEngine` folder. The custom icon is supplied with the `--icon` option.

The published installer is available from the [GitHub Releases page](https://github.com/kmsadmansakib2005-rgb/AquaPredict/releases).

## Important note

AquaPredict is a software-only simulation and prediction tool. It does not read physical tank sensors or control a real pump or motor. Physical hardware integration can be considered as a future extension.

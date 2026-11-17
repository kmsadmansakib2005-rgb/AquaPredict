# AquaPredict

**AquaPredict – Smart Water Tank Simulation, Prediction & Numerical Analysis System**

A desktop application built with Java Swing and Python. Java provides the user interface, while the Python engine performs numerical calculations. The two parts communicate through JSON over standard input/output.

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
        ├── linear_equations.py
        └── ode_methods.py
```

## Requirements

- JDK 26
- Python 3
- Maven
- Gson (downloaded automatically by Maven)

## Run the Python engine

Open a terminal in `python-engine` and run a request, for example:

```powershell
'{"operation":"tank_simulation","capacity":1000,"initial_water":300,"inflow_rate":20,"outflow_rate":5,"simulation_time":10,"time_step":1}' | python main.py
```

## Run the Java desktop application

Open a terminal in `java-app` and run:

```powershell
mvn clean compile exec:java
```

Or open `java-app/pom.xml` in IntelliJ IDEA and run `com.aquapredict.AquaPredictApp`.

The application looks for the sibling `python-engine` directory automatically. Run it from the project root or the `java-app` directory so that it can locate the Python engine.

## Features

- Dashboard with simulation summary
- Virtual tank visualization
- Water-level chart
- Tank simulation using Euler's method
- Root finding: Bisection, False Position, Newton-Raphson
- Linear equations: Gauss Elimination and Gauss-Jordan
- ODE methods: Euler, Modified Euler and RK4
- Java Swing ↔ Python JSON integration

## Notes

The initial version is a software-only virtual tank simulation. Physical sensor integration can be added as a future extension.

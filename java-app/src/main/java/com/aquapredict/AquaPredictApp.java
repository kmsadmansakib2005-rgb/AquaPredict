package com.aquapredict;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.geom.Path2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AquaPredictApp extends JFrame {
    private static final Color BG = new Color(10, 17, 27);
    private static final Color PANEL = new Color(19, 30, 43);
    private static final Color PANEL_LIGHT = new Color(27, 42, 56);
    private static final Color GREEN = new Color(38, 190, 137);
    private static final Color TEXT = new Color(235, 243, 245);
    private static final Color MUTED = new Color(145, 164, 171);
    private static final Gson GSON = new Gson();

    private final CardLayout pageLayout = new CardLayout();
    private final JPanel pages = new JPanel(pageLayout);
    private final JLabel status = new JLabel("Python engine ready");
    private final JLabel dashboardVolume = new JLabel("-- L");
    private final JLabel dashboardStatus = new JLabel("Waiting for simulation");
    private final WaterChart dashboardChart = new WaterChart();
    private final TankPanel tankPanel = new TankPanel();
    private Path pythonDirectory;

    public AquaPredictApp() {
        super("AquaPredict | Smart Water Analysis");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1120, 720));
        setSize(1280, 800);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        pages.setBackground(BG);
        pages.add(new DashboardPanel(), "Dashboard");
        pages.add(tankPanel, "Tank Simulation");
        pages.add(new LabPanel(), "Numerical Lab");

        add(buildSidebar(), BorderLayout.WEST);
        add(pages, BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);
        showPage("Dashboard");
    }

    private JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setBackground(new Color(13, 23, 34));
        side.setPreferredSize(new Dimension(230, 0));
        side.setBorder(new EmptyBorder(26, 16, 20, 16));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));

        JLabel brand = new JLabel("AquaPredict");
        brand.setForeground(TEXT);
        brand.setFont(new Font("SansSerif", Font.BOLD, 23));
        JLabel tagline = new JLabel("SMART WATER ANALYTICS");
        tagline.setForeground(GREEN);
        tagline.setFont(new Font("SansSerif", Font.BOLD, 9));
        tagline.setBorder(new EmptyBorder(5, 2, 30, 0));
        side.add(brand);
        side.add(tagline);

        side.add(navButton("Dashboard", "Overview of your tank"));
        side.add(Box.createVerticalStrut(9));
        side.add(navButton("Tank Simulation", "Simulate water levels"));
        side.add(Box.createVerticalStrut(9));
        side.add(navButton("Numerical Lab", "Run numerical methods"));

        side.add(Box.createVerticalGlue());
        JLabel version = new JLabel("AQUAPREDICT  •  VERSION 1.0");
        version.setForeground(MUTED);
        version.setFont(new Font("SansSerif", Font.PLAIN, 9));
        side.add(version);
        return side;
    }

    private JButton navButton(String page, String subtitle) {
        JButton button = new JButton("<html><div style='font-size:13px'><b>" + page
                + "</b></div><div style='font-size:10px;color:#91A4AB'>" + subtitle + "</div></html>");
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setForeground(TEXT);
        button.setBackground(PANEL);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(13, 13, 13, 8));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        button.addActionListener(e -> showPage(page));
        return button;
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(13, 23, 34));
        bar.setBorder(new EmptyBorder(9, 18, 9, 18));
        status.setForeground(MUTED);
        status.setFont(new Font("SansSerif", Font.PLAIN, 11));
        JLabel right = new JLabel("JAVA SWING  +  PYTHON");
        right.setForeground(GREEN);
        right.setFont(new Font("SansSerif", Font.BOLD, 10));
        bar.add(status, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private void showPage(String page) {
        pageLayout.show(pages, page);
        status.setText(page + "  •  AquaPredict");
    }

    private JPanel page(String title, String subtitle) {
        JPanel root = new JPanel(new BorderLayout(0, 20));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(28, 30, 24, 30));
        JPanel heading = new JPanel();
        heading.setBackground(BG);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(TEXT);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 27));
        JLabel sub = new JLabel(subtitle);
        sub.setForeground(MUTED);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setBorder(new EmptyBorder(6, 1, 0, 0));
        heading.add(titleLabel);
        heading.add(sub);
        root.add(heading, BorderLayout.NORTH);
        return root;
    }

    private JPanel cardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(PANEL);
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }

    private JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setBorder(new EmptyBorder(0, 0, 12, 0));
        return label;
    }

    private JTextField field(String value) {
        JTextField field = new JTextField(value);
        field.setForeground(TEXT);
        field.setCaretColor(GREEN);
        field.setBackground(PANEL_LIGHT);
        field.setBorder(new EmptyBorder(10, 11, 10, 11));
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return field;
    }

    private void addField(JPanel panel, String label, JTextField field) {
        JLabel name = new JLabel(label);
        name.setForeground(MUTED);
        name.setFont(new Font("SansSerif", Font.PLAIN, 11));
        name.setBorder(new EmptyBorder(8, 0, 5, 0));
        panel.add(name);
        panel.add(field);
    }

    private JButton actionButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(new Color(8, 25, 22));
        button.setBackground(GREEN);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(11, 18, 11, 18));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void runPython(JsonObject request, Consumer<JsonObject> onSuccess) {
        status.setText("Running Python calculation...");
        new SwingWorker<JsonObject, Void>() {
            @Override
            protected JsonObject doInBackground() throws Exception {
                Path engine = locatePythonEngine();
                Process process = new ProcessBuilder("python", "main.py")
                        .directory(engine.toFile()).start();

                try (BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(process.getOutputStream()))) {
                    writer.write(GSON.toJson(request));
                    writer.newLine();
                    writer.flush();
                }

                StringBuilder output = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) output.append(line);
                }

                StringBuilder errors = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) errors.append(line).append('\n');
                }

                int exitCode = process.waitFor();
                if (output.isEmpty()) {
                    throw new IllegalStateException(errors.isEmpty()
                            ? "Python returned no response." : errors.toString());
                }
                JsonObject response = JsonParser.parseString(output.toString()).getAsJsonObject();
                if (exitCode != 0 && !response.has("success")) {
                    throw new IllegalStateException(errors.toString());
                }
                return response;
            }

            @Override
            protected void done() {
                try {
                    JsonObject response = get();
                    if (!response.get("success").getAsBoolean()) {
                        throw new IllegalStateException(response.get("error").getAsString());
                    }
                    status.setText("Calculation completed successfully");
                    onSuccess.accept(response.getAsJsonObject("result"));
                } catch (Exception ex) {
                    status.setText("Calculation failed");
                    tankPanel.calculationFailed();
                    JOptionPane.showMessageDialog(AquaPredictApp.this,
                            ex.getMessage(), "AquaPredict Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private Path locatePythonEngine() throws Exception {
        if (pythonDirectory != null && Files.exists(pythonDirectory.resolve("main.py"))) {
            return pythonDirectory;
        }
        Path current = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
        List<Path> candidates = List.of(
                current.resolve("python-engine"),
                current.resolve("../python-engine").normalize(),
                current.resolve("../../python-engine").normalize(),
                current.resolve("../..").resolve("python-engine").normalize()
        );
        for (Path candidate : candidates) {
            if (Files.exists(candidate.resolve("main.py"))) {
                pythonDirectory = candidate;
                return candidate;
            }
        }
        throw new IllegalStateException("Cannot find python-engine/main.py. "
                + "Open the AquaPredict project folder or configure the Python engine path.");
    }

    private class DashboardPanel extends JPanel {
        DashboardPanel() {
            setBackground(BG);
            setBorder(new EmptyBorder(28, 30, 24, 30));
            setLayout(new BorderLayout(0, 20));

            JPanel heading = new JPanel();
            heading.setBackground(BG);
            heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
            JLabel title = new JLabel("Good day. Welcome to AquaPredict.");
            title.setForeground(TEXT);
            title.setFont(new Font("SansSerif", Font.BOLD, 25));
            JLabel subtitle = new JLabel("Monitor, simulate and analyze your water system.");
            subtitle.setForeground(MUTED);
            subtitle.setBorder(new EmptyBorder(7, 0, 0, 0));
            heading.add(title);
            heading.add(subtitle);
            add(heading, BorderLayout.NORTH);

            JPanel body = new JPanel(new BorderLayout(16, 16));
            body.setBackground(BG);
            JPanel metrics = new JPanel(new GridLayout(1, 3, 14, 0));
            metrics.setBackground(BG);
            metrics.add(metric("LATEST WATER VOLUME", dashboardVolume, "From your last simulation"));
            metrics.add(metric("SYSTEM STATUS", dashboardStatus, "Python calculation engine"));
            JLabel methods = new JLabel("9");
            methods.setForeground(TEXT);
            methods.setFont(new Font("SansSerif", Font.BOLD, 25));
            metrics.add(metric("NUMERICAL METHODS", methods, "Available across 3 categories"));

            JPanel chartCard = cardPanel();
            chartCard.add(sectionLabel("Water level overview"));
            chartCard.add(dashboardChart);
            JPanel quick = cardPanel();
            quick.add(sectionLabel("Quick actions"));
            JButton simulate = actionButton("Open Tank Simulation");
            simulate.setAlignmentX(Component.LEFT_ALIGNMENT);
            simulate.addActionListener(e -> showPage("Tank Simulation"));
            JButton lab = actionButton("Open Numerical Lab");
            lab.setAlignmentX(Component.LEFT_ALIGNMENT);
            lab.addActionListener(e -> showPage("Numerical Lab"));
            quick.add(simulate);
            quick.add(Box.createVerticalStrut(10));
            quick.add(lab);

            body.add(metrics, BorderLayout.NORTH);
            body.add(chartCard, BorderLayout.CENTER);
            body.add(quick, BorderLayout.SOUTH);
            add(body, BorderLayout.CENTER);
        }

        private JPanel metric(String heading, JLabel value, String note) {
            JPanel panel = cardPanel();
            JLabel h = new JLabel(heading);
            h.setForeground(MUTED);
            h.setFont(new Font("SansSerif", Font.BOLD, 10));
            value.setFont(new Font("SansSerif", Font.BOLD, 24));
            value.setForeground(TEXT);
            JLabel n = new JLabel(note);
            n.setForeground(MUTED);
            n.setFont(new Font("SansSerif", Font.PLAIN, 10));
            panel.add(h);
            panel.add(Box.createVerticalStrut(10));
            panel.add(value);
            panel.add(Box.createVerticalStrut(5));
            panel.add(n);
            return panel;
        }
    }

    private class TankPanel extends JPanel {
        private final JTextField capacity = field("1000");
        private final JTextField initial = field("300");
        private final JTextField inflow = field("20");
        private final JTextField outflow = field("5");
        private final JTextField duration = field("50");
        private final JTextField step = field("1");
        private final JTextField pumpOnBelow = field("30");
        private final JTextField pumpOffAt = field("90");
        private final JCheckBox automaticPump = new JCheckBox("Enable automatic virtual pump", true);
        private final JLabel pumpStatus = new JLabel("Pump: --");
        private final JLabel pumpEventSummary = new JLabel("Pump events will appear after simulation.");
        private final JLabel finalVolume = new JLabel("-- L");
        private final JLabel simulationClock = new JLabel("Elapsed time: 0.0 min");
        private final WaterChart chart = new WaterChart();
        private final TankDrawing tank = new TankDrawing();
        private JButton runButton;
        private Timer animationTimer;
        private boolean preparingSimulation;

        TankPanel() {
            setBackground(BG);
            setBorder(new EmptyBorder(28, 30, 24, 30));
            setLayout(new BorderLayout(0, 18));
            JPanel heading = new JPanel();
            heading.setBackground(BG);
            heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
            JLabel title = new JLabel("Tank Simulation");
            title.setForeground(TEXT);
            title.setFont(new Font("SansSerif", Font.BOLD, 26));
            JLabel sub = new JLabel("Configure a virtual tank and calculate its water-level changes.");
            sub.setForeground(MUTED);
            heading.add(title);
            heading.add(Box.createVerticalStrut(6));
            heading.add(sub);
            add(heading, BorderLayout.NORTH);

            JPanel content = new JPanel(new GridLayout(1, 2, 16, 0));
            content.setBackground(BG);
            JPanel form = cardPanel();
            form.add(sectionLabel("Tank parameters"));
            addField(form, "Tank capacity (L)", capacity);
            addField(form, "Initial water volume (L)", initial);
            addField(form, "Inflow rate (L/min)", inflow);
            addField(form, "Outflow rate (L/min)", outflow);
            addField(form, "Simulation duration (min)", duration);
            addField(form, "Time step (min)", step);
            form.add(Box.createVerticalStrut(12));
            form.add(sectionLabel("Automatic pump control"));
            automaticPump.setBackground(PANEL);
            automaticPump.setForeground(TEXT);
            automaticPump.setFocusPainted(false);
            automaticPump.setAlignmentX(Component.LEFT_ALIGNMENT);
            form.add(automaticPump);
            addField(form, "Switch pump ON below water level (%)", pumpOnBelow);
            addField(form, "Switch pump OFF at water level (%)", pumpOffAt);
            pumpStatus.setForeground(GREEN);
            pumpStatus.setFont(new Font("SansSerif", Font.BOLD, 15));
            pumpStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
            pumpEventSummary.setForeground(MUTED);
            pumpEventSummary.setFont(new Font("SansSerif", Font.PLAIN, 10));
            pumpEventSummary.setAlignmentX(Component.LEFT_ALIGNMENT);
            form.add(Box.createVerticalStrut(8));
            form.add(pumpStatus);
            form.add(pumpEventSummary);
            runButton = actionButton("Start Simulation");
            runButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            runButton.setBorder(new EmptyBorder(12, 20, 12, 20));
            runButton.addActionListener(e -> simulate());
            form.add(Box.createVerticalStrut(15));
            form.add(runButton);

            JPanel visual = cardPanel();
            visual.add(sectionLabel("Live tank model"));
            tank.setPreferredSize(new Dimension(300, 235));
            visual.add(tank);
            simulationClock.setForeground(MUTED);
            simulationClock.setFont(new Font("SansSerif", Font.PLAIN, 11));
            visual.add(simulationClock);
            finalVolume.setForeground(GREEN);
            finalVolume.setFont(new Font("SansSerif", Font.BOLD, 27));
            visual.add(Box.createVerticalStrut(5));
            visual.add(finalVolume);
            JLabel chartTitle = new JLabel("Water volume over time");
            chartTitle.setForeground(MUTED);
            chartTitle.setBorder(new EmptyBorder(14, 0, 8, 0));
            visual.add(chartTitle);
            visual.add(chart);

            content.add(scrollForm(form));
            content.add(visual);
            add(content, BorderLayout.CENTER);
        }

        private void simulate() {
            try {
                if (animationTimer != null && animationTimer.isRunning()) animationTimer.stop();
                preparingSimulation = true;
                runButton.setEnabled(false);
                runButton.setText("Preparing simulation...");

                JsonObject request = new JsonObject();
                request.addProperty("operation", "tank_simulation");
                request.addProperty("capacity", number(capacity));
                request.addProperty("initial_water", number(initial));
                request.addProperty("inflow_rate", number(inflow));
                request.addProperty("outflow_rate", number(outflow));
                request.addProperty("simulation_time", integer(duration));
                request.addProperty("time_step", integer(step));
                request.addProperty("automatic_pump", automaticPump.isSelected());
                request.addProperty("pump_on_below_percent", number(pumpOnBelow));
                request.addProperty("pump_off_at_percent", number(pumpOffAt));

                if (integer(duration) <= 0 || integer(step) <= 0) {
                    throw new IllegalArgumentException("Duration and time step must be positive integers.");
                }
                if (automaticPump.isSelected()
                        && (number(pumpOnBelow) < 0 || number(pumpOffAt) > 100
                        || number(pumpOnBelow) >= number(pumpOffAt))) {
                    throw new IllegalArgumentException(
                            "Pump thresholds must satisfy 0 <= ON threshold < OFF threshold <= 100.");
                }

                double startingCapacity = number(capacity);
                double startingVolume = number(initial);
                boolean startingPump = automaticPump.isSelected()
                        && startingVolume < startingCapacity * number(pumpOffAt) / 100.0;
                tank.setState(startingVolume / startingCapacity, startingPump, 0);
                finalVolume.setText(String.format("%.1f L", startingVolume));
                simulationClock.setText("Elapsed time: 0.0 min");
                pumpStatus.setText(startingPump ? "MOTOR ON • PUMPING WATER"
                        : automaticPump.isSelected() ? "MOTOR OFF" : "MOTOR CONTROL DISABLED");
                pumpStatus.setForeground(startingPump ? GREEN
                        : automaticPump.isSelected() ? new Color(255, 120, 105) : MUTED);
                pumpEventSummary.setText(startingPump
                        ? "Water is starting to flow into the tank..."
                        : "Motor is not running.");
                runPython(request, result -> {
                    preparingSimulation = false;
                    List<Double> times = toList(result.getAsJsonArray("times"));
                    List<Double> levels = toList(result.getAsJsonArray("water_levels"));
                    JsonArray events = result.getAsJsonArray("pump_events");
                    boolean pumpEnabled = result.get("pump_enabled").getAsBoolean();
                    double tankCapacity = number(capacity);

                    chart.setData(List.of(times.get(0)), List.of(levels.get(0)));
                    dashboardChart.setData(List.of(times.get(0)), List.of(levels.get(0)));
                    tank.setState(levels.get(0) / tankCapacity,
                            pumpEnabled && initialPumpState(events), 0);
                    finalVolume.setText(String.format("%.1f L", levels.get(0)));
                    simulationClock.setText("Elapsed time: 0.0 min");

                    if (pumpEnabled && initialPumpState(events)) {
                        pumpStatus.setText("MOTOR ON • PUMPING WATER");
                        pumpStatus.setForeground(GREEN);
                        pumpEventSummary.setText("Water is flowing into the tank...");
                        dashboardStatus.setText("Motor ON • Pumping water");
                    } else {
                        pumpStatus.setText(pumpEnabled ? "MOTOR OFF" : "MOTOR CONTROL DISABLED");
                        pumpStatus.setForeground(pumpEnabled
                                ? new Color(255, 120, 105) : MUTED);
                        pumpEventSummary.setText(pumpEnabled
                                ? "Motor is off; waiting for the lower water threshold."
                                : "Automatic virtual pump is disabled.");
                        dashboardStatus.setText(pumpEnabled ? "Motor OFF" : "Pump disabled");
                    }

                    runButton.setText("Simulation running...");
                    status.setText("Live tank simulation in progress...");
                    playSimulation(times, levels, events, pumpEnabled, tankCapacity);
                });
            } catch (Exception ex) {
                preparingSimulation = false;
                runButton.setEnabled(true);
                runButton.setText("Start Simulation");
                showInputError(ex);
            }
        }

        private void calculationFailed() {
            if (preparingSimulation) {
                preparingSimulation = false;
                runButton.setEnabled(true);
                runButton.setText("Start Simulation");
            }
        }

        private boolean initialPumpState(JsonArray events) {
            return !events.isEmpty()
                    && events.get(0).getAsJsonObject().get("pump_on").getAsBoolean();
        }

        private void playSimulation(List<Double> times, List<Double> levels,
                                    JsonArray events, boolean pumpEnabled, double tankCapacity) {
            if (animationTimer != null && animationTimer.isRunning()) animationTimer.stop();

            int frameCount = Math.max(1, (times.size() - 1) * 10);
            int[] frame = {0};
            animationTimer = new Timer(100, event -> {
                frame[0]++;
                double progress = Math.min(1.0, (double) frame[0] / frameCount);
                double currentTime = times.get(times.size() - 1) * progress;

                int segment = 0;
                while (segment < times.size() - 2 && times.get(segment + 1) < currentTime) {
                    segment++;
                }
                double segmentStart = times.get(segment);
                double segmentEnd = times.get(Math.min(segment + 1, times.size() - 1));
                double fraction = segmentEnd == segmentStart ? 1.0
                        : Math.max(0, Math.min(1, (currentTime - segmentStart)
                        / (segmentEnd - segmentStart)));
                double currentVolume = levels.get(segment)
                        + (levels.get(Math.min(segment + 1, levels.size() - 1))
                        - levels.get(segment)) * fraction;

                boolean motorOn = pumpEnabled && pumpStateAt(events, currentTime);
                tank.setState(currentVolume / tankCapacity, motorOn, frame[0]);

                List<Double> visibleTimes = new ArrayList<>();
                List<Double> visibleLevels = new ArrayList<>();
                for (int i = 0; i <= segment; i++) {
                    visibleTimes.add(times.get(i));
                    visibleLevels.add(levels.get(i));
                }
                if (visibleTimes.isEmpty() || currentTime > visibleTimes.get(visibleTimes.size() - 1)) {
                    visibleTimes.add(currentTime);
                    visibleLevels.add(currentVolume);
                }
                chart.setData(visibleTimes, visibleLevels);
                dashboardChart.setData(visibleTimes, visibleLevels);

                double percentage = currentVolume / tankCapacity * 100.0;
                finalVolume.setText(String.format("%.1f L  (%.1f%%)", currentVolume, percentage));
                dashboardVolume.setText(String.format("%.1f L", currentVolume));
                simulationClock.setText(String.format("Elapsed time: %.1f / %.1f min",
                        currentTime, times.get(times.size() - 1)));

                if (pumpEnabled && motorOn) {
                    pumpStatus.setText("MOTOR ON • PUMPING WATER");
                    pumpStatus.setForeground(GREEN);
                    pumpEventSummary.setText("Water is flowing into the tank. The motor will stop at "
                            + pumpOffAt.getText().trim() + "%.");
                    dashboardStatus.setText("Motor ON • Pumping water");
                } else if (pumpEnabled) {
                    pumpStatus.setText("MOTOR OFF • WATER INLET STOPPED");
                    pumpStatus.setForeground(new Color(255, 120, 105));
                    pumpEventSummary.setText("Motor is OFF. Water inlet flow has stopped.");
                    dashboardStatus.setText("Motor OFF • Inlet stopped");
                } else {
                    pumpStatus.setText("MOTOR CONTROL DISABLED");
                    pumpStatus.setForeground(MUTED);
                    dashboardStatus.setText("Open-loop simulation");
                }

                if (progress >= 1.0) {
                    animationTimer.stop();
                    runButton.setEnabled(true);
                    runButton.setText("Run Again");
                    finalVolume.setText(String.format("%.2f L", levels.get(levels.size() - 1)));
                    dashboardVolume.setText(String.format("%.2f L", levels.get(levels.size() - 1)));
                    simulationClock.setText(String.format("Simulation completed • %.1f min",
                            times.get(times.size() - 1)));
                    if (pumpEnabled) {
                        boolean finalPumpState = pumpStateAt(events, times.get(times.size() - 1));
                        pumpStatus.setText(finalPumpState
                                ? "MOTOR ON • PUMPING WATER" : "MOTOR OFF • INLET STOPPED");
                        pumpStatus.setForeground(finalPumpState ? GREEN : new Color(255, 120, 105));
                        dashboardStatus.setText(finalPumpState
                                ? "Simulation complete • Motor ON" : "Simulation complete • Motor OFF");
                        pumpEventSummary.setText(finalPumpState
                                ? "Simulation ended while the motor was running."
                                : "Tank reached the OFF threshold. Motor stopped automatically.");
                    } else {
                        dashboardStatus.setText("Simulation complete • Pump disabled");
                    }
                    status.setText("Tank simulation completed");
                }
            });
            animationTimer.start();
        }

        private boolean pumpStateAt(JsonArray events, double simulationTime) {
            boolean state = false;
            for (int i = 0; i < events.size(); i++) {
                JsonObject pumpEvent = events.get(i).getAsJsonObject();
                if (pumpEvent.get("time").getAsDouble() <= simulationTime + 1e-9) {
                    state = pumpEvent.get("pump_on").getAsBoolean();
                } else {
                    break;
                }
            }
            return state;
        }
    }

    private JScrollPane scrollForm(JPanel form) {
        JScrollPane scroll = new JScrollPane(form,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(PANEL);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        return scroll;
    }

    private class LabPanel extends JPanel {
        LabPanel() {
            setBackground(BG);
            setBorder(new EmptyBorder(28, 30, 24, 30));
            setLayout(new BorderLayout(0, 18));
            JPanel heading = new JPanel();
            heading.setBackground(BG);
            heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
            JLabel title = new JLabel("Numerical Methods Laboratory");
            title.setForeground(TEXT);
            title.setFont(new Font("SansSerif", Font.BOLD, 26));
            JLabel sub = new JLabel("Run the methods from your Numerical Methods Lab syllabus.");
            sub.setForeground(MUTED);
            heading.add(title);
            heading.add(Box.createVerticalStrut(6));
            heading.add(sub);
            add(heading, BorderLayout.NORTH);

            JTabbedPane tabs = new JTabbedPane();
            tabs.setOpaque(true);
            tabs.setBackground(PANEL);
            tabs.setForeground(TEXT);
            tabs.setUI(new BasicTabbedPaneUI() {
                @Override
                protected void paintTabBackground(Graphics g, int placement, int index,
                                                  int x, int y, int width, int height,
                                                  boolean selected) {
                    g.setColor(selected ? GREEN : PANEL_LIGHT);
                    g.fillRect(x, y, width, height);
                }

                @Override
                protected void paintTabBorder(Graphics g, int placement, int index,
                                              int x, int y, int width, int height,
                                              boolean selected) {
                    // Flat tabs keep the dark interface visually consistent.
                }

                @Override
                protected void paintContentBorder(Graphics g, int placement, int selectedIndex) {
                    // The parent panel supplies the dark content background.
                }
            });
            tabs.addTab("Root Finding", new RootPanel());
            tabs.addTab("Linear Equations", new LinearPanel());
            tabs.addTab("ODE Methods", new OdePanel());
            tabs.addChangeListener(e -> {
                for (int i = 0; i < tabs.getTabCount(); i++) {
                    tabs.setForegroundAt(i, i == tabs.getSelectedIndex()
                            ? new Color(8, 25, 22) : TEXT);
                }
            });
            tabs.setForegroundAt(0, new Color(8, 25, 22));
            add(tabs, BorderLayout.CENTER);
        }
    }

    private class RootPanel extends JPanel {
        private final JComboBox<String> method = new JComboBox<>(
                new String[]{"Bisection", "False Position", "Newton-Raphson"});
        private final JTextField initial = field("300");
        private final JTextField inflow = field("20");
        private final JTextField outflow = field("5");
        private final JTextField target = field("800");
        private final JTextField a = field("0");
        private final JTextField b = field("100");
        private final JTextField guess = field("50");
        private final JTextArea output = outputArea();

        RootPanel() {
            setBackground(BG);
            setLayout(new GridLayout(1, 2, 15, 0));
            JPanel form = cardPanel();
            form.add(sectionLabel("Root finding parameters"));
            addField(form, "Method", method);
            addField(form, "Initial water (L)", initial);
            addField(form, "Inflow rate (L/min)", inflow);
            addField(form, "Outflow rate (L/min)", outflow);
            addField(form, "Target water level (L)", target);
            addField(form, "Lower bound (a)", a);
            addField(form, "Upper bound (b)", b);
            addField(form, "Initial guess (Newton-Raphson)", guess);
            JButton run = actionButton("Calculate root");
            run.setAlignmentX(Component.LEFT_ALIGNMENT);
            run.addActionListener(e -> calculate());
            form.add(Box.createVerticalStrut(12));
            form.add(run);
            JPanel result = cardPanel();
            result.add(sectionLabel("Calculation result"));
            result.add(new JScrollPane(output));
            add(scrollForm(form));
            add(result);
        }

        private void calculate() {
            try {
                String selected = method.getSelectedItem().toString();
                String key = selected.equals("Newton-Raphson") ? "newton_raphson"
                        : selected.equals("False Position") ? "false_position" : "bisection";
                JsonObject req = new JsonObject();
                req.addProperty("operation", "root_finding");
                req.addProperty("method", key);
                req.addProperty("initial_water", number(initial));
                req.addProperty("inflow_rate", number(inflow));
                req.addProperty("outflow_rate", number(outflow));
                req.addProperty("target_water", number(target));
                req.addProperty("a", number(a));
                req.addProperty("b", number(b));
                req.addProperty("initial_guess", number(guess));
                runPython(req, result -> output.setText(
                        "Method: " + selected + "\n\nTime to reach target: "
                                + result.get("root").getAsDouble() + " minutes\n"
                                + "Iterations: " + result.get("iterations").getAsInt()));
            } catch (Exception ex) {
                showInputError(ex);
            }
        }
    }

    private class LinearPanel extends JPanel {
        private final JComboBox<String> method = new JComboBox<>(
                new String[]{"Gauss Elimination", "Gauss-Jordan"});
        private final JTextField matrix = field("2,1,1;3,2,1;1,2,3");
        private final JTextField constants = field("10,14,14");
        private final JTextArea output = outputArea();

        LinearPanel() {
            setBackground(BG);
            setLayout(new GridLayout(1, 2, 15, 0));
            JPanel form = cardPanel();
            form.add(sectionLabel("Linear system"));
            addField(form, "Method", method);
            addField(form, "Coefficient matrix (rows separated by ;)", matrix);
            addField(form, "Constant vector (comma separated)", constants);
            JLabel hint = new JLabel("<html>Example: 2,1,1;3,2,1;1,2,3<br>and 10,14,14</html>");
            hint.setForeground(MUTED);
            hint.setFont(new Font("SansSerif", Font.PLAIN, 11));
            form.add(Box.createVerticalStrut(9));
            form.add(hint);
            JButton run = actionButton("Solve system");
            run.setAlignmentX(Component.LEFT_ALIGNMENT);
            run.addActionListener(e -> solve());
            form.add(Box.createVerticalStrut(14));
            form.add(run);
            JPanel result = cardPanel();
            result.add(sectionLabel("Solution"));
            result.add(new JScrollPane(output));
            add(scrollForm(form));
            add(result);
        }

        private void solve() {
            try {
                String[] rows = matrix.getText().trim().split(";");
                JsonArray a = new JsonArray();
                for (String row : rows) {
                    JsonArray values = new JsonArray();
                    for (String value : row.split(",")) values.add(Double.parseDouble(value.trim()));
                    a.add(values);
                }
                JsonArray b = new JsonArray();
                for (String value : constants.getText().split(",")) {
                    b.add(Double.parseDouble(value.trim()));
                }
                JsonObject req = new JsonObject();
                req.addProperty("operation", "linear_equations");
                req.addProperty("method", method.getSelectedIndex() == 0 ? "gauss" : "gauss_jordan");
                req.add("A", a);
                req.add("B", b);
                runPython(req, result -> {
                    JsonArray solution = result.getAsJsonArray("solution");
                    StringBuilder text = new StringBuilder("Solution\n\n");
                    for (int i = 0; i < solution.size(); i++) {
                        text.append((char) ('x' + i)).append(" = ")
                                .append(solution.get(i).getAsDouble()).append("\n");
                    }
                    output.setText(text.toString());
                });
            } catch (Exception ex) {
                showInputError(ex);
            }
        }
    }

    private class OdePanel extends JPanel {
        private final JComboBox<String> method = new JComboBox<>(
                new String[]{"Euler", "Modified Euler", "Runge-Kutta 4"});
        private final JTextField initial = field("300");
        private final JTextField capacity = field("1000");
        private final JTextField duration = field("10");
        private final JTextField step = field("1");
        private final JTextField inflowBase = field("20");
        private final JTextField increase = field("2");
        private final JTextField outflow = field("5");
        private final JTextArea output = outputArea();

        OdePanel() {
            setBackground(BG);
            setLayout(new GridLayout(1, 2, 15, 0));
            JPanel form = cardPanel();
            form.add(sectionLabel("ODE simulation parameters"));
            addField(form, "Method", method);
            addField(form, "Initial water (L)", initial);
            addField(form, "Tank capacity (L)", capacity);
            addField(form, "Duration (min)", duration);
            addField(form, "Time step (min)", step);
            addField(form, "Base inflow (L/min)", inflowBase);
            addField(form, "Inflow increase per minute", increase);
            addField(form, "Outflow (L/min)", outflow);
            JButton run = actionButton("Run ODE method");
            run.setAlignmentX(Component.LEFT_ALIGNMENT);
            run.addActionListener(e -> calculate());
            form.add(Box.createVerticalStrut(12));
            form.add(run);
            JPanel result = cardPanel();
            result.add(sectionLabel("ODE result"));
            result.add(new JScrollPane(output));
            add(scrollForm(form));
            add(result);
        }

        private void calculate() {
            try {
                String key = method.getSelectedIndex() == 0 ? "euler"
                        : method.getSelectedIndex() == 1 ? "modified_euler" : "rk4";
                JsonObject req = new JsonObject();
                req.addProperty("operation", "ode_methods");
                req.addProperty("method", key);
                req.addProperty("initial_water", number(initial));
                req.addProperty("capacity", number(capacity));
                req.addProperty("simulation_time", number(duration));
                req.addProperty("time_step", number(step));
                req.addProperty("inflow_base", number(inflowBase));
                req.addProperty("inflow_increase", number(increase));
                req.addProperty("outflow_rate", number(outflow));
                runPython(req, result -> {
                    output.setText("Method: " + method.getSelectedItem()
                            + "\nFinal water volume: " + result.get("final_volume").getAsDouble()
                            + " L\n\nTime (min)      Water (L)\n");
                    JsonArray times = result.getAsJsonArray("times");
                    JsonArray levels = result.getAsJsonArray("water_levels");
                    for (int i = 0; i < times.size(); i++) {
                        output.append(String.format("%-15s %s%n",
                                times.get(i).getAsString(), levels.get(i).getAsString()));
                    }
                });
            } catch (Exception ex) {
                showInputError(ex);
            }
        }
    }

    private JTextArea outputArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setForeground(TEXT);
        area.setBackground(PANEL_LIGHT);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setBorder(new EmptyBorder(12, 12, 12, 12));
        area.setText("Your calculation results will appear here.");
        return area;
    }

    private void addField(JPanel panel, String label, JComboBox<?> combo) {
        JLabel name = new JLabel(label);
        name.setForeground(MUTED);
        name.setFont(new Font("SansSerif", Font.PLAIN, 11));
        name.setBorder(new EmptyBorder(8, 0, 5, 0));
        panel.add(name);
        combo.setBackground(PANEL_LIGHT);
        combo.setForeground(TEXT);
        panel.add(combo);
    }

    private double number(JTextField field) {
        return Double.parseDouble(field.getText().trim());
    }

    private int integer(JTextField field) {
        return Integer.parseInt(field.getText().trim());
    }

    private List<Double> toList(JsonArray array) {
        List<Double> values = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) values.add(array.get(i).getAsDouble());
        return values;
    }

    private void showInputError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Check your input", JOptionPane.WARNING_MESSAGE);
    }

    private static class WaterChart extends JPanel {
        private List<Double> times = List.of();
        private List<Double> levels = List.of();

        WaterChart() {
            setBackground(PANEL);
            setPreferredSize(new Dimension(400, 210));
        }

        void setData(List<Double> times, List<Double> levels) {
            this.times = new ArrayList<>(times);
            this.levels = new ArrayList<>(levels);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int left = 42, right = getWidth() - 18, top = 18, bottom = getHeight() - 30;
            g.setColor(new Color(55, 72, 82));
            for (int i = 0; i <= 4; i++) {
                int y = top + (bottom - top) * i / 4;
                g.drawLine(left, y, right, y);
            }
            g.setColor(MUTED);
            g.setFont(new Font("SansSerif", Font.PLAIN, 9));
            g.drawString("Water (L)", 5, 12);
            g.drawString("Time", right - 15, getHeight() - 8);
            if (levels.size() < 2) {
                g.drawString("Run a simulation to display the water-level graph.", left + 8, (top + bottom) / 2);
                g.dispose();
                return;
            }
            double min = Math.min(0, levels.stream().mapToDouble(Double::doubleValue).min().orElse(0));
            double max = levels.stream().mapToDouble(Double::doubleValue).max().orElse(1);
            if (max <= min) max = min + 1;
            Path2D path = new Path2D.Double();
            for (int i = 0; i < levels.size(); i++) {
                double x = left + (right - left) * (double) i / (levels.size() - 1);
                double y = bottom - (levels.get(i) - min) / (max - min) * (bottom - top);
                if (i == 0) path.moveTo(x, y); else path.lineTo(x, y);
            }
            g.setColor(GREEN);
            g.setStroke(new BasicStroke(2.5f));
            g.draw(path);
            g.dispose();
        }
    }

    private static class TankDrawing extends JPanel {
        private double level = 0.30;
        private boolean pumpOn = true;
        private int animationFrame = 0;

        TankDrawing() {
            setBackground(PANEL);
        }

        void setState(double level, boolean pumpOn, int animationFrame) {
            this.level = Math.max(0, Math.min(1, level));
            this.pumpOn = pumpOn;
            this.animationFrame = animationFrame;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int tankW = 112, tankH = 174;
            int tankX = getWidth() - tankW - 28, tankY = 28;
            int waterHeight = (int) (tankH * level);
            int motorX = 20, motorY = 65;

            // Pipe and motor illustration.
            g.setColor(new Color(105, 133, 142));
            g.setStroke(new BasicStroke(7, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(motorX + 38, motorY + 14, tankX + tankW / 2, motorY + 14);
            g.drawLine(tankX + tankW / 2, motorY + 14, tankX + tankW / 2, tankY + 2);

            g.setColor(pumpOn ? GREEN : new Color(105, 120, 128));
            g.fillRoundRect(motorX, motorY, 42, 30, 8, 8);
            g.setColor(new Color(8, 25, 22));
            g.setStroke(new BasicStroke(2));
            g.drawOval(motorX + 11, motorY + 6, 18, 18);
            g.drawLine(motorX + 20, motorY + 9, motorX + 20, motorY + 21);

            g.setFont(new Font("SansSerif", Font.BOLD, 10));
            g.setColor(pumpOn ? GREEN : new Color(255, 120, 105));
            g.drawString(pumpOn ? "MOTOR ON" : "MOTOR OFF", motorX - 1, motorY + 48);

            if (pumpOn) {
                g.setColor(new Color(69, 211, 235));
                g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawLine(tankX + tankW / 2, tankY + 2, tankX + tankW / 2, tankY + 25);
                for (int i = 0; i < 4; i++) {
                    int dropY = tankY + 24 + Math.floorMod(animationFrame * 5 + i * 13, 34);
                    g.fillOval(tankX + tankW / 2 - 3, dropY, 6, 9);
                }
                g.setFont(new Font("SansSerif", Font.PLAIN, 9));
                g.drawString("WATER FLOW", motorX - 1, motorY + 62);
            } else {
                g.setColor(new Color(255, 120, 105));
                g.setStroke(new BasicStroke(2));
                g.drawLine(tankX + tankW / 2 - 5, tankY + 10,
                        tankX + tankW / 2 + 5, tankY + 20);
                g.drawLine(tankX + tankW / 2 + 5, tankY + 10,
                        tankX + tankW / 2 - 5, tankY + 20);
                g.setFont(new Font("SansSerif", Font.PLAIN, 9));
                g.drawString("FLOW STOPPED", motorX - 1, motorY + 62);
            }

            // Tank outline and moving water level.
            g.setColor(new Color(105, 133, 142));
            g.setStroke(new BasicStroke(3));
            g.drawRoundRect(tankX, tankY, tankW, tankH, 18, 18);
            if (waterHeight > 0) {
                g.setColor(new Color(29, 167, 205, 205));
                g.fillRoundRect(tankX + 3, tankY + tankH - waterHeight,
                        tankW - 6, waterHeight, 14, 14);
                g.setColor(new Color(120, 230, 245, 190));
                g.setStroke(new BasicStroke(2));
                int surfaceY = tankY + tankH - waterHeight + 3;
                g.drawLine(tankX + 8, surfaceY, tankX + tankW - 8, surfaceY);
            }

            g.setColor(TEXT);
            g.setFont(new Font("SansSerif", Font.BOLD, 12));
            g.drawString(String.format("%.0f%%", level * 100), tankX + 40, tankY + tankH + 22);
            g.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AquaPredictApp().setVisible(true));
    }
}

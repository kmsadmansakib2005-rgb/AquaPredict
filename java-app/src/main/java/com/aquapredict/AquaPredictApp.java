package com.aquapredict;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
        pages.add(new PredictorPanel(), "Predictor");

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
        side.add(navButton("Predictor", "Predict tank filling time"));

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
        bar.add(status, BorderLayout.WEST);
        JButton aboutButton = new JButton("About");
        aboutButton.setForeground(TEXT);
        aboutButton.setBackground(PANEL);
        aboutButton.setFont(new Font("SansSerif", Font.BOLD, 11));
        aboutButton.setFocusPainted(false);
        aboutButton.setBorder(new EmptyBorder(7, 16, 7, 16));
        aboutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        aboutButton.addActionListener(e -> showAboutDialog());
        bar.add(aboutButton, BorderLayout.EAST);
        return bar;
    }

    private void showAboutDialog() {
        JDialog dialog = new JDialog(this, "About AquaPredict", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setContentPane(new AboutPanel());
        dialog.setSize(620, 700);
        dialog.setMinimumSize(new Dimension(540, 620));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
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
                Process process = startPythonEngine();

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

    private Process startPythonEngine() throws Exception {
        // Installed builds include a self-contained Python engine beside the application JAR.
        Path codeLocation = Paths.get(
                AquaPredictApp.class.getProtectionDomain().getCodeSource().getLocation().toURI()
        ).toAbsolutePath().normalize();
        Path appDirectory = Files.isDirectory(codeLocation) ? codeLocation : codeLocation.getParent();
        Path bundledEngine = appDirectory.resolve("AquaPredictEngine").resolve("AquaPredictEngine.exe");

        if (Files.isRegularFile(bundledEngine)) {
            return new ProcessBuilder(bundledEngine.toString())
                    .directory(bundledEngine.getParent().toFile())
                    .start();
        }

        // Development mode: use the local Python source tree and installed Python interpreter.
        Path engine = locatePythonEngine();
        return new ProcessBuilder("python", "main.py").directory(engine.toFile()).start();
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


    private class AboutPanel extends JPanel {
        AboutPanel() {
            setBackground(BG);
            setBorder(new EmptyBorder(28, 30, 24, 30));
            setLayout(new BorderLayout(0, 20));
            JLabel title = new JLabel("About the Creator");
            title.setForeground(TEXT);
            title.setFont(new Font("SansSerif", Font.BOLD, 27));
            JLabel subtitle = new JLabel("The person behind AquaPredict.");
            subtitle.setForeground(MUTED);
            subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
            JPanel heading = new JPanel();
            heading.setBackground(BG);
            heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
            heading.add(title);
            heading.add(Box.createVerticalStrut(6));
            heading.add(subtitle);
            add(heading, BorderLayout.NORTH);

            JPanel profile = new JPanel();
            profile.setBackground(PANEL);
            profile.setBorder(new EmptyBorder(28, 30, 28, 30));
            profile.setLayout(new BoxLayout(profile, BoxLayout.Y_AXIS));
            JLabel portrait = new JLabel();
            portrait.setAlignmentX(Component.CENTER_ALIGNMENT);
            portrait.setPreferredSize(new Dimension(220, 220));
            portrait.setMinimumSize(new Dimension(220, 220));
            portrait.setMaximumSize(new Dimension(220, 220));
            try (java.io.InputStream stream = getClass().getResourceAsStream("/images/creator.jpg")) {
                if (stream != null) {
                    Image image = javax.imageio.ImageIO.read(stream).getScaledInstance(220, 220, Image.SCALE_SMOOTH);
                    portrait.setIcon(new ImageIcon(image));
                } else {
                    portrait.setText("KS");
                    portrait.setForeground(GREEN);
                    portrait.setFont(new Font("SansSerif", Font.BOLD, 48));
                }
            } catch (Exception ex) {
                portrait.setText("KS");
                portrait.setForeground(GREEN);
                portrait.setFont(new Font("SansSerif", Font.BOLD, 48));
            }
            profile.add(portrait);
            profile.add(Box.createVerticalStrut(22));
            JLabel name = new JLabel("K. M. Sadman Sakib");
            name.setForeground(TEXT);
            name.setFont(new Font("SansSerif", Font.BOLD, 25));
            name.setAlignmentX(Component.CENTER_ALIGNMENT);
            profile.add(name);
            JLabel role = new JLabel("CREATOR & DEVELOPER");
            role.setForeground(GREEN);
            role.setFont(new Font("SansSerif", Font.BOLD, 11));
            role.setAlignmentX(Component.CENTER_ALIGNMENT);
            role.setBorder(new EmptyBorder(8, 0, 24, 0));
            profile.add(role);
            profile.add(profileDetail("University", "Bangladesh University of Professionals (BUP)"));
            profile.add(Box.createVerticalStrut(13));
            profile.add(profileDetail("Department", "Computer Science and Engineering (CSE)"));
            profile.add(Box.createVerticalStrut(24));
            JLabel note = new JLabel("Developed as an academic project for the Numerical Methods Lab.");
            note.setForeground(MUTED);
            note.setFont(new Font("SansSerif", Font.PLAIN, 11));
            note.setAlignmentX(Component.CENTER_ALIGNMENT);
            profile.add(note);
            JPanel center = new JPanel(new GridBagLayout());
            center.setBackground(BG);
            center.add(profile);
            add(center, BorderLayout.CENTER);
        }

        private JPanel profileDetail(String heading, String value) {
            JPanel item = new JPanel();
            item.setBackground(PANEL_LIGHT);
            item.setBorder(new EmptyBorder(12, 16, 12, 16));
            item.setLayout(new BoxLayout(item, BoxLayout.Y_AXIS));
            JLabel label = new JLabel(heading.toUpperCase());
            label.setForeground(MUTED);
            label.setFont(new Font("SansSerif", Font.BOLD, 9));
            JLabel content = new JLabel(value);
            content.setForeground(TEXT);
            content.setFont(new Font("SansSerif", Font.PLAIN, 13));
            item.add(label);
            item.add(Box.createVerticalStrut(5));
            item.add(content);
            item.setAlignmentX(Component.CENTER_ALIGNMENT);
            return item;
        }
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
            JLabel methods = new JLabel("6");
            methods.setForeground(TEXT);
            methods.setFont(new Font("SansSerif", Font.BOLD, 25));
            metrics.add(metric("NUMERICAL METHODS", methods, "Root finding and ODE methods"));

            JPanel chartCard = cardPanel();
            chartCard.add(sectionLabel("Water level overview"));
            chartCard.add(dashboardChart);
            JPanel quick = cardPanel();
            quick.add(sectionLabel("Quick actions"));
            JButton simulate = actionButton("Open Tank Simulation");
            simulate.setAlignmentX(Component.LEFT_ALIGNMENT);
            simulate.addActionListener(e -> showPage("Tank Simulation"));
            JButton lab = actionButton("Open Predictor");
            lab.setAlignmentX(Component.LEFT_ALIGNMENT);
            lab.addActionListener(e -> showPage("Predictor"));
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
        private JButton motorButton;
        private Timer animationTimer;
        private boolean preparingSimulation;
        private boolean simulationRunning;
        private boolean motorOn;
        private double currentVolume;
        private double elapsedMinutes;
        private double tankCapacity;
        private double simulationDuration;
        private double inflowRate;
        private double outflowRate;
        private double lowThreshold;
        private double highThreshold;
        private final List<Double> liveTimes = new ArrayList<>();
        private final List<Double> liveLevels = new ArrayList<>();

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
            form.add(sectionLabel("Pump control thresholds"));
            addField(form, "Automatically switch pump ON below (%)", pumpOnBelow);
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
            runButton = actionButton("Start Simulator");
            runButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            runButton.setBorder(new EmptyBorder(12, 20, 12, 20));
            runButton.addActionListener(e -> toggleSimulation());

            motorButton = actionButton("Motor ON");
            motorButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            motorButton.setEnabled(false);
            motorButton.addActionListener(e -> toggleMotorManually());

            JPanel controls = new JPanel(new GridLayout(1, 2, 10, 0));
            controls.setBackground(PANEL);
            controls.add(runButton);
            controls.add(motorButton);
            form.add(Box.createVerticalStrut(15));
            form.add(controls);

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

        private void toggleSimulation() {
            if (simulationRunning) {
                stopSimulation("Simulator stopped by user.");
            } else {
                startSimulation();
            }
        }

        private void startSimulation() {
            try {
                if (animationTimer != null && animationTimer.isRunning()) animationTimer.stop();
                tankCapacity = number(capacity);
                currentVolume = number(initial);
                inflowRate = number(inflow);
                outflowRate = number(outflow);
                simulationDuration = number(duration);
                double timeStep = number(step);
                lowThreshold = number(pumpOnBelow) / 100.0 * tankCapacity;
                highThreshold = number(pumpOffAt) / 100.0 * tankCapacity;

                if (tankCapacity <= 0 || currentVolume < 0 || currentVolume > tankCapacity
                        || inflowRate < 0 || outflowRate < 0 || simulationDuration <= 0
                        || timeStep <= 0) {
                    throw new IllegalArgumentException("Check tank capacity, water volume, rates, duration and time step.");
                }
                if (number(pumpOnBelow) < 0 || number(pumpOffAt) > 100
                        || number(pumpOnBelow) >= number(pumpOffAt)) {
                    throw new IllegalArgumentException("Pump thresholds must satisfy 0 <= ON threshold < OFF threshold <= 100.");
                }

                elapsedMinutes = 0;
                motorOn = false;
                simulationRunning = true;
                liveTimes.clear();
                liveLevels.clear();
                liveTimes.add(0.0);
                liveLevels.add(currentVolume);

                // The motor starts automatically only when the water is already below the lower threshold.
                if (currentVolume < lowThreshold) motorOn = true;
                updateLiveDisplay();
                runButton.setText("Stop Simulator");
                motorButton.setEnabled(true);
                motorButton.setText(motorOn ? "Motor OFF" : "Motor ON");
                status.setText("Live tank simulation running...");

                animationTimer = new Timer(100, e -> advanceSimulation());
                animationTimer.start();
            } catch (Exception ex) {
                showInputError(ex);
            }
        }

        private void advanceSimulation() {
            double deltaTime = Math.min(0.1, simulationDuration - elapsedMinutes);
            if (deltaTime <= 1e-9) {
                stopSimulation("Simulation completed.");
                return;
            }

            // Automatic low-level protection always has priority over a manual OFF command.
            if (!motorOn && currentVolume < lowThreshold) motorOn = true;

            double previousVolume = currentVolume;
            double rate = (motorOn ? inflowRate : 0.0) - outflowRate;
            currentVolume += rate * deltaTime;
            currentVolume = Math.max(0, Math.min(tankCapacity, currentVolume));
            elapsedMinutes += deltaTime;

            // Enforce threshold crossings exactly at 30% and 90%.
            if (motorOn && previousVolume < highThreshold && currentVolume >= highThreshold) {
                currentVolume = highThreshold;
                motorOn = false;
            } else if (!motorOn && previousVolume >= lowThreshold && currentVolume <= lowThreshold) {
                currentVolume = lowThreshold;
                motorOn = true;
            }

            liveTimes.add(elapsedMinutes);
            liveLevels.add(currentVolume);
            updateLiveDisplay();

            if (elapsedMinutes >= simulationDuration - 1e-9) {
                stopSimulation("Simulation completed.");
            }
        }

        private void toggleMotorManually() {
            if (!simulationRunning) return;
            if (!motorOn && currentVolume >= highThreshold - 1e-9) {
                JOptionPane.showMessageDialog(AquaPredictApp.this,
                        "The motor cannot be started at 90% or above.",
                        "Motor safety limit", JOptionPane.WARNING_MESSAGE);
                return;
            }
            motorOn = !motorOn;
            updateLiveDisplay();
        }

        private void updateLiveDisplay() {
            double percentage = currentVolume / tankCapacity * 100.0;
            tank.setState(percentage / 100.0, motorOn, (int) (elapsedMinutes * 10));
            finalVolume.setText(String.format("%.1f L  (%.1f%%)", currentVolume, percentage));
            simulationClock.setText(String.format("Elapsed time: %.1f / %.1f min",
                    elapsedMinutes, simulationDuration));
            chart.setData(liveTimes, liveLevels);
            dashboardChart.setData(liveTimes, liveLevels);
            dashboardVolume.setText(String.format("%.1f L", currentVolume));

            if (motorOn) {
                pumpStatus.setText("MOTOR ON • WATER ENTERING");
                pumpStatus.setForeground(GREEN);
                pumpEventSummary.setText("Motor is running. It will stop automatically at "
                        + pumpOffAt.getText().trim() + "%.");
                dashboardStatus.setText("Motor ON • Pumping water");
                motorButton.setText("Motor OFF");
            } else {
                pumpStatus.setText("MOTOR OFF • WATER DECREASING");
                pumpStatus.setForeground(new Color(255, 120, 105));
                pumpEventSummary.setText("Motor is OFF. Water decreases at the configured outflow rate.");
                dashboardStatus.setText("Motor OFF • Water decreasing");
                motorButton.setText("Motor ON");
            }
        }

        private void stopSimulation(String message) {
            if (animationTimer != null) animationTimer.stop();
            simulationRunning = false;
            runButton.setText("Start Simulator");
            motorButton.setEnabled(false);
            motorButton.setText("Motor ON");
            simulationClock.setText(String.format("%s • %.1f min", message, elapsedMinutes));
            status.setText(message);
        }

        private void calculationFailed() {
            if (preparingSimulation) {
                preparingSimulation = false;
                runButton.setEnabled(true);
                runButton.setText("Start Simulator");
            }
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

    private class PredictorPanel extends JPanel {
        private final JComboBox<String> method = new JComboBox<>(new String[]{
                "Bisection", "False Position", "Newton-Raphson", "Euler", "Modified Euler", "Runge-Kutta 4 (RK4)"});
        private final JTextField capacity = field("1000");
        private final JTextField initial = field("300");
        private final JTextField targetPercent = field("80");
        private final JTextField inflow = field("20");
        private final JTextField inflowIncrease = field("0.5");
        private final JTextField outflow = field("5");
        private final JTextField step = field("0.5");
        private final JTextField maxTime = field("100");
        private final JTextField lowerBound = field("0");
        private final JTextField upperBound = field("100");
        private final JTextField initialGuess = field("20");
        private final JTextArea output = outputArea();

        PredictorPanel() {
            setBackground(BG);
            setBorder(new EmptyBorder(28, 30, 24, 30));
            setLayout(new BorderLayout(0, 18));

            JPanel heading = new JPanel();
            heading.setBackground(BG);
            heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
            JLabel title = new JLabel("Predictor");
            title.setForeground(TEXT);
            title.setFont(new Font("SansSerif", Font.BOLD, 26));
            JLabel subtitle = new JLabel("Estimate how long it will take to reach your selected tank level.");
            subtitle.setForeground(MUTED);
            subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
            heading.add(title);
            heading.add(Box.createVerticalStrut(6));
            heading.add(subtitle);
            add(heading, BorderLayout.NORTH);

            JPanel content = new JPanel(new GridLayout(1, 2, 15, 0));
            content.setBackground(BG);
            JPanel form = cardPanel();
            form.add(sectionLabel("Tank and flow parameters"));
            addField(form, "Numerical method", method);
            addField(form, "Tank capacity (L)", capacity);
            addField(form, "Initial water volume (L)", initial);
            addField(form, "Target tank level (%)", targetPercent);
            addField(form, "Base inflow rate (L/min)", inflow);
            addField(form, "Inflow increase (L/min per minute)", inflowIncrease);
            addField(form, "Outflow rate (L/min)", outflow);
            addField(form, "Time step for ODE methods (min)", step);
            addField(form, "Maximum prediction time (min)", maxTime);
            form.add(Box.createVerticalStrut(8));
            form.add(sectionLabel("Root-finding parameters"));
            addField(form, "Lower time bound (a)", lowerBound);
            addField(form, "Upper time bound (b)", upperBound);
            addField(form, "Initial guess (Newton-Raphson)", initialGuess);
            JButton run = actionButton("Predict Filling Time");
            run.setAlignmentX(Component.LEFT_ALIGNMENT);
            run.addActionListener(e -> calculate());
            form.add(Box.createVerticalStrut(14));
            form.add(run);
            JPanel result = cardPanel();
            result.add(sectionLabel("Prediction result"));
            output.setText("Enter the tank and flow details, choose a numerical method, and click Predict Filling Time.");
            result.add(new JScrollPane(output));
            content.add(scrollForm(form));
            content.add(result);
            add(content, BorderLayout.CENTER);
        }

        private void calculate() {
            try {
                double cap = number(capacity), start = number(initial), target = number(targetPercent);
                double dt = number(step), limit = number(maxTime), qIn = number(inflow);
                double qIncrease = number(inflowIncrease), qOut = number(outflow);
                if (cap <= 0 || start < 0 || start > cap || target < 0 || target > 100
                        || dt <= 0 || limit <= 0 || qIn < 0 || qIncrease < 0 || qOut < 0) {
                    throw new IllegalArgumentException("Enter valid tank, flow, time-step and duration values. Target level must be between 0 and 100%.");
                }
                String selected = method.getSelectedItem().toString();
                String key = selected.equals("Newton-Raphson") ? "newton_raphson"
                        : selected.equals("False Position") ? "false_position"
                        : selected.equals("Euler") ? "euler"
                        : selected.equals("Modified Euler") ? "modified_euler"
                        : selected.equals("Runge-Kutta 4 (RK4)") ? "rk4" : "bisection";
                JsonObject req = new JsonObject();
                req.addProperty("operation", "predictor");
                req.addProperty("method", key);
                req.addProperty("capacity", cap);
                req.addProperty("initial_water", start);
                req.addProperty("target_percent", target);
                req.addProperty("inflow_base", qIn);
                req.addProperty("inflow_increase", qIncrease);
                req.addProperty("outflow_rate", qOut);
                req.addProperty("time_step", dt);
                req.addProperty("max_time", limit);
                req.addProperty("a", number(lowerBound));
                req.addProperty("b", number(upperBound));
                req.addProperty("initial_guess", number(initialGuess));
                runPython(req, result -> {
                    StringBuilder report = new StringBuilder();
                    report.append("Selected method: ").append(selected).append("\n");
                    report.append(String.format("Target tank level: %.1f%% (%.2f L)%n",
                            result.get("target_percent").getAsDouble(), result.get("target_water").getAsDouble()));
                    report.append(String.format("%nPredicted filling time: %.4f minutes%n",
                            result.get("predicted_time").getAsDouble()));
                    if (result.has("iterations")) report.append("Iterations: ").append(result.get("iterations").getAsInt()).append("\n");
                    if (result.has("steps")) report.append("Time steps evaluated: ").append(result.get("steps").getAsInt()).append("\n");
                    report.append("\nEstimate based on the selected numerical method and entered flow model.");
                    output.setText(report.toString());
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

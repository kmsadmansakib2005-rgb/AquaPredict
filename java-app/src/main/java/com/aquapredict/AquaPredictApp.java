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
        private final JTextField duration = field("10");
        private final JTextField step = field("1");
        private final JLabel finalVolume = new JLabel("-- L");
        private final WaterChart chart = new WaterChart();
        private final TankDrawing tank = new TankDrawing();

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
            JButton run = actionButton("Run Simulation");
            run.setAlignmentX(Component.LEFT_ALIGNMENT);
            run.setBorder(new EmptyBorder(12, 20, 12, 20));
            run.addActionListener(e -> simulate());
            form.add(Box.createVerticalStrut(15));
            form.add(run);

            JPanel visual = cardPanel();
            visual.add(sectionLabel("Live tank model"));
            tank.setPreferredSize(new Dimension(300, 235));
            visual.add(tank);
            finalVolume.setForeground(GREEN);
            finalVolume.setFont(new Font("SansSerif", Font.BOLD, 27));
            visual.add(Box.createVerticalStrut(8));
            visual.add(finalVolume);
            JLabel chartTitle = new JLabel("Water volume over time");
            chartTitle.setForeground(MUTED);
            chartTitle.setBorder(new EmptyBorder(14, 0, 8, 0));
            visual.add(chartTitle);
            visual.add(chart);

            content.add(form);
            content.add(visual);
            add(content, BorderLayout.CENTER);
        }

        private void simulate() {
            try {
                JsonObject request = new JsonObject();
                request.addProperty("operation", "tank_simulation");
                request.addProperty("capacity", number(capacity));
                request.addProperty("initial_water", number(initial));
                request.addProperty("inflow_rate", number(inflow));
                request.addProperty("outflow_rate", number(outflow));
                request.addProperty("simulation_time", integer(duration));
                request.addProperty("time_step", integer(step));
                if (integer(duration) <= 0 || integer(step) <= 0) {
                    throw new IllegalArgumentException("Duration and time step must be positive integers.");
                }
                runPython(request, result -> {
                    double finalValue = result.get("final_volume").getAsDouble();
                    finalVolume.setText(String.format("%.2f L", finalValue));
                    dashboardVolume.setText(String.format("%.2f L", finalValue));
                    dashboardStatus.setText("Simulation complete");
                    List<Double> times = toList(result.getAsJsonArray("times"));
                    List<Double> levels = toList(result.getAsJsonArray("water_levels"));
                    chart.setData(times, levels);
                    dashboardChart.setData(times, levels);
                    tank.setLevel(finalValue / number(capacity));
                });
            } catch (Exception ex) {
                showInputError(ex);
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
                    g.setColor(PANEL);
                    g.fillRect(tabPane.getInsets().left, calculateTabAreaHeight(placement, runCount),
                            tabPane.getWidth() - tabPane.getInsets().left - tabPane.getInsets().right,
                            tabPane.getHeight() - calculateTabAreaHeight(placement, runCount)
                                    - tabPane.getInsets().bottom);
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

        TankDrawing() {
            setBackground(PANEL);
        }

        void setLevel(double level) {
            this.level = Math.max(0, Math.min(1, level));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = 112, h = 190;
            int x = (getWidth() - w) / 2, y = 12;
            g.setColor(new Color(105, 133, 142));
            g.setStroke(new BasicStroke(3));
            g.drawRoundRect(x, y, w, h, 18, 18);
            int waterHeight = (int) (h * level);
            g.setColor(new Color(29, 167, 145, 190));
            g.fillRoundRect(x + 3, y + h - waterHeight, w - 6, waterHeight, 14, 14);
            g.setColor(TEXT);
            g.setFont(new Font("SansSerif", Font.BOLD, 12));
            g.drawString(String.format("%.0f%%", level * 100), x + 40, y + h + 22);
            g.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AquaPredictApp().setVisible(true));
    }
}

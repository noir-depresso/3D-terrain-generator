import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {

    // ===== Worm controls =====
    private final JSpinner numWormsSpinner;
    private final JCheckBox chooseColorsCheckbox;

    private final JCheckBox randomEndpointsCheckbox;
    private final JTextField startField;
    private final JTextField endField;

    private final JRadioButton fixedSizeRadio;
    private final JRadioButton randomSizeRadio;
    private final JSpinner fixedSizeSpinner;
    private final JSpinner minSizeSpinner;
    private final JSpinner maxSizeSpinner;

    // ===== Terrain controls =====
    private final JCheckBox terrainModeCheckbox;

    private final JSpinner gridXSpinner;
    private final JSpinner gridZSpinner;
    private final JSpinner spacingSpinner;
    private final JSpinner seedSpinner;

    private final JSpinner noiseScaleSpinner;
    private final JSpinner amplitudeSpinner;
    private final JSpinner octavesSpinner;
    private final JSpinner persistenceSpinner;
    private final JSpinner lacunaritySpinner;

    private final JCheckBox ridgedCheckbox;
    private final JCheckBox valleysCheckbox;
    private final JSpinner sharpnessSpinner;

    private final JCheckBox rowsCheckbox;
    private final JCheckBox colsCheckbox;
    private final JSpinner terrainDetailSpinner;

    public SettingsPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        int row = 0;

        // ===== Number of worms =====
        JLabel wormsLabel = new JLabel("Number of worms (lines):");
        numWormsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));

        c.gridx = 0; c.gridy = row;
        add(wormsLabel, c);
        c.gridx = 1;
        add(numWormsSpinner, c);
        row++;

        // ===== Color choice =====
        chooseColorsCheckbox = new JCheckBox("Choose colors manually (otherwise random neon)");
        chooseColorsCheckbox.setSelected(false);

        c.gridx = 0; c.gridy = row; c.gridwidth = 2;
        add(chooseColorsCheckbox, c);
        row++;
        c.gridwidth = 1;

        // ===== Endpoints =====
        randomEndpointsCheckbox = new JCheckBox("Random start/end points");
        randomEndpointsCheckbox.setSelected(true);

        c.gridx = 0; c.gridy = row; c.gridwidth = 2;
        add(randomEndpointsCheckbox, c);
        row++;
        c.gridwidth = 1;

        startField = new JTextField("0,0,0", 15);
        endField   = new JTextField("50,20,50", 15);

        c.gridx = 0; c.gridy = row;
        add(new JLabel("Start (x,y,z):"), c);
        c.gridx = 1;
        add(startField, c);
        row++;

        c.gridx = 0; c.gridy = row;
        add(new JLabel("End (x,y,z):"), c);
        c.gridx = 1;
        add(endField, c);
        row++;

        // ===== Segment size mode =====
        fixedSizeRadio  = new JRadioButton("Fixed segment size:");
        randomSizeRadio = new JRadioButton("Random segment size in range:");
        ButtonGroup sizeGroup = new ButtonGroup();
        sizeGroup.add(fixedSizeRadio);
        sizeGroup.add(randomSizeRadio);

        fixedSizeRadio.setSelected(true);

        fixedSizeSpinner = new JSpinner(new SpinnerNumberModel(1.0, 0.1, 10.0, 0.1));
        minSizeSpinner   = new JSpinner(new SpinnerNumberModel(0.5, 0.1, 10.0, 0.1));
        maxSizeSpinner   = new JSpinner(new SpinnerNumberModel(1.5, 0.1, 10.0, 0.1));

        c.gridx = 0; c.gridy = row;
        add(fixedSizeRadio, c);
        c.gridx = 1;
        add(fixedSizeSpinner, c);
        row++;

        JPanel rangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        rangePanel.add(new JLabel("min:"));
        rangePanel.add(minSizeSpinner);
        rangePanel.add(new JLabel("max:"));
        rangePanel.add(maxSizeSpinner);

        c.gridx = 0; c.gridy = row;
        add(randomSizeRadio, c);
        c.gridx = 1;
        add(rangePanel, c);
        row++;

        // ===== Terrain mode toggle =====
        terrainModeCheckbox = new JCheckBox("Terrain mode (Perlin wireframe)");
        terrainModeCheckbox.setSelected(false);

        c.gridx = 0; c.gridy = row; c.gridwidth = 2;
        add(terrainModeCheckbox, c);
        row++;
        c.gridwidth = 1;

        // ===== Terrain controls =====
        gridXSpinner = new JSpinner(new SpinnerNumberModel(120, 10, 500, 10));
        gridZSpinner = new JSpinner(new SpinnerNumberModel(120, 10, 500, 10));
        spacingSpinner = new JSpinner(new SpinnerNumberModel(2.0, 0.1, 20.0, 0.1));
        seedSpinner = new JSpinner(new SpinnerNumberModel(1337, 0, Integer.MAX_VALUE, 1));

        noiseScaleSpinner = new JSpinner(new SpinnerNumberModel(0.05, 0.001, 1.0, 0.005));
        amplitudeSpinner = new JSpinner(new SpinnerNumberModel(35.0, 0.0, 300.0, 1.0));
        octavesSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 12, 1));
        persistenceSpinner = new JSpinner(new SpinnerNumberModel(0.5, 0.01, 0.99, 0.01));
        lacunaritySpinner = new JSpinner(new SpinnerNumberModel(2.0, 1.0, 4.0, 0.1));
        terrainDetailSpinner = new JSpinner(new SpinnerNumberModel(0.25, 0.05, 1.0, 0.05));

        ridgedCheckbox = new JCheckBox("Ridged mountains");
        ridgedCheckbox.setSelected(true);

        valleysCheckbox = new JCheckBox("Valleys (invert)");
        valleysCheckbox.setSelected(false);

        sharpnessSpinner = new JSpinner(new SpinnerNumberModel(2.2, 0.1, 8.0, 0.1));

        rowsCheckbox = new JCheckBox("Draw rows");
        colsCheckbox = new JCheckBox("Draw cols");
        rowsCheckbox.setSelected(true);
        colsCheckbox.setSelected(true);

        // Layout terrain controls
        c.gridx = 0; c.gridy = row; add(new JLabel("Grid X:"), c);
        c.gridx = 1; add(gridXSpinner, c); row++;

        c.gridx = 0; c.gridy = row; add(new JLabel("Grid Z:"), c);
        c.gridx = 1; add(gridZSpinner, c); row++;

        c.gridx = 0; c.gridy = row; add(new JLabel("Spacing:"), c);
        c.gridx = 1; add(spacingSpinner, c); row++;

        c.gridx = 0; c.gridy = row; add(new JLabel("Seed:"), c);
        c.gridx = 1; add(seedSpinner, c); row++;

        c.gridx = 0; c.gridy = row; add(new JLabel("Noise scale:"), c);
        c.gridx = 1; add(noiseScaleSpinner, c); row++;

        c.gridx = 0; c.gridy = row; add(new JLabel("Amplitude:"), c);
        c.gridx = 1; add(amplitudeSpinner, c); row++;

        c.gridx = 0; c.gridy = row; add(new JLabel("Octaves:"), c);
        c.gridx = 1; add(octavesSpinner, c); row++;

        c.gridx = 0; c.gridy = row; add(new JLabel("Persistence:"), c);
        c.gridx = 1; add(persistenceSpinner, c); row++;

        c.gridx = 0; c.gridy = row; add(new JLabel("Lacunarity:"), c);
        c.gridx = 1; add(lacunaritySpinner, c); row++;

        c.gridx = 0; c.gridy = row; c.gridwidth = 2;
        add(ridgedCheckbox, c);
        row++;

        c.gridx = 0; c.gridy = row; c.gridwidth = 2;
        add(valleysCheckbox, c);
        row++;
        c.gridwidth = 1;

        c.gridx = 0; c.gridy = row; add(new JLabel("Sharpness:"), c);
        c.gridx = 1; add(sharpnessSpinner, c); row++;

        c.gridx = 0; c.gridy = row; add(new JLabel("Terrain detail (1=full):"), c);
        c.gridx = 1; add(terrainDetailSpinner, c); row++;

        JPanel wfPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        wfPanel.add(rowsCheckbox);
        wfPanel.add(colsCheckbox);

        c.gridx = 0; c.gridy = row; c.gridwidth = 2;
        add(wfPanel, c);
        row++;
        c.gridwidth = 1;

        // ===== Initial enable/disable =====
        updateAllEnabledStates();

        // ===== Listeners =====
        terrainModeCheckbox.addActionListener(e -> updateAllEnabledStates());
        randomEndpointsCheckbox.addActionListener(e -> updateAllEnabledStates());
        fixedSizeRadio.addActionListener(e -> updateAllEnabledStates());
        randomSizeRadio.addActionListener(e -> updateAllEnabledStates());
    }

    // One function controls everything so states never conflict
    private void updateAllEnabledStates() {
        boolean terrain = terrainModeCheckbox.isSelected();

        // Terrain controls
        gridXSpinner.setEnabled(terrain);
        gridZSpinner.setEnabled(terrain);
        spacingSpinner.setEnabled(terrain);
        seedSpinner.setEnabled(terrain);

        noiseScaleSpinner.setEnabled(terrain);
        amplitudeSpinner.setEnabled(terrain);
        octavesSpinner.setEnabled(terrain);
        persistenceSpinner.setEnabled(terrain);
        lacunaritySpinner.setEnabled(terrain);

        ridgedCheckbox.setEnabled(terrain);
        valleysCheckbox.setEnabled(terrain);
        sharpnessSpinner.setEnabled(terrain);

        terrainDetailSpinner.setEnabled(terrain);
        rowsCheckbox.setEnabled(terrain);
        colsCheckbox.setEnabled(terrain);

        // Worm controls
        numWormsSpinner.setEnabled(!terrain);
        chooseColorsCheckbox.setEnabled(!terrain);

        randomEndpointsCheckbox.setEnabled(!terrain);
        boolean random = randomEndpointsCheckbox.isSelected();
        startField.setEnabled(!terrain && !random);
        endField.setEnabled(!terrain && !random);

        fixedSizeRadio.setEnabled(!terrain);
        randomSizeRadio.setEnabled(!terrain);

        boolean fixed  = fixedSizeRadio.isSelected();
        boolean randSz = randomSizeRadio.isSelected();

        fixedSizeSpinner.setEnabled(!terrain && fixed);
        minSizeSpinner.setEnabled(!terrain && randSz);
        maxSizeSpinner.setEnabled(!terrain && randSz);
    }

    // ===== Terrain getters =====
    public boolean isTerrainMode() {
        return terrainModeCheckbox.isSelected();
    }

    public double getTerrainDetail() {
        return ((Number) terrainDetailSpinner.getValue()).doubleValue();
    }

    public TerrainSettings buildTerrainSettings() {
        TerrainSettings ts = new TerrainSettings();

        ts.gridX = ((Number) gridXSpinner.getValue()).intValue();
        ts.gridZ = ((Number) gridZSpinner.getValue()).intValue();
        ts.spacing = ((Number) spacingSpinner.getValue()).doubleValue();
        ts.seed = ((Number) seedSpinner.getValue()).longValue();

        ts.noiseScale = ((Number) noiseScaleSpinner.getValue()).doubleValue();
        ts.amplitude = ((Number) amplitudeSpinner.getValue()).doubleValue();
        ts.octaves = ((Number) octavesSpinner.getValue()).intValue();
        ts.persistence = ((Number) persistenceSpinner.getValue()).doubleValue();
        ts.lacunarity = ((Number) lacunaritySpinner.getValue()).doubleValue();

        ts.ridgedMountains = ridgedCheckbox.isSelected();
        ts.valleys = valleysCheckbox.isSelected();
        ts.sharpness = ((Number) sharpnessSpinner.getValue()).doubleValue();

        ts.drawRows = rowsCheckbox.isSelected();
        ts.drawCols = colsCheckbox.isSelected();

        return ts;
    }

    // ===== Worm getters =====
    public int getNumWorms() {
        return (Integer) numWormsSpinner.getValue();
    }

    public boolean isChooseColorsManually() {
        return chooseColorsCheckbox.isSelected();
    }

    public boolean isRandomEndpoints() {
        return randomEndpointsCheckbox.isSelected();
    }

    public String getStartText() {
        return startField.getText();
    }

    public String getEndText() {
        return endField.getText();
    }

    public boolean useRandomSegmentSizes() {
        return randomSizeRadio.isSelected();
    }

    public double getFixedSize() {
        return ((Number) fixedSizeSpinner.getValue()).doubleValue();
    }

    public double getMinSize() {
        return ((Number) minSizeSpinner.getValue()).doubleValue();
    }

    public double getMaxSize() {
        return ((Number) maxSizeSpinner.getValue()).doubleValue();
    }

    // Build a WormSettings template (shared defaults for all worms)
    public WormSettings buildSettingsTemplate() {
        WormSettings s = new WormSettings();

        if (!isRandomEndpoints()) {
            s.startPoint = parseVector(getStartText(), new Vector3(0, 0, 0));
            s.endPoint   = parseVector(getEndText(), new Vector3(50, 20, 50));
        }

        s.useRandomSegmentSizes = useRandomSegmentSizes();
        if (s.useRandomSegmentSizes) {
            double min = getMinSize();
            double max = getMaxSize();
            if (min <= 0 || max <= 0 || min >= max) {
                min = 0.5;
                max = 1.5;
            }
            s.minSegmentSize = min;
            s.maxSegmentSize = max;
            s.fixedSegmentSize = (min + max) / 2.0;
        } else {
            double fixed = getFixedSize();
            if (fixed <= 0) fixed = 1.0;
            s.fixedSegmentSize = fixed;
            s.minSegmentSize = fixed;
            s.maxSegmentSize = fixed;
        }

        return s;
    }

    private Vector3 parseVector(String text, Vector3 fallback) {
        try {
            String[] parts = text.split(",");
            if (parts.length != 3) return fallback;
            double x = Double.parseDouble(parts[0].trim());
            double y = Double.parseDouble(parts[1].trim());
            double z = Double.parseDouble(parts[2].trim());
            return new Vector3(x, y, z);
        } catch (Exception e) {
            return fallback;
        }
    }
}

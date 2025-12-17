import javax.swing.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomWalk3DApp {

    private static final Random RAND = new Random();

    private static Color randomNeonColor() {
        float hue = RAND.nextFloat();
        return Color.getHSBColor(hue, 1.0f, 1.0f);
    }

    private static double randRange(double min, double max) {
        return min + RAND.nextDouble() * (max - min);
    }

    private static Vector3 randomPointInCube(double min, double max) {
        return new Vector3(
                randRange(min, max),
                randRange(min, max),
                randRange(min, max)
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            // === Show settings panel ===
            SettingsPanel panel = new SettingsPanel();
            int result = JOptionPane.showConfirmDialog(
                    null,
                    panel,
                    "3D Worm / Terrain Settings",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result != JOptionPane.OK_OPTION) return;

            // Create lists FIRST (so terrain/worm both can fill them)
            List<PathGenerator> generators = new ArrayList<>();
            List<Color> colors = new ArrayList<>();

            if (panel.isTerrainMode()) {
                // ===== TERRAIN MODE =====
                TerrainSettings ts = panel.buildTerrainSettings();
                
                double detail = panel.getTerrainDetail(); // e.g. 0.2 .. 1.0
                generators.addAll(PerlinTerrainGenerator.generateWireframeDetail(ts, detail));
                for (int i = 0; i < generators.size(); i++) colors.add(Color.WHITE);

//
//                generators.addAll(PerlinTerrainGenerator.generateWireframe(ts));
//
//                // One color per polyline
//                Color terrainColor = new Color(200, 200, 255);
//                for (int i = 0; i < generators.size(); i++) {
//                    colors.add(terrainColor);
//                }
            	generators.addAll(PerlinTerrainGenerator.generateWireframe(ts, 2));
            	for (int i = 0; i < generators.size(); i++) colors.add(Color.WHITE);


            } else {
                // ===== WORM MODE =====
                int numWorms = panel.getNumWorms();
                if (numWorms < 1) numWorms = 1;
                if (numWorms > 10) numWorms = 10;

                boolean chooseColorsManually = panel.isChooseColorsManually();
                boolean randomEndpoints = panel.isRandomEndpoints();

                WormSettings template = panel.buildSettingsTemplate();

                for (int i = 0; i < numWorms; i++) {
                    WormSettings s = template.copy();

                    if (randomEndpoints) {
                        s.startPoint = randomPointInCube(-30, 30);
                        s.endPoint   = randomPointInCube(-30, 30);
                    }

                    RandomWormGenerator gen = new RandomWormGenerator(s);
                    gen.generate();
                    generators.add(gen);

                    Color col;
                    if (chooseColorsManually) {
                        Color initial = randomNeonColor();
                        col = JColorChooser.showDialog(
                                null,
                                "Choose color for worm #" + (i + 1),
                                initial
                        );
                        if (col == null) col = initial;
                    } else {
                        col = randomNeonColor();
                    }
                    colors.add(col);
                }
            }

            // ===== Create window =====
            JFrame frame = new JFrame(panel.isTerrainMode() ? "3D Terrain Viewer" : "3D Random Worm Viewer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            RandomWalk3DViewer viewer = new RandomWalk3DViewer(generators, colors);
            frame.add(viewer);

            frame.setSize(900, 700);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

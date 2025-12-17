import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class TerrainPolylineGenerator implements PathGenerator, SegmentColorProvider {
    private final List<Vector3> points = new ArrayList<>();
    private final List<Double> segmentSizes = new ArrayList<>();
    private final List<Color> segmentColors = new ArrayList<>();
    private final WormSettings settings;

    private double minH = -1;
    private double maxH =  1;

    public TerrainPolylineGenerator(WormSettings settings) {
        this.settings = settings;
    }

    public void setPoints(List<Vector3> pts, double minHeight, double maxHeight) {
        points.clear();
        points.addAll(pts);

        this.minH = minHeight;
        this.maxH = maxHeight;
        if (Math.abs(maxH - minH) < 1e-9) {
            maxH = minH + 1.0;
        }

        segmentSizes.clear();
        segmentColors.clear();

        for (int i = 1; i < points.size(); i++) {
            Vector3 a = points.get(i - 1);
            Vector3 b = points.get(i);

            segmentSizes.add(b.subtract(a).length());

            double avgY = 0.5 * (a.y + b.y);
            segmentColors.add(colorFromHeight(avgY));
        }
    }

    private Color colorFromHeight(double y) {
        double t = (y - minH) / (maxH - minH);
        if (t < 0) t = 0;
        if (t > 1) t = 1;

        // hue: 0.66 (blue) -> 0.0 (red)
        float hue = (float) (0.66 * (1.0 - t));
        float sat = 1.0f;

        // make low areas slightly darker, peaks brighter
        float bri = (float) (0.55 + 0.45 * t);

        return Color.getHSBColor(hue, sat, bri);
    }

    @Override public void generate() { /* no-op */ }
    @Override public List<Vector3> getPoints() { return points; }
    @Override public List<Double> getSegmentSizes() { return segmentSizes; }
    @Override public WormSettings getSettings() { return settings; }

    @Override
    public Color getSegmentColor(int segmentIndex) {
        if (segmentIndex < 0 || segmentIndex >= segmentColors.size()) return Color.WHITE;
        return segmentColors.get(segmentIndex);
    }
}

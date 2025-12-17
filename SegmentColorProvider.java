import java.awt.Color;

public interface SegmentColorProvider {
    // segmentIndex corresponds to line from points[i] -> points[i+1]
    Color getSegmentColor(int segmentIndex);
}

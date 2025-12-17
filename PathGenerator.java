import java.util.List;

public interface PathGenerator {
    void generate();
    List<Vector3> getPoints();
    List<Double> getSegmentSizes();
    WormSettings getSettings();
}

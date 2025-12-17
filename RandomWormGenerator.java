import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomWormGenerator implements PathGenerator{

    private final WormSettings settings;
    private final Random rand = new Random();

    private final List<Vector3> points = new ArrayList<>();
    private final List<Double> segmentSizes = new ArrayList<>();

    public RandomWormGenerator(WormSettings settings) {
        this.settings = settings;
    }

    public void generate() {
        points.clear();
        segmentSizes.clear();

        Vector3 p = new Vector3(
                settings.startPoint.x,
                settings.startPoint.y,
                settings.startPoint.z
        );

        // Initial direction: towards end (or default if same point)
        Vector3 toEnd = new Vector3(
                settings.endPoint.x - p.x,
                settings.endPoint.y - p.y,
                settings.endPoint.z - p.z
        );
        Vector3 dir = toEnd.normalize();
        if (dir.length() < 1e-6) {
            dir = new Vector3(0, 0, 1);
        }

        points.add(new Vector3(p.x, p.y, p.z));

        for (int i = 0; i < settings.numSteps; i++) {
            // Vector to end
            toEnd = new Vector3(
                    settings.endPoint.x - p.x,
                    settings.endPoint.y - p.y,
                    settings.endPoint.z - p.z
            );
            double distToEnd = toEnd.length();

            // If very close to end, stop
            if (distToEnd < settings.minSegmentSize * 1.2) {
                break;
            }

            Vector3 targetDir = toEnd.normalize();
            Vector3 randomDir = randomUnitVector();

            double t = (double) i / (double) settings.numSteps;

            // Attraction grows over time (so later steps home in on end)
            double attraction = settings.baseAttraction + settings.attractionGrowth * t;
            if (attraction > 0.8) attraction = 0.8;

            double wanderStrength = settings.wanderStrength;
            double persistence = 1.0 - attraction;

            // Combine: old dir + towards end + random
            Vector3 combined = dir.scale(persistence)
                    .add(targetDir.scale(attraction))
                    .add(randomDir.scale(wanderStrength));

            Vector3 newDir = combined.normalize();
            if (newDir.length() < 1e-6) {
                newDir = dir;
            }

            dir = newDir;

            // Segment size
            double stepSize;
            if (settings.useRandomSegmentSizes) {
                stepSize = settings.minSegmentSize +
                        rand.nextDouble() * (settings.maxSegmentSize - settings.minSegmentSize);
            } else {
                stepSize = settings.fixedSegmentSize;
            }

            if (stepSize > distToEnd) {
                stepSize = distToEnd;
            }

            p = p.add(dir.scale(stepSize));

            segmentSizes.add(stepSize);
            points.add(new Vector3(p.x, p.y, p.z));
        }

        // Snap last point exactly to end
        Vector3 last = points.get(points.size() - 1);
        Vector3 finalVec = new Vector3(
                settings.endPoint.x - last.x,
                settings.endPoint.y - last.y,
                settings.endPoint.z - last.z
        );
        double finalDist = finalVec.length();

        if (finalDist > 1e-6) {
            segmentSizes.add(finalDist);
            points.add(new Vector3(
                    settings.endPoint.x,
                    settings.endPoint.y,
                    settings.endPoint.z
            ));
        }

        recenterPath();
    }

    private Vector3 randomUnitVector() {
        double u = 2 * rand.nextDouble() - 1;  // [-1,1]
        double theta = 2 * Math.PI * rand.nextDouble();
        double sqrt1MinusU2 = Math.sqrt(1 - u * u);
        double x = sqrt1MinusU2 * Math.cos(theta);
        double y = sqrt1MinusU2 * Math.sin(theta);
        double z = u;
        return new Vector3(x, y, z);
    }

    private void recenterPath() {
        if (points.isEmpty()) return;

        double sumX = 0, sumY = 0, sumZ = 0;
        for (Vector3 v : points) {
            sumX += v.x;
            sumY += v.y;
            sumZ += v.z;
        }
        double cx = sumX / points.size();
        double cy = sumY / points.size();
        double cz = sumZ / points.size();

        for (Vector3 v : points) {
            v.x -= cx;
            v.y -= cy;
            v.z -= cz;
        }
    }

    public List<Vector3> getPoints() {
        return points;
    }

    public List<Double> getSegmentSizes() {
        return segmentSizes;
    }

    public WormSettings getSettings() {
        return settings;
    }
}

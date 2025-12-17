import java.util.ArrayList;
import java.util.List;

public final class SurfaceExtractor {

    public static List<SurfacePoint> extractSurfacePoints(
            DensityField field,
            Vector3 min, Vector3 max,
            double step,
            boolean clipEnabled,
            Vector3 clipOrigin,
            Vector3 clipNormalUnit
    ) {
        ArrayList<SurfacePoint> out = new ArrayList<>();

        // precompute eps for gradient
        Vector3 ex = new Vector3(step, 0, 0);
        Vector3 ey = new Vector3(0, step, 0);
        Vector3 ez = new Vector3(0, 0, step);

        // simple directional light (vector-based)
        Vector3 lightDir = new Vector3(0.35, 0.9, 0.25).normalize();

        for (double x = min.x; x <= max.x; x += step) {
            for (double y = min.y; y <= max.y; y += step) {
                for (double z = min.z; z <= max.z; z += step) {
                    Vector3 p = new Vector3(x, y, z);

                    if (clipEnabled) {
                        double side = p.subtract(clipOrigin).dot(clipNormalUnit);
                        if (side > 0) continue; // cut away the "front" half
                    }

                    double d = field.density(p);
                    if (d <= 0) continue; // air

                    // surface test: any 6-neighbor is air
                    if (field.density(p.add(ex)) > 0 &&
                        field.density(p.subtract(ex)) > 0 &&
                        field.density(p.add(ey)) > 0 &&
                        field.density(p.subtract(ey)) > 0 &&
                        field.density(p.add(ez)) > 0 &&
                        field.density(p.subtract(ez)) > 0) {
                        continue; // fully inside rock -> do not draw
                    }

                    // gradient normal
                    double gx = field.density(p.add(ex)) - field.density(p.subtract(ex));
                    double gy = field.density(p.add(ey)) - field.density(p.subtract(ey));
                    double gz = field.density(p.add(ez)) - field.density(p.subtract(ez));

                    Vector3 n = new Vector3(gx, gy, gz).normalize();
                    double light = Math.max(0.08, Math.min(1.0, n.dot(lightDir)));

                    out.add(new SurfacePoint(p, n, light));
                }
            }
        }
        return out;
    }
}

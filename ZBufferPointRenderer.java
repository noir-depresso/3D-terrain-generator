import java.awt.image.BufferedImage;
import java.util.List;

public final class ZBufferPointRenderer {

    public static final class ProjectResult {
        public final int sx, sy;
        public final double depth; // bigger = farther or vice versa, just be consistent
        public ProjectResult(int sx, int sy, double depth) {
            this.sx = sx; this.sy = sy; this.depth = depth;
        }
    }

    public interface Projector {
        ProjectResult project(Vector3 world, int w, int h);
    }

    public static void render(
            BufferedImage img,
            List<SurfacePoint> points,
            Projector projector
    ) {
        int w = img.getWidth();
        int h = img.getHeight();

        double[] zbuf = new double[w * h];
        for (int i = 0; i < zbuf.length; i++) zbuf[i] = Double.POSITIVE_INFINITY;

        for (SurfacePoint sp : points) {
            ProjectResult pr = projector.project(sp.pos, w, h);
            if (pr == null) continue;

            int x = pr.sx, y = pr.sy;
            if (x < 0 || x >= w || y < 0 || y >= h) continue;

            int idx = y * w + x;
            double z = pr.depth;

            // smaller depth = closer (if you use opposite, flip comparison)
            if (z >= zbuf[idx]) continue;
            zbuf[idx] = z;

            // grayscale lighting (you can tint later)
            int c = (int) Math.round(255 * sp.light);
            int rgb = (0xFF << 24) | (c << 16) | (c << 8) | (c);
            img.setRGB(x, y, rgb);
        }
    }
}

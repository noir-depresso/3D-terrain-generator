import java.util.ArrayList;
import java.util.List;

public class PerlinTerrainGenerator {

	double detail = 0.25;                 // 25% detail
	int step = (int) Math.max(1, Math.round(1.0 / detail));  // 4

	public static List<PathGenerator> generateWireframeDetail(TerrainSettings ts, double detail01) {
	    // detail01: (0, 1], where 1 = full detail, 0.25 = 4x stride
	    if (detail01 <= 0) detail01 = 0.05;
	    if (detail01 > 1) detail01 = 1.0;

	    int step = (int) Math.max(1, Math.round(1.0 / detail01));
	    return generateWireframe(ts, step);
	}

	
    public static List<PathGenerator> generateWireframe(TerrainSettings ts, int step) {
        PerlinNoise pn = new PerlinNoise(ts.seed);

        
        int gx = Math.max(2, ts.gridX);
        int gz = Math.max(2, ts.gridZ);

        double[][] H = new double[gz][gx];

        // 1) Compute FULL heightmap
        for (int z = 0; z < gz; z++) {
            for (int x = 0; x < gx; x++) {
                double nx = x * ts.noiseScale;
                double nz = z * ts.noiseScale;

                double n = pn.fbm(nx, nz, ts.octaves, ts.persistence, ts.lacunarity); // ~[-1,1]

                double shaped;
                if (ts.ridgedMountains) {
                    double ridge = 1.0 - Math.abs(n);
                    ridge = clamp01(ridge);
                    shaped = Math.pow(ridge, ts.sharpness);
                } else {
                    double h01 = (n + 1.0) * 0.5;
                    h01 = clamp01(h01);
                    shaped = Math.pow(h01, ts.sharpness);
                }

                double h = shaped * ts.amplitude;
                if (ts.valleys) h = -h;

                H[z][x] = h;
            }
        }

        // 2) Smooth full heightmap (smooth surface)
        smoothHeightmap(H, 2);

        // 3) Find min/max AFTER smoothing (for coloring)
        double minH = Double.POSITIVE_INFINITY;
        double maxH = Double.NEGATIVE_INFINITY;
        for (int z = 0; z < gz; z++) {
            for (int x = 0; x < gx; x++) {
                double h = H[z][x];
                if (h < minH) minH = h;
                if (h > maxH) maxH = h;
            }
        }

        // 4) Geometry centering (computed once)
        double halfW = (gx - 1) * ts.spacing * 0.5;
        double halfD = (gz - 1) * ts.spacing * 0.5;

        // 5) LOD step used ONLY for sampling/drawing
        step = Math.max(1, step);

        WormSettings wireSettings = new WormSettings();
        wireSettings.useRandomSegmentSizes = true;
        wireSettings.fixedSegmentSize = 1.0;
        wireSettings.minSegmentSize = ts.strokeMin;
        wireSettings.maxSegmentSize = ts.strokeMax;

        List<PathGenerator> paths = new ArrayList<>();

        if (ts.drawRows) {
            for (int z = 0; z < gz; z += step) {
                List<Vector3> row = new ArrayList<>((gx + step - 1) / step);

                for (int x = 0; x < gx; x += step) {
                    double wx = x * ts.spacing - halfW;   // x exists here
                    double wz = z * ts.spacing - halfD;   // z exists here
                    double wy = H[z][x];
                    row.add(new Vector3(wx, wy, wz));
                }

                if (row.size() >= 2) {
                    TerrainPolylineGenerator g = new TerrainPolylineGenerator(wireSettings);
                    g.setPoints(row, minH, maxH);
                    paths.add(g);
                }
            }
        }

        if (ts.drawCols) {
            for (int x = 0; x < gx; x += step) {
                List<Vector3> col = new ArrayList<>((gz + step - 1) / step);

                for (int z = 0; z < gz; z += step) {
                    double wx = x * ts.spacing - halfW;
                    double wz = z * ts.spacing - halfD;
                    double wy = H[z][x];
                    col.add(new Vector3(wx, wy, wz));
                }

                if (col.size() >= 2) {
                    TerrainPolylineGenerator g = new TerrainPolylineGenerator(wireSettings);
                    g.setPoints(col, minH, maxH);
                    paths.add(g);
                }
            }
        }

        return paths;
    }

    private static void smoothHeightmap(double[][] H, int iterations) {
        int gz = H.length;
        int gx = H[0].length;
        double[][] tmp = new double[gz][gx];

        for (int it = 0; it < iterations; it++) {
            for (int z = 0; z < gz; z++) {
                for (int x = 0; x < gx; x++) {
                    double sum = 0.0;
                    int count = 0;

                    for (int dz = -1; dz <= 1; dz++) {
                        int zz = z + dz;
                        if (zz < 0 || zz >= gz) continue;

                        for (int dx = -1; dx <= 1; dx++) {
                            int xx = x + dx;
                            if (xx < 0 || xx >= gx) continue;

                            sum += H[zz][xx];
                            count++;
                        }
                    }

                    tmp[z][x] = sum / Math.max(1, count);
                }
            }

            for (int z = 0; z < gz; z++) {
                System.arraycopy(tmp[z], 0, H[z], 0, gx);
            }
        }
    }

    private static double clamp01(double v) {
        if (v < 0) return 0;
        if (v > 1) return 1;
        return v;
    }
}

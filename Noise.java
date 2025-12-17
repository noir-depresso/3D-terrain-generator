public final class Noise {
    private Noise() {}

    private static double fade(double t) {
        // smootherstep
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

    private static long hash3(int x, int y, int z, long seed) {
        long h = seed;
        h ^= x * 0x9E3779B97F4A7C15L;
        h ^= y * 0xC2B2AE3D27D4EB4FL;
        h ^= z * 0x165667B19E3779F9L;
        h ^= (h >>> 27);
        h *= 0x3C79AC492BA7B653L;
        h ^= (h >>> 33);
        h *= 0x1C69B3F74AC4AE35L;
        h ^= (h >>> 27);
        return h;
    }

    private static double hashToUnit(long h) {
        // [0,1)
        return ((h >>> 11) * (1.0 / (1L << 53)));
    }

    /** Value noise in [-1,1]. */
    public static double valueNoise3(double x, double y, double z, long seed) {
        int x0 = (int) Math.floor(x), y0 = (int) Math.floor(y), z0 = (int) Math.floor(z);
        int x1 = x0 + 1, y1 = y0 + 1, z1 = z0 + 1;

        double tx = x - x0, ty = y - y0, tz = z - z0;
        double u = fade(tx), v = fade(ty), w = fade(tz);

        double c000 = hashToUnit(hash3(x0,y0,z0,seed)) * 2 - 1;
        double c100 = hashToUnit(hash3(x1,y0,z0,seed)) * 2 - 1;
        double c010 = hashToUnit(hash3(x0,y1,z0,seed)) * 2 - 1;
        double c110 = hashToUnit(hash3(x1,y1,z0,seed)) * 2 - 1;
        double c001 = hashToUnit(hash3(x0,y0,z1,seed)) * 2 - 1;
        double c101 = hashToUnit(hash3(x1,y0,z1,seed)) * 2 - 1;
        double c011 = hashToUnit(hash3(x0,y1,z1,seed)) * 2 - 1;
        double c111 = hashToUnit(hash3(x1,y1,z1,seed)) * 2 - 1;

        double x00 = lerp(c000, c100, u);
        double x10 = lerp(c010, c110, u);
        double x01 = lerp(c001, c101, u);
        double x11 = lerp(c011, c111, u);

        double y0v = lerp(x00, x10, v);
        double y1v = lerp(x01, x11, v);

        return lerp(y0v, y1v, w);
    }

    /** Fractal Brownian motion in roughly [-1,1]. */
    public static double fbm3(Vector3 p, double baseFreq, int octaves, double lacunarity, double gain, long seed) {
        double amp = 1.0;
        double freq = baseFreq;
        double sum = 0.0;
        double norm = 0.0;

        for (int i = 0; i < octaves; i++) {
            double n = valueNoise3(p.x * freq, p.y * freq, p.z * freq, seed + i * 1337L);
            sum += n * amp;
            norm += amp;
            amp *= gain;
            freq *= lacunarity;
        }
        return sum / Math.max(1e-9, norm);
    }

    /** 2D fbm using 3D noise with y=0. */
    public static double fbm2(double x, double z, double baseFreq, int octaves, double lacunarity, double gain, long seed) {
        Vector3 p = new Vector3(x, 0, z);
        return fbm3(p, baseFreq, octaves, lacunarity, gain, seed);
    }
}

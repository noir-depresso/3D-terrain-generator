import java.util.Random;

public class PerlinNoise {
    private final int[] p = new int[512];

    public PerlinNoise(long seed) {
        int[] perm = new int[256];
        for (int i = 0; i < 256; i++) perm[i] = i;

        Random r = new Random(seed);
        for (int i = 255; i > 0; i--) {
            int j = r.nextInt(i + 1);
            int tmp = perm[i];
            perm[i] = perm[j];
            perm[j] = tmp;
        }

        for (int i = 0; i < 512; i++) p[i] = perm[i & 255];
    }

    private static double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private static double lerp(double a, double b, double t) {
        return a + t * (b - a);
    }

    private static double grad(int hash, double x, double y) {
        int h = hash & 3;
        return switch (h) {
            case 0 ->  x + y;
            case 1 -> -x + y;
            case 2 ->  x - y;
            default -> -x - y;
        };
    }

    // ~[-1, 1]
    public double noise(double x, double y) {
        int X = (int)Math.floor(x) & 255;
        int Y = (int)Math.floor(y) & 255;

        double xf = x - Math.floor(x);
        double yf = y - Math.floor(y);

        double u = fade(xf);
        double v = fade(yf);

        int aa = p[p[X] + Y];
        int ab = p[p[X] + Y + 1];
        int ba = p[p[X + 1] + Y];
        int bb = p[p[X + 1] + Y + 1];

        double x1 = lerp(grad(aa, xf,     yf),     grad(ba, xf - 1, yf),     u);
        double x2 = lerp(grad(ab, xf,     yf - 1), grad(bb, xf - 1, yf - 1), u);

        return lerp(x1, x2, v);
    }

    public double fbm(double x, double y, int octaves, double persistence, double lacunarity) {
        double amp = 1.0;
        double freq = 1.0;
        double sum = 0.0;
        double ampSum = 0.0;

        for (int i = 0; i < octaves; i++) {
            sum += noise(x * freq, y * freq) * amp;
            ampSum += amp;
            amp *= persistence;
            freq *= lacunarity;
        }
        return (ampSum < 1e-9) ? 0.0 : sum / ampSum;
    }
}

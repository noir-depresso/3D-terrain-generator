public final class CaveTerrainGenerator implements DensityField {
    public long seed = 12345;

    // Surface / ground
    public double surfaceBaseY = 40.0;
    public double surfaceAmp = 18.0;
    public double surfaceFreq = 0.015;

    // Cave carving
    public double caveFreq = 0.035;        // lower => bigger caves
    public double caveWarpFreq = 0.02;
    public double caveWarpAmp = 18.0;      // warp amount in world units
    public double caveThreshold = 0.28;    // higher => fewer caves

    // Only carve deep enough underground (prevents open air holes)
    public double minDepthBelowSurface = 8.0;

    @Override
    public double density(Vector3 p) {
        // 1) Surface height (2D fbm)
        double h = surfaceBaseY + surfaceAmp * Noise.fbm2(p.x, p.z, surfaceFreq, 5, 2.0, 0.5, seed);

        // rockDensity > 0 below surface, <=0 above surface
        double rockDensity = h - p.y;

        // If above surface: air
        if (rockDensity <= 0) return rockDensity;

        // 2) Warp the cave sampling point to avoid “static mush”
        Vector3 warp = new Vector3(
                Noise.fbm3(p, caveWarpFreq, 3, 2.0, 0.5, seed + 100) * caveWarpAmp,
                Noise.fbm3(p, caveWarpFreq, 3, 2.0, 0.5, seed + 200) * caveWarpAmp,
                Noise.fbm3(p, caveWarpFreq, 3, 2.0, 0.5, seed + 300) * caveWarpAmp
        );
        Vector3 pw = p.add(warp);

        // 3) Cave field
        double caveValue = Noise.fbm3(pw, caveFreq, 5, 2.0, 0.5, seed + 999);

        // Only carve if deep enough below surface
        if (p.y > h - minDepthBelowSurface) {
            return rockDensity; // keep solid near the top
        }

        // 4) Carve: caveCarve < 0 => air (inside cave)
        double caveCarve = caveThreshold - caveValue;

        // Combine: if caveCarve is negative, it wins (air)
        return Math.min(rockDensity, caveCarve);
    }
}

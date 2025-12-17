public class TerrainSettings {
    public int gridX = 80;
    public int gridZ = 80;
    public double spacing = 2.0;

    public long seed = 1337;

    // Noise
    public double noiseScale = 0.05;
    public double amplitude  = 35.0;
    public int octaves = 5;
    public double persistence = 0.5;
    public double lacunarity  = 2.0;

    // Shape
    public boolean ridgedMountains = true;
    public boolean valleys = false;
    public double sharpness = 2.2;

    // Wireframe
    public boolean drawRows = true;
    public boolean drawCols = true;

    // Stroke mapping
    public double strokeMin = 0.6;
    public double strokeMax = 2.5;
}

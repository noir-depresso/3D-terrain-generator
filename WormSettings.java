public class WormSettings {

    public Vector3 startPoint;
    public Vector3 endPoint;

    public boolean useRandomSegmentSizes;
    public double fixedSegmentSize;
    public double minSegmentSize;
    public double maxSegmentSize;

    public int numSteps;

    // Behaviour of the worm (you can expose these later in UI if you want)
    public double baseAttraction;
    public double attractionGrowth;
    public double wanderStrength;

    public WormSettings() {
        // Default values
        this.startPoint = new Vector3(0, 0, 0);
        this.endPoint = new Vector3(50, 20, 50);

        this.useRandomSegmentSizes = false;
        this.fixedSegmentSize = 1.0;
        this.minSegmentSize = fixedSegmentSize;
        this.maxSegmentSize = fixedSegmentSize;

        this.numSteps = 2000;

        this.baseAttraction = 0.05;
        this.attractionGrowth = 0.7;
        this.wanderStrength = 0.8;
    }

    public WormSettings copy() {
        WormSettings w = new WormSettings();
        w.startPoint = new Vector3(startPoint.x, startPoint.y, startPoint.z);
        w.endPoint = new Vector3(endPoint.x, endPoint.y, endPoint.z);
        w.useRandomSegmentSizes = useRandomSegmentSizes;
        w.fixedSegmentSize = fixedSegmentSize;
        w.minSegmentSize = minSegmentSize;
        w.maxSegmentSize = maxSegmentSize;
        w.numSteps = numSteps;
        w.baseAttraction = baseAttraction;
        w.attractionGrowth = attractionGrowth;
        w.wanderStrength = wanderStrength;
        return w;
    }
}

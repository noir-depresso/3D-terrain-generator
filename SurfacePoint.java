public final class SurfacePoint {
    public final Vector3 pos;
    public final Vector3 normal;  // unit
    public final double light;    // 0..1

    public SurfacePoint(Vector3 pos, Vector3 normal, double light) {
        this.pos = pos;
        this.normal = normal;
        this.light = light;
    }
}

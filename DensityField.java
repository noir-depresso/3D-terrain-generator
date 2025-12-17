public interface DensityField {
    /**
     * > 0 : solid
     * <=0 : air
     */
    double density(Vector3 p);
}

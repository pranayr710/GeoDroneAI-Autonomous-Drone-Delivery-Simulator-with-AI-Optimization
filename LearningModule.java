package ai;

/**
 * Base type for small learning components used in the simulator.
 * Originally this supported only 1D (x -> y) mappings, but it now
 * also provides optional multi–feature hooks that advanced models
 * may override.
 */
public abstract class LearningModule {

    /**
     * Train the model on historical data (x -> value).
     * Typical interpretation: x = time index.
     */
    public abstract void train(double[] x, double[] y);

    /**
     * Predict a future value for a given x (e.g., future time index).
     */
    public abstract double predict(double x);

    // ---------- Optional multi–feature variants ----------

    /**
     * Optional multi–feature training hook.
     * Default implementation simply throws if not overridden.
     */
    public void train(double[][] X, double[] y) {
        throw new UnsupportedOperationException("Multi-feature training not implemented for " + getClass().getSimpleName());
    }

    /**
     * Optional multi–feature prediction hook.
     * Default implementation delegates to the 1D predict using the first feature.
     */
    public double predict(double[] features) {
        if (features == null || features.length == 0) {
            throw new IllegalArgumentException("features must contain at least one value");
        }
        return predict(features[0]);
    }
}

package ai;

/**
 * DemandForecaster implements a weighted moving average /
 * exponential smoothing forecaster for ride demand.
 *
 * It keeps the latest smoothed level and exposes basic
 * error metrics (RMSE, MAE) against the training data.
 */
public class DemandForecaster extends LearningModule {

    private double alpha = 0.6; // smoothing factor (0..1)
    private double level = 0.0;
    private boolean trained = false;

    private double rmse = 0.0;
    private double mae  = 0.0;

    @Override
    public void train(double[] t, double[] demand) {
        if (demand == null || t == null || demand.length == 0 || demand.length != t.length) {
            throw new IllegalArgumentException("Invalid data for DemandForecaster.");
        }

        int n = demand.length;
        double[] forecast = new double[n];

        level = demand[0];
        forecast[0] = level;

        double sumSq = 0.0;
        double sumAbs = 0.0;

        for (int i = 1; i < n; i++) {
            // Exponential smoothing:
            //   F_t = alpha * Y_{t-1} + (1-alpha) * F_{t-1}
            level = alpha * demand[i - 1] + (1.0 - alpha) * level;
            forecast[i] = level;
            double err = forecast[i] - demand[i];
            sumSq  += err * err;
            sumAbs += Math.abs(err);
        }

        int denom = Math.max(1, n - 1);
        this.rmse = Math.sqrt(sumSq / denom);
        this.mae  = sumAbs / denom;
        this.trained = true;
    }

    @Override
    public double predict(double tFuture) {
        // Time index is ignored; this is a level-based forecast.
        if (!trained) return 0.0;
        return level;
    }

    /**
     * Compute a simple weighted moving average over the last w points,
     * where more recent observations get higher weights.
     */
    public double weightedMovingAverage(double[] history, int w) {
        if (history == null || history.length == 0 || w <= 0) {
            throw new IllegalArgumentException("Invalid history/width.");
        }
        int n = history.length;
        int start = Math.max(0, n - w);
        int count = n - start;

        double weightSum = 0.0;
        double acc = 0.0;
        int weight = 1;
        for (int i = start; i < n; i++) {
            acc += history[i] * weight;
            weightSum += weight;
            weight++;
        }
        return acc / weightSum;
    }

    public double[] forecastNext(int steps) {
        double[] out = new double[steps];
        for (int i = 0; i < steps; i++) {
            out[i] = level;
        }
        return out;
    }

    public void setAlpha(double alpha) {
        if (alpha <= 0.0 || alpha >= 1.0) {
            throw new IllegalArgumentException("alpha must be in (0,1)");
        }
        this.alpha = alpha;
    }

    public double getAlpha() { return alpha; }
    public double getRmse()  { return rmse; }
    public double getMae()   { return mae; }
    public boolean isTrained(){ return trained; }
}

package ai;

/**
 * Simple anomaly detector based on z-score.
 * Values that are more than k standard deviations away from mean are flagged.
 */
public class AnomalyDetector {

    private double mean;
    private double std;
    private boolean trained = false;

    public void train(double[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("Empty data");
        }
        int n = values.length;
        double sum = 0;
        for (double v : values) sum += v;
        mean = sum / n;

        double sq = 0;
        for (double v : values) {
            double d = v - mean;
            sq += d * d;
        }
        std = Math.sqrt(sq / n);
        trained = true;
    }

    public boolean isAnomaly(double v, double k) {
        if (!trained || std == 0) return false;
        double z = Math.abs(v - mean) / std;
        return z > k;
    }

    public double getMean() { return mean; }
    public double getStd() { return std; }
}

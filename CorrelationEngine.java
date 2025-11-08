package ai;

/**
 * Utility methods to compute covariance and Pearson correlation
 * between demand, supply, and surge series. The Dashboard uses
 * these values to show how tightly demand/supply are linked to
 * surge in each zone.
 */
public class CorrelationEngine {

    private static void checkLength(double[] a, double[] b) {
        if (a == null || b == null || a.length == 0 || a.length != b.length) {
            throw new IllegalArgumentException("Arrays must be non-null, same length, and non-empty.");
        }
    }

    public static double mean(double[] v) {
        if (v == null || v.length == 0) return 0.0;
        double s = 0.0;
        for (double x : v) s += x;
        return s / v.length;
    }

    public static double covariance(double[] x, double[] y) {
        checkLength(x, y);
        double mx = mean(x);
        double my = mean(y);
        double acc = 0.0;
        for (int i = 0; i < x.length; i++) {
            acc += (x[i] - mx) * (y[i] - my);
        }
        return acc / x.length;
    }

    public static double pearson(double[] x, double[] y) {
        checkLength(x, y);
        double mx = mean(x);
        double my = mean(y);
        double num = 0.0;
        double sx2 = 0.0;
        double sy2 = 0.0;
        for (int i = 0; i < x.length; i++) {
            double dx = x[i] - mx;
            double dy = y[i] - my;
            num += dx * dy;
            sx2 += dx * dx;
            sy2 += dy * dy;
        }
        double den = Math.sqrt(sx2 * sy2);
        if (den == 0.0) return 0.0;
        return num / den;
    }
}

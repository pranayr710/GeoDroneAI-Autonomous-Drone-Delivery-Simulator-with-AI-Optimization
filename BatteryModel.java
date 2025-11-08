package ai;

/**
 * Simple regression model that predicts battery usage
 * as a function of distance and elevation gain.
 *
 *  energy = w0 + w1 * distance + w2 * elevation
 */
public class BatteryModel extends LearningModule {

    private double w0, w1, w2;
    private boolean trained = false;

    @Override
    public void train(double[] distance, double[] energy) {
        if (distance == null || energy == null || distance.length == 0 || distance.length != energy.length) {
            throw new IllegalArgumentException("Invalid data for BatteryModel.");
        }
        int n = distance.length;
        // For simplicity treat elevation as zero in this overload.
        double[] elev = new double[n];
        trainMulti(distance, elev, energy);
    }

    public void train(double[] distance, double[] elevation, double[] energy) {
        if (distance == null || elevation == null || energy == null
                || distance.length == 0
                || distance.length != elevation.length
                || distance.length != energy.length) {
            throw new IllegalArgumentException("Invalid data for BatteryModel (multi).");
        }
        trainMulti(distance, elevation, energy);
    }

    private void trainMulti(double[] dist, double[] elev, double[] energy) {
        int n = dist.length;
        double sumX1 = 0, sumX2 = 0, sumY = 0;
        double sumX1X1 = 0, sumX2X2 = 0, sumX1X2 = 0;
        double sumX1Y = 0, sumX2Y = 0;

        for (int i = 0; i < n; i++) {
            double x1 = dist[i];
            double x2 = elev[i];
            double y  = energy[i];
            sumX1 += x1;
            sumX2 += x2;
            sumY  += y;
            sumX1X1 += x1 * x1;
            sumX2X2 += x2 * x2;
            sumX1X2 += x1 * x2;
            sumX1Y  += x1 * y;
            sumX2Y  += x2 * y;
        }

        double[][] A = {
                { n,      sumX1,   sumX2,   sumY   },
                { sumX1,  sumX1X1, sumX1X2, sumX1Y },
                { sumX2,  sumX1X2, sumX2X2, sumX2Y }
        };
        solve3x3(A);
        this.trained = true;
    }

    private void solve3x3(double[][] A) {
        // Very small system – use naive Gaussian elimination.
        int rows = 3;
        int cols = 4;
        for (int col = 0; col < 3; col++) {
            int pivotRow = col;
            for (int r = col + 1; r < rows; r++) {
                if (Math.abs(A[r][col]) > Math.abs(A[pivotRow][col])) {
                    pivotRow = r;
                }
            }
            double[] tmp = A[col];
            A[col] = A[pivotRow];
            A[pivotRow] = tmp;

            double pivot = A[col][col];
            if (Math.abs(pivot) < 1e-9) continue;
            for (int c = col; c < cols; c++) {
                A[col][c] /= pivot;
            }
            for (int r = 0; r < rows; r++) {
                if (r == col) continue;
                double factor = A[r][col];
                for (int c = col; c < cols; c++) {
                    A[r][c] -= factor * A[col][c];
                }
            }
        }
        this.w0 = A[0][3];
        this.w1 = A[1][3];
        this.w2 = A[2][3];
    }

    @Override
    public double predict(double distance) {
        if (!trained) return 0.0;
        // Elevation not known in this overload; treat as 0.
        return predict(distance, 0.0);
    }

    public double predict(double distance, double elevation) {
        if (!trained) return 0.0;
        return w0 + w1 * distance + w2 * elevation;
    }

    public boolean isTrained() { return trained; }
    public double getW0() { return w0; }
    public double getW1() { return w1; }
    public double getW2() { return w2; }
}

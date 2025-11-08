package ai;

import java.util.Arrays;

/**
 * Multi–feature surge predictor.
 *
 * Features supported:
 *  • Simple 1D polynomial regression on time index (degree 3) – for
 *    backward compatibility with the older design.
 *  • Multi–feature linear regression:
 *        x = [timeIndex, weatherIndex, demand, driverCount]
 *    trained via ordinary least squares (normal equation).
 *
 * The model also computes classic error metrics on the training data:
 * RMSE, MAE, and R², which are displayed in the Dashboard.
 */
public class SurgePredictor extends LearningModule {

    private double[] weights;       // includes intercept as weights[0]
    private boolean trained = false;
    private int featureCount = 1;   // not counting intercept

    private double rmse = 0.0;
    private double mae  = 0.0;
    private double r2   = 0.0;

    // ------------ Core fitting helpers ------------

    @Override
    public void train(double[] t, double[] y) {
        // Backward compatible: 1D polynomial regression on time index
        if (t == null || y == null || t.length == 0 || t.length != y.length) {
            throw new IllegalArgumentException("Invalid training data for SurgePredictor (1D).");
        }
        int n = t.length;
        int p = 4; // [1, t, t^2, t^3]
        double[][] X = new double[n][p];
        for (int i = 0; i < n; i++) {
            double ti  = t[i];
            double ti2 = ti * ti;
            double ti3 = ti2 * ti;
            X[i][0] = 1.0;
            X[i][1] = ti;
            X[i][2] = ti2;
            X[i][3] = ti3;
        }
        fitNormalEq(X, y);
    }

    /**
     * Train using multi–feature inputs.
     * Expected feature order (for the project):
     *   [timeIndex, weatherIndex, demand, driverCount]
     */
    @Override
    public void train(double[][] X, double[] y) {
        if (X == null || y == null || X.length == 0 || X.length != y.length) {
            throw new IllegalArgumentException("Invalid training data for SurgePredictor (multi-feature).");
        }
        int n = X.length;
        int p = X[0].length + 1; // + 1 for intercept
        double[][] design = new double[n][p];
        for (int i = 0; i < n; i++) {
            design[i][0] = 1.0;
            System.arraycopy(X[i], 0, design[i], 1, p - 1);
        }
        fitNormalEq(design, y);
    }

    private void fitNormalEq(double[][] X, double[] y) {
        int n = X.length;
        int p = X[0].length;
        this.featureCount = p - 1;

        // Compute XtX and XtY
        double[][] XtX = new double[p][p];
        double[]  XtY  = new double[p];
        for (int i = 0; i < n; i++) {
            double[] row = X[i];
            double yi = y[i];
            for (int j = 0; j < p; j++) {
                XtY[j] += row[j] * yi;
                double rj = row[j];
                for (int k = 0; k < p; k++) {
                    XtX[j][k] += rj * row[k];
                }
            }
        }

        // Build augmented matrix [XtX | XtY]
        double[][] A = new double[p][p + 1];
        for (int i = 0; i < p; i++) {
            System.arraycopy(XtX[i], 0, A[i], 0, p);
            A[i][p] = XtY[i];
        }

        this.weights = gaussianSolve(A);
        this.trained = true;

        // Compute error metrics on training set
        double sumSq = 0.0;
        double sumAbs = 0.0;
        double sumY = 0.0;
        double sumYSq = 0.0;

        for (int i = 0; i < n; i++) {
            double yHat = predictFromRow(X[i]);
            double err = yHat - y[i];
            sumSq  += err * err;
            sumAbs += Math.abs(err);
            sumY   += y[i];
            sumYSq += y[i] * y[i];
        }

        double mse = sumSq / n;
        this.rmse = Math.sqrt(mse);
        this.mae  = sumAbs / n;
        double meanY = sumY / n;
        double ssTot = sumYSq - n * meanY * meanY;
        this.r2 = (ssTot == 0.0) ? 1.0 : 1.0 - (sumSq / ssTot);
    }

    private static double[] gaussianSolve(double[][] A) {
        int n = A.length;
        int m = A[0].length; // n + 1

        for (int col = 0, row = 0; col < n && row < n; col++, row++) {
            // Pivot selection
            int sel = row;
            for (int i = row + 1; i < n; i++) {
                if (Math.abs(A[i][col]) > Math.abs(A[sel][col])) {
                    sel = i;
                }
            }
            if (Math.abs(A[sel][col]) < 1e-9) {
                continue;
            }

            // Swap rows
            double[] tmp = A[row];
            A[row] = A[sel];
            A[sel] = tmp;

            // Normalize pivot row
            double pivot = A[row][col];
            for (int j = col; j < m; j++) {
                A[row][j] /= pivot;
            }

            // Eliminate other rows
            for (int i = 0; i < n; i++) {
                if (i == row) continue;
                double factor = A[i][col];
                if (Math.abs(factor) < 1e-9) continue;
                for (int j = col; j < m; j++) {
                    A[i][j] -= factor * A[row][j];
                }
            }
        }

        double[] x = new double[n];
        for (int i = 0; i < n; i++) {
            x[i] = A[i][m - 1];
        }
        return x;
    }

    private double predictFromRow(double[] row) {
        if (!trained || weights == null) return 1.0;
        double sum = 0.0;
        int len = Math.min(weights.length, row.length);
        for (int i = 0; i < len; i++) {
            sum += weights[i] * row[i];
        }
        // Clamp surge minimum
        if (sum < 1.0) sum = 1.0;
        return sum;
    }

    // ---------- Prediction APIs ----------

    @Override
    public double predict(double t) {
        if (!trained) return 1.0;
        // Interpret as 1D polynomial on time index
        double t2 = t * t;
        double t3 = t2 * t;
        double[] row = new double[] { 1.0, t, t2, t3 };
        if (weights.length != row.length) {
            // Fallback to linear
            return Math.max(1.0, weights[0] + weights[1] * t);
        }
        return predictFromRow(row);
    }

    /**
     * Predict surge given full feature vector:
     *   timeIndex, weatherIndex, demand, driverCount
     */
    @Override
    public double predict(double[] features) {
        if (!trained || features == null) return 1.0;
        double[] row;
        if (weights.length == features.length + 1) {
            row = new double[weights.length];
            row[0] = 1.0;
            System.arraycopy(features, 0, row, 1, features.length);
        } else {
            // Fallback: just use time index as 1D
            double t = features[0];
            return predict(t);
        }
        return predictFromRow(row);
    }

    /**
     * Convenience wrapper for the project’s canonical feature ordering.
     */
    public double predict(double timeIndex,
                          double weatherIndex,
                          double demand,
                          double driverCount) {
        double[] f = new double[] { timeIndex, weatherIndex, demand, driverCount };
        return predict(f);
    }

    // ---------- Metrics & debug ----------

    public boolean isTrained()  { return trained; }
    public int getFeatureCount(){ return featureCount; }

    public double getRmse() { return rmse; }
    public double getMae()  { return mae; }
    public double getR2()   { return r2; }

    public double[] getWeights() {
        return weights == null ? null : Arrays.copyOf(weights, weights.length);
    }

    @Override
    public String toString() {
        return "SurgePredictor{" +
                "trained=" + trained +
                ", featureCount=" + featureCount +
                ", rmse=" + rmse +
                ", mae=" + mae +
                ", r2=" + r2 +
                ", weights=" + Arrays.toString(weights) +
                '}';
    }
}

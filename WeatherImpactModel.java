package ai;

/**
 * Simple linear model that approximates how weather affects either
 * surge or delay probability.
 *
 * Feature vector: [windSpeed, rainIndex, temperature]
 */
public class WeatherImpactModel extends LearningModule {

    private double[] w = new double[4]; // intercept + 3 features
    private boolean trained = false;

    @Override
    public void train(double[] x, double[] y) {
        // Not used in this project; call the multi-feature version instead.
        throw new UnsupportedOperationException("Use train(double[][], double[]) for WeatherImpactModel");
    }

    @Override
    public void train(double[][] X, double[] y) {
        if (X == null || y == null || X.length == 0 || X.length != y.length) {
            throw new IllegalArgumentException("Invalid data for WeatherImpactModel.");
        }
        int n = X.length;
        int p = 4;
        double[][] A = new double[p][p + 1];

        for (int i = 0; i < n; i++) {
            double[] row = X[i];
            double yi = y[i];
            double[] phi = new double[] { 1.0, row[0], row[1], row[2] };
            for (int j = 0; j < p; j++) {
                A[j][p] += phi[j] * yi;
                for (int k = 0; k < p; k++) {
                    A[j][k] += phi[j] * phi[k];
                }
            }
        }

        // Gaussian elimination for 4x4
        int rows = p;
        int cols = p + 1;
        for (int col = 0; col < p; col++) {
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
        for (int i = 0; i < p; i++) {
            w[i] = A[i][p];
        }
        trained = true;
    }

    @Override
    public double predict(double x) {
        // Interpret as "rain index only" for compatibility
        if (!trained) return 0.0;
        return w[0] + w[2] * x;
    }

    public double predict(double windSpeed, double rainIndex, double temperature) {
        if (!trained) return 0.0;
        return w[0] + w[1] * windSpeed + w[2] * rainIndex + w[3] * temperature;
    }
}

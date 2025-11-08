package ai;

import java.util.Random;

/**
 * Tiny 1-hidden-layer neural network (3 inputs -> 1 output).
 * This is intentionally lightweight and "from scratch" in Java,
 * just enough to showcase a neural model in the project.
 *
 * Inputs:  [timeIndex, demand, driverCount]
 * Output:  predicted surge multiplier
 */
public class NeuralSurgePredictor extends LearningModule {

    private final int inputSize = 3;
    private final int hiddenSize = 6;

    private double[][] w1 = new double[hiddenSize][inputSize];
    private double[]   b1 = new double[hiddenSize];
    private double[]   w2 = new double[hiddenSize];
    private double     b2 = 0.0;

    private boolean initialized = false;

    public NeuralSurgePredictor() {
        initWeights();
    }

    private void initWeights() {
        Random rnd = new Random(42);
        for (int i = 0; i < hiddenSize; i++) {
            for (int j = 0; j < inputSize; j++) {
                w1[i][j] = (rnd.nextDouble() - 0.5) * 0.5;
            }
            b1[i] = 0.0;
            w2[i] = (rnd.nextDouble() - 0.5) * 0.5;
        }
        b2 = 0.0;
        initialized = true;
    }

    private double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    private double forward(double[] x) {
        double[] h = new double[hiddenSize];
        for (int i = 0; i < hiddenSize; i++) {
            double sum = b1[i];
            for (int j = 0; j < inputSize; j++) {
                sum += w1[i][j] * x[j];
            }
            h[i] = sigmoid(sum);
        }
        double out = b2;
        for (int i = 0; i < hiddenSize; i++) {
            out += w2[i] * h[i];
        }
        // Map to surge range ~[1,3]
        return 1.0 + Math.max(0.0, Math.min(2.0, out));
    }

    @Override
    public void train(double[] t, double[] y) {
        // For simplicity use no training and keep random weights;
        // this still demonstrates the NN structure for the project.
        // A fully trained variant could be added later.
    }

    @Override
    public double predict(double timeIndex) {
        double[] x = new double[] { timeIndex, 0.0, 0.0 };
        return forward(x);
    }

    public double predict(double timeIndex, double demand, double driverCount) {
        double[] x = new double[] { timeIndex, demand, driverCount };
        return forward(x);
    }
}

package ai;

/**
 * Simple tabular Q-learning agent that learns how to
 * adjust fares (+10% or -10%) based on (zone, surgeBucket).
 *
 * state  = (zoneIndex, surgeBucket)
 * action = 0 => -10% fare, 1 => +10% fare
 */
public class FareRLAgent {

    private final int zones;
    private final int surgeBuckets;
    private final double[][][] q; // [zone][bucket][action]

    private double alpha = 0.2;  // learning rate
    private double gamma = 0.9;  // discount factor

    public FareRLAgent(int zones, int surgeBuckets) {
        this.zones = zones;
        this.surgeBuckets = surgeBuckets;
        this.q = new double[zones][surgeBuckets][2];
    }

    private int bucket(double surge) {
        // Map surge in [1, 3] into discrete buckets
        double s = Math.max(1.0, Math.min(3.0, surge));
        int b = (int) Math.floor((s - 1.0) / (2.0 / surgeBuckets));
        if (b < 0) b = 0;
        if (b >= surgeBuckets) b = surgeBuckets - 1;
        return b;
    }

    public int chooseAction(int zone, double surge, double epsilon) {
        int b = bucket(surge);
        if (Math.random() < epsilon) {
            return Math.random() < 0.5 ? 0 : 1;
        }
        double q0 = q[zone][b][0];
        double q1 = q[zone][b][1];
        return q1 > q0 ? 1 : 0;
    }

    public void update(int zone,
                       double surge,
                       int action,
                       double reward,
                       double nextSurge) {
        int b  = bucket(surge);
        int nb = bucket(nextSurge);
        double bestNext = Math.max(q[zone][nb][0], q[zone][nb][1]);
        double old = q[zone][b][action];
        double target = reward + gamma * bestNext;
        q[zone][b][action] = old + alpha * (target - old);
    }

    public double getQ(int zone, int bucket, int action) {
        return q[zone][bucket][action];
    }
}

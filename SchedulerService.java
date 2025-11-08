package services;

import models.DeliveryPoint;
import models.Drone;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * SchedulerService: assigns the best drone for a given delivery point.
 *
 * Original logic: nearest + highest battery.
 * Extended logic: we keep that heuristic but also incorporate
 * a simple "performance score" learned over time.
 *
 * The performance score is updated externally by calling
 * {@link #recordResult(String, boolean, double)} after each trip.
 */
public class SchedulerService {

    public static class Candidate {
        public final Drone drone;
        public final double score;
        public Candidate(Drone d, double score) {
            this.drone = d;
            this.score = score;
        }
    }

    private static class Perf {
        int successTrips;
        int failedTrips;
        double totalTime;
    }

    private final Map<String, Perf> perfMap = new HashMap<>();

    /**
     * Record the result of a completed trip so that future scheduling
     * decisions can favour better performing drones.
     */
    public void recordResult(String droneId, boolean success, double tripTimeMinutes) {
        if (droneId == null) return;
        Perf p = perfMap.computeIfAbsent(droneId, k -> new Perf());
        if (success) p.successTrips++;
        else p.failedTrips++;
        if (tripTimeMinutes > 0) p.totalTime += tripTimeMinutes;
    }

    private double performancePenalty(Drone d) {
        Perf p = perfMap.get(d.ID);
        if (p == null) return 0.0;
        int totalTrips = p.successTrips + p.failedTrips;
        if (totalTrips == 0) return 0.0;
        double failRate = (double) p.failedTrips / totalTrips;
        double avgTime  = p.totalTime / totalTrips;
        // Higher fail rate and longer time => larger penalty
        return failRate * 5.0 + avgTime * 0.05;
    }

    /**
     * Select the best drone (lowest score wins).
     * Score = distance + low-battery penalty + performance penalty.
     */
    public Drone selectBestDrone(List<Drone> drones, DeliveryPoint dp) {
        if (drones == null || drones.isEmpty() || dp == null) return null;

        PriorityQueue<Candidate> pq = new PriorityQueue<>(Comparator.comparingDouble(c -> c.score));

        for (Drone d : drones) {
            double dist = Math.hypot(d.X - dp.X, d.Y - dp.Y);
            double batteryPenalty = (100 - d.BAT_PCT) / 100.0 * 10.0;
            double perfPenalty = performancePenalty(d);
            double score = dist + batteryPenalty + perfPenalty;
            pq.add(new Candidate(d, score));
        }

        Candidate best = pq.peek();
        return (best == null) ? null : best.drone;
    }
}

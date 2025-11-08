package pricing;

public class FareEngine {

    private final double baseFare;
    private final double ratePerKm;
    private final double ratePerMin;

    public FareEngine(double baseFare, double ratePerKm, double ratePerMin) {
        this.baseFare = baseFare;
        this.ratePerKm = ratePerKm;
        this.ratePerMin = ratePerMin;
    }

    public double calculateFare(double distanceKm,
                                double timeMinutes,
                                double surgeMultiplier,
                                double fuelPrice) {

        double fuelFactor = fuelPrice / 100.0;
        double effectivePerKm = ratePerKm * fuelFactor;

        double core = baseFare + (distanceKm * effectivePerKm) + (timeMinutes * ratePerMin);
        return core * surgeMultiplier;
    }
}

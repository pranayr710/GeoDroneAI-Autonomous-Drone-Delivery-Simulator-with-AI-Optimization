package ai;

/**
 * Very small decision tree style risk classifier
 * for zones based on incident frequency.
 *
 * This is intentionally simple and interpretable – similar
 * in spirit to ID3 without full tree generation.
 */
public class ZoneRiskClassifier {

    public enum RiskLevel { LOW, MEDIUM, HIGH }

    public RiskLevel classify(int incidents, double unsafeRatio) {
        if (incidents == 0 && unsafeRatio < 0.05) {
            return RiskLevel.LOW;
        }
        if (incidents > 20 || unsafeRatio > 0.4) {
            return RiskLevel.HIGH;
        }
        return RiskLevel.MEDIUM;
    }
}

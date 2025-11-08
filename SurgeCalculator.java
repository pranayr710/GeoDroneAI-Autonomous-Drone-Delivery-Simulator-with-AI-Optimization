package pricing;

import java.util.HashMap;
import java.util.Map;

class ZoneStats {
    int activeRequests;
    int availableDrivers;
}

public class SurgeCalculator {

    private final Map<String, ZoneStats> zoneMap = new HashMap<>();

    private ZoneStats getOrCreateZone(String zoneId) {
        return zoneMap.computeIfAbsent(zoneId, z -> new ZoneStats());
    }

    public void updateZone(String zoneId, int activeRequests, int availableDrivers) {
        ZoneStats stats = getOrCreateZone(zoneId);
        stats.activeRequests = activeRequests;
        stats.availableDrivers = availableDrivers;
    }

    public double getSurge(String zoneId) {
        ZoneStats stats = zoneMap.get(zoneId);
        if (stats == null) {
            return 1.0;
        }
        int demand = stats.activeRequests;
        int supply = stats.availableDrivers;

        if (supply <= 0) {
            return 1.0 + demand;
        }
        double raw = (double) demand / supply;
        if (raw < 1.0) raw = 1.0;
        return raw;
    }
}

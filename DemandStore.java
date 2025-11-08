package services;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** Central demand registry keyed by DeliveryPoint ID, used by UI for heatmap. */
public final class DemandStore {
    private static final Map<String, Integer> DEMAND = new HashMap<>();

    private DemandStore() {}

    public static synchronized void add(String dpId) {
        DEMAND.put(dpId, DEMAND.getOrDefault(dpId, 0) + 1);
    }

    public static synchronized int get(String dpId) {
        return DEMAND.getOrDefault(dpId, 0);
    }

    public static synchronized Map<String,Integer> snapshot() {
        return Collections.unmodifiableMap(new HashMap<>(DEMAND));
    }

    public static synchronized void clear() {
        DEMAND.clear();
    }
}

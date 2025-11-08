package pricing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StationPriceSeries {
    private final String stationId;
    private final List<PriceEntry> entries;
    private SegmentTree segmentTree;

    public StationPriceSeries(String stationId) {
        this.stationId = stationId;
        this.entries = new ArrayList<>();
        this.segmentTree = new SegmentTree(0);
    }

    public String getStationId() {
        return stationId;
    }

    public List<PriceEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public SegmentTree getSegmentTree() {
        return segmentTree;
    }

    public void addEntry(PriceEntry entry) {
        entries.add(entry);
        rebuildSegmentTree();
    }

    private void rebuildSegmentTree() {
        int n = entries.size();
        double[] prices = new double[n];
        for (int i = 0; i < n; i++) {
            prices[i] = entries.get(i).getPrice();
        }
        segmentTree = new SegmentTree(prices);
    }

    public PriceEntry getLatestEntry() {
        if (entries.isEmpty()) return null;
        return entries.get(entries.size() - 1);
    }
}

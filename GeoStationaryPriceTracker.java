package pricing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeoStationaryPriceTracker {

    private final Map<String, StationPriceSeries> seriesMap;

    public GeoStationaryPriceTracker() {
        this.seriesMap = new HashMap<>();
    }

    private StationPriceSeries getOrCreateSeries(String stationId) {
        return seriesMap.computeIfAbsent(stationId, StationPriceSeries::new);
    }

    public void addPrice(String stationId, long timestamp, double price) {
        StationPriceSeries s = getOrCreateSeries(stationId);
        s.addEntry(new PriceEntry(timestamp, price));
    }

    public Double getMinPrice(String stationId, long fromTime, long toTime) {
        StationPriceSeries s = seriesMap.get(stationId);
        if (s == null) return null;

        List<PriceEntry> entries = s.getEntries();
        if (entries.isEmpty()) return null;

        int lIdx = TimeIndexMapper.lowerBound(entries, fromTime);
        int rIdx = TimeIndexMapper.upperBound(entries, toTime);

        if (lIdx > rIdx || lIdx >= entries.size() || rIdx < 0) return null;

        SegmentTree.Node res = s.getSegmentTree().query(lIdx, rIdx);
        return (res == null) ? null : res.min;
    }

    public Double getMaxPrice(String stationId, long fromTime, long toTime) {
        StationPriceSeries s = seriesMap.get(stationId);
        if (s == null) return null;

        List<PriceEntry> entries = s.getEntries();
        if (entries.isEmpty()) return null;

        int lIdx = TimeIndexMapper.lowerBound(entries, fromTime);
        int rIdx = TimeIndexMapper.upperBound(entries, toTime);

        if (lIdx > rIdx || lIdx >= entries.size() || rIdx < 0) return null;

        SegmentTree.Node res = s.getSegmentTree().query(lIdx, rIdx);
        return (res == null) ? null : res.max;
    }

    public Double getAvgPrice(String stationId, long fromTime, long toTime) {
        StationPriceSeries s = seriesMap.get(stationId);
        if (s == null) return null;

        List<PriceEntry> entries = s.getEntries();
        if (entries.isEmpty()) return null;

        int lIdx = TimeIndexMapper.lowerBound(entries, fromTime);
        int rIdx = TimeIndexMapper.upperBound(entries, toTime);

        if (lIdx > rIdx || lIdx >= entries.size() || rIdx < 0) return null;

        SegmentTree.Node res = s.getSegmentTree().query(lIdx, rIdx);
        if (res == null) return null;

        int count = rIdx - lIdx + 1;
        return res.sum / count;
    }

    public PriceEntry getLatestPriceEntry(String stationId) {
        StationPriceSeries s = seriesMap.get(stationId);
        if (s == null) return null;
        return s.getLatestEntry();
    }

    public void printAllPrices(String stationId) {
        StationPriceSeries s = seriesMap.get(stationId);
        if (s == null) {
            System.out.println("No data for station " + stationId);
            return;
        }
        System.out.println("Price history for station " + stationId + ":");
        for (PriceEntry e : s.getEntries()) {
            System.out.println("  time=" + e.getTimestamp() + ", price=" + e.getPrice());
        }
    }
}

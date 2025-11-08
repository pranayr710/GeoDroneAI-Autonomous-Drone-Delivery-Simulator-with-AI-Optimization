package pricing;

public class PriceEntry {
    private final long timestamp;  // epochDay
    private final double price;

    public PriceEntry(long timestamp, double price) {
        this.timestamp = timestamp;
        this.price = price;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "PriceEntry{" +
                "timestamp=" + timestamp +
                ", price=" + price +
                '}';
    }
}

package services;

public class WeatherService {
    private final SegmentTreeService SEG;

    public WeatherService(SegmentTreeService SEG) {
        this.SEG = SEG;
    }

    public void MS(int X1, int X2, int Y1, int Y2, int Z1, int Z2) {
        System.out.println("[WeatherService] Marking storm region unsafe: x[" + X1 + "-" + X2 + "] y[" + Y1 + "-" + Y2 + "] z[" + Z1 + "-" + Z2 + "]");
        SEG.MRU(X1, X2, Y1, Y2, Z1, Z2);
    }
}
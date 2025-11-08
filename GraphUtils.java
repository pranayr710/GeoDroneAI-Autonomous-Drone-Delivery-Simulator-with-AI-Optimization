package utils;

import java.util.Collection;

import models.ChargingStation;
import models.Warehouse;

public class GraphUtils {
    public static Warehouse FNW(Collection<Warehouse> WH, int X, int Y) {
        Warehouse BEST = null; double BESTD = Double.MAX_VALUE;
        for (Warehouse W : WH) {
            double D = Math.hypot(W.X - X, W.Y - Y);
            if (D < BESTD) { BESTD = D; BEST = W; }
        }
        return BEST;
    }

    public static ChargingStation FNC(Collection<ChargingStation> ST, int X, int Y) {
        ChargingStation BEST = null; double BESTD = Double.MAX_VALUE;
        for (ChargingStation C : ST) {
            double D = Math.hypot(C.X - X, C.Y - Y);
            if (D < BESTD) { BESTD = D; BEST = C; }
        }
        return BEST;
    }
}   
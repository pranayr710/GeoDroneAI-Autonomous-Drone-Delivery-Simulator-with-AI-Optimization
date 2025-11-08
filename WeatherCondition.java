package models;

public class WeatherCondition {
    public final String ID;
    public final String TYP;
    public final int X1, X2, Y1, Y2, Z1, Z2;

    public WeatherCondition(String ID, String TYP, int X1, int X2, int Y1, int Y2, int Z1, int Z2) {
        this.ID = ID; this.TYP = TYP;
        this.X1 = X1; this.X2 = X2; this.Y1 = Y1; this.Y2 = Y2; this.Z1 = Z1; this.Z2 = Z2;
    }

    public String toString() {
        return "Weather{" + ID + " " + TYP + " in x[" + X1 + "-" + X2 + "] y[" + Y1 + "-" + Y2 + "] z[" + Z1 + "-" + Z2 + "]}";
    }
}
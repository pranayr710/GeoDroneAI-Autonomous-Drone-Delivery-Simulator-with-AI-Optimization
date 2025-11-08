package models;

public class NoFlyZone {
    public final String ID;
    public final String DESC; // Description
    public final int X1, X2, Y1, Y2, Z1, Z2;
    public final boolean PERM;

    public NoFlyZone(String ID, String DESC, int X1, int X2, int Y1, int Y2, int Z1, int Z2) {
        this.ID = ID; this.DESC = DESC;
        this.X1 = X1; this.X2 = X2; this.Y1 = Y1; this.Y2 = Y2; this.Z1 = Z1; this.Z2 = Z2;
        this.PERM = true;
    }

  
    public String toString() {
        return "NoFlyZone{" + ID + " '" + DESC + "' x[" + X1 + "-" + X2 + "] y[" + Y1 + "-" + Y2 + "] z[" + Z1 + "-" + Z2 + "]}";
    }
}
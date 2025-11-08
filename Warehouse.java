package models;

public class Warehouse {
    public final String ID;
    public final String NM; // Name
    public final int X, Y, Z;
    public final int CAP;

    public Warehouse(String ID, String NM, int X, int Y, int Z, int CAP) {
        this.ID = ID; this.NM = NM; this.X = X; this.Y = Y; this.Z = Z; this.CAP = CAP;
    }

    
    public String toString() {
        return "Warehouse{" + ID + " '" + NM + "' loc=(" + X + "," + Y + "," + Z + ") cap=" + CAP + "}";
    }
}
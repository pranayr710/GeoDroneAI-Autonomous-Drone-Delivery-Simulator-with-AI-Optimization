package models;

public class DeliveryPoint {
    public final String ID;
    public final String ADDR;
    public final int X, Y, Z;

    public DeliveryPoint(String ID, String ADDR, int X, int Y, int Z) {
        this.ID = ID; this.ADDR = ADDR; this.X = X; this.Y = Y; this.Z = Z;
    }

 
    public String toString() {
        return "DeliveryPoint{" + ID + " '" + ADDR + "' loc=(" + X + "," + Y + "," + Z + ")}";
    }
}
package models;

public class ChargingStation {
    public final String ID;
    public final String NM;
    public final int X, Y, Z;
    public final int CAP; // number of simultaneous chargers

    public ChargingStation(String ID, String NM, int X, int Y, int Z, int CAP) 
    {
        this.ID = ID; this.NM = NM; this.X = X; this.Y = Y; this.Z = Z; this.CAP = CAP;
    }


    public String toString() {
        return "ChargingStation{" + ID + " '" + NM + "' loc=(" + X + "," + Y + "," + Z + ") cap=" + CAP + "}";
    }
}
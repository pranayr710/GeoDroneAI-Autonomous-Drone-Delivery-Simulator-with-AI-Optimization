package models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Drone {
    public final String ID;
    public int X, Y, Z;
    public int BAT_PCT; // Battery Percentage 
    public String STAT; // Battery Status
    public final List<int[]> RES_PATH = new ArrayList<>();

    public Drone(String ID, int X, int Y, int Z) {
        this.ID = (ID == null || ID.isEmpty()) ? "dr-" + UUID.randomUUID().toString().substring(0,8) : ID;
        this.X = X; this.Y = Y; this.Z = Z;
        this.BAT_PCT = 100;
        this.STAT = "IDLE";
    }

   
    public String toString() {
        return "Drone{" + ID + " pos=(" + X + "," + Y + "," + Z + ") bat=" + BAT_PCT + "% status=" + STAT + " reserved=" + RES_PATH.size() + "}";
    }
}
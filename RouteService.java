package services;

import models.*;

import java.util.*;

public class RouteService {
    private final DatabaseService DB;
    private SegmentTreeService SEG;  // keep private

    public SegmentTreeService GET_STS() 
    {
        return this.SEG;
    }

    public RouteService(DatabaseService DB, SegmentTreeService SEG) {
        this.DB = DB; this.SEG = SEG;
    }

    /**
     * Create a path (list of int[] waypoints) from (sx,sy,sz) to (ex,ey,ez).
     * Steps by max delta to produce enough waypoints.
     */
    public List<int[]> SP(int SX, int SY, int SZ, int EX, int EY, int EZ) {
        List<int[]> PATH = new ArrayList<>();
        int DX = EX - SX, DY = EY - SY, DZ = EZ - SZ;
        int STEPS = Math.max(Math.max(Math.abs(DX), Math.abs(DY)), Math.abs(DZ));
        if (STEPS == 0) { PATH.add(new int[]{SX,SY,SZ}); return PATH;}
        double STEP_X = DX / (double) STEPS, STEP_Y = DY / (double) STEPS, STEP_Z = DZ / (double) STEPS;
        double CX = SX, CY = SY, CZ = SZ;
        for (int I = 0; I <= STEPS; I++) {
            int IX = (int)Math.round(CX), IY = (int)Math.round(CY), IZ = (int)Math.round(CZ);
            PATH.add(new int[]{IX, IY, IZ});
            CX += STEP_X; CY += STEP_Y; CZ += STEP_Z;
        }
        // remove duplicates contiguous
        List<int[]> COMPACT = new ArrayList<>();
        int[] PREV = null;
        for (int[] P : PATH) {
            if (PREV == null || P[0]!=PREV[0] || P[1]!=PREV[1] || P[2]!=PREV[2]) COMPACT.add(P);
            PREV = P;
        }
        return COMPACT;
    }

    /**
     * High-level: plan route from drone to delivery point id, optionally suggest a charging station.
     */
    public List<int[]> PRTD(Drone DR, DeliveryPoint DP) {
        // straight path for now
        return SP(DR.X, DR.Y, DR.Z, DP.X, DP.Y, DP.Z);
    }

    /**
     * Find nearest charging station to a location (x,y).
     */
    public ChargingStation NC(int X, int Y) {
        double BEST = Double.MAX_VALUE; ChargingStation BEST_C = null;
        for (ChargingStation C : DB.LCS()) {
            double D = Math.hypot(C.X - X, C.Y - Y);
            if (D < BEST) { BEST = D; BEST_C = C; }
        }
        return BEST_C;
    }

    /**
     * Find nearest warehouse to a given delivery point (for assignment).
     */
    public Warehouse NWTD(DeliveryPoint DP) {
        double BEST = Double.MAX_VALUE; Warehouse BEST_W = null;
        for (Warehouse W : DB.LW()) {
            double D = Math.hypot(W.X - DP.X, W.Y - DP.Y);
            if (D < BEST) { BEST = D; BEST_W = W; }
        }
        return BEST_W;
    }

    /**
     * Validate a path is free/safe in the segment tree.
     */
    public boolean VPS(List<int[]> PATH) {
        for (int[] P : PATH) {
            if (!SEG.IS_CELL_FREE(P[0], P[1], P[2])) return false;
            if (SEG.IS_NOFLY_CELL(P[0], P[1], P[2]) || SEG.IS_UNSAFE_CELL(P[0], P[1], P[2])) return false;
        }
        return true;
    }

    /**
     * Reserve path for drone if safe. Returns true if reserved successfully.
     */
    // RPFD:- Reserve Path For Drone

    public boolean RPFD(String DRONE_ID, List<int[]> PATH) {
        // double-check and then insert reservations
        for (int[] P : PATH) {
            if (!SEG.IS_CELL_FREE(P[0], P[1], P[2])) return false;
            if (SEG.IS_NOFLY_CELL(P[0], P[1], P[2]) || SEG.IS_UNSAFE_CELL(P[0], P[1], P[2])) return false;
        }
        for (int[] P : PATH) SEG.INS_RES(DRONE_ID, P[0], P[1], P[2]);
        return true;
    }
    // FPFD:- Free Path For Drone
    
    public void FPFD(String DRONE_ID, List<int[]> PATH) {
        for (int[] P : PATH) SEG.REM_RES(DRONE_ID, P[0], P[1], P[2]);
    }
}
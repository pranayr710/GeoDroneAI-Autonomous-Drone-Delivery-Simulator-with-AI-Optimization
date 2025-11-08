package services;

import models.*;

import java.util.*;

public class DroneService {
    private final DatabaseService DB;
    private final SegmentTreeService SEG;
    private final RouteService RT;
    private final Map<String, Drone> DR = new LinkedHashMap<>();

    public DroneService(DatabaseService DB, SegmentTreeService SEG, RouteService RT) {
        this.DB = DB; this.SEG = SEG; this.RT = RT;
    }

    public Drone CDW(String ID_OR_NULL, String WH_ID) {
        Warehouse W = DB.GW(WH_ID);
        if (W == null) return null;
        Drone D = new Drone(ID_OR_NULL, W.X, W.Y, W.Z);
        DR.put(D.ID, D);
        System.out.println("[DroneService] Created " + D.ID + " at warehouse " + W.ID);
        return D;
    }

    public Drone GD(String ID) { return DR.get(ID); }

    /**
     * Plan a delivery for a given drone id to target delivery point id.
     * Steps:
     *  - fetch drone and dp
     *  - compute straight path (routeService)
     *  - validate path is safe (no-fly / unsafe / reserved)
     *  - reserve path in segment tree and attach to drone
     *
     * returns true on success.
     */
    public boolean PD(String DRONE_ID, String DP_ID) {
        Drone D = DR.get(DRONE_ID);
        if (D == null) { System.out.println("[DroneService] Unknown drone " + DRONE_ID); return false; }
        DeliveryPoint DP = DB.GDP(DP_ID);
        if (DP == null) { System.out.println("[DroneService] Unknown delivery point " + DP_ID); return false; }

        // compute path
        List<int[]> PATH = RT.PRTD(D, DP);

        // check for existing no-fly zones overlapping (quick check)
        for (int[] P : PATH) {
            if (SEG.IS_NOFLY_CELL(P[0], P[1], P[2])) {
                System.out.println("[Planner] Path intersects permanent no-fly. Aborting.");
                return false;
            }
            if (SEG.IS_UNSAFE_CELL(P[0], P[1], P[2])) {
                System.out.println("[Planner] Path intersects unsafe (weather). Aborting.");
                return false;
            }
        }

        // reserve
        boolean OK = RT.RPFD(D.ID, PATH);
        if (!OK) {
            System.out.println("[Planner] Reservation failed (conflict). Attempting simple alternatives...");
            // Try altitude shifts (+/- 20) up to 5 levels
            for (int K = 1; K <= 5 && !OK; K++) {
                int ALT_UP = DP.Z + K*20;
                int ALT_DN = Math.max(0, DP.Z - K*20);
                List<int[]> P_UP = RT.SP(D.X, D.Y, D.Z, DP.X, DP.Y, ALT_UP);
                if (RT.VPS(P_UP) && RT.RPFD(D.ID, P_UP)) { PATH = P_UP; OK = true; break; }
                List<int[]> P_DN = RT.SP(D.X, D.Y, D.Z, DP.X, DP.Y, ALT_DN);
                if (RT.VPS(P_DN) && RT.RPFD(D.ID, P_DN)) { PATH = P_DN; OK = true; break; }
            }
            // If still not ok, try nearest charging station detour
            if (!OK) {
                ChargingStation CS = RT.NC(DP.X, DP.Y);
                if (CS != null) {
                    List<int[]> TO_CS = RT.SP(D.X, D.Y, D.Z, CS.X, CS.Y, CS.Z);
                    List<int[]> CS_TO_DEST = RT.SP(CS.X, CS.Y, CS.Z, DP.X, DP.Y, DP.Z);
                    if (RT.VPS(TO_CS) && RT.VPS(CS_TO_DEST)) {
                        OK = RT.RPFD(D.ID, TO_CS);
                        if (OK) OK = RT.RPFD(D.ID, CS_TO_DEST);
                        if (OK) {
                            // merge paths
                            List<int[]> MERGED = new ArrayList<>(TO_CS);
                            MERGED.addAll(CS_TO_DEST);
                            PATH = MERGED;
                        } else {
                            // rollback
                            RT.FPFD(D.ID, TO_CS);
                            RT.FPFD(D.ID, CS_TO_DEST);
                            OK = false;
                        }
                    }
                }
            }
        }

        if (!OK) {
            System.out.println("[Planner] All strategies failed. Delivery not reserved.");
            return false;
        }

        // attach path to drone
        D.RES_PATH.clear();
        D.RES_PATH.addAll(PATH);
        D.STAT = "RESERVED";
        System.out.println("[Planner] Reserved path for drone " + D.ID + " pathLen=" + PATH.size());
        return true;
    }

    public void FD(String DRONE_ID) {
        Drone D = DR.get(DRONE_ID);
        if (D == null) return;
        // free reserved path
        RT.FPFD(D.ID, D.RES_PATH);
        D.RES_PATH.clear();
        D.STAT = "ARRIVED";
        System.out.println("[DroneService] Drone " + D.ID + " finished and freed cells.");
    }

    public Collection<Drone> LD() { return DR.values(); }
}
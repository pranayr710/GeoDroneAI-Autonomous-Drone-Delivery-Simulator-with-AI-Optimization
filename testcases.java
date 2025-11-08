package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import models.DeliveryPoint;
import models.Drone;
import models.Warehouse;
import models.ChargingStation;
import models.NoFlyZone;
import services.DatabaseService;
import services.DroneService;
import services.RouteService;
import services.SegmentTreeService;
import services.WeatherService;

public class testcases {

    // Methods to view entities
    public static void VW(DatabaseService DB) {
        System.out.println("\n--- Warehouses ---");
        for(Warehouse w : DB.LW()) {
            System.out.println(w);
        }
    }

    public static void VDP(DatabaseService DB) {
        System.out.println("\n--- Delivery Points ---");
        for(DeliveryPoint d : DB.LDP()) {
            System.out.println(d);
        }
    }

    public static void VCS(DatabaseService DB) {
        System.out.println("\n--- Charging Stations ---");
        for(ChargingStation c : DB.LCS()) {
            System.out.println(c);
        }
    }

    public static void VNFZ(DatabaseService DB) {
        System.out.println("\n--- No-Fly Zones ---");
        for(NoFlyZone nf : DB.LNFZ()) {
            System.out.println(nf);
        }
    }

    public static void RUN_ALL(DroneService DS, DatabaseService DB, RouteService RT, WeatherService WS) {
        System.out.println("[TestCases] Starting all test scenarios...");

        // View all entities first
        VW(DB);
        VDP(DB);
        VCS(DB);
        VNFZ(DB);

        // 1: Simple delivery from nearest warehouse to D1
        System.out.println("\n[Test 1] Simple delivery to D1");
        Warehouse w1 = DB.LW().iterator().next();
        Drone d1 = DS.CDW(null, w1.ID);
        DS.PD(d1.ID, "D1");

        // 2: Delivery that needs a charger (simulate low battery)
        System.out.println("\n[Test 2] Low battery delivery requiring charger");
        Drone d2 = DS.CDW(null, w1.ID);
        d2.BAT_PCT = 30; // low battery
        DS.PD(d2.ID, "D2");

        // 3: Delivery intersects no-fly NF2 -> expect failure or reroute
        System.out.println("\n[Test 3] Delivery crossing NF2 (military base)");
        Drone d3 = DS.CDW(null, w1.ID);
        boolean ok3 = DS.PD(d3.ID, "D4"); // D4 near NF area in preload
        System.out.println("[Test 3] plan result: " + ok3);

        // 4: Multiple drones create overlap -> collision detect
        System.out.println("\n[Test 4] Create overlapping reservations to trigger overlap detection");
        Drone d4a = DS.CDW(null, "W2");
        Drone d4b = DS.CDW(null, "W2");
        DS.PD(d4a.ID, "D7");
        DS.PD(d4b.ID, "D7"); // likely overlap

        // Access SegmentTreeService via getter
        SegmentTreeService ST = RT.GET_STS();
        List<String> overlaps = ST.FO();
        System.out.println("[Test 4] Overlaps: " + overlaps);

        // 5: Simulate storm and reroute
        System.out.println("\n[Test 5] Storm region and reroute affected drones");
        WS.MS(20, 60, 10, 80, 0, 300);
        Drone d5 = DS.CDW(null, "W1");
        boolean ok5 = DS.PD(d5.ID, "D5");
        System.out.println("[Test 5] plan after storm: " + ok5);

        // 6: Bulk reservations stress test
        System.out.println("\n[Test 6] Bulk reservations (attempt many drone creations & plans)");
        int successes = 0, attempts = 0;
        for (Warehouse w : DB.LW()) {
            Drone dd = DS.CDW(null, w.ID);
            List<DeliveryPoint> dps = new ArrayList<>(DB.LDP());
            DeliveryPoint dp = dps.get(new Random().nextInt(dps.size()));
            attempts++;
            if (DS.PD(dd.ID, dp.ID)) successes++;
        }
        System.out.println("[Test 6] bulk attempts=" + attempts + " successes=" + successes);

        // 7: Finish a few deliveries
        System.out.println("\n[Test 7] Finishing some deliveries (freeing cells)");
        for (Drone d : DS.LD()) {
            DS.FD(d.ID);
            break; // finish first only for demo
        }

        System.out.println("\n[TestCases] Completed scenarios.");
    }
}
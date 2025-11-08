package services;

import models.*;

import java.util.*;

public class DatabaseService {
    private final Map<String, Warehouse> WH = new LinkedHashMap<>();
    private final Map<String, DeliveryPoint> DP = new LinkedHashMap<>();
    private final Map<String, ChargingStation> CS = new LinkedHashMap<>();
    private final Map<String, NoFlyZone> NFZ = new LinkedHashMap<>();

    public void AW(Warehouse W) 
    { 
        WH.put(W.ID, W); 
    }
    public void ADP(DeliveryPoint D) 
    { 
        DP.put(D.ID, D); 
    }
    public void ACS(ChargingStation C) 
    { 
        CS.put(C.ID, C); 
    }
    public void ANFZ(NoFlyZone N) 
    { 
        NFZ.put(N.ID, N); 
    }

    public Warehouse GW(String ID) 
    { 
        return WH.get(ID); 
    }
    public DeliveryPoint GDP(String ID) 
    { 
        return DP.get(ID); 
    }
    public ChargingStation GCS(String ID) 
    { 
        return CS.get(ID); 
    }
    public NoFlyZone GNFZ(String ID) 
    { 
        return NFZ.get(ID); 
    }

    public Collection<Warehouse> LW() 
    { 
        return WH.values(); 
    }
    public Collection<DeliveryPoint> LDP() 
    { 
        return DP.values(); 
    }
    public Collection<ChargingStation> LCS() 
    { 
        return CS.values(); 
    }
    public Collection<NoFlyZone> LNFZ() 
    { 
        return NFZ.values(); 
    }

    /**
     * Preload a large realistic dataset (≥15 per group) with varied coordinates.
     */
    public void PSD() {
        // Warehouses (15)
        AW(new Warehouse("W1", "Central Hub", 10, 10, 50, 20));
        AW(new Warehouse("W2", "North Hub", 80, 20, 60, 18));
        AW(new Warehouse("W3", "South Hub", 20, 400, 55, 16));
        AW(new Warehouse("W4", "East Hub", 420, 50, 70, 22));
        AW(new Warehouse("W5", "West Hub", 50, 300, 65, 14));
        AW(new Warehouse("W6", "Harbor Hub", 480, 450, 75, 25));
        AW(new Warehouse("W7", "Airport Hub", 250, 10, 80, 30));
        AW(new Warehouse("W8", "Inland Hub", 200, 200, 60, 12));
        AW(new Warehouse("W9", "Industrial Hub", 150, 420, 55, 10));
        AW(new Warehouse("W10", "Campus Hub", 300, 150, 45, 16));
        AW(new Warehouse("W11", "Valley Hub", 60, 80, 50, 14));
        AW(new Warehouse("W12", "Plains Hub", 340, 300, 60, 20));
        AW(new Warehouse("W13", "Ridge Hub", 95, 95, 48, 12));
        AW(new Warehouse("W14", "Suburban Hub", 120, 60, 55, 12));
        AW(new Warehouse("W15", "Coastal Hub", 490, 30, 70, 26));

        // Delivery Points (15)
        ADP(new DeliveryPoint("D1", "123 Main St", 150, 120, 80));
        ADP(new DeliveryPoint("D2", "45 Park Ave", 160, 30, 100));
        ADP(new DeliveryPoint("D3", "78 Lakeside", 320, 260, 90));
        ADP(new DeliveryPoint("D4", "9 Oak Road", 30, 320, 40));
        ADP(new DeliveryPoint("D5", "221B Baker", 420, 40, 75));
        ADP(new DeliveryPoint("D6", "11 River Dr", 480, 480, 80));
        ADP(new DeliveryPoint("D7", "88 Hilltop", 70, 160, 65));
        ADP(new DeliveryPoint("D8", "12 Sunrise Blvd", 245, 210, 55));
        ADP(new DeliveryPoint("D9", "307 Elm St", 360, 90, 70));
        ADP(new DeliveryPoint("D10", "505 Market", 50, 450, 60));
        ADP(new DeliveryPoint("D11", "7 Pine Lane", 220, 370, 68));
        ADP(new DeliveryPoint("D12", "88 Sunset Ave", 410, 220, 72));
        ADP(new DeliveryPoint("D13", "2 Harbor Way", 470, 60, 75));
        ADP(new DeliveryPoint("D14", "300 Oakwood", 180, 80, 60));
        ADP(new DeliveryPoint("D15", "991 Riverfront Rd", 30, 30, 55));

        // Charging Stations (15)
        ACS(new ChargingStation("C1", "Alpha", 80, 60, 40, 6));
        ACS(new ChargingStation("C2", "Bravo", 40, 160, 40, 6));
        ACS(new ChargingStation("C3", "Charlie", 200, 200, 50, 8));
        ACS(new ChargingStation("C4", "Delta", 300, 50, 45, 5));
        ACS(new ChargingStation("C5", "Echo", 420, 300, 60, 10));
        ACS(new ChargingStation("C6", "Foxtrot", 260, 420, 55, 7));
        ACS(new ChargingStation("C7", "Golf", 150, 150, 50, 6));
        ACS(new ChargingStation("C8", "Hotel", 350, 120, 55, 5));
        ACS(new ChargingStation("C9", "India", 30, 210, 40, 4));
        ACS(new ChargingStation("C10", "Juliet", 470, 430, 60, 8));
        ACS(new ChargingStation("C11", "Kilo", 100, 300, 45, 6));
        ACS(new ChargingStation("C12", "Lima", 210, 50, 50, 6));
        ACS(new ChargingStation("C13", "Mike", 280, 270, 55, 6));
        ACS(new ChargingStation("C14", "November", 390, 200, 60, 7));
        ACS(new ChargingStation("C15", "Oscar", 10, 400, 45, 5));

        // No-Fly zones (several)
        ANFZ(new NoFlyZone("NF1", "Airport approach", 230, 270, 0, 60, 0, 400));
        ANFZ(new NoFlyZone("NF2", "Military base", 0, 40, 0, 80, 0, 1000));
        ANFZ(new NoFlyZone("NF3", "Government complex", 440, 480, 20, 80, 0, 500));
        ANFZ(new NoFlyZone("NF4", "Protected Park", 100, 140, 100, 140, 0, 200));
        ANFZ(new NoFlyZone("NF5", "Stadium event", 300, 340, 140, 180, 0, 400));
    }
}
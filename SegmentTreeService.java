package services;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SegmentTreeService {
    private final int XMIN, XMAX, YMIN, YMAX, ZMIN, ZMAX;

    // cell -> set of droneIds reserved
    private final Map<String, Set<String>> RES = new ConcurrentHashMap<>();

    // set of unsafe cells (marked by weather)
    private final Set<String> UNSAFE = Collections.synchronizedSet(new HashSet<>());

    // set of no-fly cells (permanent)
    private final Set<String> NOFLY = Collections.synchronizedSet(new HashSet<>());

    public SegmentTreeService(int XMIN, int XMAX, int YMIN, int YMAX, int ZMIN, int ZMAX) {
        this.XMIN = XMIN; this.XMAX = XMAX; this.YMIN = YMIN; this.YMAX = YMAX; this.ZMIN = ZMIN; this.ZMAX = ZMAX;
    }

    private String KEY(int X, int Y, int Z) { return X + ":" + Y + ":" + Z; }

    private boolean IN_BOUNDS(int X,int Y, int Z) {
        return !(X < XMIN || X > XMAX || Y < YMIN || Y > YMAX || Z < ZMIN || Z > ZMAX);
    }

    public synchronized boolean INS_RES(String DRONE_ID, int X, int Y, int Z) {
        if (!IN_BOUNDS(X,Y,Z)) return false;
        String K = KEY(X,Y,Z);
        if (NOFLY.contains(K) || UNSAFE.contains(K)) return false;
        Set<String> S = RES.computeIfAbsent(K, _ -> Collections.synchronizedSet(new HashSet<>()));
        S.add(DRONE_ID);
        return true;
    }

    public synchronized void REM_RES(String DRONE_ID, int X, int Y, int Z) {
        String K = KEY(X,Y,Z);
        Set<String> S = RES.get(K);
        if (S != null) {
            S.remove(DRONE_ID);
            if (S.isEmpty()) RES.remove(K);
        }
    }

    public synchronized boolean IS_CELL_FREE(int X, int Y, int Z) {
        String K = KEY(X,Y,Z);
        if (NOFLY.contains(K) || UNSAFE.contains(K)) return false;
        Set<String> S = RES.get(K);
        return S == null || S.isEmpty();
    }

    public synchronized void MRU(int X1, int X2, int Y1, int Y2, int Z1, int Z2) {
        for (int X = Math.max(XMIN, X1); X <= Math.min(XMAX, X2); X++) {
            for (int Y = Math.max(YMIN, Y1); Y <= Math.min(YMAX, Y2); Y++) {
                for (int Z = Math.max(ZMIN, Z1); Z <= Math.min(ZMAX, Z2); Z++) {
                    UNSAFE.add(KEY(X,Y,Z));
                }
            }
        }
    }

    public synchronized void MRNF(int X1, int X2, int Y1, int Y2, int Z1, int Z2) 
    {
        for (int X = Math.max(XMIN, X1); X <= Math.min(XMAX, X2); X++) {
            for (int Y = Math.max(YMIN, Y1); Y <= Math.min(YMAX, Y2); Y++) {
                for (int Z = Math.max(ZMIN, Z1); Z <= Math.min(ZMAX, Z2); Z++) {
                    NOFLY.add(KEY(X,Y,Z));
                }
            }
        }
    }

    public synchronized int CNT_DR_IN_REG(int X1, int X2, int Y1, int Y2, int Z1, int Z2) {
        Set<String> SEEN = new HashSet<>();
        for (int X = Math.max(XMIN, X1); X <= Math.min(XMAX, X2); X++) {
            for (int Y = Math.max(YMIN, Y1); Y <= Math.min(YMAX, Y2); Y++) {
                for (int Z = Math.max(ZMIN, Z1); Z <= Math.min(ZMAX, Z2); Z++) {
                    String K = KEY(X,Y,Z);
                    Set<String> S = RES.get(K);
                    if (S != null) SEEN.addAll(S);
                }
            }
        }
        return SEEN.size();
    }

    public synchronized int CNT_UNSAFE_IN_REG(int X1, int X2, int Y1, int Y2, int Z1, int Z2) {
        int C = 0;
        for (int X = Math.max(XMIN, X1); X <= Math.min(XMAX, X2); X++) {
            for (int Y = Math.max(YMIN, Y1); Y <= Math.min(YMAX, Y2); Y++) {
                for (int Z = Math.max(ZMIN, Z1); Z <= Math.min(ZMAX, Z2); Z++) {
                    if (UNSAFE.contains(KEY(X,Y,Z))) C++;
                }
            }
        }
        return C;
    }

    public synchronized List<String> FO() {
        List<String> OUT = new ArrayList<>();
        for (Map.Entry<String, Set<String>> E : RES.entrySet()) {
            if (E.getValue().size() > 1) {
                OUT.add(E.getKey() + " -> " + E.getValue().toString());
            }
        }
        return OUT;
    }

    public synchronized boolean IS_UNSAFE_CELL(int X, int Y, int Z) {
        return UNSAFE.contains(KEY(X,Y,Z));
    }

    public synchronized boolean IS_NOFLY_CELL(int X, int Y, int Z) {
        return NOFLY.contains(KEY(X,Y,Z));
    }

    public synchronized void CLR_UNSAFE() { UNSAFE.clear(); }
}
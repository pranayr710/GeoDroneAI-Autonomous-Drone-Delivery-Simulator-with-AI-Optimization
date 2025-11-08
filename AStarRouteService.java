package services;

import models.*;
import java.util.*;

/**
 * A* variant of RouteService for more advanced pathfinding.
 * Keeps existing RouteService intact; this is used for experimentation and demos.
 */
public class AStarRouteService {

    private final DatabaseService DB;
    private final SegmentTreeService SEG;

    public AStarRouteService(DatabaseService DB, SegmentTreeService SEG) {
        this.DB = DB;
        this.SEG = SEG;
    }

    /**
     * Node in A* search: (x,y,z) with g (distance so far) and f = g + heuristic.
     */
    private static class Node implements Comparable<Node> {
        int x, y, z;
        double g, f;

        Node(int x, int y, int z, double g, double f) {
            this.x = x; this.y = y; this.z = z;
            this.g = g; this.f = f;
        }

        @Override
        public int compareTo(Node o) {
            return Double.compare(this.f, o.f);
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Node)) return false;
            Node n = (Node) obj;
            return n.x == x && n.y == y && n.z == z;
        }
    }

    private double heuristic(int x, int y, int z, int tx, int ty, int tz) {
        return Math.sqrt((x - tx) * (x - tx) + (y - ty) * (y - ty) + (z - tz) * (z - tz));
    }

    /**
     * A* search in 3D grid, respecting unsafe / no-fly / occupied cells in SegmentTreeService.
     */
    public List<int[]> findPath(int sx, int sy, int sz, int tx, int ty, int tz) {
        PriorityQueue<Node> open = new PriorityQueue<>();
        Map<String, Double> bestG = new HashMap<>();
        Map<String, int[]> parent = new HashMap<>();

        Node start = new Node(sx, sy, sz, 0, heuristic(sx, sy, sz, tx, ty, tz));
        open.add(start);
        bestG.put(key(sx, sy, sz), 0.0);

        int[][] DIRS = {
                {1, 0, 0}, {-1, 0, 0},
                {0, 1, 0}, {0, -1, 0},
                {0, 0, 1}, {0, 0, -1}
        };

        while (!open.isEmpty()) {
            Node cur = open.poll();
            if (cur.x == tx && cur.y == ty && cur.z == tz) {
                return reconstructPath(parent, tx, ty, tz, sx, sy, sz);
            }

            for (int[] d : DIRS) {
                int nx = cur.x + d[0];
                int ny = cur.y + d[1];
                int nz = cur.z + d[2];

                if (!(nx >= 0 && nx <= 500 && ny >= 0 && ny <= 500 && nz >= 0 && nz <= 1000)) continue;
                if (!SEG.IS_CELL_FREE(nx, ny, nz)) continue;
                if (SEG.IS_NOFLY_CELL(nx, ny, nz) || SEG.IS_UNSAFE_CELL(nx, ny, nz)) continue;

                double ng = cur.g + 1; // uniform cost for neighbors
                String k = key(nx, ny, nz);
                if (!bestG.containsKey(k) || ng < bestG.get(k)) {
                    bestG.put(k, ng);
                    double nf = ng + heuristic(nx, ny, nz, tx, ty, tz);
                    open.add(new Node(nx, ny, nz, ng, nf));
                    parent.put(k, new int[]{cur.x, cur.y, cur.z});
                }
            }
        }
        return Collections.emptyList();
    }

    private String key(int x, int y, int z) {
        return x + ":" + y + ":" + z;
    }

    private List<int[]> reconstructPath(Map<String, int[]> parent,
                                        int tx, int ty, int tz,
                                        int sx, int sy, int sz) {
        List<int[]> path = new ArrayList<>();
        int cx = tx, cy = ty, cz = tz;
        while (!(cx == sx && cy == sy && cz == sz)) {
            path.add(new int[]{cx, cy, cz});
            int[] par = parent.get(key(cx, cy, cz));
            if (par == null) break;
            cx = par[0]; cy = par[1]; cz = par[2];
        }
        path.add(new int[]{sx, sy, sz});
        Collections.reverse(path);
        return path;
    }
}

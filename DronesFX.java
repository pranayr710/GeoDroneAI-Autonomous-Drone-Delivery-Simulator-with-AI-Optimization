package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import models.*;
import services.*;

/**
 * A Risk-Aware Geospatial Drone Routing System — Drones Visualizer
 * Uses SegmentTreeService regional queries to show drone-density heat.
 * NOTE: This class only adds functionality and does not rename existing variables.
 */
public class DronesFX extends Application {

    private static final int W = 900;
    private static final int H = 900;
    private static final int WORLD = 500; // 0..500 grid
    private static final int PAD = 40;
    private static final int TILE = 25;   // heat tile size

    private DatabaseService DB;
    private SegmentTreeService SEG;
    private RouteService RT;
    private DroneService DS;
    private WeatherService WS;

    private Canvas canvas;
    private final Random RNG = new Random();

    private double sx(int x){ return PAD + (x / (double)WORLD) * (W - 2*PAD); }
    private double sy(int y){ return PAD + (y / (double)WORLD) * (H - 2*PAD); }

    @Override
    public void start(Stage stage) {
        DB = new DatabaseService();
        SEG = new SegmentTreeService(0, 500, 0, 500, 0, 1000);
        RT = new RouteService(DB, SEG);
        DS = new DroneService(DB, SEG, RT);
        WS = new WeatherService(SEG);
        DB.PSD();

        BorderPane root = new BorderPane();
        canvas = new Canvas(W, H);
        root.setCenter(canvas);

        Button addStorm = new Button("Add Storm");
        Button clearUnsafe = new Button("Clear Unsafe");
        Button spawnDrones = new Button("Spawn Drones");
        Button randomDeliveries = new Button("Random Deliveries");
        CheckBox showHeat = new CheckBox("Show Drone Heat"); showHeat.setSelected(true);
        CheckBox showUnsafe = new CheckBox("Show Unsafe"); showUnsafe.setSelected(true);
        CheckBox showNoFly = new CheckBox("Show No-Fly"); showNoFly.setSelected(true);
        Label title = new Label("A Risk-Aware Geospatial Drone Routing System — Visualizer");

        addStorm.setOnAction(e -> {
            int x1 = RNG.nextInt(350), y1 = RNG.nextInt(350);
            int x2 = x1 + 80 + RNG.nextInt(120);
            int y2 = y1 + 80 + RNG.nextInt(120);
            SEG.MRU(x1, x2, y1, y2, 0, 80);
            draw(showHeat.isSelected(), showUnsafe.isSelected(), showNoFly.isSelected());
        });

        clearUnsafe.setOnAction(e -> { SEG.CLR_UNSAFE(); draw(showHeat.isSelected(), showUnsafe.isSelected(), showNoFly.isSelected()); });

        spawnDrones.setOnAction(e -> {
            for (Warehouse w : DB.LW()) {
                if (RNG.nextBoolean()) {
                    Drone d = DS.CDW(null, w.ID);
                    List<DeliveryPoint> dps = new ArrayList<>(DB.LDP());
                    if (!dps.isEmpty() && RNG.nextBoolean()) {
                        DeliveryPoint dp = dps.get(RNG.nextInt(dps.size()));
                        DS.PD(d.ID, dp.ID);
                    }
                }
            }
            draw(showHeat.isSelected(), showUnsafe.isSelected(), showNoFly.isSelected());
        });

        randomDeliveries.setOnAction(e -> {
            List<DeliveryPoint> dps = new ArrayList<>(DB.LDP());
            if (dps.isEmpty()) return;
            for (int i=0;i<8;i++){
                DeliveryPoint dp = dps.get(RNG.nextInt(dps.size()));
                Warehouse best = null; double bestD = Double.MAX_VALUE;
                for (Warehouse w : DB.LW()) {
                    double dist = services.PricingService.distanceKm(w, dp);
                    if (dist < bestD) { bestD = dist; best = w; }
                }
                if (best != null) {
                    Drone d = DS.CDW(null, best.ID);
                    DS.PD(d.ID, dp.ID);
                }
            }
            draw(showHeat.isSelected(), showUnsafe.isSelected(), showNoFly.isSelected());
        });

        ToolBar bar = new ToolBar(addStorm, clearUnsafe, spawnDrones, randomDeliveries, showHeat, showUnsafe, showNoFly, title);
        root.setTop(bar);

        draw(true, true, true);

        stage.setTitle("Drone Visualizer");
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void draw(boolean heat, boolean unsafe, boolean nofly) {
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setFill(Color.WHITE);
        g.fillRect(0,0,W,H);

        // grid
        g.setStroke(Color.LIGHTGRAY);
        for (int i=0;i<=10;i++){
            double xx = PAD + i*(W-2*PAD)/10.0;
            double yy = PAD + i*(H-2*PAD)/10.0;
            g.strokeLine(PAD, yy, W-PAD, yy);
            g.strokeLine(xx, PAD, xx, H-PAD);
        }

        // no-fly overlay (red)
        if (nofly) {
            g.setGlobalAlpha(0.20);
            g.setFill(Color.RED);
            for (NoFlyZone nf : DB.LNFZ()) {
                double x = sx(nf.X1), y = sy(nf.Y1);
                double x2 = sx(nf.X2), y2 = sy(nf.Y2);
                g.fillRect(Math.min(x,x2), Math.min(y,y2), Math.abs(x2-x), Math.abs(y2-y));
            }
            g.setGlobalAlpha(1.0);
        }

        // unsafe dots (orange)
        if (unsafe) {
            g.setFill(Color.ORANGE);
            for (int x=0;x<=WORLD;x+=25){
                for (int y=0;y<=WORLD;y+=25){
                    if (SEG.IS_UNSAFE_CELL(x, y, 50)) {
                        g.fillOval(sx(x)-2, sy(y)-2, 4, 4);
                    }
                }
            }
        }

        // density heat via SegmentTreeService
        if (heat) {
            int maxCnt = 1;
            int step = TILE;
            int[][] grid = new int[WORLD/step + 1][WORLD/step + 1];
            for (int gx=0, x=0; x<=WORLD; gx++, x+=step){
                for (int gy=0, y=0; y<=WORLD; gy++, y+=step){
                    int cnt = SEG.CNT_DR_IN_REG(x, Math.min(x+step, WORLD),
                                                y, Math.min(y+step, WORLD),
                                                0, 1000);
                    grid[gx][gy] = cnt;
                    if (cnt > maxCnt) maxCnt = cnt;
                }
            }
            for (int gx=0, x=0; x<=WORLD; gx++, x+=step){
                for (int gy=0, y=0; y<=WORLD; gy++, y+=step){
                    int cnt = grid[gx][gy];
                    if (cnt == 0) continue;
                    double alpha = Math.min(0.8, 0.15 + 0.65 * (cnt / (double)maxCnt));
                    g.setGlobalAlpha(alpha);
                    g.setFill(Color.DEEPSKYBLUE);
                    double cx = sx(x), cy = sy(y);
                    double cw = sx(Math.min(x+step, WORLD)) - cx;
                    double ch = sy(Math.min(y+step, WORLD)) - cy;
                    g.fillRect(cx, cy, cw, ch);
                }
            }
            g.setGlobalAlpha(1.0);
        }

        // Warehouses (blue), chargers (yellow), delivery points (green), drones (black)
        g.setFill(Color.DEEPSKYBLUE);
        for (Warehouse w : DB.LW()) g.fillOval(sx(w.X)-5, sy(w.Y)-5, 10, 10);

        g.setFill(Color.GOLD);
        for (ChargingStation c : DB.LCS()) g.fillRect(sx(c.X)-4, sy(c.Y)-4, 8, 8);

        g.setFill(Color.LIMEGREEN);
        for (DeliveryPoint d : DB.LDP()) g.fillOval(sx(d.X)-5, sy(d.Y)-5, 10, 10);

        g.setFill(Color.BLACK);
        for (Drone dr : DS.LD()) g.fillOval(sx(dr.X)-3, sy(dr.Y)-3, 6, 6);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

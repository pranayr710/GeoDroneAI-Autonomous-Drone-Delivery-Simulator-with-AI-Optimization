package main;

import ai.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pricing.GeoStationaryPriceTracker;

/**
 * Upgraded JavaFX dashboard that visualises the AI layer:
 *
 *  • Real-time style surge forecast
 *  • Predicted vs. actual demand comparison
 *  • Fare RL Q-values (which action is favoured)
 *  • Error metrics (RMSE, MAE, R²)
 *  • Simple 24h replay of surge history
 */
public class DashboardFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        TabPane tabs = new TabPane();

        tabs.getTabs().add(makeSurgeForecastTab());
        tabs.getTabs().add(makeDemandTab());
        tabs.getTabs().add(makeFareRLTab());
        tabs.getTabs().add(makeMetricsTab());
        tabs.getTabs().add(makeReplayTab());

        BorderPane root = new BorderPane(tabs);
        Scene scene = new Scene(root, 1100, 700);
        primaryStage.setTitle("Drone Fleet – AI Insights Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // ---------- Synthetic demo data ----------

    private static final int HOURS = 24;

    private double[] timeIndices() {
        double[] t = new double[HOURS];
        for (int i = 0; i < HOURS; i++) t[i] = i;
        return t;
    }

    private double[] sampleDemand() {
        double[] d = new double[HOURS];
        for (int i = 0; i < HOURS; i++) {
            // morning & evening peaks
            d[i] = 20 + 30 * Math.exp(-Math.pow(i - 9, 2) / 18.0)
                       + 25 * Math.exp(-Math.pow(i - 18, 2) / 18.0);
        }
        return d;
    }

    private double[] sampleSupply() {
        double[] s = new double[HOURS];
        for (int i = 0; i < HOURS; i++) {
            s[i] = 40 + 10 * Math.sin(i / 3.0);
        }
        return s;
    }

    private double[] sampleSurge(double[] demand, double[] supply) {
        double[] surge = new double[HOURS];
        for (int i = 0; i < HOURS; i++) {
            double ratio = demand[i] / Math.max(10.0, supply[i]);
            surge[i] = 1.0 + Math.min(2.0, Math.max(0.0, ratio - 0.5));
        }
        return surge;
    }

    // ---------- Tabs ----------

    private Tab makeSurgeForecastTab() {
        double[] t = timeIndices();
        double[] demand = sampleDemand();
        double[] supply = sampleSupply();
        double[] surgeActual = sampleSurge(demand, supply);

        // Weather index is a simple synthetic curve
        double[] weather = new double[HOURS];
        for (int i = 0; i < HOURS; i++) {
            weather[i] = 0.3 + 0.2 * Math.sin(i / 4.0);
        }

        // Train multi-feature surge predictor
        SurgePredictor predictor = new SurgePredictor();
        double[][] features = new double[HOURS][4];
        for (int i = 0; i < HOURS; i++) {
            features[i][0] = t[i];
            features[i][1] = weather[i];
            features[i][2] = demand[i];
            features[i][3] = supply[i];
        }
        predictor.train(features, surgeActual);

        final NumberAxis xAxis = new NumberAxis(0, HOURS - 1, 2);
        final NumberAxis yAxis = new NumberAxis(0.8, 3.2, 0.2);
        xAxis.setLabel("Hour");
        yAxis.setLabel("Surge Multiplier");
        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Surge Forecast (Multi-feature Regression)");

        XYChart.Series<Number, Number> actualSeries = new XYChart.Series<>();
        actualSeries.setName("Actual");
        XYChart.Series<Number, Number> predictedSeries = new XYChart.Series<>();
        predictedSeries.setName("Predicted");

        for (int i = 0; i < HOURS; i++) {
            double pred = predictor.predict(t[i], weather[i], demand[i], supply[i]);
            actualSeries.getData().add(new XYChart.Data<>(i, surgeActual[i]));
            predictedSeries.getData().add(new XYChart.Data<>(i, pred));
        }

        chart.getData().addAll(actualSeries, predictedSeries);

        VBox box = new VBox(10, chart);
        box.setPadding(new Insets(10));
        Tab tab = new Tab("Surge Forecast", box);
        tab.setClosable(false);
        return tab;
    }

    private Tab makeDemandTab() {
        double[] t = timeIndices();
        double[] demand = sampleDemand();

        DemandForecaster forecaster = new DemandForecaster();
        forecaster.train(t, demand);

        double[] forecast = forecaster.forecastNext(HOURS);

        final NumberAxis xAxis = new NumberAxis(0, HOURS - 1, 2);
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Hour");
        yAxis.setLabel("Demand");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Demand – Predicted vs Actual");

        XYChart.Series<Number, Number> actualSeries = new XYChart.Series<>();
        actualSeries.setName("Actual");
        XYChart.Series<Number, Number> forecastSeries = new XYChart.Series<>();
        forecastSeries.setName("Forecast");

        for (int i = 0; i < HOURS; i++) {
            actualSeries.getData().add(new XYChart.Data<>(i, demand[i]));
            forecastSeries.getData().add(new XYChart.Data<>(i, forecast[i]));
        }

        chart.getData().addAll(actualSeries, forecastSeries);

        VBox box = new VBox(10, chart);
        box.setPadding(new Insets(10));
        Tab tab = new Tab("Demand", box);
        tab.setClosable(false);
        return tab;
    }

    private Tab makeFareRLTab() {
        double[] t = timeIndices();
        double[] demand = sampleDemand();
        double[] supply = sampleSupply();
        double[] surge = sampleSurge(demand, supply);

        FareRLAgent agent = new FareRLAgent(1, 4);

        // Run a few training episodes
        for (int episode = 0; episode < 50; episode++) {
            for (int i = 0; i < HOURS - 1; i++) {
                double s = surge[i];
                int action = agent.chooseAction(0, s, 0.2);
                double nextS = surge[i + 1];
                // Reward: prefer actions that keep surge moderate (~1.5)
                double target = 1.5;
                double reward = -Math.abs(nextS - target);
                agent.update(0, s, action, reward, nextS);
            }
        }

        // Visualise learned Q-values per bucket
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Action");
        yAxis.setLabel("Q-value");

        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Fare Adjustment – Q-learning Insight (Zone 0)");

        String[] actions = {"-10%", "+10%"};
        for (int b = 0; b < 4; b++) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Surge bucket " + b);
            for (int a = 0; a < 2; a++) {
                double qVal = agent.getQ(0, b, a);
                series.getData().add(new XYChart.Data<>(actions[a], qVal));
            }
            chart.getData().add(series);
        }

        VBox box = new VBox(10, chart);
        box.setPadding(new Insets(10));
        Tab tab = new Tab("Fare RL", box);
        tab.setClosable(false);
        return tab;
    }

    private Tab makeMetricsTab() {
        double[] t = timeIndices();
        double[] demand = sampleDemand();
        double[] supply = sampleSupply();
        double[] surgeActual = sampleSurge(demand, supply);

        double[] weather = new double[HOURS];
        for (int i = 0; i < HOURS; i++) {
            weather[i] = 0.3 + 0.2 * Math.sin(i / 4.0);
        }

        SurgePredictor predictor = new SurgePredictor();
        double[][] features = new double[HOURS][4];
        for (int i = 0; i < HOURS; i++) {
            features[i][0] = t[i];
            features[i][1] = weather[i];
            features[i][2] = demand[i];
            features[i][3] = supply[i];
        }
        predictor.train(features, surgeActual);

        DemandForecaster forecaster = new DemandForecaster();
        forecaster.train(t, demand);

        double corrDemandSurge = CorrelationEngine.pearson(demand, surgeActual);
        double corrSupplySurge = CorrelationEngine.pearson(supply, surgeActual);

        VBox box = new VBox(10);
        box.setPadding(new Insets(20));

        box.getChildren().addAll(
                new Label("Surge Predictor (multi-feature)"),
                new Label(String.format("  RMSE: %.4f", predictor.getRmse())),
                new Label(String.format("  MAE : %.4f", predictor.getMae())),
                new Label(String.format("  R²  : %.4f", predictor.getR2())),
                new Label("Demand Forecaster"),
                new Label(String.format("  RMSE: %.4f", forecaster.getRmse())),
                new Label(String.format("  MAE : %.4f", forecaster.getMae())),
                new Label("Correlation (Demand–Supply–Surge)"),
                new Label(String.format("  Corr(demand, surge): %.4f", corrDemandSurge)),
                new Label(String.format("  Corr(supply, surge): %.4f", corrSupplySurge))
        );

        Tab tab = new Tab("AI Metrics", box);
        tab.setClosable(false);
        return tab;
    }

    private Tab makeReplayTab() {
        double[] t = timeIndices();
        double[] demand = sampleDemand();
        double[] supply = sampleSupply();
        double[] surge = sampleSurge(demand, supply);

        NumberAxis xAxis = new NumberAxis(0, HOURS - 1, 2);
        NumberAxis yAxis = new NumberAxis(0.8, 3.2, 0.2);
        xAxis.setLabel("Hour (past 24h)");
        yAxis.setLabel("Surge Multiplier");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Historical Replay – Surge over last 24 hours");

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Surge");
        for (int i = 0; i < HOURS; i++) {
            series.getData().add(new XYChart.Data<>(i, surge[i]));
        }
        chart.getData().add(series);

        VBox box = new VBox(10, chart);
        box.setPadding(new Insets(10));
        Tab tab = new Tab("24h Replay", box);
        tab.setClosable(false);
        return tab;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

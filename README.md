# 🛰️ GeoDroneAI  
### *An Intelligent Drone Delivery Simulator with AI-Driven Optimization, Forecasting, and Reinforcement Learning*  
> Built entirely in Java — blending classical DSA + Machine Learning + Simulation + Analytics  




## 🌍 Overview

**GeoDroneAI** is a next-generation **Drone Delivery Simulation Framework** integrating:
- Intelligent Route Planning  
- 3D Geospatial Navigation  
- AI-Powered Surge Prediction  
- Battery Optimization  
- Reinforcement Learning–based Pricing  

The system simulates a **realistic drone fleet** with weather impact, zone-based surge modeling, dynamic fare adjustments, and learning-based decision feedback — all inside a modular **Java console application**.

> ⚡ *GeoDroneAI bridges classical DSA with applied AI — showing how algorithms and data can co-exist in real-world systems.*

---

## 🧠 System Architecture

```text
+------------------------------------------------------------+
|                        GeoDroneAI                          |
+------------------------------------------------------------+
|                         main.Main                          |
|                     (Console Interface)                    |
+------------------------------------------------------------+
|              SERVICES LAYER (Execution & Mgmt)             |
|  • DroneService – manages drone fleet & flight paths       |
|  • RouteService – computes routes, checks conflicts        |
|  • WeatherService – handles storm/unsafe zones             |
|  • SegmentTreeService – 3D airspace indexing               |
+------------------------------------------------------------+
|              AI LAYER (Predictive Intelligence)            |
|  • SurgePredictor – regression-based surge estimation      |
|  • DemandForecaster – weighted moving average forecast     |
|  • BatteryModel – energy consumption predictor             |
|  • FareRLAgent – Q-learning based fare adjustment          |
|  • CorrelationEngine – demand-supply correlation           |
+------------------------------------------------------------+
|                DATABASE / MODELS LAYER                     |
|  • Warehouse, DeliveryPoint, Drone, ChargingStation, etc.  |
+------------------------------------------------------------+




```
---

## ⚙️ Core Modules

| Module | Description |
|--------|--------------|
| **Warehouse Manager** | Manages all warehouse locations, capacities, and IDs. |
| **Delivery Planner** | Creates drones, plans paths, avoids no-fly zones. |
| **Weather Simulator** | Marks regions unsafe using 3D bounding boxes. |
| **Collision Engine** | Detects overlapping drone paths using spatial segment trees. |
| **Geo Station Manager** | Tracks price variations per station. |
| **Fare Estimator** | Calculates dynamic fare based on surge, fuel, and time. |

---

## 🤖 Artificial Intelligence Components

| AI Module | Type | Description |
|------------|------|-------------|
| **SurgePredictor** | Multi-variable Regression | Learns surge factor from time, weather, demand, and driver count. |
| **DemandForecaster** | Weighted Moving Average + Polynomial Regression | Predicts future demand trend based on past hours. |
| **BatteryModel** | Regression on Distance + Altitude | Predicts energy drain per route; ensures safe flight dispatch. |
| **FareRLAgent** | Q-Learning Reinforcement Model | Learns fare changes rewarded by trip success rate & wait time. |
| **CorrelationEngine** | Pearson Correlation | Quantifies link between demand–supply to balance fleet. |

📊 AI Model Insights

### 🧩 SurgePredictor  
Uses **Multiple Linear Regression**:  y = a + b1t + b2w + b3d + b4c

where  
`y` = surge factor, `t` = time index, `w` = weather, `d` = demand, `c` = driver count.  
Outputs:
- RMSE (Root Mean Squared Error)
- MAE (Mean Absolute Error)
- R² (Goodness of fit)

---

### 🔮 DemandForecaster  
Implements **Weighted Moving Average** with exponential smoothing:  F(t+1) = αD(t) + (1 - α)F(t)

Used to predict next-hour delivery requests.

---

### ⚡ BatteryModel  
Predicts battery drain as a function of distance and altitude using regression:  E = k0 + k1distance + k2altitude  

Helps prevent drone mission failures.

---

### 🧠 FareRLAgent (Reinforcement Learning)
Implements **Q-Learning** algorithm:  Q(s, a) ← Q(s, a) + α [R + γ max(Q(s', a')) - Q(s, a)]

States: `(zone, surge)`  
Actions: `(+10%, -10%)` fare adjustments  
Rewards: trip success & wait time balance  

---

### 📈 CorrelationEngine  
Uses **Pearson’s Coefficient**:  r = Σ(xi - x̄)(yi - ȳ) / √(Σ(xi - x̄)² Σ(yi - ȳ)²)

Shows whether market is demand-heavy or supply-heavy.

---

## 🧩 Project Structure
```
GeoDroneAI/
├── src/
│   ├── main/           → Main.java (menu controller)
│   ├── models/         → Warehouse, Drone, DeliveryPoint, etc.
│   ├── services/       → DroneService, RouteService, WeatherService
│   ├── ai/             → SurgePredictor, DemandForecaster, BatteryModel, RLAgent
│   └── tests/          → AIDiagnostics.java, TestCases.java
├── bin/                → Compiled class files
├── README.md           → Full documentation
└── run_instructions.txt
```


---

## 🚀 Setup & Execution

### 💾 Prerequisites
- Java JDK 17 or above  
- (Optional) JavaFX SDK if you later expand to dashboard view  

---

### 🧱 Compile

javac -d bin src/ai/*.java src/models/*.java src/services/*.java src/tests/*.java src/main/*.java

java -cp bin main.Main

🧭 Console Menu
1) List all data
2) Create Delivery
3) Simulate Weather
4) Query Airspace
5) Run Collision Check
6) Run Full Test Suite (AI + Planning)
7) Geo: Station Price Manager
8) Geo: Ride Fare Estimator
0) Exit

🧪 Automated Test Suite

Run option 6 → executes tests.TestCases + tests.AIDiagnostics.
```
Expected Output:
=====================
 AI DIAGNOSTIC REPORT
=====================
[SurgePredictor] RMSE = 0.175  MAE = 0.130  R² = 0.892
[DemandForecaster] RMSE = 0.210  MAE = 0.150  Forecast(Next) = 69.0
[BatteryModel] Predicted energy drain for 15km,120m = 18.72%
[FareRLAgent] Ran 30 simulated episodes (Q-table updated).
[CorrelationEngine] Demand–Supply correlation = -0.745
```
✅ AI metrics generated successfully!


✅ AI metrics generated successfully!

💡 Key Features
```
3D Segment Tree-based airspace structure

Dynamic weather unsafe zone simulation

AI-based battery prediction and surge modeling

Reinforcement learning-based pricing

Rich AI diagnostics with regression metrics

Extensible modular OOP architecture
```
📈 Sample Output (Screenshot Example)
=== Drone Delivery Simulator (GeoDroneAI) ===
Choice: 6
```
[DroneService] Created dr-1349ff at warehouse W3
[Planner] Reserved path for drone dr-1349ff pathLen=301
...
[SurgePredictor] RMSE = 0.175  MAE = 0.130  R² = 0.892
...
[CorrelationEngine] Demand–Supply correlation = -0.745
```
🧱 Technologies Used
```
Layer	Technology
Language	Java (JDK 17+)
Paradigm	OOP + Modular Architecture
AI/ML	Custom Regression, Q-Learning, Forecasting
Data Structures	Segment Tree, ArrayList, HashMap
Simulation	Console-based dynamic environment
Optional UI	JavaFX for visualization dashboards
```
🧭 Future Scope

🔹 Integrate real map APIs (OpenStreetMap / Google Maps)

🔹 Build JavaFX Dashboard showing live surge and AI metrics

🔹 Add Neural Surge Predictor with backpropagation

🔹 Extend to Multi-Agent Reinforcement Learning for drone swarms

🔹 Migrate data layer to MySQL for persistence

🤝 Contribution Guide

Want to improve GeoDroneAI?

Fork the repository

Create a feature branch

Commit your changes

Submit a Pull Request

Code style:

Java 17

Follow Google Java Style Guide

Document all public methods

📜 License

This project is released under the MIT License.
You’re free to use, modify, and distribute with attribution.

👨‍💻 Author

Raparla Megha Pranay
Computer Science Engineering (2st Year)
Passionate about building real-world intelligent systems that combine algorithms, AI, and simulation.


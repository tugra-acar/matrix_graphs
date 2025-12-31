# matrix_graphs
A comprehensive graph simulation engine designed to model, secure, and optimize the hidden communication network of the Resistance inside the Matrix. Built from scratch in Java without using built-in collection libraries for core data structures. The system features a custom CLI to manage network topology, utilizes multi-objective pathfinding algorithms (Dijkstra variant) to calculate optimal routes under dynamic latency and clearance constraints, and performs critical network analysis to detect articulation points (single points of failure) and bridges.

## 📜 Overview

**matrix_graphs** is a robust graph simulation engine capable of modeling, analyzing, and traversing complex network topologies. Inspired by the infrastructure of "The Matrix," this system simulates a network of safe hosts and hidden backdoors, allowing for advanced routing optimization and structural vulnerability analysis.

Built entirely from scratch in Java, this project demonstrates core computer science concepts by implementing custom data structures and algorithms without relying on standard collection libraries. It features a CLI-based operator console for dynamic network management.

## 🚀 Key Features

### 1. Network Topology Simulation
- **Dynamic Graph Construction:** Create nodes (Hosts) with specific security attributes and link them via weighted edges (Backdoors).
- **Edge Attributes:** Links are modeled with real-world constraints including latency, bandwidth, and firewall security levels.
- **State Management:** Simulates active network changes by sealing/unsealing tunnels in real-time.

### 2. Multi-Objective Pathfinding
- **Advanced Routing:** Calculates the optimal path between nodes using a modified **Dijkstra’s Algorithm**.
- **Dynamic Weighting:** Handles dynamic edge costs where latency increases based on network congestion ($\lambda$) and path length (hop count).
- **Constraint Satisfaction:** Filters paths based on minimum bandwidth requirements and node security clearances.

### 3. Structural Analysis & Vulnerability Detection
- **Connectivity Analysis:** Uses **BFS (Breadth-First Search)** to determine network segmentation and count connected components.
- **Critical Failure Points:**
  - **Articulation Points:** Identifies nodes whose removal would fragment the network (Single Point of Failure).
  - **Bridges:** Detects critical edges that are essential for graph connectivity.
- **Cycle Detection:** Uses **DFS (Depth-First Search)** to identify loops within the network topology.

## 🛠 Technical Implementation

This project was engineered with a **zero-dependency** philosophy to demonstrate deep understanding of data structures:

* **Custom HashMap:** Implemented a generic hash map using separate chaining with `ArrayList` and `LinkedList` to achieve $O(1)$ average time complexity for lookups.
* **Custom Priority Queue:** Built a binary heap structure to optimize the efficiency of the pathfinding algorithms.
* **Graph Representation:** Utilizes an adjacency list architecture integrated within the custom hash map for efficient traversal.

## 💻 Installation & Usage

### Prerequisites
- Java Development Kit (JDK) 11 or higher.

### Compilation
Compile all source files using the standard Java compiler:

```bash
javac *.java

```

### Running the Engine

The program accepts an instruction file containing network commands and logs the simulation results to an output file.

```bash
java Main <input_file> <output_file>

```

**Example:**

```bash
java Main scenarios/network_layout.txt logs/simulation_results.txt

```

### 🧪 Testing & Validation

The project includes a custom automated test suite (`Tester.java`) that verifies the engine's correctness against a set of test cases.

To run the automated tests:

```bash
java Tester

```

*The tester will automatically execute inputs from the `testcases/inputs` directory and compare the results against expected outputs.*

## 🕹 Command Interface & Output

The engine parses a specific command set to manipulate the graph.

**Input Command Example:**

```text
spawn_host ZION_CORE 5
spawn_host MORPHEUS_HUB 3
link_backdoor ZION_CORE MORPHEUS_HUB 10 2000 3
trace_route ZION_CORE MORPHEUS_HUB 1000 2
simulate_breach ZION_CORE
oracle_report

```

**Generated Log Output Example:**

```text
Spawned host ZION_CORE with clearance level 5.
Spawned host MORPHEUS_HUB with clearance level 3.
Linked ZION_CORE <-> MORPHEUS_HUB with latency 10ms, bandwidth 2000Mbps, firewall 3.
Optimal route ZION_CORE -> MORPHEUS_HUB: ZION_CORE -> MORPHEUS_HUB (Latency = 12ms)
Host ZION_CORE is NOT an articulation point. Network remains the same.
--- Resistance Network Report ---
Total Hosts: 2
Total Unsealed Backdoors: 1
Network Connectivity: Connected
Connected Components: 1
Contains Cycles: No
Average Bandwidth: 2000.0Mbps
Average Clearance Level: 4.0

```

## 📂 File Structure

* `Main.java`: Command interpreter and simulation loop.
* `Host.java`: Node object definition representing network hosts.
* `Backdoor.java`: Edge object definition representing tunnels.
* `HashMap.java`: **[Custom Core]** Optimized key-value storage implementation.
* `PriorityQueue.java`: **[Custom Core]** Binary heap implementation for Dijkstra's algorithm.
* `Tester.java`: Automated testing utility for batch validation.

## 📝 License

This project is open source and available under the [MIT License](https://www.google.com/search?q=LICENSE).

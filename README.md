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

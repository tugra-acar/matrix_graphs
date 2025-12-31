import java.io.*;
import java.util.Locale;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Main {
    // global storage
    public static HashMap<String, Host> network = new HashMap<>();
    public static BufferedWriter writer;
    public static BufferedReader reader;
    public static StringTokenizer tokenizer;

    public static void main(String[] args) {
        // check args
        if (args.length != 2) {
            System.out.println("Usage: java Main <input_file> <output_file>");
            return;
        }

        try {
            reader = new BufferedReader(new FileReader(args[0]));
            writer = new BufferedWriter(new FileWriter(args[1]));

            String command;
            while ((command = nextToken()) != null) {
                switch (command) {
                    case "spawn_host":
                        spawnHost();
                        break;
                    case "link_backdoor":
                        linkBackdoor();
                        break;
                    case "seal_backdoor":
                        sealBackdoor();
                        break;
                    case "trace_route":
                        traceRoute();
                        break;
                    case "scan_connectivity":
                        scanConnectivity();
                        break;
                    case "simulate_breach":
                        simulateBreach();
                        break;
                    case "oracle_report":
                        oracleReport();
                        break;
                    default:
                        break;
                }
            }

            reader.close();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // tokenizer helper
    private static String nextToken() throws IOException {
        while (tokenizer == null || !tokenizer.hasMoreTokens()) {
            String line = reader.readLine();
            if (line == null) return null;
            tokenizer = new StringTokenizer(line);
        }
        return tokenizer.nextToken();
    }

    // write to output
    public static void log(String message) throws IOException {
        writer.write(message);
        writer.newLine();
    }

    private static void spawnHost() throws IOException {
        String id = nextToken();
        int clearance = Integer.parseInt(nextToken());

        if (!id.matches("[A-Z0-9_]+") || network.containsKey(id)) {
            log("Some error occurred in spawn_host.");
        } else {
            Host newHost = new Host(id, clearance);
            network.put(id, newHost);
            log("Spawned host " + id + " with clearance level " + clearance + ".");
        }
    }

    private static void linkBackdoor() throws IOException {
        String id1 = nextToken();
        String id2 = nextToken();
        long lat = Long.parseLong(nextToken());
        long bw = Long.parseLong(nextToken());
        int fw = Integer.parseInt(nextToken());

        Host h1 = network.get(id1);
        Host h2 = network.get(id2);

        // validate nodes
        if (h1 == null || h2 == null || id1.equals(id2)) {
            log("Some error occurred in link_backdoor.");
            return;
        }

        // check if connected
        boolean exists = false;
        for (Backdoor b : h1.connections) {
            if (b.destination.id.equals(id2)) {
                exists = true;
                break;
            }
        }

        if (exists) {
            log("Some error occurred in link_backdoor.");
        } else {
            // bidirectional link
            h1.connections.add(new Backdoor(h2, lat, bw, fw));
            h2.connections.add(new Backdoor(h1, lat, bw, fw));
            log("Linked " + id1 + " <-> " + id2 + " with latency " + lat + "ms, bandwidth " + bw + "Mbps, firewall " + fw + ".");
        }
    }

    private static void sealBackdoor() throws IOException {
        String id1 = nextToken();
        String id2 = nextToken();

        Host h1 = network.get(id1);
        Host h2 = network.get(id2);

        if (h1 == null || h2 == null) {
            log("Some error occurred in seal_backdoor.");
            return;
        }

        Backdoor b1 = null;
        for (Backdoor b : h1.connections) {
            if (b.destination.id.equals(id2)) {
                b1 = b;
                break;
            }
        }

        Backdoor b2 = null;
        for (Backdoor b : h2.connections) {
            if (b.destination.id.equals(id1)) {
                b2 = b;
                break;
            }
        }

        if (b1 == null || b2 == null) {
            log("Some error occurred in seal_backdoor.");
        } else {
            // toggle status
            b1.isSealed = !b1.isSealed;
            b2.isSealed = !b2.isSealed;

            if (b1.isSealed) {
                log("Backdoor " + id1 + " <-> " + id2 + " sealed.");
            } else {
                log("Backdoor " + id1 + " <-> " + id2 + " unsealed.");
            }
        }
    }

    private static void traceRoute() throws IOException {
        String sourceId = nextToken();
        String destId = nextToken();
        long minBandwidth = Long.parseLong(nextToken());
        int lambda = Integer.parseInt(nextToken());

        Host sourceHost = network.get(sourceId);
        Host destHost = network.get(destId);

        if (sourceHost == null || destHost == null) {
            log("Some error occurred in trace_route.");
            return;
        }

        if (sourceId.equals(destId)) {
            log("Optimal route " + sourceId + " -> " + destId + ": " + sourceId + " (Latency = 0ms)");
            return;
        }

        // reset distances
        for (String key : network.keys()) { network.get(key).distance = Long.MAX_VALUE; }

        PriorityQueue pq = new PriorityQueue();

        // init source
        sourceHost.distance = 0;
        pq.push(new PriorityQueue.RouteState(sourceHost, 0, 0, null));

        PriorityQueue.RouteState finalState = null;

        if (lambda == 0) {
            // dijkstra logic
            while (!pq.isEmpty()) {
                PriorityQueue.RouteState current = pq.pop();

                if (current.currentHost == destHost) {
                    finalState = current;
                    break;
                }

                if (current.totalLatency > current.currentHost.distance) {
                    continue;
                }

                for (Backdoor link : current.currentHost.connections) {
                    if (link.isSealed) continue;
                    if (link.bandwidth < minBandwidth) continue;
                    if (current.currentHost.clearance < link.firewall) continue;

                    long newDist = current.totalLatency + link.latency;
                    Host neighbor = link.destination;

                    if (newDist < neighbor.distance) {
                        neighbor.distance = newDist;
                        pq.push(new PriorityQueue.RouteState(neighbor, newDist, current.hopCount + 1, current));
                    }
                }
            }
        } else {
            // dynamic latency logic
            HashMap<String, ArrayList<long[]>> visitedStates = new HashMap<>();
            ArrayList<long[]> startList = new ArrayList<>();
            startList.add(new long[]{0, 0});
            visitedStates.put(sourceId, startList);

            while (!pq.isEmpty()) {
                PriorityQueue.RouteState current = pq.pop();

                if (current.currentHost == destHost) {
                    finalState = current;
                    break;
                }

                // check stale state
                ArrayList<long[]> currentStates = visitedStates.get(current.currentHost.id);
                boolean isStale = false;
                if (currentStates != null) {
                    for(long[] s : currentStates) {
                        if(s[0] <= current.totalLatency && s[1] <= current.hopCount) {
                            if(s[0] == current.totalLatency && s[1] == current.hopCount) continue;
                            isStale = true; break;
                        }
                    }
                }
                if(isStale) continue;

                for (Backdoor link : current.currentHost.connections) {
                    if (link.isSealed) continue;
                    if (link.bandwidth < minBandwidth) continue;
                    if (current.currentHost.clearance < link.firewall) continue;

                    long dynamicCost = link.latency + (long)(lambda * current.hopCount);
                    long newTotalLatency = current.totalLatency + dynamicCost;
                    int newHops = current.hopCount + 1;
                    Host neighbor = link.destination;

                    ArrayList<long[]> states = visitedStates.get(neighbor.id);
                    if (states == null) {
                        states = new ArrayList<>();
                        visitedStates.put(neighbor.id, states);
                    }

                    boolean dominated = false;
                    java.util.Iterator<long[]> it = states.iterator();
                    while(it.hasNext()){
                        long[] s = it.next();
                        if (newTotalLatency >= s[0] && newHops >= s[1]) { dominated = true; break; }
                        if (newTotalLatency <= s[0] && newHops <= s[1]) { it.remove(); }
                    }

                    if (!dominated) {
                        states.add(new long[]{newTotalLatency, newHops});
                        pq.push(new PriorityQueue.RouteState(neighbor, newTotalLatency, newHops, current));
                    }
                }
            }
        }

        // print result
        if (finalState != null) {
            java.util.LinkedList<String> pathStack = new java.util.LinkedList<>();
            PriorityQueue.RouteState temp = finalState;
            while (temp != null) {
                pathStack.addFirst(temp.currentHost.id);
                temp = temp.previous;
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < pathStack.size(); i++) {
                sb.append(pathStack.get(i));
                if (i < pathStack.size() - 1) sb.append(" -> ");
            }
            log("Optimal route " + sourceId + " -> " + destId + ": " + sb.toString() + " (Latency = " + finalState.totalLatency + "ms)");
        } else {
            log("No route found from " + sourceId + " to " + destId);
        }
    }

    // bfs for connected components
    private static int countComponents(String excludedHostId) {
        HashMap<String, Boolean> visited = new HashMap<>();
        int components = 0;

        ArrayList<String> allHosts = network.keys();

        for (String startNode : allHosts) {
            if (startNode.equals(excludedHostId) || visited.containsKey(startNode)) {
                continue;
            }

            components++;
            ArrayList<String> q = new ArrayList<>();
            q.add(startNode);
            visited.put(startNode, true);

            int head = 0;
            while(head < q.size()){
                String uId = q.get(head++);
                Host u = network.get(uId);

                for(Backdoor b : u.connections){
                    if(b.isSealed) continue;

                    String vId = b.destination.id;
                    if(vId.equals(excludedHostId)) continue;

                    if(!visited.containsKey(vId)){
                        visited.put(vId, true);
                        q.add(vId);
                    }
                }
            }
        }
        return components;
    }

    private static void scanConnectivity() throws IOException {
        int components = countComponents(null);

        if (components <= 1) {
            log("Network is fully connected.");
        } else {
            log("Network has " + components + " disconnected components.");
        }
    }

    private static void simulateBreach() throws IOException {
        String arg1 = nextToken();
        String arg2 = null;

        // check optional second arg on same line
        if (tokenizer.hasMoreTokens()) {
            arg2 = nextToken();
        }

        int initialComponents = countComponents(null);

        if (arg2 == null) {
            // host breach
            String hostId = arg1;
            Host h = network.get(hostId);
            if (h == null) {
                log("Some error occurred in simulate_breach.");
                return;
            }
            int afterComponents = countComponents(hostId);
            if (afterComponents > initialComponents) {
                log("Host " + hostId + " IS an articulation point.");
                log("Failure results in " + afterComponents + " disconnected components.");
            } else {
                log("Host " + hostId + " is NOT an articulation point. Network remains the same.");
            }
        } else {
            // backdoor breach
            String id1 = arg1;
            String id2 = arg2;
            Host h1 = network.get(id1);
            Host h2 = network.get(id2);
            Backdoor target = null, mirror = null;

            if (h1 != null && h2 != null) {
                for (Backdoor b : h1.connections) {
                    if (b.destination.id.equals(id2)) target = b;
                }
                for (Backdoor b : h2.connections) {
                    if (b.destination.id.equals(id1)) mirror = b;
                }
            }

            if (target == null || mirror == null || target.isSealed) {
                log("Some error occurred in simulate_breach.");
                return;
            }

            target.isSealed = true;
            mirror.isSealed = true;
            int afterComponents = countComponents(null);
            target.isSealed = false;
            mirror.isSealed = false;

            if (afterComponents > initialComponents) {
                log("Backdoor " + id1 + " <-> " + id2 + " IS a bridge.");
                log("Failure results in " + afterComponents + " disconnected components.");
            } else {
                log("Backdoor " + id1 + " <-> " + id2 + " is NOT a bridge. Network remains the same.");
            }
        }
    }

    // cycle check dfs
    private static boolean hasCycle() {
        HashMap<String, Boolean> visited = new HashMap<>();
        ArrayList<String> keys = network.keys();

        for (String key : keys) {
            if (!visited.containsKey(key)) {
                if (dfsCycle(key, null, visited)) return true;
            }
        }
        return false;
    }

    private static boolean dfsCycle(String currentId, String parentId, HashMap<String, Boolean> visited) {
        visited.put(currentId, true);
        Host h = network.get(currentId);

        for (Backdoor b : h.connections) {
            if (b.isSealed) continue;

            String neighborId = b.destination.id;

            if (neighborId.equals(parentId)) continue;

            if (visited.containsKey(neighborId)) {
                return true;
            }

            if (dfsCycle(neighborId, currentId, visited)) return true;
        }
        return false;
    }

    private static void oracleReport() throws IOException {
        int totalHosts = network.getSize();
        int totalUnsealed = 0;
        double totalBandwidth = 0;
        double totalClearance = 0;

        ArrayList<String> keys = network.keys();
        for (String key : keys) {
            Host h = network.get(key);
            totalClearance += h.clearance;

            for (Backdoor b : h.connections) {
                if (!b.isSealed) {
                    totalUnsealed++;
                    totalBandwidth += b.bandwidth;
                }
            }
        }

        int uniqueBackdoors = totalUnsealed / 2;
        double avgBandwidth = (uniqueBackdoors == 0) ? 0.0 : (totalBandwidth / 2.0) / uniqueBackdoors;
        double avgClearance = (totalHosts == 0) ? 0.0 : totalClearance / totalHosts;

        int components = countComponents(null);
        String connStatus = (components <= 1) ? "Connected" : "Disconnected";
        String cycleStatus = hasCycle() ? "Yes" : "No";

        String avgBwStr = String.format(Locale.US, "%.1f", avgBandwidth);
        String avgClStr = String.format(Locale.US, "%.1f", avgClearance);

        log("--- Resistance Network Report ---");
        log("Total Hosts: " + totalHosts);
        log("Total Unsealed Backdoors: " + uniqueBackdoors);
        log("Network Connectivity: " + connStatus);
        log("Connected Components: " + components);
        log("Contains Cycles: " + cycleStatus);
        log("Average Bandwidth: " + avgBwStr + "Mbps");
        log("Average Clearance Level: " + avgClStr);
    }
}
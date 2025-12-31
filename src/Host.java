import java.util.ArrayList;

public class Host {
    public String id;
    public int clearance;
    public ArrayList<Backdoor> connections;
    public long distance = Long.MAX_VALUE;

    public Host(String id, int clearance) {
        this.id = id;
        this.clearance = clearance;
        this.connections = new ArrayList<>();
    }
}
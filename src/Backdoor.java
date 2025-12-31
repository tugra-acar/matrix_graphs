public class Backdoor {
    public Host destination; // where does this tunnel go
    public long latency;
    public long bandwidth;
    public int firewall;
    public boolean isSealed;

    public Backdoor(Host destination, long latency, long bandwidth, int firewall) {
        this.destination = destination;
        this.latency = latency;
        this.bandwidth = bandwidth;
        this.firewall = firewall;
        this.isSealed = false;
    }
}
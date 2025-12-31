import java.util.ArrayList;

public class PriorityQueue {

    public static class RouteState {
        public Host currentHost;
        public long totalLatency;
        public int hopCount;
        public RouteState previous;

        public RouteState(Host currentHost, long totalLatency, int hopCount, RouteState previous) {
            this.currentHost = currentHost;
            this.totalLatency = totalLatency;
            this.hopCount = hopCount;
            this.previous = previous;
        }
    }

    private ArrayList<RouteState> heap;

    public PriorityQueue() {
        heap = new ArrayList<>();
        heap.add(null); // 1-based indexing
    }

    private boolean better(RouteState a, RouteState b) {
        if (a.totalLatency != b.totalLatency) return a.totalLatency < b.totalLatency;
        if (a.hopCount != b.hopCount) return a.hopCount < b.hopCount;
        return a.currentHost.id.compareTo(b.currentHost.id) < 0;
    }

    public int getSize() {
        return heap.size() - 1;
    }

    public boolean isEmpty() {
        return getSize() == 0;
    }

    public void push(RouteState state) {
        heap.add(state);
        percolateUp(getSize());
    }

    public RouteState pop() {
        if (isEmpty()) return null;
        RouteState best = heap.get(1);
        int lastIndex = getSize();
        swap(1, lastIndex);
        heap.remove(lastIndex);
        if (!isEmpty()) {
            percolateDown(1);
        }
        return best;
    }

    private void percolateUp(int i) {
        while (i > 1) {
            int parent = i / 2;
            if (!better(heap.get(i), heap.get(parent))) break;
            swap(i, parent);
            i = parent;
        }
    }

    private void percolateDown(int i) {
        int n = getSize();
        while (2 * i <= n) {
            int left = 2 * i;
            int right = left + 1;
            int best = left;
            if (right <= n && better(heap.get(right), heap.get(left))) {
                best = right;
            }
            if (better(heap.get(i), heap.get(best))) break;
            swap(i, best);
            i = best;
        }
    }

    private void swap(int i, int j) {
        RouteState tmp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, tmp);
    }
}
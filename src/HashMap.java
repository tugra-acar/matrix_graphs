import java.util.ArrayList;
import java.util.LinkedList;

public class HashMap<K, V> {

    private static class Node<K, V> {
        K key;
        V value;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private ArrayList<LinkedList<Node<K, V>>> table;
    private int capacity;
    private int size;

    private static final int DEFAULT_CAPACITY = 200003; // prime number

    public HashMap() {
        this.capacity = DEFAULT_CAPACITY;
        this.size = 0;

        table = new ArrayList<>(capacity);
        for (int i = 0; i < capacity; i++) {
            table.add(null);
        }
    }

    private int hash(K key) {
        return (key.hashCode() & 0x7fffffff) % capacity;
    }

    public boolean put(K key, V value) {
        int h = hash(key);

        if (table.get(h) == null) {
            table.set(h, new LinkedList<>());
        }

        LinkedList<Node<K, V>> bucket = table.get(h);

        for (Node<K, V> node : bucket) {
            if (node.key.equals(key)) {
                return false;
            }
        }

        bucket.add(new Node<>(key, value));
        size++;
        return true;
    }

    public V get(K key) {
        int h = hash(key);
        LinkedList<Node<K, V>> bucket = table.get(h);

        if (bucket == null) {
            return null;
        }

        for (Node<K, V> node : bucket) {
            if (node.key.equals(key)) {
                return node.value;
            }
        }
        return null;
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    public ArrayList<K> keys() {
        ArrayList<K> list = new ArrayList<>();

        for (LinkedList<Node<K, V>> bucket : table) {
            if (bucket != null) {
                for (Node<K, V> node : bucket) {
                    list.add(node.key);
                }
            }
        }

        return list;
    }


    public int getSize() {return size;}
}

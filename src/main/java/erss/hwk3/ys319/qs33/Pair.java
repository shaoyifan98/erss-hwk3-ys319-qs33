package erss.hwk3.ys319.qs33;

public class Pair<K, V> {
    private final K first;
    private final V second;

    public Pair(K _first, V _second) {
        this.first = _first;
        this.second = _second;
    }

    public K getFirst() {
        return first;
    }

    public V getSecond() {
        return second;
    }
}

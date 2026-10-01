package bench;

public final class Partitioner {

    private Partitioner() {}

    public static int partition(int key, int p) {
        int h = Integer.hashCode(key);
        return ((h % p) + p) % p;
    }
}

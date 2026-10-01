package bench;

public final class RestoreCounter {

    private long value;

    public long incrementBy(int n) {
        long v = value;
        for (int i = 0; i < n; i++) v++;
        value = v;
        return v;
    }
}

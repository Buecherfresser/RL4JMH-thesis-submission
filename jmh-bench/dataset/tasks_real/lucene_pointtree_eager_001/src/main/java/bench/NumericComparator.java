package bench;

/**
 * Wraps a long[] source and exposes {@link #min()} which materialises an
 * internal prefix structure on demand.
 */
public final class NumericComparator {

    private final long[] source;
    private long[] tree;

    public NumericComparator(long[] source) {
        this.source = source;
    }

    public long min() {
        long[] t = tree;
        if (t == null) {
            t = buildTree(source);
            tree = t;
        }
        return t[0];
    }

    static long[] buildTree(long[] data) {
        int n = data.length;
        long[] t = new long[n];
        long min = Long.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            long s = 0L;
            for (int j = 0; j <= i; j++) {
                s += data[j];
            }
            t[i] = s;
            if (data[i] < min) {
                min = data[i];
            }
        }
        if (n > 0) {
            t[0] = min;
        }
        return t;
    }
}

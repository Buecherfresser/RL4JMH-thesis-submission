package bench;

public final class BoxingSum {

    private BoxingSum() {}

    /** Sum the array using a primitive long accumulator. */
    public static long sum(int[] xs) {
        long acc = 0L;
        for (int i = 0, n = xs.length; i < n; i++) {
            acc = acc + xs[i];
        }
        return acc;
    }
}

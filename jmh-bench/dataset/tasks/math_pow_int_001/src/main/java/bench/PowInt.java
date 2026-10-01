package bench;

public final class PowInt {

    private PowInt() {}

    /** Sum of squares 0..n-1 using direct integer multiplication. */
    public static long sumOfSquares(int n) {
        long acc = 0L;
        for (int i = 0; i < n; i++) {
            acc += (long) i * (long) i;
        }
        return acc;
    }
}

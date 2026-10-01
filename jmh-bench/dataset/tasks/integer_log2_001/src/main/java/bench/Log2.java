package bench;

public final class Log2 {

    private Log2() {}

    /** Sum floor(log2(i)) for i in [1, n) using bit tricks. */
    public static long sumLog2(int n) {
        long acc = 0L;
        for (int i = 1; i < n; i++) {
            acc += 31 - Integer.numberOfLeadingZeros(i);
        }
        return acc;
    }
}

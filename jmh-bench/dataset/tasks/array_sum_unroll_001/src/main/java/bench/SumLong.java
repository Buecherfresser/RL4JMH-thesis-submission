package bench;

public final class SumLong {

    private SumLong() {}

    /** Sum every {@code stride}-th element of {@code arr}. */
    public static long sumStride(long[] arr, int stride) {
        long total = 0L;
        for (int i = 0, n = arr.length; i < n; i += stride) {
            total += arr[i];
        }
        return total;
    }
}

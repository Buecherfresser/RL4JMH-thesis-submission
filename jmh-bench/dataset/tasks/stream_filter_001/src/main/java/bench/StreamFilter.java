package bench;

public final class StreamFilter {

    private StreamFilter() {}

    /** Count strictly-greater-than-{@code threshold} elements. */
    public static int countOver(int[] xs, int threshold) {
        int count = 0;
        for (int i = 0, n = xs.length; i < n; i++) {
            if (xs[i] > threshold) {
                count++;
            }
        }
        return count;
    }
}

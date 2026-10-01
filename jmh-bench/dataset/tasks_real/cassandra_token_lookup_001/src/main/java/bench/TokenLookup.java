package bench;

public final class TokenLookup {

    private TokenLookup() {}

    public static int find(long[] tokens, long target) {
        int lo = 0, hi = tokens.length;
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (tokens[mid] < target) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }
        return lo;
    }
}

package bench;

public final class ModExp {

    private ModExp() {}

    /** Returns base^exp mod mod for non-negative {@code exp}. */
    public static long pow(long base, long exp, long mod) {
        long result = 1L;
        long b = base % mod;
        while (exp > 0) {
            if ((exp & 1L) == 1L) {
                result = (result * b) % mod;
            }
            b = (b * b) % mod;
            exp >>>= 1;
        }
        return result;
    }
}

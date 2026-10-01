package bench;

/**
 * Sums a long[] using a single accumulator.
 */
public final class Reducer {

    private Reducer() {}

    public static long reduce(long[] xs, long seed) {
        long acc = seed;
        for (int i = 0; i < xs.length; i++) {
            acc += xs[i];
        }
        return acc;
    }
}

package bench;

public final class Primes {

    private Primes() {}

    /** Count primes below {@code limit} via Sieve of Eratosthenes. */
    public static int count(int limit) {
        if (limit < 3) return limit == 2 ? 1 : 0;
        boolean[] composite = new boolean[limit];
        int count = 0;
        for (int i = 2; i < limit; i++) {
            if (!composite[i]) {
                count++;
                for (long j = (long) i * i; j < limit; j += i) {
                    composite[(int) j] = true;
                }
            }
        }
        return count;
    }
}

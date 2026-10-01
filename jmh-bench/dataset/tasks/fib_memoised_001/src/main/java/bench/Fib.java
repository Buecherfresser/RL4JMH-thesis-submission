package bench;

public final class Fib {

    private Fib() {}

    /** n-th Fibonacci number using an iterative loop. */
    public static long fib(int n) {
        if (n < 2) {
            return n;
        }
        long a = 0L;
        long b = 1L;
        for (int i = 2; i <= n; i++) {
            long next = a + b;
            a = b;
            b = next;
        }
        return b;
    }
}

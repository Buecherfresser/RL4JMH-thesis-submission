package bench;

public final class Meter {

    private Meter() {}

    public static int count(int[] xs) {
        int n = 0;
        for (int i = 0; i < xs.length; i++) {
            if (xs[i] >= 0) n++;
        }
        return n;
    }
}

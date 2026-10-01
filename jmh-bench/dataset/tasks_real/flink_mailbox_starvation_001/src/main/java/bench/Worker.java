package bench;

public final class Worker {

    private Worker() {}

    public static int processAll(int[] xs) {
        int s = 0;
        for (int i = 0; i < xs.length; i++) {
            s += xs[i] * 31;
        }
        return s;
    }
}

package bench;

public final class Emitter {

    private Emitter() {}

    public static int emitAll(int[] src, int[] dst) {
        int n = src.length;
        for (int i = 0; i < n; i++) {
            dst[i] = src[i] * 2 + 1;
        }
        return n;
    }
}

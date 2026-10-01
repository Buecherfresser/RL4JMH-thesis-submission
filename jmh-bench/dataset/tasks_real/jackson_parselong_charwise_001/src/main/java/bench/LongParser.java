package bench;

public final class LongParser {

    private LongParser() {}

    public static long parse(char[] buf, int off, int len) {
        long result = 0;
        boolean neg = false;
        int i = off;
        int end = off + len;
        if (len > 0 && buf[off] == '-') {
            neg = true;
            i++;
        }
        for (; i < end; i++) {
            result = result * 10 + (buf[i] - '0');
        }
        return neg ? -result : result;
    }
}

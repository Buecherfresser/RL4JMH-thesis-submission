package bench;

public final class IntInput {

    private IntInput() {}

    public static int readInt(byte[] buf, int off) {
        return ((buf[off] & 0xff) << 24)
             | ((buf[off + 1] & 0xff) << 16)
             | ((buf[off + 2] & 0xff) << 8)
             | (buf[off + 3] & 0xff);
    }
}

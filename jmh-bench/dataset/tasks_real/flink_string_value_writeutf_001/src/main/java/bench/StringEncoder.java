package bench;

public final class StringEncoder {

    private StringEncoder() {}

    public static byte[] encode(String s) {
        int n = s.length();
        byte[] out = new byte[n * 3];
        int p = 0;
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            if (c < 0x80) {
                out[p++] = (byte) c;
            } else if (c < 0x800) {
                out[p++] = (byte) (0xC0 | (c >> 6));
                out[p++] = (byte) (0x80 | (c & 0x3F));
            } else {
                out[p++] = (byte) (0xE0 | (c >> 12));
                out[p++] = (byte) (0x80 | ((c >> 6) & 0x3F));
                out[p++] = (byte) (0x80 | (c & 0x3F));
            }
        }
        byte[] result = new byte[p];
        System.arraycopy(out, 0, result, 0, p);
        return result;
    }
}

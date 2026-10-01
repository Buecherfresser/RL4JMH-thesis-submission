package bench;

public final class BufferAssembler {

    private BufferAssembler() {}

    public static byte[] assemble(byte[][] slices) {
        int total = 0;
        for (int i = 0; i < slices.length; i++) total += slices[i].length;
        byte[] out = new byte[total];
        int p = 0;
        for (int i = 0; i < slices.length; i++) {
            byte[] s = slices[i];
            System.arraycopy(s, 0, out, p, s.length);
            p += s.length;
        }
        return out;
    }
}

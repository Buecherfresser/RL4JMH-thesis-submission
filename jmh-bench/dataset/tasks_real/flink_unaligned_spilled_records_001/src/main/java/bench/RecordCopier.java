package bench;

public final class RecordCopier {

    private RecordCopier() {}

    public static byte[] copyAll(byte[][] records, int totalLen) {
        byte[] out = new byte[totalLen];
        int p = 0;
        for (int i = 0; i < records.length; i++) {
            byte[] r = records[i];
            System.arraycopy(r, 0, out, p, r.length);
            p += r.length;
        }
        return out;
    }
}

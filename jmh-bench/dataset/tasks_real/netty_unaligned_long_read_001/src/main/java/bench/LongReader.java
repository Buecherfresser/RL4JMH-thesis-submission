package bench;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;

public final class LongReader {

    private static final VarHandle LONG_VH =
        MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.BIG_ENDIAN);

    private LongReader() {}

    public static long readLong(byte[] buf, int off) {
        return (long) LONG_VH.get(buf, off);
    }
}

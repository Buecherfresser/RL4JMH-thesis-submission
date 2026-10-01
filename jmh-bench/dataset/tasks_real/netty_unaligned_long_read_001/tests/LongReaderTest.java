package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class LongReaderTest {
    @Test public void readsZero() {
        assertEquals(0L, LongReader.readLong(new byte[8], 0));
    }
    @Test public void readsOne() {
        byte[] b = new byte[]{0,0,0,0,0,0,0,1};
        assertEquals(1L, LongReader.readLong(b, 0));
    }
    @Test public void readsMinusOne() {
        byte[] b = new byte[]{-1,-1,-1,-1,-1,-1,-1,-1};
        assertEquals(-1L, LongReader.readLong(b, 0));
    }
    @Test public void readsAtOffset() {
        byte[] b = new byte[]{0,0,0,0, 0,0,0,0, 1,2,3,4,5,6,7,8};
        assertEquals(0x0102030405060708L, LongReader.readLong(b, 8));
    }
}

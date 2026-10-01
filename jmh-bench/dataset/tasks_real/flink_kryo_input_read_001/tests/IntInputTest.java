package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class IntInputTest {
    @Test public void readsZero() {
        assertEquals(0, IntInput.readInt(new byte[]{0,0,0,0}, 0));
    }
    @Test public void readsOne() {
        assertEquals(1, IntInput.readInt(new byte[]{0,0,0,1}, 0));
    }
    @Test public void readsMinusOne() {
        assertEquals(-1, IntInput.readInt(new byte[]{-1,-1,-1,-1}, 0));
    }
    @Test public void readsAtOffset() {
        byte[] b = new byte[]{0, 0, 0, 0, 1, 2, 3, 4};
        assertEquals(0x01020304, IntInput.readInt(b, 4));
    }
}

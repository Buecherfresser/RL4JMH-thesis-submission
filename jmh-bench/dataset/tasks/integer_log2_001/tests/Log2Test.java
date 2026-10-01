package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class Log2Test {
    @Test public void below2() { assertEquals(0L, Log2.sumLog2(1)); }
    @Test public void twoTo4() {
        // floor(log2(1))=0, log2(2)=1, log2(3)=1 -> 0+1+1 = 2
        assertEquals(2L, Log2.sumLog2(4));
    }
    @Test public void atPowerOfTwo() {
        // 1..7: 0+1+1+2+2+2+2 = 10
        assertEquals(10L, Log2.sumLog2(8));
    }
}

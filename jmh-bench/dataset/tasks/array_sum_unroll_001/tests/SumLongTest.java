package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class SumLongTest {
    @Test public void strideOne() {
        assertEquals(10L, SumLong.sumStride(new long[]{1, 2, 3, 4}, 1));
    }
    @Test public void strideTwo() {
        assertEquals(4L, SumLong.sumStride(new long[]{1, 2, 3, 4}, 2));
    }
    @Test public void empty() {
        assertEquals(0L, SumLong.sumStride(new long[0], 4));
    }
}

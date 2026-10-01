package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class MeterTest {
    @Test public void emptyArray() {
        assertEquals(0, Meter.count(new int[0]));
    }
    @Test public void allPositive() {
        assertEquals(3, Meter.count(new int[]{1,2,3}));
    }
    @Test public void zeroIsNonNegative() {
        assertEquals(1, Meter.count(new int[]{0}));
    }
    @Test public void mixedSigns() {
        assertEquals(2, Meter.count(new int[]{-1, 0, 1, -2}));
    }
}

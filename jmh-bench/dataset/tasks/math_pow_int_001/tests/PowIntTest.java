package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class PowIntTest {
    @Test public void zero() { assertEquals(0L, PowInt.sumOfSquares(0)); }
    @Test public void one() { assertEquals(0L, PowInt.sumOfSquares(1)); }
    @Test public void four() { assertEquals(0L + 1 + 4 + 9, PowInt.sumOfSquares(4)); }
    @Test public void ten() { assertEquals(285L, PowInt.sumOfSquares(10)); }
}

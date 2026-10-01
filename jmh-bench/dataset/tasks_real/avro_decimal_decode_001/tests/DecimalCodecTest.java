package bench;

import static org.junit.Assert.assertEquals;
import java.math.BigDecimal;
import org.junit.Test;

public class DecimalCodecTest {
    @Test public void zero() {
        assertEquals(new BigDecimal("0.00"), DecimalCodec.decode(new byte[]{0}, 2));
    }
    @Test public void positive() {
        assertEquals(new BigDecimal("1.23"), DecimalCodec.decode(new byte[]{0, 123}, 2));
    }
    @Test public void negative() {
        assertEquals(new BigDecimal("-1.23"), DecimalCodec.decode(new byte[]{-1, -123}, 2));
    }
    @Test public void scaleZero() {
        assertEquals(new BigDecimal("256"), DecimalCodec.decode(new byte[]{1, 0}, 0));
    }
}

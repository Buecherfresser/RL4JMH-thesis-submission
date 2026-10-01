package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ModExpTest {
    @Test public void zeroExp() { assertEquals(1L, ModExp.pow(7L, 0L, 13L)); }
    @Test public void smallCase() { assertEquals(8L, ModExp.pow(2L, 3L, 100L)); }
    @Test public void modulus() { assertEquals(24L, ModExp.pow(2L, 10L, 100L)); /* 1024 % 100 */ }
    @Test public void large() {
        // 5^7 mod 1000 = 78125 mod 1000 = 125
        assertEquals(125L, ModExp.pow(5L, 7L, 1000L));
    }
}

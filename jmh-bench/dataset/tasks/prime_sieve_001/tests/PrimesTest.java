package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class PrimesTest {
    @Test public void limitTwo() { assertEquals(1, Primes.count(2)); /* includes 2 per SUT contract */ }
    @Test public void limitThree() { assertEquals(1, Primes.count(3)); /* only 2 < 3 */ }
    @Test public void below10() { assertEquals(4, Primes.count(10)); /* 2,3,5,7 */ }
    @Test public void below30() { assertEquals(10, Primes.count(30)); }
}

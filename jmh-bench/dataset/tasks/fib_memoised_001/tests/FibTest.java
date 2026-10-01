package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class FibTest {
    @Test public void zero() { assertEquals(0L, Fib.fib(0)); }
    @Test public void one() { assertEquals(1L, Fib.fib(1)); }
    @Test public void ten() { assertEquals(55L, Fib.fib(10)); }
    @Test public void twenty() { assertEquals(6765L, Fib.fib(20)); }
}

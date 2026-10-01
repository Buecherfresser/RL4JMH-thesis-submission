package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class MetricsCounterTest {
    @Test public void startsAtZero() {
        assertEquals(0L, new MetricsCounter().incrementBy(0));
    }
    @Test public void singleIncrement() {
        assertEquals(1L, new MetricsCounter().incrementBy(1));
    }
    @Test public void manyIncrements() {
        assertEquals(100L, new MetricsCounter().incrementBy(100));
    }
    @Test public void accumulatesAcrossCalls() {
        MetricsCounter c = new MetricsCounter();
        c.incrementBy(5);
        assertEquals(15L, c.incrementBy(10));
    }
}

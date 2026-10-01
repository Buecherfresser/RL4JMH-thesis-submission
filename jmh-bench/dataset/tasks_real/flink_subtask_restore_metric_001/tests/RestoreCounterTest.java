package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class RestoreCounterTest {
    @Test public void zeroIncrements() {
        assertEquals(0L, new RestoreCounter().incrementBy(0));
    }
    @Test public void singleIncrement() {
        assertEquals(1L, new RestoreCounter().incrementBy(1));
    }
    @Test public void manyIncrements() {
        assertEquals(1000L, new RestoreCounter().incrementBy(1000));
    }
    @Test public void accumulatesAcrossCalls() {
        RestoreCounter c = new RestoreCounter();
        c.incrementBy(5);
        assertEquals(15L, c.incrementBy(10));
    }
}

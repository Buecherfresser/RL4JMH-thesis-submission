package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class AdvancerTest {
    @Test public void exactMatch() {
        assertEquals(2, Advancer.advance(new int[]{1,3,5,7}, 5));
    }
    @Test public void betweenValues() {
        assertEquals(2, Advancer.advance(new int[]{1,3,5,7}, 4));
    }
    @Test public void beyondEnd() {
        assertEquals(4, Advancer.advance(new int[]{1,3,5,7}, 100));
    }
    @Test public void beforeStart() {
        assertEquals(0, Advancer.advance(new int[]{10, 20}, 5));
    }
    @Test public void emptyArray() {
        assertEquals(0, Advancer.advance(new int[0], 1));
    }
}

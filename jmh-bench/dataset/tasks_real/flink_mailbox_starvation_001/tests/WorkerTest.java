package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class WorkerTest {
    @Test public void emptyArray() {
        assertEquals(0, Worker.processAll(new int[0]));
    }
    @Test public void singleElement() {
        assertEquals(31, Worker.processAll(new int[]{1}));
    }
    @Test public void sumsAll() {
        assertEquals(31 * 6, Worker.processAll(new int[]{1,2,3}));
    }
    @Test public void handlesNegatives() {
        assertEquals(-31, Worker.processAll(new int[]{-1}));
    }
}

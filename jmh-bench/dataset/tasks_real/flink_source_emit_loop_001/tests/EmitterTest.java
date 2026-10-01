package bench;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class EmitterTest {
    @Test public void empty() {
        int[] src = new int[0];
        int[] dst = new int[0];
        assertEquals(0, Emitter.emitAll(src, dst));
    }
    @Test public void singleElement() {
        int[] dst = new int[1];
        Emitter.emitAll(new int[]{3}, dst);
        assertArrayEquals(new int[]{7}, dst);
    }
    @Test public void multipleElements() {
        int[] dst = new int[3];
        Emitter.emitAll(new int[]{0, 1, 2}, dst);
        assertArrayEquals(new int[]{1, 3, 5}, dst);
    }
    @Test public void returnsLength() {
        assertEquals(4, Emitter.emitAll(new int[]{1,1,1,1}, new int[4]));
    }
}

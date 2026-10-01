package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class BoxingSumTest {
    @Test
    public void emptyArray_isZero() {
        assertEquals(0L, BoxingSum.sum(new int[0]));
    }

    @Test
    public void positiveValues() {
        assertEquals(15L, BoxingSum.sum(new int[]{1, 2, 3, 4, 5}));
    }

    @Test
    public void mixedValues() {
        assertEquals(0L, BoxingSum.sum(new int[]{-3, 1, 2}));
    }
}

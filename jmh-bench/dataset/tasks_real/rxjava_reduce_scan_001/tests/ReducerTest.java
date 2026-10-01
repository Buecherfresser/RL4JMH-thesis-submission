package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ReducerTest {
    @Test public void empty() { assertEquals(7L, Reducer.reduce(new long[0], 7L)); }
    @Test public void sums() { assertEquals(10L, Reducer.reduce(new long[]{1, 2, 3, 4}, 0L)); }
    @Test public void withSeed() { assertEquals(15L, Reducer.reduce(new long[]{1, 2, 3, 4}, 5L)); }
    @Test public void negatives() { assertEquals(-3L, Reducer.reduce(new long[]{-1, -2}, 0L)); }
}

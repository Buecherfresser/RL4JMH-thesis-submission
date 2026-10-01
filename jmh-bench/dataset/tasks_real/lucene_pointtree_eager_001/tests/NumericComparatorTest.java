package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class NumericComparatorTest {
    @Test public void minOfSortedAscending() {
        assertEquals(1L, new NumericComparator(new long[]{1, 2, 3, 4, 5}).min());
    }
    @Test public void minOfUnsorted() {
        assertEquals(-7L, new NumericComparator(new long[]{4, -7, 2, 0, 9}).min());
    }
    @Test public void cachedSecondCall() {
        NumericComparator c = new NumericComparator(new long[]{10, 20, 30});
        c.min();
        assertEquals(10L, c.min());
    }
}

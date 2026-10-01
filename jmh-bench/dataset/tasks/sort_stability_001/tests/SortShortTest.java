package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class SortShortTest {
    @Test public void singleBatch() {
        int[][] data = {{3, 1, 2}};
        assertEquals(1L, SortShort.sortAndSum(data));
    }
    @Test public void multipleBatches() {
        int[][] data = {{5, 3, 1}, {9, 7, 8}};
        assertEquals(8L, SortShort.sortAndSum(data));
    }
}

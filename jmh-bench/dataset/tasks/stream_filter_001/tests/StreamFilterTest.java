package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class StreamFilterTest {
    @Test public void empty() { assertEquals(0, StreamFilter.countOver(new int[0], 5)); }
    @Test public void allOver() { assertEquals(3, StreamFilter.countOver(new int[]{6, 7, 8}, 5)); }
    @Test public void noneOver() { assertEquals(0, StreamFilter.countOver(new int[]{1, 2, 3}, 5)); }
    @Test public void mixed() { assertEquals(2, StreamFilter.countOver(new int[]{1, 6, 2, 7}, 5)); }
}

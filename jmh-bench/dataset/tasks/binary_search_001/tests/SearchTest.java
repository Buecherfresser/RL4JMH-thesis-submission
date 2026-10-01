package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class SearchTest {
    @Test public void found() { assertEquals(2, Search.indexOf(new int[]{0, 2, 4, 6, 8}, 4)); }
    @Test public void firstElement() { assertEquals(0, Search.indexOf(new int[]{0, 2, 4}, 0)); }
    @Test public void lastElement() { assertEquals(2, Search.indexOf(new int[]{0, 2, 4}, 4)); }
    @Test public void absent() { assertEquals(-1, Search.indexOf(new int[]{0, 2, 4}, 3)); }
    @Test public void empty() { assertEquals(-1, Search.indexOf(new int[0], 0)); }
}

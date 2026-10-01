package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class DedupTest {
    @Test public void empty() { assertEquals(0, Dedup.countUnique(new int[0])); }
    @Test public void allSame() { assertEquals(1, Dedup.countUnique(new int[]{7, 7, 7})); }
    @Test public void allDistinct() { assertEquals(4, Dedup.countUnique(new int[]{1, 2, 3, 4})); }
    @Test public void someDups() { assertEquals(3, Dedup.countUnique(new int[]{1, 2, 1, 3, 2, 3})); }
}

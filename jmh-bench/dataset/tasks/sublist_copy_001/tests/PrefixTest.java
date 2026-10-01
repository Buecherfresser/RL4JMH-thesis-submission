package bench;

import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import org.junit.Test;

public class PrefixTest {
    @Test public void zeroPrefix() {
        assertEquals(0L, Prefix.sumPrefix(Arrays.asList(1, 2, 3), 0));
    }
    @Test public void fullList() {
        assertEquals(6L, Prefix.sumPrefix(Arrays.asList(1, 2, 3), 3));
    }
    @Test public void partial() {
        assertEquals(3L, Prefix.sumPrefix(Arrays.asList(1, 2, 5, 10), 2));
    }
}

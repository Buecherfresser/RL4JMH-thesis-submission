package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class TokenLookupTest {
    @Test public void exactMatch() {
        assertEquals(2, TokenLookup.find(new long[]{1L, 3L, 5L, 7L}, 5L));
    }
    @Test public void beyondEnd() {
        assertEquals(4, TokenLookup.find(new long[]{1L, 3L, 5L, 7L}, 100L));
    }
    @Test public void betweenValues() {
        assertEquals(3, TokenLookup.find(new long[]{1L, 3L, 5L, 7L}, 6L));
    }
    @Test public void beforeStart() {
        assertEquals(0, TokenLookup.find(new long[]{10L}, 1L));
    }
    @Test public void emptyArray() {
        assertEquals(0, TokenLookup.find(new long[0], 0L));
    }
}

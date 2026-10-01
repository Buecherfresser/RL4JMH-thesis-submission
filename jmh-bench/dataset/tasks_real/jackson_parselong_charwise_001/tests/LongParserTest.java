package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class LongParserTest {
    @Test public void parsesZero() {
        char[] b = "0".toCharArray();
        assertEquals(0L, LongParser.parse(b, 0, 1));
    }
    @Test public void parsesPositive() {
        char[] b = "12345".toCharArray();
        assertEquals(12345L, LongParser.parse(b, 0, 5));
    }
    @Test public void parsesNegative() {
        char[] b = "-987".toCharArray();
        assertEquals(-987L, LongParser.parse(b, 0, 4));
    }
    @Test public void parsesSlice() {
        char[] b = "xx42yy".toCharArray();
        assertEquals(42L, LongParser.parse(b, 2, 2));
    }
}

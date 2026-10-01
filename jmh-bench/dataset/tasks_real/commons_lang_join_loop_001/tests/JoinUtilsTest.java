package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class JoinUtilsTest {
    @Test public void emptyInput() {
        assertEquals("", JoinUtils.join(new String[0], ","));
    }
    @Test public void singleToken() {
        assertEquals("a", JoinUtils.join(new String[]{"a"}, ","));
    }
    @Test public void multipleTokens() {
        assertEquals("a,b,c", JoinUtils.join(new String[]{"a","b","c"}, ","));
    }
    @Test public void multiCharSeparator() {
        assertEquals("a::b", JoinUtils.join(new String[]{"a","b"}, "::"));
    }
}

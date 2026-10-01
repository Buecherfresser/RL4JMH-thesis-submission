package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ReplacerTest {
    @Test public void empty() { assertEquals("", Replacer.stripSpaces("")); }
    @Test public void noSpaces() { assertEquals("abc", Replacer.stripSpaces("abc")); }
    @Test public void allSpaces() { assertEquals("", Replacer.stripSpaces("    ")); }
    @Test public void mixed() { assertEquals("helloworld", Replacer.stripSpaces("hello world")); }
}

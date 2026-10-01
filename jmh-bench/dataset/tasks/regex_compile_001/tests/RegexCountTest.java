package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class RegexCountTest {
    @Test public void emptyString() { assertEquals(0, RegexCount.countMatches("")); }
    @Test public void singleWord() { assertEquals(1, RegexCount.countMatches("hello")); }
    @Test public void threeWords() { assertEquals(3, RegexCount.countMatches("a b c")); }
    @Test public void withPunctuation() { assertEquals(2, RegexCount.countMatches("hi, there!")); }
}

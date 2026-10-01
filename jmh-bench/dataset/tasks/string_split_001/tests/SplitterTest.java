package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class SplitterTest {
    @Test public void empty() { assertEquals(0, Splitter.count("", ',')); }
    @Test public void singleToken() { assertEquals(1, Splitter.count("abc", ',')); }
    @Test public void threeTokens() { assertEquals(3, Splitter.count("a,b,c", ',')); }
    @Test public void trailingSep() { assertEquals(3, Splitter.count("a,b,", ',')); }
}

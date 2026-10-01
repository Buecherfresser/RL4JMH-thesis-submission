package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ConcatenatorTest {
    @Test public void emptyInput() {
        assertEquals("", Concatenator.join(new String[0]));
    }
    @Test public void singletonInput() {
        assertEquals("hello", Concatenator.join(new String[]{"hello"}));
    }
    @Test public void preservesOrder() {
        assertEquals("abc", Concatenator.join(new String[]{"a", "b", "c"}));
    }
    @Test public void allowsEmptyStrings() {
        assertEquals("xy", Concatenator.join(new String[]{"x", "", "y", ""}));
    }
}

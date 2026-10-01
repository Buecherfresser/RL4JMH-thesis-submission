package bench;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;

public class StringJoinerTest {

    @Test
    public void emptyList_returnsEmpty() {
        assertEquals("", StringJoiner.join(Collections.<String>emptyList(), ","));
    }

    @Test
    public void singleElement_noSeparator() {
        assertEquals("a", StringJoiner.join(Arrays.asList("a"), ","));
    }

    @Test
    public void threeElements_commaSeparated() {
        assertEquals("a,b,c", StringJoiner.join(Arrays.asList("a", "b", "c"), ","));
    }
}

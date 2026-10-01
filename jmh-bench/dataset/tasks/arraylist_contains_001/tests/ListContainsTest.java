package bench;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;

public class ListContainsTest {

    @Test
    public void absent_returnsMinusOne() {
        assertEquals(-1, ListContains.firstIndexOf(Arrays.asList(1, 2, 3), 42));
    }

    @Test
    public void emptyList_returnsMinusOne() {
        assertEquals(-1, ListContains.firstIndexOf(Collections.<Integer>emptyList(), 0));
    }

    @Test
    public void hit_returnsFirstIndex() {
        assertEquals(2, ListContains.firstIndexOf(Arrays.asList(1, 2, 3, 3, 4), 3));
    }
}

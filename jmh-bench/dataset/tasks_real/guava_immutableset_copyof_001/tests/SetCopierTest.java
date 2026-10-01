package bench;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import org.junit.Test;

public class SetCopierTest {
    @Test public void empty() {
        assertEquals(0, SetCopier.copyOf(Collections.<Integer>emptyList()).size());
    }
    @Test public void distinct() {
        HashSet<Integer> s = SetCopier.copyOf(Arrays.asList(1, 2, 3, 4));
        assertEquals(4, s.size());
        assertTrue(s.contains(3));
    }
    @Test public void duplicates() {
        HashSet<Integer> s = SetCopier.copyOf(Arrays.asList(1, 1, 2, 2, 3));
        assertEquals(3, s.size());
    }
}

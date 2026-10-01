package bench;

import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;

public class IndexedSumTest {
    @Test public void empty() {
        assertEquals(0L, IndexedSum.sum(new LinkedList<Integer>()));
    }
    @Test public void arrayList() {
        assertEquals(6L, IndexedSum.sum(Arrays.asList(1, 2, 3)));
    }
    @Test public void linkedList() {
        List<Integer> l = new LinkedList<>();
        l.add(10); l.add(20); l.add(30);
        assertEquals(60L, IndexedSum.sum(l));
    }
}

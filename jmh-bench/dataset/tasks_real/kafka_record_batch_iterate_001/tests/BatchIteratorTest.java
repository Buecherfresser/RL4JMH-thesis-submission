package bench;

import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

public class BatchIteratorTest {
    @Test public void emptyOuter() {
        assertEquals(0L, BatchIterator.sum(Collections.<List<Integer>>emptyList()));
    }
    @Test public void emptyInner() {
        assertEquals(0L, BatchIterator.sum(Arrays.asList(Collections.<Integer>emptyList())));
    }
    @Test public void singleBatch() {
        assertEquals(6L, BatchIterator.sum(Arrays.asList(Arrays.asList(1, 2, 3))));
    }
    @Test public void multipleBatches() {
        assertEquals(15L, BatchIterator.sum(Arrays.asList(
            Arrays.asList(1, 2),
            Arrays.asList(3, 4, 5))));
    }
}

package bench;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class PartitionerTest {
    @Test public void zeroKey() {
        assertEquals(0, Partitioner.partition(0, 7));
    }
    @Test public void partitionInRange() {
        int p = Partitioner.partition(123456, 64);
        assertTrue(p >= 0 && p < 64);
    }
    @Test public void negativeKeyInRange() {
        int p = Partitioner.partition(-987654, 32);
        assertTrue(p >= 0 && p < 32);
    }
    @Test public void deterministicForKey() {
        assertEquals(Partitioner.partition(42, 16), Partitioner.partition(42, 16));
    }
}

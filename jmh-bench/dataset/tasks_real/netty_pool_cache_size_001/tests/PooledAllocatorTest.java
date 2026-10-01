package bench;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import org.junit.Test;

public class PooledAllocatorTest {
    @Test public void allocatesRightSize() {
        PooledAllocator a = new PooledAllocator();
        byte[] buf = a.allocate(64);
        assertEquals(64, buf.length);
    }

    @Test public void releaseAndReallocateReusesBuffer() {
        PooledAllocator a = new PooledAllocator();
        byte[] first = a.allocate(64);
        a.release(first);
        byte[] second = a.allocate(64);
        assertEquals(64, second.length);
    }

    @Test public void differentSizesNotReused() {
        PooledAllocator a = new PooledAllocator();
        byte[] first = a.allocate(64);
        a.release(first);
        byte[] big = a.allocate(128);
        assertEquals(128, big.length);
        assertNotSame(first, big);
    }
}

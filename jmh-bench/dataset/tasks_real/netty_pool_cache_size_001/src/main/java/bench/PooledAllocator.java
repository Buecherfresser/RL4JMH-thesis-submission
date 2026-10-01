package bench;

import java.util.Arrays;

/**
 * Tiny size-class buffer pool.
 *
 * <p>Callers obtain a buffer of a given size via {@link #allocate(int)} and
 * return it via {@link #release(byte[])}.
 */
public final class PooledAllocator {

    static final int DEFAULT_CACHE_CAPACITY = 8;

    private final byte[][] cache;

    public PooledAllocator() {
        this(DEFAULT_CACHE_CAPACITY);
    }

    PooledAllocator(int capacity) {
        this.cache = new byte[capacity][];
    }

    public byte[] allocate(int size) {
        for (int i = 0; i < cache.length; i++) {
            byte[] b = cache[i];
            if (b != null && b.length == size) {
                cache[i] = null;
                Arrays.fill(b, (byte) 0);
                return b;
            }
        }
        return new byte[size];
    }

    public void release(byte[] buf) {
        for (int i = 0; i < cache.length; i++) {
            if (cache[i] == null) {
                cache[i] = buf;
                return;
            }
        }
    }
}

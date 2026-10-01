package bench;

import java.util.concurrent.atomic.AtomicLong;

public final class MetricsCounter {

    private final AtomicLong value = new AtomicLong();

    public long incrementBy(int n) {
        long v = 0;
        for (int i = 0; i < n; i++) v = value.incrementAndGet();
        return v;
    }
}

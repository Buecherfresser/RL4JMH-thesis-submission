package bench.generated.c020;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.fastfilter.xor.XorFuse8;
import java.util.SplittableRandom;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class XorFuse8Benchmark {

    private long[] keys;
    private XorFuse8 filter;
    private long presentKey;
    private long absentKey;

    @Setup(Level.Trial)
    public void setUp() {
        SplittableRandom rnd = new SplittableRandom(12345L);
        int size = 100_000;
        keys = new long[size];
        for (int i = 0; i < size; i++) {
            keys[i] = rnd.nextLong();
        }
        filter = XorFuse8.construct(keys);
        presentKey = keys[0];

        // generate a key that is guaranteed not to be in the set
        outer:
        while (true) {
            long candidate = rnd.nextLong();
            for (long k : keys) {
                if (k == candidate) {
                    continue outer;
                }
            }
            absentKey = candidate;
            break;
        }
    }

    @Benchmark
    public XorFuse8 constructFilter() {
        return XorFuse8.construct(keys);
    }

    @Benchmark
    public boolean lookupPresent() {
        return filter.mayContain(presentKey);
    }

    @Benchmark
    public boolean lookupAbsent() {
        return filter.mayContain(absentKey);
    }

    @Benchmark
    public long getBitCount() {
        return filter.getBitCount();
    }
}

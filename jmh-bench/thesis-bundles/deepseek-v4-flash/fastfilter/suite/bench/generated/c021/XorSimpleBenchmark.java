package bench.generated.c021;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import org.fastfilter.xor.XorSimple;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class XorSimpleBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        long[] keys;
        XorSimple filter;
        long[] queryKeys;
        int queryIndex = 0;

        @Setup(Level.Trial)
        public void setup() {
            int n = 10000;
            Random rnd = new Random(12345);
            keys = new long[n];
            for (int i = 0; i < n; i++) {
                keys[i] = rnd.nextLong();
            }

            // Construct filter with retry (XorSimple may throw for some seeds)
            while (true) {
                try {
                    filter = XorSimple.construct(keys);
                    break;
                } catch (ArrayIndexOutOfBoundsException e) {
                    // retry
                }
            }

            // Build query keys: mix of present and absent
            int presentCount = 1000;
            int absentCount = 1000;
            queryKeys = new long[presentCount + absentCount];
            for (int i = 0; i < presentCount; i++) {
                queryKeys[i] = keys[i];
            }
            // Generate absent keys not in the set
            int added = 0;
            while (added < absentCount) {
                long candidate = rnd.nextLong();
                boolean inSet = false;
                for (long k : keys) {
                    if (k == candidate) {
                        inSet = true;
                        break;
                    }
                }
                if (!inSet) {
                    queryKeys[presentCount + added] = candidate;
                    added++;
                }
            }
        }
    }

    @Benchmark
    public XorSimple construct(BenchmarkState state) {
        while (true) {
            try {
                return XorSimple.construct(state.keys);
            } catch (ArrayIndexOutOfBoundsException e) {
                // retry
            }
        }
    }

    @Benchmark
    public boolean mayContain(BenchmarkState state) {
        long key = state.queryKeys[state.queryIndex++ % state.queryKeys.length];
        return state.filter.mayContain(key);
    }
}

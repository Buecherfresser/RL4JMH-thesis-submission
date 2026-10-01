package bench.generated.c009;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import org.fastfilter.cuckoo.Cuckoo8;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Cuckoo8Benchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        long[] keys;
        Cuckoo8 filter;
        long presentKey;
        long absentKey;

        // For insert benchmarks
        Cuckoo8[] insertFilters;
        long[][] insertKeys;
        int[] insertIndices;
        int insertPoolSize;
        int insertKeysPerFilter;
        int nextFilter = 0;

        @Setup(Level.Trial)
        public void setup() {
            int numKeys = 10000;
            Random rnd = new Random(12345);
            keys = new long[numKeys];
            for (int i = 0; i < numKeys; i++) {
                keys[i] = rnd.nextLong();
            }
            filter = Cuckoo8.construct(keys);
            presentKey = keys[0];
            // Generate an absent key
            do {
                absentKey = rnd.nextLong();
            } while (contains(keys, absentKey));

            // Prepare insert pool
            insertPoolSize = 10;
            insertKeysPerFilter = 1000;
            insertFilters = new Cuckoo8[insertPoolSize];
            insertKeys = new long[insertPoolSize][insertKeysPerFilter];
            insertIndices = new int[insertPoolSize];
            for (int f = 0; f < insertPoolSize; f++) {
                insertFilters[f] = new Cuckoo8(insertKeysPerFilter);
                for (int i = 0; i < insertKeysPerFilter; i++) {
                    insertKeys[f][i] = rnd.nextLong();
                }
                insertIndices[f] = 0;
            }
        }

        private boolean contains(long[] arr, long key) {
            for (long k : arr) {
                if (k == key) return true;
            }
            return false;
        }
    }

    @Benchmark
    public Cuckoo8 construct(BenchmarkState state) {
        return Cuckoo8.construct(state.keys);
    }

    @Benchmark
    public boolean mayContainPresent(BenchmarkState state) {
        return state.filter.mayContain(state.presentKey);
    }

    @Benchmark
    public boolean mayContainAbsent(BenchmarkState state) {
        return state.filter.mayContain(state.absentKey);
    }

    @Benchmark
    @Threads(1)
    public void insert(BenchmarkState state, Blackhole bh) {
        int f = state.nextFilter;
        state.nextFilter = (state.nextFilter + 1) % state.insertPoolSize;
        int idx = state.insertIndices[f];
        if (idx >= state.insertKeysPerFilter) {
            // Should not happen with correct invocation count, but guard anyway
            return;
        }
        long key = state.insertKeys[f][idx];
        state.insertIndices[f] = idx + 1;
        state.insertFilters[f].insert(key);
        bh.consume(state.insertFilters[f]);
    }

    @Benchmark
    public long getBitCount(BenchmarkState state) {
        return state.filter.getBitCount();
    }
}

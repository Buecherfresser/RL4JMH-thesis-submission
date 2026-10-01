package bench.generated.c005;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import org.fastfilter.bloom.count.SuccinctCountingBlockedBloomRanked;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SuccinctCountingBlockedBloomRankedBenchmark {

    private int keyCount;
    private int bitsPerKey;
    private long[] keys;

    @Setup(Level.Trial)
    public void generateKeys() {
        keyCount = 16384;
        bitsPerKey = 10;
        Random rnd = new Random(0x12345678L);
        keys = new long[keyCount];
        for (int i = 0; i < keyCount; i++) {
            keys[i] = rnd.nextLong();
        }
    }

    @State(Scope.Benchmark)
    public static class TrialFilterState {
        SuccinctCountingBlockedBloomRanked filter;
        long[] keys;
        int idx;

        @Setup(Level.Trial)
        public void init(SuccinctCountingBlockedBloomRankedBenchmark outer) {
            this.keys = outer.keys;
            this.filter = SuccinctCountingBlockedBloomRanked.construct(this.keys, outer.bitsPerKey);
            this.idx = 0;
        }
    }

    @State(Scope.Benchmark)
    public static class AddState {
        SuccinctCountingBlockedBloomRanked filter;
        long[] keys;
        int idx;

        @Setup(Level.Trial)
        public void init(SuccinctCountingBlockedBloomRankedBenchmark outer) {
            this.keys = outer.keys;
            this.filter = SuccinctCountingBlockedBloomRanked.construct(this.keys, outer.bitsPerKey);
            this.idx = 0;
        }
    }

    @State(Scope.Benchmark)
    public static class RemoveState {
        SuccinctCountingBlockedBloomRanked filter;
        long[] keys;
        int idx;

        @Setup(Level.Iteration)
        public void init(SuccinctCountingBlockedBloomRankedBenchmark outer) {
            this.keys = outer.keys;
            this.filter = SuccinctCountingBlockedBloomRanked.construct(this.keys, outer.bitsPerKey);
            this.idx = 0;
        }
    }

    @Benchmark
    public boolean benchMayContain(TrialFilterState state) {
        long key = state.keys[state.idx];
        state.idx = (state.idx + 1) & (state.keys.length - 1);
        return state.filter.mayContain(key);
    }

    @Benchmark
    public void benchAdd(AddState state, Blackhole bh) {
        long key = state.keys[state.idx];
        state.idx = (state.idx + 1) & (state.keys.length - 1);
        state.filter.add(key);
        bh.consume(state.filter);
    }

    @Benchmark
    public void benchRemove(RemoveState state, Blackhole bh) {
        long key = state.keys[state.idx];
        state.idx = (state.idx + 1) & (state.keys.length - 1);
        state.filter.remove(key);
        bh.consume(state.filter);
    }

    @Benchmark
    public long benchCardinality(TrialFilterState state) {
        return state.filter.cardinality();
    }

    @Benchmark
    public long benchBitCount(TrialFilterState state) {
        return state.filter.getBitCount();
    }

    @Benchmark
    public boolean benchSupportsAdd(TrialFilterState state) {
        return state.filter.supportsAdd();
    }

    @Benchmark
    public boolean benchSupportsRemove(TrialFilterState state) {
        return state.filter.supportsRemove();
    }
}

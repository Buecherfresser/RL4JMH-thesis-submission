package bench.generated.c000;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import org.fastfilter.Filter;
import org.fastfilter.FilterType;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FilterTypeBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        @Param({"16384"})
        public int keyCount;

        public long[] keys;
        public long lookupKey;
        public int setting = 10;

        public Filter bloomFilter;
        public Filter countingBloomFilter;
        public Filter succinctCountingBloomFilter;
        public Filter succinctCountingBloomRankedFilter;
        public Filter blockedBloomFilter;
        public Filter succinctCountingBlockedBloomFilter;
        public Filter succinctCountingBlockedBloomRankedFilter;
        public Filter xorSimpleFilter;
        public Filter xorSimple2Filter;
        public Filter xor8Filter;
        public Filter xor16Filter;
        public Filter xorPlus8Filter;
        public Filter cuckoo8Filter;
        public Filter cuckoo16Filter;
        public Filter cuckooPlus8Filter;
        public Filter cuckooPlus16Filter;
        public Filter gcsFilter;

        @Setup(Level.Trial)
        public void setUp() {
            Random rnd = new Random(12345L);
            keys = new long[keyCount];
            for (int i = 0; i < keyCount; i++) {
                keys[i] = rnd.nextLong();
            }
            lookupKey = keys[keyCount / 2];

            bloomFilter = FilterType.BLOOM.construct(keys, setting);
            countingBloomFilter = FilterType.COUNTING_BLOOM.construct(keys, setting);
            succinctCountingBloomFilter = FilterType.SUCCINCT_COUNTING_BLOOM.construct(keys, setting);
            succinctCountingBloomRankedFilter = FilterType.SUCCINCT_COUNTING_BLOOM_RANKED.construct(keys, setting);
            blockedBloomFilter = FilterType.BLOCKED_BLOOM.construct(keys, setting);
            succinctCountingBlockedBloomFilter = FilterType.SUCCINCT_COUNTING_BLOCKED_BLOOM.construct(keys, setting);
            succinctCountingBlockedBloomRankedFilter = FilterType.SUCCINCT_COUNTING_BLOCKED_BLOOM_RANKED.construct(keys, setting);
            xorSimpleFilter = FilterType.XOR_SIMPLE.construct(keys, setting);
            while (true) {
                try {
                    xorSimple2Filter = FilterType.XOR_SIMPLE_2.construct(keys, setting);
                    break;
                } catch (ArrayIndexOutOfBoundsException e) {
                    // retry
                }
            }
            xor8Filter = FilterType.XOR_8.construct(keys, setting);
            xor16Filter = FilterType.XOR_16.construct(keys, setting);
            xorPlus8Filter = FilterType.XOR_PLUS_8.construct(keys, setting);
            cuckoo8Filter = FilterType.CUCKOO_8.construct(keys, setting);
            cuckoo16Filter = FilterType.CUCKOO_16.construct(keys, setting);
            cuckooPlus8Filter = FilterType.CUCKOO_PLUS_8.construct(keys, setting);
            cuckooPlus16Filter = FilterType.CUCKOO_PLUS_16.construct(keys, setting);
            gcsFilter = FilterType.GCS.construct(keys, setting);
        }
    }

    // Construction benchmarks
    @Benchmark
    public Filter constructBloom(BenchmarkState s) {
        return FilterType.BLOOM.construct(s.keys, s.setting);
    }

    @Benchmark
    public Filter constructCountingBloom(BenchmarkState s) {
        return FilterType.COUNTING_BLOOM.construct(s.keys, s.setting);
    }

    @Benchmark
    public Filter constructSuccinctCountingBloom(BenchmarkState s) {
        return FilterType.SUCCINCT_COUNTING_BLOOM.construct(s.keys, s.setting);
    }

    @Benchmark
    public Filter constructSuccinctCountingBloomRanked(BenchmarkState s) {
        return FilterType.SUCCINCT_COUNTING_BLOOM_RANKED.construct(s.keys, s.setting);
    }

    @Benchmark
    public Filter constructBlockedBloom(BenchmarkState s) {
        return FilterType.BLOCKED_BLOOM.construct(s.keys, s.setting);
    }

    @Benchmark
    public Filter constructSuccinctCountingBlockedBloom(BenchmarkState s) {
        return FilterType.SUCCINCT_COUNTING_BLOCKED_BLOOM.construct(s.keys, s.setting);
    }

    @Benchmark
    public Filter constructSuccinctCountingBlockedBloomRanked(BenchmarkState s) {
        return FilterType.SUCCINCT_COUNTING_BLOCKED_BLOOM_RANKED.construct(s.keys, s.setting);
    }

    @Benchmark
    public Filter constructXorSimple(BenchmarkState s) {
        return FilterType.XOR_SIMPLE.construct(s.keys, s.setting);
    }

    @Benchmark
    public Filter constructXorSimple2(BenchmarkState s) {
        while (true) {
            try {
                return FilterType.XOR_SIMPLE_2.construct(s.keys, s.setting);
            } catch (ArrayIndexOutOfBoundsException e) {
                // retry
            }
        }
    }

    @Benchmark
    public Filter constructXor8(BenchmarkState s) {
        return FilterType.XOR_8.construct(s.keys, s.setting);
    }

    @Benchmark
    public Filter constructXor16(BenchmarkState s) {
        return FilterType.XOR_16.construct(s.keys, s.setting);
    }

    @Benchmark
    public Filter constructXorPlus8(BenchmarkState s) {
        return FilterType.XOR_PLUS_8.construct(s.keys, s.setting);
    }

    @Benchmark
    public Filter constructCuckoo8(BenchmarkState s) {
        return FilterType.CUCKOO_8.construct(s.keys, s.setting);
    }

    @Benchmark
    public Filter constructCuckoo16(BenchmarkState s) {
        return FilterType.CUCKOO_16.construct(s.keys, s.setting);
    }

    @Benchmark
    public Filter constructCuckooPlus8(BenchmarkState s) {
        return FilterType.CUCKOO_PLUS_8.construct(s.keys, s.setting);
    }

    @Benchmark
    public Filter constructCuckooPlus16(BenchmarkState s) {
        return FilterType.CUCKOO_PLUS_16.construct(s.keys, s.setting);
    }

    @Benchmark
    public Filter constructGcs(BenchmarkState s) {
        return FilterType.GCS.construct(s.keys, s.setting);
    }

    // Lookup benchmarks
    @Benchmark
    public boolean lookupBloom(BenchmarkState s) {
        return s.bloomFilter.mayContain(s.lookupKey);
    }

    @Benchmark
    public boolean lookupCountingBloom(BenchmarkState s) {
        return s.countingBloomFilter.mayContain(s.lookupKey);
    }

    @Benchmark
    public boolean lookupSuccinctCountingBloom(BenchmarkState s) {
        return s.succinctCountingBloomFilter.mayContain(s.lookupKey);
    }

    @Benchmark
    public boolean lookupSuccinctCountingBloomRanked(BenchmarkState s) {
        return s.succinctCountingBloomRankedFilter.mayContain(s.lookupKey);
    }

    @Benchmark
    public boolean lookupBlockedBloom(BenchmarkState s) {
        return s.blockedBloomFilter.mayContain(s.lookupKey);
    }

    @Benchmark
    public boolean lookupSuccinctCountingBlockedBloom(BenchmarkState s) {
        return s.succinctCountingBlockedBloomFilter.mayContain(s.lookupKey);
    }

    @Benchmark
    public boolean lookupSuccinctCountingBlockedBloomRanked(BenchmarkState s) {
        return s.succinctCountingBlockedBloomRankedFilter.mayContain(s.lookupKey);
    }

    @Benchmark
    public boolean lookupXorSimple(BenchmarkState s) {
        return s.xorSimpleFilter.mayContain(s.lookupKey);
    }

    @Benchmark
    public boolean lookupXorSimple2(BenchmarkState s) {
        return s.xorSimple2Filter.mayContain(s.lookupKey);
    }

    @Benchmark
    public boolean lookupXor8(BenchmarkState s) {
        return s.xor8Filter.mayContain(s.lookupKey);
    }

    @Benchmark
    public boolean lookupXor16(BenchmarkState s) {
        return s.xor16Filter.mayContain(s.lookupKey);
    }

    @Benchmark
    public boolean lookupXorPlus8(BenchmarkState s) {
        return s.xorPlus8Filter.mayContain(s.lookupKey);
    }

    @Benchmark
    public boolean lookupCuckoo8(BenchmarkState s) {
        return s.cuckoo8Filter.mayContain(s.lookupKey);
    }

    @Benchmark
    public boolean lookupCuckoo16(BenchmarkState s) {
        return s.cuckoo16Filter.mayContain(s.lookupKey);
    }

    @Benchmark
    public boolean lookupCuckooPlus8(BenchmarkState s) {
        return s.cuckooPlus8Filter.mayContain(s.lookupKey);
    }

    @Benchmark
    public boolean lookupCuckooPlus16(BenchmarkState s) {
        return s.cuckooPlus16Filter.mayContain(s.lookupKey);
    }

    @Benchmark
    public boolean lookupGcs(BenchmarkState s) {
        return s.gcsFilter.mayContain(s.lookupKey);
    }
}

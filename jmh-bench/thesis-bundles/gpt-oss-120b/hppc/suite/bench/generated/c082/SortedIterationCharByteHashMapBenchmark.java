package bench.generated.c082;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.SortedIterationCharByteHashMap;
import com.carrotsearch.hppc.CharByteHashMap;
import com.carrotsearch.hppc.comparators.CharComparator;
import com.carrotsearch.hppc.comparators.CharByteComparator;
import com.carrotsearch.hppc.cursors.CharByteCursor;
import com.carrotsearch.hppc.cursors.CharCursor;
import com.carrotsearch.hppc.cursors.ByteCursor;
import com.carrotsearch.hppc.procedures.CharByteProcedure;
import com.carrotsearch.hppc.predicates.CharBytePredicate;
import com.carrotsearch.hppc.procedures.CharProcedure;
import com.carrotsearch.hppc.predicates.CharPredicate;
import com.carrotsearch.hppc.CharCollection;
import com.carrotsearch.hppc.ByteContainer;
import com.carrotsearch.hppc.procedures.ByteProcedure;
import com.carrotsearch.hppc.predicates.BytePredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationCharByteHashMapBenchmark {
    private CharByteHashMap delegate;
    private SortedIterationCharByteHashMap viewByKey;
    private SortedIterationCharByteHashMap viewByKeyValue;

    @Setup(Level.Trial)
    public void setup() {
        int size = 1024;
        delegate = new CharByteHashMap();
        for (int i = 0; i < size; i++) {
            char key = (char) i;
            byte value = (byte) (i & 0xFF);
            delegate.put(key, value);
        }
        CharComparator keyComparator = (a, b) -> Character.compare(a, b);
        CharByteComparator kvComparator = (k1, v1, k2, v2) -> {
            int cmp = Character.compare(k1, k2);
            if (cmp != 0) {
                return cmp;
            }
            return Byte.compare(v1, v2);
        };
        viewByKey = new SortedIterationCharByteHashMap(delegate, keyComparator);
        viewByKeyValue = new SortedIterationCharByteHashMap(delegate, kvComparator);
    }

    @Benchmark
    public int benchmarkSizeKeySorted() {
        return viewByKey.size();
    }

    @Benchmark
    public boolean benchmarkIsEmptyKeySorted() {
        return viewByKey.isEmpty();
    }

    @Benchmark
    public boolean benchmarkContainsKeyKeySorted() {
        return viewByKey.containsKey((char) 123);
    }

    @Benchmark
    public byte benchmarkGetKeySorted() {
        return viewByKey.get((char) 123);
    }

    @Benchmark
    public byte benchmarkGetOrDefaultKeySorted() {
        return viewByKey.getOrDefault((char) 123, (byte) 0);
    }

    @Benchmark
    public void benchmarkIterateKeySorted(Blackhole bh) {
        for (CharByteCursor c : viewByKey) {
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchmarkForEachProcedureKeySorted(Blackhole bh) {
        viewByKey.forEach((CharByteProcedure) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
        });
    }

    @Benchmark
    public void benchmarkForEachPredicateKeySorted(Blackhole bh) {
        viewByKey.forEach((CharBytePredicate) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
            return true;
        });
    }

    @Benchmark
    public CharCollection benchmarkKeysView() {
        return viewByKey.keys();
    }

    @Benchmark
    public ByteContainer benchmarkValuesView() {
        return viewByKey.values();
    }

    @Benchmark
    public int benchmarkIndexGet() {
        return viewByKey.indexGet(0);
    }

    @Benchmark
    public boolean benchmarkIndexExists() {
        return viewByKey.indexExists(0);
    }

    @Benchmark
    public int benchmarkIndexOf() {
        return viewByKey.indexOf((char) 0);
    }
}

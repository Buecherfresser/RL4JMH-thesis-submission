package bench.generated.c089;

import com.carrotsearch.hppc.CharShortHashMap;
import com.carrotsearch.hppc.SortedIterationCharShortHashMap;
import com.carrotsearch.hppc.comparators.CharComparator;
import com.carrotsearch.hppc.comparators.CharShortComparator;
import com.carrotsearch.hppc.cursors.CharShortCursor;
import com.carrotsearch.hppc.procedures.CharShortProcedure;
import com.carrotsearch.hppc.predicates.CharShortPredicate;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationCharShortHashMapBenchmark {

    private static final int MAP_SIZE = 1024;

    private CharShortHashMap delegate;
    private SortedIterationCharShortHashMap sortedView;
    private char existingKey;
    private char missingKey;
    private int existingIndex;

    @Setup(Level.Trial)
    public void setup() {
        Random random = new Random(42L);
        delegate = new CharShortHashMap(MAP_SIZE);
        while (delegate.size() < MAP_SIZE) {
            char key = (char) (random.nextInt(Character.MAX_VALUE - 1) + 1); // 1..65535
            if (!delegate.containsKey(key)) {
                delegate.put(key, (short) random.nextInt(Short.MAX_VALUE));
            }
        }
        existingKey = delegate.keys().toArray()[0];
        missingKey = 0;
        while (delegate.containsKey(missingKey)) {
            missingKey++;
        }

        sortedView = new SortedIterationCharShortHashMap(delegate, (CharComparator) (a, b) -> Character.compare(a, b));
        existingIndex = sortedView.indexOf(existingKey);
    }

    // ---------- Construction ----------

    @Benchmark
    public SortedIterationCharShortHashMap construction() {
        return new SortedIterationCharShortHashMap(delegate, (CharComparator) (a, b) -> Character.compare(a, b));
    }

    // ---------- Basic read operations ----------

    @Benchmark
    public boolean containsKeyHit() {
        return sortedView.containsKey(existingKey);
    }

    @Benchmark
    public boolean containsKeyMiss() {
        return sortedView.containsKey(missingKey);
    }

    @Benchmark
    public short getHit() {
        return sortedView.get(existingKey);
    }

    @Benchmark
    public short getMiss() {
        return sortedView.get(missingKey);
    }

    @Benchmark
    public short getOrDefaultHit() {
        return sortedView.getOrDefault(existingKey, (short) -1);
    }

    @Benchmark
    public short getOrDefaultMiss() {
        return sortedView.getOrDefault(missingKey, (short) -1);
    }

    @Benchmark
    public int size() {
        return sortedView.size();
    }

    @Benchmark
    public boolean isEmpty() {
        return sortedView.isEmpty();
    }

    // ---------- Index-based operations ----------

    @Benchmark
    public int indexOfExisting() {
        return sortedView.indexOf(existingKey);
    }

    @Benchmark
    public boolean indexExistsExisting() {
        return sortedView.indexExists(existingIndex);
    }

    @Benchmark
    public short indexGetExisting() {
        return sortedView.indexGet(existingIndex);
    }

    // ---------- Iteration ----------

    @Benchmark
    public long iterateEntries() {
        long sum = 0;
        for (CharShortCursor cursor : sortedView) {
            sum += cursor.value;
        }
        return sum;
    }

    @Benchmark
    public long forEachProcedure() {
        final long[] sum = {0};
        sortedView.forEach((CharShortProcedure) (key, value) -> sum[0] += value);
        return sum[0];
    }

    @Benchmark
    public long forEachPredicate() {
        final long[] sum = {0};
        sortedView.forEach((CharShortPredicate) (key, value) -> {
            sum[0] += value;
            return true;
        });
        return sum[0];
    }

    // ---------- Key/Value views ----------

    @Benchmark
    public long iterateKeys() {
        long sum = 0;
        for (com.carrotsearch.hppc.cursors.CharCursor cursor : sortedView.keys()) {
            sum += cursor.value;
        }
        return sum;
    }

    @Benchmark
    public long iterateValues() {
        long sum = 0;
        for (com.carrotsearch.hppc.cursors.ShortCursor cursor : sortedView.values()) {
            sum += cursor.value;
        }
        return sum;
    }

    @Benchmark
    public long keysForEach() {
        final long[] sum = {0};
        sortedView.keys().forEach((com.carrotsearch.hppc.procedures.CharProcedure) value -> sum[0] += value);
        return sum[0];
    }

    // ---------- Misc ----------

    @Benchmark
    public int visualizeKeyDistribution() {
        return sortedView.visualizeKeyDistribution(10).length();
    }
}

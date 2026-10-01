package bench.generated.c084;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import com.carrotsearch.hppc.CharDoubleHashMap;
import com.carrotsearch.hppc.SortedIterationCharDoubleHashMap;
import com.carrotsearch.hppc.comparators.CharComparator;
import com.carrotsearch.hppc.cursors.CharDoubleCursor;
import com.carrotsearch.hppc.cursors.CharCursor;
import com.carrotsearch.hppc.cursors.DoubleCursor;
import com.carrotsearch.hppc.procedures.CharDoubleProcedure;
import com.carrotsearch.hppc.predicates.CharDoublePredicate;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationCharDoubleHashMapBenchmark {

    private CharDoubleHashMap delegate;
    private SortedIterationCharDoubleHashMap sortedView;
    private char[] existingKeys;
    private char[] missingKeys;
    private double[] existingValues;
    private int validIndex;
    private int invalidIndex;

    @Setup(Level.Trial)
    public void setup() {
        Random random = new Random(12345);
        delegate = new CharDoubleHashMap();
        int size = 1000;
        for (int i = 0; i < size; i++) {
            char key;
            do {
                key = (char) (random.nextInt(Character.MAX_VALUE - 1) + 1); // avoid 0
            } while (delegate.containsKey(key));
            double value = random.nextDouble();
            delegate.put(key, value);
        }
        sortedView = new SortedIterationCharDoubleHashMap(delegate, (CharComparator) (a, b) -> Character.compare(a, b));

        // Precompute sample keys and values
        int sampleSize = 10;
        existingKeys = new char[sampleSize];
        existingValues = new double[sampleSize];
        int i = 0;
        for (CharDoubleCursor c : delegate) {
            if (i >= sampleSize) break;
            existingKeys[i] = c.key;
            existingValues[i] = c.value;
            i++;
        }
        // Generate missing keys (not in delegate)
        missingKeys = new char[sampleSize];
        for (int j = 0; j < sampleSize; j++) {
            char key;
            do {
                key = (char) (random.nextInt(Character.MAX_VALUE - 1) + 1);
            } while (delegate.containsKey(key));
            missingKeys[j] = key;
        }
        // Precompute a valid index from delegate for an existing key
        validIndex = delegate.indexOf(existingKeys[0]);
        invalidIndex = -1; // or some index not valid
    }

    @Benchmark
    public SortedIterationCharDoubleHashMap construct() {
        return new SortedIterationCharDoubleHashMap(delegate, (CharComparator) (a, b) -> Character.compare(a, b));
    }

    @Benchmark
    public double getExisting() {
        return sortedView.get(existingKeys[0]);
    }

    @Benchmark
    public double getMissing() {
        return sortedView.get(missingKeys[0]);
    }

    @Benchmark
    public double getOrDefaultExisting() {
        return sortedView.getOrDefault(existingKeys[0], -1.0);
    }

    @Benchmark
    public double getOrDefaultMissing() {
        return sortedView.getOrDefault(missingKeys[0], -1.0);
    }

    @Benchmark
    public boolean containsKeyExisting() {
        return sortedView.containsKey(existingKeys[0]);
    }

    @Benchmark
    public boolean containsKeyMissing() {
        return sortedView.containsKey(missingKeys[0]);
    }

    @Benchmark
    public int indexOfExisting() {
        return sortedView.indexOf(existingKeys[0]);
    }

    @Benchmark
    public int indexOfMissing() {
        return sortedView.indexOf(missingKeys[0]);
    }

    @Benchmark
    public double indexGetValid() {
        return sortedView.indexGet(validIndex);
    }

    @Benchmark
    public boolean indexExistsValid() {
        return sortedView.indexExists(validIndex);
    }

    @Benchmark
    public boolean indexExistsInvalid() {
        return sortedView.indexExists(invalidIndex);
    }

    @Benchmark
    public double iterateEntries() {
        double sum = 0;
        for (CharDoubleCursor c : sortedView) {
            sum += c.value;
        }
        return sum;
    }

    @Benchmark
    public double forEachProcedure() {
        double[] sum = {0};
        sortedView.forEach((CharDoubleProcedure) (k, v) -> sum[0] += v);
        return sum[0];
    }

    @Benchmark
    public double forEachPredicateAll() {
        double[] sum = {0};
        sortedView.forEach((CharDoublePredicate) (k, v) -> { sum[0] += v; return true; });
        return sum[0];
    }

    @Benchmark
    public int forEachPredicateEarly() {
        int[] count = {0};
        sortedView.forEach((CharDoublePredicate) (k, v) -> { count[0]++; return count[0] < 10; });
        return count[0];
    }

    @Benchmark
    public int iterateKeys() {
        int sum = 0;
        for (CharCursor c : sortedView.keys()) {
            sum += c.value;
        }
        return sum;
    }

    @Benchmark
    public double iterateValues() {
        double sum = 0;
        for (DoubleCursor c : sortedView.values()) {
            sum += c.value;
        }
        return sum;
    }

    @Benchmark
    public boolean keysContains() {
        return sortedView.keys().contains(existingKeys[0]);
    }

    @Benchmark
    public boolean valuesContains() {
        return sortedView.values().contains(existingValues[0]);
    }

    @Benchmark
    public int size() {
        return sortedView.size();
    }

    @Benchmark
    public boolean isEmpty() {
        return sortedView.isEmpty();
    }
}

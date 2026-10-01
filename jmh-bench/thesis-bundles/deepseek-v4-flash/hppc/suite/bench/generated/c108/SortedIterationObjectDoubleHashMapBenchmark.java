package bench.generated.c108;

import com.carrotsearch.hppc.ObjectDoubleHashMap;
import com.carrotsearch.hppc.SortedIterationObjectDoubleHashMap;
import com.carrotsearch.hppc.comparators.ObjectDoubleComparator;
import com.carrotsearch.hppc.cursors.DoubleCursor;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.carrotsearch.hppc.cursors.ObjectDoubleCursor;
import com.carrotsearch.hppc.predicates.ObjectDoublePredicate;
import com.carrotsearch.hppc.procedures.DoubleProcedure;
import com.carrotsearch.hppc.procedures.ObjectDoubleProcedure;
import com.carrotsearch.hppc.procedures.ObjectProcedure;
import org.openjdk.jmh.annotations.*;

import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationObjectDoubleHashMapBenchmark {

    private ObjectDoubleHashMap<String> delegate;
    private SortedIterationObjectDoubleHashMap<String> view;
    private SortedIterationObjectDoubleHashMap<String> viewByKeyValue;
    private Comparator<String> keyComparator;
    private ObjectDoubleComparator<String> keyValueComparator;
    private String sampleKey;
    private double sampleValue;
    private int sampleIndex;
    private int size;

    @Setup(Level.Trial)
    public void setup() {
        size = 4096;
        Random random = new Random(0x12345678L);
        delegate = new ObjectDoubleHashMap<>(size);

        for (int i = 0; i < size; i++) {
            String key = "key-" + i;
            double value = random.nextDouble();
            delegate.put(key, value);
            if (i == size / 2) {
                sampleKey = key;
                sampleValue = value;
            }
        }

        keyComparator = Comparator.naturalOrder();
        keyValueComparator = new ObjectDoubleComparator<String>() {
            @Override
            public int compare(String key1, double value1, String key2, double value2) {
                int keyOrder = keyComparator.compare(key1, key2);
                if (keyOrder != 0) {
                    return keyOrder;
                }
                return Double.compare(value1, value2);
            }
        };

        view = new SortedIterationObjectDoubleHashMap<>(delegate, keyComparator);
        viewByKeyValue = new SortedIterationObjectDoubleHashMap<>(delegate, keyValueComparator);
        sampleIndex = delegate.indexOf(sampleKey);
    }

    @Benchmark
    public SortedIterationObjectDoubleHashMap<String> constructWithKeyComparator() {
        return new SortedIterationObjectDoubleHashMap<>(delegate, keyComparator);
    }

    @Benchmark
    public SortedIterationObjectDoubleHashMap<String> constructWithKeyValueComparator() {
        return new SortedIterationObjectDoubleHashMap<>(delegate, keyValueComparator);
    }

    @Benchmark
    public double iterateEntries() {
        double sum = 0;
        for (ObjectDoubleCursor<String> cursor : view) {
            sum += cursor.value;
        }
        return sum;
    }

    @Benchmark
    public double forEachProcedure() {
        double[] sum = new double[1];
        view.forEach((ObjectDoubleProcedure<String>) (k, v) -> sum[0] += v);
        return sum[0];
    }

    @Benchmark
    public boolean forEachPredicate() {
        int[] count = new int[1];
        view.forEach((ObjectDoublePredicate<String>) (k, v) -> {
            count[0]++;
            return true;
        });
        return count[0] == size;
    }

    @Benchmark
    public boolean containsKey() {
        return view.containsKey(sampleKey);
    }

    @Benchmark
    public double get() {
        return view.get(sampleKey);
    }

    @Benchmark
    public double getOrDefault() {
        return view.getOrDefault(sampleKey, -1.0);
    }

    @Benchmark
    public int indexOf() {
        return view.indexOf(sampleKey);
    }

    @Benchmark
    public boolean indexExists() {
        return view.indexExists(sampleIndex);
    }

    @Benchmark
    public double indexGet() {
        return view.indexGet(sampleIndex);
    }

    @Benchmark
    public int size() {
        return view.size();
    }

    @Benchmark
    public boolean isEmpty() {
        return view.isEmpty();
    }

    @Benchmark
    public boolean keysContains() {
        return view.keys().contains(sampleKey);
    }

    @Benchmark
    public int keysIterate() {
        int count = 0;
        for (ObjectCursor<String> cursor : view.keys()) {
            count++;
        }
        return count;
    }

    @Benchmark
    public int keysForEach() {
        int[] count = new int[1];
        view.keys().forEach((ObjectProcedure<String>) k -> count[0]++);
        return count[0];
    }

    @Benchmark
    public boolean valuesContains() {
        return view.values().contains(sampleValue);
    }

    @Benchmark
    public int valuesIterate() {
        int count = 0;
        for (DoubleCursor cursor : view.values()) {
            count++;
        }
        return count;
    }

    @Benchmark
    public int valuesForEach() {
        int[] count = new int[1];
        view.values().forEach((DoubleProcedure) v -> count[0]++);
        return count[0];
    }

    @Benchmark
    public String visualizeKeyDistribution() {
        return view.visualizeKeyDistribution(100);
    }
}

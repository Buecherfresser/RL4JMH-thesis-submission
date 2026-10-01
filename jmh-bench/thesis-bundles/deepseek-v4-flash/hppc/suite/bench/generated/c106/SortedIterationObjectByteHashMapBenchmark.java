package bench.generated.c106;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import java.util.Comparator;
import java.util.Random;

import com.carrotsearch.hppc.ObjectByteHashMap;
import com.carrotsearch.hppc.SortedIterationObjectByteHashMap;
import com.carrotsearch.hppc.procedures.ObjectByteProcedure;
import com.carrotsearch.hppc.predicates.ObjectBytePredicate;
import com.carrotsearch.hppc.procedures.ObjectProcedure;
import com.carrotsearch.hppc.procedures.ByteProcedure;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationObjectByteHashMapBenchmark {

    private ObjectByteHashMap<String> delegate;
    private SortedIterationObjectByteHashMap<String> sortedView;
    private Comparator<String> comparator;
    private String existingKey;
    private int existingIndex;

    @Setup(Level.Trial)
    public void setup() {
        delegate = new ObjectByteHashMap<>();
        Random rnd = new Random(12345L);
        for (int i = 0; i < 1000; i++) {
            String key = "key-" + i;
            byte value = (byte) rnd.nextInt(128);
            delegate.put(key, value);
        }

        existingKey = delegate.keys().iterator().next().value;
        comparator = Comparator.naturalOrder();
        sortedView = new SortedIterationObjectByteHashMap<>(delegate, comparator);
        existingIndex = sortedView.indexOf(existingKey);
    }

    @Benchmark
    public SortedIterationObjectByteHashMap<String> construct() {
        return new SortedIterationObjectByteHashMap<>(delegate, comparator);
    }

    @Benchmark
    public byte get() {
        return sortedView.get(existingKey);
    }

    @Benchmark
    public boolean containsKey() {
        return sortedView.containsKey(existingKey);
    }

    @Benchmark
    public int indexOf() {
        return sortedView.indexOf(existingKey);
    }

    @Benchmark
    public byte indexGet() {
        return sortedView.indexGet(existingIndex);
    }

    @Benchmark
    public boolean keysContains() {
        return sortedView.keys().contains(existingKey);
    }

    @Benchmark
    public int size() {
        return sortedView.size();
    }

    @Benchmark
    public boolean isEmpty() {
        return sortedView.isEmpty();
    }

    @Benchmark
    public ObjectByteProcedure<String> forEachProcedure() {
        return sortedView.forEach(new ObjectByteProcedure<String>() {
            @Override
            public void apply(String key, byte value) {
                // no-op
            }
        });
    }

    @Benchmark
    public ObjectBytePredicate<String> forEachPredicate() {
        return sortedView.forEach(new ObjectBytePredicate<String>() {
            @Override
            public boolean apply(String key, byte value) {
                return true;
            }
        });
    }

    @Benchmark
    public ObjectProcedure<String> keysForEach() {
        return sortedView.keys().forEach(new ObjectProcedure<String>() {
            @Override
            public void apply(String value) {
                // no-op
            }
        });
    }

    @Benchmark
    public ByteProcedure valuesForEach() {
        return sortedView.values().forEach(new ByteProcedure() {
            @Override
            public void apply(byte value) {
                // no-op
            }
        });
    }

    @Benchmark
    public String visualizeKeyDistribution() {
        return sortedView.visualizeKeyDistribution(10);
    }
}

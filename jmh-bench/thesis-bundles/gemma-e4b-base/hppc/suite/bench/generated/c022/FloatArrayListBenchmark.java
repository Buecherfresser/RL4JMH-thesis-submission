package bench.generated.c022;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.FloatArrayList;
import com.carrotsearch.hppc.FloatIndexedContainer;
import com.carrotsearch.hppc.predicates.FloatPredicate;
import com.carrotsearch.hppc.procedures.FloatProcedure;
import java.util.Arrays;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FloatArrayListBenchmark {

    private static final int LIST_SIZE = 1000;
    private float[] inputData;
    private FloatArrayList list;
    private float testValue;
    private FloatPredicate predicate;
    private FloatProcedure procedure;

    @Setup(Level.Trial)
    public void setupTrial() {
        // Generate fixed input data once per trial
        inputData = new float[LIST_SIZE];
        Random random = new Random(42);
        for (int i = 0; i < LIST_SIZE; i++) {
            inputData[i] = random.nextFloat();
        }
        
        // Initialize a base list structure
        list = new FloatArrayList(LIST_SIZE);
        for (int i = 0; i < LIST_SIZE; i++) {
            list.add(inputData[i]);
        }
        
        // Setup predicates/procedures
        predicate = new FloatPredicate() {
            @Override
            public boolean apply(float f) {
                return f > 0.5f;
            }
        };
        procedure = new FloatProcedure() {
            @Override
            public void apply(float f) {
                // Simple operation
            }
        };
    }

    @Setup(Level.Iteration)
    public void setupIteration() {
        // Reset the list state for mutable operations to ensure consistent timing
        // We clone the initial state to avoid modifying the state used by other benchmarks
        list = new FloatArrayList(LIST_SIZE);
        for (int i = 0; i < LIST_SIZE; i++) {
            list.add(inputData[i]);
        }
        
        // Set a specific value for search/set operations
        testValue = inputData[LIST_SIZE / 2];
    }

    // --- Read Operations ---

    @Benchmark
    public float getElement() {
        return list.get(LIST_SIZE / 2);
    }

    @Benchmark
    public boolean containsElement() {
        return list.contains(testValue);
    }

    @Benchmark
    public int indexOfElement() {
        return list.indexOf(testValue);
    }

    @Benchmark
    public int lastIndexOfElement() {
        return list.lastIndexOf(testValue);
    }

    // --- Write/Mutation Operations ---

    @Benchmark
    public void addSingleElement() {
        list.add(0.1f);
    }

    @Benchmark
    public void addMultipleElements() {
        list.add(0.1f, 0.2f);
    }

    @Benchmark
    public void addArrayRange() {
        // Add a small range from the input data
        list.add(inputData, 0, 10);
    }

    @Benchmark
    public void insertElement() {
        list.insert(LIST_SIZE / 2, 99.9f);
    }

    @Benchmark
    public float setElement() {
        return list.set(LIST_SIZE / 2, 1.1f);
    }

    @Benchmark
    public float removeAtElement() {
        return list.removeAt(LIST_SIZE / 2);
    }

    @Benchmark
    public float removeLastElement() {
        return list.removeLast();
    }

    @Benchmark
    public void removeRange() {
        list.removeRange(LIST_SIZE / 4, LIST_SIZE / 2);
    }

    // --- Search/Filter Operations ---

    @Benchmark
    public int removeAllByValue() {
        // Remove all occurrences of the test value
        return list.removeAll(testValue);
    }

    @Benchmark
    public int removeAllByPredicate() {
        // Remove all elements matching the predicate
        return list.removeAll(predicate);
    }

    // --- Transformation Operations ---

    @Benchmark
    public FloatIndexedContainer sortList() {
        return list.sort();
    }

    @Benchmark
    public FloatIndexedContainer reverseList() {
        return list.reverse();
    }

    // --- Iteration Operations ---

    @Benchmark
    public void iterateUsingIterator() {
        list.iterator().next(); // Just ensure iteration starts
    }

    @Benchmark
    public void iterateUsingForEachProcedure() {
        list.forEach(procedure);
    }

    @Benchmark
    public void iterateUsingForEachPredicate() {
        list.forEach(predicate);
    }
}

package bench.generated.c022;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import com.carrotsearch.hppc.FloatArrayList;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class FloatArrayListBenchmark {

    @Benchmark
    public void testGet(Blackhole bh) {
        // Create a small, non-empty list for testing read operations
        FloatArrayList list = FloatArrayList.from(1.0f, 2.0f, 3.0f);
        try {
            bh.consume(list.get(0));
        } catch (Exception e) {
            // Ignore exceptions if the list is empty
        }
    }

    @Benchmark
    public void testSize(Blackhole bh) {
        FloatArrayList list = FloatArrayList.from(1.0f);
        bh.consume(list.size());
    }

    @Benchmark
    public void testIsEmpty(Blackhole bh) {
        FloatArrayList list = FloatArrayList.from();
        bh.consume(list.isEmpty());
    }

    @Benchmark
    public void testAddSingle(Blackhole bh) {
        FloatArrayList list = FloatArrayList.from();
        list.add(1.0f);
        bh.consume(list.size());
    }

    @Benchmark
    public void testAddTwo(Blackhole bh) {
        FloatArrayList list = FloatArrayList.from();
        list.add(1.0f, 2.0f);
        bh.consume(list.size());
    }

    @Benchmark
    public void testRemoveLast(Blackhole bh) {
        FloatArrayList list = FloatArrayList.from(1.0f, 2.0f, 3.0f);
        list.removeLast();
        bh.consume(list.size());
    }

    @Benchmark
    public void testRemoveAt(Blackhole bh) {
        FloatArrayList list = FloatArrayList.from(1.0f, 2.0f, 3.0f, 4.0f);
        list.removeAt(1); // Remove 2.0f
        bh.consume(list.size());
    }

    @Benchmark
    public void testContains(Blackhole bh) {
        FloatArrayList list = FloatArrayList.from(1.0f, 2.0f, 3.0f);
        bh.consume(list.contains(2.0f));
    }

    @Benchmark
    public void testIndexOf(Blackhole bh) {
        FloatArrayList list = FloatArrayList.from(1.0f, 2.0f, 3.0f);
        bh.consume(list.indexOf(2.0f));
    }

    @Benchmark
    public void testRemoveAll(Blackhole bh) {
        // Setup a list with duplicates to test removal logic
        FloatArrayList list = FloatArrayList.from(1.0f, 2.0f, 1.0f, 3.0f, 1.0f);
        int removed = list.removeAll(1.0f);
        bh.consume(removed);
        bh.consume(list.size()); // Check if size was updated correctly
    }

    @Benchmark
    public void testClear(Blackhole bh) {
        FloatArrayList list = FloatArrayList.from(1.0f, 2.0f, 3.0f);
        list.clear();
        bh.consume(list.size());
    }

    @Benchmark
    public void testSort(Blackhole bh) {
        // Test sorting performance on a small array
        FloatArrayList list = FloatArrayList.from(3.0f, 1.0f, 2.0f);
        list.sort();
        // Consume the result (the list reference)
        bh.consume(list);
    }

    @Benchmark
    public void testAddBulk(Blackhole bh) {
        // Test adding a large array of elements
        FloatArrayList list = FloatArrayList.from();
        float[] data = {1.0f, 2.0f, 3.0f, 4.0f, 5.0f};
        list.add(data, 0, data.length);
        bh.consume(list.size());
    }
}

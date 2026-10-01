package bench.generated.c019;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.DoubleArrayList;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class DoubleArrayListBenchmark {

    // State field for read-only or reusable structures if needed.
    // Since DoubleArrayList is mutable, we will instantiate it locally or rely on static factories
    // to avoid state pollution between benchmarks.

    @Benchmark
    public void testAddSingle(Blackhole bh) {
        // Create a fresh instance for each benchmark run to avoid state pollution
        DoubleArrayList list = new DoubleArrayList();
        list.add(1.0);
        bh.consume(list);
    }

    @Benchmark
    public void testGet(Blackhole bh) {
        DoubleArrayList list = DoubleArrayList.from(1.0, 2.0, 3.0);
        bh.consume(list.get(1));
    }

    @Benchmark
    public void testSet(Blackhole bh) {
        DoubleArrayList list = DoubleArrayList.from(1.0, 2.0, 3.0);
        list.set(1, 99.9);
        bh.consume(list);
    }

    @Benchmark
    public void testRemoveLast(Blackhole bh) {
        DoubleArrayList list = DoubleArrayList.from(1.0, 2.0, 3.0);
        list.removeLast();
        bh.consume(list);
    }

    @Benchmark
    public void testRemoveAt(Blackhole bh) {
        DoubleArrayList list = DoubleArrayList.from(1.0, 2.0, 3.0, 4.0);
        list.removeAt(1);
        bh.consume(list);
    }

    @Benchmark
    public void testContains(Blackhole bh) {
        DoubleArrayList list = DoubleArrayList.from(1.0, 2.0, 3.0);
        bh.consume(list.contains(2.0));
    }

    @Benchmark
    public void testAddMultiple(Blackhole bh) {
        DoubleArrayList list = new DoubleArrayList();
        list.add(1.0);
        list.add(2.0);
        list.add(3.0);
        bh.consume(list);
    }

    @Benchmark
    public void testBulkAdd(Blackhole bh) {
        // Test add(double[] elements, int start, int length)
        DoubleArrayList list = new DoubleArrayList();
        double[] data = {4.0, 5.0, 6.0};
        list.add(data, 0, data.length);
        bh.consume(list);
    }

    @Benchmark
    public void testSort(Blackhole bh) {
        // Test sort() which modifies the internal buffer
        DoubleArrayList list = DoubleArrayList.from(3.0, 1.0, 2.0);
        list.sort();
        bh.consume(list);
    }

    @Benchmark
    public void testClone(Blackhole bh) {
        // Test clone()
        DoubleArrayList original = DoubleArrayList.from(1.0, 2.0);
        DoubleArrayList clone = original.clone();
        bh.consume(clone);
    }

    @Benchmark
    public void testClear(Blackhole bh) {
        // Test clear()
        DoubleArrayList list = new DoubleArrayList();
        list.add(1.0);
        list.add(2.0);
        list.clear();
        bh.consume(list);
    }
}

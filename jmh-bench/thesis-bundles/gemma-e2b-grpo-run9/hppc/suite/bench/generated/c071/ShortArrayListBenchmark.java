package bench.generated.c071;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.ShortArrayList;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ShortArrayListBenchmark {

    // Since ShortArrayList is mutable, we create instances locally or rely on static factories
    // to avoid state pollution between benchmarks.

    @Benchmark
    public void testSize(Blackhole bh) {
        // Test an empty list
        ShortArrayList list = new ShortArrayList();
        bh.consume(list.size());
    }

    @Benchmark
    public void testGet(Blackhole bh) {
        // Test a list with one element
        ShortArrayList list = ShortArrayList.from((short) 10);
        bh.consume(list.get(0));
    }

    @Benchmark
    public void testContains(Blackhole bh) {
        // Test a list with one element (present)
        ShortArrayList list = ShortArrayList.from((short) 10);
        bh.consume(list.contains((short) 10));
    }

    @Benchmark
    public void testContainsMissing(Blackhole bh) {
        // Test a list with one element (missing)
        ShortArrayList list = ShortArrayList.from((short) 10);
        bh.consume(list.contains((short) 20));
    }

    @Benchmark
    public void testAddSingle(Blackhole bh) {
        // Test adding a single element
        ShortArrayList list = new ShortArrayList();
        list.add((short) 5);
        bh.consume(list.size());
    }

    @Benchmark
    public void testAddTwo(Blackhole bh) {
        // Test adding two elements
        ShortArrayList list = new ShortArrayList();
        list.add((short) 1);
        list.add((short) 2);
        bh.consume(list.size());
    }

    @Benchmark
    public void testSet(Blackhole bh) {
        // Test setting an element (requires a list with at least one element)
        ShortArrayList list = ShortArrayList.from((short) 10);
        list.set(0, (short) 99);
        bh.consume(list.get(0));
    }

    @Benchmark
    public void testRemoveLast(Blackhole bh) {
        // Test removing the last element (requires a list with at least one element)
        ShortArrayList list = ShortArrayList.from((short) 10);
        list.removeLast();
        bh.consume(list.size());
    }

    @Benchmark
    public void testRemoveElement(Blackhole bh) {
        // Test removing an existing element
        ShortArrayList list = ShortArrayList.from((short) 10);
        list.add((short) 20);
        list.removeElement((short) 10);
        bh.consume(list.size());
    }

    @Benchmark
    public void testRemoveNonExistentElement(Blackhole bh) {
        // Test removing a non-existent element (should return -1 index, size unchanged)
        ShortArrayList list = ShortArrayList.from((short) 10);
        list.removeElement((short) 99);
        bh.consume(list.size());
    }

    @Benchmark
    public void testStaticFactory(Blackhole bh) {
        // Test static factory method
        ShortArrayList list = ShortArrayList.from((short) 1);
        bh.consume(list.size());
    }
}

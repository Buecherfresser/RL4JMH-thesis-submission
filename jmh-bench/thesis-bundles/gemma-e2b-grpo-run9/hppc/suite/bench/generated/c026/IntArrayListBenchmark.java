package bench.generated.c026;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.IntArrayList;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IntArrayListBenchmark {

    // State fields for read-only operations (no mutation needed)
    private IntArrayList readOnlyList;

    // State field for mutable operations (will be recreated or reset per benchmark if mutation is involved)
    private IntArrayList mutableList;

    @Setup
    public void setup() {
        // Setup a small, non-empty list for read-only tests
        this.readOnlyList = new IntArrayList(10);
    }

    @Benchmark
    public void testSize(Blackhole bh) {
        // Test size()
        bh.consume(readOnlyList.size());
    }

    @Benchmark
    public void testIsEmpty(Blackhole bh) {
        // Test isEmpty()
        bh.consume(readOnlyList.isEmpty());
    }

    @Benchmark
    public void testGet(Blackhole bh) {
        // Test get(index)
        bh.consume(readOnlyList.get(0));
    }

    @Benchmark
    public void testSet(Blackhole bh) {
        // Test set(index, value)
        // Since we are using a fresh instance or a read-only one, this is safe.
        readOnlyList.set(0, 999);
        bh.consume(readOnlyList.get(0));
    }

    @Benchmark
    public void testClear(Blackhole bh) {
        // Test clear()
        readOnlyList.clear();
        bh.consume(readOnlyList.size());
    }

    @Benchmark
    public void testToArray(Blackhole bh) {
        // Test toArray()
        int[] array = readOnlyList.toArray();
        bh.consume(array.length);
    }

    @Benchmark
    public void testHashCode(Blackhole bh) {
        // Test hashCode()
        bh.consume(readOnlyList.hashCode());
    }

    @Benchmark
    public void testSort(Blackhole bh) {
        // Test sort() - requires a mutable list
        IntArrayList list = new IntArrayList(10);
        // Populate it slightly to ensure sort has work to do
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        list.sort();
        bh.consume(list);
    }

    @Benchmark
    public void testReverse(Blackhole bh) {
        // Test reverse() - requires a mutable list
        IntArrayList list = new IntArrayList(10);
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        list.reverse();
        bh.consume(list);
    }

    @Benchmark
    public void testRemoveLast(Blackhole bh) {
        // Test removeLast() - requires a mutable list
        IntArrayList list = new IntArrayList(10);
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        list.removeLast();
        bh.consume(list);
    }

    @Benchmark
    public void testRemoveAt(Blackhole bh) {
        // Test removeAt(index) - requires a mutable list
        IntArrayList list = new IntArrayList(10);
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        list.removeAt(5);
        bh.consume(list);
    }

    @Benchmark
    public void testRemoveRange(Blackhole bh) {
        // Test removeRange(fromIndex, toIndex) - requires a mutable list
        IntArrayList list = new IntArrayList(10);
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        list.removeRange(2, 5);
        bh.consume(list);
    }

    @Benchmark
    public void testAdd(Blackhole bh) {
        // Test add(int) - requires a mutable list
        IntArrayList list = new IntArrayList(10);
        list.add(1);
        bh.consume(list);
    }

    @Benchmark
    public void testAddTwo(Blackhole bh) {
        // Test add(int, int) - requires a mutable list
        IntArrayList list = new IntArrayList(10);
        list.add(1, 2);
        bh.consume(list);
    }
}

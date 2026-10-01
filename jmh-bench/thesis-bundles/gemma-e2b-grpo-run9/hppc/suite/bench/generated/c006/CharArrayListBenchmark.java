package bench.generated.c006;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import com.carrotsearch.hppc.CharArrayList;
import com.carrotsearch.hppc.CharContainer;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class CharArrayListBenchmark {

    // State field for the subject under test.
    // Since CharArrayList is mutable and resizing occurs, we create a fresh instance
    // in each benchmark method or rely on the JMH harness to manage state isolation
    // if we were using a mutable state field across benchmarks.
    // For simplicity and safety against mutation side effects, we instantiate locally
    // or rely on the fact that JMH isolates state per benchmark method if the state
    // is not explicitly shared across methods (which is the default behavior for @State).

    private CharArrayList list;

    @Setup
    public void setup() {
        // Initialize a baseline list. Since we are benchmarking various operations,
        // we don't need a massive setup, just a functional instance.
        this.list = new CharArrayList(100);
    }

    @Benchmark
    public void testSize(Blackhole bh) {
        // Read operation
        bh.consume(list.size());
    }

    @Benchmark
    public void testIsEmpty(Blackhole bh) {
        // Read operation
        bh.consume(list.isEmpty());
    }

    @Benchmark
    public void testGet(Blackhole bh) {
        // Read operation (assuming list is not empty, which it isn't after setup)
        bh.consume(list.get(0));
    }

    @Benchmark
    public void testContains(Blackhole bh) {
        // Read operation
        bh.consume(list.contains('a'));
    }

    @Benchmark
    public void testAddSingleChar(Blackhole bh) {
        // Mutating operation (testing add(char))
        list.add('x');
        bh.consume(list.size());
    }

    @Benchmark
    public void testSet(Blackhole bh) {
        // Mutating operation (testing set(int, char))
        list.set(0, 'z');
        bh.consume(list.get(0));
    }

    @Benchmark
    public void testRemoveLast(Blackhole bh) {
        // Mutating operation (testing removeLast())
        list.removeLast();
        bh.consume(list.size());
    }

    @Benchmark
    public void testRemoveAt(Blackhole bh) {
        // Mutating operation (testing removeAt(int))
        // We must ensure the index is valid, relying on the setup size (100)
        list.removeAt(0);
        bh.consume(list.size());
    }

    @Benchmark
    public void testSort(Blackhole bh) {
        // Mutating operation (testing sort())
        // Note: This operation modifies the internal buffer.
        list.add('a');
        list.add('b');
        list.add('c');
        list.sort();
        bh.consume(list.size());
    }

    @Benchmark
    public void testReverse(Blackhole bh) {
        // Mutating operation (testing reverse())
        list.add('a');
        list.add('b');
        list.add('c');
        list.reverse();
        bh.consume(list.get(0)); // Check if the first element was swapped
    }

    @Benchmark
    public void testClear(Blackhole bh) {
        // Mutating operation (testing clear())
        list.add('a');
        list.add('b');
        list.clear();
        bh.consume(list.isEmpty());
    }

    @Benchmark
    public void testToArray(Blackhole bh) {
        // Read/Copy operation (testing toArray())
        // This operation creates a new array, which is a good test case.
        bh.consume(list.toArray());
    }
}

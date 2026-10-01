package bench.generated.c003;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.ByteArrayList;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class ByteArrayListBenchmark {

    private ByteArrayList list;

    @Setup
    public void setup() {
        // Initialize a base list.
        this.list = new ByteArrayList(100);
    }

    @Benchmark
    public void testAddSingleElement(Blackhole bh) {
        // Test basic single element addition
        list.add((byte) 0xAA);
        bh.consume(list.size());
    }

    @Benchmark
    public void testGetElement(Blackhole bh) {
        // Test reading an element (requires list to be populated)
        list.add((byte) 0xBB);
        bh.consume(list.get(0));
    }

    @Benchmark
    public void testBulkAdd(Blackhole bh) {
        // Test adding a range of elements
        byte[] data = new byte[100];
        Arrays.fill(data, (byte) 0xCC);
        list.add(data, 0, data.length);
        bh.consume(list.size());
    }

    @Benchmark
    public void testContainsElement(Blackhole bh) {
        // Test contains check on a populated list
        list.add((byte) 0x11);
        list.add((byte) 0x22);
        bh.consume(list.contains((byte) 0x11));
    }

    @Benchmark
    public void testIndexOfElement(Blackhole bh) {
        // Test index lookup on a populated list
        list.add((byte) 0x11);
        list.add((byte) 0x22);
        bh.consume(list.indexOf((byte) 0x11));
    }

    @Benchmark
    public void testRemoveLast(Blackhole bh) {
        // Test removal of the last element
        list.add((byte) 0xAA);
        bh.consume(list.removeLast());
    }

    @Benchmark
    public void testRemoveAtMiddle(Blackhole bh) {
        // Test removal from the middle (requires list to be populated)
        list.add((byte) 0xAA);
        list.add((byte) 0xBB);
        list.add((byte) 0xCC);
        bh.consume(list.removeAt(1));
    }

    @Benchmark
    public void testRemoveAll(Blackhole bh) {
        // Test removal of all elements matching a predicate
        list.add((byte) 0x11);
        list.add((byte) 0x22);
        bh.consume(list.removeAll(b -> b == (byte) 0x11));
    }

    @Benchmark
    public void testIteration(Blackhole bh) {
        // Test iteration via iterator (read-only operation)
        list.add((byte) 0xAA);
        try {
            list.iterator().next();
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(list.size());
    }

    @Benchmark
    public void testClone(Blackhole bh) {
        // Test cloning operation
        try {
            ByteArrayList cloned = list.clone();
            bh.consume(cloned.size());
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}

package bench.generated.c074;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.concurrent.TimeUnit;
import jodd.util.CollectionUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CollectionUtilBenchmark {

    private List<String> inputList;
    private Iterator<String> inputIterator;
    private Enumeration<String> inputEnumeration;

    @Setup
    public void setup() {
        // Create a reasonably sized input payload
        inputList = new ArrayList<>(1000);
        for (int i = 0; i < 1000; i++) {
            inputList.add("TestString_" + i);
        }
        inputIterator = inputList.iterator();
        inputEnumeration = Collections.enumeration(inputList);
    }

    @Benchmark
    public void testAsEnumeration_fromIterator(Blackhole bh) {
        // Input: Iterator
        Enumeration<String> result = CollectionUtil.asEnumeration(inputIterator);
        bh.consume(result);
    }

    @Benchmark
    public void testAsIterator_fromEnumeration(Blackhole bh) {
        // Input: Enumeration
        Iterator<String> result = CollectionUtil.asIterator(inputEnumeration);
        bh.consume(result);
    }

    @Benchmark
    public void testCollectionOf_fromIterator(Blackhole bh) {
        // Input: Iterator
        Collection<String> result = CollectionUtil.collectionOf(inputIterator);
        bh.consume(result);
    }

    @Benchmark
    public void testStreamOf_fromIterator_nonParallel(Blackhole bh) {
        // Input: Iterator
        Stream<String> result = CollectionUtil.streamOf(inputIterator);
        // Consume the stream to ensure processing happens
        bh.consume(result.toList());
    }

    @Benchmark
    public void testStreamOf_fromIterable_nonParallel(Blackhole bh) {
        // Input: Iterable (the list itself)
        Stream<String> result = CollectionUtil.streamOf(inputList);
        // Consume the stream
        bh.consume(result.toList());
    }

    @Benchmark
    public void testParallelStreamOf_fromIterator(Blackhole bh) {
        // Input: Iterator
        Stream<String> result = CollectionUtil.parallelStreamOf(inputIterator);
        // Consume the stream
        bh.consume(result.toList());
    }

    @Benchmark
    public void testParallelStreamOf_fromIterable(Blackhole bh) {
        // Input: Iterable (the list itself)
        Stream<String> result = CollectionUtil.parallelStreamOf(inputList);
        // Consume the stream
        bh.consume(result.toList());
    }
}

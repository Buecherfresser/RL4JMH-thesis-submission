package bench.generated.c074;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.concurrent.TimeUnit;
import jodd.util.CollectionUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 2)
@Measurement(iterations = 5, time = 1)
public class CollectionUtilBenchmark {

    // State for testing collectionOf
    private Iterator<Integer> integerIterator;
    private int collectionOfSize;

    @Setup
    public void setup() {
        // Setup a large iterator for collectionOf testing
        List<Integer> initialList = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            initialList.add(i);
        }
        this.integerIterator = initialList.iterator();
        this.collectionOfSize = initialList.size();
    }

    @Benchmark
    public void benchmarkCollectionOf(Blackhole bh) {
        Collection<Integer> result = CollectionUtil.collectionOf(integerIterator);
        bh.consume(result);
    }

    // State for testing streamOf (Iterator)
    private Iterator<String> stringIterator;

    @Setup
    public void setupStringIterator() {
        // Setup a large iterator for streamOf(Iterator) testing
        List<String> initialList = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            initialList.add("item_" + i);
        }
        this.stringIterator = initialList.iterator();
    }

    @Benchmark
    public void benchmarkStreamOfIterator(Blackhole bh) {
        Stream<String> result = CollectionUtil.streamOf(stringIterator);
        bh.consume(result);
    }

    // State for testing streamOf (Iterable)
    private Iterable<Double> doubleIterable;

    @Setup
    public void setupDoubleIterable() {
        // Setup a large iterable for streamOf(Iterable) testing
        List<Double> initialList = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            initialList.add((double) i);
        }
        this.doubleIterable = initialList;
    }

    @Benchmark
    public void benchmarkStreamOfIterable(Blackhole bh) {
        Stream<Double> result = CollectionUtil.streamOf(doubleIterable);
        bh.consume(result);
    }

    // State for testing parallelStreamOf (Iterator)
    private Iterator<Boolean> booleanIterator;

    @Setup
    public void setupBooleanIterator() {
        // Setup a large iterator for parallelStreamOf(Iterator) testing
        List<Boolean> initialList = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            initialList.add(i % 2 == 0);
        }
        this.booleanIterator = initialList.iterator();
    }

    @Benchmark
    public void benchmarkParallelStreamOfIterator(Blackhole bh) {
        Stream<Boolean> result = CollectionUtil.parallelStreamOf(booleanIterator);
        bh.consume(result);
    }

    // State for testing parallelStreamOf (Iterable)
    private Iterable<String> stringIterable;

    @Setup
    public void setupStringIterable() {
        // Setup a large iterable for parallelStreamOf(Iterable) testing
        List<String> initialList = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            initialList.add("data_" + i);
        }
        this.stringIterable = initialList;
    }

    @Benchmark
    public void benchmarkParallelStreamOfIterable(Blackhole bh) {
        Stream<String> result = CollectionUtil.parallelStreamOf(stringIterable);
        bh.consume(result);
    }

    // State for testing asEnumeration
    private Iterator<Character> charIterator;

    @Setup
    public void setupCharIterator() {
        // Setup a large iterator for asEnumeration testing
        List<Character> initialList = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            initialList.add((char) ('a' + i));
        }
        this.charIterator = initialList.iterator();
    }

    @Benchmark
    public void benchmarkAsEnumeration(Blackhole bh) {
        Enumeration<Character> result = CollectionUtil.asEnumeration(charIterator);
        bh.consume(result);
    }
}

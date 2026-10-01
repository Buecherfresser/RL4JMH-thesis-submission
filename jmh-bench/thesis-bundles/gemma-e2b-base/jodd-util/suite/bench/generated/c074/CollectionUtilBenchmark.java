package bench.generated.c074;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.util.CollectionUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 2)
@Measurement(iterations = 5, time = 2)
public class CollectionUtilBenchmark {

    // State for read-only operations (Iterator/Enumeration conversion)
    private Iterator<Integer> integerIterator;
    private Enumeration<Integer> integerEnumeration;

    // State for collectionOf (Iterator to Collection)
    private Iterator<String> stringIterator;

    // State for streamOf (non-parallel)
    private Iterator<Integer> intStreamIterator;
    private Iterable<String> stringIterable;

    // State for parallelStreamOf (Iterator to Stream)
    private Iterator<Integer> intParallelIterator;
    private Iterable<String> stringParallelIterable;

    @Setup
    public void setup() {
        // Setup for Iterator/Enumeration conversions
        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        this.integerIterator = numbers.iterator();
        this.integerEnumeration = CollectionUtil.asEnumeration(this.integerIterator);

        // Setup for collectionOf
        List<String> strings = List.of("a", "b", "c", "d", "e", "f", "g", "h", "i", "j");
        this.stringIterator = strings.iterator();

        // Setup for streamOf (non-parallel)
        this.intStreamIterator = integerIterator;
        this.stringIterable = strings;

        // Setup for parallelStreamOf
        this.intParallelIterator = integerIterator;
        this.stringParallelIterable = strings;
    }

    @Benchmark
    public void benchmarkAsEnumeration(Blackhole bh) {
        Enumeration<Integer> enumeration = CollectionUtil.asEnumeration(integerIterator);
        bh.consume(enumeration);
    }

    @Benchmark
    public void benchmarkAsIterator(Blackhole bh) {
        Iterator<Integer> iterator = CollectionUtil.asIterator(integerEnumeration);
        bh.consume(iterator);
    }

    @Benchmark
    public void benchmarkCollectionOf(Blackhole bh) {
        Collection<String> collection = CollectionUtil.collectionOf(stringIterator);
        bh.consume(collection);
    }

    @Benchmark
    public void benchmarkStreamOfNonParallel(Blackhole bh) {
        Stream<Integer> stream = CollectionUtil.streamOf(intStreamIterator);
        bh.consume(stream);
    }

    @Benchmark
    public void benchmarkStreamOfIterableNonParallel(Blackhole bh) {
        Stream<String> stream = CollectionUtil.streamOf(stringIterable);
        bh.consume(stream);
    }

    @Benchmark
    public void benchmarkParallelStreamOfIterator(Blackhole bh) {
        Stream<Integer> stream = CollectionUtil.parallelStreamOf(intParallelIterator);
        bh.consume(stream);
    }

    @Benchmark
    public void benchmarkParallelStreamOfIterable(Blackhole bh) {
        Stream<String> stream = CollectionUtil.parallelStreamOf(stringParallelIterable);
        bh.consume(stream);
    }
}

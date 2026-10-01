package bench.generated.c074;

import org.openjdk.jmh.annotations.*;
import java.util.*;
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
    private List<Integer> data;

    @Setup(Level.Trial)
    public void setUp() {
        data = new ArrayList<>(1000);
        for (int i = 0; i < 1000; i++) {
            data.add(i);
        }
    }

    @Benchmark
    public Enumeration<Integer> asEnumeration() {
        return CollectionUtil.asEnumeration(data.iterator());
    }

    @Benchmark
    public Iterator<Integer> asIterator() {
        Enumeration<Integer> enumeration = CollectionUtil.asEnumeration(data.iterator());
        return CollectionUtil.asIterator(enumeration);
    }

    @Benchmark
    public Collection<Integer> collectionOf() {
        return CollectionUtil.collectionOf(data.iterator());
    }

    @Benchmark
    public long streamOfIterator() {
        Stream<Integer> stream = CollectionUtil.streamOf(data.iterator());
        return stream.count();
    }

    @Benchmark
    public long streamOfIterable() {
        Stream<Integer> stream = CollectionUtil.streamOf(data);
        return stream.count();
    }

    @Benchmark
    public long parallelStreamOfIterator() {
        Stream<Integer> stream = CollectionUtil.parallelStreamOf(data.iterator());
        return stream.count();
    }

    @Benchmark
    public long parallelStreamOfIterable() {
        Stream<Integer> stream = CollectionUtil.parallelStreamOf(data);
        return stream.count();
    }
}

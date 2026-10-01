package bench.generated.c074;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.util.CollectionUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.Collections;
import java.util.Collection;
import java.util.stream.Stream;

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
    public Enumeration<Integer> benchAsEnumeration() {
        Iterator<Integer> it = data.iterator();
        return CollectionUtil.asEnumeration(it);
    }

    @Benchmark
    public void benchAsIterator(Blackhole bh) {
        Enumeration<Integer> en = Collections.enumeration(data);
        Iterator<Integer> it = CollectionUtil.asIterator(en);
        bh.consume(it);
    }

    @Benchmark
    public Collection<Integer> benchCollectionOf() {
        Iterator<Integer> it = data.iterator();
        return CollectionUtil.collectionOf(it);
    }

    @Benchmark
    public long benchStreamOfIterator() {
        Iterator<Integer> it = data.iterator();
        Stream<Integer> stream = CollectionUtil.streamOf(it);
        return stream.count();
    }

    @Benchmark
    public long benchStreamOfIterable() {
        Stream<Integer> stream = CollectionUtil.streamOf(data);
        return stream.count();
    }

    @Benchmark
    public long benchParallelStreamOfIterator() {
        Iterator<Integer> it = data.iterator();
        Stream<Integer> stream = CollectionUtil.parallelStreamOf(it);
        return stream.count();
    }

    @Benchmark
    public long benchParallelStreamOfIterable() {
        Stream<Integer> stream = CollectionUtil.parallelStreamOf(data);
        return stream.count();
    }
}

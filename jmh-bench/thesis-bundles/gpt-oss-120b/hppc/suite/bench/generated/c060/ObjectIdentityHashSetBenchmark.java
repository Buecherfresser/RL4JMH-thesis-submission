package bench.generated.c060;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ObjectIdentityHashSet;
import java.util.Random;
import java.util.Arrays;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectIdentityHashSetBenchmark {

    private ObjectIdentityHashSet<Object> set;
    private Object[] existingElements;
    private Object[] newElements;
    private int idx;

    @Setup(Level.Trial)
    public void init() {
        int size = 1024;
        Random rnd = new Random(12345L);
        existingElements = new Object[size];
        for (int i = 0; i < size; i++) {
            existingElements[i] = new Object();
        }
        set = new ObjectIdentityHashSet<>(size);
        set.addAll(Arrays.asList(existingElements));

        newElements = new Object[size];
        for (int i = 0; i < size; i++) {
            newElements[i] = new Object();
        }
        idx = 0;
    }

    @Benchmark
    public boolean contains() {
        Object key = existingElements[idx];
        idx = (idx + 1) & (existingElements.length - 1);
        return set.contains(key);
    }

    @Benchmark
    public int size() {
        return set.size();
    }

    @Benchmark
    public void iterate(Blackhole bh) {
        for (Object o : set) {
            bh.consume(o);
        }
    }

    @Benchmark
    public int toArray(Blackhole bh) {
        Object[] arr = set.toArray();
        bh.consume(arr);
        return arr.length;
    }

    @Benchmark
    public void ensureCapacity(Blackhole bh) {
        set.ensureCapacity(set.size() + 1000);
        bh.consume(set.size());
    }

    @Benchmark
    public ObjectIdentityHashSet<Object> addAll() {
        ObjectIdentityHashSet<Object> temp = new ObjectIdentityHashSet<>(newElements.length);
        temp.addAll(Arrays.asList(newElements));
        return temp;
    }

    @Benchmark
    public boolean remove() {
        ObjectIdentityHashSet<Object> temp = new ObjectIdentityHashSet<>(1);
        Object o = new Object();
        temp.add(o);
        return temp.remove(o);
    }

    @Benchmark
    public int clear() {
        ObjectIdentityHashSet<Object> temp = ObjectIdentityHashSet.from(existingElements);
        temp.clear();
        return temp.size();
    }
}

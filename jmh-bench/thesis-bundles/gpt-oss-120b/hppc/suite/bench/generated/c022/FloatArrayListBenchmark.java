package bench.generated.c022;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.FloatArrayList;
import com.carrotsearch.hppc.procedures.FloatProcedure;
import com.carrotsearch.hppc.predicates.FloatPredicate;
import com.carrotsearch.hppc.cursors.FloatCursor;
import java.util.Iterator;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FloatArrayListBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        FloatArrayList list;
        int randomIndex;
        float randomValue;

        @Setup(Level.Trial)
        public void setup() {
            Random rnd = new Random(0);
            int size = 1024;
            list = new FloatArrayList(size);
            for (int i = 0; i < size; i++) {
                list.add(rnd.nextFloat());
            }
            randomIndex = rnd.nextInt(size);
            randomValue = list.get(randomIndex);
        }
    }

    @Benchmark
    public float benchmarkGet(BenchmarkState s) {
        return s.list.get(s.randomIndex);
    }

    @Benchmark
    public float benchmarkSet(BenchmarkState s) {
        return s.list.set(s.randomIndex, s.randomValue + 1.0f);
    }

    @Benchmark
    public boolean benchmarkContains(BenchmarkState s) {
        return s.list.contains(s.randomValue);
    }

    @Benchmark
    public int benchmarkIndexOf(BenchmarkState s) {
        return s.list.indexOf(s.randomValue);
    }

    @Benchmark
    public int benchmarkLastIndexOf(BenchmarkState s) {
        return s.list.lastIndexOf(s.randomValue);
    }

    @Benchmark
    public void benchmarkIterate(BenchmarkState s, Blackhole bh) {
        Iterator<FloatCursor> it = s.list.iterator();
        while (it.hasNext()) {
            bh.consume(it.next().value);
        }
    }

    @Benchmark
    public void benchmarkForEachProcedure(BenchmarkState s, Blackhole bh) {
        s.list.forEach((FloatProcedure) v -> bh.consume(v));
    }

    @Benchmark
    public void benchmarkForEachPredicate(BenchmarkState s, Blackhole bh) {
        s.list.forEach((FloatPredicate) v -> {
            bh.consume(v);
            return true;
        });
    }

    @Benchmark
    public FloatArrayList benchmarkSort(BenchmarkState s) {
        return (FloatArrayList) s.list.sort();
    }

    @Benchmark
    public FloatArrayList benchmarkReverse(BenchmarkState s) {
        return (FloatArrayList) s.list.reverse();
    }

    @Benchmark
    public float[] benchmarkToArray(BenchmarkState s) {
        return s.list.toArray();
    }

    @Benchmark
    public FloatArrayList benchmarkClone(BenchmarkState s) {
        return s.list.clone();
    }

    @Benchmark
    public int benchmarkHashCode(BenchmarkState s) {
        return s.list.hashCode();
    }

    @Benchmark
    public boolean benchmarkEqualsSelf(BenchmarkState s) {
        return s.list.equals(s.list);
    }
}

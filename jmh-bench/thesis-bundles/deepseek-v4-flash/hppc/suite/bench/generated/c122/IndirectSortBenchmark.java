package bench.generated.c122;

import com.carrotsearch.hppc.sorting.IndirectSort;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.IntBinaryOperator;
import org.openjdk.jmh.annotations.*;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IndirectSortBenchmark {

    @State(Scope.Benchmark)
    public static class SmallData {
        int[] data;
        int[] order;
        Integer[] boxed;

        @Setup(Level.Trial)
        public void setup() {
            int n = 32;
            Random rnd = new Random(12345);
            data = new int[n];
            order = new int[n];
            boxed = new Integer[n];
            for (int i = 0; i < n; i++) {
                data[i] = rnd.nextInt();
                order[i] = i;
                boxed[i] = data[i];
            }
        }
    }

    @State(Scope.Benchmark)
    public static class LargeData {
        int[] data;
        int[] order;
        Integer[] boxed;

        @Setup(Level.Trial)
        public void setup() {
            int n = 1000;
            Random rnd = new Random(67890);
            data = new int[n];
            order = new int[n];
            boxed = new Integer[n];
            for (int i = 0; i < n; i++) {
                data[i] = rnd.nextInt();
                order[i] = i;
                boxed[i] = data[i];
            }
        }
    }

    private static IntBinaryOperator comparatorFrom(int[] data) {
        return (a, b) -> Integer.compare(data[a], data[b]);
    }

    @Benchmark
    public int[] mergesortStartLengthSmall(SmallData sd) {
        return IndirectSort.mergesort(0, sd.data.length, comparatorFrom(sd.data));
    }

    @Benchmark
    public int[] mergesortStartLengthLarge(LargeData ld) {
        return IndirectSort.mergesort(0, ld.data.length, comparatorFrom(ld.data));
    }

    @Benchmark
    public int[] mergesortOrderArraySmall(SmallData sd) {
        return IndirectSort.mergesort(sd.order, comparatorFrom(sd.data));
    }

    @Benchmark
    public int[] mergesortOrderArrayLarge(LargeData ld) {
        return IndirectSort.mergesort(ld.order, comparatorFrom(ld.data));
    }

    @Benchmark
    public int[] mergesortGenericSmall(SmallData sd) {
        return IndirectSort.mergesort(sd.boxed, 0, sd.boxed.length, Comparator.naturalOrder());
    }

    @Benchmark
    public int[] mergesortGenericLarge(LargeData ld) {
        return IndirectSort.mergesort(ld.boxed, 0, ld.boxed.length, Comparator.naturalOrder());
    }
}

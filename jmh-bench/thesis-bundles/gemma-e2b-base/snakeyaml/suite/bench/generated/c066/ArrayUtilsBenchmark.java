package bench.generated.c066;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.yaml.snakeyaml.util.ArrayUtils;


@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ArrayUtilsBenchmark {

    private Integer[] array1;
    private Integer[] array2;
    private Integer[] largeArray;

    @Setup
    public void setup() {
        // Setup for toUnmodifiableList benchmarks
        int size = 10000;
        largeArray = new Integer[size];
        for (int i = 0; i < size; i++) {
            largeArray[i] = i;
        }

        // Setup for toUnmodifiableCompositeList benchmarks
        array1 = new Integer[5000];
        for (int i = 0; i < 5000; i++) {
            array1[i] = i;
        }
        array2 = new Integer[5000];
        for (int i = 0; i < 5000; i++) {
            array2[i] = 10000 + i;
        }
    }

    @Benchmark
    public void toUnmodifiableList_LargeArray(Blackhole bh) {
        List<Integer> result = ArrayUtils.toUnmodifiableList(largeArray);
        bh.consume(result);
    }

    @Benchmark
    public void toUnmodifiableList_EmptyArray(Blackhole bh) {
        Integer[] emptyArray = new Integer[0];
        List<Integer> result = ArrayUtils.toUnmodifiableList(emptyArray);
        bh.consume(result);
    }

    @Benchmark
    public void toUnmodifiableList_SmallArray(Blackhole bh) {
        Integer[] smallArray = {1, 2, 3, 4, 5};
        List<Integer> result = ArrayUtils.toUnmodifiableList(smallArray);
        bh.consume(result);
    }

    @Benchmark
    public void toUnmodifiableCompositeList_BothNonEmpty(Blackhole bh) {
        List<Integer> result = ArrayUtils.toUnmodifiableCompositeList(array1, array2);
        bh.consume(result);
    }

    @Benchmark
    public void toUnmodifiableCompositeList_Array1Empty(Blackhole bh) {
        Integer[] emptyArray = new Integer[0];
        List<Integer> result = ArrayUtils.toUnmodifiableCompositeList(emptyArray, array2);
        bh.consume(result);
    }

    @Benchmark
    public void toUnmodifiableCompositeList_Array2Empty(Blackhole bh) {
        Integer[] emptyArray = new Integer[0];
        List<Integer> result = ArrayUtils.toUnmodifiableCompositeList(array1, emptyArray);
        bh.consume(result);
    }
}

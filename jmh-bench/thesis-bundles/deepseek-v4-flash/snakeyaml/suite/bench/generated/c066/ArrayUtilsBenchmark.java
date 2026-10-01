package bench.generated.c066;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
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

    private Integer[] nonEmptyArray;
    private Integer[] emptyArray;
    private Integer[] array1;
    private Integer[] array2;
    private List<Integer> nonEmptyList;
    private List<Integer> compositeList;
    private int index;

    @Setup(Level.Trial)
    public void setup() {
        nonEmptyArray = new Integer[100];
        for (int i = 0; i < 100; i++) {
            nonEmptyArray[i] = i;
        }
        emptyArray = new Integer[0];
        array1 = new Integer[50];
        for (int i = 0; i < 50; i++) {
            array1[i] = i;
        }
        array2 = new Integer[50];
        for (int i = 0; i < 50; i++) {
            array2[i] = i + 50;
        }
        nonEmptyList = ArrayUtils.toUnmodifiableList(nonEmptyArray);
        compositeList = ArrayUtils.toUnmodifiableCompositeList(array1, array2);
        index = 50;
    }

    @Benchmark
    public List<Integer> toUnmodifiableListNonEmpty() {
        return ArrayUtils.toUnmodifiableList(nonEmptyArray);
    }

    @Benchmark
    public List<Integer> toUnmodifiableListEmpty() {
        return ArrayUtils.toUnmodifiableList(emptyArray);
    }

    @Benchmark
    public List<Integer> toUnmodifiableCompositeListBoth() {
        return ArrayUtils.toUnmodifiableCompositeList(array1, array2);
    }

    @Benchmark
    public List<Integer> toUnmodifiableCompositeListFirstEmpty() {
        return ArrayUtils.toUnmodifiableCompositeList(emptyArray, array2);
    }

    @Benchmark
    public List<Integer> toUnmodifiableCompositeListSecondEmpty() {
        return ArrayUtils.toUnmodifiableCompositeList(array1, emptyArray);
    }

    @Benchmark
    public void listGet(Blackhole bh) {
        bh.consume(nonEmptyList.get(index));
    }

    @Benchmark
    public void compositeListGet(Blackhole bh) {
        bh.consume(compositeList.get(index));
    }

    @Benchmark
    public void listSize(Blackhole bh) {
        bh.consume(nonEmptyList.size());
    }

    @Benchmark
    public void compositeListSize(Blackhole bh) {
        bh.consume(compositeList.size());
    }
}

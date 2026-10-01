package bench.generated.c066;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.List;
import org.yaml.snakeyaml.util.ArrayUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ArrayUtilsBenchmark {

    private static final int NON_EMPTY_SIZE = 1000;
    private static final int HALF_SIZE = 500;

    private String[] nonEmptyArray;
    private String[] emptyArray;
    private String[] firstArray;
    private String[] secondArray;

    @Setup(Level.Trial)
    public void setUp() {
        nonEmptyArray = new String[NON_EMPTY_SIZE];
        for (int i = 0; i < NON_EMPTY_SIZE; i++) {
            nonEmptyArray[i] = "value-" + i;
        }
        emptyArray = new String[0];

        firstArray = new String[HALF_SIZE];
        secondArray = new String[HALF_SIZE];
        for (int i = 0; i < HALF_SIZE; i++) {
            firstArray[i] = "first-" + i;
            secondArray[i] = "second-" + i;
        }
    }

    @Benchmark
    public List<String> benchmarkToUnmodifiableList() {
        return ArrayUtils.toUnmodifiableList(nonEmptyArray);
    }

    @Benchmark
    public List<String> benchmarkToUnmodifiableCompositeListBothNonEmpty() {
        return ArrayUtils.toUnmodifiableCompositeList(firstArray, secondArray);
    }

    @Benchmark
    public List<String> benchmarkToUnmodifiableCompositeListFirstEmpty() {
        return ArrayUtils.toUnmodifiableCompositeList(emptyArray, nonEmptyArray);
    }

    @Benchmark
    public List<String> benchmarkToUnmodifiableCompositeListSecondEmpty() {
        return ArrayUtils.toUnmodifiableCompositeList(nonEmptyArray, emptyArray);
    }
}

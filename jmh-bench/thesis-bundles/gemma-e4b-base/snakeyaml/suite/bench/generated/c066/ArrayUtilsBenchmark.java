package bench.generated.c066;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.util.ArrayUtils;
import java.util.List;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ArrayUtilsBenchmark {

    // Inputs for toUnmodifiableList
    private String[] elementsSmall;
    private String[] elementsMedium;
    private String[] elementsLarge;

    // Inputs for toUnmodifiableCompositeList
    private String[] array1Small;
    private String[] array2Small;
    private String[] array1Medium;
    private String[] array2Medium;
    private String[] array1Large;
    private String[] array2Large;

    @Setup(Level.Trial)
    public void setup() {
        // Small inputs (e.g., size 10)
        elementsSmall = new String[10];
        array1Small = new String[10];
        array2Small = new String[10];
        for (int i = 0; i < 10; i++) {
            elementsSmall[i] = "A" + i;
            array1Small[i] = "A" + i;
            array2Small[i] = "B" + i;
        }

        // Medium inputs (e.g., size 1000)
        elementsMedium = new String[1000];
        array1Medium = new String[1000];
        array2Medium = new String[1000];
        for (int i = 0; i < 1000; i++) {
            elementsMedium[i] = "M" + i;
            array1Medium[i] = "M" + i;
            array2Medium[i] = "N" + i;
        }

        // Large inputs (e.g., size 10000)
        elementsLarge = new String[10000];
        array1Large = new String[10000];
        array2Large = new String[10000];
        for (int i = 0; i < 10000; i++) {
            elementsLarge[i] = "L" + i;
            array1Large[i] = "L" + i;
            array2Large[i] = "P" + i;
        }
    }

    // --- Benchmarks for toUnmodifiableList ---

    @Benchmark
    public List<String> toUnmodifiableList_Small(Blackhole bh) {
        List<String> result = ArrayUtils.toUnmodifiableList(elementsSmall);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public List<String> toUnmodifiableList_Medium(Blackhole bh) {
        List<String> result = ArrayUtils.toUnmodifiableList(elementsMedium);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public List<String> toUnmodifiableList_Large(Blackhole bh) {
        List<String> result = ArrayUtils.toUnmodifiableList(elementsLarge);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public List<String> toUnmodifiableList_Empty(Blackhole bh) {
        String[] emptyArray = new String[0];
        List<String> result = ArrayUtils.toUnmodifiableList(emptyArray);
        bh.consume(result);
        return result;
    }

    // --- Benchmarks for toUnmodifiableCompositeList ---

    @Benchmark
    public List<String> toUnmodifiableCompositeList_Small(Blackhole bh) {
        List<String> result = ArrayUtils.toUnmodifiableCompositeList(array1Small, array2Small);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public List<String> toUnmodifiableCompositeList_Medium(Blackhole bh) {
        List<String> result = ArrayUtils.toUnmodifiableCompositeList(array1Medium, array2Medium);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public List<String> toUnmodifiableCompositeList_Large(Blackhole bh) {
        List<String> result = ArrayUtils.toUnmodifiableCompositeList(array1Large, array2Large);
        bh.consume(result);
        return result;
    }

    // Edge case: array1 is empty
    @Benchmark
    public List<String> toUnmodifiableCompositeList_Array1Empty(Blackhole bh) {
        String[] emptyArray = new String[0];
        List<String> result = ArrayUtils.toUnmodifiableCompositeList(emptyArray, array2Small);
        bh.consume(result);
        return result;
    }

    // Edge case: array2 is empty
    @Benchmark
    public List<String> toUnmodifiableCompositeList_Array2Empty(Blackhole bh) {
        String[] emptyArray = new String[0];
        List<String> result = ArrayUtils.toUnmodifiableCompositeList(array1Small, emptyArray);
        bh.consume(result);
        return result;
    }

    // Edge case: both arrays are empty
    @Benchmark
    public List<String> toUnmodifiableCompositeList_BothEmpty(Blackhole bh) {
        String[] emptyArray = new String[0];
        List<String> result = ArrayUtils.toUnmodifiableCompositeList(emptyArray, emptyArray);
        bh.consume(result);
        return result;
    }
}

package bench.generated.c066;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.List;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ArrayUtilsBenchmark {

    // State field to hold a reference to the class under test, if needed,
    // though ArrayUtils is static, so it's not strictly necessary for method calls.
    // We keep the class structure clean.

    @Setup
    public void setup() {
        // Setup logic, if any, would go here.
    }

    @Benchmark
    public void testToUnmodifiableList(Blackhole bh) {
        // Test case 1: Empty array
        try {
            List<Integer> list1 = org.yaml.snakeyaml.util.ArrayUtils.toUnmodifiableList(new Integer[0]);
            bh.consume(list1);
        } catch (Exception e) {
            // Ignore exceptions for benchmarking if they are expected in edge cases,
            // but generally, we want the benchmark to run smoothly.
        }

        // Test case 2: Non-empty array
        try {
            Integer[] array = {1, 2, 3, 4, 5};
            List<Integer> list2 = org.yaml.snakeyaml.util.ArrayUtils.toUnmodifiableList(array);
            bh.consume(list2);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testToUnmodifiableCompositeList(Blackhole bh) {
        // Test case 1: Array1 is empty
        try {
            Integer[] array2 = {10, 20};
            List<Integer> list1 = org.yaml.snakeyaml.util.ArrayUtils.toUnmodifiableCompositeList(new Integer[0], array2);
            bh.consume(list1);
        } catch (Exception e) {
            // Ignore exceptions
        }

        // Test case 2: Array2 is empty
        try {
            Integer[] array1 = {1, 2};
            List<Integer> list2 = org.yaml.snakeyaml.util.ArrayUtils.toUnmodifiableCompositeList(array1, new Integer[0]);
            bh.consume(list2);
        } catch (Exception e) {
            // Ignore exceptions
        }

        // Test case 3: Both arrays non-empty
        try {
            Integer[] array1 = {1, 2};
            Integer[] array2 = {3, 4};
            List<Integer> list3 = org.yaml.snakeyaml.util.ArrayUtils.toUnmodifiableCompositeList(array1, array2);
            bh.consume(list3);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}

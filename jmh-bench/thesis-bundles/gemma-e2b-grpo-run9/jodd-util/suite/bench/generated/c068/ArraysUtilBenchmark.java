package bench.generated.c068;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.util.ArraysUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ArraysUtilBenchmark {

    // Since ArraysUtil methods are static, no instance state is required.

    // --- Tests for join ---

    @Benchmark
    public void joinStrings(Blackhole bh) {
        try {
            // Input data created locally to avoid FINAL anti-pattern
            String[] arr1 = {"a", "b"};
            String[] arr2 = {"c", "d"};
            ArraysUtil.join(arr1, arr2);
        } catch (Exception e) {
            bh.consume(e);
        }
    }

    @Benchmark
    public void joinBytes(Blackhole bh) {
        try {
            byte[] arr1 = {1, 2};
            byte[] arr2 = {3, 4};
            ArraysUtil.join(arr1, arr2);
        } catch (Exception e) {
            bh.consume(e);
        }
    }

    @Benchmark
    public void joinInts(Blackhole bh) {
        try {
            int[] arr1 = {1, 2};
            int[] arr2 = {3, 4};
            ArraysUtil.join(arr1, arr2);
        } catch (Exception e) {
            bh.consume(e);
        }
    }

    // --- Tests for resize ---

    @Benchmark
    public void resizeIntArray(Blackhole bh) {
        try {
            int[] original = {1, 2, 3};
            ArraysUtil.resize(original, 5);
        } catch (Exception e) {
            bh.consume(e);
        }
    }

    @Benchmark
    public void resizeByteArray(Blackhole bh) {
        try {
            byte[] original = {1, 2, 3};
            ArraysUtil.resize(original, 10);
        } catch (Exception e) {
            bh.consume(e);
        }
    }

    // --- Tests for append ---

    @Benchmark
    public void appendIntArray(Blackhole bh) {
        try {
            int[] original = {1, 2};
            ArraysUtil.append(original, 3);
        } catch (Exception e) {
            bh.consume(e);
        }
    }

    @Benchmark
    public void appendByteArray(Blackhole bh) {
        try {
            byte[] original = {1, 2};
            ArraysUtil.append(original, (byte) 3);
        } catch (Exception e) {
            bh.consume(e);
        }
    }

    // --- Tests for indexOf (Read-only operations) ---

    @Benchmark
    public void indexOfIntArray(Blackhole bh) {
        try {
            int[] array = {1, 2, 3, 4, 5};
            ArraysUtil.indexOf(array, 3);
        } catch (Exception e) {
            bh.consume(e);
        }
    }

    @Benchmark
    public void containsIntArray(Blackhole bh) {
        try {
            int[] array = {1, 2, 3, 4, 5};
            ArraysUtil.contains(array, 3);
        } catch (Exception e) {
            bh.consume(e);
        }
    }

    @Benchmark
    public void indexOfDoubleArray(Blackhole bh) {
        try {
            double[] array = {1.0, 2.5, 3.0};
            ArraysUtil.indexOf(array, new double[]{2.5});
        } catch (Exception e) {
            bh.consume(e);
        }
    }

    @Benchmark
    public void containsDoubleArray(Blackhole bh) {
        try {
            double[] array = {1.0, 2.5, 3.0};
            ArraysUtil.contains(array, new double[]{2.5});
        } catch (Exception e) {
            bh.consume(e);
        }
    }

    // --- Tests for toString (Read-only operations) ---

    @Benchmark
    public void toStringIntArray(Blackhole bh) {
        try {
            int[] array = {1, 2, 3};
            ArraysUtil.toString(array);
        } catch (Exception e) {
            bh.consume(e);
        }
    }

    @Benchmark
    public void toStringByteArray(Blackhole bh) {
        try {
            byte[] array = {1, 2, 3};
            ArraysUtil.toString(array);
        } catch (Exception e) {
            bh.consume(e);
        }
    }
}

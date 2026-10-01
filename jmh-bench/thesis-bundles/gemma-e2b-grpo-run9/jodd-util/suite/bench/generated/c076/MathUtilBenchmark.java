package bench.generated.c076;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import jodd.util.MathUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MathUtilBenchmark {

    @Benchmark
    public void testParseDigit(Blackhole bh) {
        // Test case 1: Digit '5'
        int result1 = MathUtil.parseDigit('5');
        bh.consume(result1);

        // Test case 2: Uppercase 'A'
        int result2 = MathUtil.parseDigit('A');
        bh.consume(result2);

        // Test case 3: Lowercase 'a'
        int result3 = MathUtil.parseDigit('a');
        bh.consume(result3);
    }

    @Benchmark
    public void testRandomLong(Blackhole bh) {
        // Test case 1: Range [0, 1000]
        long result1 = MathUtil.randomLong(0, 1000);
        bh.consume(result1);

        // Test case 2: Range [-5000, 5000]
        long result2 = MathUtil.randomLong(-5000, 5000);
        bh.consume(result2);
    }

    @Benchmark
    public void testRandomInt(Blackhole bh) {
        // Test case 1: Range [1, 100]
        int result1 = MathUtil.randomInt(1, 100);
        bh.consume(result1);

        // Test case 2: Range [-100, 100]
        int result2 = MathUtil.randomInt(-100, 100);
        bh.consume(result2);
    }

    @Benchmark
    public void testIsEven(Blackhole bh) {
        // Test case 1: Even number
        boolean result1 = MathUtil.isEven(42);
        bh.consume(result1);

        // Test case 2: Odd number
        boolean result2 = MathUtil.isEven(13);
        bh.consume(result2);
    }

    @Benchmark
    public void testIsOdd(Blackhole bh) {
        // Test case 1: Odd number
        boolean result1 = MathUtil.isOdd(13);
        bh.consume(result1);

        // Test case 2: Even number
        boolean result2 = MathUtil.isOdd(22);
        bh.consume(result2);
    }

    @Benchmark
    public void testHumanReadableByteCount(Blackhole bh) {
        // Test case 1: Small value (less than 1024)
        String result1 = MathUtil.humanReadableByteCount(500, false);
        bh.consume(result1);

        // Test case 2: Medium value (around 1KB)
        String result2 = MathUtil.humanReadableByteCount(1500, false);
        bh.consume(result2);

        // Test case 3: Large value (around 1MB)
        String result3 = MathUtil.humanReadableByteCount(1048576, true);
        bh.consume(result3);
    }

    @Benchmark
    public void testMathUtilIntegration(Blackhole bh) {
        // A simple sequence of calls to exercise multiple methods
        int val = MathUtil.randomInt(1, 100);
        boolean even = MathUtil.isEven(val);
        String human = MathUtil.humanReadableByteCount(1024, false);
        bh.consume(val);
        bh.consume(even);
        bh.consume(human);
    }
}

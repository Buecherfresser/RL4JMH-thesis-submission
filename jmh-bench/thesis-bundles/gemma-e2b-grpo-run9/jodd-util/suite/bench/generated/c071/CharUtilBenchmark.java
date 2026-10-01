package bench.generated.c071;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import jodd.util.CharUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharUtilBenchmark {

    // Since CharUtil methods are static and return new objects,
    // we do not need instance state (@State fields).

    @Benchmark
    public void testToByteArray(Blackhole bh) {
        // Test conversion from char[] to byte[] using default UTF-8 encoding
        char[] input = "Hello World".toCharArray();
        byte[] result = CharUtil.toByteArray(input);
        bh.consume(result);
    }

    @Benchmark
    public void testToRawByteArray(Blackhole bh) {
        // Test conversion from char[] to raw byte array (2 bytes per char)
        char[] input = {'A', 'B', 'C'};
        byte[] result = CharUtil.toRawByteArray(input);
        bh.consume(result);
    }

    @Benchmark
    public void testToCharArray(Blackhole bh) {
        // Test conversion from byte[] to char[] using default encoding
        byte[] input = new byte[]{10, 20, 30};
        char[] result = CharUtil.toCharArray(input);
        bh.consume(result);
    }

    @Benchmark
    public void testIsAlpha(Blackhole bh) {
        // Test isAlpha on lowercase
        boolean result = CharUtil.isAlpha('a');
        bh.consume(result);
    }

    @Benchmark
    public void testIsDigit(Blackhole bh) {
        // Test isDigit on a digit
        boolean result = CharUtil.isDigit('5');
        bh.consume(result);
    }

    @Benchmark
    public void testIsReserved(Blackhole bh) {
        // Test isReserved on a generic delimiter ':'
        boolean result = CharUtil.isReserved(':');
        bh.consume(result);
    }

    @Benchmark
    public void testHex2Int(Blackhole bh) {
        // Test hex2int conversion
        try {
            int result = CharUtil.hex2int('A');
            bh.consume(result);
        } catch (IllegalArgumentException e) {
            // Ignore expected exception if input validation fails, though we use valid input here.
        }
    }

    @Benchmark
    public void testFindFirstEqual(Blackhole bh) {
        // Test findFirstEqual (exact match)
        char[] source = {'a', 'b', 'c', 'd'};
        char[] match = {'c', 'x'};
        int result = CharUtil.findFirstEqual(source, 0, match);
        bh.consume(result);
    }

    @Benchmark
    public void testFindFirstDiff(Blackhole bh) {
        // Test findFirstDiff (exact match)
        char[] source = {'a', 'b', 'c', 'd'};
        char[] match = {'x', 'y'};
        int result = CharUtil.findFirstDiff(source, 0, match);
        bh.consume(result);
    }

    @Benchmark
    public void testIsWordChar(Blackhole bh) {
        // Test isWordChar (includes digits, alpha, and underscore)
        boolean result = CharUtil.isWordChar('_');
        bh.consume(result);
    }
}

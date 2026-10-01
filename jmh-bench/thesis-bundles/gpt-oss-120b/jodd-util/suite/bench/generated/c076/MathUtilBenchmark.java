package bench.generated.c076;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadLocalRandom;
import jodd.util.MathUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MathUtilBenchmark {

    private char[] digits;
    private int digitIdx;

    private int[] intValues;
    private int intIdx;

    private long[] byteCounts;
    private int byteIdx;

    @Setup(Level.Trial)
    public void setUp() {
        // digits 0-9, a-z, A-Z
        digits = new char[10 + 26 + 26];
        int pos = 0;
        for (char c = '0'; c <= '9'; c++) {
            digits[pos++] = c;
        }
        for (char c = 'a'; c <= 'z'; c++) {
            digits[pos++] = c;
        }
        for (char c = 'A'; c <= 'Z'; c++) {
            digits[pos++] = c;
        }
        digitIdx = 0;

        // random ints for even/odd checks
        intValues = new int[1024];
        for (int i = 0; i < intValues.length; i++) {
            intValues[i] = ThreadLocalRandom.current().nextInt();
        }
        intIdx = 0;

        // various byte counts for human readable formatting
        byteCounts = new long[] {
                0L,
                500L,
                1024L,
                1500L,
                10_000L,
                1_048_576L,
                5_000_000L,
                10_000_000_000L
        };
        byteIdx = 0;
    }

    @Benchmark
    public int benchmarkParseDigit() {
        char d = digits[digitIdx];
        digitIdx = (digitIdx + 1) % digits.length;
        return MathUtil.parseDigit(d);
    }

    @Benchmark
    public long benchmarkRandomLong() {
        return MathUtil.randomLong(0L, 1_000_000L);
    }

    @Benchmark
    public int benchmarkRandomInt() {
        return MathUtil.randomInt(0, 1_000_000);
    }

    @Benchmark
    public boolean benchmarkIsEven() {
        int v = intValues[intIdx];
        intIdx = (intIdx + 1) % intValues.length;
        return MathUtil.isEven(v);
    }

    @Benchmark
    public boolean benchmarkIsOdd() {
        int v = intValues[intIdx];
        intIdx = (intIdx + 1) % intValues.length;
        return MathUtil.isOdd(v);
    }

    @Benchmark
    public String benchmarkHumanReadableByteCountSI() {
        long b = byteCounts[byteIdx];
        byteIdx = (byteIdx + 1) % byteCounts.length;
        return MathUtil.humanReadableByteCount(b, true);
    }

    @Benchmark
    public String benchmarkHumanReadableByteCountBinary() {
        long b = byteCounts[byteIdx];
        byteIdx = (byteIdx + 1) % byteCounts.length;
        return MathUtil.humanReadableByteCount(b, false);
    }
}

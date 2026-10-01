package bench.generated.c076;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import jodd.util.MathUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MathUtilBenchmark {

    private char digitChar;
    private char lowerChar;
    private char upperChar;
    private int evenInt;
    private int oddInt;
    private long smallBytes;
    private long largeBytes;
    private boolean useSiTrue;
    private boolean useSiFalse;

    @Setup(Level.Trial)
    public void setup() {
        digitChar = '7';
        lowerChar = 'f';
        upperChar = 'F';
        evenInt = 42;
        oddInt = 43;
        smallBytes = 512L;
        largeBytes = 123456789L;
        useSiTrue = true;
        useSiFalse = false;
    }

    @Benchmark
    public int parseDigitNumeric() {
        return MathUtil.parseDigit(digitChar);
    }

    @Benchmark
    public int parseDigitLowercase() {
        return MathUtil.parseDigit(lowerChar);
    }

    @Benchmark
    public int parseDigitUppercase() {
        return MathUtil.parseDigit(upperChar);
    }

    @Benchmark
    public long randomLong() {
        return MathUtil.randomLong(0L, 1000L);
    }

    @Benchmark
    public int randomInt() {
        return MathUtil.randomInt(0, 1000);
    }

    @Benchmark
    public boolean isEven() {
        return MathUtil.isEven(evenInt);
    }

    @Benchmark
    public boolean isOdd() {
        return MathUtil.isOdd(oddInt);
    }

    @Benchmark
    public String humanReadableByteCountSmallSi() {
        return MathUtil.humanReadableByteCount(smallBytes, useSiTrue);
    }

    @Benchmark
    public String humanReadableByteCountSmallBinary() {
        return MathUtil.humanReadableByteCount(smallBytes, useSiFalse);
    }

    @Benchmark
    public String humanReadableByteCountLargeSi() {
        return MathUtil.humanReadableByteCount(largeBytes, useSiTrue);
    }

    @Benchmark
    public String humanReadableByteCountLargeBinary() {
        return MathUtil.humanReadableByteCount(largeBytes, useSiFalse);
    }
}

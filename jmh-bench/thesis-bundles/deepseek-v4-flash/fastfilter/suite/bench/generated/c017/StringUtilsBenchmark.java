package bench.generated.c017;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.fastfilter.utils.StringUtils;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class StringUtilsBenchmark {

    private char[] digitChars;
    private char[] lowerChars;
    private char[] upperChars;
    private char[] invalidChars;
    private char[] allChars;

    private int digitIndex;
    private int lowerIndex;
    private int upperIndex;
    private int invalidIndex;
    private int allIndex;
    private int allIndexConsumed;

    @Setup(Level.Trial)
    public void setup() {
        digitChars = new char[64];
        lowerChars = new char[64];
        upperChars = new char[64];
        invalidChars = new char[64];
        allChars = new char[256];

        for (int i = 0; i < digitChars.length; i++) {
            digitChars[i] = (char) ('0' + (i % 10));
            lowerChars[i] = (char) ('a' + (i % 6));
            upperChars[i] = (char) ('A' + (i % 6));
            invalidChars[i] = (char) (0x3A + (i % 7));
        }

        for (int i = 0; i < allChars.length; i++) {
            allChars[i] = (char) (i % 103);
        }

        digitIndex = 0;
        lowerIndex = 0;
        upperIndex = 0;
        invalidIndex = 0;
        allIndex = 0;
        allIndexConsumed = 0;
    }

    @Benchmark
    public int getHexDigit() {
        return StringUtils.getHex(digitChars[digitIndex++ & (digitChars.length - 1)]);
    }

    @Benchmark
    public int getHexLowercase() {
        return StringUtils.getHex(lowerChars[lowerIndex++ & (lowerChars.length - 1)]);
    }

    @Benchmark
    public int getHexUppercase() {
        return StringUtils.getHex(upperChars[upperIndex++ & (upperChars.length - 1)]);
    }

    @Benchmark
    public int getHexInvalid() {
        return StringUtils.getHex(invalidChars[invalidIndex++ & (invalidChars.length - 1)]);
    }

    @Benchmark
    public int getHexAllChars() {
        return StringUtils.getHex(allChars[allIndex++ & (allChars.length - 1)]);
    }

    @Benchmark
    public void getHexAllCharsConsumed(Blackhole bh) {
        bh.consume(StringUtils.getHex(allChars[allIndexConsumed++ & (allChars.length - 1)]));
    }
}

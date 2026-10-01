package bench.generated.c017;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.fastfilter.utils.StringUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class StringUtilsBenchmark {

    private char zeroChar;
    private char nineChar;
    private char aChar;
    private char fChar;
    private char AChar;
    private char FChar;
    private char nonHexChar;
    private char[] randomChars;
    private java.util.Random rand;

    @Setup(Level.Trial)
    public void setUp() {
        zeroChar = '0';
        nineChar = '9';
        aChar = 'a';
        fChar = 'f';
        AChar = 'A';
        FChar = 'F';
        nonHexChar = 'g';
        rand = new java.util.Random(12345L);
        randomChars = new char[256];
        for (int i = 0; i < randomChars.length; i++) {
            randomChars[i] = (char) (rand.nextInt(0x80));
        }
    }

    @Benchmark
    public int benchmarkGetHexZero() {
        return StringUtils.getHex(zeroChar);
    }

    @Benchmark
    public int benchmarkGetHexNine() {
        return StringUtils.getHex(nineChar);
    }

    @Benchmark
    public int benchmarkGetHexA() {
        return StringUtils.getHex(aChar);
    }

    @Benchmark
    public int benchmarkGetHexF() {
        return StringUtils.getHex(fChar);
    }

    @Benchmark
    public int benchmarkGetHexUpperA() {
        return StringUtils.getHex(AChar);
    }

    @Benchmark
    public int benchmarkGetHexUpperF() {
        return StringUtils.getHex(FChar);
    }

    @Benchmark
    public int benchmarkGetHexNonHex() {
        return StringUtils.getHex(nonHexChar);
    }

    @Benchmark
    public int benchmarkGetHexRandom() {
        char c = randomChars[rand.nextInt(randomChars.length)];
        return StringUtils.getHex(c);
    }
}

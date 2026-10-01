package bench.generated.c070;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.util.CharSequenceUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharSequenceUtilBenchmark {

    private String equalStr1;
    private String equalStr2;

    private String mixedCaseStr;
    private String lowerCaseStr;

    private String prefixStr;
    private String lowerPrefix;

    private String ignoreCaseStr1;
    private String ignoreCaseStr2;

    private char testChar;
    private String matchSeq;

    private String sourceSeq;
    private int sourceIndex;

    private char[] sourceArray;
    private char matchChar;

    @Setup(Level.Trial)
    public void setup() {
        equalStr1 = "The quick brown fox jumps over the lazy dog";
        equalStr2 = new String(equalStr1);

        mixedCaseStr = "HelloWorld";
        lowerCaseStr = "helloworld";

        prefixStr = "JavaBenchmark";
        lowerPrefix = "javabenchmark";

        ignoreCaseStr1 = "CaseInsensitive";
        ignoreCaseStr2 = "caseinsensitive";

        testChar = 'e';
        matchSeq = "aeiou";

        sourceSeq = "bcdfghaejklmno";
        sourceIndex = 0;

        sourceArray = sourceSeq.toCharArray();
        matchChar = 'a';
    }

    @Benchmark
    public boolean benchmarkEquals() {
        return CharSequenceUtil.equals(equalStr1, equalStr2);
    }

    @Benchmark
    public boolean benchmarkEqualsToLowercase() {
        return CharSequenceUtil.equalsToLowercase(mixedCaseStr, lowerCaseStr);
    }

    @Benchmark
    public boolean benchmarkStartsWithLowercase() {
        return CharSequenceUtil.startsWithLowercase(mixedCaseStr, lowerPrefix);
    }

    @Benchmark
    public boolean benchmarkEqualsIgnoreCase() {
        return CharSequenceUtil.equalsIgnoreCase(ignoreCaseStr1, ignoreCaseStr2);
    }

    @Benchmark
    public boolean benchmarkEqualsOne() {
        return CharSequenceUtil.equalsOne(testChar, matchSeq);
    }

    @Benchmark
    public int benchmarkFindFirstEqualCharSequence() {
        return CharSequenceUtil.findFirstEqual(sourceSeq, sourceIndex, matchSeq);
    }

    @Benchmark
    public int benchmarkFindFirstEqualCharArray() {
        return CharSequenceUtil.findFirstEqual(sourceArray, sourceIndex, matchChar);
    }

    @Benchmark
    public int benchmarkFindFirstDiffCharSequence() {
        return CharSequenceUtil.findFirstDiff(sourceSeq, sourceIndex, matchSeq);
    }

    @Benchmark
    public int benchmarkFindFirstDiffCharArray() {
        return CharSequenceUtil.findFirstDiff(sourceArray, sourceIndex, matchChar);
    }
}

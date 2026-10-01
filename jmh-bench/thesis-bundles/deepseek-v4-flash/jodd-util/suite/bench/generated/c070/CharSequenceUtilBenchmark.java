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
    private String diffStr;      // different content, same length
    private String diffLenStr;   // different length
    private String upperStr;
    private String lowerStr;
    private String startsUpper;
    private String prefixLower;
    private String matchStr;     // contains match char at end
    private String noMatchStr;   // does not contain match char
    private String diffSource;   // for findFirstDiff
    private char matchChar;
    private char nonMatchChar;
    private String matchSet;     // set of chars containing matchChar
    private char[] charArray;
    private char[] diffCharArray;
    private int startIndex;

    @Setup(Level.Trial)
    public void setup() {
        // Build strings of length ~100
        equalStr1 = buildString('a', 100);
        equalStr2 = buildString('a', 100);
        diffStr = buildString('b', 100);       // different char, same length
        diffLenStr = buildString('a', 99);     // different length

        upperStr = buildString('A', 100);
        lowerStr = buildString('a', 100);

        startsUpper = buildString('X', 100) + "rest";
        prefixLower = "xx";

        // For findFirstEqual, put match char at the end
        StringBuilder sb = new StringBuilder(100);
        for (int i = 0; i < 99; i++) sb.append('x');
        sb.append('z');
        matchStr = sb.toString();

        noMatchStr = buildString('x', 100);    // no 'z'

        // For findFirstDiff, all same then different at end
        sb = new StringBuilder(100);
        for (int i = 0; i < 99; i++) sb.append('y');
        sb.append('a');
        diffSource = sb.toString();

        matchChar = 'z';
        nonMatchChar = 'q';
        matchSet = "abcxyz";                   // contains matchChar
        charArray = matchStr.toCharArray();
        diffCharArray = diffSource.toCharArray();
        startIndex = 0;
    }

    private String buildString(char c, int length) {
        char[] buf = new char[length];
        for (int i = 0; i < length; i++) buf[i] = c;
        return new String(buf);
    }

    @Benchmark
    public boolean equalsEqual() {
        return CharSequenceUtil.equals(equalStr1, equalStr2);
    }

    @Benchmark
    public boolean equalsDifferent() {
        return CharSequenceUtil.equals(equalStr1, diffStr);
    }

    @Benchmark
    public boolean equalsDifferentLength() {
        return CharSequenceUtil.equals(equalStr1, diffLenStr);
    }

    @Benchmark
    public boolean equalsToLowercaseMatch() {
        return CharSequenceUtil.equalsToLowercase(upperStr, lowerStr);
    }

    @Benchmark
    public boolean startsWithLowercaseMatch() {
        return CharSequenceUtil.startsWithLowercase(startsUpper, prefixLower);
    }

    @Benchmark
    public boolean equalsIgnoreCaseMatch() {
        return CharSequenceUtil.equalsIgnoreCase(upperStr, lowerStr);
    }

    @Benchmark
    public boolean equalsOneFound() {
        return CharSequenceUtil.equalsOne(matchChar, matchSet);
    }

    @Benchmark
    public boolean equalsOneNotFound() {
        return CharSequenceUtil.equalsOne(nonMatchChar, matchSet);
    }

    @Benchmark
    public int findFirstEqualFound() {
        return CharSequenceUtil.findFirstEqual(matchStr, startIndex, matchSet);
    }

    @Benchmark
    public int findFirstEqualNotFound() {
        return CharSequenceUtil.findFirstEqual(noMatchStr, startIndex, matchSet);
    }

    @Benchmark
    public int findFirstEqualCharArrayFound() {
        return CharSequenceUtil.findFirstEqual(charArray, startIndex, matchChar);
    }

    @Benchmark
    public int findFirstEqualCharArrayNotFound() {
        return CharSequenceUtil.findFirstEqual(charArray, startIndex, nonMatchChar);
    }

    @Benchmark
    public int findFirstDiffFound() {
        return CharSequenceUtil.findFirstDiff(diffSource, startIndex, matchSet);
    }

    @Benchmark
    public int findFirstDiffCharArrayFound() {
        return CharSequenceUtil.findFirstDiff(diffCharArray, startIndex, 'y');
    }

    @Benchmark
    public int findFirstDiffCharArrayNotFound() {
        return CharSequenceUtil.findFirstDiff(diffCharArray, startIndex, 'a');
    }
}

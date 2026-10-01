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

    // Inputs for comparison methods (s1 vs s2)
    private String s1;
    private String s2;
    private String s3; // Different length/content

    // Inputs for finding methods (source vs match)
    private String sourceLong;
    private char[] sourceArray;
    private String matchChars;
    private char singleMatchChar;
    private int startIndex = 0;

    @Setup(Level.Trial)
    public void setup() {
        // Setup inputs for comparison methods
        s1 = "HelloWorld";
        s2 = "helloworld"; // Case difference
        s3 = "Goodbye"; // Different content

        // Setup inputs for finding methods
        // Long source string
        sourceLong = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        
        // Array source
        sourceArray = new char[50];
        for (int i = 0; i < 50; i++) {
            sourceArray[i] = (char) ('a' + (i % 26));
        }

        // Match characters (for equalsOne and finding methods)
        matchChars = "aeiou";
        
        // Single character match
        singleMatchChar = 'e';
    }

    @Benchmark
    public void testEquals(Blackhole bh) {
        // Test exact equality
        bh.consume(CharSequenceUtil.equals(s1, s1));
        bh.consume(CharSequenceUtil.equals(s1, s2));
        bh.consume(CharSequenceUtil.equals(s1, s3));
    }

    @Benchmark
    public void testEqualsToLowercase(Blackhole bh) {
        // Test case-insensitive equality
        bh.consume(CharSequenceUtil.equalsToLowercase(s1, s2));
        bh.consume(CharSequenceUtil.equalsToLowercase(s1, s3));
    }

    @Benchmark
    public void testStartsWithLowercase(Blackhole bh) {
        // Test startsWithLowercase
        bh.consume(CharSequenceUtil.startsWithLowercase(s1, "hello"));
        bh.consume(CharSequenceUtil.startsWithLowercase(s1, "Goodbye"));
    }

    @Benchmark
    public void testEqualsIgnoreCase(Blackhole bh) {
        // Test case-insensitive equality
        bh.consume(CharSequenceUtil.equalsIgnoreCase(s1, s2));
        bh.consume(CharSequenceUtil.equalsIgnoreCase(s1, s3));
    }

    @Benchmark
    public void testEqualsOne(Blackhole bh) {
        // Test equalsOne (char in CharSequence)
        bh.consume(CharSequenceUtil.equalsOne('o', matchChars));
        bh.consume(CharSequenceUtil.equalsOne('z', matchChars));
    }

    @Benchmark
    public void testFindFirstEqualCharSequence(Blackhole bh) {
        // Test findFirstEqual (CharSequence source, CharSequence match)
        // Test case where match is found
        bh.consume(CharSequenceUtil.findFirstEqual(sourceLong, startIndex, "cgh"));
        // Test case where match is not found (should return -1)
        bh.consume(CharSequenceUtil.findFirstEqual(sourceLong, startIndex, "xyz"));
    }

    @Benchmark
    public void testFindFirstEqualCharArray(Blackhole bh) {
        // Test findFirstEqual (char[] source, char match)
        // Test case where match is found
        bh.consume(CharSequenceUtil.findFirstEqual(sourceArray, startIndex, 'a'));
        // Test case where match is not found (should return -1)
        bh.consume(CharSequenceUtil.findFirstEqual(sourceArray, startIndex, 'z'));
    }

    @Benchmark
    public void testFindFirstDiffCharSequence(Blackhole bh) {
        // Test findFirstDiff (CharSequence source, CharSequence match)
        // Test case where difference is found early
        bh.consume(CharSequenceUtil.findFirstDiff(sourceLong, startIndex, "abc"));
        // Test case where no difference is found (should return -1)
        bh.consume(CharSequenceUtil.findFirstDiff(sourceLong, startIndex, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"));
    }

    @Benchmark
    public void testFindFirstDiffCharArray(Blackhole bh) {
        // Test findFirstDiff (char[] source, char match)
        // Test case where difference is found
        bh.consume(CharSequenceUtil.findFirstDiff(sourceArray, startIndex, 'z'));
        // Test case where no difference is found (should return -1)
        bh.consume(CharSequenceUtil.findFirstDiff(sourceArray, startIndex, 'a'));
    }
}

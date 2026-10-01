package bench.generated.c071;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.util.CharUtil;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharUtilBenchmark {

    private byte sampleByte;
    private char sampleChar;
    private char[] sampleCharArray;
    private String sampleString;
    private byte[] sampleByteArray;
    private char[] sampleMatchArray;

    @Setup(Level.Trial)
    public void setup() {
        sampleByte = (byte) 0xAB;
        sampleChar = 'A';
        int size = 256;
        sampleCharArray = new char[size];
        for (int i = 0; i < size; i++) {
            sampleCharArray[i] = (char) i;
        }
        sampleString = new String(sampleCharArray);
        sampleByteArray = new byte[size];
        for (int i = 0; i < size; i++) {
            sampleByteArray[i] = (byte) i;
        }
        sampleMatchArray = new char[] {'a', 'e', 'i', 'o', 'u'};
    }

    @Benchmark
    public char benchToChar() {
        return CharUtil.toChar(sampleByte);
    }

    @Benchmark
    public byte[] benchToSimpleByteArrayFromCharArray() {
        return CharUtil.toSimpleByteArray(sampleCharArray);
    }

    @Benchmark
    public byte[] benchToSimpleByteArrayFromCharSequence() {
        return CharUtil.toSimpleByteArray(sampleString);
    }

    @Benchmark
    public char[] benchToSimpleCharArray() {
        return CharUtil.toSimpleCharArray(sampleByteArray);
    }

    @Benchmark
    public int benchToAscii() {
        return CharUtil.toAscii(sampleChar);
    }

    @Benchmark
    public byte[] benchToAsciiByteArrayFromCharArray() {
        return CharUtil.toAsciiByteArray(sampleCharArray);
    }

    @Benchmark
    public byte[] benchToAsciiByteArrayFromCharSequence() {
        return CharUtil.toAsciiByteArray(sampleString);
    }

    @Benchmark
    public byte[] benchToRawByteArray() {
        return CharUtil.toRawByteArray(sampleCharArray);
    }

    @Benchmark
    public char[] benchToRawCharArray() {
        return CharUtil.toRawCharArray(sampleByteArray);
    }

    @Benchmark
    public byte[] benchToByteArrayDefault() {
        return CharUtil.toByteArray(sampleCharArray);
    }

    @Benchmark
    public byte[] benchToByteArrayCharset() {
        return CharUtil.toByteArray(sampleCharArray, StandardCharsets.UTF_8);
    }

    @Benchmark
    public char[] benchToCharArrayDefault() {
        return CharUtil.toCharArray(sampleByteArray);
    }

    @Benchmark
    public char[] benchToCharArrayCharset() {
        return CharUtil.toCharArray(sampleByteArray, StandardCharsets.UTF_8);
    }

    @Benchmark
    public boolean benchEqualsOne() {
        return CharUtil.equalsOne(sampleChar, sampleMatchArray);
    }

    @Benchmark
    public int benchFindFirstEqualArray() {
        return CharUtil.findFirstEqual(sampleCharArray, 0, sampleMatchArray);
    }

    @Benchmark
    public int benchFindFirstEqualChar() {
        return CharUtil.findFirstEqual(sampleCharArray, 0, sampleChar);
    }

    @Benchmark
    public int benchFindFirstDiffArray() {
        return CharUtil.findFirstDiff(sampleCharArray, 0, sampleMatchArray);
    }

    @Benchmark
    public int benchFindFirstDiffChar() {
        return CharUtil.findFirstDiff(sampleCharArray, 0, sampleChar);
    }

    @Benchmark
    public boolean benchIsWhitespace() {
        return CharUtil.isWhitespace(sampleChar);
    }

    @Benchmark
    public boolean benchIsLowercaseAlpha() {
        return CharUtil.isLowercaseAlpha(sampleChar);
    }

    @Benchmark
    public boolean benchIsUppercaseAlpha() {
        return CharUtil.isUppercaseAlpha(sampleChar);
    }

    @Benchmark
    public boolean benchIsAlphaOrDigit() {
        return CharUtil.isAlphaOrDigit(sampleChar);
    }

    @Benchmark
    public boolean benchIsWordChar() {
        return CharUtil.isWordChar(sampleChar);
    }

    @Benchmark
    public boolean benchIsPropertyNameChar() {
        return CharUtil.isPropertyNameChar(sampleChar);
    }

    @Benchmark
    public boolean benchIsAlpha() {
        return CharUtil.isAlpha(sampleChar);
    }

    @Benchmark
    public boolean benchIsDigit() {
        return CharUtil.isDigit(sampleChar);
    }

    @Benchmark
    public boolean benchIsHexDigit() {
        return CharUtil.isHexDigit(sampleChar);
    }

    @Benchmark
    public boolean benchIsGenericDelimiter() {
        return CharUtil.isGenericDelimiter(sampleChar);
    }

    @Benchmark
    public boolean benchIsSubDelimiter() {
        return CharUtil.isSubDelimiter(sampleChar);
    }

    @Benchmark
    public boolean benchIsReserved() {
        return CharUtil.isReserved(sampleChar);
    }

    @Benchmark
    public boolean benchIsUnreserved() {
        return CharUtil.isUnreserved(sampleChar);
    }

    @Benchmark
    public boolean benchIsPchar() {
        return CharUtil.isPchar(sampleChar);
    }

    @Benchmark
    public char benchToUpperAscii() {
        return CharUtil.toUpperAscii(sampleChar);
    }

    @Benchmark
    public char benchToLowerAscii() {
        return CharUtil.toLowerAscii(sampleChar);
    }

    @Benchmark
    public int benchHex2int() {
        return CharUtil.hex2int('A');
    }

    @Benchmark
    public char benchInt2hex() {
        return CharUtil.int2hex(10);
    }
}

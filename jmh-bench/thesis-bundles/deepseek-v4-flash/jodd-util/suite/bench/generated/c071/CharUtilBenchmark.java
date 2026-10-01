package bench.generated.c071;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import jodd.util.CharUtil;
import java.nio.charset.StandardCharsets;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharUtilBenchmark {

    @State(Scope.Benchmark)
    public static class BenchState {
        byte byteValue = 65; // 'A'
        char[] testChars;
        String testString;
        byte[] testBytes;
        char[] matchArray;
        char matchSingle;
        char alphaChar;
        char nonAlphaChar;
        char digitChar;
        char nonDigitChar;
        char uppercaseChar;
        char lowercaseChar;
        char hexDigitChar;
        char nonHexDigitChar;
        char genericDelimiterChar;
        char nonGenericDelimiterChar;
        char subDelimiterChar;
        char nonSubDelimiterChar;
        char reservedChar;
        char nonReservedChar;
        char unreservedChar;
        char nonUnreservedChar;
        char pcharChar;
        char nonPcharChar;
        char whitespaceChar;
        char nonWhitespaceChar;
        char asciiChar;
        char nonAsciiChar;
        char upperAsciiTarget;
        char lowerAsciiTarget;
        int hexValue = 15;

        @Setup(Level.Trial)
        public void setup() {
            testChars = "Hello, World! 0123456789".toCharArray();
            testString = "Hello, World! 0123456789";
            testBytes = testString.getBytes(StandardCharsets.UTF_8);
            matchArray = new char[] {'e', 'o', 'x'};
            matchSingle = 'e';
            alphaChar = 'a';
            nonAlphaChar = '+';
            digitChar = '5';
            nonDigitChar = 'x';
            uppercaseChar = 'A';
            lowercaseChar = 'a';
            hexDigitChar = 'F';
            nonHexDigitChar = 'G';
            genericDelimiterChar = ':';
            nonGenericDelimiterChar = 'a';
            subDelimiterChar = '!';
            nonSubDelimiterChar = 'a';
            reservedChar = ':';
            nonReservedChar = 'a';
            unreservedChar = 'a';
            nonUnreservedChar = ':';
            pcharChar = 'a'; // unreserved
            nonPcharChar = '#'; // not in pchar
            whitespaceChar = ' ';
            nonWhitespaceChar = 'a';
            asciiChar = 'a';
            nonAsciiChar = '\u0100'; // 256 > 0xFF
            upperAsciiTarget = 'a';
            lowerAsciiTarget = 'A';
        }
    }

    // toChar
    @Benchmark
    public char toChar(BenchState state) {
        return CharUtil.toChar(state.byteValue);
    }

    // toSimpleByteArray(char[])
    @Benchmark
    public byte[] toSimpleByteArrayCharArray(BenchState state) {
        return CharUtil.toSimpleByteArray(state.testChars);
    }

    // toSimpleByteArray(CharSequence)
    @Benchmark
    public byte[] toSimpleByteArrayCharSequence(BenchState state) {
        return CharUtil.toSimpleByteArray(state.testString);
    }

    // toSimpleCharArray
    @Benchmark
    public char[] toSimpleCharArray(BenchState state) {
        return CharUtil.toSimpleCharArray(state.testBytes);
    }

    // toAscii
    @Benchmark
    public int toAsciiNormal(BenchState state) {
        return CharUtil.toAscii(state.asciiChar);
    }

    @Benchmark
    public int toAsciiNonAscii(BenchState state) {
        return CharUtil.toAscii(state.nonAsciiChar);
    }

    // toAsciiByteArray(char[])
    @Benchmark
    public byte[] toAsciiByteArrayCharArray(BenchState state) {
        return CharUtil.toAsciiByteArray(state.testChars);
    }

    // toAsciiByteArray(CharSequence)
    @Benchmark
    public byte[] toAsciiByteArrayCharSequence(BenchState state) {
        return CharUtil.toAsciiByteArray(state.testString);
    }

    // toRawByteArray
    @Benchmark
    public byte[] toRawByteArray(BenchState state) {
        return CharUtil.toRawByteArray(state.testChars);
    }

    // toRawCharArray
    @Benchmark
    public char[] toRawCharArray(BenchState state) {
        return CharUtil.toRawCharArray(state.testBytes);
    }

    // toByteArray(char[])
    @Benchmark
    public byte[] toByteArrayCharArray(BenchState state) {
        return CharUtil.toByteArray(state.testChars);
    }

    // toByteArray(char[], Charset)
    @Benchmark
    public byte[] toByteArrayCharArrayCharset(BenchState state) {
        return CharUtil.toByteArray(state.testChars, StandardCharsets.UTF_8);
    }

    // toCharArray(byte[])
    @Benchmark
    public char[] toCharArrayByteArray(BenchState state) {
        return CharUtil.toCharArray(state.testBytes);
    }

    // toCharArray(byte[], Charset)
    @Benchmark
    public char[] toCharArrayByteArrayCharset(BenchState state) {
        return CharUtil.toCharArray(state.testBytes, StandardCharsets.UTF_8);
    }

    // equalsOne
    @Benchmark
    public boolean equalsOneTrue(BenchState state) {
        return CharUtil.equalsOne(state.matchSingle, state.matchArray);
    }

    @Benchmark
    public boolean equalsOneFalse(BenchState state) {
        return CharUtil.equalsOne('z', state.matchArray);
    }

    // findFirstEqual(char[], index, char[])
    @Benchmark
    public int findFirstEqualCharArray(BenchState state) {
        return CharUtil.findFirstEqual(state.testChars, 0, state.matchArray);
    }

    // findFirstEqual(char[], index, char)
    @Benchmark
    public int findFirstEqualChar(BenchState state) {
        return CharUtil.findFirstEqual(state.testChars, 0, state.matchSingle);
    }

    // findFirstDiff(char[], index, char[])
    @Benchmark
    public int findFirstDiffCharArray(BenchState state) {
        return CharUtil.findFirstDiff(state.testChars, 0, state.matchArray);
    }

    // findFirstDiff(char[], index, char)
    @Benchmark
    public int findFirstDiffChar(BenchState state) {
        return CharUtil.findFirstDiff(state.testChars, 0, state.matchSingle);
    }

    // isWhitespace
    @Benchmark
    public boolean isWhitespaceTrue(BenchState state) {
        return CharUtil.isWhitespace(state.whitespaceChar);
    }

    @Benchmark
    public boolean isWhitespaceFalse(BenchState state) {
        return CharUtil.isWhitespace(state.nonWhitespaceChar);
    }

    // isLowercaseAlpha
    @Benchmark
    public boolean isLowercaseAlphaTrue(BenchState state) {
        return CharUtil.isLowercaseAlpha(state.lowercaseChar);
    }

    @Benchmark
    public boolean isLowercaseAlphaFalse(BenchState state) {
        return CharUtil.isLowercaseAlpha(state.uppercaseChar);
    }

    // isUppercaseAlpha
    @Benchmark
    public boolean isUppercaseAlphaTrue(BenchState state) {
        return CharUtil.isUppercaseAlpha(state.uppercaseChar);
    }

    @Benchmark
    public boolean isUppercaseAlphaFalse(BenchState state) {
        return CharUtil.isUppercaseAlpha(state.lowercaseChar);
    }

    // isAlphaOrDigit
    @Benchmark
    public boolean isAlphaOrDigitTrue(BenchState state) {
        return CharUtil.isAlphaOrDigit(state.alphaChar);
    }

    @Benchmark
    public boolean isAlphaOrDigitFalse(BenchState state) {
        return CharUtil.isAlphaOrDigit(state.nonAlphaChar);
    }

    // isWordChar
    @Benchmark
    public boolean isWordCharTrue(BenchState state) {
        return CharUtil.isWordChar(state.alphaChar);
    }

    @Benchmark
    public boolean isWordCharFalse(BenchState state) {
        return CharUtil.isWordChar(state.nonAlphaChar);
    }

    // isPropertyNameChar
    @Benchmark
    public boolean isPropertyNameCharTrue(BenchState state) {
        return CharUtil.isPropertyNameChar(state.alphaChar);
    }

    @Benchmark
    public boolean isPropertyNameCharFalse(BenchState state) {
        return CharUtil.isPropertyNameChar(state.nonAlphaChar);
    }

    // isAlpha
    @Benchmark
    public boolean isAlphaTrue(BenchState state) {
        return CharUtil.isAlpha(state.alphaChar);
    }

    @Benchmark
    public boolean isAlphaFalse(BenchState state) {
        return CharUtil.isAlpha(state.nonAlphaChar);
    }

    // isDigit
    @Benchmark
    public boolean isDigitTrue(BenchState state) {
        return CharUtil.isDigit(state.digitChar);
    }

    @Benchmark
    public boolean isDigitFalse(BenchState state) {
        return CharUtil.isDigit(state.nonDigitChar);
    }

    // isHexDigit
    @Benchmark
    public boolean isHexDigitTrue(BenchState state) {
        return CharUtil.isHexDigit(state.hexDigitChar);
    }

    @Benchmark
    public boolean isHexDigitFalse(BenchState state) {
        return CharUtil.isHexDigit(state.nonHexDigitChar);
    }

    // isGenericDelimiter
    @Benchmark
    public boolean isGenericDelimiterTrue(BenchState state) {
        return CharUtil.isGenericDelimiter(state.genericDelimiterChar);
    }

    @Benchmark
    public boolean isGenericDelimiterFalse(BenchState state) {
        return CharUtil.isGenericDelimiter(state.nonGenericDelimiterChar);
    }

    // isSubDelimiter
    @Benchmark
    public boolean isSubDelimiterTrue(BenchState state) {
        return CharUtil.isSubDelimiter(state.subDelimiterChar);
    }

    @Benchmark
    public boolean isSubDelimiterFalse(BenchState state) {
        return CharUtil.isSubDelimiter(state.nonSubDelimiterChar);
    }

    // isReserved
    @Benchmark
    public boolean isReservedTrue(BenchState state) {
        return CharUtil.isReserved(state.reservedChar);
    }

    @Benchmark
    public boolean isReservedFalse(BenchState state) {
        return CharUtil.isReserved(state.nonReservedChar);
    }

    // isUnreserved
    @Benchmark
    public boolean isUnreservedTrue(BenchState state) {
        return CharUtil.isUnreserved(state.unreservedChar);
    }

    @Benchmark
    public boolean isUnreservedFalse(BenchState state) {
        return CharUtil.isUnreserved(state.nonUnreservedChar);
    }

    // isPchar
    @Benchmark
    public boolean isPcharTrue(BenchState state) {
        return CharUtil.isPchar(state.pcharChar);
    }

    @Benchmark
    public boolean isPcharFalse(BenchState state) {
        return CharUtil.isPchar(state.nonPcharChar);
    }

    // toUpperAscii
    @Benchmark
    public char toUpperAscii(BenchState state) {
        return CharUtil.toUpperAscii(state.upperAsciiTarget);
    }

    // toLowerAscii
    @Benchmark
    public char toLowerAscii(BenchState state) {
        return CharUtil.toLowerAscii(state.lowerAsciiTarget);
    }

    // hex2int
    @Benchmark
    public int hex2int(BenchState state) {
        return CharUtil.hex2int(state.hexDigitChar);
    }

    // int2hex
    @Benchmark
    public char int2hex(BenchState state) {
        return CharUtil.int2hex(state.hexValue);
    }
}

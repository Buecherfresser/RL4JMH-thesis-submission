package bench.generated.c081;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Locale;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import jodd.util.StringUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class StringUtilBenchmark {

    // --- Setup Data ---
    private String largeString;
    private String[] largeStringArray;
    private byte[] largeByteArray;
    private String[] replacementSubstrings;
    private String[] replacementWithStrings;
    private String[] charArraySubstrings;
    private String[] charArrayReplacement;
    private String[] collectionForJoin;
    private Object[] objectArrayForJoin;

    @Setup
    public void setup() {
        // 1. Large String Setup (for replace, remove, substring, search)
        String base = "This is a test string for benchmarking string utilities. It contains various characters, numbers, and mixed case words. We need to test replacement, removal, splitting, and searching capabilities thoroughly. JMH benchmarks require large inputs to measure performance accurately.";
        this.largeString = base.repeat(50); // ~2500 characters

        // 2. Array Setup (for array operations like join)
        this.largeStringArray = new String[100];
        for (int i = 0; i < 100; i++) {
            largeStringArray[i] = base + i;
        }

        // 3. Replacement Setup
        this.replacementSubstrings = new String[10];
        this.replacementWithStrings = new String[10];
        for (int i = 0; i < 10; i++) {
            replacementSubstrings[i] = "test";
            replacementWithStrings[i] = "REPLACED";
        }

        // 4. Character Array Setup
        this.charArraySubstrings = new String[10];
        this.charArrayReplacement = new String[10];
        for (int i = 0; i < 10; i++) {
            charArraySubstrings[i] = "abcde";
            charArrayReplacement[i] = "XYZ";
        }

        // 5. Collection Setup (for join)
        this.collectionForJoin = new String[50];
        for (int i = 0; i < 50; i++) {
            collectionForJoin[i] = "item_" + i;
        }
        this.objectArrayForJoin = new Object[50];
        for (int i = 0; i < 50; i++) {
            objectArrayForJoin[i] = "obj_" + i;
        }

        // 6. Byte Array Setup (for hex conversion)
        this.largeByteArray = new byte[1024];
        for (int i = 0; i < 1024; i++) {
            largeByteArray[i] = (byte) (i % 256);
        }
    }

    // --- String Replacement Benchmarks ---

    @Benchmark
    public void benchReplace(Blackhole bh) {
        String result = StringUtil.replace(largeString, "test", "REPLACED");
        bh.consume(result);
    }

    @Benchmark
    public void benchReplaceChar(Blackhole bh) {
        String result = StringUtil.replaceChar(largeString, 't', 'X');
        bh.consume(result);
    }

    @Benchmark
    public void benchReplaceChars(Blackhole bh) {
        char[] sub = {'t', 'e'};
        char[] with = {'X', 'Y'};
        String result = StringUtil.replaceChars(largeString, sub, with);
        bh.consume(result);
    }

    @Benchmark
    public void benchReplaceFirst(Blackhole bh) {
        String result = StringUtil.replaceFirst(largeString, "test", "FIRST");
        bh.consume(result);
    }

    @Benchmark
    public void benchReplaceLast(Blackhole bh) {
        String result = StringUtil.replaceLast(largeString, "test", "LAST");
        bh.consume(result);
    }

    @Benchmark
    public void benchRemove(Blackhole bh) {
        String result = StringUtil.remove(largeString, "test");
        bh.consume(result);
    }

    @Benchmark
    public void benchRemoveChars(Blackhole bh) {
        String result = StringUtil.removeChars(largeString, "aeiou");
        bh.consume(result);
    }

    // --- String Comparison and Utility Benchmarks ---

    @Benchmark
    public void benchEquals(Blackhole bh) {
        String s1 = largeString;
        String s2 = largeString;
        boolean result = StringUtil.equals(s1, s2);
        bh.consume(result);
    }

    @Benchmark
    public void benchIsBlank(Blackhole bh) {
        String s = "   \t\n  ";
        boolean result = StringUtil.isBlank(s);
        bh.consume(result);
    }

    @Benchmark
    public void benchIsNotBlank(Blackhole bh) {
        String s = "  hello  ";
        boolean result = StringUtil.isNotBlank(s);
        bh.consume(result);
    }

    @Benchmark
    public void benchContainsOnlyWhitespaces(Blackhole bh) {
        String s = " \t \n ";
        boolean result = StringUtil.containsOnlyWhitespaces(s);
        bh.consume(result);
    }

    @Benchmark
    public void benchContainsOnlyDigits(Blackhole bh) {
        String s = "12345";
        boolean result = StringUtil.containsOnlyDigits(s);
        bh.consume(result);
    }

    @Benchmark
    public void benchContainsOnlyDigitsAndSigns(Blackhole bh) {
        String s = "123-45+6";
        boolean result = StringUtil.containsOnlyDigitsAndSigns(s);
        bh.consume(result);
    }

    @Benchmark
    public void benchTrimDown(Blackhole bh) {
        String s = "  data  ";
        String result = StringUtil.trimDown(s);
        bh.consume(result);
    }

    // --- Casing and Transformation Benchmarks ---

    @Benchmark
    public void benchToLowercase(Blackhole bh) {
        String s = "HeLlO wOrLd 123";
        String result = StringUtil.toLowerCase(s);
        bh.consume(result);
    }

    @Benchmark
    public void benchToUpperCase(Blackhole bh) {
        String s = "heLlO wOrLd 123";
        Locale locale = Locale.US;
        String result = StringUtil.toUpperCase(s, locale);
        bh.consume(result);
    }

    @Benchmark
    public void benchTitle(Blackhole bh) {
        String s = "hello world this is a test";
        String result = StringUtil.title(s);
        bh.consume(result);
    }

    @Benchmark
    public void benchFromCamelCase(Blackhole bh) {
        String s = "myVariableName";
        String result = StringUtil.fromCamelCase(s, '_');
        bh.consume(result);
    }

    @Benchmark
    public void benchToCamelCase(Blackhole bh) {
        String s = "my_variable_name";
        String result = StringUtil.toCamelCase(s, false, '_');
        bh.consume(result);
    }

    // --- Substring and Search Benchmarks ---

    @Benchmark
    public void benchSubstring(Blackhole bh) {
        String s = largeString;
        String result = StringUtil.substring(s, 100, 200);
        bh.consume(result);
    }

    @Benchmark
    public void benchIsSubstringAt(Blackhole bh) {
        String s = largeString;
        String sub = "test string";
        int offset = 500;
        boolean result = StringUtil.isSubstringAt(s, sub, offset);
        bh.consume(result);
    }

    @Benchmark
    public void benchIndexOfIgnoreCase(Blackhole bh) {
        String s = largeString;
        String sub = "various characters";
        int startIndex = 100;
        int result = StringUtil.indexOfIgnoreCase(s, sub, startIndex);
        bh.consume(result);
    }

    @Benchmark
    public void benchCountIgnoreCase(Blackhole bh) {
        String s = "test test test test";
        String sub = "test";
        int result = StringUtil.countIgnoreCase(s, sub);
        bh.consume(result);
    }

    @Benchmark
    public void benchCount(Blackhole bh) {
        String s = "abababa";
        String sub = "aba";
        int result = StringUtil.count(s, sub);
        bh.consume(result);
    }

    // --- Split Benchmarks ---

    @Benchmark
    public void benchSplit(Blackhole bh) {
        String s = "a,b,c,d";
        String[] result = StringUtil.split(s, ",");
        bh.consume(result);
    }

    @Benchmark
    public void benchSplitCChar(Blackhole bh) {
        String s = "a,b,c,d";
        char[] delimiters = {',', ' '};
        String[] result = StringUtil.splitc(s, delimiters);
        bh.consume(result);
    }

    // --- Join Benchmarks ---

    @Benchmark
    public void benchJoinArray(Blackhole bh) {
        String[] arr = largeStringArray;
        String result = StringUtil.join(arr, " | ");
        bh.consume(result);
    }

    @Benchmark
    public void benchJoinObjectArray(Blackhole bh) {
        Object[] arr = objectArrayForJoin;
        String result = StringUtil.join(arr, "-");
        bh.consume(result);
    }

    @Benchmark
    public void benchJoinCollectionChar(Blackhole bh) {
        Collection<String> collection = Arrays.asList(collectionForJoin);
        String result = StringUtil.join(collection, ",");
        bh.consume(result);
    }

    @Benchmark
    public void benchJoinCollectionString(Blackhole bh) {
        Collection<String> collection = Arrays.asList(collectionForJoin);
        String result = StringUtil.join(collection, "---");
        bh.consume(result);
    }

    // --- Edge Case and Conversion Benchmarks ---

    @Benchmark
    public void benchCompressChars(Blackhole bh) {
        String s = "aaaaabbbbbcc";
        char c = 'a';
        String result = StringUtil.compressChars(s, c);
        bh.consume(result);
    }

    @Benchmark
    public void benchToHexString(Blackhole bh) {
        byte[] bytes = largeByteArray;
        String result = StringUtil.toHexString(bytes);
        bh.consume(result);
    }

    @Benchmark
    public void benchEscapeJava(Blackhole bh) {
        String s = "String with \\ and \" quotes.\nNewline\tTab\bBackspace";
        String result = StringUtil.escapeJava(s);
        bh.consume(result);
    }

    @Benchmark
    public void benchUnescapeJava(Blackhole bh) {
        String s = "String with \\\\ and \\\" quotes.\nNewline\tTab\bBackspace";
        String result = StringUtil.unescapeJava(s);
        bh.consume(result);
    }

    @Benchmark
    public void benchCutSurrounding(Blackhole bh) {
        String s = "prefix_data_suffix";
        String result = StringUtil.cutSurrounding(s, "prefix_", "_suffix");
        bh.consume(result);
    }
}

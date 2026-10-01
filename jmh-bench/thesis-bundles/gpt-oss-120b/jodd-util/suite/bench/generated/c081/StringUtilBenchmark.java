package bench.generated.c081;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.util.StringUtil;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.nio.charset.Charset;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class StringUtilBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        String source;
        String sub;
        String with;
        char subChar;
        char withChar;
        char[] subChars;
        char[] withChars;
        String[] stringArray;
        Object[] objectArray;
        Collection<Object> objectCollection;
        byte[] byteArray;
        Charset utf8;
        Charset iso88591;
        String longString;
        String camel;
        String snake;
        String lineWithTabs;
        String javaEscaped;
        String javaEscapedOriginal;
        String[] manyStrings;
        String[] manySubs;
        String[] manyWiths;
        int[] intArray;
        String[] emptyArray;
        String[] nullArray;

        @Setup(Level.Trial)
        public void setUp() {
            source = "The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog.";
            sub = "quick";
            with = "slow";
            subChar = 'q';
            withChar = 's';
            subChars = new char[] { 'q', 'b', 'j' };
            withChars = new char[] { 's', 'c', 'k' };
            stringArray = new String[] { "alpha", "beta", "gamma", "delta" };
            objectArray = new Object[] { "one", 2, 3.0, true };
            objectCollection = new ArrayList<>();
            objectCollection.add("col1");
            objectCollection.add(42);
            objectCollection.add('c');
            byteArray = new byte[] { 0x0A, 0x1B, 0x2C, 0x3D };
            utf8 = Charset.forName("UTF-8");
            iso88591 = Charset.forName("ISO-8859-1");
            longString = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. ".repeat(20);
            camel = "thisIsCamelCaseString";
            snake = "this_is_snake_case_string";
            lineWithTabs = "\tpublic\tstatic\tvoid\tmain(String[] args){\n\t\tSystem.out.println(\"Hello\");\n\t}";
            javaEscapedOriginal = "Line1\nLine2\t\"Quote\"\\Backslash";
            javaEscaped = StringUtil.escapeJava(javaEscapedOriginal);
            manyStrings = new String[] { "one", "two", "three", "four", "five" };
            manySubs = new String[] { "one", "two", "three" };
            manyWiths = new String[] { "ONE", "TWO", "THREE" };
            intArray = new int[] { 1, 2, 3, 4, 5 };
            emptyArray = new String[0];
            nullArray = null;
        }
    }

    // replace
    @Benchmark
    public String replace(BenchmarkState s) {
        return StringUtil.replace(s.source, s.sub, s.with);
    }

    @Benchmark
    public String replaceChar(BenchmarkState s) {
        return StringUtil.replaceChar(s.source, s.subChar, s.withChar);
    }

    @Benchmark
    public String replaceChars(BenchmarkState s) {
        return StringUtil.replaceChars(s.source, s.subChars, s.withChars);
    }

    @Benchmark
    public String replaceFirst(BenchmarkState s) {
        return StringUtil.replaceFirst(s.source, s.sub, s.with);
    }

    @Benchmark
    public String replaceFirstChar(BenchmarkState s) {
        return StringUtil.replaceFirst(s.source, s.subChar, s.withChar);
    }

    @Benchmark
    public String replaceLast(BenchmarkState s) {
        return StringUtil.replaceLast(s.source, s.sub, s.with);
    }

    @Benchmark
    public String replaceLastChar(BenchmarkState s) {
        return StringUtil.replaceLast(s.source, s.subChar, s.withChar);
    }

    // remove
    @Benchmark
    public String remove(BenchmarkState s) {
        return StringUtil.remove(s.source, s.sub);
    }

    @Benchmark
    public String removeCharsString(BenchmarkState s) {
        return StringUtil.removeChars(s.source, "aeiou");
    }

    @Benchmark
    public String removeCharsArray(BenchmarkState s) {
        return StringUtil.removeChars(s.source, 'a', 'e', 'i', 'o', 'u');
    }

    @Benchmark
    public String removeChar(BenchmarkState s) {
        return StringUtil.remove(s.source, s.subChar);
    }

    // equality and checks
    @Benchmark
    public boolean equals(BenchmarkState s) {
        return StringUtil.equals(s.source, s.source);
    }

    @Benchmark
    public boolean isEmpty(BenchmarkState s) {
        return StringUtil.isEmpty(s.source);
    }

    @Benchmark
    public boolean isBlank(BenchmarkState s) {
        return StringUtil.isBlank(s.source);
    }

    @Benchmark
    public boolean isAllEmpty(BenchmarkState s) {
        return StringUtil.isAllEmpty(s.emptyArray);
    }

    @Benchmark
    public boolean isAllBlank(BenchmarkState s) {
        return StringUtil.isAllBlank(s.emptyArray);
    }

    @Benchmark
    public boolean containsOnlyWhitespaces(BenchmarkState s) {
        return StringUtil.containsOnlyWhitespaces("   \t\n");
    }

    @Benchmark
    public boolean containsOnlyDigits(BenchmarkState s) {
        return StringUtil.containsOnlyDigits("123456");
    }

    @Benchmark
    public boolean containsOnlyDigitsAndSigns(BenchmarkState s) {
        return StringUtil.containsOnlyDigitsAndSigns("-123+456");
    }

    @Benchmark
    public boolean isNotEmpty(BenchmarkState s) {
        return StringUtil.isNotEmpty(s.source);
    }

    @Benchmark
    public String toString(BenchmarkState s) {
        return StringUtil.toString(s.objectArray);
    }

    @Benchmark
    public String toSafeString(BenchmarkState s) {
        return StringUtil.toSafeString(null);
    }

    @Benchmark
    public String[] toStringArray(BenchmarkState s) {
        return StringUtil.toStringArray(s.intArray);
    }

    // capitalize / case
    @Benchmark
    public String capitalize(BenchmarkState s) {
        return StringUtil.capitalize(s.source);
    }

    @Benchmark
    public String uncapitalize(BenchmarkState s) {
        return StringUtil.uncapitalize(s.source);
    }

    @Benchmark
    public String decapitalize(BenchmarkState s) {
        return StringUtil.decapitalize("URL");
    }

    @Benchmark
    public String title(BenchmarkState s) {
        return StringUtil.title(s.source);
    }

    @Benchmark
    public String truncate(BenchmarkState s) {
        return StringUtil.truncate(s.source, 50);
    }

    @Benchmark
    public String substring(BenchmarkState s) {
        return StringUtil.substring(s.source, -5, 0);
    }

    @Benchmark
    public boolean isSubstringAt(BenchmarkState s) {
        return StringUtil.isSubstringAt(s.source, "quick", 4);
    }

    // split
    @Benchmark
    public String[] split(BenchmarkState s) {
        return StringUtil.split(s.source, " ");
    }

    @Benchmark
    public String[] splitcString(BenchmarkState s) {
        return StringUtil.splitc(s.source, " ");
    }

    @Benchmark
    public String[] splitcCharArray(BenchmarkState s) {
        return StringUtil.splitc(s.source, new char[] { ' ', '.' });
    }

    @Benchmark
    public String[] splitcChar(BenchmarkState s) {
        return StringUtil.splitc(s.source, ' ');
    }

    // compress
    @Benchmark
    public String compressChars(BenchmarkState s) {
        return StringUtil.compressChars(s.source, ' ');
    }

    // indexOf
    @Benchmark
    public int indexOf(BenchmarkState s) {
        return StringUtil.indexOf(s.source, s.sub, 0, s.source.length());
    }

    @Benchmark
    public int indexOfChar(BenchmarkState s) {
        return StringUtil.indexOf(s.source, s.subChar, 0, s.source.length());
    }

    @Benchmark
    public int indexOfIgnoreCaseChar(BenchmarkState s) {
        return StringUtil.indexOfIgnoreCase(s.source, s.subChar, 0, s.source.length());
    }

    @Benchmark
    public int indexOfIgnoreCaseString(BenchmarkState s) {
        return StringUtil.indexOfIgnoreCase(s.source, s.sub);
    }

    @Benchmark
    public int lastIndexOfIgnoreCaseString(BenchmarkState s) {
        return StringUtil.lastIndexOfIgnoreCase(s.source, s.sub);
    }

    @Benchmark
    public int lastIndexOfString(BenchmarkState s) {
        return StringUtil.lastIndexOf(s.source, s.sub, s.source.length(), 0);
    }

    @Benchmark
    public int lastIndexOfChar(BenchmarkState s) {
        return StringUtil.lastIndexOf(s.source, s.subChar, s.source.length(), 0);
    }

    // starts/ends
    @Benchmark
    public boolean startsWithIgnoreCase(BenchmarkState s) {
        return StringUtil.startsWithIgnoreCase(s.source, "the");
    }

    @Benchmark
    public boolean endsWithIgnoreCase(BenchmarkState s) {
        return StringUtil.endsWithIgnoreCase(s.source, "dog.");
    }

    @Benchmark
    public boolean startsWithChar(BenchmarkState s) {
        return StringUtil.startsWithChar(s.source, 'T');
    }

    @Benchmark
    public boolean endsWithChar(BenchmarkState s) {
        return StringUtil.endsWithChar(s.source, '.');
    }

    // count
    @Benchmark
    public int count(BenchmarkState s) {
        return StringUtil.count(s.source, s.sub);
    }

    @Benchmark
    public int countChar(BenchmarkState s) {
        return StringUtil.count(s.source, s.subChar);
    }

    @Benchmark
    public int countIgnoreCase(BenchmarkState s) {
        return StringUtil.countIgnoreCase(s.source, "THE");
    }

    // array indexOf
    @Benchmark
    public int[] indexOfArray(BenchmarkState s) {
        return StringUtil.indexOf(s.source, s.manySubs);
    }

    @Benchmark
    public int[] indexOfIgnoreCaseArray(BenchmarkState s) {
        return StringUtil.indexOfIgnoreCase(s.source, s.manySubs);
    }

    @Benchmark
    public int[] lastIndexOfArray(BenchmarkState s) {
        return StringUtil.lastIndexOf(s.source, s.manySubs);
    }

    @Benchmark
    public int[] lastIndexOfIgnoreCaseArray(BenchmarkState s) {
        return StringUtil.lastIndexOfIgnoreCase(s.source, s.manySubs);
    }

    // array equals
    @Benchmark
    public boolean equalsStringArray(BenchmarkState s) {
        return StringUtil.equals(s.stringArray, s.stringArray);
    }

    @Benchmark
    public boolean equalsIgnoreCaseStringArray(BenchmarkState s) {
        return StringUtil.equalsIgnoreCase(s.stringArray, s.stringArray);
    }

    // replace with arrays
    @Benchmark
    public String replaceArray(BenchmarkState s) {
        return StringUtil.replace(s.source, s.manySubs, s.manyWiths);
    }

    @Benchmark
    public String replaceIgnoreCaseArray(BenchmarkState s) {
        return StringUtil.replaceIgnoreCase(s.source, s.manySubs, s.manyWiths);
    }

    // equalsOne
    @Benchmark
    public int equalsOne(BenchmarkState s) {
        return StringUtil.equalsOne(s.source, s.manySubs);
    }

    @Benchmark
    public int equalsOneIgnoreCase(BenchmarkState s) {
        return StringUtil.equalsOneIgnoreCase(s.source, s.manySubs);
    }

    // startsWithOne
    @Benchmark
    public int startsWithOne(BenchmarkState s) {
        return StringUtil.startsWithOne(s.source, s.manySubs);
    }

    @Benchmark
    public int startsWithOneIgnoreCase(BenchmarkState s) {
        return StringUtil.startsWithOneIgnoreCase(s.source, s.manySubs);
    }

    // endsWithOne
    @Benchmark
    public int endsWithOne(BenchmarkState s) {
        return StringUtil.endsWithOne(s.source, s.manySubs);
    }

    @Benchmark
    public int endsWithOneIgnoreCase(BenchmarkState s) {
        return StringUtil.endsWithOneIgnoreCase(s.source, s.manySubs);
    }

    // indexOfChars
    @Benchmark
    public int indexOfCharsString(BenchmarkState s) {
        return StringUtil.indexOfChars(s.source, "aeiou");
    }

    @Benchmark
    public int indexOfCharsArray(BenchmarkState s) {
        return StringUtil.indexOfChars(s.source, new char[] { 'a', 'e', 'i', 'o', 'u' });
    }

    // whitespace indexes
    @Benchmark
    public int indexOfWhitespace(BenchmarkState s) {
        return StringUtil.indexOfWhitespace(s.source);
    }

    @Benchmark
    public int indexOfNonWhitespace(BenchmarkState s) {
        return StringUtil.indexOfNonWhitespace(s.source);
    }

    // strip
    @Benchmark
    public String stripLeadingChar(BenchmarkState s) {
        return StringUtil.stripLeadingChar(s.source, 'T');
    }

    @Benchmark
    public String stripTrailingChar(BenchmarkState s) {
        return StringUtil.stripTrailingChar(s.source, '.');
    }

    @Benchmark
    public String stripChar(BenchmarkState s) {
        return StringUtil.stripChar(s.source, ' ');
    }

    @Benchmark
    public String stripToChar(BenchmarkState s) {
        return StringUtil.stripToChar(s.source, 'q');
    }

    @Benchmark
    public String stripFromChar(BenchmarkState s) {
        return StringUtil.stripFromChar(s.source, 'q');
    }

    // trim
    @Benchmark
    public void trimAll(BenchmarkState s, Blackhole bh) {
        String[] arr = s.stringArray.clone();
        StringUtil.trimAll(arr);
        bh.consume(arr);
    }

    @Benchmark
    public void trimDownAll(BenchmarkState s, Blackhole bh) {
        String[] arr = s.stringArray.clone();
        StringUtil.trimDownAll(arr);
        bh.consume(arr);
    }

    @Benchmark
    public String trimDown(BenchmarkState s) {
        return StringUtil.trimDown("   hello   ");
    }

    @Benchmark
    public String crop(BenchmarkState s) {
        return StringUtil.crop("");
    }

    @Benchmark
    public void cropAll(BenchmarkState s, Blackhole bh) {
        String[] arr = s.stringArray.clone();
        StringUtil.cropAll(arr);
        bh.consume(arr);
    }

    @Benchmark
    public String trimLeft(BenchmarkState s) {
        return StringUtil.trimLeft("   left");
    }

    @Benchmark
    public String trimRight(BenchmarkState s) {
        return StringUtil.trimRight("right   ");
    }

    // region
    @Benchmark
    public int[] indexOfRegionSimple(BenchmarkState s) {
        return StringUtil.indexOfRegion(s.source, "quick", "dog");
    }

    @Benchmark
    public int[] indexOfRegionEscaped(BenchmarkState s) {
        return StringUtil.indexOfRegion(s.source, "quick", "dog", '\\');
    }

    // join
    @Benchmark
    public String joinObjectArray(BenchmarkState s) {
        return StringUtil.join(s.objectArray);
    }

    @Benchmark
    public String joinObjectArrayChar(BenchmarkState s) {
        return StringUtil.join(s.objectArray, ',');
    }

    @Benchmark
    public String joinCollectionChar(BenchmarkState s) {
        return StringUtil.join(s.objectCollection, ',');
    }

    @Benchmark
    public String joinCollectionString(BenchmarkState s) {
        return StringUtil.join(s.objectCollection, ",");
    }

    @Benchmark
    public String joinObjectArrayString(BenchmarkState s) {
        return StringUtil.join(s.objectArray, ",");
    }

    // charset conversion
    @Benchmark
    public String convertCharset(BenchmarkState s) {
        return StringUtil.convertCharset(s.source, s.utf8, s.iso88591);
    }

    // isCharAtEqual
    @Benchmark
    public boolean isCharAtEqual(BenchmarkState s) {
        return StringUtil.isCharAtEqual(s.source, 0, s.source.charAt(0));
    }

    // surround / prefix / suffix
    @Benchmark
    public String surround(BenchmarkState s) {
        return StringUtil.surround(s.source, "*");
    }

    @Benchmark
    public String prefix(BenchmarkState s) {
        return StringUtil.prefix(s.source, ">>");
    }

    @Benchmark
    public String suffix(BenchmarkState s) {
        return StringUtil.suffix(s.source, "<<");
    }

    // cut
    @Benchmark
    public String cutToIndexOf(BenchmarkState s) {
        return StringUtil.cutToIndexOf(s.source, "fox");
    }

    @Benchmark
    public String cutFromIndexOf(BenchmarkState s) {
        return StringUtil.cutFromIndexOf(s.source, "fox");
    }

    @Benchmark
    public String cutPrefix(BenchmarkState s) {
        return StringUtil.cutPrefix(s.source, "The");
    }

    @Benchmark
    public String cutSuffix(BenchmarkState s) {
        return StringUtil.cutSuffix(s.source, "dog.");
    }

    @Benchmark
    public String cutSurrounding(BenchmarkState s) {
        return StringUtil.cutSurrounding(s.source, "The", "dog.");
    }

    @Benchmark
    public String cutBetween(BenchmarkState s) {
        return StringUtil.cutBetween(s.source, "quick", "jumps");
    }

    // escaped
    @Benchmark
    public boolean isCharAtEscaped(BenchmarkState s) {
        return StringUtil.isCharAtEscaped("a\\b", 2, '\\');
    }

    @Benchmark
    public int indexOfUnescapedChar(BenchmarkState s) {
        return StringUtil.indexOfUnescapedChar("a\\b", 'b', '\\');
    }

    // insert
    @Benchmark
    public String insert(BenchmarkState s) {
        return StringUtil.insert(s.source, "[INSERT]", 10);
    }

    // repeat
    @Benchmark
    public String repeatString(BenchmarkState s) {
        return StringUtil.repeat("abc", 10);
    }

    @Benchmark
    public String repeatChar(BenchmarkState s) {
        return StringUtil.repeat('x', 10);
    }

    // reverse
    @Benchmark
    public String reverse(BenchmarkState s) {
        return StringUtil.reverse(s.source);
    }

    // common prefix
    @Benchmark
    public String maxCommonPrefix(BenchmarkState s) {
        return StringUtil.maxCommonPrefix("abcdef", "abcxyz");
    }

    @Benchmark
    public String findCommonPrefix(BenchmarkState s) {
        return StringUtil.findCommonPrefix(s.stringArray);
    }

    // shorten
    @Benchmark
    public String shorten(BenchmarkState s) {
        return StringUtil.shorten(s.longString, 100, "...");
    }

    // case conversion
    @Benchmark
    public String toLowerCase(BenchmarkState s) {
        return StringUtil.toLowerCase(s.source);
    }

    @Benchmark
    public String toUpperCase(BenchmarkState s) {
        return StringUtil.toUpperCase(s.source);
    }

    // remove quotes
    @Benchmark
    public String removeQuotes(BenchmarkState s) {
        return StringUtil.removeQuotes("\"quoted\"");
    }

    // hex
    @Benchmark
    public String toHexString(BenchmarkState s) {
        return StringUtil.toHexString(s.byteArray);
    }

    // functional
    @Benchmark
    public String ifNotNull(BenchmarkState s) {
        return StringUtil.ifNotNull(s.source, new Function<String, String>() {
            @Override
            public String apply(String t) {
                return t.toUpperCase();
            }
        });
    }

    // detect quote
    @Benchmark
    public char detectQuoteChar(BenchmarkState s) {
        return StringUtil.detectQuoteChar("\"test\"");
    }

    // camel case conversion
    @Benchmark
    public String fromCamelCase(BenchmarkState s) {
        return StringUtil.fromCamelCase(s.camel, '_');
    }

    @Benchmark
    public String toCamelCase(BenchmarkState s) {
        return StringUtil.toCamelCase(s.snake, false, '_');
    }

    // tabs to spaces
    @Benchmark
    public String convertTabsToSpaces(BenchmarkState s) {
        return StringUtil.convertTabsToSpaces(s.lineWithTabs, 4);
    }

    // escape/unescape java
    @Benchmark
    public String escapeJava(BenchmarkState s) {
        return StringUtil.escapeJava(s.javaEscapedOriginal);
    }

    @Benchmark
    public String unescapeJava(BenchmarkState s) {
        return StringUtil.unescapeJava(s.javaEscaped);
    }
}

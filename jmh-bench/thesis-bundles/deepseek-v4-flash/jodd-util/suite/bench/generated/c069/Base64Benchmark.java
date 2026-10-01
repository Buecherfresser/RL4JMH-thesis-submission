package bench.generated.c069;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import jodd.util.Base64;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Base64Benchmark {

    private byte[] rawBytes;
    private String rawString;

    private char[] encodedCharsNoSep;
    private char[] encodedCharsSep;
    private byte[] encodedBytesNoSep;
    private byte[] encodedBytesSep;
    private String encodedStringNoSep;
    private String encodedStringSep;

    @Setup(Level.Trial)
    public void setup() {
        // Build a representative ASCII string of ~1024 bytes
        StringBuilder sb = new StringBuilder();
        String base = "The quick brown fox jumps over the lazy dog 1234567890!@#$%^&*()_+-=[]{}|;':\",./<>?`~";
        while (sb.length() < 1024) {
            sb.append(base);
        }
        rawString = sb.substring(0, 1024);
        rawBytes = rawString.getBytes(StandardCharsets.UTF_8);

        // Pre-encode for decode benchmarks
        encodedCharsNoSep = Base64.encodeToChar(rawBytes, false);
        encodedCharsSep = Base64.encodeToChar(rawBytes, true);
        encodedBytesNoSep = Base64.encodeToByte(rawBytes, false);
        encodedBytesSep = Base64.encodeToByte(rawBytes, true);
        encodedStringNoSep = Base64.encodeToString(rawBytes, false);
        encodedStringSep = Base64.encodeToString(rawBytes, true);
    }

    // --- encodeToChar ---
    @Benchmark
    public char[] encodeToCharNoSep() {
        return Base64.encodeToChar(rawBytes, false);
    }

    @Benchmark
    public char[] encodeToCharSep() {
        return Base64.encodeToChar(rawBytes, true);
    }

    // --- encodeToByte ---
    @Benchmark
    public byte[] encodeToByteNoSep() {
        return Base64.encodeToByte(rawBytes, false);
    }

    @Benchmark
    public byte[] encodeToByteSep() {
        return Base64.encodeToByte(rawBytes, true);
    }

    // --- encodeToString (byte[]) ---
    @Benchmark
    public String encodeToStringNoSep() {
        return Base64.encodeToString(rawBytes, false);
    }

    @Benchmark
    public String encodeToStringSep() {
        return Base64.encodeToString(rawBytes, true);
    }

    // --- encodeToString (String) ---
    @Benchmark
    public String encodeToStringFromStringNoSep() {
        return Base64.encodeToString(rawString, false);
    }

    @Benchmark
    public String encodeToStringFromStringSep() {
        return Base64.encodeToString(rawString, true);
    }

    // --- encodeToByte (String) ---
    @Benchmark
    public byte[] encodeToByteFromStringNoSep() {
        return Base64.encodeToByte(rawString, false);
    }

    @Benchmark
    public byte[] encodeToByteFromStringSep() {
        return Base64.encodeToByte(rawString, true);
    }

    // --- decode (char[]) ---
    @Benchmark
    public byte[] decodeCharsNoSep() {
        return Base64.decode(encodedCharsNoSep);
    }

    @Benchmark
    public byte[] decodeCharsSep() {
        return Base64.decode(encodedCharsSep);
    }

    // --- decode (byte[]) ---
    @Benchmark
    public byte[] decodeBytesNoSep() {
        return Base64.decode(encodedBytesNoSep);
    }

    @Benchmark
    public byte[] decodeBytesSep() {
        return Base64.decode(encodedBytesSep);
    }

    // --- decode (String) ---
    @Benchmark
    public byte[] decodeStringNoSep() {
        return Base64.decode(encodedStringNoSep);
    }

    @Benchmark
    public byte[] decodeStringSep() {
        return Base64.decode(encodedStringSep);
    }

    // --- decodeToString (byte[]) ---
    @Benchmark
    public String decodeToStringBytesNoSep() {
        return Base64.decodeToString(encodedBytesNoSep);
    }

    @Benchmark
    public String decodeToStringBytesSep() {
        return Base64.decodeToString(encodedBytesSep);
    }

    // --- decodeToString (String) ---
    @Benchmark
    public String decodeToStringStringNoSep() {
        return Base64.decodeToString(encodedStringNoSep);
    }

    @Benchmark
    public String decodeToStringStringSep() {
        return Base64.decodeToString(encodedStringSep);
    }
}

package bench.generated.c069;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.util.Base64;
import java.nio.charset.StandardCharsets;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Base64Benchmark {

    private byte[] rawBytes;
    private String rawString;
    private char[] encodedChars;
    private byte[] encodedBytes;
    private String encodedString;

    @Setup(Level.Trial)
    public void setup() {
        // deterministic payload of 1024 bytes
        rawBytes = new byte[1024];
        for (int i = 0; i < rawBytes.length; i++) {
            rawBytes[i] = (byte) (i & 0xFF);
        }
        rawString = new String(rawBytes, StandardCharsets.UTF_8);

        // pre‑compute encoded forms (no line separators)
        encodedChars = Base64.encodeToChar(rawBytes, false);
        encodedBytes = Base64.encodeToByte(rawBytes, false);
        encodedString = Base64.encodeToString(rawBytes, false);
    }

    // -----------------------------------------------------------------
    // Encoding benchmarks
    // -----------------------------------------------------------------

    @Benchmark
    public char[] encodeToCharNoLineSep() {
        return Base64.encodeToChar(rawBytes, false);
    }

    @Benchmark
    public char[] encodeToCharWithLineSep() {
        return Base64.encodeToChar(rawBytes, true);
    }

    @Benchmark
    public byte[] encodeToByteNoLineSep() {
        return Base64.encodeToByte(rawBytes, false);
    }

    @Benchmark
    public byte[] encodeToByteWithLineSep() {
        return Base64.encodeToByte(rawBytes, true);
    }

    @Benchmark
    public byte[] encodeToByteFromString() {
        return Base64.encodeToByte(rawString);
    }

    @Benchmark
    public byte[] encodeToByteFromStringLineSep() {
        return Base64.encodeToByte(rawString, true);
    }

    @Benchmark
    public String encodeToStringFromBytesNoLineSep() {
        return Base64.encodeToString(rawBytes, false);
    }

    @Benchmark
    public String encodeToStringFromBytesLineSep() {
        return Base64.encodeToString(rawBytes, true);
    }

    @Benchmark
    public String encodeToStringFromStringNoLineSep() {
        return Base64.encodeToString(rawString, false);
    }

    @Benchmark
    public String encodeToStringFromStringLineSep() {
        return Base64.encodeToString(rawString, true);
    }

    // -----------------------------------------------------------------
    // Decoding benchmarks
    // -----------------------------------------------------------------

    @Benchmark
    public byte[] decodeCharArray() {
        return Base64.decode(encodedChars);
    }

    @Benchmark
    public byte[] decodeByteArray() {
        return Base64.decode(encodedBytes);
    }

    @Benchmark
    public byte[] decodeString() {
        return Base64.decode(encodedString);
    }

    @Benchmark
    public String decodeToStringFromByteArray() {
        return Base64.decodeToString(encodedBytes);
    }

    @Benchmark
    public String decodeToStringFromString() {
        return Base64.decodeToString(encodedString);
    }
}

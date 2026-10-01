package bench.generated.c069;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import jodd.util.Base64;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Base64Benchmark {

    // --- State Fields ---
    private byte[] rawBytes;
    private String rawString;
    private char[] encodedChars;
    private byte[] encodedBytesNoSep;
    private byte[] encodedBytesWithSep;
    private String decodedString;

    // --- Setup ---
    @Setup
    public void setup() {
        // 1. Setup raw byte array payload
        String originalString = "This is a test string for Base64 encoding, including some special characters: \u00A9\u00A9";
        this.rawString = originalString;
        this.rawBytes = originalString.getBytes(StandardCharsets.UTF_8);

        // 2. Setup char array derived from raw bytes (for char-based decode)
        this.encodedChars = Base64.encodeToChar(this.rawBytes, false);

        // 3. Setup byte array results
        this.encodedBytesNoSep = Base64.encodeToByte(this.rawBytes);
        this.encodedBytesWithSep = Base64.encodeToByte(this.rawBytes, true);

        // 4. Setup decoded string result
        this.decodedString = Base64.decodeToString(this.encodedBytesNoSep);
    }

    // --- Benchmarks: Encoding ---

    @Benchmark
    public void encodeBytesNoSep(Blackhole bh) {
        byte[] result = Base64.encodeToByte(rawBytes);
        bh.consume(result);
    }

    @Benchmark
    public void encodeBytesWithSep(Blackhole bh) {
        byte[] result = Base64.encodeToByte(rawBytes, true);
        bh.consume(result);
    }

    @Benchmark
    public void encodeStringNoSep(Blackhole bh) {
        byte[] result = Base64.encodeToByte(rawString);
        bh.consume(result);
    }

    @Benchmark
    public void encodeStringWithSep(Blackhole bh) {
        byte[] result = Base64.encodeToByte(rawString, true);
        bh.consume(result);
    }

    // --- Benchmarks: Decoding ---

    @Benchmark
    public void decodeBytes(Blackhole bh) {
        byte[] result = Base64.decode(encodedBytesNoSep);
        bh.consume(result);
    }

    @Benchmark
    public void decodeString(Blackhole bh) {
        byte[] result = Base64.decode(rawString);
        bh.consume(result);
    }

    @Benchmark
    public void decodeStringFromBytes(Blackhole bh) {
        String result = Base64.decodeToString(encodedBytesNoSep);
        bh.consume(result);
    }
}

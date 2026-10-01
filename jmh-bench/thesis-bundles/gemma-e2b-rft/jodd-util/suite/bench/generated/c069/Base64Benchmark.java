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
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class Base64Benchmark {

    // --- Input Data Setup ---
    private byte[] testBytes;
    private String testString;
    private char[] testCharArray;
    private byte[] encodedBytes;
    private String encodedString;

    @Setup
    public void setup() {
        // Input 1: Standard ASCII payload
        String asciiString = "This is a test string for Base64 encoding.";
        this.testString = asciiString;
        this.testBytes = asciiString.getBytes(StandardCharsets.UTF_8);

        // Input 2: Payload with complex UTF-8 characters
        String utf8String = "Hello, world! 🚀 Base64 test with special characters: ñéü";
        this.testString = utf8String;
        this.testBytes = utf8String.getBytes(StandardCharsets.UTF_8);

        // Input 3: A larger, repetitive byte array for throughput testing
        byte[] largeBytes = new byte[1024 * 1024]; // 1MB
        for (int i = 0; i < largeBytes.length; i++) {
            largeBytes[i] = (byte) (i % 256);
        }
        this.testBytes = largeBytes;

        // Pre-calculate encoded results for string/byte conversions
        this.encodedString = Base64.encodeToString(testString);
        this.encodedBytes = Base64.encodeToByte(testBytes, false);
        this.testCharArray = Base64.encodeToChar(testBytes, false);
    }

    // --- Encoding Benchmarks (Byte Array -> Various Outputs) ---

    @Benchmark
    public void encodeBytesNoLineSep(Blackhole bh) {
        byte[] result = Base64.encodeToByte(testBytes);
        bh.consume(result);
    }

    @Benchmark
    public void encodeBytesWithLineSep(Blackhole bh) {
        byte[] result = Base64.encodeToByte(testBytes, true);
        bh.consume(result);
    }

    @Benchmark
    public void encodeStringNoLineSep(Blackhole bh) {
        String result = Base64.encodeToString(testString);
        bh.consume(result);
    }

    @Benchmark
    public void encodeStringWithLineSep(Blackhole bh) {
        String result = Base64.encodeToString(testString, true);
        bh.consume(result);
    }

    // --- Encoding Benchmarks (String -> Byte Array) ---

    @Benchmark
    public void encodeStringToBytesNoLineSep(Blackhole bh) {
        byte[] result = Base64.encodeToByte(testString);
        bh.consume(result);
    }

    @Benchmark
    public void encodeStringToBytesWithLineSep(Blackhole bh) {
        byte[] result = Base64.encodeToByte(testString, true);
        bh.consume(result);
    }

    // --- Encoding Benchmarks (Byte Array -> Char Array) ---

    @Benchmark
    public void encodeBytesToCharArrayNoLineSep(Blackhole bh) {
        char[] result = Base64.encodeToChar(testBytes, false);
        bh.consume(result);
    }

    @Benchmark
    public void encodeBytesToCharArrayWithLineSep(Blackhole bh) {
        char[] result = Base64.encodeToChar(testBytes, true);
        bh.consume(result);
    }

    // --- Decoding Benchmarks (Byte Array -> Various Outputs) ---

    @Benchmark
    public void decodeBytesToBytes(Blackhole bh) {
        byte[] result = Base64.decode(testBytes);
        bh.consume(result);
    }

    @Benchmark
    public void decodeBytesToByteString(Blackhole bh) {
        byte[] result = Base64.decode(testBytes);
        String resultString = Base64.decodeToString(result);
        bh.consume(resultString);
    }

    // --- Decoding Benchmarks (String -> Various Outputs) ---

    @Benchmark
    public void decodeStringToBytes(Blackhole bh) {
        byte[] result = Base64.decode(encodedString);
        bh.consume(result);
    }

    @Benchmark
    public void decodeStringToByteString(Blackhole bh) {
        String result = Base64.decodeToString(encodedString);
        bh.consume(result);
    }

    // --- Decoding Benchmarks (Char Array -> Byte Array) ---

    @Benchmark
    public void decodeCharArrayToBytesNoLineSep(Blackhole bh) {
        byte[] result = Base64.decode(testCharArray);
        bh.consume(result);
    }

    @Benchmark
    public void decodeCharArrayToBytesWithLineSep(Blackhole bh) {
        byte[] result = Base64.decode(testCharArray);
        bh.consume(result);
    }
}

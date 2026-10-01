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

    private byte[] inputBytes;
    private String inputString;
    private byte[] encodedBytes;
    private String encodedString;
    private final int INPUT_SIZE = 4096;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Create raw input data
        inputBytes = new byte[INPUT_SIZE];
        for (int i = 0; i < INPUT_SIZE; i++) {
            inputBytes[i] = (byte) (i % 256);
        }
        inputString = new String(inputBytes, StandardCharsets.UTF_8);

        // 2. Pre-calculate encoded inputs for decoding benchmarks
        // We use non-line-separated versions for consistent input size
        encodedBytes = Base64.encodeToByte(inputBytes, false);
        encodedString = Base64.encodeToString(inputBytes, false);
    }

    // --- Encoding Tests (Byte Array Input) ---

    @Benchmark
    public void encodeToChar_NoLineSeparator(Blackhole bh) {
        char[] result = Base64.encodeToChar(inputBytes, false);
        bh.consume(result);
    }

    @Benchmark
    public void encodeToChar_WithLineSeparator(Blackhole bh) {
        char[] result = Base64.encodeToChar(inputBytes, true);
        bh.consume(result);
    }

    @Benchmark
    public void encodeToByte_NoLineSeparator(Blackhole bh) {
        byte[] result = Base64.encodeToByte(inputBytes, false);
        bh.consume(result);
    }

    @Benchmark
    public void encodeToByte_WithLineSeparator(Blackhole bh) {
        byte[] result = Base64.encodeToByte(inputBytes, true);
        bh.consume(result);
    }

    // --- Encoding Tests (String Input) ---

    @Benchmark
    public void encodeToString_NoLineSeparator(Blackhole bh) {
        String result = Base64.encodeToString(inputString, false);
        bh.consume(result);
    }

    @Benchmark
    public void encodeToString_WithLineSeparator(Blackhole bh) {
        String result = Base64.encodeToString(inputString, true);
        bh.consume(result);
    }

    // --- Decoding Tests (Byte Array Input) ---

    @Benchmark
    public void decode_ByteInput(Blackhole bh) {
        // Use pre-calculated encoded input
        byte[] result = Base64.decode(encodedBytes);
        bh.consume(result);
    }

    // --- Decoding Tests (String Input) ---

    @Benchmark
    public void decodeToString_StringInput(Blackhole bh) {
        // Use pre-calculated encoded input. Must use Base64.decode(String) to get byte[].
        byte[] result = Base64.decode(encodedString);
        bh.consume(result);
    }
}

package bench.generated.c069;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import jodd.util.Base64;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Base64Benchmark {

    // Since Base64 methods are static and pure functions, we don't strictly need @State fields
    // unless we were testing a mutating object. We rely on local variables or method arguments.

    @Benchmark
    public void encodeToString_NoLineSep(Blackhole bh) {
        String input = "This is a test string for Base64 encoding.";
        try {
            String result = Base64.encodeToString(input);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions for benchmarking purposes if they are expected to be rare
        }
    }

    @Benchmark
    public void decodeToString_NoLineSep(Blackhole bh) {
        // "Hello World!" encoded in Base64
        String input = "SGVsbG8gV29ybGQh"; 
        try {
            byte[] decodedBytes = Base64.decode(input);
            String result = new String(decodedBytes, StandardCharsets.UTF_8);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void encodeToString_WithLineSep(Blackhole bh) {
        String input = "A very long string that might require line separation for testing purposes.";
        try {
            String result = Base64.encodeToString(input, true);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void encodeToByte_String(Blackhole bh) {
        String input = "Test data for byte encoding.";
        try {
            byte[] result = Base64.encodeToByte(input);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void decode_Byte(Blackhole bh) {
        // A known valid Base64 string for "Hello World!"
        String encodedString = "SGVsbG8gV29ybGQh";
        try {
            byte[] result = Base64.decode(encodedString.getBytes(StandardCharsets.UTF_8));
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void decodeToString_Byte(Blackhole bh) {
        // A known valid Base64 string for "Hello World!"
        String encodedString = "SGVsbG8gV29ybGQh";
        try {
            byte[] result = Base64.decode(encodedString);
            String decoded = new String(result, StandardCharsets.UTF_8);
            bh.consume(decoded);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}

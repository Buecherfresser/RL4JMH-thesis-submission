package bench.generated.c067;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import jodd.typeconverter.impl.UUIDConverter;
import jodd.typeconverter.TypeConversionException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class UUIDConverterBenchmark {

    private UUIDConverter converter;

    @Setup
    public void setup() {
        // Initialize the converter instance. Since the method is not static,
        // we need an instance.
        this.converter = new UUIDConverter();
    }

    @Benchmark
    public void testNullInput(Blackhole bh) {
        try {
            UUID result = converter.convert(null);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions for null input path if they are expected to be handled gracefully
        }
    }

    @Benchmark
    public void testUuidInput(Blackhole bh) {
        UUID uuid = UUID.randomUUID();
        try {
            UUID result = converter.convert(uuid);
            bh.consume(result);
        } catch (Exception e) {
            // Should not happen for a valid UUID input
        }
    }

    @Benchmark
    public void testCharSequenceInput(Blackhole bh) {
        // Use a valid UUID string to test UUID.fromString path
        String uuidString = UUID.randomUUID().toString();
        try {
            UUID result = converter.convert(uuidString);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions for invalid string input if they are expected
        }
    }

    @Benchmark
    public void testByteArrayInput(Blackhole bh) {
        // Test the byte array path (UUID.nameUUIDFromBytes)
        // We use 16 bytes, which is the standard size for UUID bytes.
        byte[] bytes = new byte[16];
        // Fill with some non-zero data to ensure the conversion logic runs
        for (int i = 0; i < 16; i++) {
            bytes[i] = (byte) i;
        }
        try {
            UUID result = converter.convert(bytes);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}

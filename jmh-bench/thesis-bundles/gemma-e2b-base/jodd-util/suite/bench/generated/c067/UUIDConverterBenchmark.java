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

    // Input fixtures built in @Setup
    private UUID uuidInput;
    private String uuidStringInput;
    private byte[] byteArrayInput;

    @Setup
    public void setup() {
        converter = new UUIDConverter();

        // 1. UUID Input
        this.uuidInput = UUID.randomUUID();

        // 2. CharSequence Input (String representation of UUID)
        this.uuidStringInput = this.uuidInput.toString();

        // 3. Byte Array Input (Input for nameUUIDFromBytes)
        // Generate a byte array that is likely to produce a valid UUID name
        this.byteArrayInput = new byte[16];
        for (int i = 0; i < 16; i++) {
            this.byteArrayInput[i] = (byte) (i % 256);
        }
    }

    @Benchmark
    public void testConvert_UUID(Blackhole bh) {
        UUID result = converter.convert(uuidInput);
        bh.consume(result);
    }

    @Benchmark
    public void testConvert_CharSequence(Blackhole bh) {
        UUID result = converter.convert(uuidStringInput);
        bh.consume(result);
    }

    @Benchmark
    public void testConvert_ByteArray(Blackhole bh) {
        UUID result = converter.convert(byteArrayInput);
        bh.consume(result);
    }

    @Benchmark
    public void testConvert_Null(Blackhole bh) {
        UUID result = converter.convert(null);
        bh.consume(result);
    }
}

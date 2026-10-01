package bench.generated.c067;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.UUIDConverter;
import java.util.UUID;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class UUIDConverterBenchmark {

    private UUIDConverter converter;
    private UUID testUuid;
    private String testUuidString;
    private byte[] testUuidBytes;

    @Setup(Level.Trial)
    public void setup() {
        converter = new UUIDConverter();
        
        // 1. UUID object input
        testUuid = UUID.randomUUID();
        
        // 2. CharSequence (String) input
        testUuidString = testUuid.toString();
        
        // 3. byte[] input (16 bytes is standard for UUID representation)
        // We use a fixed 16-byte array for consistent benchmarking of nameUUIDFromBytes
        testUuidBytes = new byte[16];
        for (int i = 0; i < 16; i++) {
            testUuidBytes[i] = (byte) (i % 256);
        }
    }

    @Benchmark
    public UUID convertFromUuid(Blackhole bh) {
        UUID result = converter.convert(testUuid);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public UUID convertFromString(Blackhole bh) {
        UUID result = converter.convert(testUuidString);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public UUID convertFromBytes(Blackhole bh) {
        // The converter expects an array of bytes for this path
        UUID result = converter.convert(testUuidBytes);
        bh.consume(result);
        return result;
    }
}

package bench.generated.c067;

import jodd.typeconverter.impl.UUIDConverter;
import org.openjdk.jmh.annotations.*;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class UUIDConverterBenchmark {

    private UUIDConverter converter;
    private UUID uuid;
    private String uuidString;
    private byte[] uuidBytes;

    @Setup
    public void setup() {
        converter = new UUIDConverter();
        uuid = UUID.randomUUID();
        uuidString = uuid.toString();
        uuidBytes = new byte[16];
        // Fill with deterministic bytes (e.g., from the UUID)
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        for (int i = 0; i < 8; i++) {
            uuidBytes[i] = (byte) (msb >>> (8 * (7 - i)));
            uuidBytes[8 + i] = (byte) (lsb >>> (8 * (7 - i)));
        }
    }

    @Benchmark
    public UUID convertUUID() {
        return converter.convert(uuid);
    }

    @Benchmark
    public UUID convertString() {
        return converter.convert(uuidString);
    }

    @Benchmark
    public UUID convertBytes() {
        return converter.convert(uuidBytes);
    }

    @Benchmark
    public UUID convertNull() {
        return converter.convert(null);
    }
}

package bench.generated.c067;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.UUIDConverter;
import java.util.UUID;
import java.nio.charset.StandardCharsets;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class UUIDConverterBenchmark {

    private UUIDConverter converter;
    private UUID sampleUuid;
    private String uuidString;
    private byte[] uuidBytes;

    @Setup
    public void setup() {
        converter = new UUIDConverter();
        sampleUuid = UUID.randomUUID();
        uuidString = sampleUuid.toString();
        uuidBytes = uuidString.getBytes(StandardCharsets.UTF_8);
    }

    @Benchmark
    public UUID convertFromUuid() {
        return converter.convert(sampleUuid);
    }

    @Benchmark
    public UUID convertFromString() {
        return converter.convert(uuidString);
    }

    @Benchmark
    public UUID convertFromByteArray() {
        return converter.convert(uuidBytes);
    }

    @Benchmark
    public UUID convertFromNull() {
        return converter.convert(null);
    }
}

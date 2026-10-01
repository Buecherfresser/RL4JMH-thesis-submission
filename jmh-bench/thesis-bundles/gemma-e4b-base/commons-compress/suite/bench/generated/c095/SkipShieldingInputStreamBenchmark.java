package bench.generated.c095;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.utils.SkipShieldingInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SkipShieldingInputStreamBenchmark {

    private ByteArrayInputStream underlyingStream;
    private SkipShieldingInputStream shieldedStream;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // Create a large underlying stream to ensure skips are meaningful
        // 1MB of data
        byte[] data = new byte[1024 * 1024];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (i % 256);
        }
        underlyingStream = new ByteArrayInputStream(data);
        shieldedStream = new SkipShieldingInputStream(underlyingStream);
    }

    @Benchmark
    public long skipSmallAmount(Blackhole bh) throws IOException {
        // Skip a small amount (e.g., 100 bytes)
        long result = shieldedStream.skip(100);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long skipBufferAmount(Blackhole bh) throws IOException {
        // Skip an amount equal to the internal buffer size (8192 bytes)
        long result = shieldedStream.skip(8192);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long skipLargeAmount(Blackhole bh) throws IOException {
        // Skip a large amount (e.g., 1MB)
        long result = shieldedStream.skip(1024 * 1024);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long skipNegativeAmount(Blackhole bh) throws IOException {
        // Test negative skip amount (should return 0)
        long result = shieldedStream.skip(-10);
        bh.consume(result);
        return result;
    }
}

package bench.generated.c006;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.arj.ArjArchiveInputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ArjArchiveInputStreamBenchmark {

    private byte[] arjSignature;

    @Setup
    public void setUp() {
        arjSignature = new byte[2];
        arjSignature[0] = (byte) 0x60; // ARJ_MAGIC_1
        arjSignature[1] = (byte) 0xEA; // ARJ_MAGIC_2
    }

    @Benchmark
    public boolean benchmarkMatchesSignature() {
        return ArjArchiveInputStream.matches(arjSignature, arjSignature.length);
    }
}

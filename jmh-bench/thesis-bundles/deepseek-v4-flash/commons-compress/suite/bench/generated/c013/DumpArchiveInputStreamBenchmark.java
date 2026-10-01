package bench.generated.c013;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.apache.commons.compress.archivers.dump.DumpArchiveInputStream;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DumpArchiveInputStreamBenchmark {

    @State(Scope.Benchmark)
    public static class DumpState {
        byte[] header;

        @Setup
        public void setup() {
            // Create a 1 KiB header (TP_SIZE) with a simple pattern.
            // The matches() method only checks the first 32 bytes for magic,
            // and if length >= TP_SIZE it verifies the checksum.
            // We use a non‑valid header; the benchmark measures the cost of the check.
            header = new byte[1024];
            Arrays.fill(header, (byte) 0x01);
        }
    }

    @Benchmark
    public boolean matches(DumpState state, Blackhole bh) {
        boolean result = DumpArchiveInputStream.matches(state.header, state.header.length);
        bh.consume(result);
        return result;
    }
}

package bench.generated.c026;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@State(org.openjdk.jmh.annotations.Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TarArchiveOutputStreamBenchmark {

    @State(org.openjdk.jmh.annotations.Scope.Benchmark)
    public static class BenchmarkState {
        byte[] payload;
        TarArchiveEntry fileEntry;

        // Stream pre‑populated for getBytesWritten()
        ByteArrayOutputStream baosForGet;
        TarArchiveOutputStream tosForGet;

        // Stream used for setter benchmarks (no mutation of payload)
        TarArchiveOutputStream tosForSet;

        @org.openjdk.jmh.annotations.Setup(org.openjdk.jmh.annotations.Level.Trial)
        public void setup() throws IOException {
            // 1 KB payload
            payload = new byte[1024];
            for (int i = 0; i < payload.length; i++) {
                payload[i] = (byte) (i & 0xFF);
            }

            fileEntry = new TarArchiveEntry("file.txt");
            fileEntry.setSize(payload.length);

            // Prepare a stream that already has data written – used by getBytesWritten()
            baosForGet = new ByteArrayOutputStream();
            tosForGet = new TarArchiveOutputStream(baosForGet);
            tosForGet.putArchiveEntry(fileEntry);
            tosForGet.write(payload);
            tosForGet.closeArchiveEntry();

            // Prepare a fresh stream for setter benchmarks
            tosForSet = new TarArchiveOutputStream(new ByteArrayOutputStream());
        }
    }

    @Benchmark
    public void benchmarkPutArchiveEntry(BenchmarkState state, Blackhole bh) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        TarArchiveOutputStream tos = new TarArchiveOutputStream(baos);
        tos.putArchiveEntry(state.fileEntry);
        bh.consume(tos);
        // No close to keep only the method under test invoked once
    }

    @Benchmark
    public void benchmarkSetLongFileMode(BenchmarkState state, Blackhole bh) {
        state.tosForSet.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
        bh.consume(state.tosForSet);
    }

    @Benchmark
    public void benchmarkSetBigNumberMode(BenchmarkState state, Blackhole bh) {
        state.tosForSet.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_POSIX);
        bh.consume(state.tosForSet);
    }

    @Benchmark
    public void benchmarkSetAddPaxHeadersForNonAsciiNames(BenchmarkState state, Blackhole bh) {
        state.tosForSet.setAddPaxHeadersForNonAsciiNames(true);
        bh.consume(state.tosForSet);
    }

    @Benchmark
    public long benchmarkGetBytesWritten(BenchmarkState state) {
        return state.tosForGet.getBytesWritten();
    }
}

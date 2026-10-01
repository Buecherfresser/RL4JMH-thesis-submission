package bench.generated.c013;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.dump.DumpArchiveInputStream;
import org.apache.commons.compress.archivers.dump.DumpArchiveConstants;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DumpArchiveInputStreamBenchmark {

    private byte[] matchingBuffer;
    private byte[] nonMatchingBuffer;

    @Setup(Level.Trial)
    public void setUp() {
        // Buffer that should match the NFS magic at offset 24
        matchingBuffer = new byte[32];
        int magic = DumpArchiveConstants.NFS_MAGIC;
        // Store magic in big-endian order (as DumpArchiveInputStream.matches expects)
        matchingBuffer[24] = (byte) ((magic >>> 24) & 0xFF);
        matchingBuffer[25] = (byte) ((magic >>> 16) & 0xFF);
        matchingBuffer[26] = (byte) ((magic >>> 8) & 0xFF);
        matchingBuffer[27] = (byte) (magic & 0xFF);

        // Buffer that does NOT match the magic (filled with zeros)
        nonMatchingBuffer = new byte[32];
    }

    @Benchmark
    public boolean benchmarkMatchesTrue() {
        return DumpArchiveInputStream.matches(matchingBuffer, matchingBuffer.length);
    }

    @Benchmark
    public boolean benchmarkMatchesFalse() {
        return DumpArchiveInputStream.matches(nonMatchingBuffer, nonMatchingBuffer.length);
    }
}

package bench.generated.c003;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.ar.ArArchiveInputStream;
import org.apache.commons.compress.archivers.ar.ArArchiveOutputStream;
import org.apache.commons.compress.archivers.ar.ArArchiveEntry;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ArArchiveInputStreamBenchmark {

    private byte[] archiveBytes;
    private final byte[] signature = new byte[]{0x21, 0x3c, 0x61, 0x72, 0x63, 0x68, 0x3e, 0x0a};

    @Setup(Level.Trial)
    public void buildArchive() throws IOException {
        byte[] payload = new byte[1024];
        for (int i = 0; i < payload.length; i++) {
            payload[i] = (byte) (i & 0xFF);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ArArchiveOutputStream arOut = new ArArchiveOutputStream(baos);

        // Normal entry
        ArArchiveEntry e1 = new ArArchiveEntry("file1.txt", payload.length);
        arOut.putArchiveEntry(e1);
        arOut.write(payload);
        arOut.closeArchiveEntry();

        // Long name entry (BSD style)
        String longName = "verylongfilename12345.txt";
        ArArchiveEntry e2 = new ArArchiveEntry(longName, payload.length);
        arOut.putArchiveEntry(e2);
        arOut.write(payload);
        arOut.closeArchiveEntry();

        arOut.finish();
        arOut.close();

        archiveBytes = baos.toByteArray();
    }

    @State(Scope.Benchmark)
    public static class ReadState {
        private final byte[] buffer = new byte[512];
        private ArArchiveInputStream stream;

        @Setup(Level.Invocation)
        public void init(ReadStateHelper helper) throws IOException {
            stream = new ArArchiveInputStream(new ByteArrayInputStream(helper.archiveBytes));
            // Position at first entry
            stream.getNextEntry();
        }
    }

    // Helper to expose archiveBytes to ReadState without making it static
    @State(Scope.Benchmark)
    public static class ReadStateHelper {
        @Setup(Level.Trial)
        public void copy(ArArchiveInputStreamBenchmark outer) {
            // No action needed; the outer benchmark holds archiveBytes
        }

        // Provide access to the outer archiveBytes
        private byte[] archiveBytes;

        @Setup(Level.Trial)
        public void init(ArArchiveInputStreamBenchmark outer) {
            this.archiveBytes = outer.archiveBytes;
        }
    }

    @Benchmark
    public boolean benchmarkMatches(Blackhole bh) {
        boolean result = ArArchiveInputStream.matches(signature, signature.length);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String benchmarkGetNextEntry() throws IOException {
        ArArchiveInputStream in = new ArArchiveInputStream(new ByteArrayInputStream(archiveBytes));
        try {
            return in.getNextEntry().getName();
        } finally {
            in.close();
        }
    }

    @Benchmark
    public int benchmarkReadEntryData(ReadState state) throws IOException {
        int read = state.stream.read(state.buffer, 0, state.buffer.length);
        return read;
    }
}

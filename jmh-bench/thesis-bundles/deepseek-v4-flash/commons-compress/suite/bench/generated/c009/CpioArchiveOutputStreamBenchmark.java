package bench.generated.c009;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.commons.compress.archivers.cpio.CpioArchiveOutputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;
import org.apache.commons.compress.archivers.cpio.CpioConstants;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CpioArchiveOutputStreamBenchmark {

    @State(Scope.Benchmark)
    public static class DataState {
        byte[] payload;

        @Setup(Level.Trial)
        public void setup() {
            payload = new byte[4096];
            for (int i = 0; i < payload.length; i++) {
                payload[i] = (byte) (i % 251);
            }
        }
    }

    private CpioArchiveEntry createEntry(String name, short format, long size) {
        CpioArchiveEntry entry = new CpioArchiveEntry(name, format);
        entry.setSize(size);
        entry.setMode(CpioConstants.C_ISREG);
        entry.setNumberOfLinks(1);
        entry.setTime(0);
        entry.setUID(0);
        entry.setGID(0);
        return entry;
    }

    private byte[] writeFullEntry(short format, DataState state) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (CpioArchiveOutputStream out = new CpioArchiveOutputStream(baos, format)) {
            CpioArchiveEntry entry = createEntry("file", format, state.payload.length);
            out.putArchiveEntry(entry);
            out.write(state.payload, 0, state.payload.length);
            out.closeArchiveEntry();
            out.finish();
        }
        return baos.toByteArray();
    }

    @Benchmark
    public byte[] writeNewEntry(DataState state) throws IOException {
        return writeFullEntry(CpioConstants.FORMAT_NEW, state);
    }

    @Benchmark
    public byte[] writeNewCrcEntry(DataState state) throws IOException {
        return writeFullEntry(CpioConstants.FORMAT_NEW_CRC, state);
    }

    @Benchmark
    public byte[] writeOldAsciiEntry(DataState state) throws IOException {
        return writeFullEntry(CpioConstants.FORMAT_OLD_ASCII, state);
    }

    @Benchmark
    public byte[] writeOldBinaryEntry(DataState state) throws IOException {
        return writeFullEntry(CpioConstants.FORMAT_OLD_BINARY, state);
    }

    @Benchmark
    public byte[] putAndCloseEntry(DataState state) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (CpioArchiveOutputStream out = new CpioArchiveOutputStream(baos, CpioConstants.FORMAT_NEW)) {
            CpioArchiveEntry entry = createEntry("empty", CpioConstants.FORMAT_NEW, 0);
            out.putArchiveEntry(entry);
            out.closeArchiveEntry();
            out.finish();
        }
        return baos.toByteArray();
    }

    @Benchmark
    public byte[] finishEmpty(DataState state) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (CpioArchiveOutputStream out = new CpioArchiveOutputStream(baos, CpioConstants.FORMAT_NEW)) {
            out.finish();
        }
        return baos.toByteArray();
    }
}

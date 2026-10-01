package bench.generated.c008;

import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;
import org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveOutputStream;
import org.apache.commons.compress.archivers.cpio.CpioConstants;
import org.apache.commons.compress.utils.IOUtils;
import org.openjdk.jmh.annotations.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CpioArchiveInputStreamBenchmark {

    @State(Scope.Benchmark)
    public static class Data {
        byte[] newArchive;
        byte[] newCrcArchive;
        byte[] oldAsciiArchive;
        byte[] oldBinaryArchive;
        byte[] signatureNew;
        byte[] signatureNewCrc;
        byte[] signatureOldAscii;

        @Setup(Level.Trial)
        public void setup() throws IOException {
            newArchive = createArchive(CpioConstants.FORMAT_NEW);
            newCrcArchive = createArchive(CpioConstants.FORMAT_NEW_CRC);
            oldAsciiArchive = createArchive(CpioConstants.FORMAT_OLD_ASCII);
            oldBinaryArchive = createArchive(CpioConstants.FORMAT_OLD_BINARY);
            signatureNew = "070701".getBytes(StandardCharsets.US_ASCII);
            signatureNewCrc = "070702".getBytes(StandardCharsets.US_ASCII);
            signatureOldAscii = "070707".getBytes(StandardCharsets.US_ASCII);
        }

        private byte[] createArchive(short format) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (CpioArchiveOutputStream out = new CpioArchiveOutputStream(baos, format)) {
                CpioArchiveEntry entry;

                entry = new CpioArchiveEntry(format, "file1.txt");
                entry.setSize(6);
                out.putArchiveEntry(entry);
                out.write("Hello ".getBytes(StandardCharsets.UTF_8));
                out.closeArchiveEntry();

                entry = new CpioArchiveEntry(format, "file2.txt");
                entry.setSize(6);
                out.putArchiveEntry(entry);
                out.write("World ".getBytes(StandardCharsets.UTF_8));
                out.closeArchiveEntry();

                entry = new CpioArchiveEntry(format, "file3.txt");
                entry.setSize(8);
                out.putArchiveEntry(entry);
                out.write("FooBar!?".getBytes(StandardCharsets.UTF_8));
                out.closeArchiveEntry();

                // Trailer is written automatically on finish/close
            }
            return baos.toByteArray();
        }
    }

    // ---------- Read all entry data ----------

    @Benchmark
    public long readAllNew(Data data) throws IOException {
        return readAll(data.newArchive);
    }

    @Benchmark
    public long readAllNewCrc(Data data) throws IOException {
        return readAll(data.newCrcArchive);
    }

    @Benchmark
    public long readAllOldAscii(Data data) throws IOException {
        return readAll(data.oldAsciiArchive);
    }

    @Benchmark
    public long readAllOldBinary(Data data) throws IOException {
        return readAll(data.oldBinaryArchive);
    }

    // ---------- Read entries only (no data) ----------

    @Benchmark
    public long getNextEntryNew(Data data) throws IOException {
        return getNextEntryCount(data.newArchive);
    }

    // ---------- Skip entry data ----------

    @Benchmark
    public long skipDataNew(Data data) throws IOException {
        return skipData(data.newArchive);
    }

    // ---------- Static matches ----------

    @Benchmark
    public boolean matchesNew(Data data) {
        return CpioArchiveInputStream.matches(data.signatureNew, data.signatureNew.length);
    }

    @Benchmark
    public boolean matchesNewCrc(Data data) {
        return CpioArchiveInputStream.matches(data.signatureNewCrc, data.signatureNewCrc.length);
    }

    @Benchmark
    public boolean matchesOldAscii(Data data) {
        return CpioArchiveInputStream.matches(data.signatureOldAscii, data.signatureOldAscii.length);
    }

    // ---------- Helpers ----------

    private static long readAll(byte[] archive) throws IOException {
        try (CpioArchiveInputStream in = new CpioArchiveInputStream(new ByteArrayInputStream(archive))) {
            CpioArchiveEntry entry;
            long total = 0;
            while ((entry = in.getNextEntry()) != null) {
                total += IOUtils.toByteArray(in).length;
            }
            return total;
        }
    }

    private static long getNextEntryCount(byte[] archive) throws IOException {
        try (CpioArchiveInputStream in = new CpioArchiveInputStream(new ByteArrayInputStream(archive))) {
            CpioArchiveEntry entry;
            long count = 0;
            while ((entry = in.getNextEntry()) != null) {
                count++;
            }
            return count;
        }
    }

    private static long skipData(byte[] archive) throws IOException {
        try (CpioArchiveInputStream in = new CpioArchiveInputStream(new ByteArrayInputStream(archive))) {
            CpioArchiveEntry entry;
            long total = 0;
            while ((entry = in.getNextEntry()) != null) {
                total += in.skip(entry.getSize());
            }
            return total;
        }
    }
}

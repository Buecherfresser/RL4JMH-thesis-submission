package bench.generated.c008;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveOutputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CpioArchiveInputStreamBenchmark {

    private byte[] archiveBytes;
    private byte[] signatureBytes;

    @Setup(Level.Trial)
    public void setUp() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (CpioArchiveOutputStream out = new CpioArchiveOutputStream(baos)) {
            // first entry
            byte[] data1 = "Hello World".getBytes("UTF-8");
            CpioArchiveEntry e1 = new CpioArchiveEntry(CpioArchiveEntry.FORMAT_NEW);
            e1.setName("file1.txt");
            e1.setSize(data1.length);
            e1.setMode(0644);
            out.putArchiveEntry(e1);
            out.write(data1);
            out.closeArchiveEntry();

            // second entry – larger payload
            byte[] data2 = new byte[2048];
            for (int i = 0; i < data2.length; i++) {
                data2[i] = (byte) (i & 0xFF);
            }
            CpioArchiveEntry e2 = new CpioArchiveEntry(CpioArchiveEntry.FORMAT_NEW);
            e2.setName("file2.bin");
            e2.setSize(data2.length);
            e2.setMode(0644);
            out.putArchiveEntry(e2);
            out.write(data2);
            out.closeArchiveEntry();

            out.finish();
        }
        archiveBytes = baos.toByteArray();

        int sigLen = Math.min(6, archiveBytes.length);
        signatureBytes = new byte[sigLen];
        System.arraycopy(archiveBytes, 0, signatureBytes, 0, sigLen);
    }

    @Benchmark
    public CpioArchiveEntry benchmarkGetNextEntry() throws IOException {
        try (CpioArchiveInputStream in = new CpioArchiveInputStream(new ByteArrayInputStream(archiveBytes))) {
            return in.getNextEntry();
        }
    }

    @Benchmark
    public int benchmarkReadEntryData() throws IOException {
        try (CpioArchiveInputStream in = new CpioArchiveInputStream(new ByteArrayInputStream(archiveBytes))) {
            CpioArchiveEntry entry = in.getNextEntry();
            if (entry == null) {
                return 0;
            }
            byte[] data = IOUtils.toByteArray(in);
            return data.length;
        }
    }

    @Benchmark
    public long benchmarkSkipWithinEntry() throws IOException {
        try (CpioArchiveInputStream in = new CpioArchiveInputStream(new ByteArrayInputStream(archiveBytes))) {
            CpioArchiveEntry entry = in.getNextEntry();
            if (entry == null) {
                return 0L;
            }
            return in.skip(100);
        }
    }

    @Benchmark
    public int benchmarkAvailable() throws IOException {
        try (CpioArchiveInputStream in = new CpioArchiveInputStream(new ByteArrayInputStream(archiveBytes))) {
            CpioArchiveEntry entry = in.getNextEntry();
            if (entry == null) {
                return -1;
            }
            return in.available();
        }
    }

    @Benchmark
    public boolean benchmarkMatchesSignature() {
        return CpioArchiveInputStream.matches(signatureBytes, signatureBytes.length);
    }

    @Benchmark
    public int benchmarkReadBuffer() throws IOException {
        try (CpioArchiveInputStream in = new CpioArchiveInputStream(new ByteArrayInputStream(archiveBytes))) {
            CpioArchiveEntry entry = in.getNextEntry();
            if (entry == null) {
                return -1;
            }
            byte[] buf = new byte[512];
            return in.read(buf, 0, buf.length);
        }
    }
}

package bench.generated.c038;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ZipFileBenchmark {

    private byte[] zipData;
    private ZipFile zipFile;
    private ZipArchiveEntry[] entries;
    private String[] entryNames;
    private ZipArchiveEntry targetEntry;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // Build a zip archive in memory with 10 entries, each containing 1KB of data
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipArchiveOutputStream zos = new ZipArchiveOutputStream(baos)) {
            zos.setMethod(ZipArchiveOutputStream.DEFLATED);
            byte[] payload = new byte[1024];
            for (int i = 0; i < payload.length; i++) {
                payload[i] = (byte) (i % 256);
            }
            for (int i = 0; i < 10; i++) {
                String name = "entry" + i;
                ZipArchiveEntry entry = new ZipArchiveEntry(name);
                zos.putArchiveEntry(entry);
                zos.write(payload);
                zos.closeArchiveEntry();
            }
        }
        zipData = baos.toByteArray();

        // Open the ZipFile from the byte array
        zipFile = ZipFile.builder().setByteArray(zipData).get();

        // Collect entries and names
        Enumeration<ZipArchiveEntry> en = zipFile.getEntries();
        int count = 0;
        while (en.hasMoreElements()) {
            en.nextElement();
            count++;
        }
        entries = new ZipArchiveEntry[count];
        entryNames = new String[count];
        en = zipFile.getEntries();
        int idx = 0;
        while (en.hasMoreElements()) {
            ZipArchiveEntry e = en.nextElement();
            entries[idx] = e;
            entryNames[idx] = e.getName();
            idx++;
        }
        targetEntry = zipFile.getEntry("entry5");
    }

    @TearDown(Level.Trial)
    public void tearDown() throws IOException {
        if (zipFile != null) {
            zipFile.close();
        }
    }

    @Benchmark
    public int getEntriesEnumeration(Blackhole bh) throws IOException {
        Enumeration<ZipArchiveEntry> en = zipFile.getEntries();
        int count = 0;
        while (en.hasMoreElements()) {
            en.nextElement();
            count++;
        }
        bh.consume(count);
        return count;
    }

    @Benchmark
    public String getEntryByName(Blackhole bh) {
        ZipArchiveEntry e = zipFile.getEntry("entry5");
        String name = e != null ? e.getName() : null;
        bh.consume(name);
        return name;
    }

    @Benchmark
    public int getInputStreamAndRead(Blackhole bh) throws IOException {
        try (var is = zipFile.getInputStream(targetEntry)) {
            byte[] data = IOUtils.toByteArray(is);
            bh.consume(data.length);
            return data.length;
        }
    }

    @Benchmark
    public long readAllEntries(Blackhole bh) throws IOException {
        long total = 0;
        for (ZipArchiveEntry entry : entries) {
            try (var is = zipFile.getInputStream(entry)) {
                total += IOUtils.toByteArray(is).length;
            }
        }
        bh.consume(total);
        return total;
    }

    @Benchmark
    public long streamCount(Blackhole bh) {
        long count = zipFile.stream().count();
        bh.consume(count);
        return count;
    }
}

package bench.generated.c004;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.ar.ArArchiveOutputStream;
import org.apache.commons.compress.archivers.ar.ArArchiveEntry;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.LinkOption;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ArArchiveOutputStreamBenchmark {

    private int payloadSize;
    private byte[] payload;
    private String entryName;
    private File dummyFile;
    private Path dummyPath;
    private LinkOption[] emptyLinkOptions;

    @Setup(Level.Trial)
    public void setUp() {
        payloadSize = 1024;
        payload = new byte[payloadSize];
        new Random(0xdeadbeefL).nextBytes(payload);
        entryName = "testfile.txt";
        dummyFile = new File("dummy");
        dummyPath = Paths.get("dummy");
        emptyLinkOptions = new LinkOption[0];
    }

    private ArArchiveOutputStream newStream(ByteArrayOutputStream baos) throws IOException {
        return new ArArchiveOutputStream(baos);
    }

    @Benchmark
    public int benchmarkPutArchiveEntry() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ArArchiveOutputStream out = newStream(baos);
        ArArchiveEntry entry = out.createArchiveEntry(dummyFile, entryName);
        out.putArchiveEntry(entry);
        out.close();
        return baos.size();
    }

    @Benchmark
    public int benchmarkWriteData() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ArArchiveOutputStream out = newStream(baos);
        ArArchiveEntry entry = out.createArchiveEntry(dummyFile, entryName);
        out.putArchiveEntry(entry);
        out.write(payload, 0, payload.length);
        out.close();
        return baos.size();
    }

    @Benchmark
    public int benchmarkCloseArchiveEntry() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ArArchiveOutputStream out = newStream(baos);
        ArArchiveEntry entry = out.createArchiveEntry(dummyFile, entryName);
        out.putArchiveEntry(entry);
        out.write(payload, 0, payload.length);
        out.closeArchiveEntry();
        out.close();
        return baos.size();
    }

    @Benchmark
    public int benchmarkFinish() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ArArchiveOutputStream out = newStream(baos);
        ArArchiveEntry entry = out.createArchiveEntry(dummyFile, entryName);
        out.putArchiveEntry(entry);
        out.write(payload, 0, payload.length);
        out.closeArchiveEntry();
        out.finish();
        out.close();
        return baos.size();
    }

    @Benchmark
    public int benchmarkSetLongFileMode() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ArArchiveOutputStream out = newStream(baos);
        out.setLongFileMode(ArArchiveOutputStream.LONGFILE_BSD);
        out.close();
        return 0;
    }

    @Benchmark
    public String benchmarkCreateArchiveEntryFile() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ArArchiveOutputStream out = newStream(baos);
        ArArchiveEntry entry = out.createArchiveEntry(dummyFile, entryName);
        out.close();
        return entry.getName();
    }

    @Benchmark
    public String benchmarkCreateArchiveEntryPath() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ArArchiveOutputStream out = newStream(baos);
        ArArchiveEntry entry = out.createArchiveEntry(dummyPath, entryName, emptyLinkOptions);
        out.close();
        return entry.getName();
    }

    @Benchmark
    public int benchmarkClose() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ArArchiveOutputStream out = newStream(baos);
        ArArchiveEntry entry = out.createArchiveEntry(dummyFile, entryName);
        out.putArchiveEntry(entry);
        out.write(payload, 0, payload.length);
        out.closeArchiveEntry();
        out.close();
        return baos.size();
    }
}

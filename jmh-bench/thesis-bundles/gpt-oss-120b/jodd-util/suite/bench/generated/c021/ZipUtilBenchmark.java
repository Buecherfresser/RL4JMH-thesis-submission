package bench.generated.c021;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.io.ZipUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.zip.ZipOutputStream;

@State(org.openjdk.jmh.annotations.Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ZipUtilBenchmark {

    private static final int PAYLOAD_SIZE = 1024; // 1 KiB

    private byte[] payload;
    private String entryPath;
    private String folderPath;
    private String comment;

    @Setup
    public void setup() {
        Random random = new Random(0xDEADBEEF);
        payload = new byte[PAYLOAD_SIZE];
        random.nextBytes(payload);
        entryPath = "test/file.txt";
        folderPath = "test/folder";
        comment = "benchmark comment";
    }

    @Benchmark
    public byte[] benchmarkAddByteArrayToZip() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            ZipUtil.addToZip(zos, payload, entryPath, comment);
            zos.finish();
        }
        return baos.toByteArray();
    }

    @Benchmark
    public byte[] benchmarkAddFolderToZip() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            ZipUtil.addFolderToZip(zos, folderPath, comment);
            zos.finish();
        }
        return baos.toByteArray();
    }
}

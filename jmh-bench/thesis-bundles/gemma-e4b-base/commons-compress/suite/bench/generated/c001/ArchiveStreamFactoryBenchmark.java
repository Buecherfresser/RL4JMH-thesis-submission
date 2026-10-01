package bench.generated.c001;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ar.ArArchiveInputStream;
import org.apache.commons.compress.archivers.ar.ArArchiveOutputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveOutputStream;
import org.apache.commons.compress.archivers.dump.DumpArchiveInputStream;
import org.apache.commons.compress.archivers.jar.JarArchiveInputStream;
import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ArchiveStreamFactoryBenchmark {

    private ArchiveStreamFactory factory;

    // Inputs for Input Stream creation (simulating different archive signatures)
    private InputStream zipInputStream;
    private InputStream tarInputStream;
    private InputStream jarInputStream;
    private InputStream arInputStream;
    private InputStream cpioInputStream;
    private InputStream dumpInputStream;

    // Inputs for Output Stream creation (simulating output streams)
    private ByteArrayOutputStream outputStream;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize the factory instance
        factory = new ArchiveStreamFactory();

        // 1. Setup Input Streams (Signatures)
        // Note: These byte arrays are minimal representations of signatures required for detection.

        // ZIP signature (minimal)
        byte[] zipSignature = new byte[]{0x50, 0x4B, 0x03, 0x04};
        zipInputStream = new ByteArrayInputStream(zipSignature);

        // TAR header (minimal 512 bytes)
        byte[] tarHeader = new byte[512];
        tarInputStream = new ByteArrayInputStream(tarHeader);

        // JAR signature (similar to ZIP)
        byte[] jarSignature = new byte[]{0x50, 0x4B, 0x03, 0x04};
        jarInputStream = new ByteArrayInputStream(jarSignature);

        // AR signature (minimal)
        byte[] arSignature = new byte[]{0x41, 0x52, 0x00};
        arInputStream = new ByteArrayInputStream(arSignature);

        // CPIO signature (minimal)
        byte[] cpioSignature = new byte[]{0x01, 0x02, 0x03};
        cpioInputStream = new ByteArrayInputStream(cpioSignature);

        // DUMP signature (minimal 32 bytes)
        byte[] dumpSignature = new byte[32];
        dumpInputStream = new ByteArrayInputStream(dumpSignature);

        // 2. Setup Output Stream
        outputStream = new ByteArrayOutputStream();
    }

    // --- Input Stream Benchmarks (Reading/Detection) ---

    @Benchmark
    public ArchiveInputStream benchmarkCreateArchiveInputStream_Zip_Named() throws ArchiveException {
        // Test: createArchiveInputStream(String archiverName, InputStream in, String actualEncoding)
        return factory.createArchiveInputStream(ArchiveStreamFactory.ZIP, zipInputStream, null);
    }

    @Benchmark
    public ArchiveInputStream benchmarkCreateArchiveInputStream_Tar_Named() throws ArchiveException {
        // Test: createArchiveInputStream(String archiverName, InputStream in, String actualEncoding)
        return factory.createArchiveInputStream(ArchiveStreamFactory.TAR, tarInputStream, null);
    }

    @Benchmark
    public ArchiveInputStream benchmarkCreateArchiveInputStream_Jar_Named() throws ArchiveException {
        // Test: createArchiveInputStream(String archiverName, InputStream in, String actualEncoding)
        return factory.createArchiveInputStream(ArchiveStreamFactory.JAR, jarInputStream, null);
    }

    @Benchmark
    public ArchiveInputStream benchmarkCreateArchiveInputStream_Ar_Named() throws ArchiveException {
        // Test: createArchiveInputStream(String archiverName, InputStream in, String actualEncoding)
        return factory.createArchiveInputStream(ArchiveStreamFactory.AR, arInputStream, null);
    }

    @Benchmark
    public ArchiveInputStream benchmarkCreateArchiveInputStream_Cpio_Named() throws ArchiveException {
        // Test: createArchiveInputStream(String archiverName, InputStream in, String actualEncoding)
        return factory.createArchiveInputStream(ArchiveStreamFactory.CPIO, cpioInputStream, null);
    }

    @Benchmark
    public ArchiveInputStream benchmarkCreateArchiveInputStream_Dump_Named() throws ArchiveException {
        // Test: createArchiveInputStream(String archiverName, InputStream in, String actualEncoding)
        return factory.createArchiveInputStream(ArchiveStreamFactory.DUMP, dumpInputStream, null);
    }

    @Benchmark
    public ArchiveInputStream benchmarkCreateArchiveInputStream_AutoDetect_Zip() throws ArchiveException {
        // Test: createArchiveInputStream(InputStream in)
        // We use the zip stream input here, relying on auto-detection
        return factory.createArchiveInputStream(zipInputStream);
    }

    @Benchmark
    public ArchiveInputStream benchmarkCreateArchiveInputStream_AutoDetect_Tar() throws ArchiveException {
        // Test: createArchiveInputStream(InputStream in)
        return factory.createArchiveInputStream(tarInputStream);
    }

    // --- Output Stream Benchmarks (Writing) ---

    @Benchmark
    public ArchiveOutputStream benchmarkCreateArchiveOutputStream_Zip_Named() throws ArchiveException {
        // Test: createArchiveOutputStream(String archiverName, OutputStream out, String actualEncoding)
        return factory.createArchiveOutputStream(ArchiveStreamFactory.ZIP, outputStream, null);
    }

    @Benchmark
    public ArchiveOutputStream benchmarkCreateArchiveOutputStream_Tar_Named() throws ArchiveException {
        // Test: createArchiveOutputStream(String archiverName, OutputStream out, String actualEncoding)
        return factory.createArchiveOutputStream(ArchiveStreamFactory.TAR, outputStream, null);
    }

    @Benchmark
    public ArchiveOutputStream benchmarkCreateArchiveOutputStream_Jar_Named() throws ArchiveException {
        // Test: createArchiveOutputStream(String archiverName, OutputStream out, String actualEncoding)
        return factory.createArchiveOutputStream(ArchiveStreamFactory.JAR, outputStream, null);
    }

    @Benchmark
    public ArchiveOutputStream benchmarkCreateArchiveOutputStream_Cpio_Named() throws ArchiveException {
        // Test: createArchiveOutputStream(String archiverName, OutputStream out, String actualEncoding)
        return factory.createArchiveOutputStream(ArchiveStreamFactory.CPIO, outputStream, null);
    }

    @Benchmark
    public ArchiveOutputStream benchmarkCreateArchiveOutputStream_Ar_Named() throws ArchiveException {
        // Test: createArchiveOutputStream(String archiverName, OutputStream out, String actualEncoding)
        return factory.createArchiveOutputStream(ArchiveStreamFactory.AR, outputStream, null);
    }
}

package bench.generated.c001;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.SortedMap;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamProvider;
import org.apache.commons.compress.archivers.ar.ArArchiveEntry;
import org.apache.commons.compress.archivers.ar.ArArchiveOutputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;
import org.apache.commons.compress.archivers.cpio.CpioArchiveOutputStream;
import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ArchiveStreamFactoryBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        ArchiveStreamFactory factory = new ArchiveStreamFactory();
        byte[] zipBytes;
        byte[] tarBytes;
        byte[] arBytes;
        byte[] cpioBytes;
        byte[] jarBytes;

        @Setup(Level.Trial)
        public void setup() throws IOException {
            byte[] data = "Hello, Commons Compress!".getBytes("UTF-8");
            zipBytes = createZip(data);
            tarBytes = createTar(data);
            arBytes = createAr(data);
            cpioBytes = createCpio(data);
            jarBytes = createJar(data);
        }

        private byte[] createZip(byte[] data) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ZipArchiveOutputStream zos = new ZipArchiveOutputStream(baos)) {
                ZipArchiveEntry entry = new ZipArchiveEntry("test.txt");
                entry.setSize(data.length);
                zos.putArchiveEntry(entry);
                zos.write(data);
                zos.closeArchiveEntry();
            }
            return baos.toByteArray();
        }

        private byte[] createTar(byte[] data) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (TarArchiveOutputStream tos = new TarArchiveOutputStream(baos)) {
                TarArchiveEntry entry = new TarArchiveEntry("test.txt");
                entry.setSize(data.length);
                tos.putArchiveEntry(entry);
                tos.write(data);
                tos.closeArchiveEntry();
            }
            return baos.toByteArray();
        }

        private byte[] createAr(byte[] data) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ArArchiveOutputStream aos = new ArArchiveOutputStream(baos)) {
                ArArchiveEntry entry = new ArArchiveEntry("test.txt", data.length);
                aos.putArchiveEntry(entry);
                aos.write(data);
                aos.closeArchiveEntry();
            }
            return baos.toByteArray();
        }

        private byte[] createCpio(byte[] data) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (CpioArchiveOutputStream cos = new CpioArchiveOutputStream(baos)) {
                CpioArchiveEntry entry = new CpioArchiveEntry("test.txt", data.length);
                cos.putArchiveEntry(entry);
                cos.write(data);
                cos.closeArchiveEntry();
            }
            return baos.toByteArray();
        }

        private byte[] createJar(byte[] data) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (JarArchiveOutputStream jos = new JarArchiveOutputStream(baos)) {
                ZipArchiveEntry entry = new ZipArchiveEntry("META-INF/MANIFEST.MF");
                byte[] manifest = "Manifest-Version: 1.0\n".getBytes("UTF-8");
                entry.setSize(manifest.length);
                jos.putArchiveEntry(entry);
                jos.write(manifest);
                jos.closeArchiveEntry();
                // Add a regular file entry
                entry = new ZipArchiveEntry("test.txt");
                entry.setSize(data.length);
                jos.putArchiveEntry(entry);
                jos.write(data);
                jos.closeArchiveEntry();
            }
            return baos.toByteArray();
        }
    }

    // ----- createArchiveInputStream by name -----
    @Benchmark
    public ArchiveInputStream<?> createZipInputStream(BenchmarkState state) throws Exception {
        return state.factory.createArchiveInputStream(ArchiveStreamFactory.ZIP,
                new ByteArrayInputStream(state.zipBytes));
    }

    @Benchmark
    public ArchiveInputStream<?> createTarInputStream(BenchmarkState state) throws Exception {
        return state.factory.createArchiveInputStream(ArchiveStreamFactory.TAR,
                new ByteArrayInputStream(state.tarBytes));
    }

    @Benchmark
    public ArchiveInputStream<?> createArInputStream(BenchmarkState state) throws Exception {
        return state.factory.createArchiveInputStream(ArchiveStreamFactory.AR,
                new ByteArrayInputStream(state.arBytes));
    }

    @Benchmark
    public ArchiveInputStream<?> createCpioInputStream(BenchmarkState state) throws Exception {
        return state.factory.createArchiveInputStream(ArchiveStreamFactory.CPIO,
                new ByteArrayInputStream(state.cpioBytes));
    }

    @Benchmark
    public ArchiveInputStream<?> createJarInputStream(BenchmarkState state) throws Exception {
        return state.factory.createArchiveInputStream(ArchiveStreamFactory.JAR,
                new ByteArrayInputStream(state.jarBytes));
    }

    // ----- createArchiveInputStream auto-detect -----
    @Benchmark
    public ArchiveInputStream<?> createAutoDetectZip(BenchmarkState state) throws Exception {
        return state.factory.createArchiveInputStream(new ByteArrayInputStream(state.zipBytes));
    }

    @Benchmark
    public ArchiveInputStream<?> createAutoDetectTar(BenchmarkState state) throws Exception {
        return state.factory.createArchiveInputStream(new ByteArrayInputStream(state.tarBytes));
    }

    @Benchmark
    public ArchiveInputStream<?> createAutoDetectAr(BenchmarkState state) throws Exception {
        return state.factory.createArchiveInputStream(new ByteArrayInputStream(state.arBytes));
    }

    @Benchmark
    public ArchiveInputStream<?> createAutoDetectCpio(BenchmarkState state) throws Exception {
        return state.factory.createArchiveInputStream(new ByteArrayInputStream(state.cpioBytes));
    }

    // ----- createArchiveOutputStream -----
    @Benchmark
    public ArchiveOutputStream<?> createZipOutputStream(BenchmarkState state) throws Exception {
        return state.factory.createArchiveOutputStream(ArchiveStreamFactory.ZIP,
                new ByteArrayOutputStream());
    }

    @Benchmark
    public ArchiveOutputStream<?> createTarOutputStream(BenchmarkState state) throws Exception {
        return state.factory.createArchiveOutputStream(ArchiveStreamFactory.TAR,
                new ByteArrayOutputStream());
    }

    @Benchmark
    public ArchiveOutputStream<?> createArOutputStream(BenchmarkState state) throws Exception {
        return state.factory.createArchiveOutputStream(ArchiveStreamFactory.AR,
                new ByteArrayOutputStream());
    }

    @Benchmark
    public ArchiveOutputStream<?> createCpioOutputStream(BenchmarkState state) throws Exception {
        return state.factory.createArchiveOutputStream(ArchiveStreamFactory.CPIO,
                new ByteArrayOutputStream());
    }

    @Benchmark
    public ArchiveOutputStream<?> createJarOutputStream(BenchmarkState state) throws Exception {
        return state.factory.createArchiveOutputStream(ArchiveStreamFactory.JAR,
                new ByteArrayOutputStream());
    }

    // ----- detect -----
    @Benchmark
    public String detectZip(BenchmarkState state) throws Exception {
        return ArchiveStreamFactory.detect(new ByteArrayInputStream(state.zipBytes));
    }

    @Benchmark
    public String detectTar(BenchmarkState state) throws Exception {
        return ArchiveStreamFactory.detect(new ByteArrayInputStream(state.tarBytes));
    }

    @Benchmark
    public String detectAr(BenchmarkState state) throws Exception {
        return ArchiveStreamFactory.detect(new ByteArrayInputStream(state.arBytes));
    }

    @Benchmark
    public String detectCpio(BenchmarkState state) throws Exception {
        return ArchiveStreamFactory.detect(new ByteArrayInputStream(state.cpioBytes));
    }

    // ----- provider maps and names -----
    @Benchmark
    public SortedMap<String, ArchiveStreamProvider> getInputStreamProviders(BenchmarkState state) {
        return state.factory.getArchiveInputStreamProviders();
    }

    @Benchmark
    public SortedMap<String, ArchiveStreamProvider> getOutputStreamProviders(BenchmarkState state) {
        return state.factory.getArchiveOutputStreamProviders();
    }

    @Benchmark
    public Set<String> getInputStreamNames(BenchmarkState state) {
        return state.factory.getInputStreamArchiveNames();
    }

    @Benchmark
    public Set<String> getOutputStreamNames(BenchmarkState state) {
        return state.factory.getOutputStreamArchiveNames();
    }

    // ----- encoding -----
    @Benchmark
    public String getEntryEncoding(BenchmarkState state) {
        return state.factory.getEntryEncoding();
    }

    @Benchmark
    public String setEntryEncoding(BenchmarkState state) {
        state.factory.setEntryEncoding("UTF-8");
        return state.factory.getEntryEncoding();
    }

    // ----- static provider discovery -----
    @Benchmark
    public SortedMap<String, ArchiveStreamProvider> findAvailableInputProviders() {
        return ArchiveStreamFactory.findAvailableArchiveInputStreamProviders();
    }

    @Benchmark
    public SortedMap<String, ArchiveStreamProvider> findAvailableOutputProviders() {
        return ArchiveStreamFactory.findAvailableArchiveOutputStreamProviders();
    }
}

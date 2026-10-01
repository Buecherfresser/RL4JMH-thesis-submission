package bench.generated.c019;

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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;

@State(org.openjdk.jmh.annotations.Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class JarArchiveOutputStreamBenchmark {

    @State(org.openjdk.jmh.annotations.Scope.Benchmark)
    public static class PayloadState {
        byte[] data;

        @Setup(org.openjdk.jmh.annotations.Level.Trial)
        public void setUp() {
            data = new byte[1024];
            for (int i = 0; i < data.length; i++) {
                data[i] = (byte) (i & 0xFF);
            }
        }
    }

    @State(org.openjdk.jmh.annotations.Scope.Benchmark)
    public static class PutEntryState {
        ByteArrayOutputStream baos;
        JarArchiveOutputStream jarOut;
        ZipArchiveEntry entry;

        @Setup(org.openjdk.jmh.annotations.Level.Invocation)
        public void setUp() throws IOException {
            baos = new ByteArrayOutputStream();
            jarOut = new JarArchiveOutputStream(baos);
            entry = new ZipArchiveEntry("test.txt");
            entry.setSize(0);
        }
    }

    @State(org.openjdk.jmh.annotations.Scope.Benchmark)
    public static class WriteState {
        ByteArrayOutputStream baos;
        JarArchiveOutputStream jarOut;

        @Setup(org.openjdk.jmh.annotations.Level.Invocation)
        public void setUp(PayloadState payload) throws IOException {
            baos = new ByteArrayOutputStream();
            jarOut = new JarArchiveOutputStream(baos);
            ZipArchiveEntry entry = new ZipArchiveEntry("test.txt");
            entry.setSize(payload.data.length);
            jarOut.putArchiveEntry(entry);
        }
    }

    @State(org.openjdk.jmh.annotations.Scope.Benchmark)
    public static class CloseEntryState {
        ByteArrayOutputStream baos;
        JarArchiveOutputStream jarOut;

        @Setup(org.openjdk.jmh.annotations.Level.Invocation)
        public void setUp(PayloadState payload) throws IOException {
            baos = new ByteArrayOutputStream();
            jarOut = new JarArchiveOutputStream(baos);
            ZipArchiveEntry entry = new ZipArchiveEntry("test.txt");
            entry.setSize(payload.data.length);
            jarOut.putArchiveEntry(entry);
            jarOut.write(payload.data, 0, payload.data.length);
        }
    }

    @State(org.openjdk.jmh.annotations.Scope.Benchmark)
    public static class FinishState {
        ByteArrayOutputStream baos;
        JarArchiveOutputStream jarOut;

        @Setup(org.openjdk.jmh.annotations.Level.Invocation)
        public void setUp() throws IOException {
            baos = new ByteArrayOutputStream();
            jarOut = new JarArchiveOutputStream(baos);
        }
    }

    @Benchmark
    public void benchmarkPutArchiveEntry(PutEntryState state, Blackhole bh) throws IOException {
        state.jarOut.putArchiveEntry(state.entry);
        bh.consume(state.entry);
    }

    @Benchmark
    public void benchmarkWriteBytes(WriteState state, PayloadState payload, Blackhole bh) throws IOException {
        state.jarOut.write(payload.data, 0, payload.data.length);
        bh.consume(payload.data.length);
    }

    @Benchmark
    public void benchmarkCloseArchiveEntry(CloseEntryState state, Blackhole bh) throws IOException {
        state.jarOut.closeArchiveEntry();
        bh.consume(state.jarOut);
    }

    @Benchmark
    public void benchmarkFinish(FinishState state, Blackhole bh) throws IOException {
        state.jarOut.finish();
        byte[] result = state.baos.toByteArray();
        bh.consume(result.length);
    }
}

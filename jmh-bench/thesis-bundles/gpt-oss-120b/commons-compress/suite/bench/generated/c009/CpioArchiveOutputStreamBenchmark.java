package bench.generated.c009;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.LinkOption;
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
    public static class SharedState {
        byte[] payload;
        CpioArchiveEntry entryTemplate;
        File dummyFile;
        Path dummyPath;

        @Setup(Level.Trial)
        public void setup() throws IOException {
            payload = new byte[1024];
            for (int i = 0; i < payload.length; i++) {
                payload[i] = (byte) (i & 0xFF);
            }
            entryTemplate = new CpioArchiveEntry(CpioConstants.FORMAT_NEW);
            entryTemplate.setName("testfile");
            entryTemplate.setSize(payload.length);
            entryTemplate.setMode(CpioConstants.C_ISREG);
            entryTemplate.setUID(0);
            entryTemplate.setGID(0);
            entryTemplate.setNumberOfLinks(1);
            entryTemplate.setTime(System.currentTimeMillis() / 1000);
            dummyFile = new File("dummy.txt");
            dummyPath = Paths.get("dummy.txt");
        }

        public CpioArchiveEntry newEntry() {
            CpioArchiveEntry e = new CpioArchiveEntry(CpioConstants.FORMAT_NEW);
            e.setName(entryTemplate.getName());
            e.setSize(entryTemplate.getSize());
            e.setMode(entryTemplate.getMode());
            e.setUID(entryTemplate.getUID());
            e.setGID(entryTemplate.getGID());
            e.setNumberOfLinks(entryTemplate.getNumberOfLinks());
            e.setTime(entryTemplate.getTime());
            return e;
        }
    }

    @Benchmark
    public int benchmarkPutArchiveEntry(SharedState state) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CpioArchiveOutputStream out = new CpioArchiveOutputStream(baos);
        CpioArchiveEntry entry = state.newEntry();
        out.putArchiveEntry(entry);
        int size = baos.size();
        out.close();
        return size;
    }

    @State(Scope.Benchmark)
    public static class WriteState {
        CpioArchiveOutputStream out;
        ByteArrayOutputStream baos;
        byte[] payload;

        @Setup(Level.Invocation)
        public void setup(SharedState shared) throws IOException {
            baos = new ByteArrayOutputStream();
            out = new CpioArchiveOutputStream(baos);
            CpioArchiveEntry entry = shared.newEntry();
            out.putArchiveEntry(entry);
            payload = shared.payload;
        }
    }

    @Benchmark
    public int benchmarkWrite(WriteState ws) throws IOException {
        ws.out.write(ws.payload, 0, ws.payload.length);
        return ws.payload.length;
    }

    @State(Scope.Benchmark)
    public static class CloseState {
        CpioArchiveOutputStream out;
        ByteArrayOutputStream baos;

        @Setup(Level.Invocation)
        public void setup(SharedState shared) throws IOException {
            baos = new ByteArrayOutputStream();
            out = new CpioArchiveOutputStream(baos);
            CpioArchiveEntry entry = shared.newEntry();
            out.putArchiveEntry(entry);
            out.write(shared.payload, 0, shared.payload.length);
        }
    }

    @Benchmark
    public void benchmarkCloseArchiveEntry(CloseState cs, Blackhole bh) throws IOException {
        cs.out.closeArchiveEntry();
        bh.consume(cs.out);
    }

    @State(Scope.Benchmark)
    public static class FinishState {
        CpioArchiveOutputStream out;
        ByteArrayOutputStream baos;

        @Setup(Level.Invocation)
        public void setup(SharedState shared) throws IOException {
            baos = new ByteArrayOutputStream();
            out = new CpioArchiveOutputStream(baos);
            CpioArchiveEntry entry = shared.newEntry();
            out.putArchiveEntry(entry);
            out.write(shared.payload, 0, shared.payload.length);
            out.closeArchiveEntry();
        }
    }

    @Benchmark
    public int benchmarkFinish(FinishState fs) throws IOException {
        fs.out.finish();
        return fs.baos.size();
    }

    @Benchmark
    public CpioArchiveEntry benchmarkCreateArchiveEntryFile(SharedState state) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CpioArchiveOutputStream out = new CpioArchiveOutputStream(baos);
        CpioArchiveEntry e = out.createArchiveEntry(state.dummyFile, "fileFromFile");
        out.close();
        return e;
    }

    @Benchmark
    public CpioArchiveEntry benchmarkCreateArchiveEntryPath(SharedState state) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CpioArchiveOutputStream out = new CpioArchiveOutputStream(baos);
        CpioArchiveEntry e = out.createArchiveEntry(state.dummyPath, "fileFromPath", new LinkOption[0]);
        out.close();
        return e;
    }
}

package bench.generated.c023;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZMethod;
import org.apache.commons.compress.archivers.sevenz.SevenZMethodConfiguration;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.nio.channels.SeekableByteChannel;
import java.util.Collections;
import java.util.List;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SevenZOutputFileBenchmark {

    @State(Scope.Benchmark)
    public static class BaseState {
        byte[] payload;

        @Setup(Level.Trial)
        public void setUp() throws IOException {
            payload = new byte[1024];
            for (int i = 0; i < payload.length; i++) {
                payload[i] = (byte) i;
            }
        }
    }

    @State(Scope.Benchmark)
    public static class PutEntryState extends BaseState {
        SevenZOutputFile sevenZOut;
        SevenZArchiveEntry entry;

        @Setup(Level.Trial)
        public void setUp() throws IOException {
            SeekableByteChannel channel = new SeekableInMemoryByteChannel();
            sevenZOut = new SevenZOutputFile(channel);
            entry = new SevenZArchiveEntry();
            entry.setName("entry.txt");
            entry.setDirectory(false);
        }

        @TearDown(Level.Trial)
        public void tearDown() throws IOException {
            sevenZOut.close();
        }
    }

    @State(Scope.Benchmark)
    public static class SetCompressionState extends BaseState {
        SevenZOutputFile sevenZOut;

        @Setup(Level.Trial)
        public void setUp() throws IOException {
            SeekableByteChannel channel = new SeekableInMemoryByteChannel();
            sevenZOut = new SevenZOutputFile(channel);
        }

        @TearDown(Level.Trial)
        public void tearDown() throws IOException {
            sevenZOut.close();
        }
    }

    @State(Scope.Benchmark)
    public static class SetMethodsState extends BaseState {
        SevenZOutputFile sevenZOut;
        List<SevenZMethodConfiguration> methods;

        @Setup(Level.Trial)
        public void setUp() throws IOException {
            SeekableByteChannel channel = new SeekableInMemoryByteChannel();
            sevenZOut = new SevenZOutputFile(channel);
            methods = Collections.singletonList(new SevenZMethodConfiguration(SevenZMethod.DEFLATE));
        }

        @TearDown(Level.Trial)
        public void tearDown() throws IOException {
            sevenZOut.close();
        }
    }

    @State(Scope.Benchmark)
    public static class WriteState extends BaseState {
        SevenZOutputFile sevenZOut;

        @Setup(Level.Trial)
        public void setUp() throws IOException {
            SeekableByteChannel channel = new SeekableInMemoryByteChannel();
            sevenZOut = new SevenZOutputFile(channel);
            SevenZArchiveEntry entry = new SevenZArchiveEntry();
            entry.setName("data.bin");
            entry.setDirectory(false);
            sevenZOut.putArchiveEntry(entry);
        }

        @TearDown(Level.Trial)
        public void tearDown() throws IOException {
            sevenZOut.close();
        }
    }

    @State(Scope.Benchmark)
    public static class CloseEntryState extends BaseState {
        SevenZOutputFile sevenZOut;

        @Setup(Level.Trial)
        public void setUp() throws IOException {
            SeekableByteChannel channel = new SeekableInMemoryByteChannel();
            sevenZOut = new SevenZOutputFile(channel);
            SevenZArchiveEntry entry = new SevenZArchiveEntry();
            entry.setName("data.bin");
            entry.setDirectory(false);
            sevenZOut.putArchiveEntry(entry);
            sevenZOut.write(payload);
        }

        @TearDown(Level.Trial)
        public void tearDown() throws IOException {
            sevenZOut.close();
        }
    }

    @State(Scope.Benchmark)
    public static class FinishState extends BaseState {
        SevenZOutputFile sevenZOut;

        @Setup(Level.Trial)
        public void setUp() throws IOException {
            SeekableByteChannel channel = new SeekableInMemoryByteChannel();
            sevenZOut = new SevenZOutputFile(channel);
            SevenZArchiveEntry entry = new SevenZArchiveEntry();
            entry.setName("data.bin");
            entry.setDirectory(false);
            sevenZOut.putArchiveEntry(entry);
            sevenZOut.write(payload);
            sevenZOut.closeArchiveEntry();
        }

        @TearDown(Level.Trial)
        public void tearDown() throws IOException {
            sevenZOut.close();
        }
    }

    @Benchmark
    public void benchmarkPutArchiveEntry(PutEntryState state, Blackhole bh) throws IOException {
        state.sevenZOut.putArchiveEntry(state.entry);
        bh.consume(state.entry);
    }

    @Benchmark
    public void benchmarkSetContentCompression(SetCompressionState state, Blackhole bh) {
        state.sevenZOut.setContentCompression(SevenZMethod.BZIP2);
        bh.consume(state.sevenZOut);
    }

    @Benchmark
    public void benchmarkSetContentMethods(SetMethodsState state, Blackhole bh) {
        state.sevenZOut.setContentMethods(state.methods);
        bh.consume(state.sevenZOut);
    }

    @Benchmark
    public void benchmarkWriteByteArray(WriteState state, Blackhole bh) throws IOException {
        state.sevenZOut.write(state.payload);
        bh.consume(state.payload.length);
    }

    @Benchmark
    public void benchmarkWriteInputStream(WriteState state, Blackhole bh) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(state.payload);
        state.sevenZOut.write(bais);
        bh.consume(state.payload.length);
    }

    @Benchmark
    public void benchmarkCloseArchiveEntry(CloseEntryState state, Blackhole bh) throws IOException {
        state.sevenZOut.closeArchiveEntry();
        bh.consume(state.sevenZOut);
    }

    @Benchmark
    public void benchmarkFinish(FinishState state, Blackhole bh) throws IOException {
        state.sevenZOut.finish();
        bh.consume(state.sevenZOut);
    }
}

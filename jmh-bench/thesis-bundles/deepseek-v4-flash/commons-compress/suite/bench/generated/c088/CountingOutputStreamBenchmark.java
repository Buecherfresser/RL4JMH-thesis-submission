package bench.generated.c088;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.compress.utils.CountingOutputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CountingOutputStreamBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        CountingOutputStream countingStream;
        byte[] payload;
        CountingOutputStream prePopulatedStream;

        @Setup(Level.Trial)
        public void setup() throws IOException {
            payload = new byte[1024];
            for (int i = 0; i < payload.length; i++) {
                payload[i] = (byte) i;
            }
            countingStream = new CountingOutputStream(new NullOutputStream());
            prePopulatedStream = new CountingOutputStream(new NullOutputStream());
            prePopulatedStream.write(payload);
            prePopulatedStream.write(payload, 0, 100);
            prePopulatedStream.write(42);
        }
    }

    private static class NullOutputStream extends OutputStream {
        @Override
        public void write(int b) throws IOException {
            // discard
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            // discard
        }
    }

    @Benchmark
    public long writeByteArray(BenchmarkState state) throws IOException {
        state.countingStream.write(state.payload);
        return state.countingStream.getBytesWritten();
    }

    @Benchmark
    public long writeByteArrayOffLen(BenchmarkState state) throws IOException {
        state.countingStream.write(state.payload, 0, state.payload.length);
        return state.countingStream.getBytesWritten();
    }

    @Benchmark
    public long writeInt(BenchmarkState state) throws IOException {
        state.countingStream.write(42);
        return state.countingStream.getBytesWritten();
    }

    @Benchmark
    public long getBytesWritten(BenchmarkState state) {
        return state.prePopulatedStream.getBytesWritten();
    }
}

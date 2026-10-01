package bench.generated.c088;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.utils.CountingOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CountingOutputStreamBenchmark {

    @State(Scope.Benchmark)
    public static class WriteState {
        byte[] data;
        ByteArrayOutputStream baos;

        @Setup(Level.Trial)
        public void setUp() {
            data = new byte[1024];
            for (int i = 0; i < data.length; i++) {
                data[i] = (byte) (i & 0xFF);
            }
        }

        @Setup(Level.Invocation)
        public void init() {
            baos = new ByteArrayOutputStream(data.length);
        }

        CountingOutputStream newCOS() {
            return new CountingOutputStream(baos);
        }
    }

    @State(Scope.Benchmark)
    public static class GetState {
        byte[] data;
        CountingOutputStream cos;

        @Setup(Level.Trial)
        public void setUp() {
            data = new byte[1024];
            for (int i = 0; i < data.length; i++) {
                data[i] = (byte) (i & 0xFF);
            }
        }

        @Setup(Level.Invocation)
        public void init() throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length);
            cos = new CountingOutputStream(baos);
            cos.write(data);
        }
    }

    @Benchmark
    public long writeByteArray(WriteState state) throws IOException {
        CountingOutputStream cos = state.newCOS();
        cos.write(state.data);
        return cos.getBytesWritten();
    }

    @Benchmark
    public long writeByteArrayPartial(WriteState state) throws IOException {
        CountingOutputStream cos = state.newCOS();
        cos.write(state.data, 0, state.data.length);
        return cos.getBytesWritten();
    }

    @Benchmark
    public long writeInt(WriteState state) throws IOException {
        CountingOutputStream cos = state.newCOS();
        cos.write(state.data[0] & 0xFF);
        return cos.getBytesWritten();
    }

    @Benchmark
    public long getBytesWritten(GetState state) {
        return state.cos.getBytesWritten();
    }
}

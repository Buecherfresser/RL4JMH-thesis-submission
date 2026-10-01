package bench.generated.c013;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.io.CharBufferReader;
import java.nio.CharBuffer;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharBufferReaderBenchmark {

    @State(Scope.Benchmark)
    public static class Data {
        CharBuffer buffer;
        char[] destChunk;

        @Setup(Level.Trial)
        public void setup() {
            // Build a 1024-character payload.
            StringBuilder sb = new StringBuilder(1024);
            for (int i = 0; i < 1024; i++) {
                sb.append((char) ('a' + (i % 26)));
            }
            buffer = CharBuffer.wrap(sb.toString().toCharArray());
            destChunk = new char[256];
        }
    }

    @Benchmark
    public int readChunk(Data data) {
        CharBufferReader reader = new CharBufferReader(data.buffer);
        return reader.read(data.destChunk, 0, data.destChunk.length);
    }

    @Benchmark
    public int readSingle(Data data) {
        CharBufferReader reader = new CharBufferReader(data.buffer);
        return reader.read();
    }
}

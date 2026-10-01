package bench.generated.c012;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import jodd.io.AppendableWriter;
import java.io.StringWriter;
import java.io.IOException;
import java.io.Writer;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
@Threads(1)
public class AppendableWriterBenchmark {

    @State(Scope.Benchmark)
    public static class WriteState {
        static final int POOL_SIZE = 1024;
        Pair[] pairs = new Pair[POOL_SIZE];
        int index = 0;
        String payloadString;
        char[] payloadChars;
        CharSequence payloadSeq;

        static class Pair {
            AppendableWriter writer;
            StringBuilder builder;
            Pair(AppendableWriter w, StringBuilder b) {
                writer = w;
                builder = b;
            }
        }

        @Setup(Level.Trial)
        public void setup() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 10; i++) {
                sb.append("The quick brown fox jumps over the lazy dog. 0123456789");
            }
            payloadString = sb.toString();
            payloadChars = payloadString.toCharArray();
            payloadSeq = new StringBuilder(payloadString);

            for (int i = 0; i < POOL_SIZE; i++) {
                StringBuilder builder = new StringBuilder();
                AppendableWriter writer = new AppendableWriter(builder);
                pairs[i] = new Pair(writer, builder);
            }
        }

        Pair nextPair() {
            Pair p = pairs[index];
            index = (index + 1) % POOL_SIZE;
            return p;
        }
    }

    @State(Scope.Benchmark)
    public static class FlushState {
        AppendableWriter writer;

        @Setup(Level.Trial)
        public void setup() {
            writer = new AppendableWriter(new StringWriter());
        }
    }

    @Benchmark
    public int writeCharArrayRange(WriteState state) throws IOException {
        WriteState.Pair pair = state.nextPair();
        pair.writer.write(state.payloadChars, 0, state.payloadChars.length);
        return pair.builder.length();
    }

    @Benchmark
    public int writeCharArray(WriteState state) throws IOException {
        WriteState.Pair pair = state.nextPair();
        pair.writer.write(state.payloadChars);
        return pair.builder.length();
    }

    @Benchmark
    public int writeString(WriteState state) throws IOException {
        WriteState.Pair pair = state.nextPair();
        pair.writer.write(state.payloadString);
        return pair.builder.length();
    }

    @Benchmark
    public int writeStringRange(WriteState state) throws IOException {
        WriteState.Pair pair = state.nextPair();
        pair.writer.write(state.payloadString, 0, state.payloadString.length());
        return pair.builder.length();
    }

    @Benchmark
    public int writeInt(WriteState state) throws IOException {
        WriteState.Pair pair = state.nextPair();
        pair.writer.write('a');
        return pair.builder.length();
    }

    @Benchmark
    public Writer appendChar(WriteState state) throws IOException {
        WriteState.Pair pair = state.nextPair();
        return pair.writer.append('a');
    }

    @Benchmark
    public Writer appendCharSeq(WriteState state) throws IOException {
        WriteState.Pair pair = state.nextPair();
        return pair.writer.append(state.payloadSeq);
    }

    @Benchmark
    public Writer appendCharSeqRange(WriteState state) throws IOException {
        WriteState.Pair pair = state.nextPair();
        return pair.writer.append(state.payloadSeq, 0, state.payloadSeq.length());
    }

    @Benchmark
    public AppendableWriter flush(FlushState state) throws IOException {
        state.writer.flush();
        return state.writer;
    }

    @Benchmark
    public AppendableWriter close() throws IOException {
        AppendableWriter writer = new AppendableWriter(new StringBuilder());
        writer.close();
        return writer;
    }
}

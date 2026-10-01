package bench.generated.c013;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Level;
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

    private CharBuffer sourceBuffer;
    private char[] destFull;
    private char[] destPartial;
    private int offset;
    private int length;

    @Setup(Level.Trial)
    public void setUp() {
        int size = 8192;
        char[] data = new char[size];
        for (int i = 0; i < size; i++) {
            data[i] = (char) ('a' + (i % 26));
        }
        sourceBuffer = CharBuffer.wrap(data);
        destFull = new char[size];
        destPartial = new char[256];
        offset = 10;
        length = 100;
    }

    @Benchmark
    public int readSingleChar() {
        CharBufferReader reader = new CharBufferReader(sourceBuffer);
        return reader.read();
    }

    @Benchmark
    public int readFullArray() {
        CharBufferReader reader = new CharBufferReader(sourceBuffer);
        return reader.read(destFull, 0, destFull.length);
    }

    @Benchmark
    public int readPartialArray() {
        CharBufferReader reader = new CharBufferReader(sourceBuffer);
        return reader.read(destPartial, offset, length);
    }

    @Benchmark
    public void readPartialArrayConsume(Blackhole bh) {
        CharBufferReader reader = new CharBufferReader(sourceBuffer);
        int n = reader.read(destPartial, offset, length);
        bh.consume(n);
        bh.consume(destPartial);
    }

    @Benchmark
    public int readAllChars() {
        CharBufferReader reader = new CharBufferReader(sourceBuffer);
        int count = 0;
        int c;
        while ((c = reader.read()) != -1) {
            count++;
        }
        return count;
    }
}

package bench.generated.c013;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.nio.CharBuffer;
import jodd.io.CharBufferReader;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(java.util.concurrent.TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharBufferReaderBenchmark {

    @Benchmark
    public void benchmarkRead(Blackhole bh) {
        try {
            // Create a temporary, non-persistent CharBuffer instance for this invocation.
            CharBuffer charBuffer = CharBuffer.allocate(1024);
            CharBufferReader reader = new CharBufferReader(charBuffer);

            // Test read() method (reads one char)
            reader.read();

            // Test read(char[], offset, length) method
            char[] buffer = new char[1024];
            reader.read(buffer, 0, 1024);

        } catch (Exception e) {
            // Ignore exceptions for benchmarking purposes
        }
    }
}

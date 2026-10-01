package bench.generated.c082;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.utils.ByteUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class ByteUtilsBenchmark {

    // State fields for read-only data or reusable objects
    private byte[] testBytes;

    @Setup
    public void setup() {
        // Prepare a fixed payload. This array is read-only for the benchmark methods.
        // We use a simple sequence of bytes.
        this.testBytes = new byte[16];
        for (int i = 0; i < 16; i++) {
            testBytes[i] = (byte) (i % 256);
        }
    }

    @Benchmark
    public void benchmarkFromLittleEndian_Array(Blackhole bh) {
        // Test reading the whole array
        long result = ByteUtils.fromLittleEndian(testBytes);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkFromLittleEndian_Segment(Blackhole bh) {
        // Test reading a segment (e.g., first 8 bytes)
        long result = ByteUtils.fromLittleEndian(testBytes, 0, 8);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkToLittleEndian_Array(Blackhole bh) {
        // Test writing a value to a portion of the array.
        // Since this method modifies the array in place, we rely on JMH's isolation
        // or the fact that we are not reusing the exact same array instance across trials
        // if the benchmark runner is aggressive.
        ByteUtils.toLittleEndian(testBytes, 0xDEADBEEFL, 0, 8);
        bh.consume(null); // Void method, consume null or nothing
    }
}

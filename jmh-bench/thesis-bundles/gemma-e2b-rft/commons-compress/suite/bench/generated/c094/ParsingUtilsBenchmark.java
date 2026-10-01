package bench.generated.c094;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.utils.ParsingUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ParsingUtilsBenchmark {

    // Input data prepared in @Setup
    private String intValueBase10;
    private String intValueBase16;
    private String longValueBase10;
    private String longValueBase16;
    private int radix16 = 16;
    private int radix2 = 2;

    @Setup
    public void setup() throws IOException {
        // Test values:
        // Integer: 12345 (Base 10)
        this.intValueBase10 = "12345";
        // Integer: FF (Base 16) -> 255
        this.intValueBase16 = "FF";
        // Long: 9876543210 (Base 10)
        this.longValueBase10 = "9876543210";
        // Long: FFFFFFFFFFFFFFFF (Base 16)
        this.longValueBase16 = "FFFFFFFFFFFFFFFF";
    }

    @Benchmark
    public void parseIntValueBase10(Blackhole bh) throws IOException {
        int result = ParsingUtils.parseIntValue(intValueBase10);
        bh.consume(result);
    }

    @Benchmark
    public void parseIntValueBase16(Blackhole bh) throws IOException {
        int result = ParsingUtils.parseIntValue(intValueBase16, radix16);
        bh.consume(result);
    }

    @Benchmark
    public void parseLongValueBase10(Blackhole bh) throws IOException {
        long result = ParsingUtils.parseLongValue(longValueBase10);
        bh.consume(result);
    }

    @Benchmark
    public void parseLongValueBase16(Blackhole bh) throws IOException {
        long result = ParsingUtils.parseLongValue(longValueBase16, radix16);
        bh.consume(result);
    }
}

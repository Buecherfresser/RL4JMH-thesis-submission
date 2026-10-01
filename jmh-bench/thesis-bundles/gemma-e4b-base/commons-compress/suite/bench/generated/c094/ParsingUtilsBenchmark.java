package bench.generated.c094;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.utils.ParsingUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ParsingUtilsBenchmark {

    private String validIntString;
    private String validLongString;
    private String validHexIntString;
    private String validHexLongString;

    @Setup(Level.Trial)
    public void setup() {
        // Standard base-10 inputs
        validIntString = "123456789";
        validLongString = "9876543210987654321";

        // Hexadecimal inputs (radix 16)
        validHexIntString = "FF";
        validHexLongString = "FFFFFFFFFFFFFFFF";
    }

    @Benchmark
    public void parseIntValue_Base10(Blackhole bh) throws java.io.IOException {
        int result = ParsingUtils.parseIntValue(validIntString);
        bh.consume(result);
    }

    @Benchmark
    public void parseIntValue_Radix16(Blackhole bh) throws java.io.IOException {
        int result = ParsingUtils.parseIntValue(validHexIntString, 16);
        bh.consume(result);
    }

    @Benchmark
    public void parseLongValue_Base10(Blackhole bh) throws java.io.IOException {
        long result = ParsingUtils.parseLongValue(validLongString);
        bh.consume(result);
    }

    @Benchmark
    public void parseLongValue_Radix16(Blackhole bh) throws java.io.IOException {
        long result = ParsingUtils.parseLongValue(validHexLongString, 16);
        bh.consume(result);
    }
}

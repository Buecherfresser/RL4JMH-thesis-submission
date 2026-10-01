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

    // Inputs built in @Setup
    private String intValue10;
    private String intValueRadix;
    private String longValue10;
    private String longValueRadix;

    @Setup
    public void setup() throws IOException {
        // Setup inputs. Using reasonably sized strings for parsing tests.
        intValue10 = "1234567890";
        intValueRadix = "1234567890"; // String representing a number in a different base (e.g., base 16 or 8)
        longValue10 = "9876543210123456789L";
        longValueRadix = "9876543210123456789";
    }

    @Benchmark
    public void parseIntValue10(Blackhole bh) throws IOException {
        int result = ParsingUtils.parseIntValue(intValue10);
        bh.consume(result);
    }

    @Benchmark
    public void parseIntValueRadix(Blackhole bh) throws IOException {
        int result = ParsingUtils.parseIntValue(intValueRadix, 10); // Testing parsing a string using radix 10
        bh.consume(result);
    }

    @Benchmark
    public void parseLongValue10(Blackhole bh) throws IOException {
        long result = ParsingUtils.parseLongValue(longValue10);
        bh.consume(result);
    }

    @Benchmark
    public void parseLongValueRadix(Blackhole bh) throws IOException {
        long result = ParsingUtils.parseLongValue(longValueRadix, 10); // Testing parsing a string using radix 10
        bh.consume(result);
    }
}

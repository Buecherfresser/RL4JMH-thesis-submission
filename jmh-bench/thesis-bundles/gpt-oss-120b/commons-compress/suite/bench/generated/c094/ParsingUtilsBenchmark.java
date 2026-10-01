package bench.generated.c094;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.utils.ParsingUtils;
import java.io.IOException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ParsingUtilsBenchmark {

    private String decimalIntString;
    private String hexIntString;
    private String decimalLongString;
    private String hexLongString;

    @Setup(Level.Trial)
    public void setUp() {
        // Build representative numeric strings
        decimalIntString = Integer.toString(Integer.MAX_VALUE);
        hexIntString = Integer.toHexString(Integer.MAX_VALUE);
        decimalLongString = Long.toString(Long.MAX_VALUE);
        hexLongString = Long.toHexString(Long.MAX_VALUE);
    }

    @Benchmark
    public int parseIntDecimal() throws IOException {
        return ParsingUtils.parseIntValue(decimalIntString);
    }

    @Benchmark
    public int parseIntHex() throws IOException {
        return ParsingUtils.parseIntValue(hexIntString, 16);
    }

    @Benchmark
    public long parseLongDecimal() throws IOException {
        return ParsingUtils.parseLongValue(decimalLongString);
    }

    @Benchmark
    public long parseLongHex() throws IOException {
        return ParsingUtils.parseLongValue(hexLongString, 16);
    }
}

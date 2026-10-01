package bench.generated.c094;

import org.apache.commons.compress.utils.ParsingUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ParsingUtilsBenchmark {

    private String decimalInt;
    private String hexInt;
    private String octalInt;
    private String decimalLong;
    private String hexLong;
    private String octalLong;

    @Setup(Level.Trial)
    public void setup() {
        decimalInt = "2147483647";
        hexInt = "7fffffff";
        octalInt = "17777777777";
        decimalLong = "9223372036854775807";
        hexLong = "7fffffffffffffff";
        octalLong = "777777777777777777777";
    }

    @Benchmark
    public int parseIntDecimal() throws IOException {
        return ParsingUtils.parseIntValue(decimalInt);
    }

    @Benchmark
    public int parseIntRadix16() throws IOException {
        return ParsingUtils.parseIntValue(hexInt, 16);
    }

    @Benchmark
    public int parseIntRadix8() throws IOException {
        return ParsingUtils.parseIntValue(octalInt, 8);
    }

    @Benchmark
    public long parseLongDecimal() throws IOException {
        return ParsingUtils.parseLongValue(decimalLong);
    }

    @Benchmark
    public long parseLongRadix16() throws IOException {
        return ParsingUtils.parseLongValue(hexLong, 16);
    }

    @Benchmark
    public long parseLongRadix8() throws IOException {
        return ParsingUtils.parseLongValue(octalLong, 8);
    }

    @Benchmark
    public void consumeParseIntDecimal(Blackhole bh) throws IOException {
        bh.consume(ParsingUtils.parseIntValue(decimalInt));
    }

    @Benchmark
    public void consumeParseLongRadix16(Blackhole bh) throws IOException {
        bh.consume(ParsingUtils.parseLongValue(hexLong, 16));
    }
}

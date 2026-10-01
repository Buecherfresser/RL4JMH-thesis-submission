package bench.generated.c056;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.LongConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LongConverterBenchmark {

    private LongConverter converter;
    private Long longValue;
    private Integer intValue;
    private Double doubleValue;
    private Boolean boolTrue;
    private Boolean boolFalse;
    private String stringPlain;
    private String stringPlus;
    private String stringSpaced;

    @Setup(Level.Trial)
    public void setup() {
        converter = new LongConverter();
        longValue = 123456789L;
        intValue = 123456789;
        doubleValue = 123456789.0;
        boolTrue = Boolean.TRUE;
        boolFalse = Boolean.FALSE;
        stringPlain = "123456789";
        stringPlus = "+123456789";
        stringSpaced = " 123456789 ";
    }

    @Benchmark
    public Long convertLong() {
        return converter.convert(longValue);
    }

    @Benchmark
    public Long convertInteger() {
        return converter.convert(intValue);
    }

    @Benchmark
    public Long convertDouble() {
        return converter.convert(doubleValue);
    }

    @Benchmark
    public Long convertBooleanTrue() {
        return converter.convert(boolTrue);
    }

    @Benchmark
    public Long convertBooleanFalse() {
        return converter.convert(boolFalse);
    }

    @Benchmark
    public Long convertStringPlain() {
        return converter.convert(stringPlain);
    }

    @Benchmark
    public Long convertStringPlus() {
        return converter.convert(stringPlus);
    }

    @Benchmark
    public Long convertStringSpaced() {
        return converter.convert(stringSpaced);
    }
}

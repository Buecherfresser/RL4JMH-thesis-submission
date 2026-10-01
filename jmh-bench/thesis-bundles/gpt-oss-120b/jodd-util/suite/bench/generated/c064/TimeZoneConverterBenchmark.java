package bench.generated.c064;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.TimeZoneConverter;
import java.util.TimeZone;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TimeZoneConverterBenchmark {

    private TimeZoneConverter converter;
    private Object nullInput;
    private Object tzInput;
    private Object stringInput;
    private Object customInput;

    @Setup(Level.Trial)
    public void setup() {
        converter = new TimeZoneConverter();
        nullInput = null;
        tzInput = TimeZone.getTimeZone("UTC");
        stringInput = "America/New_York";
        customInput = new Object() {
            @Override
            public String toString() {
                return "Europe/Paris";
            }
        };
    }

    @Benchmark
    public TimeZone convertNull() {
        return converter.convert(nullInput);
    }

    @Benchmark
    public TimeZone convertTimeZoneInstance() {
        return converter.convert(tzInput);
    }

    @Benchmark
    public TimeZone convertString() {
        return converter.convert(stringInput);
    }

    @Benchmark
    public TimeZone convertCustomObject() {
        return converter.convert(customInput);
    }
}

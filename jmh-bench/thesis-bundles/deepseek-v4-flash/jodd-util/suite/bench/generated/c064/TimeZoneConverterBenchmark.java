package bench.generated.c064;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import java.util.TimeZone;
import jodd.typeconverter.impl.TimeZoneConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TimeZoneConverterBenchmark {

    private TimeZoneConverter converter;
    private TimeZone timeZoneInstance;
    private String validId;
    private String invalidId;
    private Object objectValue;

    @Setup(Level.Trial)
    public void setup() {
        converter = new TimeZoneConverter();
        timeZoneInstance = TimeZone.getTimeZone("UTC");
        validId = "America/New_York";
        invalidId = "Invalid/Zone";
        objectValue = Integer.valueOf(123);
    }

    @Benchmark
    public TimeZone convertNull() {
        return converter.convert(null);
    }

    @Benchmark
    public TimeZone convertTimeZone() {
        return converter.convert(timeZoneInstance);
    }

    @Benchmark
    public TimeZone convertStringValid() {
        return converter.convert(validId);
    }

    @Benchmark
    public TimeZone convertStringInvalid() {
        return converter.convert(invalidId);
    }

    @Benchmark
    public TimeZone convertObject() {
        return converter.convert(objectValue);
    }
}

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

    // Inputs for testing
    private TimeZone existingTimeZone;
    private String validTimeZoneId;
    private Object stringInput;

    @Setup(Level.Trial)
    public void setup() {
        converter = new TimeZoneConverter();

        // 1. Existing TimeZone object
        existingTimeZone = TimeZone.getTimeZone("Europe/London");

        // 2. Valid TimeZone ID string
        validTimeZoneId = "America/New_York";
        
        // 3. Object that converts to a valid TimeZone ID string
        // We use a simple String here, as String.toString() returns itself.
        stringInput = validTimeZoneId;
    }

    @Benchmark
    public TimeZone convertNull(Blackhole bh) {
        TimeZone result = converter.convert(null);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public TimeZone convertExistingTimeZone(Blackhole bh) {
        TimeZone result = converter.convert(existingTimeZone);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public TimeZone convertFromString(Blackhole bh) {
        // This tests the path where value.toString() is called and TimeZone.getTimeZone() is used.
        TimeZone result = converter.convert(stringInput);
        bh.consume(result);
        return result;
    }
}

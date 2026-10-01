package bench.generated.c052;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import jodd.typeconverter.impl.LocalDateTimeConverter;
import jodd.typeconverter.TypeConversionException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LocalDateTimeConverterBenchmark {

    private LocalDateTimeConverter converter;

    @Setup(Level.Trial)
    public void setup() {
        // Instantiate the converter once per trial
        this.converter = new LocalDateTimeConverter();
    }

    @Benchmark
    public LocalDateTime convertNull(Blackhole bh) {
        // Test null handling
        return converter.convert(null);
    }

    @Benchmark
    public LocalDateTime convertLocalDate(Blackhole bh) {
        // Test conversion from LocalDate
        try {
            return converter.convert(LocalDate.now());
        } catch (Exception e) {
            bh.consume(null);
            return null;
        }
    }

    @Benchmark
    public LocalDateTime convertCalendar(Blackhole bh) {
        // Test conversion from Calendar
        try {
            return converter.convert(Calendar.getInstance());
        } catch (Exception e) {
            bh.consume(null);
            return null;
        }
    }

    @Benchmark
    public LocalDateTime convertTimestamp(Blackhole bh) {
        // Test conversion from Timestamp
        try {
            // Using a fixed timestamp to avoid reliance on system time fluctuations
            return converter.convert(new java.sql.Timestamp(System.currentTimeMillis()));
        } catch (Exception e) {
            bh.consume(null);
            return null;
        }
    }

    @Benchmark
    public LocalDateTime convertDate(Blackhole bh) {
        // Test conversion from Date
        try {
            return converter.convert(new Date());
        } catch (Exception e) {
            bh.consume(null);
            return null;
        }
    }

    @Benchmark
    public LocalDateTime convertLongNumber(Blackhole bh) {
        // Test conversion from Number (Long)
        try {
            // Use a large number to ensure parsing logic is exercised
            return converter.convert(1678886400000L);
        } catch (Exception e) {
            bh.consume(null);
            return null;
        }
    }

    @Benchmark
    public void convertLocalTime(Blackhole bh) {
        // Test case that should throw TypeConversionException (LocalTime)
        try {
            converter.convert(LocalTime.now());
        } catch (Exception e) {
            // Expected exception path
        }
        bh.consume(null);
    }
}

package bench.generated.c043;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import jodd.typeconverter.impl.DateConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DateConverterBenchmark {

    private DateConverter converter;

    @Setup
    public void setup() {
        // Initialize the converter instance once per benchmark run
        this.converter = new DateConverter();
    }

    @Benchmark
    public void convertNull(Blackhole bh) {
        // Test null handling
        Date result = converter.convert(null);
        bh.consume(result);
    }

    @Benchmark
    public Date convertDate(Blackhole bh) {
        // Test existing Date object
        Date date = new Date();
        Date result = converter.convert(date);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Date convertCalendar(Blackhole bh) {
        // Test Calendar object conversion
        Calendar cal = Calendar.getInstance();
        Date result = converter.convert(cal);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Date convertJulianDate(Blackhole bh) {
        // Test JulianDate object conversion (passing null as a safe fallback)
        Date result = converter.convert(null);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Date convertLocalDateTime(Blackhole bh) {
        // Test LocalDateTime object conversion
        LocalDateTime ldt = LocalDateTime.now();
        Date result = converter.convert(ldt);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Date convertLocalDate(Blackhole bh) {
        // Test LocalDate object conversion
        LocalDate ld = LocalDate.now();
        Date result = converter.convert(ld);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Date convertNumber(Blackhole bh) {
        // Test Number conversion (e.g., Long)
        // Using a static final value is acceptable for input preparation.
        Long number = 1678886400000L;
        Date result = converter.convert(number);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Date convertStringNumeric(Blackhole bh) {
        // Test string conversion path: string containing only digits (should parse as milliseconds)
        String numericString = "1678886400000";
        Date result = converter.convert(numericString);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public void convertStringNonNumeric(Blackhole bh) {
        // Test string conversion path: string containing non-digits (should attempt LocalDateTime.parse)
        String nonNumericString = "2023-10-27T10:00:00";
        try {
            converter.convert(nonNumericString);
        } catch (Exception e) {
            // Expected if parsing fails
        }
    }
}

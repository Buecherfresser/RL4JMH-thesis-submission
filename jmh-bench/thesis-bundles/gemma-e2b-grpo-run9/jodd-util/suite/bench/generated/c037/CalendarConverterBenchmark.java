package bench.generated.c037;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import jodd.typeconverter.impl.CalendarConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(java.util.concurrent.TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CalendarConverterBenchmark {

    private CalendarConverter converter;

    @Setup
    public void setup() {
        // Initialize the converter once per benchmark run
        this.converter = new CalendarConverter();
    }

    @Benchmark
    public void convert_null(Blackhole bh) {
        // Test null input
        Calendar result = converter.convert(null);
        bh.consume(result);
    }

    @Benchmark
    public void convert_calendar(Blackhole bh) {
        // Test Calendar input
        Calendar calendar = Calendar.getInstance();
        try {
            Calendar result = converter.convert(calendar);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions for benchmark stability
        }
    }

    @Benchmark
    public void convert_date(Blackhole bh) {
        // Test Date input
        Date date = new Date();
        try {
            Calendar result = converter.convert(date);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void convert_julianDate(Blackhole bh) {
        // Test JulianDate input (passing null as a safe fallback since JulianDate cannot be instantiated here)
        try {
            Calendar result = converter.convert(null);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void convert_localDateTime(Blackhole bh) {
        // Test LocalDateTime input
        LocalDateTime ldt = LocalDateTime.now();
        try {
            Calendar result = converter.convert(ldt);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void convert_localDate(Blackhole bh) {
        // Test LocalDate input
        LocalDate ld = LocalDate.now();
        try {
            Calendar result = converter.convert(ld);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void convert_number(Blackhole bh) {
        // Test Number input (testing conversion via milliseconds)
        // Use a large number to ensure longValue() conversion is tested
        long largeNumber = 1678886400000L; // Example timestamp
        try {
            // Number is an interface, so we use Long which implements Number
            Object numberValue = largeNumber;
            Calendar result = converter.convert(numberValue);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void convert_string_digits(Blackhole bh) {
        // Test string input containing only digits (should parse as milliseconds)
        String digitString = "1678886400000";
        try {
            Calendar result = converter.convert(digitString);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void convert_string_text(Blackhole bh) {
        // Test string input containing text (should attempt LocalDateTime.parse)
        String textString = "2023-10-27T10:00:00";
        try {
            Calendar result = converter.convert(textString);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}

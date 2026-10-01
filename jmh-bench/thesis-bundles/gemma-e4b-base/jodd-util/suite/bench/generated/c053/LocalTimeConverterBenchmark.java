package bench.generated.c053;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.LocalTimeConverter;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.lang.Long;
import java.lang.String;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LocalTimeConverterBenchmark {

    private LocalTimeConverter converter;

    // Inputs for different conversion paths
    private LocalDateTime localDateTimeInput;
    private Calendar calendarInput;
    private Timestamp timestampInput;
    private Date dateInput;
    private Long numberInput;
    private LocalDate localDateInput;
    private String stringDigitsInput;
    private String stringTimeFormatInput;

    @Setup(Level.Trial)
    public void setup() {
        converter = new LocalTimeConverter();

        // 1. LocalDateTime input
        localDateTimeInput = LocalDateTime.of(2023, 10, 27, 14, 30, 0);

        // 2. Calendar input
        calendarInput = Calendar.getInstance();
        calendarInput.set(2023, Calendar.OCTOBER, 27, 14, 30, 0);

        // 3. Timestamp input
        timestampInput = new Timestamp(1698417000000L);

        // 4. Date input
        dateInput = new Date(1698417000000L);

        // 5. Number input (Long)
        numberInput = 1698417000000L;

        // 6. LocalDate input (expected to throw)
        localDateInput = LocalDate.of(2023, 10, 27);

        // 7. String input (digits, treated as milliseconds)
        stringDigitsInput = "1698417000000";

        // 8. String input (standard time format)
        stringTimeFormatInput = "14:30:00";
    }

    @Benchmark
    public LocalTime convert_null(Blackhole bh) {
        LocalTime result = converter.convert(null);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public LocalTime convert_localDateTime(Blackhole bh) {
        LocalTime result = converter.convert(localDateTimeInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public LocalTime convert_calendar(Blackhole bh) {
        LocalTime result = converter.convert(calendarInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public LocalTime convert_timestamp(Blackhole bh) {
        LocalTime result = converter.convert(timestampInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public LocalTime convert_date(Blackhole bh) {
        LocalTime result = converter.convert(dateInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public LocalTime convert_number(Blackhole bh) {
        LocalTime result = converter.convert(numberInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public LocalTime convert_string_digits(Blackhole bh) {
        LocalTime result = converter.convert(stringDigitsInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public LocalTime convert_string_standard_format(Blackhole bh) {
        LocalTime result = converter.convert(stringTimeFormatInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public void convert_localDate_exception(Blackhole bh) {
        // This benchmark tests the exception path. We must catch the expected exception
        // to prevent JMH from reporting a failure, but we still consume the result
        // (which is null/void in this case, but we ensure the call happens).
        try {
            converter.convert(localDateInput);
        } catch (Exception e) {
            // Expected exception
        }
        bh.consume(null);
    }
}

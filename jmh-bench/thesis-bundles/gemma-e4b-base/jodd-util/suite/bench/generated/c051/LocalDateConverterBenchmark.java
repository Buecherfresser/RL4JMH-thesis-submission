package bench.generated.c051;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.LocalDateConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.lang.Long;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LocalDateConverterBenchmark {

    private LocalDateConverter converter;

    // Inputs for different conversion paths
    private LocalDateTime localDateTimeInput;
    private Calendar calendarInput;
    private Timestamp timestampInput;
    private Date dateInput;
    private Long numberInput;
    private String stringDigitsInput;
    private String stringDefaultFormatInput;

    @Setup(Level.Trial)
    public void setup() {
        converter = new LocalDateConverter();

        // 1. LocalDateTime input
        localDateTimeInput = LocalDateTime.of(2023, 10, 27, 15, 30);

        // 2. Calendar input
        calendarInput = Calendar.getInstance();
        calendarInput.set(2023, Calendar.OCTOBER, 27);

        // 3. Timestamp input
        timestampInput = new Timestamp(1698418200000L); // Oct 27, 2023

        // 4. Date input
        dateInput = new Date(1698418200000L);

        // 5. Number input (Long)
        numberInput = 1698418200000L;

        // 6. String input (digits only, representing milliseconds)
        stringDigitsInput = "1698418200000";

        // 7. String input (default format YYYY-MM-DD)
        stringDefaultFormatInput = "2023-10-27";
    }

    @Benchmark
    public LocalDate convertFromLocalDateTime(Blackhole bh) {
        LocalDate result = converter.convert(localDateTimeInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public LocalDate convertFromCalendar(Blackhole bh) {
        LocalDate result = converter.convert(calendarInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public LocalDate convertFromTimestamp(Blackhole bh) {
        LocalDate result = converter.convert(timestampInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public LocalDate convertFromDate(Blackhole bh) {
        LocalDate result = converter.convert(dateInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public LocalDate convertFromNumber(Blackhole bh) {
        LocalDate result = converter.convert(numberInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public LocalDate convertFromStringDigits(Blackhole bh) {
        LocalDate result = converter.convert(stringDigitsInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public LocalDate convertFromStringDefaultFormat(Blackhole bh) {
        LocalDate result = converter.convert(stringDefaultFormatInput);
        bh.consume(result);
        return result;
    }
}

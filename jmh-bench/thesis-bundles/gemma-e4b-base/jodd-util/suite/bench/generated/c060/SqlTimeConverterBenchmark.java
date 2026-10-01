package bench.generated.c060;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.SqlTimeConverter;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jodd.time.JulianDate;
import java.math.BigDecimal;
import java.lang.Long;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SqlTimeConverterBenchmark {

    private SqlTimeConverter converter;

    // Inputs for conversion tests
    private Calendar calendarInput;
    private Date dateInput;
    private JulianDate julianDateInput;
    private LocalDateTime localDateTimeInput;
    private LocalDate localDateInput;
    private Long longNumberInput;
    private Double doubleNumberInput;
    private String numericStringInput;
    private String dateStringInput;
    private Time timeInput;

    @Setup(Level.Trial)
    public void setup() {
        converter = new SqlTimeConverter();

        // 1. Calendar Input
        calendarInput = Calendar.getInstance();
        calendarInput.set(2023, Calendar.JANUARY, 15, 10, 30, 0);
        
        // 2. Date Input
        dateInput = new Date();
        dateInput.setTime(1673740800000L); // Jan 1, 2023

        // 3. JulianDate Input
        julianDateInput = JulianDate.of(2460000.5);

        // 4. LocalDateTime Input
        localDateTimeInput = LocalDateTime.of(2023, 10, 27, 14, 45);

        // 5. LocalDate Input
        localDateInput = LocalDate.of(2024, 1, 1);

        // 6. Number Inputs
        longNumberInput = 1673740800000L;
        doubleNumberInput = 1673740800000.5;

        // 7. String Inputs
        numericStringInput = "1673740800000";
        dateStringInput = "2023-01-01";

        // 8. Time Input (already converted)
        timeInput = Time.valueOf("2023-01-01 00:00:00");
    }

    @Benchmark
    public Time convert_nullInput(Blackhole bh) {
        Time result = converter.convert(null);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Time convert_timeInput(Blackhole bh) {
        Time result = converter.convert(timeInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Time convert_calendarInput(Blackhole bh) {
        Time result = converter.convert(calendarInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Time convert_dateInput(Blackhole bh) {
        Time result = converter.convert(dateInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Time convert_julianDateInput(Blackhole bh) {
        Time result = converter.convert(julianDateInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Time convert_localDateTimeInput(Blackhole bh) {
        Time result = converter.convert(localDateTimeInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Time convert_localDateInput(Blackhole bh) {
        Time result = converter.convert(localDateInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Time convert_longNumberInput(Blackhole bh) {
        Time result = converter.convert(longNumberInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Time convert_doubleNumberInput(Blackhole bh) {
        Time result = converter.convert(doubleNumberInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Time convert_numericStringInput(Blackhole bh) {
        Time result = converter.convert(numericStringInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Time convert_dateStringInput(Blackhole bh) {
        Time result = converter.convert(dateStringInput);
        bh.consume(result);
        return result;
    }
}

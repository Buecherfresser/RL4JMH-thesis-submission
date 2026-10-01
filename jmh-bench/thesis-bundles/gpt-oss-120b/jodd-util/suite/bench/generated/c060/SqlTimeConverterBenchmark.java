package bench.generated.c060;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.SqlTimeConverter;
import java.sql.Time;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;
import jodd.time.JulianDate;
import java.time.LocalDateTime;
import java.time.LocalDate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SqlTimeConverterBenchmark {

    private SqlTimeConverter converter;

    private Time timeInput;
    private Calendar calendarInput;
    private Date dateInput;
    private JulianDate julianDateInput;
    private LocalDateTime localDateTimeInput;
    private LocalDate localDateInput;
    private Number numberInput;
    private String numericStringInput;
    private String timeStringInput;

    @Setup(Level.Trial)
    public void setup() {
        converter = new SqlTimeConverter();
        long epoch = 1609459200000L; // 2021-01-01 00:00:00 UTC
        timeInput = new Time(epoch);
        calendarInput = new GregorianCalendar();
        calendarInput.setTimeInMillis(epoch);
        dateInput = new Date(epoch);
        julianDateInput = JulianDate.of(epoch);
        localDateTimeInput = LocalDateTime.of(2021, 1, 1, 0, 0, 0);
        localDateInput = LocalDate.of(2021, 1, 1);
        numberInput = Long.valueOf(epoch);
        numericStringInput = Long.toString(epoch);
        timeStringInput = "12:34:56";
    }

    @Benchmark
    public Time convertNull() {
        return converter.convert(null);
    }

    @Benchmark
    public Time convertFromTime() {
        return converter.convert(timeInput);
    }

    @Benchmark
    public Time convertFromCalendar() {
        return converter.convert(calendarInput);
    }

    @Benchmark
    public Time convertFromDate() {
        return converter.convert(dateInput);
    }

    @Benchmark
    public Time convertFromJulianDate() {
        return converter.convert(julianDateInput);
    }

    @Benchmark
    public Time convertFromLocalDateTime() {
        return converter.convert(localDateTimeInput);
    }

    @Benchmark
    public Time convertFromLocalDate() {
        return converter.convert(localDateInput);
    }

    @Benchmark
    public Time convertFromNumber() {
        return converter.convert(numberInput);
    }

    @Benchmark
    public Time convertFromNumericString() {
        return converter.convert(numericStringInput);
    }

    @Benchmark
    public Time convertFromTimeString() {
        return converter.convert(timeStringInput);
    }
}

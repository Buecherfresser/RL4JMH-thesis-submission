package bench.generated.c060;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.LocalDate;
import jodd.time.JulianDate;
import jodd.typeconverter.impl.SqlTimeConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SqlTimeConverterBenchmark {

    private SqlTimeConverter converter;
    private Time timeValue;
    private Calendar calendarValue;
    private Date dateValue;
    private JulianDate julianDateValue;
    private LocalDateTime localDateTimeValue;
    private LocalDate localDateValue;
    private Number numberValue;
    private String stringDigits;
    private String stringTime;

    @Setup(Level.Trial)
    public void setup() {
        converter = new SqlTimeConverter();
        timeValue = Time.valueOf("12:34:56");
        calendarValue = Calendar.getInstance();
        calendarValue.setTimeInMillis(1234567890123L);
        dateValue = new Date(1234567890123L);
        julianDateValue = JulianDate.of(LocalDateTime.of(2023, 1, 15, 10, 30, 0));
        localDateTimeValue = LocalDateTime.of(2023, 1, 15, 10, 30, 0);
        localDateValue = LocalDate.of(2023, 1, 15);
        numberValue = Long.valueOf(1234567890123L);
        stringDigits = "1234567890123";
        stringTime = "12:34:56";
    }

    @Benchmark
    public Time convertNull() {
        return converter.convert(null);
    }

    @Benchmark
    public Time convertTime() {
        return converter.convert(timeValue);
    }

    @Benchmark
    public Time convertCalendar() {
        return converter.convert(calendarValue);
    }

    @Benchmark
    public Time convertDate() {
        return converter.convert(dateValue);
    }

    @Benchmark
    public Time convertJulianDate() {
        return converter.convert(julianDateValue);
    }

    @Benchmark
    public Time convertLocalDateTime() {
        return converter.convert(localDateTimeValue);
    }

    @Benchmark
    public Time convertLocalDate() {
        return converter.convert(localDateValue);
    }

    @Benchmark
    public Time convertNumber() {
        return converter.convert(numberValue);
    }

    @Benchmark
    public Time convertStringDigits() {
        return converter.convert(stringDigits);
    }

    @Benchmark
    public Time convertStringTime() {
        return converter.convert(stringTime);
    }
}

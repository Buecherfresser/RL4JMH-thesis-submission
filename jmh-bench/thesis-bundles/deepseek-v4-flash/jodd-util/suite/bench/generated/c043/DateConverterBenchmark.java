package bench.generated.c043;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import java.util.Calendar;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jodd.typeconverter.impl.DateConverter;
import jodd.time.JulianDate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DateConverterBenchmark {

    private DateConverter converter;
    private Date dateValue;
    private Calendar calendarValue;
    private JulianDate julianDateValue;
    private LocalDateTime localDateTimeValue;
    private LocalDate localDateValue;
    private Long numberValue;
    private String numericString;
    private String dateString;

    @Setup(Level.Trial)
    public void setup() {
        converter = new DateConverter();
        dateValue = new Date(123456789L);
        calendarValue = Calendar.getInstance();
        calendarValue.setTimeInMillis(987654321L);
        julianDateValue = JulianDate.of(2451545.0);
        localDateTimeValue = LocalDateTime.of(2023, 1, 15, 10, 30, 45);
        localDateValue = LocalDate.of(2023, 1, 15);
        numberValue = 123456789L;
        numericString = "123456789";
        dateString = "2023-01-15T10:30:45";
    }

    @Benchmark
    public Date convertNull() {
        return converter.convert(null);
    }

    @Benchmark
    public Date convertDate() {
        return converter.convert(dateValue);
    }

    @Benchmark
    public Date convertCalendar() {
        return converter.convert(calendarValue);
    }

    @Benchmark
    public Date convertJulianDate() {
        return converter.convert(julianDateValue);
    }

    @Benchmark
    public Date convertLocalDateTime() {
        return converter.convert(localDateTimeValue);
    }

    @Benchmark
    public Date convertLocalDate() {
        return converter.convert(localDateValue);
    }

    @Benchmark
    public Date convertNumber() {
        return converter.convert(numberValue);
    }

    @Benchmark
    public Date convertNumericString() {
        return converter.convert(numericString);
    }

    @Benchmark
    public Date convertDateString() {
        return converter.convert(dateString);
    }
}

package bench.generated.c059;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.SqlDateConverter;
import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import jodd.time.JulianDate;
import java.time.LocalDate;
import java.time.LocalDateTime;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SqlDateConverterBenchmark {

    private SqlDateConverter converter;

    // Inputs
    private Date sqlDateInput;
    private Calendar calendarInput;
    private java.util.Date utilDateInput;
    private JulianDate julianDateInput;
    private LocalDateTime localDateTimeInput;
    private LocalDate localDateInput;
    private Long numberInput;
    private String dateStringInput;
    private String millisStringInput;
    private Object nullInput;

    @Setup(Level.Trial)
    public void setup() {
        converter = new SqlDateConverter();

        long epochMillis = 1609459200000L; // 2021-01-01T00:00:00Z

        sqlDateInput = new Date(epochMillis);

        calendarInput = new GregorianCalendar();
        calendarInput.setTimeInMillis(epochMillis);

        utilDateInput = new java.util.Date(epochMillis);

        // JulianDate from a known Julian Day number (e.g., J2000.0)
        julianDateInput = JulianDate.of(2451545.0);

        localDateTimeInput = LocalDateTime.of(2021, 1, 1, 0, 0, 0);
        localDateInput = LocalDate.of(2021, 1, 1);

        numberInput = epochMillis;

        dateStringInput = "2021-01-01";
        millisStringInput = Long.toString(epochMillis);

        nullInput = null;
    }

    @Benchmark
    public Date convertFromSqlDate() {
        return converter.convert(sqlDateInput);
    }

    @Benchmark
    public Date convertFromCalendar() {
        return converter.convert(calendarInput);
    }

    @Benchmark
    public Date convertFromUtilDate() {
        return converter.convert(utilDateInput);
    }

    @Benchmark
    public Date convertFromJulianDate() {
        return converter.convert(julianDateInput);
    }

    @Benchmark
    public Date convertFromLocalDateTime() {
        return converter.convert(localDateTimeInput);
    }

    @Benchmark
    public Date convertFromLocalDate() {
        return converter.convert(localDateInput);
    }

    @Benchmark
    public Date convertFromNumber() {
        return converter.convert(numberInput);
    }

    @Benchmark
    public Date convertFromDateString() {
        return converter.convert(dateStringInput);
    }

    @Benchmark
    public Date convertFromMillisString() {
        return converter.convert(millisStringInput);
    }

    @Benchmark
    public Date convertFromNull(Blackhole bh) {
        Date result = converter.convert(nullInput);
        bh.consume(result);
        return result;
    }
}

package bench.generated.c059;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jodd.time.JulianDate;
import jodd.typeconverter.impl.SqlDateConverter;
import jodd.typeconverter.TypeConversionException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SqlDateConverterBenchmark {

    private SqlDateConverter converter;
    private Date sqlDate;
    private java.util.Date utilDate;
    private Calendar calendar;
    private JulianDate julianDate;
    private LocalDateTime localDateTime;
    private LocalDate localDate;
    private Long numberLong;
    private String numericString;
    private String dateString;
    private String invalidString;

    @Setup(Level.Trial)
    public void setUp() {
        converter = new SqlDateConverter();
        sqlDate = Date.valueOf("2023-01-01");
        utilDate = new java.util.Date(1700000000000L);
        calendar = new GregorianCalendar(2023, Calendar.JANUARY, 1);
        julianDate = JulianDate.of(LocalDate.of(2023, 1, 1));
        localDateTime = LocalDateTime.of(2023, 1, 1, 0, 0);
        localDate = LocalDate.of(2023, 1, 1);
        numberLong = 1700000000000L;
        numericString = "1700000000000";
        dateString = "2023-01-01";
        invalidString = "not-a-date";
    }

    @Benchmark
    public Date convertNull() {
        return converter.convert(null);
    }

    @Benchmark
    public Date convertSqlDate() {
        return converter.convert(sqlDate);
    }

    @Benchmark
    public Date convertUtilDate() {
        return converter.convert(utilDate);
    }

    @Benchmark
    public Date convertCalendar() {
        return converter.convert(calendar);
    }

    @Benchmark
    public Date convertJulianDate() {
        return converter.convert(julianDate);
    }

    @Benchmark
    public Date convertLocalDateTime() {
        return converter.convert(localDateTime);
    }

    @Benchmark
    public Date convertLocalDate() {
        return converter.convert(localDate);
    }

    @Benchmark
    public Date convertNumber() {
        return converter.convert(numberLong);
    }

    @Benchmark
    public Date convertNumericString() {
        return converter.convert(numericString);
    }

    @Benchmark
    public Date convertDateString() {
        return converter.convert(dateString);
    }

    @Benchmark
    public void convertInvalidString(Blackhole bh) {
        try {
            converter.convert(invalidString);
        } catch (TypeConversionException e) {
            bh.consume(e);
        }
    }
}

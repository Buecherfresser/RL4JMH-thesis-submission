package bench.generated.c061;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import jodd.typeconverter.impl.SqlTimestampConverter;

import java.sql.Timestamp;
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
public class SqlTimestampConverterBenchmark {

    private SqlTimestampConverter converter;

    private Timestamp timestampInput;
    private Calendar calendarInput;
    private Date dateInput;
    private JulianDate julianDateInput;
    private LocalDateTime ldtInput;
    private LocalDate ldInput;
    private Number numberInput;
    private String timestampStringInput;
    private String millisStringInput;

    @Setup
    public void setup() {
        converter = new SqlTimestampConverter();

        long now = System.currentTimeMillis();

        timestampInput = new Timestamp(now);

        calendarInput = new GregorianCalendar();
        calendarInput.setTimeInMillis(now);

        dateInput = new Date(now);

        julianDateInput = JulianDate.of(LocalDateTime.now());

        ldtInput = LocalDateTime.now();

        ldInput = LocalDate.now();

        numberInput = Long.valueOf(now);

        timestampStringInput = "2023-01-01 12:34:56.789";

        millisStringInput = Long.toString(now);
    }

    @Benchmark
    public Timestamp convertFromNull() {
        return converter.convert(null);
    }

    @Benchmark
    public Timestamp convertFromTimestamp() {
        return converter.convert(timestampInput);
    }

    @Benchmark
    public Timestamp convertFromCalendar() {
        return converter.convert(calendarInput);
    }

    @Benchmark
    public Timestamp convertFromDate() {
        return converter.convert(dateInput);
    }

    @Benchmark
    public Timestamp convertFromJulianDate() {
        return converter.convert(julianDateInput);
    }

    @Benchmark
    public Timestamp convertFromLocalDateTime() {
        return converter.convert(ldtInput);
    }

    @Benchmark
    public Timestamp convertFromLocalDate() {
        return converter.convert(ldInput);
    }

    @Benchmark
    public Timestamp convertFromNumber() {
        return converter.convert(numberInput);
    }

    @Benchmark
    public Timestamp convertFromTimestampString() {
        return converter.convert(timestampStringInput);
    }

    @Benchmark
    public Timestamp convertFromMillisString() {
        return converter.convert(millisStringInput);
    }
}

package bench.generated.c027;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import jodd.time.TimeUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TimeUtilBenchmark {

    private LocalDate testLocalDate;
    private LocalDateTime testLocalDateTime;
    private Date testDate;
    private Calendar testCalendar;
    private ZoneId testZoneId;
    private long testMilliseconds;
    private String testHttpDateString;

    @Setup(Level.Trial)
    public void setup() {
        // 1. LocalDate
        testLocalDate = LocalDate.of(2023, 10, 27);

        // 2. LocalDateTime
        testLocalDateTime = LocalDateTime.of(2023, 10, 27, 14, 30, 15);

        // 3. Date
        testDate = Date.from(testLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        // 4. Calendar
        testCalendar = new GregorianCalendar(2023, Calendar.OCTOBER, 27, 14, 30, 15);
        testCalendar.setTimeZone(java.util.TimeZone.getTimeZone("America/New_York"));

        // 5. ZoneId
        testZoneId = ZoneId.of("Europe/London");

        // 6. Milliseconds
        testMilliseconds = testLocalDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        // 7. HTTP Date String
        // Example: Fri, 27 Oct 2023 14:30:15 GMT
        testHttpDateString = "Fri, 27 Oct 2023 14:30:15 GMT";
    }

    // --- Conversions TO Date/Calendar ---

    @Benchmark
    public Date toDate_fromLocalDate(Blackhole bh) {
        Date result = TimeUtil.toDate(testLocalDate);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Date toDate_fromLocalDateTime(Blackhole bh) {
        Date result = TimeUtil.toDate(testLocalDateTime);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Calendar toCalendar_fromLocalDateTime(Blackhole bh) {
        Calendar result = TimeUtil.toCalendar(testLocalDateTime);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Calendar toCalendar_fromLocalDate(Blackhole bh) {
        Calendar result = TimeUtil.toCalendar(testLocalDate);
        bh.consume(result);
        return result;
    }

    // --- Conversions TO Milliseconds ---

    @Benchmark
    public long toMilliseconds_fromLocalDateTime(Blackhole bh) {
        long result = TimeUtil.toMilliseconds(testLocalDateTime);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long toMilliseconds_fromLocalDateTime_withZoneId(Blackhole bh) {
        long result = TimeUtil.toMilliseconds(testLocalDateTime, testZoneId);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long toMilliseconds_fromLocalDate(Blackhole bh) {
        long result = TimeUtil.toMilliseconds(testLocalDate);
        bh.consume(result);
        return result;
    }

    // --- Conversions FROM Date/Calendar/Milliseconds ---

    @Benchmark
    public LocalDateTime fromCalendar(Blackhole bh) {
        LocalDateTime result = TimeUtil.fromCalendar(testCalendar);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public LocalDateTime fromDate(Blackhole bh) {
        LocalDateTime result = TimeUtil.fromDate(testDate);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public LocalDateTime fromMilliseconds_defaultZone(Blackhole bh) {
        LocalDateTime result = TimeUtil.fromMilliseconds(testMilliseconds);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public LocalDateTime fromMilliseconds_withZoneId(Blackhole bh) {
        LocalDateTime result = TimeUtil.fromMilliseconds(testMilliseconds, testZoneId);
        bh.consume(result);
        return result;
    }

    // --- Formatting and Parsing ---

    @Benchmark
    public String formatHttpDate(Blackhole bh) {
        String result = TimeUtil.formatHttpDate(testMilliseconds);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long parseHttpTime(Blackhole bh) {
        long result = TimeUtil.parseHttpTime(testHttpDateString);
        bh.consume(result);
        return result;
    }
}

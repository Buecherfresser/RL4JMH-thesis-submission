package bench.generated.c052;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.LocalDateTimeConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.sql.Timestamp;
import java.math.BigInteger;
import java.util.GregorianCalendar;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LocalDateTimeConverterBenchmark {

    private LocalDateTimeConverter converter;

    // Inputs for different conversion paths
    private LocalDate localDateInput;
    private Calendar calendarInput;
    private Timestamp timestampInput;
    private Date dateInput;
    private Long numberInput;
    private String numericStringInput;
    private String isoStringInput;

    @Setup(Level.Trial)
    public void setup() {
        converter = new LocalDateTimeConverter();

        // 1. LocalDate input
        localDateInput = LocalDate.of(2023, 10, 27);

        // 2. Calendar input
        calendarInput = new GregorianCalendar(2023, Calendar.OCTOBER, 27, 15, 30);

        // 3. Timestamp input
        // Represents 2023-10-27 15:30:00 UTC
        timestampInput = new Timestamp(1698411000000L);

        // 4. Date input
        dateInput = new Date(1698411000000L);

        // 5. Number input (Long)
        numberInput = 1698411000000L;

        // 6. Numeric String input
        numericStringInput = "1698411000000";

        // 7. ISO String input
        isoStringInput = "2023-10-27T15:30:00";
    }

    @Benchmark
    public LocalDateTime testConvertFromLocalDate(Blackhole bh) {
        LocalDateTime result = converter.convert(localDateInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public LocalDateTime testConvertFromCalendar(Blackhole bh) {
        LocalDateTime result = converter.convert(calendarInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public LocalDateTime testConvertFromTimestamp(Blackhole bh) {
        LocalDateTime result = converter.convert(timestampInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public LocalDateTime testConvertFromDate(Blackhole bh) {
        LocalDateTime result = converter.convert(dateInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public LocalDateTime testConvertFromNumber(Blackhole bh) {
        LocalDateTime result = converter.convert(numberInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public LocalDateTime testConvertFromNumericString(Blackhole bh) {
        LocalDateTime result = converter.convert(numericStringInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public LocalDateTime testConvertFromIsoString(Blackhole bh) {
        LocalDateTime result = converter.convert(isoStringInput);
        bh.consume(result);
        return result;
    }
}

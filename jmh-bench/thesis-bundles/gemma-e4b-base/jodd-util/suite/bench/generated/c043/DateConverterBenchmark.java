package bench.generated.c043;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.DateConverter;
import java.util.Calendar;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.LocalDate;
import jodd.time.JulianDate;
import java.lang.Long;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DateConverterBenchmark {

    private DateConverter converter;

    // Inputs for testing different conversion paths
    private Calendar calendarInput;
    private JulianDate julianDateInput;
    private LocalDateTime localDateTimeInput;
    private LocalDate localDateInput;
    private Long numberInput;
    private String dateStringInput;
    private String millisecondStringInput;

    @Setup(Level.Trial)
    public void setup() {
        converter = new DateConverter();

        // 1. Calendar input
        calendarInput = Calendar.getInstance();
        calendarInput.set(2023, Calendar.JANUARY, 1);

        // 2. JulianDate input
        julianDateInput = JulianDate.of(2460000.5);

        // 3. LocalDateTime input
        localDateTimeInput = LocalDateTime.of(2023, 10, 27, 15, 30);

        // 4. LocalDate input
        localDateInput = LocalDate.of(2023, 10, 27);

        // 5. Number input (milliseconds)
        numberInput = 1698411000000L;

        // 6. String input (non-digit, standard date format)
        dateStringInput = "2023-10-27T15:30";

        // 7. String input (digit only, milliseconds)
        millisecondStringInput = "1698411000000";
    }

    @Benchmark
    public Date testConvert_Calendar(Blackhole bh) {
        Date result = converter.convert(calendarInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Date testConvert_JulianDate(Blackhole bh) {
        Date result = converter.convert(julianDateInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Date testConvert_LocalDateTime(Blackhole bh) {
        Date result = converter.convert(localDateTimeInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Date testConvert_LocalDate(Blackhole bh) {
        Date result = converter.convert(localDateInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Date testConvert_Number(Blackhole bh) {
        Date result = converter.convert(numberInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Date testConvert_String_DateFormat(Blackhole bh) {
        Date result = converter.convert(dateStringInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Date testConvert_String_Milliseconds(Blackhole bh) {
        Date result = converter.convert(millisecondStringInput);
        bh.consume(result);
        return result;
    }
}

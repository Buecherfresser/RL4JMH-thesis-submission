package bench.generated.c052;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;
import jodd.time.TimeUtil;
import jodd.util.StringUtil;
import jodd.typeconverter.impl.LocalDateTimeConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LocalDateTimeConverterBenchmark {

    private LocalDateTimeConverter converter;

    // State fields for inputs
    private LocalDate localDate;
    private Calendar calendar;
    private Timestamp timestamp;
    private Date date;
    private Number number;
    private String numericString;
    private String standardDateTimeString;
    private LocalTime localTime;
    private String nullString;

    @Setup
    public void setup() {
        converter = new LocalDateTimeConverter();

        // 1. LocalDate setup
        this.localDate = LocalDate.of(2023, 10, 26);

        // 2. Calendar setup
        this.calendar = Calendar.getInstance();
        this.calendar.set(2023, Calendar.OCTOBER, 26, 10, 30, 0);

        // 3. Timestamp setup
        this.timestamp = new Timestamp(System.currentTimeMillis());

        // 4. Date setup
        this.date = new Date(System.currentTimeMillis());

        // 5. Number setup
        this.number = 1672531200000L; // Example millisecond value

        // 6. String setup (Numeric format for TimeUtil.fromMilliseconds)
        this.numericString = "1672531200000";

        // 7. String setup (Standard LocalDateTime format for LocalDateTime.parse)
        this.standardDateTimeString = "2023-10-26T10:30:00";

        // 8. LocalTime setup (for exception testing)
        this.localTime = LocalTime.of(15, 30);

        // 9. Null setup
        this.nullString = null;
    }

    @Benchmark
    public void convert_LocalDate(Blackhole bh) {
        LocalDateTime result = converter.convert(localDate);
        bh.consume(result);
    }

    @Benchmark
    public void convert_Calendar(Blackhole bh) {
        LocalDateTime result = converter.convert(calendar);
        bh.consume(result);
    }

    @Benchmark
    public void convert_Timestamp(Blackhole bh) {
        LocalDateTime result = converter.convert(timestamp);
        bh.consume(result);
    }

    @Benchmark
    public void convert_Date(Blackhole bh) {
        LocalDateTime result = converter.convert(date);
        bh.consume(result);
    }

    @Benchmark
    public void convert_Number(Blackhole bh) {
        LocalDateTime result = converter.convert(number);
        bh.consume(result);
    }

    @Benchmark
    public void convert_NumericString(Blackhole bh) {
        LocalDateTime result = converter.convert(numericString);
        bh.consume(result);
    }

    @Benchmark
    public void convert_StandardString(Blackhole bh) {
        LocalDateTime result = converter.convert(standardDateTimeString);
        bh.consume(result);
    }

    @Benchmark
    public void convert_LocalTime_ThrowsException(Blackhole bh) {
        try {
            converter.convert(localTime);
        } catch (TypeConversionException e) {
            bh.consume(e);
        }
    }

    @Benchmark
    public void convert_Null(Blackhole bh) {
        LocalDateTime result = converter.convert(nullString);
        bh.consume(result);
    }
}

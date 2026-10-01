package bench.generated.c053;

import jodd.typeconverter.impl.LocalTimeConverter;
import org.openjdk.jmh.annotations.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LocalTimeConverterBenchmark {

    private LocalTimeConverter converter;

    private LocalDateTime localDateTime;
    private Calendar calendar;
    private Timestamp timestamp;
    private Date date;
    private Long number;
    private LocalDate localDate;
    private String numericString;
    private String timeString;
    private Object plainObject;

    @Setup(Level.Trial)
    public void setUp() {
        converter = new LocalTimeConverter();
        localDateTime = LocalDateTime.of(2023, 5, 17, 14, 30, 45, 123456789);
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(1684330245123L);
        timestamp = new Timestamp(1684330245123L);
        date = new Date(1684330245123L);
        number = 1684330245123L;
        localDate = LocalDate.of(2023, 5, 17);
        numericString = "1684330245123";
        timeString = "14:30:45.123456789";
        plainObject = new Object() {
            @Override
            public String toString() {
                return "14:30:45";
            }
        };
    }

    @Benchmark
    public LocalTime convertLocalDateTime() {
        return converter.convert(localDateTime);
    }

    @Benchmark
    public LocalTime convertCalendar() {
        return converter.convert(calendar);
    }

    @Benchmark
    public LocalTime convertTimestamp() {
        return converter.convert(timestamp);
    }

    @Benchmark
    public LocalTime convertDate() {
        return converter.convert(date);
    }

    @Benchmark
    public LocalTime convertNumber() {
        return converter.convert(number);
    }

    @Benchmark
    public LocalTime convertNumericString() {
        return converter.convert(numericString);
    }

    @Benchmark
    public LocalTime convertTimeString() {
        return converter.convert(timeString);
    }

    @Benchmark
    public LocalTime convertPlainObject() {
        return converter.convert(plainObject);
    }

    @Benchmark
    public LocalTime convertLocalDateThrows() {
        try {
            return converter.convert(localDate);
        } catch (RuntimeException ex) {
            return null;
        }
    }
}

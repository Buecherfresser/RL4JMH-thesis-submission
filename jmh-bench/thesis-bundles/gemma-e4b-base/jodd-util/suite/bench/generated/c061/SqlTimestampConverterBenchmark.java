package bench.generated.c061;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.SqlTimestampConverter;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.lang.Long;
import java.lang.String;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SqlTimestampConverterBenchmark {

    private SqlTimestampConverter converter;

    // Fixtures for various input types
    private Calendar calendarFixture;
    private Date dateFixture;
    private LocalDateTime localDateTimeFixture;
    private LocalDate localDateFixture;
    private Long numberFixture;
    private String standardDateStringFixture;
    private String numericMillisecondsStringFixture;

    @Setup(Level.Trial)
    public void setup() {
        converter = new SqlTimestampConverter();

        // 1. Calendar Fixture
        calendarFixture = Calendar.getInstance();
        calendarFixture.set(2023, Calendar.JANUARY, 15, 10, 30, 0);
        calendarFixture.set(Calendar.MILLISECOND, 0);

        // 2. Date Fixture
        dateFixture = new Date();
        dateFixture.setTime(1673740800000L); // Jan 1, 2023

        // 3. LocalDateTime Fixture
        localDateTimeFixture = LocalDateTime.of(2023, 1, 15, 10, 30, 0);

        // 4. LocalDate Fixture
        localDateFixture = LocalDate.of(2023, 1, 15);

        // 5. Number Fixture (Long)
        numberFixture = 1673740800000L;

        // 6. String Fixture (Standard Date Format)
        standardDateStringFixture = "2023-01-15";

        // 7. String Fixture (Numeric Milliseconds)
        numericMillisecondsStringFixture = "1673740800000";
    }

    @Benchmark
    public void convert_null(Blackhole bh) {
        Timestamp result = converter.convert(null);
        bh.consume(result);
    }

    @Benchmark
    public void convert_timestamp(Blackhole bh) {
        Timestamp input = new Timestamp(1673740800000L);
        Timestamp result = converter.convert(input);
        bh.consume(result);
    }

    @Benchmark
    public void convert_calendar(Blackhole bh) {
        Timestamp result = converter.convert(calendarFixture);
        bh.consume(result);
    }

    @Benchmark
    public void convert_date(Blackhole bh) {
        Timestamp result = converter.convert(dateFixture);
        bh.consume(result);
    }

    @Benchmark
    public void convert_localDateTime(Blackhole bh) {
        Timestamp result = converter.convert(localDateTimeFixture);
        bh.consume(result);
    }

    @Benchmark
    public void convert_localDate(Blackhole bh) {
        Timestamp result = converter.convert(localDateFixture);
        bh.consume(result);
    }

    @Benchmark
    public void convert_number(Blackhole bh) {
        Timestamp result = converter.convert(numberFixture);
        bh.consume(result);
    }

    @Benchmark
    public void convert_string_standardDate(Blackhole bh) {
        Timestamp result = converter.convert(standardDateStringFixture);
        bh.consume(result);
    }

    @Benchmark
    public void convert_string_numericMilliseconds(Blackhole bh) {
        Timestamp result = converter.convert(numericMillisecondsStringFixture);
        bh.consume(result);
    }
}

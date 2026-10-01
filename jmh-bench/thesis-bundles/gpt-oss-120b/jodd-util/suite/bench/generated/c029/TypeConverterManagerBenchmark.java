package bench.generated.c029;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.TypeConverterManager;
import jodd.typeconverter.TypeConverter;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Arrays;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TypeConverterManagerBenchmark {

    private TypeConverterManager manager;

    private String intString;
    private String boolString;
    private String doubleString;
    private String bigDecimalString;
    private String bigIntegerString;
    private String uuidString;
    private String uriString;
    private String urlString;
    private String enumString;
    private List<String> stringList;

    private enum TestEnum {
        ONE, TWO, THREE
    }

    @Setup
    public void setup() {
        manager = TypeConverterManager.get();

        intString = "12345";
        boolString = "true";
        doubleString = "123.456";
        bigDecimalString = "1234567890.123456789";
        bigIntegerString = "12345678901234567890";
        uuidString = "123e4567-e89b-12d3-a456-426614174000";
        uriString = "http://example.com/path?query=1";
        urlString = "http://example.org/";
        enumString = "ONE";
        stringList = Arrays.asList("1", "2", "3");
    }

    @Benchmark
    public Integer benchmarkStringToInteger() {
        return manager.convertType(intString, Integer.class);
    }

    @Benchmark
    public Boolean benchmarkStringToBoolean() {
        return manager.convertType(boolString, Boolean.class);
    }

    @Benchmark
    public Double benchmarkStringToDouble() {
        return manager.convertType(doubleString, Double.class);
    }

    @Benchmark
    public BigDecimal benchmarkStringToBigDecimal() {
        return manager.convertType(bigDecimalString, BigDecimal.class);
    }

    @Benchmark
    public BigInteger benchmarkStringToBigInteger() {
        return manager.convertType(bigIntegerString, BigInteger.class);
    }

    @Benchmark
    public UUID benchmarkStringToUUID() {
        return manager.convertType(uuidString, UUID.class);
    }

    @Benchmark
    public URI benchmarkStringToURI() {
        return manager.convertType(uriString, URI.class);
    }

    @Benchmark
    public URL benchmarkStringToURL() {
        return manager.convertType(urlString, URL.class);
    }

    @Benchmark
    public LocalDate benchmarkStringToLocalDate() {
        return manager.convertType("2023-01-01", LocalDate.class);
    }

    @Benchmark
    public LocalDateTime benchmarkStringToLocalDateTime() {
        return manager.convertType("2023-01-01T12:34:56", LocalDateTime.class);
    }

    @Benchmark
    public LocalTime benchmarkStringToLocalTime() {
        return manager.convertType("12:34:56", LocalTime.class);
    }

    @Benchmark
    public TestEnum benchmarkStringToEnum() {
        return manager.convertType(enumString, TestEnum.class);
    }

    @Benchmark
    public Collection<Integer> benchmarkConvertToCollection() {
        return manager.convertToCollection(stringList, ArrayList.class, Integer.class);
    }

    @Benchmark
    public TypeConverter<?> benchmarkLookupIntegerConverter() {
        return manager.lookup(Integer.class);
    }
}

package bench.generated.c029;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.TypeConverterManager;
import jodd.typeconverter.TypeConverter;
import java.util.List;
import java.util.Collection;
import java.util.UUID;
import java.math.BigDecimal;
import java.util.Arrays;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TypeConverterManagerBenchmark {

    private TypeConverterManager manager;

    // Inputs for conversion tests
    private String stringValue;
    private String arrayStringValue;
    private Object complexObjectValue;

    @Setup(Level.Trial)
    public void setup() {
        // Access the singleton instance
        manager = TypeConverterManager.get();

        // Setup representative inputs
        stringValue = "12345";
        arrayStringValue = "1,2,3,4,5";
        
        // Complex object input (e.g., a UUID object)
        complexObjectValue = UUID.randomUUID();
    }

    // --- Lookup Benchmarks ---

    @Benchmark
    public TypeConverter lookupIntegerConverter(Blackhole bh) {
        TypeConverter<Integer> converter = manager.lookup(Integer.class);
        bh.consume(converter);
        return converter;
    }

    @Benchmark
    public TypeConverter lookupStringConverter(Blackhole bh) {
        TypeConverter<String> converter = manager.lookup(String.class);
        bh.consume(converter);
        return converter;
    }

    // --- convertType Benchmarks ---

    @Benchmark
    public Integer convertStringToInt(Blackhole bh) {
        Integer result = manager.convertType(stringValue, Integer.class);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String convertIntToString(Blackhole bh) {
        Integer input = 12345;
        String result = manager.convertType(input, String.class);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public int[] convertStringToArrayInt(Blackhole bh) {
        // Assuming StringArrayConverter handles comma-separated values
        int[] result = manager.convertType(arrayStringValue, int[].class);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public BigDecimal convertStringToBigDecimal(Blackhole bh) {
        BigDecimal result = manager.convertType(stringValue, BigDecimal.class);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public UUID convertObjectToUuid(Blackhole bh) {
        UUID result = manager.convertType(complexObjectValue, UUID.class);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String convertObjectToString(Blackhole bh) {
        String result = manager.convertType(complexObjectValue, String.class);
        bh.consume(result);
        return result;
    }

    // --- convertToCollection Benchmarks ---

    @Benchmark
    public List<String> convertStringToList(Blackhole bh) {
        // Convert comma-separated string into a List of Strings
        List<String> result = manager.convertToCollection(arrayStringValue, List.class, String.class);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Collection<Integer> convertStringToCollectionInt(Blackhole bh) {
        // Convert comma-separated string into a Collection of Integers
        Collection<Integer> result = manager.convertToCollection(arrayStringValue, Collection.class, Integer.class);
        bh.consume(result);
        return result;
    }
}

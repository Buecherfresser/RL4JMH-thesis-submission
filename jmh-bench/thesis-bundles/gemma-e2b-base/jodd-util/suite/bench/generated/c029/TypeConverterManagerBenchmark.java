package bench.generated.c029;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import jodd.typeconverter.TypeConverterManager;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TypeConverterManagerBenchmark {

    private TypeConverterManager manager;

    // --- Setup Data ---
    private Integer inputInteger;
    private Double inputDouble;
    private Boolean inputBoolean;
    private String inputString;
    private UUID inputUUID;
    private java.time.LocalDate inputLocalDate;
    private List<Object> inputCollection;
    private String[] inputStringArray;

    @Setup
    public void setup() {
        manager = TypeConverterManager.get();

        // Primitive/Scalar inputs
        inputInteger = 123456789;
        inputDouble = 3.1415926535;
        inputBoolean = true;
        inputString = "test string value";
        inputUUID = UUID.randomUUID();
        inputLocalDate = java.time.LocalDate.of(2023, 10, 27);

        // Array inputs
        inputStringArray = new String[]{"a", "b", "c", "d", "e"};
        inputCollection = new ArrayList<>();
        inputCollection.add(inputInteger);
        inputCollection.add(inputDouble);
        inputCollection.add(inputBoolean);
    }

    // --- Benchmarks for convertType (Scalar Conversions) ---

    public void convertStringToInt(Blackhole bh) {
        Integer result = manager.convertType(inputString, Integer.class);
        bh.consume(result);
    }

    public void convertDoubleToBoolean(Blackhole bh) {
        Boolean result = manager.convertType(inputDouble, Boolean.class);
        bh.consume(result);
    }

    public void convertIntegerToString(Blackhole bh) {
        String result = manager.convertType(inputInteger, String.class);
        bh.consume(result);
    }

    public void convertUUIDToInteger(Blackhole bh) {
        // This tests conversion from a complex type (UUID) to a primitive (Integer)
        Integer result = manager.convertType(inputUUID, Integer.class);
        bh.consume(result);
    }

    // --- Benchmarks for convertType (Array Conversions) ---

    public void convertStringArrayToIntegerArray(Blackhole bh) {
        Integer[] result = manager.convertType(inputStringArray, Integer[].class);
        bh.consume(result);
    }

    public void convertDoubleArrayToDoubleArray(Blackhole bh) {
        Double[] result = manager.convertType(new Double[]{1.0, 2.0, 3.0}, Double[].class);
        bh.consume(result);
    }

    // --- Benchmarks for convertToCollection (Collection Conversions) ---

    public void convertObjectToArrayList(Blackhole bh) {
        // Convert a single Integer object into a List<Object>
        // Use Object.class as componentType to ensure the result is List<Object>
        List<Object> result = manager.convertToCollection(inputInteger, ArrayList.class, Object.class);
        bh.consume(result);
    }

    public void convertObjectToSet(Blackhole bh) {
        // Convert a single Double object into a Set<Object>
        // Use Object.class as componentType to ensure the result is List<Object>
        List<Object> result = manager.convertToCollection(inputDouble, ArrayList.class, Object.class);
        bh.consume(result);
    }
}

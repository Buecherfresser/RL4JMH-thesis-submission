package bench.generated.c033;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.TypeConverterManager;
import jodd.typeconverter.impl.BooleanArrayConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BooleanArrayConverterBenchmark {

    private BooleanArrayConverter converter;

    // Inputs for testing
    private int[] intArray;
    private byte[] byteArray;
    private String[] stringArray;
    private List<Integer> integerCollection;
    private List<Double> doubleIterable;
    private String csvString;
    private Integer singleValue;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize the converter
        TypeConverterManager manager = TypeConverterManager.get();
        converter = new BooleanArrayConverter(manager);

        // 1. Primitive Array (int[]) - Test conversion from primitive array
        intArray = new int[100];
        for (int i = 0; i < 100; i++) {
            intArray[i] = i % 2 == 0 ? 1 : 0;
        }

        // 2. Primitive Array (byte[]) - Test conversion from primitive array
        byteArray = new byte[100];
        for (int i = 0; i < 100; i++) {
            byteArray[i] = i % 2 == 0 ? (byte) 1 : (byte) 0;
        }

        // 3. Object Array (String[]) - Test conversion from object array
        stringArray = new String[10];
        for (int i = 0; i < 10; i++) {
            stringArray[i] = i % 2 == 0 ? "true" : "false";
        }

        // 4. Collection (List<Integer>) - Test conversion from Collection
        integerCollection = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            integerCollection.add(i % 2 == 0 ? 1 : 0);
        }

        // 5. Iterable (List<Double>) - Test conversion from Iterable
        doubleIterable = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            doubleIterable.add(i % 2 == 0 ? 1.0 : 0.0);
        }

        // 6. CharSequence (String) - Test conversion from String (CSV split)
        csvString = "true,false,1,0,true,false";

        // 7. Single Value (Integer) - Test conversion to single element array
        singleValue = 1;
    }

    @Benchmark
    public boolean[] convert_from_int_array(Blackhole bh) {
        boolean[] result = converter.convert(intArray);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public boolean[] convert_from_byte_array(Blackhole bh) {
        boolean[] result = converter.convert(byteArray);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public boolean[] convert_from_object_array(Blackhole bh) {
        boolean[] result = converter.convert(stringArray);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public boolean[] convert_from_collection(Blackhole bh) {
        boolean[] result = converter.convert(integerCollection);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public boolean[] convert_from_iterable(Blackhole bh) {
        boolean[] result = converter.convert(doubleIterable);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public boolean[] convert_from_string_csv(Blackhole bh) {
        boolean[] result = converter.convert(csvString);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public boolean[] convert_from_single_value(Blackhole bh) {
        boolean[] result = converter.convert(singleValue);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public boolean[] convert_from_null(Blackhole bh) {
        boolean[] result = converter.convert(null);
        bh.consume(result);
        return result;
    }
}

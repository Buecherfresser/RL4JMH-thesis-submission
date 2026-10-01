package bench.generated.c047;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.FloatArrayConverter;
import jodd.typeconverter.TypeConverterManager;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FloatArrayConverterBenchmark {

    private FloatArrayConverter converter;

    // Inputs for testing different conversion paths
    private List<Integer> integerCollection;
    private List<Double> doubleIterable;
    private String stringInput;
    private int[] intArray;
    private double[] doubleArray;
    private String[] objectArray;
    private Integer scalarInteger;
    private Double scalarDouble;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize the converter using the TypeConverterManager
        TypeConverterManager manager = TypeConverterManager.get();
        converter = new FloatArrayConverter(manager);

        // 1. Collection input (List<Integer>)
        integerCollection = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            integerCollection.add(i);
        }

        // 2. Iterable input (List<Double>)
        doubleIterable = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            doubleIterable.add(i * 1.5);
        }

        // 3. CharSequence input (String)
        // Using comma as delimiter, as defined in ArrayConverter.NUMBER_DELIMITERS (assumed)
        stringInput = "1.2,3.4,5.6,7.8,9.0";

        // 4. Primitive array input (int[])
        intArray = new int[100];
        for (int i = 0; i < 100; i++) {
            intArray[i] = i;
        }

        // 5. Primitive array input (double[])
        doubleArray = new double[100];
        for (int i = 0; i < 100; i++) {
            doubleArray[i] = i * 0.1;
        }

        // 6. Object array input (String[])
        objectArray = new String[100];
        for (int i = 0; i < 100; i++) {
            objectArray[i] = String.valueOf(i);
        }

        // 7. Scalar inputs
        scalarInteger = 42;
        scalarDouble = 3.14159;
    }

    @Benchmark
    public float[] convert_null(Blackhole bh) {
        float[] result = converter.convert(null);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public float[] convert_scalar_integer(Blackhole bh) {
        float[] result = converter.convert(scalarInteger);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public float[] convert_scalar_double(Blackhole bh) {
        float[] result = converter.convert(scalarDouble);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public float[] convert_collection_of_integers(Blackhole bh) {
        float[] result = converter.convert(integerCollection);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public float[] convert_iterable_of_doubles(Blackhole bh) {
        float[] result = converter.convert(doubleIterable);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public float[] convert_string_sequence(Blackhole bh) {
        float[] result = converter.convert(stringInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public float[] convert_object_array_of_strings(Blackhole bh) {
        float[] result = converter.convert(objectArray);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public float[] convert_primitive_array_int(Blackhole bh) {
        float[] result = converter.convert(intArray);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public float[] convert_primitive_array_double(Blackhole bh) {
        float[] result = converter.convert(doubleArray);
        bh.consume(result);
        return result;
    }
}

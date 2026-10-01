package bench.generated.c042;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.CollectionConverter;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Collection;
import java.util.Arrays;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CollectionConverterBenchmark {

    // Converters
    private CollectionConverter<Integer> listIntConverter;
    private CollectionConverter<Integer> setIntConverter;
    private CollectionConverter<Long> listLongConverter;
    private CollectionConverter<Double> listDoubleConverter;
    private CollectionConverter<String> listStringConverter;
    private CollectionConverter<String> setStringConverter;

    // Input data
    private int[] intArray;
    private long[] longArray;
    private double[] doubleArray;
    private String[] stringArray;
    private String csvString;
    private List<String> iterableInput;
    private List<Integer> sourceCollection;

    @Setup(Level.Trial)
    public void setup() {
        // Converters for different target collection types and component types
        listIntConverter = new CollectionConverter<>(List.class, Integer.class);
        setIntConverter = new CollectionConverter<>(Set.class, Integer.class);
        listLongConverter = new CollectionConverter<>(List.class, Long.class);
        listDoubleConverter = new CollectionConverter<>(List.class, Double.class);
        listStringConverter = new CollectionConverter<>(List.class, String.class);
        setStringConverter = new CollectionConverter<>(Set.class, String.class);

        // Primitive arrays
        intArray = new int[100];
        for (int i = 0; i < intArray.length; i++) {
            intArray[i] = i;
        }

        longArray = new long[100];
        for (int i = 0; i < longArray.length; i++) {
            longArray[i] = i * 10L;
        }

        doubleArray = new double[100];
        for (int i = 0; i < doubleArray.length; i++) {
            doubleArray[i] = i + 0.5;
        }

        // Object array
        stringArray = new String[100];
        for (int i = 0; i < stringArray.length; i++) {
            stringArray[i] = "str" + i;
        }

        // CSV string
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            if (i > 0) sb.append(',');
            sb.append(i);
        }
        csvString = sb.toString();

        // Iterable input (List of strings)
        iterableInput = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            iterableInput.add("val" + i);
        }

        // Source collection to be converted to another collection type
        sourceCollection = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            sourceCollection.add(i);
        }
    }

    @Benchmark
    public Collection<Integer> benchmarkConvertIntArray() {
        return listIntConverter.convert(intArray);
    }

    @Benchmark
    public Collection<Long> benchmarkConvertLongArray() {
        return listLongConverter.convert(longArray);
    }

    @Benchmark
    public Collection<Double> benchmarkConvertDoubleArray() {
        return listDoubleConverter.convert(doubleArray);
    }

    @Benchmark
    public Collection<String> benchmarkConvertStringArray() {
        return listStringConverter.convert(stringArray);
    }

    @Benchmark
    public Collection<String> benchmarkConvertCsvString() {
        return listStringConverter.convert(csvString);
    }

    @Benchmark
    public Collection<String> benchmarkConvertIterable() {
        return listStringConverter.convert(iterableInput);
    }

    @Benchmark
    public Collection<Integer> benchmarkConvertCollectionToList() {
        return listIntConverter.convert(sourceCollection);
    }

    @Benchmark
    public Collection<Integer> benchmarkConvertCollectionToSet() {
        return setIntConverter.convert(sourceCollection);
    }
}

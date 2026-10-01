package bench.generated.c044;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.TypeConverterManager;
import jodd.typeconverter.impl.DoubleArrayConverter;
import jodd.util.StringUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DoubleArrayConverterBenchmark {

    private DoubleArrayConverter converter;

    // Inputs for testing different conversion paths
    private List<Integer> collectionInput;
    private List<String> iterableInput;
    private String stringInput;
    private int[] primitiveArrayInput;
    private String[] objectArrayInput;
    private Integer singleValueInput;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize the converter with a TypeConverterManager instance
        TypeConverterManager manager = TypeConverterManager.get();
        converter = new DoubleArrayConverter(manager);

        // 1. Collection input (List<Integer>)
        collectionInput = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            collectionInput.add(i);
        }

        // 2. Iterable input (List<String> - simulating non-numeric strings that convert to double)
        iterableInput = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            iterableInput.add(String.valueOf(i * 1.5));
        }

        // 3. String input (CSV format)
        // Using 100 numbers separated by commas
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append(i).append(",");
        }
        stringInput = sb.substring(0, sb.length() - 1);

        // 4. Primitive array input (int[])
        primitiveArrayInput = new int[100];
        for (int i = 0; i < 100; i++) {
            primitiveArrayInput[i] = i;
        }

        // 5. Object array input (String[])
        objectArrayInput = new String[100];
        for (int i = 0; i < 100; i++) {
            objectArrayInput[i] = String.valueOf(i * 2.0);
        }

        // 6. Single scalar value
        singleValueInput = 42;
    }

    @Benchmark
    public double[] convertCollectionToDoubleArray(Blackhole bh) {
        double[] result = converter.convert(collectionInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public double[] convertIterableToStringsToDoubleArray(Blackhole bh) {
        double[] result = converter.convert(iterableInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public double[] convertStringCsvToDoubleArray(Blackhole bh) {
        double[] result = converter.convert(stringInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public double[] convertPrimitiveIntArrayToDoubleArray(Blackhole bh) {
        double[] result = converter.convert(primitiveArrayInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public double[] convertObjectStringArrayToDoubleArray(Blackhole bh) {
        double[] result = converter.convert(objectArrayInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public double[] convertSingleScalarValueToDoubleArray(Blackhole bh) {
        double[] result = converter.convert(singleValueInput);
        bh.consume(result);
        return result;
    }
}

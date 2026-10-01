package bench.generated.c030;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.TypeConverterManager;
import jodd.typeconverter.impl.ArrayConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ArrayConverterBenchmark {

    private ArrayConverter<String> converter;

    // Inputs for testing different conversion paths
    private List<String> stringCollection;
    private String stringInput;
    private String[] stringArrayInput;
    private int[] intPrimitiveArrayInput;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize the converter for target type String
        TypeConverterManager manager = TypeConverterManager.get();
        converter = new ArrayConverter<>(manager, String.class);

        // 1. Collection input
        stringCollection = new ArrayList<>();
        stringCollection.add("apple");
        stringCollection.add("banana");
        stringCollection.add("cherry");

        // 2. String input (CharSequence path)
        stringInput = "hello world";

        // 3. Array input (same type)
        stringArrayInput = new String[]{"one", "two", "three"};

        // 4. Primitive array input (int[] -> String[])
        intPrimitiveArrayInput = new int[]{1, 2, 3};
    }

    @Benchmark
    public String[] convert_null(Blackhole bh) {
        String[] result = converter.convert(null);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String[] convert_collection(Blackhole bh) {
        String[] result = converter.convert(stringCollection);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String[] convert_string(Blackhole bh) {
        // Tests CharSequence path, which uses CsvUtil.toStringArray
        String[] result = converter.convert(stringInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String[] convert_array_same_type(Blackhole bh) {
        // Tests array path where component type matches target type
        String[] result = converter.convert(stringArrayInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String[] convert_array_primitive_to_object(Blackhole bh) {
        // Tests primitive array conversion path (int[] -> String[])
        String[] result = converter.convert(intPrimitiveArrayInput);
        bh.consume(result);
        return result;
    }
}

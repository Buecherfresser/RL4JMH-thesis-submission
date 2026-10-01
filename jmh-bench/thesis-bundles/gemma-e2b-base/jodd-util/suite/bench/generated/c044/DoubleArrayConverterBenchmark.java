package bench.generated.c044;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jodd.typeconverter.TypeConverter;
import jodd.typeconverter.TypeConverterManager;
import jodd.util.StringUtil;
import jodd.typeconverter.impl.DoubleArrayConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DoubleArrayConverterBenchmark {

    private DoubleArrayConverter converter;
    private TypeConverterManager typeConverterManager;

    // Fixed input data for testing
    private double[] simpleDoubleArray;
    private List<Double> collectionList;
    private String[] stringArray;
    private byte[] primitiveByteArray;
    // Note: objectByteArray is unused in the benchmark methods, but kept for completeness if needed later.
    private byte[] objectByteArray; 

    @Setup
    public void setup() {
        // Initialize TypeConverterManager
        this.typeConverterManager = TypeConverterManager.get();
        this.converter = new DoubleArrayConverter(typeConverterManager);

        // 1. Simple double array input
        this.simpleDoubleArray = new double[]{1.0, 2.5, 3.0, 4.5, 5.0};

        // 2. Collection input (List<Double>)
        this.collectionList = new ArrayList<>();
        this.collectionList.add(1.1);
        this.collectionList.add(2.2);
        this.collectionList.add(3.3);
        this.collectionList.add(4.4);
        this.collectionList.add(5.5);

        // 3. CharSequence input (String array)
        this.stringArray = new String[]{"1.0", "2.5", "3.0", "4.5", "5.0"};

        // 4. Primitive array input (byte[])
        this.primitiveByteArray = new byte[]{1, 2, 3, 4, 5};

        // 5. Object array input (Integer[]) - used for testing object array conversion path
        Integer[] objectArray = {10, 20, 30, 40, 50};
        this.objectByteArray = new byte[objectArray.length * 2]; // Placeholder size
    }

    @Benchmark
    public void testConvertSimpleDoubleArray(Blackhole bh) {
        double[] result = converter.convert(simpleDoubleArray);
        bh.consume(result);
    }

    @Benchmark
    public void testConvertCollection(Blackhole bh) {
        // Input is a Collection (List<Double>)
        Object input = collectionList;
        double[] result = converter.convert(input);
        bh.consume(result);
    }

    @Benchmark
    public void testConvertStringArray(Blackhole bh) {
        // Input is a CharSequence (String array)
        Object input = stringArray;
        double[] result = converter.convert(input);
        bh.consume(result);
    }

    @Benchmark
    public void testConvertPrimitiveByteArray(Blackhole bh) {
        // Input is a primitive array (byte[])
        Object input = primitiveByteArray;
        double[] result = converter.convert(input);
        bh.consume(result);
    }

    @Benchmark
    public void testConvertObjectArray(Blackhole bh) {
        // Input is an Object array (Integer[])
        Integer[] input = new Integer[]{10, 20, 30, 40, 50};
        double[] result = converter.convert(input);
        bh.consume(result);
    }
}

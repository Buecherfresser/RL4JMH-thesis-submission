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
    private Object[] inputObjectArray;
    private List<Double> inputCollection;
    private String inputString;
    private double[] inputDoubleArray;

    // Helper for creating a TypeConverterManager instance
    @Setup
    public void setup() {
        // Initialize TypeConverterManager
        this.typeConverterManager = TypeConverterManager.get();
        this.converter = new DoubleArrayConverter(typeConverterManager);

        // 1. Input for array conversion (Object array -> double[])
        inputObjectArray = new Object[]{1.0, 2.5, 3.0, 4.5, 5.0};

        // 2. Input for Collection conversion (List<Double> -> double[])
        inputCollection = new ArrayList<>();
        inputCollection.add(1.1);
        inputCollection.add(2.2);
        inputCollection.add(3.3);
        inputCollection.add(4.4);

        // 3. Input for CharSequence conversion (String -> double[])
        inputString = "1.0,2.5,3.0,4.5";

        // 4. Input for primitive array conversion (double[])
        inputDoubleArray = new double[]{10.0, 20.0, 30.0};
    }

    @Benchmark
    public void convertObjectArray(Blackhole bh) {
        double[] result = converter.convert(inputObjectArray);
        bh.consume(result);
    }

    @Benchmark
    public void convertCollection(Blackhole bh) {
        double[] result = converter.convert(inputCollection);
        bh.consume(result);
    }

    @Benchmark
    public void convertCharSequence(Blackhole bh) {
        double[] result = converter.convert(inputString);
        bh.consume(result);
    }

    @Benchmark
    public void convertPrimitiveArray(Blackhole bh) {
        double[] result = converter.convert(inputDoubleArray);
        bh.consume(result);
    }

    @Benchmark
    public void convertNull(Blackhole bh) {
        double[] result = converter.convert(null);
        bh.consume(result);
    }
}

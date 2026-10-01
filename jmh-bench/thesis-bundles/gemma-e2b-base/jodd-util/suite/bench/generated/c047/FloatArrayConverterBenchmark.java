package bench.generated.c047;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jodd.typeconverter.TypeConverter;
import jodd.typeconverter.TypeConverterManager;
import jodd.util.StringUtil;
import jodd.typeconverter.impl.FloatArrayConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class FloatArrayConverterBenchmark {

    private FloatArrayConverter converter;
    private TypeConverterManager typeConverterManager;

    // Fixed input data for testing
    private float[] floatArrayInput;
    private int[] intArrayInput;
    private double[] doubleArrayInput;
    private Collection<Float> collectionInput;
    private String stringArrayInput;

    // Setup method to initialize the converter and inputs
    @Setup
    public void setup() {
        // Initialize TypeConverterManager
        this.typeConverterManager = TypeConverterManager.get();
        this.converter = new FloatArrayConverter(this.typeConverterManager);

        // Setup fixed float array input
        floatArrayInput = new float[]{1.1f, 2.2f, 3.3f, 4.4f, 5.5f};

        // Setup fixed int array input (to test primitive array conversion)
        intArrayInput = new int[]{1, 2, 3, 4, 5};

        // Setup fixed double array input
        doubleArrayInput = new double[]{1.1, 2.2, 3.3, 4.4, 5.5};

        // Setup collection input (to test Collection conversion path)
        collectionInput = new ArrayList<>();
        collectionInput.add(1.0f);
        collectionInput.add(2.0f);
        collectionInput.add(3.0f);
        collectionInput.add(4.0f);
        collectionInput.add(5.0f);

        // Setup string array input (to test CharSequence conversion path)
        stringArrayInput = "1.0,2.0,3.0,4.0,5.0";
    }

    // Benchmark 1: Converting a standard float array (Array -> Array path)
    public void convertFloatArray(Blackhole bh) {
        float[] result = converter.convert(floatArrayInput);
        bh.consume(result);
    }

    // Benchmark 2: Converting an integer array (Primitive Array -> Float Array path)
    public void convertIntArray(Blackhole bh) {
        float[] result = converter.convert(intArrayInput);
        bh.consume(result);
    }

    // Benchmark 3: Converting a double array (Object Array -> Float Array path)
    public void convertDoubleArray(Blackhole bh) {
        float[] result = converter.convert(doubleArrayInput);
        bh.consume(result);
    }

    // Benchmark 4: Converting a Collection of Floats (Collection -> Array path)
    public void convertCollection(Blackhole bh) {
        float[] result = converter.convert(collectionInput);
        bh.consume(result);
    }

    // Benchmark 5: Converting a String (CharSequence -> Array path, testing StringUtil.splitc)
    public void convertString(Blackhole bh) {
        float[] result = converter.convert(stringArrayInput);
        bh.consume(result);
    }
}

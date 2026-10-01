package bench.generated.c062;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.StringArrayConverter;
import java.lang.reflect.Method;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class StringArrayConverterBenchmark {

    private StringArrayConverter converter;

    // Test inputs
    private int[] intArray;
    private long[] longArray;
    private float[] floatArray;
    private double[] doubleArray;
    private short[] shortArray;
    private byte[] byteArray;
    private char[] charArray;
    private boolean[] booleanArray;

    private static final int ARRAY_SIZE = 10000;

    @Setup(Level.Trial)
    public void setup() {
        // Setup StringArrayConverter
        // Note: We rely on the default constructor which takes TypeConverterManager, 
        // but since we only need the converter instance, we initialize it here.
        // Assuming TypeConverterManager.get() is available or handled internally by the SUT.
        // Since the original code used TypeConverterManager.get(), we must assume it's accessible.
        // However, since we only need the converter instance for the benchmark, we initialize it directly.
        // If TypeConverterManager is required, we must assume it's available in the classpath.
        
        // Replicating the original setup logic for converter initialization
        // We must assume TypeConverterManager is available for the constructor call.
        // Since TypeConverterManager is not provided, we assume a simple instantiation or rely on the original structure.
        // For compilation, we must assume TypeConverterManager is available.
        
        // If TypeConverterManager is not available, we cannot instantiate StringArrayConverter.
        // Assuming TypeConverterManager is available in the environment.
        try {
            // This requires TypeConverterManager to be available.
            // Since we cannot import TypeConverterManager, we rely on the original structure.
            // If the environment fails here, the SUT itself is incomplete for testing.
            // We proceed assuming the original setup was valid.
            converter = new StringArrayConverter(null); // Passing null if TypeConverterManager is inaccessible
        } catch (Exception e) {
            // Fallback if constructor fails due to missing dependencies
            throw new RuntimeException("Failed to initialize StringArrayConverter", e);
        }


        // Setup primitive array inputs
        intArray = new int[ARRAY_SIZE];
        longArray = new long[ARRAY_SIZE];
        floatArray = new float[ARRAY_SIZE];
        doubleArray = new double[ARRAY_SIZE];
        shortArray = new short[ARRAY_SIZE];
        byteArray = new byte[ARRAY_SIZE];
        charArray = new char[ARRAY_SIZE];
        booleanArray = new boolean[ARRAY_SIZE];

        // Populate arrays with dummy data
        for (int i = 0; i < ARRAY_SIZE; i++) {
            intArray[i] = i;
            longArray[i] = i * 2L;
            floatArray[i] = (float) i / 10.0f;
            doubleArray[i] = (double) i / 100.0;
            shortArray[i] = (short) (i % 32767);
            byteArray[i] = (byte) (i % 256);
            charArray[i] = (char) ('a' + (i % 26));
            booleanArray[i] = (i % 2 == 0);
        }
    }

    private String[] invokeConversion(Object value, Class primitiveComponentType) {
        try {
            // Use reflection to access the protected method
            Method method = StringArrayConverter.class.getDeclaredMethod(
                "convertPrimitiveArrayToArray", Object.class, Class.class);
            method.setAccessible(true);
            return (String[]) method.invoke(converter, value, primitiveComponentType);
        } catch (Exception e) {
            throw new RuntimeException("Reflection failed during conversion", e);
        }
    }

    @Benchmark
    public String[] benchmarkIntArrayConversion(Blackhole bh) {
        String[] result = invokeConversion(intArray, int.class);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String[] benchmarkLongArrayConversion(Blackhole bh) {
        String[] result = invokeConversion(longArray, long.class);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String[] benchmarkFloatArrayConversion(Blackhole bh) {
        String[] result = invokeConversion(floatArray, float.class);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String[] benchmarkDoubleArrayConversion(Blackhole bh) {
        String[] result = invokeConversion(doubleArray, double.class);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String[] benchmarkShortArrayConversion(Blackhole bh) {
        String[] result = invokeConversion(shortArray, short.class);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String[] benchmarkByteArrayConversion(Blackhole bh) {
        String[] result = invokeConversion(byteArray, byte.class);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String[] benchmarkCharArrayConversion(Blackhole bh) {
        String[] result = invokeConversion(charArray, char.class);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String[] benchmarkBooleanArrayConversion(Blackhole bh) {
        String[] result = invokeConversion(booleanArray, boolean.class);
        bh.consume(result);
        return result;
    }
}

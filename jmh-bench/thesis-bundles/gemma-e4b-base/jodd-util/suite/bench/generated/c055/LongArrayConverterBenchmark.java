package bench.generated.c055;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Arrays;
import jodd.typeconverter.impl.LongArrayConverter;
import jodd.typeconverter.TypeConverterManager;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LongArrayConverterBenchmark {

    private LongArrayConverter converter;
    private TypeConverterManager typeConverterManager;

    // Inputs for non-array conversions
    private Collection<Object> collectionInput;
    private List<Object> iterableInput;
    private String stringInput;
    private Object scalarInput;

    // Inputs for array conversions
    private int[] primitiveIntArrayInput;
    private Integer[] objectIntegerArrayInput;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize the subject and its dependencies
        typeConverterManager = new TypeConverterManager();
        converter = new LongArrayConverter(typeConverterManager);

        // --- Setup Collection Input (Collection path) ---
        collectionInput = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            collectionInput.add(i);
        }

        // --- Setup Iterable Input (Iterable path) ---
        iterableInput = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            iterableInput.add((double) i);
        }

        // --- Setup String Input (CharSequence path) ---
        // Using a string that contains delimiters (assuming ArrayConverter.NUMBER_DELIMITERS is comma)
        stringInput = "1,2,3,4,5,6,7,8,9,10";

        // --- Setup Scalar Input (Single element path) ---
        scalarInput = 42;

        // --- Setup Primitive Array Input (Primitive array path) ---
        primitiveIntArrayInput = new int[100];
        for (int i = 0; i < 100; i++) {
            primitiveIntArrayInput[i] = i;
        }

        // --- Setup Object Array Input (Object array path) ---
        objectIntegerArrayInput = new Integer[100];
        for (int i = 0; i < 100; i++) {
            objectIntegerArrayInput[i] = i;
        }
    }

    @Benchmark
    public long[] convertCollection(Blackhole bh) {
        long[] result = converter.convert(collectionInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long[] convertIterable(Blackhole bh) {
        // We must pass an Iterable, so we use the list implementation
        long[] result = converter.convert(iterableInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long[] convertString(Blackhole bh) {
        long[] result = converter.convert(stringInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long[] convertPrimitiveIntArray(Blackhole bh) {
        long[] result = converter.convert(primitiveIntArrayInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long[] convertObjectIntegerArray(Blackhole bh) {
        long[] result = converter.convert(objectIntegerArrayInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long[] convertScalar(Blackhole bh) {
        long[] result = converter.convert(scalarInput);
        bh.consume(result);
        return result;
    }
}

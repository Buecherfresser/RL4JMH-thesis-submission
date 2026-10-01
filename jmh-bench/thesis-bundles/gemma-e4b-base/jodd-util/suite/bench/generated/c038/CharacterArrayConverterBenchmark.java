package bench.generated.c038;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.CharacterArrayConverter;
import jodd.typeconverter.TypeConverterManager;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharacterArrayConverterBenchmark {

    private CharacterArrayConverter converter;

    // Inputs for testing different conversion paths
    private String inputString;
    private List<Character> inputCollectionChars;
    private List<Integer> inputIterableIntegers;
    private int[] inputPrimitiveIntArray;
    private String[] inputObjectArrayStrings;
    private Object inputSingleElement;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize the converter using the TypeConverterManager
        TypeConverterManager manager = TypeConverterManager.get();
        converter = new CharacterArrayConverter(manager);

        // 1. String input (CharSequence path)
        inputString = "Hello World!";

        // 2. Collection input (Collection path)
        inputCollectionChars = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            inputCollectionChars.add((char) ('a' + (i % 26)));
        }

        // 3. Iterable input (Iterable path, requires internal type conversion)
        inputIterableIntegers = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            // Assuming TypeConverterManager converts Integer to char based on value
            inputIterableIntegers.add(i % 26);
        }

        // 4. Primitive array input (int[] path)
        inputPrimitiveIntArray = new int[100];
        for (int i = 0; i < 100; i++) {
            inputPrimitiveIntArray[i] = i % 26;
        }

        // 5. Object array input (String[] path)
        inputObjectArrayStrings = new String[100];
        for (int i = 0; i < 100; i++) {
            inputObjectArrayStrings[i] = "char" + i;
        }

        // 6. Single element input (Fallback path)
        inputSingleElement = 42;
    }

    @Benchmark
    public char[] convert_String(Blackhole bh) {
        char[] result = converter.convert(inputString);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public char[] convert_Collection(Blackhole bh) {
        char[] result = converter.convert(inputCollectionChars);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public char[] convert_Iterable(Blackhole bh) {
        char[] result = converter.convert(inputIterableIntegers);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public char[] convert_PrimitiveArray_int(Blackhole bh) {
        char[] result = converter.convert(inputPrimitiveIntArray);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public char[] convert_ObjectArray_String(Blackhole bh) {
        char[] result = converter.convert(inputObjectArrayStrings);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public char[] convert_SingleElement(Blackhole bh) {
        char[] result = converter.convert(inputSingleElement);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public char[] convert_Null(Blackhole bh) {
        char[] result = converter.convert(null);
        bh.consume(result);
        return result;
    }
}

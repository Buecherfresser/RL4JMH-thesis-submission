package bench.generated.c028;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.Converter;
import java.math.BigDecimal;
import java.math.BigInteger;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ConverterBenchmark {

    private Converter converter;

    // Inputs for various conversions
    private Object stringIntInput;
    private Object stringDoubleInput;
    private Object stringBooleanInput;
    private Object stringArrayInput;
    private Object classInput;
    private Object bigIntInput;
    private Object bigDecimalInput;
    private Object intArrayInput;
    private Object charArrayInput;

    @Setup(Level.Trial)
    public void setup() {
        converter = Converter.get();

        // 1. String to Integer
        stringIntInput = "12345";

        // 2. String to Double
        stringDoubleInput = "3.1415926535";

        // 3. String to Boolean
        stringBooleanInput = "true";

        // 4. String Array to String Array
        stringArrayInput = new String[]{"apple", "banana", "cherry"};

        // 5. Class conversion
        classInput = String.class;

        // 6. BigInteger conversion (String representation)
        bigIntInput = "9876543210987654321";

        // 7. BigDecimal conversion (String representation)
        bigDecimalInput = "1234567890.123456789";

        // 8. Integer Array input (for conversion to other types)
        intArrayInput = new int[]{1, 2, 3, 4, 5};

        // 9. Character Array input
        charArrayInput = new char[]{'H', 'e', 'l', 'l', 'o'};
    }

    // --- Primitive/Wrapper Conversions ---

    @Benchmark
    public void testToInteger(Blackhole bh) {
        Integer result = converter.toInteger(stringIntInput);
        bh.consume(result);
    }

    @Benchmark
    public void testToIntValue(Blackhole bh) {
        int result = converter.toIntValue(stringIntInput);
        bh.consume(result);
    }

    @Benchmark
    public void testToLong(Blackhole bh) {
        Long result = converter.toLong(stringIntInput);
        bh.consume(result);
    }

    @Benchmark
    public void testToDouble(Blackhole bh) {
        Double result = converter.toDouble(stringDoubleInput);
        bh.consume(result);
    }

    @Benchmark
    public void testToFloat(Blackhole bh) {
        Float result = converter.toFloat(stringDoubleInput);
        bh.consume(result);
    }

    @Benchmark
    public void testToBoolean(Blackhole bh) {
        Boolean result = converter.toBoolean(stringBooleanInput);
        bh.consume(result);
    }

    @Benchmark
    public void testToBooleanValue(Blackhole bh) {
        boolean result = converter.toBooleanValue(stringBooleanInput);
        bh.consume(result);
    }

    @Benchmark
    public void testToShort(Blackhole bh) {
        Short result = converter.toShort(stringIntInput);
        bh.consume(result);
    }

    @Benchmark
    public void testToCharacter(Blackhole bh) {
        Character result = converter.toCharacter(stringIntInput);
        bh.consume(result);
    }

    @Benchmark
    public void testToByte(Blackhole bh) {
        Byte result = converter.toByte(stringIntInput);
        bh.consume(result);
    }

    // --- String Conversions ---

    @Benchmark
    public void testToString(Blackhole bh) {
        String result = converter.toString(stringIntInput);
        bh.consume(result);
    }

    @Benchmark
    public void testToStringArray(Blackhole bh) {
        String[] result = converter.toStringArray(stringArrayInput);
        bh.consume(result);
    }

    // --- Array Conversions ---

    @Benchmark
    public void testToIntegerArray(Blackhole bh) {
        int[] result = converter.toIntegerArray(intArrayInput);
        bh.consume(result);
    }

    @Benchmark
    public void testToDoubleArray(Blackhole bh) {
        double[] result = converter.toDoubleArray(intArrayInput);
        bh.consume(result);
    }

    @Benchmark
    public void testToShortArray(Blackhole bh) {
        short[] result = converter.toShortArray(intArrayInput);
        bh.consume(result);
    }

    @Benchmark
    public void testToCharacterArray(Blackhole bh) {
        char[] result = converter.toCharacterArray(charArrayInput);
        bh.consume(result);
    }

    // --- Big Number Conversions ---

    @Benchmark
    public void testToBigInteger(Blackhole bh) {
        BigInteger result = converter.toBigInteger(bigIntInput);
        bh.consume(result);
    }

    @Benchmark
    public void testToBigDecimal(Blackhole bh) {
        BigDecimal result = converter.toBigDecimal(bigDecimalInput);
        bh.consume(result);
    }

    // --- Class Conversions ---

    @Benchmark
    public void testToClass(Blackhole bh) {
        Class<?> result = converter.toClass(classInput);
        bh.consume(result);
    }

    // Note: toClassArray requires an array input, which is not easily prepared in setup
    // without creating a new array type for every benchmark. We skip it for simplicity
    // and focus on the core single-value conversions.
}

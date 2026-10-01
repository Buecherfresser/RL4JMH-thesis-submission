package bench.generated.c028;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Level;
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

    // inputs
    private Object boolInput;
    private Object intInput;
    private Object longInput;
    private Object floatInput;
    private Object doubleInput;
    private Object shortInput;
    private Object charInput;
    private Object byteInput;

    private Object booleanArrayInput;
    private Object intArrayInput;
    private Object longArrayInput;
    private Object floatArrayInput;
    private Object doubleArrayInput;
    private Object shortArrayInput;
    private Object charArrayInput;

    private Object stringInput;
    private Object classInput;
    private Object bigIntegerInput;
    private Object bigDecimalInput;

    @Setup(Level.Trial)
    public void setup() {
        converter = Converter.get();

        boolInput = "true";
        intInput = "12345";
        longInput = "1234567890123";
        floatInput = "12.34";
        doubleInput = "12.34";
        shortInput = "123";
        charInput = "A";
        byteInput = "127";

        booleanArrayInput = "true,false,true";
        intArrayInput = "1,2,3,4,5";
        longArrayInput = "10,20,30,40,50";
        floatArrayInput = "1.1,2.2,3.3";
        doubleArrayInput = "4.4,5.5,6.6";
        shortArrayInput = "7,8,9";
        charArrayInput = "a,b,c";

        stringInput = 42; // Integer will be converted to String
        classInput = "java.lang.String";

        bigIntegerInput = "123456789012345678901234567890";
        bigDecimalInput = "12345.6789";
    }

    // boolean
    @Benchmark
    public Boolean benchmarkToBoolean() {
        return converter.toBoolean(boolInput);
    }

    @Benchmark
    public Boolean benchmarkToBooleanWithDefault() {
        return converter.toBoolean(boolInput, Boolean.FALSE);
    }

    @Benchmark
    public boolean benchmarkToBooleanValue() {
        return converter.toBooleanValue(boolInput);
    }

    @Benchmark
    public boolean benchmarkToBooleanValueWithDefault() {
        return converter.toBooleanValue(boolInput, true);
    }

    // integer
    @Benchmark
    public Integer benchmarkToInteger() {
        return converter.toInteger(intInput);
    }

    @Benchmark
    public Integer benchmarkToIntegerWithDefault() {
        return converter.toInteger(intInput, -1);
    }

    @Benchmark
    public int benchmarkToIntValue() {
        return converter.toIntValue(intInput);
    }

    @Benchmark
    public int benchmarkToIntValueWithDefault() {
        return converter.toIntValue(intInput, -1);
    }

    // long
    @Benchmark
    public Long benchmarkToLong() {
        return converter.toLong(longInput);
    }

    @Benchmark
    public Long benchmarkToLongWithDefault() {
        return converter.toLong(longInput, -1L);
    }

    @Benchmark
    public long benchmarkToLongValue() {
        return converter.toLongValue(longInput);
    }

    @Benchmark
    public long benchmarkToLongValueWithDefault() {
        return converter.toLongValue(longInput, -1L);
    }

    // float
    @Benchmark
    public Float benchmarkToFloat() {
        return converter.toFloat(floatInput);
    }

    @Benchmark
    public Float benchmarkToFloatWithDefault() {
        return converter.toFloat(floatInput, -1.0f);
    }

    @Benchmark
    public float benchmarkToFloatValue() {
        return converter.toFloatValue(floatInput);
    }

    @Benchmark
    public float benchmarkToFloatValueWithDefault() {
        return converter.toFloatValue(floatInput, -1.0f);
    }

    // double
    @Benchmark
    public Double benchmarkToDouble() {
        return converter.toDouble(doubleInput);
    }

    @Benchmark
    public Double benchmarkToDoubleWithDefault() {
        return converter.toDouble(doubleInput, -1.0);
    }

    @Benchmark
    public double benchmarkToDoubleValue() {
        return converter.toDoubleValue(doubleInput);
    }

    @Benchmark
    public double benchmarkToDoubleValueWithDefault() {
        return converter.toDoubleValue(doubleInput, -1.0);
    }

    // short
    @Benchmark
    public Short benchmarkToShort() {
        return converter.toShort(shortInput);
    }

    @Benchmark
    public Short benchmarkToShortWithDefault() {
        return converter.toShort(shortInput, (short) -1);
    }

    @Benchmark
    public short benchmarkToShortValue() {
        return converter.toShortValue(shortInput);
    }

    @Benchmark
    public short benchmarkToShortValueWithDefault() {
        return converter.toShortValue(shortInput, (short) -1);
    }

    // char
    @Benchmark
    public Character benchmarkToCharacter() {
        return converter.toCharacter(charInput);
    }

    @Benchmark
    public Character benchmarkToCharacterWithDefault() {
        return converter.toCharacter(charInput, 'Z');
    }

    @Benchmark
    public char benchmarkToCharValue() {
        return converter.toCharValue(charInput);
    }

    @Benchmark
    public char benchmarkToCharValueWithDefault() {
        return converter.toCharValue(charInput, 'Z');
    }

    // byte
    @Benchmark
    public Byte benchmarkToByte() {
        return converter.toByte(byteInput);
    }

    @Benchmark
    public Byte benchmarkToByteWithDefault() {
        return converter.toByte(byteInput, (byte) -1);
    }

    @Benchmark
    public byte benchmarkToByteValue() {
        return converter.toByteValue(byteInput);
    }

    @Benchmark
    public byte benchmarkToByteValueWithDefault() {
        return converter.toByteValue(byteInput, (byte) -1);
    }

    // boolean array
    @Benchmark
    public boolean[] benchmarkToBooleanArray() {
        return converter.toBooleanArray(booleanArrayInput);
    }

    // int array
    @Benchmark
    public int[] benchmarkToIntegerArray() {
        return converter.toIntegerArray(intArrayInput);
    }

    // long array
    @Benchmark
    public long[] benchmarkToLongArray() {
        return converter.toLongArray(longArrayInput);
    }

    // float array
    @Benchmark
    public float[] benchmarkToFloatArray() {
        return converter.toFloatArray(floatArrayInput);
    }

    // double array
    @Benchmark
    public double[] benchmarkToDoubleArray() {
        return converter.toDoubleArray(doubleArrayInput);
    }

    // short array
    @Benchmark
    public short[] benchmarkToShortArray() {
        return converter.toShortArray(shortArrayInput);
    }

    // char array
    @Benchmark
    public char[] benchmarkToCharacterArray() {
        return converter.toCharacterArray(charArrayInput);
    }

    // string
    @Benchmark
    public String benchmarkToString() {
        return converter.toString(stringInput);
    }

    @Benchmark
    public String benchmarkToStringWithDefault() {
        return converter.toString(stringInput, "default");
    }

    // class
    @Benchmark
    public Class<?> benchmarkToClass() {
        return converter.toClass(classInput);
    }

    // big integer
    @Benchmark
    public BigInteger benchmarkToBigInteger() {
        return converter.toBigInteger(bigIntegerInput);
    }

    @Benchmark
    public BigInteger benchmarkToBigIntegerWithDefault() {
        return converter.toBigInteger(bigIntegerInput, BigInteger.ZERO);
    }

    // big decimal
    @Benchmark
    public BigDecimal benchmarkToBigDecimal() {
        return converter.toBigDecimal(bigDecimalInput);
    }

    @Benchmark
    public BigDecimal benchmarkToBigDecimalWithDefault() {
        return converter.toBigDecimal(bigDecimalInput, BigDecimal.ZERO);
    }
}

package bench.generated.c048;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import jodd.typeconverter.TypeConverter;
import jodd.typeconverter.impl.FloatConverter;
import jodd.typeconverter.TypeConversionException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 2)
@Measurement(iterations = 5, time = 2)
public class FloatConverterBenchmark {

    private FloatConverter converter;

    // Inputs for testing different branches of the convert method
    private Float floatValue;
    private Integer integerValue;
    private Double doubleValue;
    private Boolean booleanValue;
    private String validString;
    private String plusString;
    private String invalidString;
    private Object nullValue;

    @Setup
    public void setup() {
        converter = new FloatConverter();

        // Branch 1: Float input
        floatValue = 3.14159f;

        // Branch 2: Number input (Integer)
        integerValue = 100;

        // Branch 3: Number input (Double)
        doubleValue = 123.456;

        // Branch 4: Boolean input
        booleanValue = true;

        // Branch 5: String input (standard)
        validString = "123.45";

        // Branch 6: String input (starts with +)
        plusString = "+45.67";

        // Branch 7: Invalid String input (to test exception path)
        invalidString = "not_a_float";

        // Branch 8: Null input
        nullValue = null;
    }

    @Benchmark
    public void testFloatInput(Blackhole bh) {
        Float result = converter.convert(floatValue);
        bh.consume(result);
    }

    @Benchmark
    public void testIntegerInput(Blackhole bh) {
        Float result = converter.convert(integerValue);
        bh.consume(result);
    }

    @Benchmark
    public void testDoubleInput(Blackhole bh) {
        Float result = converter.convert(doubleValue);
        bh.consume(result);
    }

    @Benchmark
    public void testBooleanInput(Blackhole bh) {
        Float result = converter.convert(booleanValue);
        bh.consume(result);
    }

    @Benchmark
    public void testValidStringInput(Blackhole bh) {
        Float result = converter.convert(validString);
        bh.consume(result);
    }

    @Benchmark
    public void testPlusStringInput(Blackhole bh) {
        Float result = converter.convert(plusString);
        bh.consume(result);
    }

    @Benchmark
    public void testNullInput(Blackhole bh) {
        Float result = converter.convert(nullValue);
        bh.consume(result);
    }

    @Benchmark
    public void testInvalidStringInput(Blackhole bh) {
        // This test measures the path that throws TypeConversionException
        try {
            converter.convert(invalidString);
        } catch (TypeConversionException e) {
            // Expected path, consume nothing if exception is thrown
        }
        bh.consume(null); // Consume null as the return value is not guaranteed
    }
}

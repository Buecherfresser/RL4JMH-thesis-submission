package bench.generated.c028;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.util.concurrent.Callable;

import jodd.typeconverter.Converter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ConverterBenchmark {

    // Since Converter is a static facade, we don't need instance state,
    // but we can keep a reference if we were benchmarking an instance method.
    // For static methods, direct calls are fine.

    @Benchmark
    public void testToBoolean(Blackhole bh) {
        // Test conversion with null input
        Boolean result = Converter.get().toBoolean(null);
        bh.consume(result);

        // Test conversion with a simple value (e.g., true)
        Boolean result2 = Converter.get().toBoolean(true);
        bh.consume(result2);
    }

    @Benchmark
    public void testToBooleanValue(Blackhole bh) {
        // Test conversion with null input (should use default false)
        boolean result1 = Converter.get().toBooleanValue(null);
        bh.consume(result1);

        // Test conversion with a value that converts to true
        boolean result2 = Converter.get().toBooleanValue(true);
        bh.consume(result2);
    }

    @Benchmark
    public void testToInteger(Blackhole bh) {
        // Test conversion with null input (should use default 0)
        Integer result1 = Converter.get().toInteger(null);
        bh.consume(result1);

        // Test conversion with a simple value (e.g., 10)
        Integer result2 = Converter.get().toInteger(10);
        bh.consume(result2);
    }

    @Benchmark
    public void testToIntValue(Blackhole bh) {
        // Test conversion with null input (should use default 0)
        int result1 = Converter.get().toIntValue(null);
        bh.consume(result1);

        // Test conversion with a simple value (e.g., 10)
        int result2 = Converter.get().toIntValue(10);
        bh.consume(result2);
    }

    @Benchmark
    public void testToLong(Blackhole bh) {
        // Test conversion with null input
        Long result1 = Converter.get().toLong(null);
        bh.consume(result1);

        // Test conversion with a simple value
        Long result2 = Converter.get().toLong(12345L);
        bh.consume(result2);
    }

    @Benchmark
    public void testToDouble(Blackhole bh) {
        // Test conversion with null input
        Double result1 = Converter.get().toDouble(null);
        bh.consume(result1);

        // Test conversion with a simple value
        Double result2 = Converter.get().toDouble(3.14159);
        bh.consume(result2);
    }

    @Benchmark
    public void testToBigDecimal(Blackhole bh) {
        // Test conversion with null input
        BigDecimal result1 = Converter.get().toBigDecimal(null);
        bh.consume(result1);

        // Test conversion with a simple value
        BigDecimal result2 = Converter.get().toBigDecimal(new BigDecimal("123.45"));
        bh.consume(result2);
    }

    @Benchmark
    public void testToString(Blackhole bh) {
        // Test conversion of null
        String result1 = Converter.get().toString(null);
        bh.consume(result1);

        // Test conversion of a simple integer
        String result2 = Converter.get().toString(123);
        bh.consume(result2);
    }
}

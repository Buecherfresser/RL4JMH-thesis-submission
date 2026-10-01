package bench.generated.c031;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.BigDecimalConverter;
import java.math.BigDecimal;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BigDecimalConverterBenchmark {

    private BigDecimalConverter converter;

    // Inputs for testing different conversion paths
    private Object validBigDecimalInput;
    private Object validStringInput;
    private Object validIntegerInput;
    private Object validDoubleInput;
    private Object invalidStringInput;

    @Setup(Level.Trial)
    public void setup() {
        converter = new BigDecimalConverter();

        // 1. BigDecimal input (direct pass-through)
        validBigDecimalInput = new BigDecimal("12345.6789");

        // 2. String input (valid number)
        validStringInput = "98765.4321";

        // 3. Integer input (should convert via String)
        validIntegerInput = 12345;

        // 4. Double input (should convert via String)
        validDoubleInput = 54321.9876;

        // 5. Invalid String input (to test exception path)
        invalidStringInput = "not_a_number";
    }

    @Benchmark
    public void convert_BigDecimal_input(Blackhole bh) {
        BigDecimal result = converter.convert(validBigDecimalInput);
        bh.consume(result);
    }

    @Benchmark
    public void convert_String_input(Blackhole bh) {
        BigDecimal result = converter.convert(validStringInput);
        bh.consume(result);
    }

    @Benchmark
    public void convert_Integer_input(Blackhole bh) {
        BigDecimal result = converter.convert(validIntegerInput);
        bh.consume(result);
    }

    @Benchmark
    public void convert_Double_input(Blackhole bh) {
        BigDecimal result = converter.convert(validDoubleInput);
        bh.consume(result);
    }

    @Benchmark
    public void convert_Null_input(Blackhole bh) {
        BigDecimal result = converter.convert(null);
        bh.consume(result);
    }
}

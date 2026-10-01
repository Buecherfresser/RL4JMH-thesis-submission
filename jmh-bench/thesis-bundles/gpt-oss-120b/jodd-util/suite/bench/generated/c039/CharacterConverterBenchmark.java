package bench.generated.c039;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.CharacterConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharacterConverterBenchmark {

    private CharacterConverter converter;

    private Object nullInput;
    private Character charInput;
    private Integer intInput;
    private Long longInput;
    private String singleCharString;
    private String digitString;

    @Setup(Level.Trial)
    public void setup() {
        converter = new CharacterConverter();

        nullInput = null;
        charInput = Character.valueOf('Z');
        intInput = Integer.valueOf(65); // 'A'
        longInput = Long.valueOf(66L);  // 'B'
        singleCharString = "X";
        digitString = "67"; // 'C'
    }

    @Benchmark
    public Character convertNull() {
        return converter.convert(nullInput);
    }

    @Benchmark
    public Character convertCharacter() {
        return converter.convert(charInput);
    }

    @Benchmark
    public Character convertInteger() {
        return converter.convert(intInput);
    }

    @Benchmark
    public Character convertLong() {
        return converter.convert(longInput);
    }

    @Benchmark
    public Character convertSingleCharString() {
        return converter.convert(singleCharString);
    }

    @Benchmark
    public Character convertDigitString() {
        return converter.convert(digitString);
    }

    @Benchmark
    public void convertIntegerConsume(Blackhole bh) {
        bh.consume(converter.convert(intInput));
    }
}

package bench.generated.c039;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import jodd.typeconverter.impl.CharacterConverter;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharacterConverterBenchmark {

    private CharacterConverter converter;

    private Character charValue;
    private Integer intValue;
    private Long longValue;
    private Short shortValue;
    private Byte byteValue;
    private Double doubleValue;
    private Float floatValue;
    private String singleCharString;
    private String digitString;
    private String signedDigitString;
    private String whitespaceString;
    private String multiDigitString;
    private String negativeDigitString;
    private String nullString;

    @Setup(Level.Trial)
    public void setup() {
        converter = new CharacterConverter();
        charValue = 'A';
        intValue = 65;
        longValue = 65L;
        shortValue = 65;
        byteValue = 65;
        doubleValue = 65.0;
        floatValue = 65.0f;
        singleCharString = "A";
        digitString = "65";
        signedDigitString = "+65";
        whitespaceString = " A ";
        multiDigitString = "65535";
        negativeDigitString = "-1";
        nullString = null;
    }

    @Benchmark
    public Character convertCharacter() {
        return converter.convert(charValue);
    }

    @Benchmark
    public Character convertInteger() {
        return converter.convert(intValue);
    }

    @Benchmark
    public Character convertLong() {
        return converter.convert(longValue);
    }

    @Benchmark
    public Character convertShort() {
        return converter.convert(shortValue);
    }

    @Benchmark
    public Character convertByte() {
        return converter.convert(byteValue);
    }

    @Benchmark
    public Character convertDouble() {
        return converter.convert(doubleValue);
    }

    @Benchmark
    public Character convertFloat() {
        return converter.convert(floatValue);
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
    public Character convertSignedDigitString() {
        return converter.convert(signedDigitString);
    }

    @Benchmark
    public Character convertWhitespaceString() {
        return converter.convert(whitespaceString);
    }

    @Benchmark
    public Character convertMultiDigitString() {
        return converter.convert(multiDigitString);
    }

    @Benchmark
    public Character convertNegativeDigitString() {
        return converter.convert(negativeDigitString);
    }

    @Benchmark
    public Character convertNull() {
        return converter.convert(nullString);
    }
}

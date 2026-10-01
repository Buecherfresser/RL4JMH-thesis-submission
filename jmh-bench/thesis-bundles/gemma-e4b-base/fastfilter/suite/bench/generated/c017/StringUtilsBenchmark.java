package bench.generated.c017;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.fastfilter.utils.StringUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class StringUtilsBenchmark {

    private char charDigit;
    private char charLetter;

    @Setup
    public void setup() {
        // Input 1: A digit character
        charDigit = '5';
        // Input 2: A lowercase letter character
        charLetter = 'a';
    }

    @Benchmark
    public void testGetHex_Digit(Blackhole bh) {
        int result = StringUtils.getHex(charDigit);
        bh.consume(result);
    }

    @Benchmark
    public void testGetHex_Letter(Blackhole bh) {
        int result = StringUtils.getHex(charLetter);
        bh.consume(result);
    }
}

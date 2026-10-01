package bench.generated.c064;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;

import org.yaml.snakeyaml.tokens.TagTuple;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TagTupleBenchmark {

    private TagTuple tuple;
    private String handle;
    private String suffix;
    private Random random;

    @Setup
    public void setup() {
        random = new Random();
        // Setup a representative, non-trivial input for the TagTuple
        handle = generateRandomString(100);
        suffix = generateRandomString(50);
        tuple = new TagTuple(handle, suffix);
    }

    private String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append((char) (random.nextInt(26) + 'a'));
        }
        return sb.toString();
    }

    @Benchmark
    public void testConstructor(Blackhole bh) {
        // Re-instantiate the object in every benchmark run to measure construction cost
        TagTuple t = new TagTuple(generateRandomString(100), generateRandomString(50));
        bh.consume(t);
    }

    @Benchmark
    public void testGetHandle(Blackhole bh) {
        // Use the state object created in setup
        String result = tuple.getHandle();
        bh.consume(result);
    }

    @Benchmark
    public void testGetSuffix(Blackhole bh) {
        // Use the state object created in setup
        String result = tuple.getSuffix();
        bh.consume(result);
    }
}

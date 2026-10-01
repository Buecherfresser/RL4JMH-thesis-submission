package bench.generated.c005;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.lang.reflect.Constructor;
import org.yaml.snakeyaml.composer.ComposerException;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ComposerExceptionBenchmark {

    private Mark contextMark;
    private Mark problemMark;
    private String context;
    private String problem;

    // Constructors needed for instantiation
    private Constructor<ComposerException> fullContextConstructor;
    private Constructor<ComposerException> minimalContextConstructor;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        // 1. Initialize Mark objects using a valid constructor (assuming the complex one)
        // Mark(String, int, int, int, char[], int)
        contextMark = new Mark("context_start", 0, 0, 0, new char[1], 0);
        problemMark = new Mark("problem_start", 0, 0, 0, new char[1], 0);

        // 2. Initialize input strings
        context = "During YAML composition phase";
        problem = "Unexpected token encountered";

        // 3. Initialize Constructors via reflection to bypass protected access
        
        // Full Context Exception: ComposerException(String context, Mark contextMark, String problem, Mark problemMark)
        fullContextConstructor = ComposerException.class.getDeclaredConstructor(
            String.class, Mark.class, String.class, Mark.class
        );
        fullContextConstructor.setAccessible(true);

        // Minimal Context Exception: ComposerException(String problem, Mark problemMark)
        minimalContextConstructor = ComposerException.class.getDeclaredConstructor(
            String.class, Mark.class
        );
        minimalContextConstructor.setAccessible(true);
    }

    /**
     * Benchmarks the construction of ComposerException with full context information.
     * ComposerException(String context, Mark contextMark, String problem, Mark problemMark)
     */
    @Benchmark
    public void benchmarkFullContextException(Blackhole bh) throws Exception {
        // Perform construction inside the benchmark to measure overhead
        ComposerException e = (ComposerException) fullContextConstructor.newInstance(
            context, contextMark, problem, problemMark
        );
        bh.consume(e);
    }

    /**
     * Benchmarks the construction of ComposerException without context information.
     * ComposerException(String problem, Mark problemMark)
     */
    @Benchmark
    public void benchmarkMinimalContextException(Blackhole bh) throws Exception {
        // Perform construction inside the benchmark to measure overhead
        ComposerException e = (ComposerException) minimalContextConstructor.newInstance(
            problem, problemMark
        );
        bh.consume(e);
    }
}

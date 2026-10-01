package bench.generated.c032;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.parser.ParserException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ParserExceptionBenchmark {

    private String context;
    private String problem;
    private Mark contextMark;
    private Mark problemMark;

    @Setup
    public void setup() {
        // Build fixed payloads once in @Setup
        this.context = "YAML parsing failed at line 10";
        this.problem = "Unexpected token '!'";

        // Create Mark objects. The Mark constructor seems to require 6 arguments based on compilation errors.
        // We provide dummy values to satisfy the constructor signature: Mark(String context, int, int, int, char[], int)
        this.contextMark = new Mark(this.context, 10, 0, 0, new char[0], 0);
        this.problemMark = new Mark(this.problem, 25, 0, 0, new char[0], 0);
    }

    @Benchmark
    public void benchmarkExceptionConstruction(Blackhole bh) {
        // Call the subject method (constructor) exactly once per invocation
        ParserException exception = new ParserException(
                context,
                contextMark,
                problem,
                problemMark
        );
        // Consume the result
        bh.consume(exception);
    }
}

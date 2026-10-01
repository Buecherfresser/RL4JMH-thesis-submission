package bench.generated.c032;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.parser.ParserException;

/**
 * Benchmark for the construction of ParserException.
 * Since ParserException is an exception class, this benchmark measures the time
 * taken to construct an instance with complex string inputs, simulating the
 * overhead associated with error reporting during parsing.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ParserExceptionBenchmark {

    // State fields to hold inputs built in @Setup
    private String contextInput;
    private String problemInput;

    @Setup
    public void setup() {
        // Build complex, representative inputs for the exception constructor.
        // These strings simulate the context and the problematic part of the YAML input.
        this.contextInput = "Document root at line 42, column 10.";
        this.problemInput = "Unexpected token '!' found near mapping start.";
    }

    /**
     * Benchmarks the construction of a ParserException instance.
     * This simulates the cost incurred when a parser encounters an error
     * and needs to report it via ParserException.
     *
     * @param bh Blackhole to consume the result.
     */
    @Benchmark
    public void constructParserException(Blackhole bh) {
        // Call the constructor, which is the measurable operation.
        ParserException exception = new ParserException(
                this.contextInput,
                null, // Mark context (using null as a placeholder since we cannot instantiate Mark easily)
                this.problemInput,
                null  // Mark problem (using null as a placeholder)
        );
        bh.consume(exception);
    }
}

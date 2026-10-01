package bench.generated.c032;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.parser.ParserException;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ParserExceptionBenchmark {

    private String context;
    private Mark contextMark;
    private String problem;
    private Mark problemMark;

    @Setup(Level.Trial)
    public void setup() {
        // Build representative inputs for the exception constructor
        context = "The YAML document context where the parsing error occurred.";
        problem = "Unexpected token 'xyz' found at line 5, column 10.";
        
        // Initialize Mark objects using a valid constructor signature
        // Mark(String, int, int, int, char[], int)
        String dummyString = "dummy";
        int dummyInt1 = 0;
        int dummyInt2 = 0;
        int dummyInt3 = 0;
        char[] dummyCharArray = new char[0];
        int dummyInt4 = 0;
        
        contextMark = new Mark(dummyString, dummyInt1, dummyInt2, dummyInt3, dummyCharArray, dummyInt4);
        problemMark = new Mark(dummyString, dummyInt1, dummyInt2, dummyInt3, dummyCharArray, dummyInt4);
    }

    /**
     * Benchmarks the construction of ParserException, measuring the overhead 
     * of creating the exception and initializing its fields.
     */
    @Benchmark
    public ParserException createParserException(Blackhole bh) {
        // Subject method call: Constructor invocation
        ParserException exception = new ParserException(context, contextMark, problem, problemMark);
        
        // Consume the result to prevent dead code elimination
        bh.consume(exception);
        return exception;
    }
}

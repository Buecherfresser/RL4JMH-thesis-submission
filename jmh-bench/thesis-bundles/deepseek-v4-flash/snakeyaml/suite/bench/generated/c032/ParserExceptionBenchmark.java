package bench.generated.c032;

import org.openjdk.jmh.annotations.*;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.parser.ParserException;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ParserExceptionBenchmark {

    private Mark contextMark;
    private Mark problemMark;
    private String context;
    private String problem;

    @Setup(Level.Trial)
    public void setup() {
        char[] buffer = "line1\nline2\nline3".toCharArray();
        contextMark = new Mark("test", 0, 0, 0, buffer, 0);
        problemMark = new Mark("test", 5, 1, 2, buffer, 5);
        context = "while parsing a block collection";
        problem = "did not find expected '-' indicator";
    }

    @Benchmark
    public ParserException construct() {
        return new ParserException(context, contextMark, problem, problemMark);
    }
}

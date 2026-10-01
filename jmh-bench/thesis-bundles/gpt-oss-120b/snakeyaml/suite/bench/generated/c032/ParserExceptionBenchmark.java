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
    private String problem;
    private Mark contextMark;
    private Mark problemMark;
    private ParserException prebuiltException;

    @Setup(Level.Trial)
    public void setup() {
        context = "Context of error";
        problem = "Problematic token";
        char[] buffer = "sample yaml".toCharArray();
        contextMark = new Mark("test.yaml", 1, 5, 10, buffer, buffer.length);
        problemMark = new Mark("test.yaml", 2, 3, 20, buffer, buffer.length);
        prebuiltException = new ParserException(context, contextMark, problem, problemMark);
    }

    @Benchmark
    public ParserException constructException() {
        return new ParserException(context, contextMark, problem, problemMark);
    }

    @Benchmark
    public String getProblem() {
        return prebuiltException.getProblem();
    }

    @Benchmark
    public String getContext() {
        return prebuiltException.getContext();
    }

    @Benchmark
    public Mark getProblemMark() {
        return prebuiltException.getProblemMark();
    }

    @Benchmark
    public Mark getContextMark() {
        return prebuiltException.getContextMark();
    }

    @Benchmark
    public void getMessage(Blackhole bh) {
        bh.consume(prebuiltException.getMessage());
    }
}

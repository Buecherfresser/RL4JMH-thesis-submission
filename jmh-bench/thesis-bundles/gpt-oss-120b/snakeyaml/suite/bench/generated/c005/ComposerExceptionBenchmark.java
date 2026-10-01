package bench.generated.c005;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
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

    @Setup
    public void setUp() {
        this.contextMark = new Mark("context", 1, 1, 0, new char[0], 0);
        this.problemMark = new Mark("problem", 2, 2, 0, new char[0], 0);
        this.context = "sample context";
        this.problem = "sample problem";
    }

    @Benchmark
    public ComposerException createWithContext() {
        return new PublicComposerException(context, contextMark, problem, problemMark);
    }

    @Benchmark
    public ComposerException createWithoutContext() {
        return new PublicComposerException(problem, problemMark);
    }

    public static class PublicComposerException extends ComposerException {
        public PublicComposerException(String context, Mark contextMark, String problem, Mark problemMark) {
            super(context, contextMark, problem, problemMark);
        }

        public PublicComposerException(String problem, Mark problemMark) {
            super(problem, problemMark);
        }
    }
}

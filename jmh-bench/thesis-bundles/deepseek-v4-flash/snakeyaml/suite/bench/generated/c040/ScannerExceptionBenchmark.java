package bench.generated.c040;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.scanner.ScannerException;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ScannerExceptionBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        String context;
        String problem;
        String note;
        Mark contextMark;
        Mark problemMark;
        ScannerException exception;

        @Setup(Level.Trial)
        public void setup() {
            context = "while scanning a simple key";
            problem = "could not find expected ':'";
            note = "line 3, column 1";
            int[] buffer = new int[] {1, 2, 3, 4, 5};
            contextMark = new Mark("test.yaml", 0, 0, 0, buffer, 0);
            problemMark = new Mark("test.yaml", 10, 2, 5, buffer, 3);
            exception = new ScannerException(context, contextMark, problem, problemMark, note);
        }
    }

    @Benchmark
    public ScannerException construct5Args(BenchmarkState state) {
        return new ScannerException(state.context, state.contextMark, state.problem, state.problemMark, state.note);
    }

    @Benchmark
    public ScannerException construct4Args(BenchmarkState state) {
        return new ScannerException(state.context, state.contextMark, state.problem, state.problemMark);
    }

    @Benchmark
    public String getContext(BenchmarkState state) {
        return state.exception.getContext();
    }

    @Benchmark
    public String getProblem(BenchmarkState state) {
        return state.exception.getProblem();
    }

    @Benchmark
    public Mark getProblemMark(BenchmarkState state) {
        return state.exception.getProblemMark();
    }

    @Benchmark
    public Mark getContextMark(BenchmarkState state) {
        return state.exception.getContextMark();
    }

    @Benchmark
    public String getMessage(BenchmarkState state) {
        return state.exception.getMessage();
    }

    @Benchmark
    public String toString(BenchmarkState state) {
        return state.exception.toString();
    }
}

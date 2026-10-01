package bench.generated.c012;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.dump.DumpArchiveException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DumpArchiveExceptionBenchmark {

    @State(Scope.Benchmark)
    public static class BenchState {
        private String message;
        private Throwable cause;
        private DumpArchiveException exception;

        @Setup(Level.Trial)
        public void setUp() {
            message = "sample dump archive error";
            cause = new IllegalStateException("root cause");
            exception = new DumpArchiveException(message, cause);
        }
    }

    @Benchmark
    public DumpArchiveException constructDefault() {
        return new DumpArchiveException();
    }

    @Benchmark
    public DumpArchiveException constructMessage(BenchState state) {
        return new DumpArchiveException(state.message);
    }

    @Benchmark
    public DumpArchiveException constructMessageCause(BenchState state) {
        return new DumpArchiveException(state.message, state.cause);
    }

    @Benchmark
    public DumpArchiveException constructCause(BenchState state) {
        return new DumpArchiveException(state.cause);
    }

    @Benchmark
    public String getMessage(BenchState state) {
        return state.exception.getMessage();
    }

    @Benchmark
    public Throwable getCause(BenchState state) {
        return state.exception.getCause();
    }

    @Benchmark
    public String toStringBenchmark(BenchState state) {
        return state.exception.toString();
    }

    @Benchmark
    public void printStackTrace(Blackhole bh, BenchState state) {
        state.exception.printStackTrace();
    }
}

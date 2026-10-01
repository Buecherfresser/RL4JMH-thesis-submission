package bench.generated.c007;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.constructor.ConstructorException;
import org.yaml.snakeyaml.error.Mark;
import java.lang.reflect.Constructor;

@State(org.openjdk.jmh.annotations.Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ConstructorExceptionBenchmark {

    private Constructor<ConstructorException> ctor;
    private String context;
    private String problem;
    private Mark contextMark;
    private Mark problemMark;
    private Throwable cause;
    private ConstructorException preCreated;

    @Setup
    public void setup() throws Exception {
        // Prepare strings
        this.context = new String("context");
        this.problem = new String("problem");
        // Prepare marks
        char[] buffer = new char[] {'a', 'b', 'c'};
        this.contextMark = new Mark("test", 1, 2, 0, buffer, 0);
        this.problemMark = new Mark("test", 3, 4, 0, buffer, 0);
        // Prepare cause
        this.cause = new RuntimeException("cause");
        // Obtain the protected constructor via reflection
        this.ctor = (Constructor<ConstructorException>) ConstructorException.class
                .getDeclaredConstructor(String.class, Mark.class, String.class, Mark.class, Throwable.class);
        this.ctor.setAccessible(true);
        // Create a pre-instantiated exception for read‑only benchmarks
        this.preCreated = this.ctor.newInstance(context, contextMark, problem, problemMark, cause);
    }

    @Benchmark
    public ConstructorException benchmarkCreateException() throws Exception {
        // Create a new ConstructorException instance
        return this.ctor.newInstance(context, contextMark, problem, problemMark, cause);
    }

    @Benchmark
    public String benchmarkGetMessage(Blackhole bh) {
        String msg = preCreated.getMessage();
        bh.consume(msg);
        return msg;
    }

    @Benchmark
    public Mark benchmarkGetContextMark(Blackhole bh) {
        Mark m = preCreated.getContextMark();
        bh.consume(m);
        return m;
    }

    @Benchmark
    public Mark benchmarkGetProblemMark(Blackhole bh) {
        Mark m = preCreated.getProblemMark();
        bh.consume(m);
        return m;
    }

    @Benchmark
    public Throwable benchmarkGetCause(Blackhole bh) {
        Throwable t = preCreated.getCause();
        bh.consume(t);
        return t;
    }
}

package bench.generated.c007;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.constructor.ConstructorException;
import org.yaml.snakeyaml.error.Mark;
import java.lang.reflect.Constructor;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ConstructorExceptionBenchmark {

    private Constructor<?> constructor;
    private String context;
    private String problem;
    private Mark contextMark;
    private Mark problemMark;

    @Setup
    public void setup() {
        // Prepare immutable inputs
        this.context = "Test context for YAML parsing failure";
        this.problem = "Invalid type conversion during object construction";

        // Instantiate Mark objects using a valid constructor signature inferred from compilation errors.
        // Using dummy values (0, new char[0]) to satisfy the required parameters.
        this.contextMark = new Mark("dummy", 0, 0, 0, new char[0], 0);
        this.problemMark = new Mark("dummy", 0, 0, 0, new char[0], 0);

        try {
            // Get the protected constructor: (String, Mark, String, Mark)
            Constructor<?> constructorRef = ConstructorException.class.getDeclaredConstructor(
                    String.class,
                    Mark.class,
                    String.class,
                    Mark.class
            );
            constructorRef.setAccessible(true);
            this.constructor = constructorRef;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Could not find required ConstructorException constructor", e);
        }
    }

    @Benchmark
    public ConstructorException benchmarkConstructorExceptionCreation(Blackhole bh) throws Exception {
        // Call the protected constructor via reflection
        ConstructorException exception = (ConstructorException) constructor.newInstance(
                context,
                contextMark,
                problem,
                problemMark
        );
        
        // Consume the result
        bh.consume(exception);
        return exception;
    }
}

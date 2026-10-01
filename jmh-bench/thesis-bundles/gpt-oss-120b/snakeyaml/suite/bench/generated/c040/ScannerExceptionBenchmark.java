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

    private String context;
    private String problem;
    private String note;
    private Mark contextMark;
    private Mark problemMark;

    @Setup(Level.Trial)
    public void setUp() {
        context = "SampleContext";
        problem = "SampleProblem";
        note = "AdditionalNote";

        char[] buffer = new char[256];
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = (char) ('a' + (i % 26));
        }

        // Mark constructor: Mark(String name, int line, int column, int index, char[] buffer, int pointer)
        contextMark = new Mark("test.yaml", 10, 5, 0, buffer, 0);
        problemMark = new Mark("test.yaml", 12, 8, 0, buffer, 0);
    }

    @Benchmark
    public ScannerException createFullConstructor() {
        return new ScannerException(context, contextMark, problem, problemMark, note);
    }

    @Benchmark
    public ScannerException createWithoutNoteConstructor() {
        return new ScannerException(context, contextMark, problem, problemMark);
    }
}

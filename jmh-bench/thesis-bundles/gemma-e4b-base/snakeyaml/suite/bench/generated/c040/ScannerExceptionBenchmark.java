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
    private Mark contextMark;
    private String problem;
    private Mark problemMark;
    private String note;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize fixed inputs for exception construction
        this.context = "YAML document context snippet";
        
        // Fix: Mark requires a complex constructor. Using dummy data to satisfy compilation.
        // Constructor signature used: Mark(String, int, int, int, char[], int)
        this.contextMark = new Mark("context", 0, 0, 0, new char[0], 0); 
        
        this.problem = "Malformed token detected";
        this.problemMark = new Mark("problem", 10, 5, 0, new char[0], 0); 
        
        this.note = "Error details provided by scanner";
    }

    /**
     * Benchmarks the construction of ScannerException using all available arguments.
     */
    @Benchmark
    public void constructScannerExceptionFull(Blackhole bh) {
        ScannerException e = new ScannerException(
                context,
                contextMark,
                problem,
                problemMark,
                note
        );
        bh.consume(e);
    }

    /**
     * Benchmarks the construction of ScannerException using only required arguments.
     */
    @Benchmark
    public void constructScannerExceptionMinimal(Blackhole bh) {
        ScannerException e = new ScannerException(
                context,
                contextMark,
                problem,
                problemMark
        );
        bh.consume(e);
    }
}

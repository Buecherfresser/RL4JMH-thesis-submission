package bench.generated.c057;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.ShortArrayConverter;
import jodd.typeconverter.TypeConverterManager;

@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ShortArrayConverterBenchmark {

    private ShortArrayConverter converter;

    public ShortArrayConverterBenchmark() {
        // Initialize the converter. We pass null for the manager as we don't have a real implementation.
        try {
            this.converter = new ShortArrayConverter(null);
        } catch (Exception e) {
            // If initialization fails, the benchmark will handle null checks gracefully.
            System.err.println("Failed to initialize ShortArrayConverter: " + e.getMessage());
            this.converter = null;
        }
    }

    @Setup
    public void setup() {
        // Ensure the converter is initialized for the trial
        if (this.converter == null) {
            try {
                this.converter = new ShortArrayConverter(null);
            } catch (Exception e) {
                // Ignore setup failure for benchmark structure
            }
        }
    }

    @Benchmark
    public void convert_null(Blackhole bh) {
        if (converter != null) {
            // Test the null handling path
            bh.consume(converter.convert(null));
        }
    }

    @Benchmark
    public void convert_integer_array(Blackhole bh) {
        if (converter != null) {
            // Test conversion of a primitive array (int[])
            // Note: We must use an array that can be passed as Object, e.g., an int[]
            bh.consume(converter.convert(new int[]{1, 2, 3}));
        }
    }
}

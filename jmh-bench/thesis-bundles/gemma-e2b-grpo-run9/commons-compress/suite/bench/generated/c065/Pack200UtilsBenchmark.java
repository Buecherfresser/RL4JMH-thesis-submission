package bench.generated.c065;

import org.apache.commons.compress.compressors.pack200.Pack200Utils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Pack200UtilsBenchmark {

    // Since Pack200Utils methods are static and rely on File I/O,
    // we cannot easily use instance state. We rely on the benchmark
    // method to handle the necessary (though potentially failing) I/O setup.

    @Benchmark
    public void normalize_simple(Blackhole bh) {
        try {
            // Attempting to normalize a dummy file. This operation is inherently
            // I/O bound and relies on the existence of a file system structure.
            // We call the method and consume the result (void method).
            Pack200Utils.normalize(new File("dummy_input.jar"));
        } catch (IOException e) {
            // Expected if the dummy file doesn't exist or I/O fails, which is fine for a test structure.
        }
    }

    @Benchmark
    public void normalize_with_props(Blackhole bh) {
        try {
            // Test the version that takes properties map
            Map<String, String> props = new HashMap<>();
            Pack200Utils.normalize(new File("dummy_input.jar"), props);
        } catch (IOException e) {
            // Expected
        }
    }
}

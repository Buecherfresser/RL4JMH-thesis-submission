package bench.generated.c008;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.LoaderOptions;
import java.lang.ClassLoader;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CustomClassLoaderConstructorBenchmark {

    // We use a subclass to expose the protected method for benchmarking purposes
    private static class TestableConstructor extends CustomClassLoaderConstructor {
        public TestableConstructor(Class<? extends Object> theRoot, ClassLoader theLoader, LoaderOptions loadingConfig) {
            super(theRoot, theLoader, loadingConfig);
        }

        // Expose the protected method
        public Class<?> getClassForName(String name) throws ClassNotFoundException {
            return super.getClassForName(name);
        }
    }

    private TestableConstructor constructor;
    private String classNameToLoad;
    private ClassLoader customLoader;
    private LoaderOptions loadingOptions;

    @Setup(Level.Trial)
    public void setup() {
        // Use the system class loader as the custom loader for reliable testing
        this.customLoader = ClassLoader.getSystemClassLoader();
        
        // Use a standard class name that is guaranteed to exist
        this.classNameToLoad = "java.lang.String";

        // Initialize options
        this.loadingOptions = new LoaderOptions();

        // Initialize the SUT using the testable subclass
        this.constructor = new TestableConstructor(Object.class, this.customLoader, this.loadingOptions);
    }

    @Benchmark
    public Class<?> benchmarkClassForName() throws ClassNotFoundException {
        // Call the exposed method
        return constructor.getClassForName(classNameToLoad);
    }
}

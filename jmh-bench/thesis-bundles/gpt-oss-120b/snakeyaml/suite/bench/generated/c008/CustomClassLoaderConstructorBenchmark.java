package bench.generated.c008;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.LoaderOptions;
import java.net.URLClassLoader;
import java.net.URL;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CustomClassLoaderConstructorBenchmark {

    private ClassLoader customLoader;
    private CustomClassLoaderConstructor constructor;
    private Yaml yaml;
    private String yamlInt;
    private Integer intObject;

    @Setup(Level.Trial)
    public void setUp() {
        // Create a simple URLClassLoader that delegates to the system class loader
        this.customLoader = new URLClassLoader(new URL[0], getClass().getClassLoader());
        LoaderOptions loaderOptions = new LoaderOptions();
        this.constructor = new CustomClassLoaderConstructor(customLoader, loaderOptions);
        this.yaml = new Yaml(constructor);
        // YAML document that forces class loading of java.lang.Integer
        this.yamlInt = "!!java.lang.Integer 42";
        this.intObject = 42;
    }

    @Benchmark
    public Object loadInteger() throws Exception {
        // Load a YAML document that requires class loading via the custom constructor
        return yaml.load(yamlInt);
    }

    @Benchmark
    public String dumpInteger() {
        // Dump a simple Integer object
        return yaml.dump(intObject);
    }

    @Benchmark
    public CustomClassLoaderConstructor createConstructor() {
        // Benchmark the construction of a new CustomClassLoaderConstructor instance
        LoaderOptions lo = new LoaderOptions();
        return new CustomClassLoaderConstructor(customLoader, lo);
    }

    @Benchmark
    public void loadIntegerConsume(Blackhole bh) throws Exception {
        // Variant that consumes the result via Blackhole
        bh.consume(yaml.load(yamlInt));
    }

    @Benchmark
    public void dumpIntegerConsume(Blackhole bh) {
        // Variant that consumes the dump result via Blackhole
        bh.consume(yaml.dump(intObject));
    }
}

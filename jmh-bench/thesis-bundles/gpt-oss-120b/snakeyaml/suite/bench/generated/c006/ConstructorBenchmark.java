package bench.generated.c006;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.TypeDescription;
import java.util.Collection;
import java.util.Collections;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ConstructorBenchmark {

    private LoaderOptions loaderOptions;
    private TypeDescription beanTypeDescription;
    private String beanClassName;

    @Setup(Level.Trial)
    public void setUp() {
        loaderOptions = new LoaderOptions();
        // Use the inner static bean class as the root type
        beanTypeDescription = new TypeDescription(MyBean.class);
        beanClassName = MyBean.class.getName();
    }

    @Benchmark
    public Constructor benchmarkConstructorWithClass() {
        return new Constructor(MyBean.class, loaderOptions);
    }

    @Benchmark
    public Constructor benchmarkConstructorWithString() throws Exception {
        return new Constructor(beanClassName, loaderOptions);
    }

    @Benchmark
    public Constructor benchmarkConstructorWithTypeDescription() {
        return new Constructor(beanTypeDescription, loaderOptions);
    }

    @Benchmark
    public Constructor benchmarkConstructorDefaultRoot() {
        return new Constructor(loaderOptions);
    }

    // Simple bean used for TypeDescription and Class root benchmarks
    public static class MyBean {
        private String name = "test";
        private int value = 42;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}

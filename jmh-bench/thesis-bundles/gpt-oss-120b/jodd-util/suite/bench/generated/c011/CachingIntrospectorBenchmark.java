package bench.generated.c011;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.introspector.CachingIntrospector;
import jodd.introspector.ClassDescriptor;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CachingIntrospectorBenchmark {

    private CachingIntrospector introspector;
    private Class<?> sampleClass;

    private static class Sample {
        private int id;
        private String name;

        public Sample() {}

        public Sample(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    @Setup
    public void setUp() {
        introspector = new CachingIntrospector();
        sampleClass = Sample.class;
    }

    @Benchmark
    public CachingIntrospector createDefault() {
        return new CachingIntrospector();
    }

    @Benchmark
    public ClassDescriptor lookupObjectClass() {
        return introspector.lookup(Object.class);
    }

    @Benchmark
    public ClassDescriptor lookupStringClass() {
        return introspector.lookup(String.class);
    }

    @Benchmark
    public ClassDescriptor lookupArrayClass() {
        return introspector.lookup(int[].class);
    }

    @Benchmark
    public ClassDescriptor lookupSampleClass() {
        return introspector.lookup(sampleClass);
    }

    @Benchmark
    public void resetCache(Blackhole bh) {
        introspector.reset();
        bh.consume(introspector);
    }
}

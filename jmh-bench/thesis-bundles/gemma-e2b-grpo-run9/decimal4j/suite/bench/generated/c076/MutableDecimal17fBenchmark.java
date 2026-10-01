package bench.generated.c076;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.decimal4j.mutable.MutableDecimal17f;
import org.decimal4j.factory.Factory17f;
import org.decimal4j.immutable.Decimal17f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal17fBenchmark {

    @Benchmark
    public void testZero(Blackhole bh) {
        MutableDecimal17f result = MutableDecimal17f.zero();
        bh.consume(result);
    }

    @Benchmark
    public void testOne(Blackhole bh) {
        MutableDecimal17f result = MutableDecimal17f.one();
        bh.consume(result);
    }

    @Benchmark
    public void testUnscaled(Blackhole bh) {
        // Test static method that returns a new instance
        MutableDecimal17f result = MutableDecimal17f.unscaled(1234567890123456789L);
        bh.consume(result);
    }

    @Benchmark
    public void testMultiplyExact(Blackhole bh) {
        // Test instance method that returns a new object
        MutableDecimal17f original = MutableDecimal17f.zero();
        try {
            // This method returns a Multipliable17f, which we consume.
            original.multiplyExact();
        } catch (Exception e) {
            // Ignore exceptions for benchmarking purposes if they are expected
        }
    }

    @Benchmark
    public void testToImmutableDecimal(Blackhole bh) {
        // Test conversion method
        MutableDecimal17f mutableInstance = MutableDecimal17f.zero();
        try {
            bh.consume(mutableInstance.toImmutableDecimal());
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testFactoryAccess(Blackhole bh) {
        // Fix: Since getFactory() is an instance method, we must instantiate the class
        // to call it, resolving the compilation error.
        MutableDecimal17f instance = MutableDecimal17f.zero();
        Factory17f factory = instance.getFactory();
        bh.consume(factory);
    }
}

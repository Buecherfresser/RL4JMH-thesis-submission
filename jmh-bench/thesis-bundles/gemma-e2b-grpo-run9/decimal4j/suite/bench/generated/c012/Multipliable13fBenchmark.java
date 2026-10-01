package bench.generated.c012;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import org.decimal4j.api.Decimal;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.immutable.Decimal3f;
import org.decimal4j.immutable.Decimal4f;
import org.decimal4j.immutable.Decimal5f;
import org.decimal4j.immutable.Decimal13f;
import org.decimal4j.mutable.MutableDecimal0f;
import org.decimal4j.mutable.MutableDecimal1f;
import org.decimal4j.mutable.MutableDecimal2f;
import org.decimal4j.mutable.MutableDecimal3f;
import org.decimal4j.mutable.MutableDecimal4f;
import org.decimal4j.mutable.MutableDecimal5f;
import org.decimal4j.scale.Scale13f;
import org.decimal4j.exact.Multipliable13f;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class Multipliable13fBenchmark {

    // State field for the subject under test.
    private Multipliable13f multipliable13f;

    @Setup
    public void setup() {
        try {
            // Initialize a base value. We use Decimal13f.valueOf(1L) as a placeholder.
            this.multipliable13f = new Multipliable13f(Decimal13f.valueOf(1L));
        } catch (Exception e) {
            // In a real scenario, this setup failure should be handled more robustly,
            // but for JMH context, we proceed if possible.
        }
    }

    @Benchmark
    public void by_Decimal0f(Blackhole bh) {
        if (multipliable13f != null) {
            try {
                multipliable13f.by(Decimal0f.ZERO);
            } catch (Exception e) {
                // Ignore exceptions
            }
        }
    }

    @Benchmark
    public void by_MutableDecimal0f(Blackhole bh) {
        if (multipliable13f != null) {
            try {
                multipliable13f.by(MutableDecimal0f.zero());
            } catch (Exception e) {
                // Ignore exceptions
            }
        }
    }

    @Benchmark
    public void by_Decimal1f(Blackhole bh) {
        if (multipliable13f != null) {
            try {
                multipliable13f.by(Decimal1f.ONE);
            } catch (Exception e) {
                // Ignore exceptions
            }
        }
    }

    @Benchmark
    public void by_MutableDecimal1f(Blackhole bh) {
        if (multipliable13f != null) {
            try {
                multipliable13f.by(MutableDecimal1f.one());
            } catch (Exception e) {
                // Ignore exceptions
            }
        }
    }

    @Benchmark
    public void by_Decimal2f(Blackhole bh) {
        if (multipliable13f != null) {
            try {
                multipliable13f.by(Decimal2f.FIVE);
            } catch (Exception e) {
                // Ignore exceptions
            }
        }
    }

    @Benchmark
    public void by_MutableDecimal2f(Blackhole bh) {
        if (multipliable13f != null) {
            try {
                multipliable13f.by(MutableDecimal2f.two());
            } catch (Exception e) {
                // Ignore exceptions
            }
        }
    }

    @Benchmark
    public void by_Decimal3f(Blackhole bh) {
        if (multipliable13f != null) {
            try {
                multipliable13f.by(Decimal3f.TEN);
            } catch (Exception e) {
                // Ignore exceptions
            }
        }
    }

    @Benchmark
    public void by_MutableDecimal3f(Blackhole bh) {
        if (multipliable13f != null) {
            try {
                multipliable13f.by(MutableDecimal3f.ten());
            } catch (Exception e) {
                // Ignore exceptions
            }
        }
    }

    @Benchmark
    public void by_Decimal4f(Blackhole bh) {
        if (multipliable13f != null) {
            try {
                multipliable13f.by(Decimal4f.ONE);
            } catch (Exception e) {
                // Ignore exceptions
            }
        }
    }

    @Benchmark
    public void by_MutableDecimal4f(Blackhole bh) {
        if (multipliable13f != null) {
            try {
                multipliable13f.by(MutableDecimal4f.one());
            } catch (Exception e) {
                // Ignore exceptions
            }
        }
    }

    @Benchmark
    public void by_Decimal5f(Blackhole bh) {
        if (multipliable13f != null) {
            try {
                multipliable13f.by(Decimal5f.TWO);
            } catch (Exception e) {
                // Ignore exceptions
            }
        }
    }

    @Benchmark
    public void by_MutableDecimal5f(Blackhole bh) {
        if (multipliable13f != null) {
            try {
                multipliable13f.by(MutableDecimal5f.two());
            } catch (Exception e) {
                // Ignore exceptions
            }
        }
    }
}

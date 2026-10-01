package bench.generated.c078;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import org.decimal4j.mutable.MutableDecimal1f;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.api.Decimal;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal1fBenchmark {

    private static final int POOL_SIZE = 1024;

    private MutableDecimal1f[] receivers;
    private Decimal1f[] operands;
    private long[] longValues;
    private double[] doubleValues;
    private String[] stringValues;
    private BigDecimal[] bigDecimalValues;
    private int idx;

    @Setup(Level.Trial)
    public void setup() {
        receivers = new MutableDecimal1f[POOL_SIZE];
        operands = new Decimal1f[POOL_SIZE];
        longValues = new long[POOL_SIZE];
        doubleValues = new double[POOL_SIZE];
        stringValues = new String[POOL_SIZE];
        bigDecimalValues = new BigDecimal[POOL_SIZE];

        for (int i = 0; i < POOL_SIZE; i++) {
            // Receivers: mix of positive and negative values, not too large
            double rv = (i % 2 == 0) ? (i * 1.1) : -(i * 1.1);
            receivers[i] = new MutableDecimal1f(rv);

            // Operands: non-zero, mix of signs, around 1.0 magnitude
            double ov = (i % 2 == 0) ? (1.0 + (i % 10) * 0.1) : -(1.0 + (i % 10) * 0.1);
            operands[i] = Decimal1f.valueOf(ov);

            longValues[i] = (i % 2 == 0) ? (i * 100L) : -(i * 100L);
            doubleValues[i] = (i % 2 == 0) ? (i * 0.5) : -(i * 0.5);
            stringValues[i] = (i % 2 == 0) ? (i + ".5") : ("-" + i + ".5");
            bigDecimalValues[i] = new BigDecimal(stringValues[i]);
        }
        idx = 0;
    }

    private MutableDecimal1f nextReceiver() {
        return receivers[idx & (POOL_SIZE - 1)];
    }

    private Decimal1f nextOperand() {
        return operands[idx & (POOL_SIZE - 1)];
    }

    private long nextLong() {
        return longValues[idx & (POOL_SIZE - 1)];
    }

    private double nextDouble() {
        return doubleValues[idx & (POOL_SIZE - 1)];
    }

    private String nextString() {
        return stringValues[idx & (POOL_SIZE - 1)];
    }

    private BigDecimal nextBigDecimal() {
        return bigDecimalValues[idx & (POOL_SIZE - 1)];
    }

    private void advance() {
        idx++;
    }

    // ---------- Arithmetic (mutating) ----------

    @Benchmark
    public MutableDecimal1f add(Blackhole bh) {
        MutableDecimal1f r = nextReceiver();
        Decimal1f o = nextOperand();
        advance();
        return r.add(o);
    }

    @Benchmark
    public MutableDecimal1f subtract(Blackhole bh) {
        MutableDecimal1f r = nextReceiver();
        Decimal1f o = nextOperand();
        advance();
        return r.subtract(o);
    }

    @Benchmark
    public MutableDecimal1f multiply(Blackhole bh) {
        MutableDecimal1f r = nextReceiver();
        Decimal1f o = nextOperand();
        advance();
        return r.multiply(o);
    }

    @Benchmark
    public MutableDecimal1f divide(Blackhole bh) {
        MutableDecimal1f r = nextReceiver();
        Decimal1f o = nextOperand();
        advance();
        return r.divide(o);
    }

    @Benchmark
    public MutableDecimal1f negate(Blackhole bh) {
        MutableDecimal1f r = nextReceiver();
        advance();
        return r.negate();
    }

    @Benchmark
    public MutableDecimal1f abs(Blackhole bh) {
        MutableDecimal1f r = nextReceiver();
        advance();
        return r.abs();
    }

    @Benchmark
    public MutableDecimal1f square(Blackhole bh) {
        MutableDecimal1f r = nextReceiver();
        advance();
        return r.square();
    }

    @Benchmark
    public MutableDecimal1f sqrt(Blackhole bh) {
        MutableDecimal1f r = nextReceiver();
        advance();
        return r.sqrt();
    }

    @Benchmark
    public MutableDecimal1f pow(Blackhole bh) {
        MutableDecimal1f r = nextReceiver();
        advance();
        return r.pow(2);
    }

    // ---------- Setters (mutating) ----------

    @Benchmark
    public MutableDecimal1f setLong(Blackhole bh) {
        MutableDecimal1f r = nextReceiver();
        long v = nextLong();
        advance();
        return r.set(v);
    }

    @Benchmark
    public MutableDecimal1f setDouble(Blackhole bh) {
        MutableDecimal1f r = nextReceiver();
        double v = nextDouble();
        advance();
        return r.set(v);
    }

    @Benchmark
    public MutableDecimal1f setString(Blackhole bh) {
        MutableDecimal1f r = nextReceiver();
        String v = nextString();
        advance();
        return r.set(v);
    }

    @Benchmark
    public MutableDecimal1f setBigDecimal(Blackhole bh) {
        MutableDecimal1f r = nextReceiver();
        BigDecimal v = nextBigDecimal();
        advance();
        return r.set(v);
    }

    @Benchmark
    public MutableDecimal1f setUnscaled(Blackhole bh) {
        MutableDecimal1f r = nextReceiver();
        long v = nextLong();
        advance();
        return r.setUnscaled(v);
    }

    // ---------- Conversions (non-mutating) ----------

    @Benchmark
    public long longValue(Blackhole bh) {
        MutableDecimal1f r = nextReceiver();
        advance();
        return r.longValue();
    }

    @Benchmark
    public double doubleValue(Blackhole bh) {
        MutableDecimal1f r = nextReceiver();
        advance();
        return r.doubleValue();
    }

    @Benchmark
    public BigDecimal toBigDecimal(Blackhole bh) {
        MutableDecimal1f r = nextReceiver();
        advance();
        return r.toBigDecimal();
    }

    @Benchmark
    public Decimal1f toImmutable(Blackhole bh) {
        MutableDecimal1f r = nextReceiver();
        advance();
        return r.toImmutableDecimal();
    }

    @Benchmark
    public MutableDecimal1f clone(Blackhole bh) {
        MutableDecimal1f r = nextReceiver();
        advance();
        return r.clone();
    }

    // ---------- Exact multiplication ----------

    @Benchmark
    public Decimal<?> multiplyExact(Blackhole bh) {
        MutableDecimal1f r = nextReceiver();
        Decimal1f o = nextOperand();
        advance();
        return r.multiplyExact().by(o);
    }

    // ---------- Comparison ----------

    @Benchmark
    public int compareTo(Blackhole bh) {
        MutableDecimal1f r = nextReceiver();
        Decimal1f o = nextOperand();
        advance();
        return r.compareTo(o);
    }

    // ---------- Static factories (allocation) ----------

    @Benchmark
    public MutableDecimal1f zero() {
        return MutableDecimal1f.zero();
    }

    @Benchmark
    public MutableDecimal1f one() {
        return MutableDecimal1f.one();
    }

    @Benchmark
    public MutableDecimal1f valueOfLong(Blackhole bh) {
        long v = nextLong();
        advance();
        return new MutableDecimal1f(v);
    }

    @Benchmark
    public MutableDecimal1f valueOfDouble(Blackhole bh) {
        double v = nextDouble();
        advance();
        return new MutableDecimal1f(v);
    }

    @Benchmark
    public MutableDecimal1f valueOfString(Blackhole bh) {
        String v = nextString();
        advance();
        return new MutableDecimal1f(v);
    }

    @Benchmark
    public MutableDecimal1f valueOfBigDecimal(Blackhole bh) {
        BigDecimal v = nextBigDecimal();
        advance();
        return new MutableDecimal1f(v);
    }
}

package bench.generated.c084;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.util.TypeCache;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TypeCacheBenchmark {

    private TypeCache<String> defaultCache;
    private TypeCache<String> threadSafeCache;
    private TypeCache<String> weakCache;
    private TypeCache<String> noCache;

    private Class<?> key1;
    private Class<?> key2;
    private String value1;
    private String value2;

    @Setup
    public void setup() {
        key1 = String.class;
        key2 = Integer.class;
        value1 = "value1";
        value2 = "value2";

        defaultCache = TypeCache.<String>create().get();
        threadSafeCache = TypeCache.<String>create().threadsafe(true).get();
        weakCache = TypeCache.<String>create().weak(true).get();
        noCache = TypeCache.<String>create().noCache().get();

        defaultCache.put(key1, value1);
        threadSafeCache.put(key1, value1);
        weakCache.put(key1, value1);
        noCache.put(key1, value1);
    }

    // ---------- put ----------
    @Benchmark
    public String putDefault() {
        return defaultCache.put(key2, value2);
    }

    @Benchmark
    public String putThreadSafe() {
        return threadSafeCache.put(key2, value2);
    }

    @Benchmark
    public String putWeak() {
        return weakCache.put(key2, value2);
    }

    @Benchmark
    public String putNoCache() {
        return noCache.put(key2, value2);
    }

    // ---------- get ----------
    @Benchmark
    public String getDefault() {
        return defaultCache.get(key1);
    }

    @Benchmark
    public String getThreadSafe() {
        return threadSafeCache.get(key1);
    }

    @Benchmark
    public String getWeak() {
        return weakCache.get(key1);
    }

    @Benchmark
    public String getNoCache() {
        return noCache.get(key1);
    }

    // ---------- get with mapping function ----------
    @Benchmark
    public String getOrComputeDefault(Blackhole bh) {
        String result = defaultCache.get(key2, k -> value2);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String getOrComputeThreadSafe(Blackhole bh) {
        String result = threadSafeCache.get(key2, k -> value2);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String getOrComputeWeak(Blackhole bh) {
        String result = weakCache.get(key2, k -> value2);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String getOrComputeNoCache(Blackhole bh) {
        String result = noCache.get(key2, k -> value2);
        bh.consume(result);
        return result;
    }

    // ---------- remove ----------
    @Benchmark
    public String removeDefault() {
        return defaultCache.remove(key2);
    }

    @Benchmark
    public String removeThreadSafe() {
        return threadSafeCache.remove(key2);
    }

    @Benchmark
    public String removeWeak() {
        return weakCache.remove(key2);
    }

    @Benchmark
    public String removeNoCache() {
        return noCache.remove(key2);
    }

    // ---------- clear ----------
    @Benchmark
    public void clearDefault(Blackhole bh) {
        defaultCache.clear();
        bh.consume(0);
    }

    @Benchmark
    public void clearThreadSafe(Blackhole bh) {
        threadSafeCache.clear();
        bh.consume(0);
    }

    @Benchmark
    public void clearWeak(Blackhole bh) {
        weakCache.clear();
        bh.consume(0);
    }

    @Benchmark
    public void clearNoCache(Blackhole bh) {
        noCache.clear();
        bh.consume(0);
    }

    // ---------- size ----------
    @Benchmark
    public int sizeDefault() {
        return defaultCache.size();
    }

    @Benchmark
    public int sizeThreadSafe() {
        return threadSafeCache.size();
    }

    @Benchmark
    public int sizeWeak() {
        return weakCache.size();
    }

    @Benchmark
    public int sizeNoCache() {
        return noCache.size();
    }

    // ---------- isEmpty ----------
    @Benchmark
    public boolean isEmptyDefault() {
        return defaultCache.isEmpty();
    }

    @Benchmark
    public boolean isEmptyThreadSafe() {
        return threadSafeCache.isEmpty();
    }

    @Benchmark
    public boolean isEmptyWeak() {
        return weakCache.isEmpty();
    }

    @Benchmark
    public boolean isEmptyNoCache() {
        return noCache.isEmpty();
    }

    // ---------- forEachValue ----------
    @Benchmark
    public void forEachDefault(Blackhole bh) {
        defaultCache.forEachValue(bh::consume);
    }

    @Benchmark
    public void forEachThreadSafe(Blackhole bh) {
        threadSafeCache.forEachValue(bh::consume);
    }

    @Benchmark
    public void forEachWeak(Blackhole bh) {
        weakCache.forEachValue(bh::consume);
    }

    @Benchmark
    public void forEachNoCache(Blackhole bh) {
        noCache.forEachValue(bh::consume);
    }
}

package bench.generated.c084;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.Set;
import jodd.util.TypeCache;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TypeCacheBenchmark {

    // --- State for Standard Cache (IdentityHashMap) ---
    @State(Scope.Benchmark)
    public static class StandardCacheState {
        private TypeCache<String> cache;
        private Class<String> testClass = String.class;
        private String testValue = "TestValue";

        @Setup(Level.Trial)
        public void setup() {
            // Standard cache: non-threadsafe, non-weak (IdentityHashMap)
            cache = TypeCache.<String>create().get();
        }

        @Benchmark
        public String putBenchmark(Blackhole bh) {
            String result = cache.put(testClass, testValue);
            bh.consume(result);
            return result;
        }

        @Benchmark
        public String getBenchmark(Blackhole bh) {
            String result = cache.get(testClass);
            bh.consume(result);
            return result;
        }

        @Benchmark
        public String removeBenchmark(Blackhole bh) {
            String result = cache.remove(testClass);
            bh.consume(result);
            return result;
        }

        @Benchmark
        public int sizeBenchmark(Blackhole bh) {
            int size = cache.size();
            bh.consume(size);
            return size;
        }

        @Benchmark
        public boolean isEmptyBenchmark(Blackhole bh) {
            boolean empty = cache.isEmpty();
            bh.consume(empty);
            return empty;
        }

        @Benchmark
        public String getWithFunctionBenchmark(Blackhole bh) {
            // Test get(Class<K> key, Function<Class<K>, ? extends T> mappingFunction)
            Function<Class<String>, String> mappingFunction = aClass -> "GeneratedValueFor" + aClass.getSimpleName();
            String result = cache.get(testClass, mappingFunction);
            bh.consume(result);
            return result;
        }

        @Benchmark
        public void forEachValueBenchmark(Blackhole bh) {
            // Test iteration
            cache.forEachValue(value -> {
                // Consume the value to prevent dead code elimination
                bh.consume(value);
            });
        }
    }

    // --- State for ThreadSafe Cache (ConcurrentHashMap) ---
    @State(Scope.Benchmark)
    public static class ThreadSafeCacheState {
        private TypeCache<String> cache;
        private Class<String> testClass = String.class;
        private String testValue = "ThreadSafeValue";

        @Setup(Level.Trial)
        public void setup() {
            // ThreadSafe cache: threadsafe, non-weak (ConcurrentHashMap)
            cache = TypeCache.<String>create().threadsafe(true).get();
        }

        @Benchmark
        public String putBenchmark(Blackhole bh) {
            String result = cache.put(testClass, testValue);
            bh.consume(result);
            return result;
        }

        @Benchmark
        public String getBenchmark(Blackhole bh) {
            String result = cache.get(testClass);
            bh.consume(result);
            return result;
        }

        @Benchmark
        public String removeBenchmark(Blackhole bh) {
            String result = cache.remove(testClass);
            bh.consume(result);
            return result;
        }

        @Benchmark
        public int sizeBenchmark(Blackhole bh) {
            int size = cache.size();
            bh.consume(size);
            return size;
        }
    }

    // --- State for Weak Cache (WeakHashMap) ---
    @State(Scope.Benchmark)
    public static class WeakCacheState {
        private TypeCache<String> cache;
        private Class<String> testClass = String.class;
        private String testValue = "WeakValue";

        @Setup(Level.Trial)
        public void setup() {
            // Weak cache: non-threadsafe, weak (WeakHashMap)
            cache = TypeCache.<String>create().weak(true).get();
        }

        @Benchmark
        public String putBenchmark(Blackhole bh) {
            String result = cache.put(testClass, testValue);
            bh.consume(result);
            return result;
        }

        @Benchmark
        public String getBenchmark(Blackhole bh) {
            String result = cache.get(testClass);
            bh.consume(result);
            return result;
        }

        @Benchmark
        public String removeBenchmark(Blackhole bh) {
            String result = cache.remove(testClass);
            bh.consume(result);
            return result;
        }

        @Benchmark
        public int sizeBenchmark(Blackhole bh) {
            int size = cache.size();
            bh.consume(size);
            return size;
        }
    }

    // --- State for ThreadSafe Weak Cache (Synchronized WeakHashMap) ---
    @State(Scope.Benchmark)
    public static class ThreadSafeWeakCacheState {
        private TypeCache<String> cache;
        private Class<String> testClass = String.class;
        private String testValue = "TSWeakValue";

        @Setup(Level.Trial)
        public void setup() {
            // ThreadSafe Weak cache: threadsafe, weak (Synchronized WeakHashMap)
            cache = TypeCache.<String>create().weak(true).threadsafe(true).get();
        }

        @Benchmark
        public String putBenchmark(Blackhole bh) {
            String result = cache.put(testClass, testValue);
            bh.consume(result);
            return result;
        }

        @Benchmark
        public String getBenchmark(Blackhole bh) {
            String result = cache.get(testClass);
            bh.consume(result);
            return result;
        }

        @Benchmark
        public String removeBenchmark(Blackhole bh) {
            String result = cache.remove(testClass);
            bh.consume(result);
            return result;
        }

        @Benchmark
        public int sizeBenchmark(Blackhole bh) {
            int size = cache.size();
            bh.consume(size);
            return size;
        }
    }

    // --- State for No Cache Implementation (AbstractMap) ---
    @State(Scope.Benchmark)
    public static class NoCacheState {
        private TypeCache<String> cache;
        private Class<String> testClass = String.class;
        private String testValue = "NoCacheValue";

        @Setup(Level.Trial)
        public void setup() {
            // No cache implementation
            cache = TypeCache.<String>create().noCache().get();
        }

        @Benchmark
        public String putBenchmark(Blackhole bh) {
            // Should return null/be ignored
            String result = cache.put(testClass, testValue);
            bh.consume(result);
            return result;
        }

        @Benchmark
        public String getBenchmark(Blackhole bh) {
            // Should return null
            String result = cache.get(testClass);
            bh.consume(result);
            return result;
        }

        @Benchmark
        public String removeBenchmark(Blackhole bh) {
            // Should return null/be ignored
            String result = cache.remove(testClass);
            bh.consume(result);
            return result;
        }

        @Benchmark
        public int sizeBenchmark(Blackhole bh) {
            // Should return 0
            int size = cache.size();
            bh.consume(size);
            return size;
        }
    }
}

package bench.generated.c072;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ClassLoader;
import java.lang.Class;
import java.util.concurrent.TimeUnit;
import java.util.jar.Manifest;
import jodd.util.ClassLoaderUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ClassLoaderUtilBenchmark {

    private File dummyFile;
    private String testClassName;
    private Class<?> testClass;
    private ClassLoader testClassLoader;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // 1. Setup dummy file input for classpath methods
        dummyFile = new File("dummy_classpath_item");

        // 2. Setup class inputs
        testClassName = "java.lang.String";
        testClass = String.class;
        testClassLoader = ClassLoader.getSystemClassLoader();
    }

    // --- Class Loader Retrieval Benchmarks ---

    @Benchmark
    public ClassLoader benchmarkGetDefaultClassLoader(Blackhole bh) {
        ClassLoader cl = ClassLoaderUtil.getDefaultClassLoader();
        bh.consume(cl);
        return cl;
    }

    @Benchmark
    public ClassLoader benchmarkGetContextClassLoader(Blackhole bh) {
        ClassLoader cl = ClassLoaderUtil.getContextClassLoader();
        bh.consume(cl);
        return cl;
    }

    @Benchmark
    public ClassLoader benchmarkGetSystemClassLoader(Blackhole bh) {
        ClassLoader cl = ClassLoaderUtil.getSystemClassLoader();
        bh.consume(cl);
        return cl;
    }

    // --- Classpath Item Benchmarks ---

    @Benchmark
    public Manifest benchmarkGetClasspathItemManifest(Blackhole bh) {
        Manifest manifest = ClassLoaderUtil.getClasspathItemManifest(dummyFile);
        bh.consume(manifest);
        return manifest;
    }

    @Benchmark
    public String benchmarkGetClasspathItemBaseDir(Blackhole bh) {
        String baseDir = ClassLoaderUtil.getClasspathItemBaseDir(dummyFile);
        bh.consume(baseDir);
        return baseDir;
    }

    // --- Class Stream Benchmarks ---

    @Benchmark
    public InputStream benchmarkGetClassAsStreamByClass(Blackhole bh) throws IOException {
        InputStream is = ClassLoaderUtil.getClassAsStream(testClass);
        bh.consume(is);
        return is;
    }

    @Benchmark
    public InputStream benchmarkGetClassAsStreamByString(Blackhole bh) throws IOException {
        InputStream is = ClassLoaderUtil.getClassAsStream(testClassName);
        bh.consume(is);
        return is;
    }

    @Benchmark
    public InputStream benchmarkGetClassAsStreamByStringAndLoader(Blackhole bh) throws IOException {
        InputStream is = ClassLoaderUtil.getClassAsStream(testClassName, testClassLoader);
        bh.consume(is);
        return is;
    }

    // --- Class Loading Benchmarks ---

    @Benchmark
    public Class benchmarkLoadClassByString(Blackhole bh) throws ClassNotFoundException {
        Class<?> clazz = ClassLoaderUtil.loadClass(testClassName);
        bh.consume(clazz);
        return clazz;
    }

    @Benchmark
    public Class benchmarkLoadClassByStringAndLoader(Blackhole bh) throws ClassNotFoundException {
        Class<?> clazz = ClassLoaderUtil.loadClass(testClassName, testClassLoader);
        bh.consume(clazz);
        return clazz;
    }

    // --- Class Location Benchmark ---

    @Benchmark
    public String benchmarkClassLocation(Blackhole bh) {
        String location = ClassLoaderUtil.classLocation(testClass);
        bh.consume(location);
        return location;
    }
}

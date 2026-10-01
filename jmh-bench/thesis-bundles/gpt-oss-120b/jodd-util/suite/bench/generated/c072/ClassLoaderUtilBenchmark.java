package bench.generated.c072;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.util.ClassLoaderUtil;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ClassLoaderUtilBenchmark {

    private String className;
    private Class<?> clazz;
    private ClassLoader defaultClassLoader;
    private File dummyFile;

    @Setup(Level.Trial)
    public void setup() {
        className = "java.lang.String";
        clazz = String.class;
        defaultClassLoader = ClassLoaderUtil.getDefaultClassLoader();
        dummyFile = new File("/tmp");
    }

    @Benchmark
    public ClassLoader benchmarkGetDefaultClassLoader() {
        return ClassLoaderUtil.getDefaultClassLoader();
    }

    @Benchmark
    public ClassLoader benchmarkGetContextClassLoader() {
        return ClassLoaderUtil.getContextClassLoader();
    }

    @Benchmark
    public ClassLoader benchmarkGetSystemClassLoader() {
        return ClassLoaderUtil.getSystemClassLoader();
    }

    @Benchmark
    public String benchmarkGetClasspathItemBaseDir() {
        return ClassLoaderUtil.getClasspathItemBaseDir(dummyFile);
    }

    @Benchmark
    public InputStream benchmarkGetClassAsStreamByClass() throws IOException {
        return ClassLoaderUtil.getClassAsStream(clazz);
    }

    @Benchmark
    public InputStream benchmarkGetClassAsStreamByName() throws IOException {
        return ClassLoaderUtil.getClassAsStream(className);
    }

    @Benchmark
    public InputStream benchmarkGetClassAsStreamByNameAndLoader() throws IOException {
        return ClassLoaderUtil.getClassAsStream(className, defaultClassLoader);
    }

    @Benchmark
    public Class<?> benchmarkLoadClassByName() throws ClassNotFoundException {
        return ClassLoaderUtil.loadClass(className);
    }

    @Benchmark
    public Class<?> benchmarkLoadClassByNameAndLoader() throws ClassNotFoundException {
        return ClassLoaderUtil.loadClass(className, defaultClassLoader);
    }

    @Benchmark
    public String benchmarkClassLocation() {
        return ClassLoaderUtil.classLocation(clazz);
    }
}

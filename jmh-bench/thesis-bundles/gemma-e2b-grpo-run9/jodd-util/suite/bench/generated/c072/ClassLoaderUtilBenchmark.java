package bench.generated.c072;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import jodd.util.ClassLoaderUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ClassLoaderUtilBenchmark {

    // Since ClassLoaderUtil methods are static, no instance state is required.

    @Benchmark
    public void benchmarkDefaultClassLoader(Blackhole bh) {
        try {
            ClassLoaderUtil.getDefaultClassLoader();
        } catch (Exception e) {
            // Ignore exceptions for benchmarking purposes
        }
    }

    @Benchmark
    public void benchmarkGetContextClassLoader(Blackhole bh) {
        try {
            ClassLoaderUtil.getContextClassLoader();
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkGetSystemClassLoader(Blackhole bh) {
        try {
            ClassLoaderUtil.getSystemClassLoader();
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkClassLocation(Blackhole bh) {
        try {
            // Use a standard class to get a valid Class object
            // This call relies on reflection/security context, which is fine for benchmarking.
            Class<?> clazz = String.class;
            String location = ClassLoaderUtil.classLocation(clazz);
            bh.consume(location);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkLoadClass(Blackhole bh) {
        try {
            // Attempt to load a simple class. This is CPU bound and relies on the class being available.
            ClassLoaderUtil.loadClass("java.lang.String");
        } catch (Exception e) {
            // Ignore ClassNotFoundException or other IOExceptions that might occur during loading
        }
    }

    @Benchmark
    public void benchmarkClassStreamByClassName(Blackhole bh) {
        try {
            // Attempt to open a stream for a known class
            ClassLoaderUtil.getClassAsStream("java.lang.String");
        } catch (Exception e) {
            // Ignore IOException
        }
    }

    @Benchmark
    public void benchmarkClassStreamByClassNameWithLoader(Blackhole bh) {
        try {
            // Attempt to open a stream with a null loader (relying on default behavior)
            ClassLoaderUtil.getClassAsStream("java.lang.String", null);
        } catch (Exception e) {
            // Ignore IOException
        }
    }

    @Benchmark
    public void benchmarkClasspathItemBaseDir(Blackhole bh) {
        try {
            // Create a dummy file object. This tests the path manipulation logic.
            // Note: This method relies on File operations, which might be slow or fail
            // if the path is invalid, but it tests the logic path.
            java.io.File dummyFile = new java.io.File("/path/to/some/resource.jar");
            String result = ClassLoaderUtil.getClasspathItemBaseDir(dummyFile);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkClasspathItemManifest(Blackhole bh) {
        // This method involves file I/O (FileInputStream, JarFile).
        // It is included to test the path where I/O is involved.
        try {
            // Use a dummy file that likely won't exist or be readable, testing exception handling path.
            java.io.File dummyFile = new java.io.File("/non/existent/path/file.jar");
            ClassLoaderUtil.getClasspathItemManifest(dummyFile);
        } catch (Exception e) {
            // Expected to fail if files are missing, which is acceptable for this test structure.
        }
    }
}

package bench.generated.c072;

import jodd.util.ClassLoaderUtil;
import org.openjdk.jmh.annotations.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ClassLoaderUtilBenchmark {

    private String className;
    private ClassLoader classLoader;
    private File tempJar;
    private File tempFile;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        className = "java.lang.String";
        classLoader = getClass().getClassLoader();

        tempFile = File.createTempFile("classloader-util-bench", ".tmp");
        tempFile.deleteOnExit();

        tempJar = File.createTempFile("classloader-util-bench", ".jar");
        tempJar.deleteOnExit();
        try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(tempJar))) {
            jos.putNextEntry(new JarEntry("META-INF/MANIFEST.MF"));
            jos.write("Manifest-Version: 1.0\r\n".getBytes(StandardCharsets.UTF_8));
            jos.closeEntry();
        }
    }

    @Benchmark
    public ClassLoader getDefaultClassLoader() {
        return ClassLoaderUtil.getDefaultClassLoader();
    }

    @Benchmark
    public ClassLoader getContextClassLoader() {
        return ClassLoaderUtil.getContextClassLoader();
    }

    @Benchmark
    public ClassLoader getSystemClassLoader() {
        return ClassLoaderUtil.getSystemClassLoader();
    }

    @Benchmark
    public Class<?> loadClass() throws ClassNotFoundException {
        return ClassLoaderUtil.loadClass(className);
    }

    @Benchmark
    public Class<?> loadClassWithClassLoader() throws ClassNotFoundException {
        return ClassLoaderUtil.loadClass(className, classLoader);
    }

    @Benchmark
    public int getClassAsStreamByClass() throws IOException {
        try (InputStream in = ClassLoaderUtil.getClassAsStream(String.class)) {
            return readAll(in);
        }
    }

    @Benchmark
    public int getClassAsStreamByName() throws IOException {
        try (InputStream in = ClassLoaderUtil.getClassAsStream(className)) {
            return readAll(in);
        }
    }

    @Benchmark
    public int getClassAsStreamByNameAndClassLoader() throws IOException {
        try (InputStream in = ClassLoaderUtil.getClassAsStream(className, classLoader)) {
            return readAll(in);
        }
    }

    @Benchmark
    public String classLocation() {
        return ClassLoaderUtil.classLocation(ClassLoaderUtil.class);
    }

    @Benchmark
    public String getClasspathItemBaseDir() {
        return ClassLoaderUtil.getClasspathItemBaseDir(tempFile);
    }

    @Benchmark
    public Object getClasspathItemManifest() {
        return ClassLoaderUtil.getClasspathItemManifest(tempJar);
    }

    private static int readAll(InputStream in) throws IOException {
        if (in == null) {
            return -1;
        }
        byte[] buffer = new byte[256];
        int total = 0;
        int read;
        while ((read = in.read(buffer)) != -1) {
            total += read;
        }
        return total;
    }
}

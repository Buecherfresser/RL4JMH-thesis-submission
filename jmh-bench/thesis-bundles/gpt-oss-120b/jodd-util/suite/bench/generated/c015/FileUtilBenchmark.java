package bench.generated.c015;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.io.FileUtil;
import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.net.MalformedURLException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FileUtilBenchmark {

    private File simpleFile;
    private URL fileUrl;
    private File ancestorFile;
    private File descendantFile;
    private FileFilter acceptAllFilter;

    @Setup(Level.Trial)
    public void setUp() throws MalformedURLException {
        // Simple file for URL conversion
        simpleFile = new File("simple.txt");

        // URL with file protocol
        fileUrl = new URL("file:/tmp/benchmark.txt");

        // Files for ancestor checks
        ancestorFile = new File("/tmp");
        descendantFile = new File("/tmp/subdir/file.txt");

        // Accept-all filter
        acceptAllFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return true;
            }
        };
    }

    @Benchmark
    public File benchFileResolve() {
        // Resolve home directory placeholder
        return FileUtil.file("~/benchmark.txt");
    }

    @Benchmark
    public URL benchToURL() throws MalformedURLException {
        // Convert File to URL
        return FileUtil.toURL(simpleFile);
    }

    @Benchmark
    public File benchToFile() throws MalformedURLException {
        // Convert URL to File
        return FileUtil.toFile(fileUrl);
    }

    @Benchmark
    public String benchToFileName() throws MalformedURLException {
        // Extract file name from URL
        return FileUtil.toFileName(fileUrl);
    }

    @Benchmark
    public boolean benchIsAncestor() {
        // Check ancestor relationship (strict)
        return FileUtil.isAncestor(ancestorFile, descendantFile, true);
    }

    @Benchmark
    public File benchGetParentFile() {
        // Get parent file handling "." and ".."
        return FileUtil.getParentFile(descendantFile);
    }

    @Benchmark
    public boolean benchIsFilePathAcceptable() {
        // Verify file path against filter
        return FileUtil.isFilePathAcceptable(descendantFile, acceptAllFilter);
    }
}

package bench.generated.c046;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import java.io.File;
import jodd.typeconverter.impl.FileConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FileConverterBenchmark {

    private FileConverter converter;
    private byte[] byteArray;
    private String string;
    private File file;

    @Setup(Level.Trial)
    public void setup() {
        converter = new FileConverter();
        byteArray = "some test content".getBytes();
        string = "some test content";
        file = new File("test.txt"); // dummy file, not actually created
    }

    @Benchmark
    public File convertNull() {
        return converter.convert(null);
    }

    @Benchmark
    public File convertFile() {
        return converter.convert(file);
    }

    @Benchmark
    public File convertByteArray() {
        return converter.convert(byteArray);
    }

    @Benchmark
    public File convertString() {
        return converter.convert(string);
    }
}

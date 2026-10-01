package bench.generated.c046;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import jodd.typeconverter.impl.FileConverter;
import jodd.typeconverter.TypeConversionException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FileConverterBenchmark {

    private FileConverter fileConverter;

    @Setup
    public void setup() {
        // Initialize the converter instance. Since it's stateless, this is safe.
        this.fileConverter = new FileConverter();
    }

    @Benchmark
    public void testNullInput(Blackhole bh) {
        try {
            // Test null handling (fast path)
            File result = fileConverter.convert(null);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testFileInput(Blackhole bh) {
        try {
            // Test File input handling (fast path)
            // We create a dummy File object. Actual IO is skipped by the FileConverter logic
            // if it doesn't perform actual disk operations based on the provided source.
            File dummyFile = new File("temp.txt");
            File result = fileConverter.convert(dummyFile);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testByteArrayInput(Blackhole bh) {
        // Test byte array conversion (IO path)
        try {
            // Create a non-static final byte array input
            byte[] inputBytes = new byte[1024];
            // Fill it with some data
            for (int i = 0; i < inputBytes.length; i++) {
                inputBytes[i] = (byte) i;
            }

            // This call is expected to perform IO operations (createTempFile, writeBytes)
            File result = fileConverter.convert(inputBytes);
            bh.consume(result);
        } catch (Exception e) {
            // Catch potential IOExceptions or TypeConversionExceptions thrown by FileConverter
        }
    }

    @Benchmark
    public void testStringInput(Blackhole bh) {
        // Test String conversion (IO path)
        try {
            // Create a non-static final String input
            String inputString = "This is a test string for file conversion.";

            // This call is expected to perform IO operations (createTempFile, writeString)
            File result = fileConverter.convert(inputString);
            bh.consume(result);
        } catch (Exception e) {
            // Catch potential IOExceptions or TypeConversionExceptions thrown by FileConverter
        }
    }
}

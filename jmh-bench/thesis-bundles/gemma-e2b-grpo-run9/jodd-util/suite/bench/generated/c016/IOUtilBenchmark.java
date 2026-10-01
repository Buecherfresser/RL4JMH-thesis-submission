package bench.generated.c016;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import jodd.io.IOUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IOUtilBenchmark {

    // --- Benchmark Methods ---

    @Benchmark
    public void copyBytes_ReaderToWriter(Blackhole bh) {
        try {
            // Setup: Create a simple Reader and Writer
            String inputStr = "Hello World, this is a test string for IOUtil copy.";
            java.io.Reader input = new java.io.StringReader(inputStr);
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            
            // Call the static method
            IOUtil.copy(input, baos);
            
            // Consume the result
            bh.consume(baos);
        } catch (IOException e) {
            // Ignore exceptions for benchmark stability
        }
    }

    @Benchmark
    public void copyBytes_InputStreamToOutputStream(Blackhole bh) {
        try {
            // Setup: Create a simple InputStream
            byte[] data = "Test data for IOUtil stream copy.".getBytes(StandardCharsets.UTF_8);
            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(data);
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();

            // Call the static method
            IOUtil.copy(bais, baos);
            
            // Consume the result
            bh.consume(baos);
        } catch (IOException e) {
            // Ignore
        }
    }

    @Benchmark
    public void copyChars_ReaderToCharArray(Blackhole bh) {
        try {
            // Setup: Create a simple Reader
            String inputStr = "ABCDEFGHIJ";
            java.io.Reader input = new java.io.StringReader(inputStr);
            
            // Call the static method
            IOUtil.readChars(input);
            
            // Void method, consume null
            bh.consume(null); 
        } catch (IOException e) {
            // Ignore
        }
    }

    @Benchmark
    public void copyChars_InputStreamToCharArray(Blackhole bh) {
        try {
            // Setup: Create a simple InputStream
            byte[] data = "ABCDEFGHIJ".getBytes(StandardCharsets.UTF_8);
            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(data);

            // Call the static method
            IOUtil.readChars(bais);
            
            // Void method, consume null
            bh.consume(null);
        } catch (IOException e) {
            // Ignore
        }
    }

    @Benchmark
    public void compareBytes_Equal(Blackhole bh) {
        try {
            // Setup: Create two identical byte streams
            byte[] data1 = "Test data".getBytes(StandardCharsets.UTF_8);
            byte[] data2 = "Test data".getBytes(StandardCharsets.UTF_8);
            
            java.io.ByteArrayInputStream is1 = new java.io.ByteArrayInputStream(data1);
            java.io.ByteArrayInputStream is2 = new java.io.ByteArrayInputStream(data2);

            // Call the static method
            boolean result = IOUtil.compare(is1, is2);
            bh.consume(result);
        } catch (IOException e) {
            // Ignore
        }
    }

    @Benchmark
    public void compareBytes_Unequal(Blackhole bh) {
        try {
            // Setup: Create two different byte streams
            byte[] data1 = "Test data".getBytes(StandardCharsets.UTF_8);
            byte[] data2 = "Different data".getBytes(StandardCharsets.UTF_8);
            
            java.io.ByteArrayInputStream is1 = new java.io.ByteArrayInputStream(data1);
            java.io.ByteArrayInputStream is2 = new java.io.ByteArrayInputStream(data2);

            // Call the static method
            boolean result = IOUtil.compare(is1, is2);
            bh.consume(result);
        } catch (IOException e) {
            // Ignore
        }
    }

    @Benchmark
    public void compareChars_Equal(Blackhole bh) {
        try {
            // Setup: Create two identical Readers
            String inputStr1 = "ABCDEFGHIJ";
            String inputStr2 = "ABCDEFGHIJ";
            
            java.io.Reader r1 = new java.io.StringReader(inputStr1);
            java.io.Reader r2 = new java.io.StringReader(inputStr2);

            // Call the static method
            boolean result = IOUtil.compare(r1, r2);
            bh.consume(result);
        } catch (IOException e) {
            // Ignore
        }
    }

    @Benchmark
    public void compareChars_Unequal(Blackhole bh) {
        try {
            // Setup: Create two different Readers
            String inputStr1 = "ABCDEFGHIJ";
            String inputStr2 = "ABCDEFGHIK"; // Different length
            
            java.io.Reader r1 = new java.io.StringReader(inputStr1);
            java.io.Reader r2 = new java.io.StringReader(inputStr2);

            // Call the static method
            boolean result = IOUtil.compare(r1, r2);
            bh.consume(result);
        } catch (IOException e) {
            // Ignore
        }
    }
}

package bench.generated.c075;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jodd.util.CsvUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CsvUtilBenchmark {

    // --- Setup Data ---

    // Input for toCsvString: An array of objects (Strings in this case)
    private String[] csvInputData;

    // Input for toStringArray: A complex CSV string
    private String complexCsvLine;

    // --- Setup Method ---

    @Setup
    public void setup() {
        // 1. Setup data for toCsvString (Object array)
        csvInputData = new String[]{
                "Name,Age,City",
                "Alice,30,\"New York\"",
                "Bob,25,London",
                null, // Test null handling
                "Charlie,40,Paris"
        };

        // 2. Setup data for toStringArray (Complex CSV line)
        // This line includes quoting, commas within fields, and special characters (simulated)
        complexCsvLine = "ID,\"Product Name, with comma\",100,\"Description with \r\n newline\"";
    }

    // --- Benchmarks for toCsvString (Encoding) ---

    @Benchmark
    public void testToCsvString_SimpleData(Blackhole bh) {
        String result = CsvUtil.toCsvString(csvInputData);
        bh.consume(result);
    }

    @Benchmark
    public void testToCsvString_ComplexData(Blackhole bh) {
        String result = CsvUtil.toCsvString(csvInputData);
        bh.consume(result);
    }

    // --- Benchmarks for toStringArray (Decoding) ---

    @Benchmark
    public void testToStringArray_ComplexLine(Blackhole bh) {
        String[] result = CsvUtil.toStringArray(complexCsvLine);
        bh.consume(result);
    }

    @Benchmark
    public void testToStringArray_EmptyLine(Blackhole bh) {
        String emptyLine = "";
        String[] result = CsvUtil.toStringArray(emptyLine);
        bh.consume(result);
    }
}

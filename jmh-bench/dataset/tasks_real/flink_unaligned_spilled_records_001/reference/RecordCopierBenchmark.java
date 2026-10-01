package bench;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class RecordCopierBenchmark {

    @Param({"256"})
    public int records;

    @Param({"128"})
    public int recordSize;

    private byte[][] input;
    private int total;

    @Setup
    public void setup() {
        input = new byte[records][recordSize];
        total = records * recordSize;
        for (int i = 0; i < records; i++)
            for (int j = 0; j < recordSize; j++)
                input[i][j] = (byte) ((i + j) & 0xff);
    }

    @Benchmark
    public void copyAll(Blackhole bh) {
        bh.consume(RecordCopier.copyAll(input, total));
    }
}

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
public class BufferAssemblerBenchmark {

    @Param({"64"})
    public int slices;

    @Param({"256"})
    public int sliceSize;

    private byte[][] input;

    @Setup
    public void setup() {
        input = new byte[slices][sliceSize];
        for (int i = 0; i < slices; i++)
            for (int j = 0; j < sliceSize; j++)
                input[i][j] = (byte) ((i * 31 + j) & 0xff);
    }

    @Benchmark
    public void assemble(Blackhole bh) {
        bh.consume(BufferAssembler.assemble(input));
    }
}

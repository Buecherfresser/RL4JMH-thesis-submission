package bench.generated.c012;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.fastfilter.gcs.BitBuffer;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BitBufferBenchmark {

    @State(Scope.Benchmark)
    public static class BenchState {
        BitBuffer writeBuffer;
        BitBuffer readBitBuffer;
        BitBuffer readNumberBuffer;
        BitBuffer readEliasBuffer;
        BitBuffer skipGolombBuffer;
        BitBuffer zerosBuffer;
        BitBuffer mergeTarget;
        BitBuffer mergeSource;
        int mergeSourceLength;
        long bitValue;
        long signedValue;
        long positiveValue;
        int shift = 2;
        int bitCount = 32;
        long numberValue;
        int readPos = 0;

        @Setup(Level.Trial)
        public void setup() {
            Random rand = new Random(12345);
            writeBuffer = new BitBuffer(1024);
            bitValue = 1;
            signedValue = rand.nextLong();
            positiveValue = (rand.nextLong() & Long.MAX_VALUE) + 1;
            numberValue = rand.nextLong() & ((1L << 32) - 1);

            // Random bits for readBit
            readBitBuffer = new BitBuffer(1024);
            for (int i = 0; i < 16; i++) {
                readBitBuffer.writeNumber(rand.nextLong(), 64);
            }

            // Random 32-bit numbers for readNumber
            readNumberBuffer = new BitBuffer(1024);
            for (int i = 0; i < 32; i++) {
                readNumberBuffer.writeNumber(rand.nextLong() & ((1L << 32) - 1), 32);
            }

            // Elias‑delta encoded values
            readEliasBuffer = new BitBuffer(1024);
            for (int i = 0; i < 20; i++) {
                long val = (rand.nextLong() & Long.MAX_VALUE) + 1;
                readEliasBuffer.writeEliasDelta(val);
            }

            // Golomb‑Rice encoded values
            skipGolombBuffer = new BitBuffer(1024);
            for (int i = 0; i < 20; i++) {
                long val = rand.nextLong() & ((1L << 20) - 1);
                skipGolombBuffer.writeGolombRice(shift, val);
            }

            // Pattern: 1023 ones followed by a zero
            zerosBuffer = new BitBuffer(1024);
            for (int i = 0; i < 1023; i++) {
                zerosBuffer.writeBit(1);
            }
            zerosBuffer.writeBit(0);

            // Buffers for the write(BitBuffer) operation
            mergeTarget = new BitBuffer(1024);
            mergeSource = new BitBuffer(64);
            mergeSource.writeNumber(rand.nextLong(), 64);
            mergeSourceLength = mergeSource.position();
        }

        @Setup(Level.Invocation)
        public void reset() {
            writeBuffer.clear();
            writeBuffer.seek(0);
            readBitBuffer.seek(0);
            readNumberBuffer.seek(0);
            readEliasBuffer.seek(0);
            skipGolombBuffer.seek(0);
            zerosBuffer.seek(0);
            mergeTarget.seek(0);
            mergeSource.seek(mergeSourceLength);
        }
    }

    @Benchmark
    public void writeBit(BenchState s, Blackhole bh) {
        s.writeBuffer.writeBit(s.bitValue);
        bh.consume(s.writeBuffer.position());
    }

    @Benchmark
    public long readBit(BenchState s) {
        return s.readBitBuffer.readBit();
    }

    @Benchmark
    public void writeNumber(BenchState s, Blackhole bh) {
        s.writeBuffer.writeNumber(s.numberValue, s.bitCount);
        bh.consume(s.writeBuffer.position());
    }

    @Benchmark
    public long readNumber(BenchState s) {
        return s.readNumberBuffer.readNumber(s.bitCount);
    }

    @Benchmark
    public long readNumberAtPos(BenchState s) {
        return s.readNumberBuffer.readNumber(s.readPos, s.bitCount);
    }

    @Benchmark
    public void writeGolombRice(BenchState s, Blackhole bh) {
        s.writeBuffer.writeGolombRice(s.shift, s.numberValue);
        bh.consume(s.writeBuffer.position());
    }

    @Benchmark
    public void writeGolombRiceFast(BenchState s, Blackhole bh) {
        s.writeBuffer.writeGolombRiceFast(s.shift, s.numberValue);
        bh.consume(s.writeBuffer.position());
    }

    @Benchmark
    public void skipGolombRice(BenchState s, Blackhole bh) {
        s.skipGolombBuffer.skipGolombRice(s.shift);
        bh.consume(s.skipGolombBuffer.position());
    }

    @Benchmark
    public int skipGolombRiceAtPos(BenchState s, Blackhole bh) {
        int newPos = s.skipGolombBuffer.skipGolombRice(0, s.shift);
        bh.consume(newPos);
        return newPos;
    }

    @Benchmark
    public void writeEliasDelta(BenchState s, Blackhole bh) {
        s.writeBuffer.writeEliasDelta(s.positiveValue);
        bh.consume(s.writeBuffer.position());
    }

    @Benchmark
    public long readEliasDelta(BenchState s) {
        return s.readEliasBuffer.readEliasDelta();
    }

    @Benchmark
    public long foldSigned(BenchState s) {
        return BitBuffer.foldSigned(s.signedValue);
    }

    @Benchmark
    public long unfoldSigned(BenchState s) {
        return BitBuffer.unfoldSigned(s.signedValue);
    }

    @Benchmark
    public int getEliasDeltaSize(BenchState s) {
        return BitBuffer.getEliasDeltaSize(s.positiveValue);
    }

    @Benchmark
    public int readUntilZero(BenchState s) {
        return s.zerosBuffer.readUntilZero(0);
    }

    @Benchmark
    public void writeBuffer(BenchState s, Blackhole bh) {
        s.mergeTarget.write(s.mergeSource);
        bh.consume(s.mergeTarget.position());
    }
}

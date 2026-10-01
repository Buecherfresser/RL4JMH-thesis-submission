package bench.generated.c019;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import com.carrotsearch.hppc.DoubleArrayList;
import com.carrotsearch.hppc.procedures.DoubleProcedure;
import com.carrotsearch.hppc.predicates.DoublePredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DoubleArrayListBenchmark {

    private double[] baselineData;
    private DoubleArrayList readOnlyList;
    private DoubleArrayList mutableList;

    private double addValue;
    private double addValue2;
    private double[] addArray;
    private int insertIndex;
    private int getIndex;
    private int setIndex;
    private double setValue;
    private int removeIndex;
    private int rangeSize;
    private double searchValue;
    private int newSize;

    private DoubleProcedure dummyProcedure;
    private DoublePredicate alwaysTruePredicate;
    private DoublePredicate removePredicate;

    @Setup(Level.Trial)
    public void trialSetup() {
        int size = 1024;
        Random rnd = new Random(12345);
        baselineData = new double[size];
        for (int i = 0; i < size; i++) {
            baselineData[i] = rnd.nextDouble();
        }

        readOnlyList = new DoubleArrayList(size);
        for (double v : baselineData) {
            readOnlyList.add(v);
        }

        addValue = rnd.nextDouble();
        addValue2 = rnd.nextDouble();
        addArray = new double[] { addValue, addValue2 };
        insertIndex = size / 2;
        getIndex = size / 2;
        setIndex = size / 2;
        setValue = rnd.nextDouble();
        removeIndex = size / 2;
        rangeSize = size / 4;
        searchValue = baselineData[0];
        newSize = size / 2;

        dummyProcedure = new DoubleProcedure() {
            @Override
            public void apply(double value) {
                // no‑op
            }
        };
        alwaysTruePredicate = new DoublePredicate() {
            @Override
            public boolean apply(double value) {
                return true;
            }
        };
        removePredicate = new DoublePredicate() {
            @Override
            public boolean apply(double value) {
                return value > 0.5;
            }
        };
    }

    @Setup(Level.Invocation)
    public void invocationSetup() {
        mutableList = new DoubleArrayList(baselineData.length);
        mutableList.add(baselineData);
    }

    @Benchmark
    public void addSingle(Blackhole bh) {
        mutableList.add(addValue);
        bh.consume(mutableList);
    }

    @Benchmark
    public void addPair(Blackhole bh) {
        mutableList.add(addValue, addValue2);
        bh.consume(mutableList);
    }

    @Benchmark
    public void addVarargs(Blackhole bh) {
        mutableList.add(addArray);
        bh.consume(mutableList);
    }

    @Benchmark
    public int addAllFromContainer() {
        return mutableList.addAll(readOnlyList);
    }

    @Benchmark
    public void insert(Blackhole bh) {
        mutableList.insert(insertIndex, addValue);
        bh.consume(mutableList);
    }

    @Benchmark
    public double set(Blackhole bh) {
        return mutableList.set(setIndex, setValue);
    }

    @Benchmark
    public double removeAt(Blackhole bh) {
        return mutableList.removeAt(removeIndex);
    }

    @Benchmark
    public double removeLast(Blackhole bh) {
        return mutableList.removeLast();
    }

    @Benchmark
    public void removeRange(Blackhole bh) {
        mutableList.removeRange(0, rangeSize);
        bh.consume(mutableList);
    }

    @Benchmark
    public void clear(Blackhole bh) {
        mutableList.clear();
        bh.consume(mutableList);
    }

    @Benchmark
    public void release(Blackhole bh) {
        mutableList.release();
        bh.consume(mutableList);
    }

    @Benchmark
    public void resize(Blackhole bh) {
        mutableList.resize(newSize);
        bh.consume(mutableList);
    }

    @Benchmark
    public void trimToSize(Blackhole bh) {
        mutableList.trimToSize();
        bh.consume(mutableList);
    }

    @Benchmark
    public DoubleArrayList sort() {
        mutableList.sort();
        return mutableList;
    }

    @Benchmark
    public DoubleArrayList reverse() {
        mutableList.reverse();
        return mutableList;
    }

    @Benchmark
    public int removeAllPredicate() {
        return mutableList.removeAll(removePredicate);
    }

    @Benchmark
    public DoubleProcedure forEachProcedure() {
        return mutableList.forEach(dummyProcedure);
    }

    @Benchmark
    public DoublePredicate forEachPredicate() {
        return mutableList.forEach(alwaysTruePredicate);
    }

    @Benchmark
    public double get() {
        return readOnlyList.get(getIndex);
    }

    @Benchmark
    public boolean contains() {
        return readOnlyList.contains(searchValue);
    }

    @Benchmark
    public int indexOf() {
        return readOnlyList.indexOf(searchValue);
    }

    @Benchmark
    public int lastIndexOf() {
        return readOnlyList.lastIndexOf(searchValue);
    }

    @Benchmark
    public int size() {
        return readOnlyList.size();
    }

    @Benchmark
    public double[] toArray(Blackhole bh) {
        double[] arr = readOnlyList.toArray();
        bh.consume(arr);
        return arr;
    }

    @Benchmark
    public int hashCodeBenchmark() {
        return readOnlyList.hashCode();
    }

    @Benchmark
    public boolean equalsBenchmark() {
        DoubleArrayList clone = readOnlyList.clone();
        return readOnlyList.equals(clone);
    }

    @Benchmark
    public DoubleArrayList cloneBenchmark() {
        return readOnlyList.clone();
    }

    @Benchmark
    public DoubleArrayList iteratorBenchmark(Blackhole bh) {
        bh.consume(readOnlyList.iterator());
        return readOnlyList;
    }
}

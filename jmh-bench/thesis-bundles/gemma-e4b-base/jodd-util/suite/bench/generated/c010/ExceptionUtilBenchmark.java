package bench.generated.c010;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.sql.SQLException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import jodd.exception.ExceptionUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ExceptionUtilBenchmark {

    // --- Test Data Fixtures ---

    // 1. Throwable Chain for testing chain traversal methods
    private Throwable testThrowableChain;
    private Throwable rootCause;

    // 2. Standard Throwable for stack trace filtering
    private Throwable standardThrowable;

    // 3. Filter arrays
    private String[] allowFilters;
    private String[] denyFilters;

    // 4. SQL Exception collection
    private Collection<SQLException> sqlExceptions;

    // 5. Message building inputs
    private String testMessage;
    private Throwable testCauseForMessage;

    @Setup(Level.Trial)
    public void setup() {
        // --- 1. Throwable Chain Setup ---
        // Create a chain: T1 -> T2 -> T3 (Root)
        Throwable t3 = new RuntimeException("Root Cause");
        Throwable t2 = new RuntimeException("Middle Cause");
        t2.initCause(t3);
        Throwable t1 = new RuntimeException("Top Level Exception");
        t1.initCause(t2);

        this.testThrowableChain = t1;
        this.rootCause = t3;

        // --- 2. Standard Throwable Setup ---
        this.standardThrowable = new RuntimeException("Standard Test");

        // --- 3. Filter Arrays Setup ---
        this.allowFilters = new String[]{"com.jodd", "java.util"};
        this.denyFilters = new String[]{"com.sun", "jdk"};

        // --- 4. SQL Exception Setup ---
        List<SQLException> sqlList = new ArrayList<>();
        sqlList.add(new SQLException("SQL Error 1"));
        SQLException s2 = new SQLException("SQL Error 2");
        sqlList.add(s2);
        SQLException s3 = new SQLException("SQL Error 3");
        sqlList.add(s3);
        this.sqlExceptions = sqlList;

        // --- 5. Message Building Setup ---
        this.testMessage = "Operation failed";
        this.testCauseForMessage = t1;
    }

    // =================================================================
    // Stack Trace Methods
    // =================================================================

    @Benchmark
    public void getCurrentStackTraceBenchmark(Blackhole bh) {
        StackTraceElement[] result = ExceptionUtil.getCurrentStackTrace();
        bh.consume(result);
    }

    @Benchmark
    public void getStackTraceFilteredBenchmark(Blackhole bh) {
        StackTraceElement[] result = ExceptionUtil.getStackTrace(standardThrowable, allowFilters, denyFilters);
        bh.consume(result);
    }

    @Benchmark
    public void getStackTraceChainFilteredBenchmark(Blackhole bh) {
        StackTraceElement[][] result = ExceptionUtil.getStackTraceChain(standardThrowable, allowFilters, denyFilters);
        bh.consume(result);
    }

    // =================================================================
    // Exception Chain Methods
    // =================================================================

    @Benchmark
    public void getExceptionChainBenchmark(Blackhole bh) {
        Throwable[] result = ExceptionUtil.getExceptionChain(testThrowableChain);
        bh.consume(result);
    }

    @Benchmark
    public void getRootCauseBenchmark(Blackhole bh) {
        Throwable result = ExceptionUtil.getRootCause(testThrowableChain);
        bh.consume(result);
    }

    @Benchmark
    public void findCauseBenchmark(Blackhole bh) {
        // Find the specific cause type (RuntimeException)
        RuntimeException result = ExceptionUtil.findCause(testThrowableChain, RuntimeException.class);
        bh.consume(result);
    }

    // =================================================================
    // String Conversion Methods
    // =================================================================

    @Benchmark
    public void exceptionStackTraceToStringBenchmark(Blackhole bh) {
        String result = ExceptionUtil.exceptionStackTraceToString(standardThrowable);
        bh.consume(result);
    }

    @Benchmark
    public void exceptionChainToStringBenchmark(Blackhole bh) {
        String result = ExceptionUtil.exceptionChainToString(testThrowableChain);
        bh.consume(result);
    }

    // =================================================================
    // Message Building Methods
    // =================================================================

    @Benchmark
    public void buildMessageBenchmark(Blackhole bh) {
        String result = ExceptionUtil.buildMessage(testMessage, testCauseForMessage);
        bh.consume(result);
    }

    @Benchmark
    public void messageBenchmark(Blackhole bh) {
        String result = ExceptionUtil.message(standardThrowable);
        bh.consume(result);
    }

    // =================================================================
    // Wrapping and Unwrapping Methods
    // =================================================================

    @Benchmark
    public void wrapToRuntimeExceptionBenchmark(Blackhole bh) {
        RuntimeException result = ExceptionUtil.wrapToRuntimeException(standardThrowable);
        bh.consume(result);
    }

    @Benchmark
    public void wrapToExceptionBenchmark(Blackhole bh) {
        Exception result = ExceptionUtil.wrapToException(standardThrowable);
        bh.consume(result);
    }

    @Benchmark
    public void unwrapThrowableBenchmark(Blackhole bh) {
        // Simulate a wrapped throwable (e.g., InvocationTargetException wrapping RuntimeException)
        Throwable wrapped = new InvocationTargetException(standardThrowable);
        Throwable result = ExceptionUtil.unwrapThrowable(wrapped);
        bh.consume(result);
    }

    // =================================================================
    // SQL Rollup Method
    // =================================================================

    @Benchmark
    public void rollupSqlExceptionsBenchmark(Blackhole bh) {
        SQLException result = ExceptionUtil.rollupSqlExceptions(sqlExceptions);
        bh.consume(result);
    }
}

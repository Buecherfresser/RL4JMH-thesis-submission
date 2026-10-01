package bench.generated.c010;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.exception.ExceptionUtil;
import java.sql.SQLException;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ExceptionUtilBenchmark {

    // Simple exception without cause
    private Throwable simpleException;

    // Exception chain (root cause -> middle -> top)
    private Throwable chainException;

    // Filters for stack trace methods
    private String[] allowFilters;
    private String[] denyFilters;

    // Collection of SQLExceptions for rollup
    private Collection<SQLException> sqlExceptions;

    // Wrapped throwables for unwrap tests
    private Throwable invocationWrapped;
    private Throwable undeclaredWrapped;

    @Setup(Level.Trial)
    public void setup() {
        simpleException = new IllegalArgumentException("simple");

        // Build a three‑level cause chain
        Throwable root = new IllegalArgumentException("root cause");
        Throwable middle = new IllegalStateException("middle cause", root);
        chainException = new RuntimeException("top level", middle);

        // Filters: allow everything, deny nothing
        allowFilters = new String[] { "jodd" };
        denyFilters = new String[] { "nonexistent" };

        // Prepare a few SQLExceptions
        List<SQLException> list = new ArrayList<>();
        list.add(new SQLException("sql1"));
        list.add(new SQLException("sql2"));
        list.add(new SQLException("sql3"));
        sqlExceptions = list;

        // Wrapped throwables
        invocationWrapped = new InvocationTargetException(new IllegalArgumentException("invoked"));
        undeclaredWrapped = new UndeclaredThrowableException(new IllegalStateException("undeclared"));
    }

    @Benchmark
    public StackTraceElement[] benchGetCurrentStackTrace() {
        return ExceptionUtil.getCurrentStackTrace();
    }

    @Benchmark
    public StackTraceElement[] benchGetStackTrace() {
        return ExceptionUtil.getStackTrace(simpleException, allowFilters, denyFilters);
    }

    @Benchmark
    public StackTraceElement[][] benchGetStackTraceChain() {
        return ExceptionUtil.getStackTraceChain(chainException, allowFilters, denyFilters);
    }

    @Benchmark
    public Throwable[] benchGetExceptionChain() {
        return ExceptionUtil.getExceptionChain(chainException);
    }

    @Benchmark
    public String benchExceptionStackTraceToString() {
        return ExceptionUtil.exceptionStackTraceToString(chainException);
    }

    @Benchmark
    public String benchExceptionChainToString() {
        return ExceptionUtil.exceptionChainToString(chainException);
    }

    @Benchmark
    public String benchBuildMessage() {
        return ExceptionUtil.buildMessage("base message", chainException);
    }

    @Benchmark
    public Throwable benchGetRootCause() {
        return ExceptionUtil.getRootCause(chainException);
    }

    @Benchmark
    public IllegalArgumentException benchFindCause() {
        return ExceptionUtil.findCause(chainException, IllegalArgumentException.class);
    }

    @Benchmark
    public SQLException benchRollupSqlExceptions() {
        return ExceptionUtil.rollupSqlExceptions(sqlExceptions);
    }

    @Benchmark
    public String benchMessage() {
        return ExceptionUtil.message(simpleException);
    }

    @Benchmark
    public RuntimeException benchWrapToRuntimeException() {
        return ExceptionUtil.wrapToRuntimeException(simpleException);
    }

    @Benchmark
    public Exception benchWrapToException() {
        return ExceptionUtil.wrapToException(simpleException);
    }

    @Benchmark
    public Throwable benchUnwrapInvocationTargetException() {
        return ExceptionUtil.unwrapThrowable(invocationWrapped);
    }

    @Benchmark
    public Throwable benchUnwrapUndeclaredThrowableException() {
        return ExceptionUtil.unwrapThrowable(undeclaredWrapped);
    }
}

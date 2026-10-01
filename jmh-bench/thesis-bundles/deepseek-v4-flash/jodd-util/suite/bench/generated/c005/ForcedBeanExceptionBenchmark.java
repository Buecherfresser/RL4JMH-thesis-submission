package bench.generated.c005;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;
import java.util.concurrent.TimeUnit;
import jodd.bean.BeanProperty;
import jodd.bean.exception.ForcedBeanException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ForcedBeanExceptionBenchmark {

    private BeanProperty beanProperty;
    private String message;
    private Throwable cause;
    private ForcedBeanException sampleException;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        beanProperty = createBeanProperty();
        message = "forced bean exception";
        cause = new RuntimeException("cause");
        sampleException = new ForcedBeanException(message, beanProperty, cause);
    }

    @Benchmark
    public ForcedBeanException constructException() {
        return new ForcedBeanException(message, beanProperty, cause);
    }

    @Benchmark
    public String getMessage() {
        return sampleException.getMessage();
    }

    @Benchmark
    public String getLocalizedMessage() {
        return sampleException.getLocalizedMessage();
    }

    @Benchmark
    public String toString() {
        return sampleException.toString();
    }

    @Benchmark
    public Throwable getCause() {
        return sampleException.getCause();
    }

    @Benchmark
    public StackTraceElement[] getStackTrace() {
        return sampleException.getStackTrace();
    }

    private BeanProperty createBeanProperty() throws Exception {
        Class<?> bpClass = Class.forName("jodd.bean.BeanProperty");

        if (bpClass.isInterface()) {
            return (BeanProperty) Proxy.newProxyInstance(
                bpClass.getClassLoader(),
                new Class<?>[] {bpClass},
                (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "toString":
                            return "name";
                        case "hashCode":
                            return System.identityHashCode(proxy);
                        case "equals":
                            return proxy == args[0];
                        default:
                            Class<?> returnType = method.getReturnType();
                            if (returnType == boolean.class) {
                                return Boolean.FALSE;
                            }
                            if (returnType == byte.class) {
                                return Byte.valueOf((byte) 0);
                            }
                            if (returnType == short.class) {
                                return Short.valueOf((short) 0);
                            }
                            if (returnType == int.class) {
                                return Integer.valueOf(0);
                            }
                            if (returnType == long.class) {
                                return Long.valueOf(0L);
                            }
                            if (returnType == float.class) {
                                return Float.valueOf(0f);
                            }
                            if (returnType == double.class) {
                                return Double.valueOf(0d);
                            }
                            if (returnType == char.class) {
                                return Character.valueOf('x');
                            }
                            if (returnType == String.class) {
                                return "name";
                            }
                            return null;
                    }
                });
        }

        Constructor<?>[] constructors = bpClass.getConstructors();

        for (Constructor<?> ctor : constructors) {
            if (ctor.getParameterCount() == 1 &&
                ctor.getParameterTypes()[0] == String.class) {
                return (BeanProperty) ctor.newInstance("name");
            }
        }

        for (Constructor<?> ctor : constructors) {
            if (ctor.getParameterCount() == 0) {
                return (BeanProperty) ctor.newInstance();
            }
        }

        if (constructors.length > 0) {
            Constructor<?> ctor = constructors[0];
            Class<?>[] parameterTypes = ctor.getParameterTypes();
            Object[] initArgs = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                initArgs[i] = defaultValueFor(parameterTypes[i]);
            }
            return (BeanProperty) ctor.newInstance(initArgs);
        }

        throw new IllegalStateException("Cannot instantiate " + bpClass.getName());
    }

    private static Object defaultValueFor(Class<?> type) {
        if (type == String.class) {
            return "name";
        }
        if (type == Boolean.TYPE || type == Boolean.class) {
            return Boolean.FALSE;
        }
        if (type == Byte.TYPE || type == Byte.class) {
            return Byte.valueOf((byte) 0);
        }
        if (type == Short.TYPE || type == Short.class) {
            return Short.valueOf((short) 0);
        }
        if (type == Integer.TYPE || type == Integer.class) {
            return Integer.valueOf(0);
        }
        if (type == Long.TYPE || type == Long.class) {
            return Long.valueOf(0L);
        }
        if (type == Float.TYPE || type == Float.class) {
            return Float.valueOf(0f);
        }
        if (type == Double.TYPE || type == Double.class) {
            return Double.valueOf(0d);
        }
        if (type == Character.TYPE || type == Character.class) {
            return Character.valueOf('x');
        }
        return null;
    }
}

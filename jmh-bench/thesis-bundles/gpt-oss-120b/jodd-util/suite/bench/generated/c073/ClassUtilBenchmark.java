package bench.generated.c073;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.util.ClassUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.jar.JarFile;
import jodd.net.URLDecoder;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ClassUtilBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        Class<?> sampleClass;
        Method methodGet;
        Method methodSet;
        Method methodIs;
        Method methodOther;
        Method privateMethod;
        Field sampleField;
        Constructor<?> defaultConstructor;
        Object[] objects;
        String methodName;
        String privateMethodName;
        Class<?>[] assignableTarget;
        Class<?>[] assignableFrom;
        Type genericListType;
        Type genericVariableType;
        Annotation deprecatedAnnotation;
        Method objectToStringMethod;
        Method sampleMethod;
        Method sampleMethod2;
        Field genericField;
        Field stringListField;
        Class<?>[] componentTypes;
        Class<?>[] componentTypesWithImpl;
        Class<?>[] genericSuperTypes;
        Class<?>[] genericSuperTypesWithImpl;
        Class<?>[] superclasses;
        Class<?>[] interfaces;
        Method[] accessibleMethods;
        Method[] supportedMethods;
        Field[] accessibleFields;
        Field[] supportedFields;
        Constructor<?>[] constructors;
        Method[] declaredMethods;
        Field[] declaredFields;

        @Setup(Level.Trial)
        public void setup() throws Exception {
            sampleClass = Sample.class;
            methodGet = Sample.class.getMethod("getValue");
            methodSet = Sample.class.getMethod("setValue", int.class);
            methodIs = Sample.class.getMethod("isActive");
            methodOther = Sample.class.getMethod("doSomething");
            privateMethod = Sample.class.getDeclaredMethod("privateMethod");
            sampleField = Sample.class.getDeclaredField("value");
            defaultConstructor = Sample.class.getDeclaredConstructor();
            objects = new Object[] { "string", 123, null, new Sample() };
            methodName = "getValue";
            privateMethodName = "privateMethod";
            assignableTarget = new Class<?>[] { Object.class, Number.class };
            assignableFrom = new Class<?>[] { String.class, Integer.class };
            genericListType = Sample.class.getDeclaredField("stringList").getGenericType(); // ParameterizedType List<String>
            genericVariableType = GenericSample.class.getDeclaredField("genericField").getGenericType(); // TypeVariable
            deprecatedAnnotation = null; // No actual annotation instance needed for benchmark
            objectToStringMethod = Object.class.getMethod("toString");
            sampleMethod = Sample.class.getMethod("getValue");
            sampleMethod2 = Sample.class.getMethod("setValue", int.class);
            genericField = GenericSample.class.getDeclaredField("genericField");
            stringListField = Sample.class.getDeclaredField("stringList");
            componentTypes = ClassUtil.getComponentTypes(genericListType);
            componentTypesWithImpl = ClassUtil.getComponentTypes(genericListType, Sample.class);
            genericSuperTypes = ClassUtil.getComponentTypes(GenericSample.class.getGenericSuperclass());
            genericSuperTypesWithImpl = ClassUtil.getComponentTypes(GenericSample.class.getGenericSuperclass(), GenericSample.class);
            superclasses = ClassUtil.getSuperclasses(sampleClass);
            interfaces = ClassUtil.resolveAllInterfaces(sampleClass);
            accessibleMethods = ClassUtil.getAccessibleMethods(sampleClass);
            supportedMethods = ClassUtil.getSupportedMethods(sampleClass);
            accessibleFields = ClassUtil.getAccessibleFields(sampleClass);
            supportedFields = ClassUtil.getSupportedFields(sampleClass);
            constructors = new Constructor<?>[] { defaultConstructor };
            declaredMethods = new Method[] { methodGet, methodSet, methodIs, methodOther };
            declaredFields = new Field[] { sampleField };
        }
    }

    // Simple sample class with various members
    public static class Sample {
        public int value;
        private String hidden;
        public List<String> stringList = new ArrayList<>();

        public int getValue() { return value; }
        public void setValue(int v) { this.value = v; }
        public boolean isActive() { return true; }
        public void doSomething() {}
        private void privateMethod() {}
    }

    // Generic sample class for type variable tests
    public static class GenericSample<T> {
        public T genericField;
    }

    @Benchmark
    public Method benchmarkFindMethod(BenchmarkState s) {
        return ClassUtil.findMethod(s.sampleClass, s.methodName);
    }

    @Benchmark
    public Method benchmarkFindDeclaredMethod(BenchmarkState s) {
        return ClassUtil.findDeclaredMethod(s.sampleClass, s.methodName);
    }

    @Benchmark
    public Constructor<?> benchmarkFindConstructor(BenchmarkState s) {
        return ClassUtil.findConstructor(s.sampleClass, new Class<?>[0]);
    }

    @Benchmark
    public boolean benchmarkIsAllAssignableFrom(BenchmarkState s) {
        return ClassUtil.isAllAssignableFrom(s.assignableTarget, s.assignableFrom);
    }

    @Benchmark
    public Class<?>[] benchmarkGetClasses(BenchmarkState s) {
        return ClassUtil.getClasses(s.objects);
    }

    @Benchmark
    public boolean benchmarkIsTypeOf(BenchmarkState s) {
        return ClassUtil.isTypeOf(s.sampleClass, Object.class);
    }

    @Benchmark
    public boolean benchmarkIsInstanceOf(BenchmarkState s) {
        return ClassUtil.isInstanceOf(new Sample(), s.sampleClass);
    }

    @Benchmark
    public Class<?>[] benchmarkResolveAllInterfaces(BenchmarkState s) {
        return ClassUtil.resolveAllInterfaces(s.sampleClass);
    }

    @Benchmark
    public Class<?>[] benchmarkResolveAllSuperclasses(BenchmarkState s) {
        return ClassUtil.resolveAllSuperclasses(s.sampleClass);
    }

    @Benchmark
    public Method[] benchmarkGetAccessibleMethods(BenchmarkState s) {
        return ClassUtil.getAccessibleMethods(s.sampleClass);
    }

    @Benchmark
    public Method[] benchmarkGetAccessibleMethodsWithLimit(BenchmarkState s) {
        return ClassUtil.getAccessibleMethods(s.sampleClass, Object.class);
    }

    @Benchmark
    public Field[] benchmarkGetAccessibleFields(BenchmarkState s) {
        return ClassUtil.getAccessibleFields(s.sampleClass);
    }

    @Benchmark
    public Field[] benchmarkGetAccessibleFieldsWithLimit(BenchmarkState s) {
        return ClassUtil.getAccessibleFields(s.sampleClass, Object.class);
    }

    @Benchmark
    public Method[] benchmarkGetSupportedMethods(BenchmarkState s) {
        return ClassUtil.getSupportedMethods(s.sampleClass);
    }

    @Benchmark
    public Method[] benchmarkGetSupportedMethodsWithLimit(BenchmarkState s) {
        return ClassUtil.getSupportedMethods(s.sampleClass, Object.class);
    }

    @Benchmark
    public Field[] benchmarkGetSupportedFields(BenchmarkState s) {
        return ClassUtil.getSupportedFields(s.sampleClass);
    }

    @Benchmark
    public Field[] benchmarkGetSupportedFieldsWithLimit(BenchmarkState s) {
        return ClassUtil.getSupportedFields(s.sampleClass, Object.class);
    }

    @Benchmark
    public boolean benchmarkCompareDeclarations(BenchmarkState s) {
        return ClassUtil.compareDeclarations(s.methodGet, s.methodGet);
    }

    @Benchmark
    public boolean benchmarkCompareSignaturesMethod(BenchmarkState s) {
        return ClassUtil.compareSignatures(s.methodGet, s.methodGet);
    }

    @Benchmark
    public boolean benchmarkCompareSignaturesConstructor(BenchmarkState s) {
        return ClassUtil.compareSignatures((Constructor<?>) s.defaultConstructor, (Constructor<?>) s.defaultConstructor);
    }

    @Benchmark
    public boolean benchmarkCompareSignaturesField(BenchmarkState s) {
        return ClassUtil.compareSignatures(s.sampleField, s.sampleField);
    }

    @Benchmark
    public boolean benchmarkCompareParameters(BenchmarkState s) {
        return ClassUtil.compareParameters(s.assignableTarget, s.assignableTarget);
    }

    @Benchmark
    public void benchmarkForceAccess(BenchmarkState s, Blackhole bh) {
        ClassUtil.forceAccess(s.privateMethod);
        bh.consume(s.privateMethod.isAccessible());
    }

    @Benchmark
    public boolean benchmarkIsPublicMember(BenchmarkState s) {
        return ClassUtil.isPublic(s.methodGet);
    }

    @Benchmark
    public boolean benchmarkIsPublicPublicMember(BenchmarkState s) {
        return ClassUtil.isPublicPublic(s.methodGet);
    }

    @Benchmark
    public boolean benchmarkIsPublicClass(BenchmarkState s) {
        return ClassUtil.isPublic(s.sampleClass);
    }

    @Benchmark
    public Sample benchmarkNewInstanceWithParams(BenchmarkState s) throws Exception {
        return ClassUtil.newInstance(Sample.class);
    }

    @Benchmark
    public Object benchmarkNewInstanceNoParams(BenchmarkState s) throws Exception {
        return ClassUtil.newInstance(Integer.class);
    }

    @Benchmark
    public boolean benchmarkIsAssignableFromMember(BenchmarkState s) {
        return ClassUtil.isAssignableFrom(s.methodGet, s.methodSet);
    }

    @Benchmark
    public Class<?>[] benchmarkGetSuperclasses(BenchmarkState s) {
        return ClassUtil.getSuperclasses(s.sampleClass);
    }

    @Benchmark
    public boolean benchmarkIsUserDefinedMethod(BenchmarkState s) {
        return ClassUtil.isUserDefinedMethod(s.methodGet);
    }

    @Benchmark
    public boolean benchmarkIsObjectMethod(BenchmarkState s) {
        return ClassUtil.isObjectMethod(s.objectToStringMethod);
    }

    @Benchmark
    public boolean benchmarkIsBeanProperty(BenchmarkState s) {
        return ClassUtil.isBeanProperty(s.methodGet);
    }

    @Benchmark
    public boolean benchmarkIsBeanPropertyGetter(BenchmarkState s) {
        return ClassUtil.isBeanPropertyGetter(s.methodGet);
    }

    @Benchmark
    public String benchmarkGetBeanPropertyGetterName(BenchmarkState s) {
        return ClassUtil.getBeanPropertyGetterName(s.methodGet);
    }

    @Benchmark
    public boolean benchmarkIsBeanPropertySetter(BenchmarkState s) {
        return ClassUtil.isBeanPropertySetter(s.methodSet);
    }

    @Benchmark
    public String benchmarkGetBeanPropertySetterName(BenchmarkState s) {
        return ClassUtil.getBeanPropertySetterName(s.methodSet);
    }

    @Benchmark
    public Class<?> benchmarkGetComponentType(BenchmarkState s) {
        return ClassUtil.getComponentType(s.genericListType, 0);
    }

    @Benchmark
    public Class<?>[] benchmarkGetComponentTypes(BenchmarkState s) {
        return ClassUtil.getComponentTypes(s.genericListType);
    }

    @Benchmark
    public Class<?>[] benchmarkGetComponentTypesWithImpl(BenchmarkState s) {
        return ClassUtil.getComponentTypes(s.genericListType, Sample.class);
    }

    @Benchmark
    public Class<?>[] benchmarkGetGenericSupertypes(BenchmarkState s) {
        return ClassUtil.getGenericSupertypes(ArrayList.class);
    }

    @Benchmark
    public Class<?> benchmarkGetGenericSupertype(BenchmarkState s) {
        return ClassUtil.getGenericSupertype(ArrayList.class, 0);
    }

    @Benchmark
    public Class<?> benchmarkGetRawType(BenchmarkState s) {
        return ClassUtil.getRawType(s.genericListType);
    }

    @Benchmark
    public Class<?> benchmarkGetRawTypeWithImpl(BenchmarkState s) {
        return ClassUtil.getRawType(s.genericListType, Sample.class);
    }

    @Benchmark
    public Type benchmarkResolveVariable(BenchmarkState s) {
        return ClassUtil.resolveVariable((TypeVariable<?>) s.genericVariableType, GenericSample.class);
    }

    @Benchmark
    public String benchmarkTypeToString(BenchmarkState s) {
        return ClassUtil.typeToString(s.genericListType);
    }

    @Benchmark
    public Object benchmarkReadAnnotationValue(BenchmarkState s) {
        return ClassUtil.readAnnotationValue(s.deprecatedAnnotation, "value");
    }

    @Benchmark
    public Class<?> benchmarkGetCallerClassInt(BenchmarkState s) {
        return ClassUtil.getCallerClass(0);
    }

    @Benchmark
    public Class<?> benchmarkGetCallerClassNoArg(BenchmarkState s) {
        return ClassUtil.getCallerClass();
    }

    @Benchmark
    public Class<?> benchmarkFindEnum(BenchmarkState s) {
        return ClassUtil.findEnum(java.time.DayOfWeek.class);
    }

    @Benchmark
    public Class<?> benchmarkChildClassOf(BenchmarkState s) {
        return ClassUtil.childClassOf(Object.class, new Sample());
    }

    @Benchmark
    public JarFile benchmarkJarFileOf(BenchmarkState s) {
        return ClassUtil.jarFileOf(ClassUtil.class);
    }

    @Benchmark
    public String benchmarkConvertClassNameToFileNameClass(BenchmarkState s) {
        return ClassUtil.convertClassNameToFileName(s.sampleClass);
    }

    @Benchmark
    public String benchmarkConvertClassNameToFileNameString(BenchmarkState s) {
        return ClassUtil.convertClassNameToFileName(s.sampleClass.getName());
    }

    @Benchmark
    public String benchmarkGetShortClassName(BenchmarkState s) {
        return ClassUtil.getShortClassName(s.sampleClass);
    }

    @Benchmark
    public String benchmarkGetShortClassNameWithDepth(BenchmarkState s) {
        return ClassUtil.getShortClassName(s.sampleClass, 2);
    }

    @Benchmark
    public boolean benchmarkIsKotlinClass(BenchmarkState s) {
        return ClassUtil.isKotlinClass(s.sampleClass);
    }
}

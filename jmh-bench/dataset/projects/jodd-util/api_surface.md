# Jodd Util 6.3.0 - public API surface

Jodd Util (`jodd.*`) is a dependency-free utility library: bean property access,
class introspection, type conversion, string/array helpers, IO helpers and
net/MIME/URL encoding. Almost everything is a static helper or a small stateless
object, so benchmarks can drive it directly with no fixture beyond the input
data.

Most of it is CPU-bound. The `jodd.io` file helpers do touch the filesystem —
benchmark those against a temporary file created once in `@Setup`, and prefer
the in-memory `IOUtil` overloads (`Reader`/`InputStream`) elsewhere.

## Bean access — `jodd.bean`

`jodd.bean.BeanUtil` — an interface with three preconfigured instances:
`BeanUtil.pojo` (declared+forced off), `BeanUtil.declared`, `BeanUtil.forced`,
`BeanUtil.declaredForced`, `BeanUtil.silent`, `BeanUtil.declaredSilent`, …
- `Object getProperty(Object bean, String name)`,
  `<T> T getProperty(Object bean, String name, Class<T> type)`
- `void setProperty(Object bean, String name, Object value)`
- `boolean hasProperty(Object, String)`, `boolean hasRootProperty(Object, String)`
- `Class<?> getPropertyType(Object, String)`
- Nested and indexed names are supported: `"address.street"`, `"items[2].name"`,
  `"map[key]"`.

`jodd.bean.BeanCopy` — `static BeanCopy from(Object)`, `to(Object)`,
`declared(boolean)`, `forced(boolean)`, `includeFields(boolean)`,
`filter(Predicate<String>)`, `filter(BiPredicate<String, Object>)`, `copy()`.
Either end may be a `Map` instead of a POJO — `BeanCopy.from(map).to(bean)` and
`BeanCopy.from(bean).to(map)` both work.

`jodd.bean.BeanVisitor` — the shared property-walking base:
`includeFields(boolean)`, `declared(boolean)`, `ignoreNulls(boolean)`,
`ignoreEmptyString(boolean)`.

## Class introspection — `jodd.introspector`

`jodd.introspector.ClassIntrospector` — `ClassIntrospector.get()`,
`ClassDescriptor lookup(Class)`, `void reset()`; `CachingIntrospector` is the
default implementation (`new CachingIntrospector(boolean scanAccessible,
boolean enhancedProperties, boolean includeFieldsAsProperties, String[] prefixes)`).

`jodd.introspector.ClassDescriptor` — `getType()`, `isArray()`, `isMap()`,
`isList()`, `isSet()`, `isCollection()`, `isSupplier()`, `isSystemClass()`,
`getFieldDescriptor(String, boolean)`, `getAllFieldDescriptors()`,
`getMethodDescriptor(String, boolean)`, `getMethodDescriptor(String, Class[], boolean)`,
`getAllMethodDescriptors()`, `getPropertyDescriptor(String, boolean)`,
`getAllPropertyDescriptors()`, `getCtorDescriptor(Class[], boolean)`,
`getDefaultCtorDescriptor(boolean)`.

Descriptor types: `FieldDescriptor` (`getRawType()`, `getRawComponentType()`),
`MethodDescriptor` (`getRawReturnType()`, `getRawReturnComponentType()`,
`getRawParameterTypes()`), `PropertyDescriptor` (`getGetter(boolean)`,
`getSetter(boolean)`, `isGetterOnly()`, `isSetterOnly()`,
`resolveKeyType(boolean)`), `CtorDescriptor` (`getConstructor()`),
`Getter` / `Setter` (`getGetterRawType()`, `getGetterRawComponentType()`,
`getGetterRawKeyComponentType()`).

## Type conversion — `jodd.typeconverter`

`jodd.typeconverter.TypeConverterManager` — `TypeConverterManager.get()`,
`<T> T convertType(Object value, Class<T> destinationType)`,
`<T> TypeConverter<T> lookup(Class<T>)`, `register(Class<T>, TypeConverter<T>)`,
`unregister(Class)`, `convertToCollection(Object, Class<? extends Collection>, Class<T>)`.

`jodd.typeconverter.Converter` — `Converter.get()`, with typed shortcuts:
`toInteger/toIntValue`, `toLong/toLongValue`, `toShort/toShortValue`,
`toByte/toByteValue`, `toFloat/toFloatValue`, `toDouble/toDoubleValue`,
`toBoolean/toBooleanValue`, `toCharacter/toCharValue`, `toString`,
`toBigDecimal`, `toBigInteger`, `toLocalDate`, `toLocalDateTime`, `toDate`,
`toClass`, `toIntegerArray`, `toStringArray`, … each with a `defaultValue`
overload.

Array converters live in `jodd.typeconverter.impl`
(`IntegerArrayConverter`, `LongArrayConverter`, `ByteArrayConverter`,
`ShortArrayConverter`, `DoubleArrayConverter`, `FloatArrayConverter`,
`BooleanArrayConverter`, `CharacterArrayConverter`, `StringArrayConverter`,
`ClassArrayConverter`) — each converts scalars, CSV strings, collections and
other arrays into its target array type.

## Strings, arrays, collections — `jodd.util`

- `StringUtil` — the largest helper: `isEmpty`, `isBlank`, `capitalize`,
  `replace`, `remove`, `cut*`, `split`, `join`, `repeat`, `indexOfIgnoreCase`,
  `startsWithIgnoreCase`, `toCamelCase`, `fromCamelCase`, `stripLeading`, …
- `ArraysUtil` — `join`, `resize`, `append`, `insert(T[] dest, T[] src, int offset)`
  (and every primitive overload), `subarray`, `indexOf`, `contains`, `values`,
  `toString`.
- `CharUtil` — `toByteArray`, `toCharArray`, `isAlpha`, `isDigit`, `isWhitespace`,
  `toAscii`, `hexToInt`.
- `Base64` — `encodeToString(byte[]|String)`, `encodeToChars`, `decode(String)`,
  `decodeToString(byte[]|String)`.
- `CollectionUtil` — `collectionOf(Iterator)`, `streamOf(Iterator|Iterable)`,
  `parallelStreamOf(Iterator|Iterable)`, `asEnumeration(Iterator)`,
  `asIterator(Enumeration)`.
- `StringTemplateMatcher` — `StringTemplateMatcher.of(String pattern)`,
  `useRegexMatch()`, `match(String)`, returning `Match` objects.
- `TypeCache` — `TypeCache.create()`/`createDefault()` and a `Builder` with
  `weak(boolean)`, `threadsafe(boolean)`, `noCache()`, `get()`; then
  `get(Class)`, `get(Class, Function)`, `put`, `remove`, `clear`, `size`.
- `PropertiesUtil` — `createFromFile`, `loadFromFile`, `writeToFile`,
  `subset`, `resolveProperty`, `resolveAllVariables`.
- `SystemUtil` — `getInt(String, int)`, `get(String)`, `javaVersion()`,
  `userDir()`, `tempDir()`.
- `ClassLoaderUtil` — `getClassAsStream(Class)`, `getResourceAsStream(String)`,
  `loadClass(String)`, `getDefaultClassLoader()`.
- `ClassUtil`, `Wildcard` (`match`, `matchPath`), `RandomString`, `Util`.

## IO — `jodd.io`

- `IOUtil` — `readChars(Reader)`, `readBytes(InputStream)`,
  `readBytes(Reader, int count)`, `copy(...)`, `close(Closeable)`,
  `toByteArray`, `toString`.
- `FileUtil` — `readString(File)`, `writeString(File, String)`,
  `readBytes`, `writeBytes`, `copyFile`, `delete`, `mkdirs`, `createTempFile`.
- `PathUtil` — `readString(Path)`, `writeString(Path, String)`, `resolve`,
  `deleteFileTree`.
- `FileNameUtil` — `getName`, `getBaseName`, `getExtension`,
  `hasExtension(String)`, `getPath`, `normalize`, `concat`, `separatorsToUnix`.
- `StreamGobbler(InputStream[, OutputStream[, String prefix]])` — a `Runnable`
  that drains a stream; `waitFor()`.
- `AppendableWriter(Appendable)`, `CharBufferReader(CharBuffer)` — note that its
  `read(char[], int, int)` returns `0`, not `-1`, once the buffer is exhausted,
  so read it with a bounded loop,
  `UnicodeInputStream(InputStream, Charset targetEncoding)` (BOM sniffing,
  `getDetectedEncoding()`),
  `FastCharArrayWriter`, `FastByteArrayOutputStream`.
- `NetUtil` — `resolveIpAddress(String)`, `resolveHostName(byte[])`,
  `validateIPv4(String)`, `getIpAsInt(String)`, `getMaskAsInt(String)`,
  `isSocketAccessAllowed(int, int, int)`.

## Net / MIME / URL — `jodd.net`

- `MimeTypes` — `lookupMimeType(String extension)`, `getMimeType(String)`,
  `registerMimeType(String, String)`, plus `MIME_*` constants.
- `URLCoder` — `encodeQuery`, `encodePath`, `encodeHost`, `encodePort`,
  `encodeUri`, `build(String)`.
- `URLDecoder` — `decode(String)`, `decodeQuery(String)`.
- `HtmlEncoder` — `text(CharSequence)`, `attribute(CharSequence)`,
  `attributeDoubleQuoted`, `attributeSingleQuoted`, `strict`, `block`.
- `HtmlDecoder` — `decode(String)`, `detectName(char[], int)`.
- `HttpMethod` — enum with `equalsName(String)`, `valueOf(String)`.

## Date and time — `jodd.time`

- `TimeUtil` — `toMilliseconds(LocalDate|LocalDateTime[, ZoneId])`,
  `fromMilliseconds(long[, ZoneId])`, `toDate(LocalDate|LocalDateTime)`,
  `fromDate(Date)`, `toCalendar(LocalDate|LocalDateTime)`,
  `fromCalendar(Calendar)`.
- `JulianDate` — `JulianDate.of(double|long|LocalDate|LocalDateTime|Instant|int, double)`,
  `toDouble()`, `toLocalDateTime()`, `toInstant()`, `add(double|JulianDate)`,
  `daysBetween(JulianDate)`; constants `JD_1970`, `JD_2001`.

## Exceptions — `jodd.exception`

- `ExceptionUtil` — `getRootCause(Throwable)`, `getExceptionChain`,
  `exceptionStackTraceToString`, `throwException`, `wrapToRuntimeException`.
- `UncheckedException` — `callAndWrapException(Callable<V>)`,
  `runAndWrapException(Runnable)`, `wrap(Throwable)`.

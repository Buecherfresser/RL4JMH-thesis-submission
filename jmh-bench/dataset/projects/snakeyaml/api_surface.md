# SnakeYAML 2.4 - public API surface

SnakeYAML (`org.yaml.snakeyaml`) parses and emits YAML 1.1. Loading runs a
pipeline — `StreamReader` → `Scanner` (characters to tokens) → `Parser` (tokens
to events) → `Composer` (events to a node graph) → `Constructor` (nodes to Java
objects) — and dumping runs it in reverse: `Representer` → `Serializer` →
`Emitter`.

Everything is CPU-bound and in-memory. Benchmarks should hold YAML documents as
`String`s (or wrap them in `StringReader` / `ByteArrayInputStream`) rather than
touching the filesystem, and should build the document once in `@Setup` so the
measured work is parsing and emitting rather than string building. A `Yaml`
instance is **not** thread-safe: create one per `@State` object.

## Entry point

`org.yaml.snakeyaml.Yaml`
- `Yaml()`, `Yaml(LoaderOptions)`, `Yaml(DumperOptions)`,
  `Yaml(LoaderOptions, DumperOptions)`, `Yaml(Representer)`,
  `Yaml(BaseConstructor)`, `Yaml(BaseConstructor, Representer)`,
  `Yaml(BaseConstructor, Representer, DumperOptions)`,
  `Yaml(BaseConstructor, Representer, DumperOptions, LoaderOptions)`
- load: `<T> T load(String|Reader|InputStream)`,
  `<T> T loadAs(String|Reader|InputStream, Class<? super T>)`,
  `Iterable<Object> loadAll(String|Reader|InputStream)`
- dump: `String dump(Object)`, `void dump(Object, Writer)`,
  `String dumpAll(Iterator<?>)`, `void dumpAll(Iterator<?>, Writer)`,
  `String dumpAs(Object, Tag, FlowStyle)`, `String dumpAsMap(Object)`
- lower-level: `Node compose(Reader)`, `Iterable<Node> composeAll(Reader)`,
  `Iterable<Event> parse(Reader)`, `void serialize(Node, Writer)`,
  `List<Event> serialize(Node)`
- configuration: `void addTypeDescription(TypeDescription)`,
  `void setBeanAccess(BeanAccess)`, `void setName(String)`

## Options

`org.yaml.snakeyaml.LoaderOptions` — `setAllowDuplicateKeys`,
`setWarnOnDuplicateKeys`, `setWrappedToRootException`,
`setMaxAliasesForCollections`, `setAllowRecursiveKeys`, `setProcessComments`,
`setEnumCaseSensitive`, `setNestingDepthLimit`, `setCodePointLimit`,
`setMergeOnCompose`, `setTagInspector`.

`org.yaml.snakeyaml.DumperOptions` — `setDefaultFlowStyle(FlowStyle)`,
`setDefaultScalarStyle(ScalarStyle)`, `setIndent`, `setIndicatorIndent`,
`setIndentWithIndicator`, `setWidth`, `setSplitLines`, `setCanonical`,
`setPrettyFlow`, `setExplicitStart`, `setExplicitEnd`, `setLineBreak`,
`setVersion`, `setTags`, `setAllowUnicode`, `setAllowReadOnlyProperties`,
`setTimeZone`, `setAnchorGenerator`, `setMaxSimpleKeyLength`,
`setProcessComments`. Nested enums: `FlowStyle` (`BLOCK`, `FLOW`, `AUTO`),
`ScalarStyle` (`PLAIN`, `SINGLE_QUOTED`, `DOUBLE_QUOTED`, `LITERAL`, `FOLDED`),
`LineBreak`, `Version`.

`org.yaml.snakeyaml.TypeDescription` — `TypeDescription(Class<?>)`,
`TypeDescription(Class<?>, Tag)`, `addPropertyParameters(String, Class<?>...)`,
`setPropertyUtils(PropertyUtils)`, `substituteProperty(...)`.

`org.yaml.snakeyaml.inspector.TagInspector` — `boolean isGlobalTagAllowed(Tag)`;
`UnTrustedTagInspector` and `TrustedTagInspector` are the shipped
implementations (global tags are rejected by default).

## Pipeline stages (usable directly)

- `reader.StreamReader(String|Reader)`, `static boolean isPrintable(int)`;
  `reader.UnicodeReader(InputStream)` sniffs the BOM.
- `scanner.ScannerImpl(StreamReader, LoaderOptions)` — `boolean checkToken(Token.ID...)`,
  `Token peekToken()`, `Token getToken()`.
- `parser.ParserImpl(StreamReader, LoaderOptions)` / `ParserImpl(Scanner)` —
  `boolean checkEvent(Event.ID)`, `Event peekEvent()`, `Event getEvent()`.
- `composer.Composer(Parser, Resolver, LoaderOptions)` — `Node getSingleNode()`,
  `Node getNode()`, `boolean checkNode()`.
- `constructor.SafeConstructor(LoaderOptions)` (plain YAML types only),
  `constructor.Constructor(Class<?>, LoaderOptions)` (JavaBeans),
  `constructor.CustomClassLoaderConstructor(ClassLoader, LoaderOptions)`.
- `representer.Representer(DumperOptions)`, `SafeRepresenter(DumperOptions)`,
  `JsonRepresenter(DumperOptions)` — `Node representData(Object)`,
  `setDefaultScalarStyle(ScalarStyle)`, `setDefaultFlowStyle(FlowStyle)`.
- `serializer.Serializer(Emitable, Resolver, DumperOptions, Tag)` — `open()`,
  `serialize(Node)`, `close()`; `serializer.NumberAnchorGenerator(int)`.
- `emitter.Emitter(Writer, DumperOptions)` — `emit(Event)`.
- `resolver.Resolver` — `Tag resolve(NodeId, String, boolean)`,
  `addImplicitResolver(Tag, Pattern, String)`.

## Model types

`nodes.Node` (`ScalarNode`, `SequenceNode`, `MappingNode`, `AnchorNode`,
`CollectionNode`), `nodes.NodeTuple`, `nodes.Tag`, `nodes.NodeId`;
`events.Event` and its subclasses (`StreamStartEvent`, `DocumentStartEvent`,
`ScalarEvent`, `SequenceStartEvent`, `MappingStartEvent`, `AliasEvent`,
`CommentEvent`, …); `tokens.Token` and its subclasses (`ScalarToken`,
`AnchorToken`, `TagToken`, `DirectiveToken`, …); `comments.CommentLine`,
`comments.CommentType`, `comments.CommentEventsCollector`.

## Errors and utilities

`error.YAMLException`, `error.MarkedYAMLException` (`getProblemMark()`,
`getContextMark()`), `error.Mark` (`get_snippet()`, `getLine()`, `getColumn()`),
`reader.ReaderException` (`getCodePoint()`), `composer.ComposerException`,
`parser.ParserException`, `scanner.ScannerException`.

`util.ArrayUtils.toUnmodifiableList(E[])` /
`toUnmodifiableCompositeList(E[], E[])`, `util.EnumUtils.findEnumInsensitiveCase`,
`util.UriEncoder.encode(String)` / `decode(String)`, `util.ArrayStack`,
`util.MergeUtils.flatten(MappingNode)`,
`introspector.PropertyUtils` (`getProperty`, `getProperties`),
`introspector.BeanAccess` (`DEFAULT`, `FIELD`, `PROPERTY`).

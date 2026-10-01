# decimal4j 1.0.3 - public API surface

decimal4j (`org.decimal4j`) is a fixed-point arithmetic library. A value is a
`long` unscaled number plus a *scale* (0–18 decimal places) that is fixed at
compile time, so arithmetic is plain `long` arithmetic with no allocation and no
`BigDecimal`. The library ships a specialisation per scale: `Decimal0f` …
`Decimal18f` (immutable), `MutableDecimal0f` … `MutableDecimal18f`, plus
`Scale0f` … `Scale18f` and `Factory0f` … `Factory18f`.

Everything is CPU-bound and allocation-light. Benchmarks should build the input
values in `@Setup` and measure one operation kind per method; the interesting
comparisons are against `BigDecimal` and against `double`.

## Values

`org.decimal4j.immutable.Decimal5f` (the same shape for every scale)
- constants `ZERO`, `ULP`, `ONE`, `TWO`, … `TEN`, `HUNDRED`, `THOUSAND`,
  `MINUS_ONE`, `HALF`, `TENTH`, `MAX_VALUE`, `MIN_VALUE`
- `static Decimal5f valueOf(long|float|double|BigInteger|BigDecimal|String|Decimal<?>)`,
  each with a `RoundingMode` overload
- `static Decimal5f unscaled(long unscaledValue)`,
  `static Decimal5f unscaled(long unscaledValue, int scale)`

`org.decimal4j.mutable.MutableDecimal5f`
- `MutableDecimal5f()`, and constructors taking
  `long|double|String|BigInteger|BigDecimal|Decimal<?>`
- `static MutableDecimal5f zero()`, `one()`, `two()`, … `ten()`,
  `static MutableDecimal5f unscaled(long unscaledValue)`
- in-place setters: `set(...)`, `setZero()`, `setOne()`, `setMinusOne()`,
  `setUnscaled(long)`, and every arithmetic method mutates and returns `this`.

`org.decimal4j.generic.GenericImmutableDecimal` /
`GenericMutableDecimal` — scale carried at runtime rather than in the type; built
through `GenericDecimalFactory`.

## Arithmetic — `org.decimal4j.api.Decimal`

`add`, `subtract`, `multiply`, `divide`, `remainder`, `negate`, `abs`,
`invert`, `square`, `sqrt`, `pow(int)`, `avg`, `shiftLeft`, `shiftRight`,
`round(int precision)`, each with `RoundingMode` / `TruncationPolicy` overloads
and `…Unscaled(long unscaledValue[, int scale])` and `…Long(long)` variants.

- `Decimal<?> multiplyExact(Decimal<?>)` — widens the scale instead of rounding.
- `Multipliable5f multiplyExact()` — the fluent form: `a.multiplyExact().by(b)`
  returns the exact product at the summed scale (`Multipliable5f.by(Decimal6f)`
  yields a `Decimal11f`). One `Multipliable<N>f` class per scale.
- `<S extends ScaleMetrics> ImmutableDecimal<S> scale(S scaleMetrics)` and
  `scale(int scale[, RoundingMode])` — rescale to a different scale.
- `Decimal<S> multiplyUnscaled(long unscaledMultiplicand, TruncationPolicy)`.
- conversions: `longValue`, `intValue`, `doubleValue`, `floatValue`,
  `toBigDecimal`, `toBigInteger`, `unscaledValue()`, `getScale()`,
  `getScaleMetrics()`, `getFactory()`, `toString()`.

## Scale metrics — `org.decimal4j.scale`

`Scale5f.INSTANCE` (one enum constant per scale) implements `ScaleMetrics`:
- `int getScale()`, `long getScaleFactor()`, `BigInteger getScaleFactorAsBigInteger()`
- `long multiplyByScaleFactor(long)`, `long multiplyByScaleFactorExact(long)`,
  `long mulloByScaleFactor(int)`, `long mulhiByScaleFactor(int)`
- `long divideByScaleFactor(long)`, `long divideUnsignedByScaleFactor(long)`,
  `long moduloByScaleFactor(long)`
- `DecimalArithmetic getDefaultArithmetic()`, `getDefaultCheckedArithmetic()`,
  `getRoundingHalfUpArithmetic()`, `getArithmetic(RoundingMode)`,
  `getCheckedArithmetic(RoundingMode)`, `getArithmetic(TruncationPolicy)`

`org.decimal4j.scale.Scales` — `getScaleMetrics(int scale)`,
`findByScaleFactor(long)`, `VALUES`, `MIN_SCALE`, `MAX_SCALE`.

## Arithmetic back-ends — `org.decimal4j.api.DecimalArithmetic`

Obtained from `ScaleMetrics`; the implementations live in
`org.decimal4j.arithmetic` (`UncheckedScaleNfTruncatingArithmetic`,
`UncheckedScaleNfRoundingArithmetic`, `CheckedScale0fRoundingArithmetic`,
`CheckedScaleNfRoundingArithmetic`, …). All operate on raw unscaled `long`s:
- `int getScale()`, `ScaleMetrics getScaleMetrics()`, `RoundingMode getRoundingMode()`,
  `OverflowMode getOverflowMode()`, `TruncationPolicy getTruncationPolicy()`
- `long add|subtract|multiply|divide|pow|avg|invert|square|sqrt|negate|abs(long …)`
- conversions in: `fromLong(long)`, `fromFloat(float)`, `fromDouble(double)`,
  `fromBigDecimal(BigDecimal)`, `fromBigInteger(BigInteger)`,
  `fromUnscaled(long unscaledValue, int scale)`, `parse(String)`
- conversions out: `toLong`, `toFloat`, `toDouble`, `toBigDecimal`,
  `toUnscaled(long, int scale)`, `toString(long)`
- `DecimalArithmetic deriveArithmetic(RoundingMode|OverflowMode|TruncationPolicy)`

## Factories — `org.decimal4j.factory`

`Factory5f.INSTANCE` implements `DecimalFactory<Scale5f>`:
`getScale()`, `getScaleMetrics()`, `immutableType()`, `mutableType()`,
`valueOf(long|float|double|BigInteger|BigDecimal|String|Decimal<?>)` (with
`RoundingMode` overloads), `valueOfUnscaled(long[, int scale])`,
`newArray(int length)`, `newMutable()`.

`org.decimal4j.factory.Factories` — `getDecimalFactory(int|ScaleMetrics)`,
`getGenericDecimalFactory(int|ScaleMetrics)`, `VALUES`.

`org.decimal4j.generic.GenericDecimalFactory` —
`new GenericDecimalFactory<>(ScaleMetrics)` and the same `valueOf…` API,
returning `GenericImmutableDecimal` / `GenericMutableDecimal`.

## Rounding and overflow — `org.decimal4j.truncate`

- `DecimalRounding` — enum mirroring `RoundingMode`
  (`UP`, `DOWN`, `CEILING`, `FLOOR`, `HALF_UP`, `HALF_DOWN`, `HALF_EVEN`,
  `UNNECESSARY`); `getRoundingMode()`,
  `int calculateRoundingIncrement(int sign, long truncatedValue, TruncatedPart)`,
  `static DecimalRounding valueOf(RoundingMode)`.
- `TruncatedPart` — `ZERO`, `LESS_THAN_HALF_BUT_NOT_ZERO`, `EQUAL_TO_HALF`,
  `GREATER_THAN_HALF`; `boolean isGreaterThanZero()`,
  `boolean isGreaterEqualHalf()`,
  `static TruncatedPart valueOf(int firstTruncatedDigit, boolean zeroAfter)`.
- `OverflowMode` — `UNCHECKED`, `CHECKED`; `boolean isChecked()`.
- `TruncationPolicy`, and the two enums implementing it: `UncheckedRounding`
  (`toCheckedRounding()`) and `CheckedRounding` (`toUncheckedRounding()`), each
  with a constant per rounding mode and `static … valueOf(RoundingMode)`.

## Utilities — `org.decimal4j.util`

`DoubleRounder` — `new DoubleRounder(int precision)`,
`new DoubleRounder(ScaleMetrics)`, `int getPrecision()`,
`double round(double[, RoundingMode])`, and the statics
`DoubleRounder.round(double value, int precision[, RoundingMode])`.

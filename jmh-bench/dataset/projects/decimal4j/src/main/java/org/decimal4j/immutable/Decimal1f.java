/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2016 decimal4j (tools4j), Marco Terzer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.decimal4j.immutable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.base.AbstractImmutableDecimal;
import org.decimal4j.exact.Multipliable1f;
import org.decimal4j.factory.Factory1f;
import org.decimal4j.mutable.MutableDecimal1f;
import org.decimal4j.scale.Scale1f;

/**
 * <tt>Decimal1f</tt> represents an immutable decimal number with a fixed
 * number of 1 digits to the right of the decimal point.
 * <p>
 * All methods for this class throw {@code NullPointerException} when passed a
 * {@code null} object reference for any input parameter.
 */
public final class Decimal1f extends AbstractImmutableDecimal<Scale1f, Decimal1f> {

	private static final long serialVersionUID = 1L;

	/** Scale value 1 for {@code Decimal1f} returned by {@link #getScale()}.*/
	public static final int SCALE = 1;

	/** Scale metrics constant for {@code Decimal1f} returned by {@link #getScaleMetrics()}.*/
	public static final Scale1f METRICS = Scale1f.INSTANCE;

	/** Factory constant for {@code Decimal1f} returned by {@link #getFactory()}.*/
	public static final Factory1f FACTORY = Factory1f.INSTANCE;
	
	/**
	 * Default arithmetic for {@code Decimal1f} performing unchecked operations with rounding mode 
	 * {@link RoundingMode#HALF_UP HALF_UP}.
	 */
	public static final DecimalArithmetic DEFAULT_ARITHMETIC = METRICS.getDefaultArithmetic();
	
	/**
	 * Default arithmetic for {@code Decimal1f} performing checked operations with rounding mode 
	 * {@link RoundingMode#HALF_UP HALF_UP}.
	 */
	public static final DecimalArithmetic DEFAULT_CHECKED_ARITHMETIC = METRICS.getDefaultCheckedArithmetic();

	/** The unscaled long value that represents one.*/
	public static final long ONE_UNSCALED = METRICS.getScaleFactor();

	/** The {@code Decimal1f} constant zero.*/
	public static final Decimal1f ZERO = new Decimal1f(0);
    /**
     * A constant holding the smallest positive value a {@code Decimal1f}
     * can have, 10<sup>-1</sup>.
     */
	public static final Decimal1f ULP = new Decimal1f(1);

    /**
     * Initialize static constant array when class is loaded.
     */
    private static final int MAX_CONSTANT = 10;
    private static final Decimal1f POS_CONST[] = new Decimal1f[MAX_CONSTANT+1];
    private static final Decimal1f NEG_CONST[] = new Decimal1f[MAX_CONSTANT+1];

    static {
        for (int i = 1; i <= MAX_CONSTANT; i++) {
            POS_CONST[i] = new Decimal1f(ONE_UNSCALED * i);
            NEG_CONST[i] = new Decimal1f(-ONE_UNSCALED * i);
        }
    }

	/** The {@code Decimal1f} constant 1.*/
	public static final Decimal1f ONE = valueOf(1);
	/** The {@code Decimal1f} constant 2.*/
	public static final Decimal1f TWO = valueOf(2);
	/** The {@code Decimal1f} constant 3.*/
	public static final Decimal1f THREE = valueOf(3);
	/** The {@code Decimal1f} constant 4.*/
	public static final Decimal1f FOUR = valueOf(4);
	/** The {@code Decimal1f} constant 5.*/
	public static final Decimal1f FIVE = valueOf(5);
	/** The {@code Decimal1f} constant 6.*/
	public static final Decimal1f SIX = valueOf(6);
	/** The {@code Decimal1f} constant 7.*/
	public static final Decimal1f SEVEN = valueOf(7);
	/** The {@code Decimal1f} constant 8.*/
	public static final Decimal1f EIGHT = valueOf(8);
	/** The {@code Decimal1f} constant 9.*/
	public static final Decimal1f NINE = valueOf(9);
	/** The {@code Decimal1f} constant 10.*/
	public static final Decimal1f TEN = valueOf(10);
	/** The {@code Decimal1f} constant 100.*/
	public static final Decimal1f HUNDRED = new Decimal1f(100 * ONE_UNSCALED);
	/** The {@code Decimal1f} constant 1000.*/
	public static final Decimal1f THOUSAND = new Decimal1f(1000 * ONE_UNSCALED);
	/** The {@code Decimal1f} constant 10<sup>6</sup>.*/
	public static final Decimal1f MILLION = new Decimal1f(1000000 * ONE_UNSCALED);
	/** The {@code Decimal1f} constant 10<sup>9</sup>.*/
	public static final Decimal1f BILLION = new Decimal1f(1000000000 * ONE_UNSCALED);
	/** The {@code Decimal1f} constant 10<sup>12</sup>.*/
	public static final Decimal1f TRILLION = new Decimal1f(1000000000000L * ONE_UNSCALED);
	/** The {@code Decimal1f} constant 10<sup>15</sup>.*/
	public static final Decimal1f QUADRILLION = new Decimal1f(1000000000000000L * ONE_UNSCALED);

	/** The {@code Decimal1f} constant -1.*/
	public static final Decimal1f MINUS_ONE = valueOf(-1);

	/** The {@code Decimal1f} constant 0.5.*/
	public static final Decimal1f HALF = new Decimal1f(ONE_UNSCALED / 2);
	/** The {@code Decimal1f} constant 0.1.*/
	public static final Decimal1f TENTH = new Decimal1f(ONE_UNSCALED / 10);

    /**
     * A constant holding the maximum value a {@code Decimal1f} can have,
     * 922337203685477580.7.
     */
	public static final Decimal1f MAX_VALUE = new Decimal1f(Long.MAX_VALUE);
    /**
     * A constant holding the maximum integer value a {@code Decimal1f}
     * can have, 922337203685477580.0.
     */
	public static final Decimal1f MAX_INTEGER_VALUE = new Decimal1f((Long.MAX_VALUE / ONE_UNSCALED) * ONE_UNSCALED);
    /**
     * A constant holding the minimum value a {@code Decimal1f} can have,
     * -922337203685477580.8.
     */
	public static final Decimal1f MIN_VALUE = new Decimal1f(Long.MIN_VALUE);
    /**
     * A constant holding the minimum integer value a {@code Decimal1f}
     * can have, -922337203685477580.0.
     */
	public static final Decimal1f MIN_INTEGER_VALUE = new Decimal1f((Long.MIN_VALUE / ONE_UNSCALED) * ONE_UNSCALED);

	/**
	 * Private constructor with unscaled value.
	 *
	 * @param unscaled the unscaled value
	 */
	private Decimal1f(long unscaled) {
		super(unscaled);
	}

	/**
	 * Translates the string representation of a {@code Decimal} into a
	 * {@code Decimal1f}. The string representation consists of an
	 * optional sign, {@code '+'} or {@code '-'} , followed by a sequence of
	 * zero or more decimal digits ("the integer"), optionally followed by a
	 * fraction.
	 * <p>
	 * The fraction consists of a decimal point followed by zero or more decimal
	 * digits. The string must contain at least one digit in either the integer
	 * or the fraction. If the fraction contains more than 1 digits, the 
	 * value is rounded using {@link RoundingMode#HALF_UP HALF_UP} rounding. An 
	 * exception is thrown if the value is too large to be represented as a 
	 * {@code Decimal1f}.
	 *
	 * @param value
	 *            String value to convert into a {@code Decimal1f}
	 * @throws NumberFormatException
	 *             if {@code value} does not represent a valid {@code Decimal}
	 *             or if the value is too large to be represented as a 
	 *             {@code Decimal1f}
	 */
	public Decimal1f(String value) {
		super(DEFAULT_CHECKED_ARITHMETIC.parse(value));
	}
	
	@Override
	public final Scale1f getScaleMetrics() {
		return METRICS;
	}

	@Override
	public final int getScale() {
		return SCALE;
	}

	@Override
	public final Factory1f getFactory() {
		return FACTORY;
	}

	@Override
	protected final Decimal1f self() {
		return this;
	}

	@Override
	protected final DecimalArithmetic getDefaultArithmetic() {
		return DEFAULT_ARITHMETIC;
	}
	
	@Override
	protected final DecimalArithmetic getDefaultCheckedArithmetic() {
		return DEFAULT_CHECKED_ARITHMETIC;
	}
	
	@Override
	protected final DecimalArithmetic getRoundingDownArithmetic() {
		return METRICS.getRoundingDownArithmetic();
	}
	
	@Override
	protected final DecimalArithmetic getRoundingFloorArithmetic() {
		return METRICS.getRoundingFloorArithmetic();
	}
	
	@Override
	protected final DecimalArithmetic getRoundingHalfEvenArithmetic() {
		return METRICS.getRoundingHalfEvenArithmetic();
	}
	
	@Override
	protected final DecimalArithmetic getRoundingUnnecessaryArithmetic() {
		return METRICS.getRoundingUnnecessaryArithmetic();
	}

 	/**
	 * Returns a {@code Decimal1f} whose value is numerically equal to
	 * that of the specified {@code long} value. An exception is thrown if the
	 * specified value is too large to be represented as a {@code Decimal1f}.
	 *
	 * @param value
	 *            long value to convert into a {@code Decimal1f}
	 * @return a {@code Decimal1f} value numerically equal to the specified 
	 *            {@code long} value
	 * @throws IllegalArgumentException
	 *            if {@code value} is too large to be represented as a 
	 *            {@code Decimal1f}
	 */
	public static Decimal1f valueOf(long value) {
        if (value == 0)
            return ZERO;
        if (value > 0 & value <= MAX_CONSTANT)
            return POS_CONST[(int) value];
        else if (value < 0 & value >= -MAX_CONSTANT)
            return NEG_CONST[(int) -value];
		return valueOfUnscaled(DEFAULT_CHECKED_ARITHMETIC.fromLong(value));
	}

	/**
	 * Returns a {@code Decimal1f} whose value is calculated by
	 * rounding the specified {@code float} argument to scale 1
	 * using {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown
	 * if the specified value is too large to be represented as a {@code Decimal1f}. 
	 *
	 * @param value
	 *            float value to convert into a {@code Decimal1f}
	 * @return a {@code Decimal1f} calculated as: <tt>round<sub>HALF_UP</sub>(value)</tt>
	 * @throws IllegalArgumentException
	 *             if {@code value} is NaN or infinite or if the magnitude is
	 *             too large for the float to be represented as a {@code Decimal1f}
	 */
	public static Decimal1f valueOf(float value) {
		return valueOfUnscaled(DEFAULT_CHECKED_ARITHMETIC.fromFloat(value));
	}

	/**
	 * Returns a {@code Decimal1f} whose value is calculated by
	 * rounding the specified {@code float} argument to scale 1
	 * using the specified {@code roundingMode}. An exception is thrown
	 * if the specified value is too large to be represented as a {@code Decimal1f}. 
	 *
	 * @param value
	 *            float value to convert into a {@code Decimal1f}
	 * @param roundingMode
	 *            the rounding mode to apply during the conversion if necessary
	 * @return a {@code Decimal1f} calculated as: <tt>round(value)</tt>
	 * @throws IllegalArgumentException
	 *             if {@code value} is NaN or infinite or if the magnitude is
	 *             too large for the float to be represented as a {@code Decimal1f}
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is
	 *             necessary
	 */
	public static Decimal1f valueOf(float value, RoundingMode roundingMode) {
		return valueOfUnscaled(METRICS.getCheckedArithmetic(roundingMode).fromFloat(value));
	}

	/**
	 * Returns a {@code Decimal1f} whose value is calculated by
	 * rounding the specified {@code double} argument to scale 1
	 * using {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown
	 * if the specified value is too large to be represented as a {@code Decimal1f}. 
	 *
	 * @param value
	 *            double value to convert into a {@code Decimal1f}
	 * @return a {@code Decimal1f} calculated as: <tt>round<sub>HALF_UP</sub>(value)</tt>
	 * @throws IllegalArgumentException
	 *             if {@code value} is NaN or infinite or if the magnitude is
	 *             too large for the double to be represented as a {@code Decimal1f}
	 */
	public static Decimal1f valueOf(double value) {
		return valueOfUnscaled(DEFAULT_CHECKED_ARITHMETIC.fromDouble(value));
	}

	/**
	 * Returns a {@code Decimal1f} whose value is calculated by
	 * rounding the specified {@code double} argument to scale 1
	 * using the specified {@code roundingMode}. An exception is thrown
	 * if the specified value is too large to be represented as a {@code Decimal1f}. 
	 *
	 * @param value
	 *            double value to convert into a {@code Decimal1f}
	 * @param roundingMode
	 *            the rounding mode to apply during the conversion if necessary
	 * @return a {@code Decimal1f} calculated as: <tt>round(value)</tt>
	 * @throws IllegalArgumentException
	 *             if {@code value} is NaN or infinite or if the magnitude is
	 *             too large for the double to be represented as a {@code Decimal1f}
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is
	 *             necessary
	 */
	public static Decimal1f valueOf(double value, RoundingMode roundingMode) {
		return valueOfUnscaled(METRICS.getCheckedArithmetic(roundingMode).fromDouble(value));
	}

	/**
	 * Returns a {@code Decimal1f} whose value is numerically equal to that of
	 * the specified {@link BigInteger} value. An exception is thrown if the
	 * specified value is too large to be represented as a {@code Decimal1f}.
	 *
	 * @param value
	 *            {@code BigInteger} value to convert into a {@code Decimal1f}
	 * @return a {@code Decimal1f} value numerically equal to the specified big 
	 *         integer value
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a {@code Decimal1f}
	 */
	public static Decimal1f valueOf(BigInteger value) {
		return valueOfUnscaled(DEFAULT_CHECKED_ARITHMETIC.fromBigInteger(value));
	}

	/**
	 * Returns a {@code Decimal1f} whose value is calculated by rounding
	 * the specified {@link BigDecimal} argument to scale 1 using
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown if the 
	 * specified value is too large to be represented as a {@code Decimal1f}.
	 *
	 * @param value
	 *            {@code BigDecimal} value to convert into a {@code Decimal1f}
	 * @return a {@code Decimal1f} calculated as: <tt>round<sub>HALF_UP</sub>(value)</tt>
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a {@code Decimal1f}
	 */
	public static Decimal1f valueOf(BigDecimal value) {
		return valueOfUnscaled(DEFAULT_CHECKED_ARITHMETIC.fromBigDecimal(value));
	}

	/**
	 * Returns a {@code Decimal1f} whose value is calculated by rounding
	 * the specified {@link BigDecimal} argument to scale 1 using 
	 * the specified {@code roundingMode}. An exception is thrown if the 
	 * specified value is too large to be represented as a {@code Decimal1f}.
	 *
	 * @param value
	 *            {@code BigDecimal} value to convert into a {@code Decimal1f}
	 * @param roundingMode
	 *            the rounding mode to apply during the conversion if necessary
	 * @return a {@code Decimal1f} calculated as: <tt>round(value)</tt>
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a {@code Decimal1f}
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is
	 *             necessary
	 */
	public static Decimal1f valueOf(BigDecimal value, RoundingMode roundingMode) {
		return valueOfUnscaled(METRICS.getCheckedArithmetic(roundingMode).fromBigDecimal(value));
	}

	/**
	 * Returns a {@code Decimal1f} whose value is calculated by rounding
	 * the specified {@link Decimal} argument to scale 1 using
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown if the
	 * specified value is too large to be represented as a {@code Decimal1f}. 
	 *
	 * @param value
	 *            Decimal value to convert into a {@code Decimal1f} 
	 * @return a {@code Decimal1f} calculated as: <tt>round<sub>HALF_UP</sub>(value)</tt>
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a {@code Decimal1f}
	 */
	public static Decimal1f valueOf(Decimal<?> value) {
		if (value instanceof Decimal1f) {
			return (Decimal1f)value;
		}
		return valueOfUnscaled(value.unscaledValue(), value.getScale());
	}

	/**
	 * Returns a {@code Decimal1f} whose value is calculated by rounding
	 * the specified {@link Decimal} argument to scale 1 using
	 * the specified {@code roundingMode}. An exception is thrown if the
	 * specified value is too large to be represented as a {@code Decimal1f}. 
	 *
	 * @param value
	 *            Decimal value to convert into a {@code Decimal1f} 
	 * @param roundingMode
	 *            the rounding mode to apply during the conversion if necessary
	 * @return a {@code Decimal1f} calculated as: <tt>round(value)</tt>
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a {@code Decimal1f}
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is
	 *             necessary
	 */
	public static Decimal1f valueOf(Decimal<?> value, RoundingMode roundingMode) {
		if (value instanceof Decimal1f) {
			return (Decimal1f)value;
		}
		return valueOfUnscaled(value.unscaledValue(), value.getScale(), roundingMode);
	}

	/**
	 * Translates the string representation of a {@code Decimal} into a
	 * {@code Decimal1f}. The string representation consists of an
	 * optional sign, {@code '+'} or {@code '-'} , followed by a sequence of
	 * zero or more decimal digits ("the integer"), optionally followed by a
	 * fraction.
	 * <p>
	 * The fraction consists of a decimal point followed by zero or more decimal
	 * digits. The string must contain at least one digit in either the integer
	 * or the fraction. If the fraction contains more than 1 digits, the 
	 * value is rounded using {@link RoundingMode#HALF_UP HALF_UP} rounding. An 
	 * exception is thrown if the value is too large to be represented as a 
	 * {@code Decimal1f}.
	 *
	 * @param value
	 *            String value to convert into a {@code Decimal1f}
	 * @return a {@code Decimal1f} calculated as: <tt>round<sub>HALF_UP</sub>(value)</tt>
	 * @throws NumberFormatException
	 *             if {@code value} does not represent a valid {@code Decimal}
	 *             or if the value is too large to be represented as a 
	 *             {@code Decimal1f}
	 */
	public static Decimal1f valueOf(String value) {
		return valueOfUnscaled(DEFAULT_CHECKED_ARITHMETIC.parse(value));
	}

	/**
	 * Translates the string representation of a {@code Decimal} into a
	 * {@code Decimal1f}. The string representation consists of an
	 * optional sign, {@code '+'} or {@code '-'} , followed by a sequence of
	 * zero or more decimal digits ("the integer"), optionally followed by a
	 * fraction.
	 * <p>
	 * The fraction consists of a decimal point followed by zero or more decimal
	 * digits. The string must contain at least one digit in either the integer
	 * or the fraction. If the fraction contains more than 1 digits, the 
	 * value is rounded using the specified {@code roundingMode}. An exception 
	 * is thrown if the value is too large to be represented as a {@code Decimal1f}.
	 *
	 * @param value
	 *            String value to convert into a {@code Decimal1f}
	 * @param roundingMode
	 *            the rounding mode to apply if the fraction contains more than
	 *            1 digits
	 * @return a {@code Decimal1f} calculated as: <tt>round(value)</tt>
	 * @throws NumberFormatException
	 *             if {@code value} does not represent a valid {@code Decimal}
	 *             or if the value is too large to be represented as a 
	 *             {@code Decimal1f}
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is
	 *             necessary
	 */
	public static Decimal1f valueOf(String value, RoundingMode roundingMode) {
		return valueOfUnscaled(METRICS.getCheckedArithmetic(roundingMode).parse(value));
	}

	/**
	 * Returns a {@code Decimal1f} whose value is numerically equal to
	 * <tt>(unscaledValue &times; 10<sup>-1</sup>)</tt>.
	 *
	 * @param unscaledValue
	 *            unscaled value to convert into a {@code Decimal1f}
	 * @return a {@code Decimal1f} calculated as:
	 *         <tt>unscaledValue &times; 10<sup>-1</sup></tt>
	 */
	public static Decimal1f valueOfUnscaled(long unscaledValue) {
		if (unscaledValue == 0) {
			return ZERO;
		}
		if (unscaledValue == 1) {
			return ULP;
		}
		if (unscaledValue == ONE_UNSCALED) {
			return ONE;
		}
		if (unscaledValue == -ONE_UNSCALED) {
			return MINUS_ONE;
		}
		return new Decimal1f(unscaledValue);
	}

	/**
	 * Returns a {@code Decimal1f} whose value is numerically equal to
	 * <tt>(unscaledValue &times; 10<sup>-scale</sup>)</tt>. The result is
	 * rounded to scale 1 using {@link RoundingMode#HALF_UP HALF_UP} 
	 * rounding. An exception is thrown if the specified value is too large 
	 * to be represented as a {@code Decimal1f}.
	 *
	 * @param unscaledValue
	 *            unscaled value to convert into a {@code Decimal1f}
	 * @param scale
	 *            the scale to apply to {@code unscaledValue}
	 * @return a {@code Decimal1f} calculated as:
	 *         <tt>round<sub>HALF_UP</sub>(unscaledValue &times; 10<sup>-scale</sup>)</tt>
	 * @throws IllegalArgumentException
	 *             if the specified value is too large to be represented as a 
	 *             {@code Decimal1f}
	 */
	public static Decimal1f valueOfUnscaled(long unscaledValue, int scale) {
		return valueOfUnscaled(DEFAULT_CHECKED_ARITHMETIC.fromUnscaled(unscaledValue, scale));
	}

	/**
	 * Returns a {@code Decimal1f} whose value is numerically equal to
	 * <tt>(unscaledValue &times; 10<sup>-scale</sup>)</tt>. The result
	 * is rounded to scale 1 using the specified {@code roundingMode}. 
	 * An exception is thrown if the specified value is too large to be 
	 * represented as a {@code Decimal1f}.
	 *
	 * @param unscaledValue
	 *            unscaled value to convert into a Decimal1
	 * @param scale
	 *            the scale to apply to {@code unscaledValue}
	 * @param roundingMode
	 *            the rounding mode to apply during the conversion if necessary
	 * @return a {@code Decimal1f} calculated as:
	 *         <tt>round(unscaledValue &times; 10<sup>-scale</sup>)</tt>
	 * @throws IllegalArgumentException
	 *             if the specified value is too large to be represented as a {@code Decimal1f}
	 */
	public static Decimal1f valueOfUnscaled(long unscaledValue, int scale, RoundingMode roundingMode) {
		return valueOfUnscaled(METRICS.getCheckedArithmetic(roundingMode).fromUnscaled(unscaledValue, scale));
	}

	@Override
	protected Decimal1f createOrAssign(long unscaled) {
		return valueOfUnscaled(unscaled);
	}
	
	@Override
	protected Decimal1f create(long unscaled) {
		return valueOfUnscaled(unscaled);
	}
	
	@Override
	protected Decimal1f[] createArray(int length) {
		return new Decimal1f[length];
	}
	
	/**
	 * Returns this {@code Decimal} as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of {@code this} Decimal and the
	 * second factor passed to the {@code by(..)} method.
	 * <p>
	 * The method is similar to {@link #multiplyExact(Decimal) multiplyExact(Decimal)} but the result
	 * is retrieved in exact typed form with the correct result scale. 
	 * <p>
	 * For instance one can write:
	 * <pre>
	 * Decimal3f product = this.multiplyExact().by(Decimal2f.FIVE);
	 * </pre>
	 * 
	 * @return a multipliable object encapsulating this Decimal as first factor
	 *             of an exact multiplication
	 */
	public Multipliable1f multiplyExact() {
		return new Multipliable1f(this);
	}

	@Override
	public MutableDecimal1f toMutableDecimal() {
		return new MutableDecimal1f(this);
	}

	@Override
	public Decimal1f toImmutableDecimal() {
		return this;
	}
}

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
package org.decimal4j.mutable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.base.AbstractMutableDecimal;
import org.decimal4j.exact.Multipliable18f;
import org.decimal4j.factory.Factory18f;
import org.decimal4j.immutable.Decimal18f;
import org.decimal4j.scale.Scale18f;

/**
 * <tt>MutableDecimal18f</tt> represents a mutable decimal number with a fixed
 * number of 18 digits to the right of the decimal point.
 * <p>
 * All methods for this class throw {@code NullPointerException} when passed a
 * {@code null} object reference for any input parameter.
 */
public final class MutableDecimal18f extends AbstractMutableDecimal<Scale18f, MutableDecimal18f> implements Cloneable {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new {@code MutableDecimal18f} with value zero.
	 * @see #zero()
	 */
	public MutableDecimal18f() {
		super(0);
	}

	/**
	 * Private constructor with unscaled value.
	 *
	 * @param unscaledValue the unscaled value
	 * @param scale 		the scale metrics used to distinguish this constructor signature
	 *						from {@link #MutableDecimal18f(long)}
	 */
	private MutableDecimal18f(long unscaledValue, Scale18f scale) {
		super(unscaledValue);
	}

	/**
	 * Translates the string representation of a {@code Decimal} into a
	 * {@code MutableDecimal18f}. The string representation consists 
	 * of an optional sign, {@code '+'} or {@code '-'} , followed by a sequence 
	 * of zero or more decimal digits ("the integer"), optionally followed by a
	 * fraction.
	 * <p>
	 * The fraction consists of a decimal point followed by zero or more decimal
	 * digits. The string must contain at least one digit in either the integer
	 * or the fraction. If the fraction contains more than 18 digits, the 
	 * value is rounded using {@link RoundingMode#HALF_UP HALF_UP} rounding. An 
	 * exception is thrown if the value is too large to be represented as a 
	 * {@code MutableDecimal18f}.
	 *
	 * @param value
	 *            String value to convert into a {@code MutableDecimal18f}
	 * @throws NumberFormatException
	 *             if {@code value} does not represent a valid {@code Decimal}
	 *             or if the value is too large to be represented as a 
	 *             {@code MutableDecimal18f}
	 * @see #set(String, RoundingMode)
	 */
	public MutableDecimal18f(String value) {
		this();
		set(value);
	}

 	/**
	 * Constructs a {@code MutableDecimal18f} whose value is numerically equal 
	 * to that of the specified {@code long} value. An exception is thrown if the
	 * specified value is too large to be represented as a {@code MutableDecimal18f}.
	 *
	 * @param value
	 *            long value to convert into a {@code MutableDecimal18f}
	 * @throws IllegalArgumentException
	 *            if {@code value} is too large to be represented as a 
	 *            {@code MutableDecimal18f}
	 */
	public MutableDecimal18f(long value) {
		this();
		set(value);
	}

	/**
	 * Constructs a {@code MutableDecimal18f} whose value is calculated by
	 * rounding the specified {@code double} argument to scale 18 using
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown if the
	 * specified value is too large to be represented as a {@code MutableDecimal18f}. 
	 *
	 * @param value
	 *            double value to convert into a {@code MutableDecimal18f}
	 * @throws IllegalArgumentException
	 *             if {@code value} is NaN or infinite or if the magnitude is too large
	 *             for the double to be represented as a {@code MutableDecimal18f}
	 * @see #set(double, RoundingMode)
	 * @see #set(float)
	 * @see #set(float, RoundingMode)
	 */
	public MutableDecimal18f(double value) {
		this();
		set(value);
	}

	/**
	 * Constructs a {@code MutableDecimal18f} whose value is numerically equal to
	 * that of the specified {@link BigInteger} value. An exception is thrown if the
	 * specified value is too large to be represented as a {@code MutableDecimal18f}.
	 *
	 * @param value
	 *            {@code BigInteger} value to convert into a {@code MutableDecimal18f}
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a {@code MutableDecimal18f}
	 */
	public MutableDecimal18f(BigInteger value) {
		this();
		set(value);
	}

	/**
	 * Constructs a {@code MutableDecimal18f} whose value is calculated by
	 * rounding the specified {@link BigDecimal} argument to scale 18 using
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown if the 
	 * specified value is too large to be represented as a {@code MutableDecimal18f}.
	 *
	 * @param value
	 *            {@code BigDecimal} value to convert into a {@code MutableDecimal18f}
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a {@code MutableDecimal18f}
	 * @see #set(BigDecimal, RoundingMode)
	 */
	public MutableDecimal18f(BigDecimal value) {
		this();
		set(value);
	}

	/**
	 * Constructs a {@code MutableDecimal18f} whose value is numerically equal to
	 * that of the specified {@link Decimal18f} value.
	 *
	 * @param value
	 *            {@code Decimal18f} value to convert into a {@code MutableDecimal18f}
	 */
	public MutableDecimal18f(Decimal18f value) {
		this(value.unscaledValue(), Decimal18f.METRICS);
	}

	/**
	 * Constructs a {@code MutableDecimal18f} whose value is calculated by
	 * rounding the specified {@link Decimal} argument to scale 18 using
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown if 
	 * the specified value is too large to be represented as a {@code MutableDecimal18f}. 
	 *
	 * @param value
	 *            Decimal value to convert into a {@code MutableDecimal18f} 
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a {@code MutableDecimal18f}
	 * @see #set(Decimal, RoundingMode)
	 */
	public MutableDecimal18f(Decimal<?> value) {
		this();
		setUnscaled(value.unscaledValue(), value.getScale());
	}

	@Override
	protected final MutableDecimal18f create(long unscaled) {
		return new MutableDecimal18f(unscaled, Decimal18f.METRICS);
	}
	
	@Override
	protected final MutableDecimal18f[] createArray(int length) {
		return new MutableDecimal18f[length];
	}

	@Override
	protected final MutableDecimal18f self() {
		return this;
	}

	@Override
	public final Scale18f getScaleMetrics() {
		return Decimal18f.METRICS;
	}

	@Override
	public final int getScale() {
		return Decimal18f.SCALE;
	}

	@Override
	public Factory18f getFactory() {
		return Decimal18f.FACTORY;
	}
	
	@Override
	protected DecimalArithmetic getDefaultArithmetic() {
		return Decimal18f.DEFAULT_ARITHMETIC;
	}
	
	@Override
	protected DecimalArithmetic getDefaultCheckedArithmetic() {
		return Decimal18f.METRICS.getDefaultCheckedArithmetic();
	}

	@Override
	protected DecimalArithmetic getRoundingDownArithmetic() {
		return Decimal18f.METRICS.getRoundingDownArithmetic();
	}
	
	@Override
	protected DecimalArithmetic getRoundingFloorArithmetic() {
		return Decimal18f.METRICS.getRoundingFloorArithmetic();
	}
	
	@Override
	protected DecimalArithmetic getRoundingHalfEvenArithmetic() {
		return Decimal18f.METRICS.getRoundingHalfEvenArithmetic();
	}
	
	@Override
	protected DecimalArithmetic getRoundingUnnecessaryArithmetic() {
		return Decimal18f.METRICS.getRoundingUnnecessaryArithmetic();
	}

	@Override
	public MutableDecimal18f clone() {
		return new MutableDecimal18f(unscaledValue(), Decimal18f.METRICS);
	}

	/**
	 * Returns a new {@code MutableDecimal18f} whose value is equal to 
	 * <code>unscaledValue * 10<sup>-18</sup></code>.
	 * 
	 * @param unscaledValue
	 *            the unscaled decimal value to convert
	 * @return a new {@code MutableDecimal18f} value initialised with <code>unscaledValue * 10<sup>-18</sup></code>
	 * @see #setUnscaled(long, int)
	 * @see #setUnscaled(long, int, RoundingMode)
	 */
	public static MutableDecimal18f unscaled(long unscaledValue) {
		return new MutableDecimal18f(unscaledValue, Decimal18f.METRICS);
	}

	/**
	 * Returns a new {@code MutableDecimal18f} whose value is equal to zero.
	 * 
	 * @return a new {@code MutableDecimal18f} value initialised with 0.
	 */
	public static MutableDecimal18f zero() {
		return new MutableDecimal18f();
	}

	/**
	 * Returns a new {@code MutableDecimal18f} whose value is equal to one ULP.
	 * 
	 * @return a new {@code MutableDecimal18f} value initialised with 10<sup>-18</sup>.
	 */
	public static MutableDecimal18f ulp() {
		return new MutableDecimal18f(Decimal18f.ULP);
	}

	/**
	 * Returns a new {@code MutableDecimal18f} whose value is equal to one.
	 * 
	 * @return a new {@code MutableDecimal18f} value initialised with 1.
	 */
	public static MutableDecimal18f one() {
		return new MutableDecimal18f(Decimal18f.ONE);
	}

	/**
	 * Returns a new {@code MutableDecimal18f} whose value is equal to two.
	 * 
	 * @return a new {@code MutableDecimal18f} value initialised with 2.
	 */
	public static MutableDecimal18f two() {
		return new MutableDecimal18f(Decimal18f.TWO);
	}

	/**
	 * Returns a new {@code MutableDecimal18f} whose value is equal to three.
	 * 
	 * @return a new {@code MutableDecimal18f} value initialised with 3.
	 */
	public static MutableDecimal18f three() {
		return new MutableDecimal18f(Decimal18f.THREE);
	}

	/**
	 * Returns a new {@code MutableDecimal18f} whose value is equal to four.
	 * 
	 * @return a new {@code MutableDecimal18f} value initialised with 4.
	 */
	public static MutableDecimal18f four() {
		return new MutableDecimal18f(Decimal18f.FOUR);
	}

	/**
	 * Returns a new {@code MutableDecimal18f} whose value is equal to five.
	 * 
	 * @return a new {@code MutableDecimal18f} value initialised with 5.
	 */
	public static MutableDecimal18f five() {
		return new MutableDecimal18f(Decimal18f.FIVE);
	}

	/**
	 * Returns a new {@code MutableDecimal18f} whose value is equal to six.
	 * 
	 * @return a new {@code MutableDecimal18f} value initialised with 6.
	 */
	public static MutableDecimal18f six() {
		return new MutableDecimal18f(Decimal18f.SIX);
	}

	/**
	 * Returns a new {@code MutableDecimal18f} whose value is equal to seven.
	 * 
	 * @return a new {@code MutableDecimal18f} value initialised with 7.
	 */
	public static MutableDecimal18f seven() {
		return new MutableDecimal18f(Decimal18f.SEVEN);
	}

	/**
	 * Returns a new {@code MutableDecimal18f} whose value is equal to eight.
	 * 
	 * @return a new {@code MutableDecimal18f} value initialised with 8.
	 */
	public static MutableDecimal18f eight() {
		return new MutableDecimal18f(Decimal18f.EIGHT);
	}

	/**
	 * Returns a new {@code MutableDecimal18f} whose value is equal to nine.
	 * 
	 * @return a new {@code MutableDecimal18f} value initialised with 9.
	 */
	public static MutableDecimal18f nine() {
		return new MutableDecimal18f(Decimal18f.NINE);
	}


	/**
	 * Returns a new {@code MutableDecimal18f} whose value is equal to minus one.
	 * 
	 * @return a new {@code MutableDecimal18f} value initialised with -1.
	 */
	public static MutableDecimal18f minusOne() {
		return new MutableDecimal18f(Decimal18f.MINUS_ONE);
	}

	/**
	 * Returns a new {@code MutableDecimal18f} whose value is equal to one half.
	 * 
	 * @return a new {@code MutableDecimal18f} value initialised with 0.5.
	 */
	public static MutableDecimal18f half() {
		return new MutableDecimal18f(Decimal18f.HALF);
	}

	/**
	 * Returns a new {@code MutableDecimal18f} whose value is equal to one tenth.
	 * 
	 * @return a new {@code MutableDecimal18f} value initialised with 0.1.
	 */
	public static MutableDecimal18f tenth() {
		return new MutableDecimal18f(Decimal18f.TENTH);
	}

	/**
	 * Returns a new {@code MutableDecimal18f} whose value is equal to one hundredth.
	 * 
	 * @return a new {@code MutableDecimal18f} value initialised with 0.01.
	 */
	public static MutableDecimal18f hundredth() {
		return new MutableDecimal18f(Decimal18f.HUNDREDTH);
	}

	/**
	 * Returns a new {@code MutableDecimal18f} whose value is equal to one thousandth.
	 * 
	 * @return a new {@code MutableDecimal18f} value initialised with 0.001.
	 */
	public static MutableDecimal18f thousandth() {
		return new MutableDecimal18f(Decimal18f.THOUSANDTH);
	}

	/**
	 * Returns a new {@code MutableDecimal18f} whose value is equal to one millionth.
	 * 
	 * @return a new {@code MutableDecimal18f} value initialised with 10<sup>-6</sup>.
	 */
	public static MutableDecimal18f millionth() {
		return new MutableDecimal18f(Decimal18f.MILLIONTH);
	}

	/**
	 * Returns a new {@code MutableDecimal18f} whose value is equal to one billionth.
	 * 
	 * @return a new {@code MutableDecimal18f} value initialised with 10<sup>-9</sup>.
	 */
	public static MutableDecimal18f billionth() {
		return new MutableDecimal18f(Decimal18f.BILLIONTH);
	}

	/**
	 * Returns a new {@code MutableDecimal18f} whose value is equal to one trillionth.
	 * 
	 * @return a new {@code MutableDecimal18f} value initialised with 10<sup>-12</sup>.
	 */
	public static MutableDecimal18f trillionth() {
		return new MutableDecimal18f(Decimal18f.TRILLIONTH);
	}

	/**
	 * Returns a new {@code MutableDecimal18f} whose value is equal to one quadrillionth.
	 * 
	 * @return a new {@code MutableDecimal18f} value initialised with 10<sup>-15</sup>.
	 */
	public static MutableDecimal18f quadrillionth() {
		return new MutableDecimal18f(Decimal18f.QUADRILLIONTH);
	}

	/**
	 * Returns a new {@code MutableDecimal18f} whose value is equal to one quintillionth.
	 * 
	 * @return a new {@code MutableDecimal18f} value initialised with 10<sup>-18</sup>.
	 */
	public static MutableDecimal18f quintillionth() {
		return new MutableDecimal18f(Decimal18f.QUINTILLIONTH);
	}

	/**
	 * Returns this {@code Decimal} as a multipliable factor for exact 
	 * typed exact multiplication. The second factor is passed to one of
	 * the {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of {@code this} Decimal and the
	 * second factor passed to the {@code by(..)} method.
	 * <p>
	 * The method is similar to {@link #multiplyExact(Decimal) multiplyExact(Decimal)} but the result
	 * is retrieved in exact typed form with the correct result scale. 
	 * <p>
	 * For instance one can write:
	 * <pre>
	 * Decimal18f product = this.multiplyExact().by(Decimal0f.FIVE);
	 * </pre>
	 * 
	 * @return a multipliable object encapsulating this Decimal as first factor
	 *             of an exact multiplication
	 */
	public Multipliable18f multiplyExact() {
		return new Multipliable18f(this);
	}

	@Override
	public Decimal18f toImmutableDecimal() {
		return Decimal18f.valueOf(this);
	}

	@Override
	public MutableDecimal18f toMutableDecimal() {
		return this;
	}
}

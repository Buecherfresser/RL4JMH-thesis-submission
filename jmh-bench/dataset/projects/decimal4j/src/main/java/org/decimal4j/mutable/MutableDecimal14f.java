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
import org.decimal4j.exact.Multipliable14f;
import org.decimal4j.factory.Factory14f;
import org.decimal4j.immutable.Decimal14f;
import org.decimal4j.scale.Scale14f;

/**
 * <tt>MutableDecimal14f</tt> represents a mutable decimal number with a fixed
 * number of 14 digits to the right of the decimal point.
 * <p>
 * All methods for this class throw {@code NullPointerException} when passed a
 * {@code null} object reference for any input parameter.
 */
public final class MutableDecimal14f extends AbstractMutableDecimal<Scale14f, MutableDecimal14f> implements Cloneable {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new {@code MutableDecimal14f} with value zero.
	 * @see #zero()
	 */
	public MutableDecimal14f() {
		super(0);
	}

	/**
	 * Private constructor with unscaled value.
	 *
	 * @param unscaledValue the unscaled value
	 * @param scale 		the scale metrics used to distinguish this constructor signature
	 *						from {@link #MutableDecimal14f(long)}
	 */
	private MutableDecimal14f(long unscaledValue, Scale14f scale) {
		super(unscaledValue);
	}

	/**
	 * Translates the string representation of a {@code Decimal} into a
	 * {@code MutableDecimal14f}. The string representation consists 
	 * of an optional sign, {@code '+'} or {@code '-'} , followed by a sequence 
	 * of zero or more decimal digits ("the integer"), optionally followed by a
	 * fraction.
	 * <p>
	 * The fraction consists of a decimal point followed by zero or more decimal
	 * digits. The string must contain at least one digit in either the integer
	 * or the fraction. If the fraction contains more than 14 digits, the 
	 * value is rounded using {@link RoundingMode#HALF_UP HALF_UP} rounding. An 
	 * exception is thrown if the value is too large to be represented as a 
	 * {@code MutableDecimal14f}.
	 *
	 * @param value
	 *            String value to convert into a {@code MutableDecimal14f}
	 * @throws NumberFormatException
	 *             if {@code value} does not represent a valid {@code Decimal}
	 *             or if the value is too large to be represented as a 
	 *             {@code MutableDecimal14f}
	 * @see #set(String, RoundingMode)
	 */
	public MutableDecimal14f(String value) {
		this();
		set(value);
	}

 	/**
	 * Constructs a {@code MutableDecimal14f} whose value is numerically equal 
	 * to that of the specified {@code long} value. An exception is thrown if the
	 * specified value is too large to be represented as a {@code MutableDecimal14f}.
	 *
	 * @param value
	 *            long value to convert into a {@code MutableDecimal14f}
	 * @throws IllegalArgumentException
	 *            if {@code value} is too large to be represented as a 
	 *            {@code MutableDecimal14f}
	 */
	public MutableDecimal14f(long value) {
		this();
		set(value);
	}

	/**
	 * Constructs a {@code MutableDecimal14f} whose value is calculated by
	 * rounding the specified {@code double} argument to scale 14 using
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown if the
	 * specified value is too large to be represented as a {@code MutableDecimal14f}. 
	 *
	 * @param value
	 *            double value to convert into a {@code MutableDecimal14f}
	 * @throws IllegalArgumentException
	 *             if {@code value} is NaN or infinite or if the magnitude is too large
	 *             for the double to be represented as a {@code MutableDecimal14f}
	 * @see #set(double, RoundingMode)
	 * @see #set(float)
	 * @see #set(float, RoundingMode)
	 */
	public MutableDecimal14f(double value) {
		this();
		set(value);
	}

	/**
	 * Constructs a {@code MutableDecimal14f} whose value is numerically equal to
	 * that of the specified {@link BigInteger} value. An exception is thrown if the
	 * specified value is too large to be represented as a {@code MutableDecimal14f}.
	 *
	 * @param value
	 *            {@code BigInteger} value to convert into a {@code MutableDecimal14f}
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a {@code MutableDecimal14f}
	 */
	public MutableDecimal14f(BigInteger value) {
		this();
		set(value);
	}

	/**
	 * Constructs a {@code MutableDecimal14f} whose value is calculated by
	 * rounding the specified {@link BigDecimal} argument to scale 14 using
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown if the 
	 * specified value is too large to be represented as a {@code MutableDecimal14f}.
	 *
	 * @param value
	 *            {@code BigDecimal} value to convert into a {@code MutableDecimal14f}
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a {@code MutableDecimal14f}
	 * @see #set(BigDecimal, RoundingMode)
	 */
	public MutableDecimal14f(BigDecimal value) {
		this();
		set(value);
	}

	/**
	 * Constructs a {@code MutableDecimal14f} whose value is numerically equal to
	 * that of the specified {@link Decimal14f} value.
	 *
	 * @param value
	 *            {@code Decimal14f} value to convert into a {@code MutableDecimal14f}
	 */
	public MutableDecimal14f(Decimal14f value) {
		this(value.unscaledValue(), Decimal14f.METRICS);
	}

	/**
	 * Constructs a {@code MutableDecimal14f} whose value is calculated by
	 * rounding the specified {@link Decimal} argument to scale 14 using
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown if 
	 * the specified value is too large to be represented as a {@code MutableDecimal14f}. 
	 *
	 * @param value
	 *            Decimal value to convert into a {@code MutableDecimal14f} 
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a {@code MutableDecimal14f}
	 * @see #set(Decimal, RoundingMode)
	 */
	public MutableDecimal14f(Decimal<?> value) {
		this();
		setUnscaled(value.unscaledValue(), value.getScale());
	}

	@Override
	protected final MutableDecimal14f create(long unscaled) {
		return new MutableDecimal14f(unscaled, Decimal14f.METRICS);
	}
	
	@Override
	protected final MutableDecimal14f[] createArray(int length) {
		return new MutableDecimal14f[length];
	}

	@Override
	protected final MutableDecimal14f self() {
		return this;
	}

	@Override
	public final Scale14f getScaleMetrics() {
		return Decimal14f.METRICS;
	}

	@Override
	public final int getScale() {
		return Decimal14f.SCALE;
	}

	@Override
	public Factory14f getFactory() {
		return Decimal14f.FACTORY;
	}
	
	@Override
	protected DecimalArithmetic getDefaultArithmetic() {
		return Decimal14f.DEFAULT_ARITHMETIC;
	}
	
	@Override
	protected DecimalArithmetic getDefaultCheckedArithmetic() {
		return Decimal14f.METRICS.getDefaultCheckedArithmetic();
	}

	@Override
	protected DecimalArithmetic getRoundingDownArithmetic() {
		return Decimal14f.METRICS.getRoundingDownArithmetic();
	}
	
	@Override
	protected DecimalArithmetic getRoundingFloorArithmetic() {
		return Decimal14f.METRICS.getRoundingFloorArithmetic();
	}
	
	@Override
	protected DecimalArithmetic getRoundingHalfEvenArithmetic() {
		return Decimal14f.METRICS.getRoundingHalfEvenArithmetic();
	}
	
	@Override
	protected DecimalArithmetic getRoundingUnnecessaryArithmetic() {
		return Decimal14f.METRICS.getRoundingUnnecessaryArithmetic();
	}

	@Override
	public MutableDecimal14f clone() {
		return new MutableDecimal14f(unscaledValue(), Decimal14f.METRICS);
	}

	/**
	 * Returns a new {@code MutableDecimal14f} whose value is equal to 
	 * <code>unscaledValue * 10<sup>-14</sup></code>.
	 * 
	 * @param unscaledValue
	 *            the unscaled decimal value to convert
	 * @return a new {@code MutableDecimal14f} value initialised with <code>unscaledValue * 10<sup>-14</sup></code>
	 * @see #setUnscaled(long, int)
	 * @see #setUnscaled(long, int, RoundingMode)
	 */
	public static MutableDecimal14f unscaled(long unscaledValue) {
		return new MutableDecimal14f(unscaledValue, Decimal14f.METRICS);
	}

	/**
	 * Returns a new {@code MutableDecimal14f} whose value is equal to zero.
	 * 
	 * @return a new {@code MutableDecimal14f} value initialised with 0.
	 */
	public static MutableDecimal14f zero() {
		return new MutableDecimal14f();
	}

	/**
	 * Returns a new {@code MutableDecimal14f} whose value is equal to one ULP.
	 * 
	 * @return a new {@code MutableDecimal14f} value initialised with 10<sup>-14</sup>.
	 */
	public static MutableDecimal14f ulp() {
		return new MutableDecimal14f(Decimal14f.ULP);
	}

	/**
	 * Returns a new {@code MutableDecimal14f} whose value is equal to one.
	 * 
	 * @return a new {@code MutableDecimal14f} value initialised with 1.
	 */
	public static MutableDecimal14f one() {
		return new MutableDecimal14f(Decimal14f.ONE);
	}

	/**
	 * Returns a new {@code MutableDecimal14f} whose value is equal to two.
	 * 
	 * @return a new {@code MutableDecimal14f} value initialised with 2.
	 */
	public static MutableDecimal14f two() {
		return new MutableDecimal14f(Decimal14f.TWO);
	}

	/**
	 * Returns a new {@code MutableDecimal14f} whose value is equal to three.
	 * 
	 * @return a new {@code MutableDecimal14f} value initialised with 3.
	 */
	public static MutableDecimal14f three() {
		return new MutableDecimal14f(Decimal14f.THREE);
	}

	/**
	 * Returns a new {@code MutableDecimal14f} whose value is equal to four.
	 * 
	 * @return a new {@code MutableDecimal14f} value initialised with 4.
	 */
	public static MutableDecimal14f four() {
		return new MutableDecimal14f(Decimal14f.FOUR);
	}

	/**
	 * Returns a new {@code MutableDecimal14f} whose value is equal to five.
	 * 
	 * @return a new {@code MutableDecimal14f} value initialised with 5.
	 */
	public static MutableDecimal14f five() {
		return new MutableDecimal14f(Decimal14f.FIVE);
	}

	/**
	 * Returns a new {@code MutableDecimal14f} whose value is equal to six.
	 * 
	 * @return a new {@code MutableDecimal14f} value initialised with 6.
	 */
	public static MutableDecimal14f six() {
		return new MutableDecimal14f(Decimal14f.SIX);
	}

	/**
	 * Returns a new {@code MutableDecimal14f} whose value is equal to seven.
	 * 
	 * @return a new {@code MutableDecimal14f} value initialised with 7.
	 */
	public static MutableDecimal14f seven() {
		return new MutableDecimal14f(Decimal14f.SEVEN);
	}

	/**
	 * Returns a new {@code MutableDecimal14f} whose value is equal to eight.
	 * 
	 * @return a new {@code MutableDecimal14f} value initialised with 8.
	 */
	public static MutableDecimal14f eight() {
		return new MutableDecimal14f(Decimal14f.EIGHT);
	}

	/**
	 * Returns a new {@code MutableDecimal14f} whose value is equal to nine.
	 * 
	 * @return a new {@code MutableDecimal14f} value initialised with 9.
	 */
	public static MutableDecimal14f nine() {
		return new MutableDecimal14f(Decimal14f.NINE);
	}

	/**
	 * Returns a new {@code MutableDecimal14f} whose value is equal to ten.
	 * 
	 * @return a new {@code MutableDecimal14f} value initialised with 10.
	 */
	public static MutableDecimal14f ten() {
		return new MutableDecimal14f(Decimal14f.TEN);
	}
	/**
	 * Returns a new {@code MutableDecimal14f} whose value is equal to one hundred.
	 * 
	 * @return a new {@code MutableDecimal14f} value initialised with 100.
	 */
	public static MutableDecimal14f hundred() {
		return new MutableDecimal14f(Decimal14f.HUNDRED);
	}
	/**
	 * Returns a new {@code MutableDecimal14f} whose value is equal to one thousand.
	 * 
	 * @return a new {@code MutableDecimal14f} value initialised with 1000.
	 */
	public static MutableDecimal14f thousand() {
		return new MutableDecimal14f(Decimal14f.THOUSAND);
	}

	/**
	 * Returns a new {@code MutableDecimal14f} whose value is equal to minus one.
	 * 
	 * @return a new {@code MutableDecimal14f} value initialised with -1.
	 */
	public static MutableDecimal14f minusOne() {
		return new MutableDecimal14f(Decimal14f.MINUS_ONE);
	}

	/**
	 * Returns a new {@code MutableDecimal14f} whose value is equal to one half.
	 * 
	 * @return a new {@code MutableDecimal14f} value initialised with 0.5.
	 */
	public static MutableDecimal14f half() {
		return new MutableDecimal14f(Decimal14f.HALF);
	}

	/**
	 * Returns a new {@code MutableDecimal14f} whose value is equal to one tenth.
	 * 
	 * @return a new {@code MutableDecimal14f} value initialised with 0.1.
	 */
	public static MutableDecimal14f tenth() {
		return new MutableDecimal14f(Decimal14f.TENTH);
	}

	/**
	 * Returns a new {@code MutableDecimal14f} whose value is equal to one hundredth.
	 * 
	 * @return a new {@code MutableDecimal14f} value initialised with 0.01.
	 */
	public static MutableDecimal14f hundredth() {
		return new MutableDecimal14f(Decimal14f.HUNDREDTH);
	}

	/**
	 * Returns a new {@code MutableDecimal14f} whose value is equal to one thousandth.
	 * 
	 * @return a new {@code MutableDecimal14f} value initialised with 0.001.
	 */
	public static MutableDecimal14f thousandth() {
		return new MutableDecimal14f(Decimal14f.THOUSANDTH);
	}

	/**
	 * Returns a new {@code MutableDecimal14f} whose value is equal to one millionth.
	 * 
	 * @return a new {@code MutableDecimal14f} value initialised with 10<sup>-6</sup>.
	 */
	public static MutableDecimal14f millionth() {
		return new MutableDecimal14f(Decimal14f.MILLIONTH);
	}

	/**
	 * Returns a new {@code MutableDecimal14f} whose value is equal to one billionth.
	 * 
	 * @return a new {@code MutableDecimal14f} value initialised with 10<sup>-9</sup>.
	 */
	public static MutableDecimal14f billionth() {
		return new MutableDecimal14f(Decimal14f.BILLIONTH);
	}

	/**
	 * Returns a new {@code MutableDecimal14f} whose value is equal to one trillionth.
	 * 
	 * @return a new {@code MutableDecimal14f} value initialised with 10<sup>-12</sup>.
	 */
	public static MutableDecimal14f trillionth() {
		return new MutableDecimal14f(Decimal14f.TRILLIONTH);
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
	 * Decimal16f product = this.multiplyExact().by(Decimal2f.FIVE);
	 * </pre>
	 * 
	 * @return a multipliable object encapsulating this Decimal as first factor
	 *             of an exact multiplication
	 */
	public Multipliable14f multiplyExact() {
		return new Multipliable14f(this);
	}

	@Override
	public Decimal14f toImmutableDecimal() {
		return Decimal14f.valueOf(this);
	}

	@Override
	public MutableDecimal14f toMutableDecimal() {
		return this;
	}
}

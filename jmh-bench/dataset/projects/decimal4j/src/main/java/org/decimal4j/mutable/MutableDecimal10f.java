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
import org.decimal4j.exact.Multipliable10f;
import org.decimal4j.factory.Factory10f;
import org.decimal4j.immutable.Decimal10f;
import org.decimal4j.scale.Scale10f;

/**
 * <tt>MutableDecimal10f</tt> represents a mutable decimal number with a fixed
 * number of 10 digits to the right of the decimal point.
 * <p>
 * All methods for this class throw {@code NullPointerException} when passed a
 * {@code null} object reference for any input parameter.
 */
public final class MutableDecimal10f extends AbstractMutableDecimal<Scale10f, MutableDecimal10f> implements Cloneable {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new {@code MutableDecimal10f} with value zero.
	 * @see #zero()
	 */
	public MutableDecimal10f() {
		super(0);
	}

	/**
	 * Private constructor with unscaled value.
	 *
	 * @param unscaledValue the unscaled value
	 * @param scale 		the scale metrics used to distinguish this constructor signature
	 *						from {@link #MutableDecimal10f(long)}
	 */
	private MutableDecimal10f(long unscaledValue, Scale10f scale) {
		super(unscaledValue);
	}

	/**
	 * Translates the string representation of a {@code Decimal} into a
	 * {@code MutableDecimal10f}. The string representation consists 
	 * of an optional sign, {@code '+'} or {@code '-'} , followed by a sequence 
	 * of zero or more decimal digits ("the integer"), optionally followed by a
	 * fraction.
	 * <p>
	 * The fraction consists of a decimal point followed by zero or more decimal
	 * digits. The string must contain at least one digit in either the integer
	 * or the fraction. If the fraction contains more than 10 digits, the 
	 * value is rounded using {@link RoundingMode#HALF_UP HALF_UP} rounding. An 
	 * exception is thrown if the value is too large to be represented as a 
	 * {@code MutableDecimal10f}.
	 *
	 * @param value
	 *            String value to convert into a {@code MutableDecimal10f}
	 * @throws NumberFormatException
	 *             if {@code value} does not represent a valid {@code Decimal}
	 *             or if the value is too large to be represented as a 
	 *             {@code MutableDecimal10f}
	 * @see #set(String, RoundingMode)
	 */
	public MutableDecimal10f(String value) {
		this();
		set(value);
	}

 	/**
	 * Constructs a {@code MutableDecimal10f} whose value is numerically equal 
	 * to that of the specified {@code long} value. An exception is thrown if the
	 * specified value is too large to be represented as a {@code MutableDecimal10f}.
	 *
	 * @param value
	 *            long value to convert into a {@code MutableDecimal10f}
	 * @throws IllegalArgumentException
	 *            if {@code value} is too large to be represented as a 
	 *            {@code MutableDecimal10f}
	 */
	public MutableDecimal10f(long value) {
		this();
		set(value);
	}

	/**
	 * Constructs a {@code MutableDecimal10f} whose value is calculated by
	 * rounding the specified {@code double} argument to scale 10 using
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown if the
	 * specified value is too large to be represented as a {@code MutableDecimal10f}. 
	 *
	 * @param value
	 *            double value to convert into a {@code MutableDecimal10f}
	 * @throws IllegalArgumentException
	 *             if {@code value} is NaN or infinite or if the magnitude is too large
	 *             for the double to be represented as a {@code MutableDecimal10f}
	 * @see #set(double, RoundingMode)
	 * @see #set(float)
	 * @see #set(float, RoundingMode)
	 */
	public MutableDecimal10f(double value) {
		this();
		set(value);
	}

	/**
	 * Constructs a {@code MutableDecimal10f} whose value is numerically equal to
	 * that of the specified {@link BigInteger} value. An exception is thrown if the
	 * specified value is too large to be represented as a {@code MutableDecimal10f}.
	 *
	 * @param value
	 *            {@code BigInteger} value to convert into a {@code MutableDecimal10f}
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a {@code MutableDecimal10f}
	 */
	public MutableDecimal10f(BigInteger value) {
		this();
		set(value);
	}

	/**
	 * Constructs a {@code MutableDecimal10f} whose value is calculated by
	 * rounding the specified {@link BigDecimal} argument to scale 10 using
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown if the 
	 * specified value is too large to be represented as a {@code MutableDecimal10f}.
	 *
	 * @param value
	 *            {@code BigDecimal} value to convert into a {@code MutableDecimal10f}
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a {@code MutableDecimal10f}
	 * @see #set(BigDecimal, RoundingMode)
	 */
	public MutableDecimal10f(BigDecimal value) {
		this();
		set(value);
	}

	/**
	 * Constructs a {@code MutableDecimal10f} whose value is numerically equal to
	 * that of the specified {@link Decimal10f} value.
	 *
	 * @param value
	 *            {@code Decimal10f} value to convert into a {@code MutableDecimal10f}
	 */
	public MutableDecimal10f(Decimal10f value) {
		this(value.unscaledValue(), Decimal10f.METRICS);
	}

	/**
	 * Constructs a {@code MutableDecimal10f} whose value is calculated by
	 * rounding the specified {@link Decimal} argument to scale 10 using
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown if 
	 * the specified value is too large to be represented as a {@code MutableDecimal10f}. 
	 *
	 * @param value
	 *            Decimal value to convert into a {@code MutableDecimal10f} 
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a {@code MutableDecimal10f}
	 * @see #set(Decimal, RoundingMode)
	 */
	public MutableDecimal10f(Decimal<?> value) {
		this();
		setUnscaled(value.unscaledValue(), value.getScale());
	}

	@Override
	protected final MutableDecimal10f create(long unscaled) {
		return new MutableDecimal10f(unscaled, Decimal10f.METRICS);
	}
	
	@Override
	protected final MutableDecimal10f[] createArray(int length) {
		return new MutableDecimal10f[length];
	}

	@Override
	protected final MutableDecimal10f self() {
		return this;
	}

	@Override
	public final Scale10f getScaleMetrics() {
		return Decimal10f.METRICS;
	}

	@Override
	public final int getScale() {
		return Decimal10f.SCALE;
	}

	@Override
	public Factory10f getFactory() {
		return Decimal10f.FACTORY;
	}
	
	@Override
	protected DecimalArithmetic getDefaultArithmetic() {
		return Decimal10f.DEFAULT_ARITHMETIC;
	}
	
	@Override
	protected DecimalArithmetic getDefaultCheckedArithmetic() {
		return Decimal10f.METRICS.getDefaultCheckedArithmetic();
	}

	@Override
	protected DecimalArithmetic getRoundingDownArithmetic() {
		return Decimal10f.METRICS.getRoundingDownArithmetic();
	}
	
	@Override
	protected DecimalArithmetic getRoundingFloorArithmetic() {
		return Decimal10f.METRICS.getRoundingFloorArithmetic();
	}
	
	@Override
	protected DecimalArithmetic getRoundingHalfEvenArithmetic() {
		return Decimal10f.METRICS.getRoundingHalfEvenArithmetic();
	}
	
	@Override
	protected DecimalArithmetic getRoundingUnnecessaryArithmetic() {
		return Decimal10f.METRICS.getRoundingUnnecessaryArithmetic();
	}

	@Override
	public MutableDecimal10f clone() {
		return new MutableDecimal10f(unscaledValue(), Decimal10f.METRICS);
	}

	/**
	 * Returns a new {@code MutableDecimal10f} whose value is equal to 
	 * <code>unscaledValue * 10<sup>-10</sup></code>.
	 * 
	 * @param unscaledValue
	 *            the unscaled decimal value to convert
	 * @return a new {@code MutableDecimal10f} value initialised with <code>unscaledValue * 10<sup>-10</sup></code>
	 * @see #setUnscaled(long, int)
	 * @see #setUnscaled(long, int, RoundingMode)
	 */
	public static MutableDecimal10f unscaled(long unscaledValue) {
		return new MutableDecimal10f(unscaledValue, Decimal10f.METRICS);
	}

	/**
	 * Returns a new {@code MutableDecimal10f} whose value is equal to zero.
	 * 
	 * @return a new {@code MutableDecimal10f} value initialised with 0.
	 */
	public static MutableDecimal10f zero() {
		return new MutableDecimal10f();
	}

	/**
	 * Returns a new {@code MutableDecimal10f} whose value is equal to one ULP.
	 * 
	 * @return a new {@code MutableDecimal10f} value initialised with 10<sup>-10</sup>.
	 */
	public static MutableDecimal10f ulp() {
		return new MutableDecimal10f(Decimal10f.ULP);
	}

	/**
	 * Returns a new {@code MutableDecimal10f} whose value is equal to one.
	 * 
	 * @return a new {@code MutableDecimal10f} value initialised with 1.
	 */
	public static MutableDecimal10f one() {
		return new MutableDecimal10f(Decimal10f.ONE);
	}

	/**
	 * Returns a new {@code MutableDecimal10f} whose value is equal to two.
	 * 
	 * @return a new {@code MutableDecimal10f} value initialised with 2.
	 */
	public static MutableDecimal10f two() {
		return new MutableDecimal10f(Decimal10f.TWO);
	}

	/**
	 * Returns a new {@code MutableDecimal10f} whose value is equal to three.
	 * 
	 * @return a new {@code MutableDecimal10f} value initialised with 3.
	 */
	public static MutableDecimal10f three() {
		return new MutableDecimal10f(Decimal10f.THREE);
	}

	/**
	 * Returns a new {@code MutableDecimal10f} whose value is equal to four.
	 * 
	 * @return a new {@code MutableDecimal10f} value initialised with 4.
	 */
	public static MutableDecimal10f four() {
		return new MutableDecimal10f(Decimal10f.FOUR);
	}

	/**
	 * Returns a new {@code MutableDecimal10f} whose value is equal to five.
	 * 
	 * @return a new {@code MutableDecimal10f} value initialised with 5.
	 */
	public static MutableDecimal10f five() {
		return new MutableDecimal10f(Decimal10f.FIVE);
	}

	/**
	 * Returns a new {@code MutableDecimal10f} whose value is equal to six.
	 * 
	 * @return a new {@code MutableDecimal10f} value initialised with 6.
	 */
	public static MutableDecimal10f six() {
		return new MutableDecimal10f(Decimal10f.SIX);
	}

	/**
	 * Returns a new {@code MutableDecimal10f} whose value is equal to seven.
	 * 
	 * @return a new {@code MutableDecimal10f} value initialised with 7.
	 */
	public static MutableDecimal10f seven() {
		return new MutableDecimal10f(Decimal10f.SEVEN);
	}

	/**
	 * Returns a new {@code MutableDecimal10f} whose value is equal to eight.
	 * 
	 * @return a new {@code MutableDecimal10f} value initialised with 8.
	 */
	public static MutableDecimal10f eight() {
		return new MutableDecimal10f(Decimal10f.EIGHT);
	}

	/**
	 * Returns a new {@code MutableDecimal10f} whose value is equal to nine.
	 * 
	 * @return a new {@code MutableDecimal10f} value initialised with 9.
	 */
	public static MutableDecimal10f nine() {
		return new MutableDecimal10f(Decimal10f.NINE);
	}

	/**
	 * Returns a new {@code MutableDecimal10f} whose value is equal to ten.
	 * 
	 * @return a new {@code MutableDecimal10f} value initialised with 10.
	 */
	public static MutableDecimal10f ten() {
		return new MutableDecimal10f(Decimal10f.TEN);
	}
	/**
	 * Returns a new {@code MutableDecimal10f} whose value is equal to one hundred.
	 * 
	 * @return a new {@code MutableDecimal10f} value initialised with 100.
	 */
	public static MutableDecimal10f hundred() {
		return new MutableDecimal10f(Decimal10f.HUNDRED);
	}
	/**
	 * Returns a new {@code MutableDecimal10f} whose value is equal to one thousand.
	 * 
	 * @return a new {@code MutableDecimal10f} value initialised with 1000.
	 */
	public static MutableDecimal10f thousand() {
		return new MutableDecimal10f(Decimal10f.THOUSAND);
	}
	/**
	 * Returns a new {@code MutableDecimal10f} whose value is equal to one million.
	 * 
	 * @return a new {@code MutableDecimal10f} value initialised with 10<sup>6</sup>.
	 */
	public static MutableDecimal10f million() {
		return new MutableDecimal10f(Decimal10f.MILLION);
	}

	/**
	 * Returns a new {@code MutableDecimal10f} whose value is equal to minus one.
	 * 
	 * @return a new {@code MutableDecimal10f} value initialised with -1.
	 */
	public static MutableDecimal10f minusOne() {
		return new MutableDecimal10f(Decimal10f.MINUS_ONE);
	}

	/**
	 * Returns a new {@code MutableDecimal10f} whose value is equal to one half.
	 * 
	 * @return a new {@code MutableDecimal10f} value initialised with 0.5.
	 */
	public static MutableDecimal10f half() {
		return new MutableDecimal10f(Decimal10f.HALF);
	}

	/**
	 * Returns a new {@code MutableDecimal10f} whose value is equal to one tenth.
	 * 
	 * @return a new {@code MutableDecimal10f} value initialised with 0.1.
	 */
	public static MutableDecimal10f tenth() {
		return new MutableDecimal10f(Decimal10f.TENTH);
	}

	/**
	 * Returns a new {@code MutableDecimal10f} whose value is equal to one hundredth.
	 * 
	 * @return a new {@code MutableDecimal10f} value initialised with 0.01.
	 */
	public static MutableDecimal10f hundredth() {
		return new MutableDecimal10f(Decimal10f.HUNDREDTH);
	}

	/**
	 * Returns a new {@code MutableDecimal10f} whose value is equal to one thousandth.
	 * 
	 * @return a new {@code MutableDecimal10f} value initialised with 0.001.
	 */
	public static MutableDecimal10f thousandth() {
		return new MutableDecimal10f(Decimal10f.THOUSANDTH);
	}

	/**
	 * Returns a new {@code MutableDecimal10f} whose value is equal to one millionth.
	 * 
	 * @return a new {@code MutableDecimal10f} value initialised with 10<sup>-6</sup>.
	 */
	public static MutableDecimal10f millionth() {
		return new MutableDecimal10f(Decimal10f.MILLIONTH);
	}

	/**
	 * Returns a new {@code MutableDecimal10f} whose value is equal to one billionth.
	 * 
	 * @return a new {@code MutableDecimal10f} value initialised with 10<sup>-9</sup>.
	 */
	public static MutableDecimal10f billionth() {
		return new MutableDecimal10f(Decimal10f.BILLIONTH);
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
	 * Decimal12f product = this.multiplyExact().by(Decimal2f.FIVE);
	 * </pre>
	 * 
	 * @return a multipliable object encapsulating this Decimal as first factor
	 *             of an exact multiplication
	 */
	public Multipliable10f multiplyExact() {
		return new Multipliable10f(this);
	}

	@Override
	public Decimal10f toImmutableDecimal() {
		return Decimal10f.valueOf(this);
	}

	@Override
	public MutableDecimal10f toMutableDecimal() {
		return this;
	}
}

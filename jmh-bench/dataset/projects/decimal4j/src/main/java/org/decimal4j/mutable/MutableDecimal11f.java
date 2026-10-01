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
import org.decimal4j.exact.Multipliable11f;
import org.decimal4j.factory.Factory11f;
import org.decimal4j.immutable.Decimal11f;
import org.decimal4j.scale.Scale11f;

/**
 * <tt>MutableDecimal11f</tt> represents a mutable decimal number with a fixed
 * number of 11 digits to the right of the decimal point.
 * <p>
 * All methods for this class throw {@code NullPointerException} when passed a
 * {@code null} object reference for any input parameter.
 */
public final class MutableDecimal11f extends AbstractMutableDecimal<Scale11f, MutableDecimal11f> implements Cloneable {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new {@code MutableDecimal11f} with value zero.
	 * @see #zero()
	 */
	public MutableDecimal11f() {
		super(0);
	}

	/**
	 * Private constructor with unscaled value.
	 *
	 * @param unscaledValue the unscaled value
	 * @param scale 		the scale metrics used to distinguish this constructor signature
	 *						from {@link #MutableDecimal11f(long)}
	 */
	private MutableDecimal11f(long unscaledValue, Scale11f scale) {
		super(unscaledValue);
	}

	/**
	 * Translates the string representation of a {@code Decimal} into a
	 * {@code MutableDecimal11f}. The string representation consists 
	 * of an optional sign, {@code '+'} or {@code '-'} , followed by a sequence 
	 * of zero or more decimal digits ("the integer"), optionally followed by a
	 * fraction.
	 * <p>
	 * The fraction consists of a decimal point followed by zero or more decimal
	 * digits. The string must contain at least one digit in either the integer
	 * or the fraction. If the fraction contains more than 11 digits, the 
	 * value is rounded using {@link RoundingMode#HALF_UP HALF_UP} rounding. An 
	 * exception is thrown if the value is too large to be represented as a 
	 * {@code MutableDecimal11f}.
	 *
	 * @param value
	 *            String value to convert into a {@code MutableDecimal11f}
	 * @throws NumberFormatException
	 *             if {@code value} does not represent a valid {@code Decimal}
	 *             or if the value is too large to be represented as a 
	 *             {@code MutableDecimal11f}
	 * @see #set(String, RoundingMode)
	 */
	public MutableDecimal11f(String value) {
		this();
		set(value);
	}

 	/**
	 * Constructs a {@code MutableDecimal11f} whose value is numerically equal 
	 * to that of the specified {@code long} value. An exception is thrown if the
	 * specified value is too large to be represented as a {@code MutableDecimal11f}.
	 *
	 * @param value
	 *            long value to convert into a {@code MutableDecimal11f}
	 * @throws IllegalArgumentException
	 *            if {@code value} is too large to be represented as a 
	 *            {@code MutableDecimal11f}
	 */
	public MutableDecimal11f(long value) {
		this();
		set(value);
	}

	/**
	 * Constructs a {@code MutableDecimal11f} whose value is calculated by
	 * rounding the specified {@code double} argument to scale 11 using
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown if the
	 * specified value is too large to be represented as a {@code MutableDecimal11f}. 
	 *
	 * @param value
	 *            double value to convert into a {@code MutableDecimal11f}
	 * @throws IllegalArgumentException
	 *             if {@code value} is NaN or infinite or if the magnitude is too large
	 *             for the double to be represented as a {@code MutableDecimal11f}
	 * @see #set(double, RoundingMode)
	 * @see #set(float)
	 * @see #set(float, RoundingMode)
	 */
	public MutableDecimal11f(double value) {
		this();
		set(value);
	}

	/**
	 * Constructs a {@code MutableDecimal11f} whose value is numerically equal to
	 * that of the specified {@link BigInteger} value. An exception is thrown if the
	 * specified value is too large to be represented as a {@code MutableDecimal11f}.
	 *
	 * @param value
	 *            {@code BigInteger} value to convert into a {@code MutableDecimal11f}
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a {@code MutableDecimal11f}
	 */
	public MutableDecimal11f(BigInteger value) {
		this();
		set(value);
	}

	/**
	 * Constructs a {@code MutableDecimal11f} whose value is calculated by
	 * rounding the specified {@link BigDecimal} argument to scale 11 using
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown if the 
	 * specified value is too large to be represented as a {@code MutableDecimal11f}.
	 *
	 * @param value
	 *            {@code BigDecimal} value to convert into a {@code MutableDecimal11f}
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a {@code MutableDecimal11f}
	 * @see #set(BigDecimal, RoundingMode)
	 */
	public MutableDecimal11f(BigDecimal value) {
		this();
		set(value);
	}

	/**
	 * Constructs a {@code MutableDecimal11f} whose value is numerically equal to
	 * that of the specified {@link Decimal11f} value.
	 *
	 * @param value
	 *            {@code Decimal11f} value to convert into a {@code MutableDecimal11f}
	 */
	public MutableDecimal11f(Decimal11f value) {
		this(value.unscaledValue(), Decimal11f.METRICS);
	}

	/**
	 * Constructs a {@code MutableDecimal11f} whose value is calculated by
	 * rounding the specified {@link Decimal} argument to scale 11 using
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown if 
	 * the specified value is too large to be represented as a {@code MutableDecimal11f}. 
	 *
	 * @param value
	 *            Decimal value to convert into a {@code MutableDecimal11f} 
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a {@code MutableDecimal11f}
	 * @see #set(Decimal, RoundingMode)
	 */
	public MutableDecimal11f(Decimal<?> value) {
		this();
		setUnscaled(value.unscaledValue(), value.getScale());
	}

	@Override
	protected final MutableDecimal11f create(long unscaled) {
		return new MutableDecimal11f(unscaled, Decimal11f.METRICS);
	}
	
	@Override
	protected final MutableDecimal11f[] createArray(int length) {
		return new MutableDecimal11f[length];
	}

	@Override
	protected final MutableDecimal11f self() {
		return this;
	}

	@Override
	public final Scale11f getScaleMetrics() {
		return Decimal11f.METRICS;
	}

	@Override
	public final int getScale() {
		return Decimal11f.SCALE;
	}

	@Override
	public Factory11f getFactory() {
		return Decimal11f.FACTORY;
	}
	
	@Override
	protected DecimalArithmetic getDefaultArithmetic() {
		return Decimal11f.DEFAULT_ARITHMETIC;
	}
	
	@Override
	protected DecimalArithmetic getDefaultCheckedArithmetic() {
		return Decimal11f.METRICS.getDefaultCheckedArithmetic();
	}

	@Override
	protected DecimalArithmetic getRoundingDownArithmetic() {
		return Decimal11f.METRICS.getRoundingDownArithmetic();
	}
	
	@Override
	protected DecimalArithmetic getRoundingFloorArithmetic() {
		return Decimal11f.METRICS.getRoundingFloorArithmetic();
	}
	
	@Override
	protected DecimalArithmetic getRoundingHalfEvenArithmetic() {
		return Decimal11f.METRICS.getRoundingHalfEvenArithmetic();
	}
	
	@Override
	protected DecimalArithmetic getRoundingUnnecessaryArithmetic() {
		return Decimal11f.METRICS.getRoundingUnnecessaryArithmetic();
	}

	@Override
	public MutableDecimal11f clone() {
		return new MutableDecimal11f(unscaledValue(), Decimal11f.METRICS);
	}

	/**
	 * Returns a new {@code MutableDecimal11f} whose value is equal to 
	 * <code>unscaledValue * 10<sup>-11</sup></code>.
	 * 
	 * @param unscaledValue
	 *            the unscaled decimal value to convert
	 * @return a new {@code MutableDecimal11f} value initialised with <code>unscaledValue * 10<sup>-11</sup></code>
	 * @see #setUnscaled(long, int)
	 * @see #setUnscaled(long, int, RoundingMode)
	 */
	public static MutableDecimal11f unscaled(long unscaledValue) {
		return new MutableDecimal11f(unscaledValue, Decimal11f.METRICS);
	}

	/**
	 * Returns a new {@code MutableDecimal11f} whose value is equal to zero.
	 * 
	 * @return a new {@code MutableDecimal11f} value initialised with 0.
	 */
	public static MutableDecimal11f zero() {
		return new MutableDecimal11f();
	}

	/**
	 * Returns a new {@code MutableDecimal11f} whose value is equal to one ULP.
	 * 
	 * @return a new {@code MutableDecimal11f} value initialised with 10<sup>-11</sup>.
	 */
	public static MutableDecimal11f ulp() {
		return new MutableDecimal11f(Decimal11f.ULP);
	}

	/**
	 * Returns a new {@code MutableDecimal11f} whose value is equal to one.
	 * 
	 * @return a new {@code MutableDecimal11f} value initialised with 1.
	 */
	public static MutableDecimal11f one() {
		return new MutableDecimal11f(Decimal11f.ONE);
	}

	/**
	 * Returns a new {@code MutableDecimal11f} whose value is equal to two.
	 * 
	 * @return a new {@code MutableDecimal11f} value initialised with 2.
	 */
	public static MutableDecimal11f two() {
		return new MutableDecimal11f(Decimal11f.TWO);
	}

	/**
	 * Returns a new {@code MutableDecimal11f} whose value is equal to three.
	 * 
	 * @return a new {@code MutableDecimal11f} value initialised with 3.
	 */
	public static MutableDecimal11f three() {
		return new MutableDecimal11f(Decimal11f.THREE);
	}

	/**
	 * Returns a new {@code MutableDecimal11f} whose value is equal to four.
	 * 
	 * @return a new {@code MutableDecimal11f} value initialised with 4.
	 */
	public static MutableDecimal11f four() {
		return new MutableDecimal11f(Decimal11f.FOUR);
	}

	/**
	 * Returns a new {@code MutableDecimal11f} whose value is equal to five.
	 * 
	 * @return a new {@code MutableDecimal11f} value initialised with 5.
	 */
	public static MutableDecimal11f five() {
		return new MutableDecimal11f(Decimal11f.FIVE);
	}

	/**
	 * Returns a new {@code MutableDecimal11f} whose value is equal to six.
	 * 
	 * @return a new {@code MutableDecimal11f} value initialised with 6.
	 */
	public static MutableDecimal11f six() {
		return new MutableDecimal11f(Decimal11f.SIX);
	}

	/**
	 * Returns a new {@code MutableDecimal11f} whose value is equal to seven.
	 * 
	 * @return a new {@code MutableDecimal11f} value initialised with 7.
	 */
	public static MutableDecimal11f seven() {
		return new MutableDecimal11f(Decimal11f.SEVEN);
	}

	/**
	 * Returns a new {@code MutableDecimal11f} whose value is equal to eight.
	 * 
	 * @return a new {@code MutableDecimal11f} value initialised with 8.
	 */
	public static MutableDecimal11f eight() {
		return new MutableDecimal11f(Decimal11f.EIGHT);
	}

	/**
	 * Returns a new {@code MutableDecimal11f} whose value is equal to nine.
	 * 
	 * @return a new {@code MutableDecimal11f} value initialised with 9.
	 */
	public static MutableDecimal11f nine() {
		return new MutableDecimal11f(Decimal11f.NINE);
	}

	/**
	 * Returns a new {@code MutableDecimal11f} whose value is equal to ten.
	 * 
	 * @return a new {@code MutableDecimal11f} value initialised with 10.
	 */
	public static MutableDecimal11f ten() {
		return new MutableDecimal11f(Decimal11f.TEN);
	}
	/**
	 * Returns a new {@code MutableDecimal11f} whose value is equal to one hundred.
	 * 
	 * @return a new {@code MutableDecimal11f} value initialised with 100.
	 */
	public static MutableDecimal11f hundred() {
		return new MutableDecimal11f(Decimal11f.HUNDRED);
	}
	/**
	 * Returns a new {@code MutableDecimal11f} whose value is equal to one thousand.
	 * 
	 * @return a new {@code MutableDecimal11f} value initialised with 1000.
	 */
	public static MutableDecimal11f thousand() {
		return new MutableDecimal11f(Decimal11f.THOUSAND);
	}
	/**
	 * Returns a new {@code MutableDecimal11f} whose value is equal to one million.
	 * 
	 * @return a new {@code MutableDecimal11f} value initialised with 10<sup>6</sup>.
	 */
	public static MutableDecimal11f million() {
		return new MutableDecimal11f(Decimal11f.MILLION);
	}

	/**
	 * Returns a new {@code MutableDecimal11f} whose value is equal to minus one.
	 * 
	 * @return a new {@code MutableDecimal11f} value initialised with -1.
	 */
	public static MutableDecimal11f minusOne() {
		return new MutableDecimal11f(Decimal11f.MINUS_ONE);
	}

	/**
	 * Returns a new {@code MutableDecimal11f} whose value is equal to one half.
	 * 
	 * @return a new {@code MutableDecimal11f} value initialised with 0.5.
	 */
	public static MutableDecimal11f half() {
		return new MutableDecimal11f(Decimal11f.HALF);
	}

	/**
	 * Returns a new {@code MutableDecimal11f} whose value is equal to one tenth.
	 * 
	 * @return a new {@code MutableDecimal11f} value initialised with 0.1.
	 */
	public static MutableDecimal11f tenth() {
		return new MutableDecimal11f(Decimal11f.TENTH);
	}

	/**
	 * Returns a new {@code MutableDecimal11f} whose value is equal to one hundredth.
	 * 
	 * @return a new {@code MutableDecimal11f} value initialised with 0.01.
	 */
	public static MutableDecimal11f hundredth() {
		return new MutableDecimal11f(Decimal11f.HUNDREDTH);
	}

	/**
	 * Returns a new {@code MutableDecimal11f} whose value is equal to one thousandth.
	 * 
	 * @return a new {@code MutableDecimal11f} value initialised with 0.001.
	 */
	public static MutableDecimal11f thousandth() {
		return new MutableDecimal11f(Decimal11f.THOUSANDTH);
	}

	/**
	 * Returns a new {@code MutableDecimal11f} whose value is equal to one millionth.
	 * 
	 * @return a new {@code MutableDecimal11f} value initialised with 10<sup>-6</sup>.
	 */
	public static MutableDecimal11f millionth() {
		return new MutableDecimal11f(Decimal11f.MILLIONTH);
	}

	/**
	 * Returns a new {@code MutableDecimal11f} whose value is equal to one billionth.
	 * 
	 * @return a new {@code MutableDecimal11f} value initialised with 10<sup>-9</sup>.
	 */
	public static MutableDecimal11f billionth() {
		return new MutableDecimal11f(Decimal11f.BILLIONTH);
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
	 * Decimal13f product = this.multiplyExact().by(Decimal2f.FIVE);
	 * </pre>
	 * 
	 * @return a multipliable object encapsulating this Decimal as first factor
	 *             of an exact multiplication
	 */
	public Multipliable11f multiplyExact() {
		return new Multipliable11f(this);
	}

	@Override
	public Decimal11f toImmutableDecimal() {
		return Decimal11f.valueOf(this);
	}

	@Override
	public MutableDecimal11f toMutableDecimal() {
		return this;
	}
}

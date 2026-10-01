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
import org.decimal4j.exact.Multipliable0f;
import org.decimal4j.factory.Factory0f;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.scale.Scale0f;

/**
 * <tt>MutableDecimal0f</tt> represents a mutable decimal number with a fixed
 * number of 0 digits to the right of the decimal point.
 * <p>
 * All methods for this class throw {@code NullPointerException} when passed a
 * {@code null} object reference for any input parameter.
 */
public final class MutableDecimal0f extends AbstractMutableDecimal<Scale0f, MutableDecimal0f> implements Cloneable {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new {@code MutableDecimal0f} with value zero.
	 * @see #zero()
	 */
	public MutableDecimal0f() {
		super(0);
	}

	/**
	 * Private constructor with unscaled value.
	 *
	 * @param unscaledValue the unscaled value
	 * @param scale 		the scale metrics used to distinguish this constructor signature
	 *						from {@link #MutableDecimal0f(long)}
	 */
	private MutableDecimal0f(long unscaledValue, Scale0f scale) {
		super(unscaledValue);
	}

	/**
	 * Translates the string representation of a {@code Decimal} into a
	 * {@code MutableDecimal0f}. The string representation consists 
	 * of an optional sign, {@code '+'} or {@code '-'} , followed by a sequence 
	 * of zero or more decimal digits ("the integer"), optionally followed by a
	 * fraction.
	 * <p>
	 * The fraction consists of a decimal point followed by zero or more decimal
	 * digits. The string must contain at least one digit in either the integer
	 * or the fraction. If the fraction contains more than 0 digits, the 
	 * value is rounded using {@link RoundingMode#HALF_UP HALF_UP} rounding. An 
	 * exception is thrown if the value is too large to be represented as a 
	 * {@code MutableDecimal0f}.
	 *
	 * @param value
	 *            String value to convert into a {@code MutableDecimal0f}
	 * @throws NumberFormatException
	 *             if {@code value} does not represent a valid {@code Decimal}
	 *             or if the value is too large to be represented as a 
	 *             {@code MutableDecimal0f}
	 * @see #set(String, RoundingMode)
	 */
	public MutableDecimal0f(String value) {
		this();
		set(value);
	}

 	/**
	 * Constructs a {@code MutableDecimal0f} whose value is numerically equal 
	 * to that of the specified {@code long} value. An exception is thrown if the
	 * specified value is too large to be represented as a {@code MutableDecimal0f}.
	 *
	 * @param value
	 *            long value to convert into a {@code MutableDecimal0f}
	 * @throws IllegalArgumentException
	 *            if {@code value} is too large to be represented as a 
	 *            {@code MutableDecimal0f}
	 */
	public MutableDecimal0f(long value) {
		this();
		set(value);
	}

	/**
	 * Constructs a {@code MutableDecimal0f} whose value is calculated by
	 * rounding the specified {@code double} argument to scale 0 using
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown if the
	 * specified value is too large to be represented as a {@code MutableDecimal0f}. 
	 *
	 * @param value
	 *            double value to convert into a {@code MutableDecimal0f}
	 * @throws IllegalArgumentException
	 *             if {@code value} is NaN or infinite or if the magnitude is too large
	 *             for the double to be represented as a {@code MutableDecimal0f}
	 * @see #set(double, RoundingMode)
	 * @see #set(float)
	 * @see #set(float, RoundingMode)
	 */
	public MutableDecimal0f(double value) {
		this();
		set(value);
	}

	/**
	 * Constructs a {@code MutableDecimal0f} whose value is numerically equal to
	 * that of the specified {@link BigInteger} value. An exception is thrown if the
	 * specified value is too large to be represented as a {@code MutableDecimal0f}.
	 *
	 * @param value
	 *            {@code BigInteger} value to convert into a {@code MutableDecimal0f}
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a {@code MutableDecimal0f}
	 */
	public MutableDecimal0f(BigInteger value) {
		this();
		set(value);
	}

	/**
	 * Constructs a {@code MutableDecimal0f} whose value is calculated by
	 * rounding the specified {@link BigDecimal} argument to scale 0 using
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown if the 
	 * specified value is too large to be represented as a {@code MutableDecimal0f}.
	 *
	 * @param value
	 *            {@code BigDecimal} value to convert into a {@code MutableDecimal0f}
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a {@code MutableDecimal0f}
	 * @see #set(BigDecimal, RoundingMode)
	 */
	public MutableDecimal0f(BigDecimal value) {
		this();
		set(value);
	}

	/**
	 * Constructs a {@code MutableDecimal0f} whose value is numerically equal to
	 * that of the specified {@link Decimal0f} value.
	 *
	 * @param value
	 *            {@code Decimal0f} value to convert into a {@code MutableDecimal0f}
	 */
	public MutableDecimal0f(Decimal0f value) {
		this(value.unscaledValue(), Decimal0f.METRICS);
	}

	/**
	 * Constructs a {@code MutableDecimal0f} whose value is calculated by
	 * rounding the specified {@link Decimal} argument to scale 0 using
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown if 
	 * the specified value is too large to be represented as a {@code MutableDecimal0f}. 
	 *
	 * @param value
	 *            Decimal value to convert into a {@code MutableDecimal0f} 
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a {@code MutableDecimal0f}
	 * @see #set(Decimal, RoundingMode)
	 */
	public MutableDecimal0f(Decimal<?> value) {
		this();
		setUnscaled(value.unscaledValue(), value.getScale());
	}

	@Override
	protected final MutableDecimal0f create(long unscaled) {
		return new MutableDecimal0f(unscaled, Decimal0f.METRICS);
	}
	
	@Override
	protected final MutableDecimal0f[] createArray(int length) {
		return new MutableDecimal0f[length];
	}

	@Override
	protected final MutableDecimal0f self() {
		return this;
	}

	@Override
	public final Scale0f getScaleMetrics() {
		return Decimal0f.METRICS;
	}

	@Override
	public final int getScale() {
		return Decimal0f.SCALE;
	}

	@Override
	public Factory0f getFactory() {
		return Decimal0f.FACTORY;
	}
	
	@Override
	protected DecimalArithmetic getDefaultArithmetic() {
		return Decimal0f.DEFAULT_ARITHMETIC;
	}
	
	@Override
	protected DecimalArithmetic getDefaultCheckedArithmetic() {
		return Decimal0f.METRICS.getDefaultCheckedArithmetic();
	}

	@Override
	protected DecimalArithmetic getRoundingDownArithmetic() {
		return Decimal0f.METRICS.getRoundingDownArithmetic();
	}
	
	@Override
	protected DecimalArithmetic getRoundingFloorArithmetic() {
		return Decimal0f.METRICS.getRoundingFloorArithmetic();
	}
	
	@Override
	protected DecimalArithmetic getRoundingHalfEvenArithmetic() {
		return Decimal0f.METRICS.getRoundingHalfEvenArithmetic();
	}
	
	@Override
	protected DecimalArithmetic getRoundingUnnecessaryArithmetic() {
		return Decimal0f.METRICS.getRoundingUnnecessaryArithmetic();
	}

	@Override
	public MutableDecimal0f clone() {
		return new MutableDecimal0f(unscaledValue(), Decimal0f.METRICS);
	}

	/**
	 * Returns a new {@code MutableDecimal0f} whose value is equal to 
	 * <code>unscaledValue * 10<sup>-0</sup></code>.
	 * 
	 * @param unscaledValue
	 *            the unscaled decimal value to convert
	 * @return a new {@code MutableDecimal0f} value initialised with <code>unscaledValue * 10<sup>-0</sup></code>
	 * @see #setUnscaled(long, int)
	 * @see #setUnscaled(long, int, RoundingMode)
	 */
	public static MutableDecimal0f unscaled(long unscaledValue) {
		return new MutableDecimal0f(unscaledValue, Decimal0f.METRICS);
	}

	/**
	 * Returns a new {@code MutableDecimal0f} whose value is equal to zero.
	 * 
	 * @return a new {@code MutableDecimal0f} value initialised with 0.
	 */
	public static MutableDecimal0f zero() {
		return new MutableDecimal0f();
	}

	/**
	 * Returns a new {@code MutableDecimal0f} whose value is equal to one ULP.
	 * 
	 * @return a new {@code MutableDecimal0f} value initialised with 10<sup>-0</sup>=1.
	 */
	public static MutableDecimal0f ulp() {
		return new MutableDecimal0f(Decimal0f.ULP);
	}

	/**
	 * Returns a new {@code MutableDecimal0f} whose value is equal to one.
	 * 
	 * @return a new {@code MutableDecimal0f} value initialised with 1.
	 */
	public static MutableDecimal0f one() {
		return new MutableDecimal0f(Decimal0f.ONE);
	}

	/**
	 * Returns a new {@code MutableDecimal0f} whose value is equal to two.
	 * 
	 * @return a new {@code MutableDecimal0f} value initialised with 2.
	 */
	public static MutableDecimal0f two() {
		return new MutableDecimal0f(Decimal0f.TWO);
	}

	/**
	 * Returns a new {@code MutableDecimal0f} whose value is equal to three.
	 * 
	 * @return a new {@code MutableDecimal0f} value initialised with 3.
	 */
	public static MutableDecimal0f three() {
		return new MutableDecimal0f(Decimal0f.THREE);
	}

	/**
	 * Returns a new {@code MutableDecimal0f} whose value is equal to four.
	 * 
	 * @return a new {@code MutableDecimal0f} value initialised with 4.
	 */
	public static MutableDecimal0f four() {
		return new MutableDecimal0f(Decimal0f.FOUR);
	}

	/**
	 * Returns a new {@code MutableDecimal0f} whose value is equal to five.
	 * 
	 * @return a new {@code MutableDecimal0f} value initialised with 5.
	 */
	public static MutableDecimal0f five() {
		return new MutableDecimal0f(Decimal0f.FIVE);
	}

	/**
	 * Returns a new {@code MutableDecimal0f} whose value is equal to six.
	 * 
	 * @return a new {@code MutableDecimal0f} value initialised with 6.
	 */
	public static MutableDecimal0f six() {
		return new MutableDecimal0f(Decimal0f.SIX);
	}

	/**
	 * Returns a new {@code MutableDecimal0f} whose value is equal to seven.
	 * 
	 * @return a new {@code MutableDecimal0f} value initialised with 7.
	 */
	public static MutableDecimal0f seven() {
		return new MutableDecimal0f(Decimal0f.SEVEN);
	}

	/**
	 * Returns a new {@code MutableDecimal0f} whose value is equal to eight.
	 * 
	 * @return a new {@code MutableDecimal0f} value initialised with 8.
	 */
	public static MutableDecimal0f eight() {
		return new MutableDecimal0f(Decimal0f.EIGHT);
	}

	/**
	 * Returns a new {@code MutableDecimal0f} whose value is equal to nine.
	 * 
	 * @return a new {@code MutableDecimal0f} value initialised with 9.
	 */
	public static MutableDecimal0f nine() {
		return new MutableDecimal0f(Decimal0f.NINE);
	}

	/**
	 * Returns a new {@code MutableDecimal0f} whose value is equal to ten.
	 * 
	 * @return a new {@code MutableDecimal0f} value initialised with 10.
	 */
	public static MutableDecimal0f ten() {
		return new MutableDecimal0f(Decimal0f.TEN);
	}
	/**
	 * Returns a new {@code MutableDecimal0f} whose value is equal to one hundred.
	 * 
	 * @return a new {@code MutableDecimal0f} value initialised with 100.
	 */
	public static MutableDecimal0f hundred() {
		return new MutableDecimal0f(Decimal0f.HUNDRED);
	}
	/**
	 * Returns a new {@code MutableDecimal0f} whose value is equal to one thousand.
	 * 
	 * @return a new {@code MutableDecimal0f} value initialised with 1000.
	 */
	public static MutableDecimal0f thousand() {
		return new MutableDecimal0f(Decimal0f.THOUSAND);
	}
	/**
	 * Returns a new {@code MutableDecimal0f} whose value is equal to one million.
	 * 
	 * @return a new {@code MutableDecimal0f} value initialised with 10<sup>6</sup>.
	 */
	public static MutableDecimal0f million() {
		return new MutableDecimal0f(Decimal0f.MILLION);
	}
	/**
	 * Returns a new {@code MutableDecimal0f} whose value is equal to one billion.
	 * 
	 * @return a new {@code MutableDecimal0f} value initialised with 10<sup>9</sup>.
	 */
	public static MutableDecimal0f billion() {
		return new MutableDecimal0f(Decimal0f.BILLION);
	}
	/**
	 * Returns a new {@code MutableDecimal0f} whose value is equal to one trillion.
	 * 
	 * @return a new {@code MutableDecimal0f} value initialised with 10<sup>12</sup>.
	 */
	public static MutableDecimal0f trillion() {
		return new MutableDecimal0f(Decimal0f.TRILLION);
	}
	/**
	 * Returns a new {@code MutableDecimal0f} whose value is equal to one quadrillion.
	 * 
	 * @return a new {@code MutableDecimal0f} value initialised with 10<sup>15</sup>.
	 */
	public static MutableDecimal0f quadrillion() {
		return new MutableDecimal0f(Decimal0f.QUADRILLION);
	}
	/**
	 * Returns a new {@code MutableDecimal0f} whose value is equal to one quintillion.
	 * 
	 * @return a new {@code MutableDecimal0f} value initialised with 10<sup>18</sup>.
	 */
	public static MutableDecimal0f quintillion() {
		return new MutableDecimal0f(Decimal0f.QUINTILLION);
	}

	/**
	 * Returns a new {@code MutableDecimal0f} whose value is equal to minus one.
	 * 
	 * @return a new {@code MutableDecimal0f} value initialised with -1.
	 */
	public static MutableDecimal0f minusOne() {
		return new MutableDecimal0f(Decimal0f.MINUS_ONE);
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
	 * Decimal2f product = this.multiplyExact().by(Decimal2f.FIVE);
	 * </pre>
	 * 
	 * @return a multipliable object encapsulating this Decimal as first factor
	 *             of an exact multiplication
	 */
	public Multipliable0f multiplyExact() {
		return new Multipliable0f(this);
	}

	@Override
	public Decimal0f toImmutableDecimal() {
		return Decimal0f.valueOf(this);
	}

	@Override
	public MutableDecimal0f toMutableDecimal() {
		return this;
	}
}

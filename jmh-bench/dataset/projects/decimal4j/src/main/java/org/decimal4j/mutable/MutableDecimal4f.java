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
import org.decimal4j.exact.Multipliable4f;
import org.decimal4j.factory.Factory4f;
import org.decimal4j.immutable.Decimal4f;
import org.decimal4j.scale.Scale4f;

/**
 * <tt>MutableDecimal4f</tt> represents a mutable decimal number with a fixed
 * number of 4 digits to the right of the decimal point.
 * <p>
 * All methods for this class throw {@code NullPointerException} when passed a
 * {@code null} object reference for any input parameter.
 */
public final class MutableDecimal4f extends AbstractMutableDecimal<Scale4f, MutableDecimal4f> implements Cloneable {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new {@code MutableDecimal4f} with value zero.
	 * @see #zero()
	 */
	public MutableDecimal4f() {
		super(0);
	}

	/**
	 * Private constructor with unscaled value.
	 *
	 * @param unscaledValue the unscaled value
	 * @param scale 		the scale metrics used to distinguish this constructor signature
	 *						from {@link #MutableDecimal4f(long)}
	 */
	private MutableDecimal4f(long unscaledValue, Scale4f scale) {
		super(unscaledValue);
	}

	/**
	 * Translates the string representation of a {@code Decimal} into a
	 * {@code MutableDecimal4f}. The string representation consists 
	 * of an optional sign, {@code '+'} or {@code '-'} , followed by a sequence 
	 * of zero or more decimal digits ("the integer"), optionally followed by a
	 * fraction.
	 * <p>
	 * The fraction consists of a decimal point followed by zero or more decimal
	 * digits. The string must contain at least one digit in either the integer
	 * or the fraction. If the fraction contains more than 4 digits, the 
	 * value is rounded using {@link RoundingMode#HALF_UP HALF_UP} rounding. An 
	 * exception is thrown if the value is too large to be represented as a 
	 * {@code MutableDecimal4f}.
	 *
	 * @param value
	 *            String value to convert into a {@code MutableDecimal4f}
	 * @throws NumberFormatException
	 *             if {@code value} does not represent a valid {@code Decimal}
	 *             or if the value is too large to be represented as a 
	 *             {@code MutableDecimal4f}
	 * @see #set(String, RoundingMode)
	 */
	public MutableDecimal4f(String value) {
		this();
		set(value);
	}

 	/**
	 * Constructs a {@code MutableDecimal4f} whose value is numerically equal 
	 * to that of the specified {@code long} value. An exception is thrown if the
	 * specified value is too large to be represented as a {@code MutableDecimal4f}.
	 *
	 * @param value
	 *            long value to convert into a {@code MutableDecimal4f}
	 * @throws IllegalArgumentException
	 *            if {@code value} is too large to be represented as a 
	 *            {@code MutableDecimal4f}
	 */
	public MutableDecimal4f(long value) {
		this();
		set(value);
	}

	/**
	 * Constructs a {@code MutableDecimal4f} whose value is calculated by
	 * rounding the specified {@code double} argument to scale 4 using
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown if the
	 * specified value is too large to be represented as a {@code MutableDecimal4f}. 
	 *
	 * @param value
	 *            double value to convert into a {@code MutableDecimal4f}
	 * @throws IllegalArgumentException
	 *             if {@code value} is NaN or infinite or if the magnitude is too large
	 *             for the double to be represented as a {@code MutableDecimal4f}
	 * @see #set(double, RoundingMode)
	 * @see #set(float)
	 * @see #set(float, RoundingMode)
	 */
	public MutableDecimal4f(double value) {
		this();
		set(value);
	}

	/**
	 * Constructs a {@code MutableDecimal4f} whose value is numerically equal to
	 * that of the specified {@link BigInteger} value. An exception is thrown if the
	 * specified value is too large to be represented as a {@code MutableDecimal4f}.
	 *
	 * @param value
	 *            {@code BigInteger} value to convert into a {@code MutableDecimal4f}
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a {@code MutableDecimal4f}
	 */
	public MutableDecimal4f(BigInteger value) {
		this();
		set(value);
	}

	/**
	 * Constructs a {@code MutableDecimal4f} whose value is calculated by
	 * rounding the specified {@link BigDecimal} argument to scale 4 using
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown if the 
	 * specified value is too large to be represented as a {@code MutableDecimal4f}.
	 *
	 * @param value
	 *            {@code BigDecimal} value to convert into a {@code MutableDecimal4f}
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a {@code MutableDecimal4f}
	 * @see #set(BigDecimal, RoundingMode)
	 */
	public MutableDecimal4f(BigDecimal value) {
		this();
		set(value);
	}

	/**
	 * Constructs a {@code MutableDecimal4f} whose value is numerically equal to
	 * that of the specified {@link Decimal4f} value.
	 *
	 * @param value
	 *            {@code Decimal4f} value to convert into a {@code MutableDecimal4f}
	 */
	public MutableDecimal4f(Decimal4f value) {
		this(value.unscaledValue(), Decimal4f.METRICS);
	}

	/**
	 * Constructs a {@code MutableDecimal4f} whose value is calculated by
	 * rounding the specified {@link Decimal} argument to scale 4 using
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown if 
	 * the specified value is too large to be represented as a {@code MutableDecimal4f}. 
	 *
	 * @param value
	 *            Decimal value to convert into a {@code MutableDecimal4f} 
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a {@code MutableDecimal4f}
	 * @see #set(Decimal, RoundingMode)
	 */
	public MutableDecimal4f(Decimal<?> value) {
		this();
		setUnscaled(value.unscaledValue(), value.getScale());
	}

	@Override
	protected final MutableDecimal4f create(long unscaled) {
		return new MutableDecimal4f(unscaled, Decimal4f.METRICS);
	}
	
	@Override
	protected final MutableDecimal4f[] createArray(int length) {
		return new MutableDecimal4f[length];
	}

	@Override
	protected final MutableDecimal4f self() {
		return this;
	}

	@Override
	public final Scale4f getScaleMetrics() {
		return Decimal4f.METRICS;
	}

	@Override
	public final int getScale() {
		return Decimal4f.SCALE;
	}

	@Override
	public Factory4f getFactory() {
		return Decimal4f.FACTORY;
	}
	
	@Override
	protected DecimalArithmetic getDefaultArithmetic() {
		return Decimal4f.DEFAULT_ARITHMETIC;
	}
	
	@Override
	protected DecimalArithmetic getDefaultCheckedArithmetic() {
		return Decimal4f.METRICS.getDefaultCheckedArithmetic();
	}

	@Override
	protected DecimalArithmetic getRoundingDownArithmetic() {
		return Decimal4f.METRICS.getRoundingDownArithmetic();
	}
	
	@Override
	protected DecimalArithmetic getRoundingFloorArithmetic() {
		return Decimal4f.METRICS.getRoundingFloorArithmetic();
	}
	
	@Override
	protected DecimalArithmetic getRoundingHalfEvenArithmetic() {
		return Decimal4f.METRICS.getRoundingHalfEvenArithmetic();
	}
	
	@Override
	protected DecimalArithmetic getRoundingUnnecessaryArithmetic() {
		return Decimal4f.METRICS.getRoundingUnnecessaryArithmetic();
	}

	@Override
	public MutableDecimal4f clone() {
		return new MutableDecimal4f(unscaledValue(), Decimal4f.METRICS);
	}

	/**
	 * Returns a new {@code MutableDecimal4f} whose value is equal to 
	 * <code>unscaledValue * 10<sup>-4</sup></code>.
	 * 
	 * @param unscaledValue
	 *            the unscaled decimal value to convert
	 * @return a new {@code MutableDecimal4f} value initialised with <code>unscaledValue * 10<sup>-4</sup></code>
	 * @see #setUnscaled(long, int)
	 * @see #setUnscaled(long, int, RoundingMode)
	 */
	public static MutableDecimal4f unscaled(long unscaledValue) {
		return new MutableDecimal4f(unscaledValue, Decimal4f.METRICS);
	}

	/**
	 * Returns a new {@code MutableDecimal4f} whose value is equal to zero.
	 * 
	 * @return a new {@code MutableDecimal4f} value initialised with 0.
	 */
	public static MutableDecimal4f zero() {
		return new MutableDecimal4f();
	}

	/**
	 * Returns a new {@code MutableDecimal4f} whose value is equal to one ULP.
	 * 
	 * @return a new {@code MutableDecimal4f} value initialised with 10<sup>-4</sup>.
	 */
	public static MutableDecimal4f ulp() {
		return new MutableDecimal4f(Decimal4f.ULP);
	}

	/**
	 * Returns a new {@code MutableDecimal4f} whose value is equal to one.
	 * 
	 * @return a new {@code MutableDecimal4f} value initialised with 1.
	 */
	public static MutableDecimal4f one() {
		return new MutableDecimal4f(Decimal4f.ONE);
	}

	/**
	 * Returns a new {@code MutableDecimal4f} whose value is equal to two.
	 * 
	 * @return a new {@code MutableDecimal4f} value initialised with 2.
	 */
	public static MutableDecimal4f two() {
		return new MutableDecimal4f(Decimal4f.TWO);
	}

	/**
	 * Returns a new {@code MutableDecimal4f} whose value is equal to three.
	 * 
	 * @return a new {@code MutableDecimal4f} value initialised with 3.
	 */
	public static MutableDecimal4f three() {
		return new MutableDecimal4f(Decimal4f.THREE);
	}

	/**
	 * Returns a new {@code MutableDecimal4f} whose value is equal to four.
	 * 
	 * @return a new {@code MutableDecimal4f} value initialised with 4.
	 */
	public static MutableDecimal4f four() {
		return new MutableDecimal4f(Decimal4f.FOUR);
	}

	/**
	 * Returns a new {@code MutableDecimal4f} whose value is equal to five.
	 * 
	 * @return a new {@code MutableDecimal4f} value initialised with 5.
	 */
	public static MutableDecimal4f five() {
		return new MutableDecimal4f(Decimal4f.FIVE);
	}

	/**
	 * Returns a new {@code MutableDecimal4f} whose value is equal to six.
	 * 
	 * @return a new {@code MutableDecimal4f} value initialised with 6.
	 */
	public static MutableDecimal4f six() {
		return new MutableDecimal4f(Decimal4f.SIX);
	}

	/**
	 * Returns a new {@code MutableDecimal4f} whose value is equal to seven.
	 * 
	 * @return a new {@code MutableDecimal4f} value initialised with 7.
	 */
	public static MutableDecimal4f seven() {
		return new MutableDecimal4f(Decimal4f.SEVEN);
	}

	/**
	 * Returns a new {@code MutableDecimal4f} whose value is equal to eight.
	 * 
	 * @return a new {@code MutableDecimal4f} value initialised with 8.
	 */
	public static MutableDecimal4f eight() {
		return new MutableDecimal4f(Decimal4f.EIGHT);
	}

	/**
	 * Returns a new {@code MutableDecimal4f} whose value is equal to nine.
	 * 
	 * @return a new {@code MutableDecimal4f} value initialised with 9.
	 */
	public static MutableDecimal4f nine() {
		return new MutableDecimal4f(Decimal4f.NINE);
	}

	/**
	 * Returns a new {@code MutableDecimal4f} whose value is equal to ten.
	 * 
	 * @return a new {@code MutableDecimal4f} value initialised with 10.
	 */
	public static MutableDecimal4f ten() {
		return new MutableDecimal4f(Decimal4f.TEN);
	}
	/**
	 * Returns a new {@code MutableDecimal4f} whose value is equal to one hundred.
	 * 
	 * @return a new {@code MutableDecimal4f} value initialised with 100.
	 */
	public static MutableDecimal4f hundred() {
		return new MutableDecimal4f(Decimal4f.HUNDRED);
	}
	/**
	 * Returns a new {@code MutableDecimal4f} whose value is equal to one thousand.
	 * 
	 * @return a new {@code MutableDecimal4f} value initialised with 1000.
	 */
	public static MutableDecimal4f thousand() {
		return new MutableDecimal4f(Decimal4f.THOUSAND);
	}
	/**
	 * Returns a new {@code MutableDecimal4f} whose value is equal to one million.
	 * 
	 * @return a new {@code MutableDecimal4f} value initialised with 10<sup>6</sup>.
	 */
	public static MutableDecimal4f million() {
		return new MutableDecimal4f(Decimal4f.MILLION);
	}
	/**
	 * Returns a new {@code MutableDecimal4f} whose value is equal to one billion.
	 * 
	 * @return a new {@code MutableDecimal4f} value initialised with 10<sup>9</sup>.
	 */
	public static MutableDecimal4f billion() {
		return new MutableDecimal4f(Decimal4f.BILLION);
	}
	/**
	 * Returns a new {@code MutableDecimal4f} whose value is equal to one trillion.
	 * 
	 * @return a new {@code MutableDecimal4f} value initialised with 10<sup>12</sup>.
	 */
	public static MutableDecimal4f trillion() {
		return new MutableDecimal4f(Decimal4f.TRILLION);
	}

	/**
	 * Returns a new {@code MutableDecimal4f} whose value is equal to minus one.
	 * 
	 * @return a new {@code MutableDecimal4f} value initialised with -1.
	 */
	public static MutableDecimal4f minusOne() {
		return new MutableDecimal4f(Decimal4f.MINUS_ONE);
	}

	/**
	 * Returns a new {@code MutableDecimal4f} whose value is equal to one half.
	 * 
	 * @return a new {@code MutableDecimal4f} value initialised with 0.5.
	 */
	public static MutableDecimal4f half() {
		return new MutableDecimal4f(Decimal4f.HALF);
	}

	/**
	 * Returns a new {@code MutableDecimal4f} whose value is equal to one tenth.
	 * 
	 * @return a new {@code MutableDecimal4f} value initialised with 0.1.
	 */
	public static MutableDecimal4f tenth() {
		return new MutableDecimal4f(Decimal4f.TENTH);
	}

	/**
	 * Returns a new {@code MutableDecimal4f} whose value is equal to one hundredth.
	 * 
	 * @return a new {@code MutableDecimal4f} value initialised with 0.01.
	 */
	public static MutableDecimal4f hundredth() {
		return new MutableDecimal4f(Decimal4f.HUNDREDTH);
	}

	/**
	 * Returns a new {@code MutableDecimal4f} whose value is equal to one thousandth.
	 * 
	 * @return a new {@code MutableDecimal4f} value initialised with 0.001.
	 */
	public static MutableDecimal4f thousandth() {
		return new MutableDecimal4f(Decimal4f.THOUSANDTH);
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
	 * Decimal6f product = this.multiplyExact().by(Decimal2f.FIVE);
	 * </pre>
	 * 
	 * @return a multipliable object encapsulating this Decimal as first factor
	 *             of an exact multiplication
	 */
	public Multipliable4f multiplyExact() {
		return new Multipliable4f(this);
	}

	@Override
	public Decimal4f toImmutableDecimal() {
		return Decimal4f.valueOf(this);
	}

	@Override
	public MutableDecimal4f toMutableDecimal() {
		return this;
	}
}

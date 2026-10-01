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
import org.decimal4j.exact.Multipliable1f;
import org.decimal4j.factory.Factory1f;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.scale.Scale1f;

/**
 * <tt>MutableDecimal1f</tt> represents a mutable decimal number with a fixed
 * number of 1 digits to the right of the decimal point.
 * <p>
 * All methods for this class throw {@code NullPointerException} when passed a
 * {@code null} object reference for any input parameter.
 */
public final class MutableDecimal1f extends AbstractMutableDecimal<Scale1f, MutableDecimal1f> implements Cloneable {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new {@code MutableDecimal1f} with value zero.
	 * @see #zero()
	 */
	public MutableDecimal1f() {
		super(0);
	}

	/**
	 * Private constructor with unscaled value.
	 *
	 * @param unscaledValue the unscaled value
	 * @param scale 		the scale metrics used to distinguish this constructor signature
	 *						from {@link #MutableDecimal1f(long)}
	 */
	private MutableDecimal1f(long unscaledValue, Scale1f scale) {
		super(unscaledValue);
	}

	/**
	 * Translates the string representation of a {@code Decimal} into a
	 * {@code MutableDecimal1f}. The string representation consists 
	 * of an optional sign, {@code '+'} or {@code '-'} , followed by a sequence 
	 * of zero or more decimal digits ("the integer"), optionally followed by a
	 * fraction.
	 * <p>
	 * The fraction consists of a decimal point followed by zero or more decimal
	 * digits. The string must contain at least one digit in either the integer
	 * or the fraction. If the fraction contains more than 1 digits, the 
	 * value is rounded using {@link RoundingMode#HALF_UP HALF_UP} rounding. An 
	 * exception is thrown if the value is too large to be represented as a 
	 * {@code MutableDecimal1f}.
	 *
	 * @param value
	 *            String value to convert into a {@code MutableDecimal1f}
	 * @throws NumberFormatException
	 *             if {@code value} does not represent a valid {@code Decimal}
	 *             or if the value is too large to be represented as a 
	 *             {@code MutableDecimal1f}
	 * @see #set(String, RoundingMode)
	 */
	public MutableDecimal1f(String value) {
		this();
		set(value);
	}

 	/**
	 * Constructs a {@code MutableDecimal1f} whose value is numerically equal 
	 * to that of the specified {@code long} value. An exception is thrown if the
	 * specified value is too large to be represented as a {@code MutableDecimal1f}.
	 *
	 * @param value
	 *            long value to convert into a {@code MutableDecimal1f}
	 * @throws IllegalArgumentException
	 *            if {@code value} is too large to be represented as a 
	 *            {@code MutableDecimal1f}
	 */
	public MutableDecimal1f(long value) {
		this();
		set(value);
	}

	/**
	 * Constructs a {@code MutableDecimal1f} whose value is calculated by
	 * rounding the specified {@code double} argument to scale 1 using
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown if the
	 * specified value is too large to be represented as a {@code MutableDecimal1f}. 
	 *
	 * @param value
	 *            double value to convert into a {@code MutableDecimal1f}
	 * @throws IllegalArgumentException
	 *             if {@code value} is NaN or infinite or if the magnitude is too large
	 *             for the double to be represented as a {@code MutableDecimal1f}
	 * @see #set(double, RoundingMode)
	 * @see #set(float)
	 * @see #set(float, RoundingMode)
	 */
	public MutableDecimal1f(double value) {
		this();
		set(value);
	}

	/**
	 * Constructs a {@code MutableDecimal1f} whose value is numerically equal to
	 * that of the specified {@link BigInteger} value. An exception is thrown if the
	 * specified value is too large to be represented as a {@code MutableDecimal1f}.
	 *
	 * @param value
	 *            {@code BigInteger} value to convert into a {@code MutableDecimal1f}
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a {@code MutableDecimal1f}
	 */
	public MutableDecimal1f(BigInteger value) {
		this();
		set(value);
	}

	/**
	 * Constructs a {@code MutableDecimal1f} whose value is calculated by
	 * rounding the specified {@link BigDecimal} argument to scale 1 using
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown if the 
	 * specified value is too large to be represented as a {@code MutableDecimal1f}.
	 *
	 * @param value
	 *            {@code BigDecimal} value to convert into a {@code MutableDecimal1f}
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a {@code MutableDecimal1f}
	 * @see #set(BigDecimal, RoundingMode)
	 */
	public MutableDecimal1f(BigDecimal value) {
		this();
		set(value);
	}

	/**
	 * Constructs a {@code MutableDecimal1f} whose value is numerically equal to
	 * that of the specified {@link Decimal1f} value.
	 *
	 * @param value
	 *            {@code Decimal1f} value to convert into a {@code MutableDecimal1f}
	 */
	public MutableDecimal1f(Decimal1f value) {
		this(value.unscaledValue(), Decimal1f.METRICS);
	}

	/**
	 * Constructs a {@code MutableDecimal1f} whose value is calculated by
	 * rounding the specified {@link Decimal} argument to scale 1 using
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown if 
	 * the specified value is too large to be represented as a {@code MutableDecimal1f}. 
	 *
	 * @param value
	 *            Decimal value to convert into a {@code MutableDecimal1f} 
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a {@code MutableDecimal1f}
	 * @see #set(Decimal, RoundingMode)
	 */
	public MutableDecimal1f(Decimal<?> value) {
		this();
		setUnscaled(value.unscaledValue(), value.getScale());
	}

	@Override
	protected final MutableDecimal1f create(long unscaled) {
		return new MutableDecimal1f(unscaled, Decimal1f.METRICS);
	}
	
	@Override
	protected final MutableDecimal1f[] createArray(int length) {
		return new MutableDecimal1f[length];
	}

	@Override
	protected final MutableDecimal1f self() {
		return this;
	}

	@Override
	public final Scale1f getScaleMetrics() {
		return Decimal1f.METRICS;
	}

	@Override
	public final int getScale() {
		return Decimal1f.SCALE;
	}

	@Override
	public Factory1f getFactory() {
		return Decimal1f.FACTORY;
	}
	
	@Override
	protected DecimalArithmetic getDefaultArithmetic() {
		return Decimal1f.DEFAULT_ARITHMETIC;
	}
	
	@Override
	protected DecimalArithmetic getDefaultCheckedArithmetic() {
		return Decimal1f.METRICS.getDefaultCheckedArithmetic();
	}

	@Override
	protected DecimalArithmetic getRoundingDownArithmetic() {
		return Decimal1f.METRICS.getRoundingDownArithmetic();
	}
	
	@Override
	protected DecimalArithmetic getRoundingFloorArithmetic() {
		return Decimal1f.METRICS.getRoundingFloorArithmetic();
	}
	
	@Override
	protected DecimalArithmetic getRoundingHalfEvenArithmetic() {
		return Decimal1f.METRICS.getRoundingHalfEvenArithmetic();
	}
	
	@Override
	protected DecimalArithmetic getRoundingUnnecessaryArithmetic() {
		return Decimal1f.METRICS.getRoundingUnnecessaryArithmetic();
	}

	@Override
	public MutableDecimal1f clone() {
		return new MutableDecimal1f(unscaledValue(), Decimal1f.METRICS);
	}

	/**
	 * Returns a new {@code MutableDecimal1f} whose value is equal to 
	 * <code>unscaledValue * 10<sup>-1</sup></code>.
	 * 
	 * @param unscaledValue
	 *            the unscaled decimal value to convert
	 * @return a new {@code MutableDecimal1f} value initialised with <code>unscaledValue * 10<sup>-1</sup></code>
	 * @see #setUnscaled(long, int)
	 * @see #setUnscaled(long, int, RoundingMode)
	 */
	public static MutableDecimal1f unscaled(long unscaledValue) {
		return new MutableDecimal1f(unscaledValue, Decimal1f.METRICS);
	}

	/**
	 * Returns a new {@code MutableDecimal1f} whose value is equal to zero.
	 * 
	 * @return a new {@code MutableDecimal1f} value initialised with 0.
	 */
	public static MutableDecimal1f zero() {
		return new MutableDecimal1f();
	}

	/**
	 * Returns a new {@code MutableDecimal1f} whose value is equal to one ULP.
	 * 
	 * @return a new {@code MutableDecimal1f} value initialised with 10<sup>-1</sup>.
	 */
	public static MutableDecimal1f ulp() {
		return new MutableDecimal1f(Decimal1f.ULP);
	}

	/**
	 * Returns a new {@code MutableDecimal1f} whose value is equal to one.
	 * 
	 * @return a new {@code MutableDecimal1f} value initialised with 1.
	 */
	public static MutableDecimal1f one() {
		return new MutableDecimal1f(Decimal1f.ONE);
	}

	/**
	 * Returns a new {@code MutableDecimal1f} whose value is equal to two.
	 * 
	 * @return a new {@code MutableDecimal1f} value initialised with 2.
	 */
	public static MutableDecimal1f two() {
		return new MutableDecimal1f(Decimal1f.TWO);
	}

	/**
	 * Returns a new {@code MutableDecimal1f} whose value is equal to three.
	 * 
	 * @return a new {@code MutableDecimal1f} value initialised with 3.
	 */
	public static MutableDecimal1f three() {
		return new MutableDecimal1f(Decimal1f.THREE);
	}

	/**
	 * Returns a new {@code MutableDecimal1f} whose value is equal to four.
	 * 
	 * @return a new {@code MutableDecimal1f} value initialised with 4.
	 */
	public static MutableDecimal1f four() {
		return new MutableDecimal1f(Decimal1f.FOUR);
	}

	/**
	 * Returns a new {@code MutableDecimal1f} whose value is equal to five.
	 * 
	 * @return a new {@code MutableDecimal1f} value initialised with 5.
	 */
	public static MutableDecimal1f five() {
		return new MutableDecimal1f(Decimal1f.FIVE);
	}

	/**
	 * Returns a new {@code MutableDecimal1f} whose value is equal to six.
	 * 
	 * @return a new {@code MutableDecimal1f} value initialised with 6.
	 */
	public static MutableDecimal1f six() {
		return new MutableDecimal1f(Decimal1f.SIX);
	}

	/**
	 * Returns a new {@code MutableDecimal1f} whose value is equal to seven.
	 * 
	 * @return a new {@code MutableDecimal1f} value initialised with 7.
	 */
	public static MutableDecimal1f seven() {
		return new MutableDecimal1f(Decimal1f.SEVEN);
	}

	/**
	 * Returns a new {@code MutableDecimal1f} whose value is equal to eight.
	 * 
	 * @return a new {@code MutableDecimal1f} value initialised with 8.
	 */
	public static MutableDecimal1f eight() {
		return new MutableDecimal1f(Decimal1f.EIGHT);
	}

	/**
	 * Returns a new {@code MutableDecimal1f} whose value is equal to nine.
	 * 
	 * @return a new {@code MutableDecimal1f} value initialised with 9.
	 */
	public static MutableDecimal1f nine() {
		return new MutableDecimal1f(Decimal1f.NINE);
	}

	/**
	 * Returns a new {@code MutableDecimal1f} whose value is equal to ten.
	 * 
	 * @return a new {@code MutableDecimal1f} value initialised with 10.
	 */
	public static MutableDecimal1f ten() {
		return new MutableDecimal1f(Decimal1f.TEN);
	}
	/**
	 * Returns a new {@code MutableDecimal1f} whose value is equal to one hundred.
	 * 
	 * @return a new {@code MutableDecimal1f} value initialised with 100.
	 */
	public static MutableDecimal1f hundred() {
		return new MutableDecimal1f(Decimal1f.HUNDRED);
	}
	/**
	 * Returns a new {@code MutableDecimal1f} whose value is equal to one thousand.
	 * 
	 * @return a new {@code MutableDecimal1f} value initialised with 1000.
	 */
	public static MutableDecimal1f thousand() {
		return new MutableDecimal1f(Decimal1f.THOUSAND);
	}
	/**
	 * Returns a new {@code MutableDecimal1f} whose value is equal to one million.
	 * 
	 * @return a new {@code MutableDecimal1f} value initialised with 10<sup>6</sup>.
	 */
	public static MutableDecimal1f million() {
		return new MutableDecimal1f(Decimal1f.MILLION);
	}
	/**
	 * Returns a new {@code MutableDecimal1f} whose value is equal to one billion.
	 * 
	 * @return a new {@code MutableDecimal1f} value initialised with 10<sup>9</sup>.
	 */
	public static MutableDecimal1f billion() {
		return new MutableDecimal1f(Decimal1f.BILLION);
	}
	/**
	 * Returns a new {@code MutableDecimal1f} whose value is equal to one trillion.
	 * 
	 * @return a new {@code MutableDecimal1f} value initialised with 10<sup>12</sup>.
	 */
	public static MutableDecimal1f trillion() {
		return new MutableDecimal1f(Decimal1f.TRILLION);
	}
	/**
	 * Returns a new {@code MutableDecimal1f} whose value is equal to one quadrillion.
	 * 
	 * @return a new {@code MutableDecimal1f} value initialised with 10<sup>15</sup>.
	 */
	public static MutableDecimal1f quadrillion() {
		return new MutableDecimal1f(Decimal1f.QUADRILLION);
	}

	/**
	 * Returns a new {@code MutableDecimal1f} whose value is equal to minus one.
	 * 
	 * @return a new {@code MutableDecimal1f} value initialised with -1.
	 */
	public static MutableDecimal1f minusOne() {
		return new MutableDecimal1f(Decimal1f.MINUS_ONE);
	}

	/**
	 * Returns a new {@code MutableDecimal1f} whose value is equal to one half.
	 * 
	 * @return a new {@code MutableDecimal1f} value initialised with 0.5.
	 */
	public static MutableDecimal1f half() {
		return new MutableDecimal1f(Decimal1f.HALF);
	}

	/**
	 * Returns a new {@code MutableDecimal1f} whose value is equal to one tenth.
	 * 
	 * @return a new {@code MutableDecimal1f} value initialised with 0.1.
	 */
	public static MutableDecimal1f tenth() {
		return new MutableDecimal1f(Decimal1f.TENTH);
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
	public Decimal1f toImmutableDecimal() {
		return Decimal1f.valueOf(this);
	}

	@Override
	public MutableDecimal1f toMutableDecimal() {
		return this;
	}
}

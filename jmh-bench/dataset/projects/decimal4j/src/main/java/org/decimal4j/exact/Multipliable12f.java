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
package org.decimal4j.exact;

import java.util.Objects;

import org.decimal4j.api.Decimal;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.immutable.Decimal3f;
import org.decimal4j.immutable.Decimal4f;
import org.decimal4j.immutable.Decimal5f;
import org.decimal4j.immutable.Decimal6f;
import org.decimal4j.immutable.Decimal12f;
import org.decimal4j.immutable.Decimal13f;
import org.decimal4j.immutable.Decimal14f;
import org.decimal4j.immutable.Decimal15f;
import org.decimal4j.immutable.Decimal16f;
import org.decimal4j.immutable.Decimal17f;
import org.decimal4j.immutable.Decimal18f;
import org.decimal4j.mutable.MutableDecimal0f;
import org.decimal4j.mutable.MutableDecimal1f;
import org.decimal4j.mutable.MutableDecimal2f;
import org.decimal4j.mutable.MutableDecimal3f;
import org.decimal4j.mutable.MutableDecimal4f;
import org.decimal4j.mutable.MutableDecimal5f;
import org.decimal4j.mutable.MutableDecimal6f;
import org.decimal4j.scale.Scale12f;

/**
 * A {@code Multipliable12f} encapsulates a Decimal of scale 12 and facilitates
 * exact typed multiplication. The multipliable object acts as first factor in the multiplication
 * and provides a set of overloaded methods for different scales. Each one of those methods 
 * delivers a different result scale which represents the appropriate scale for the product of
 * an exact multiplication.
 * <p>
 * A {@code Multipliable12f} object is returned by {@link Decimal12f#multiplyExact()},
 * hence an exact typed multiplication can be written as:
 * <pre>
 * Decimal12f value = ... //some value
 * Decimal14f product = value.multiplyExact().by(Decimal2f.FIVE);
 * </pre>
 */
public final class Multipliable12f {
	
	private final Decimal<Scale12f> value;
	
	/**
	 * Constructor with Decimal value to be encapsulated.
	 * @param value the decimal value to be wrapped as a multipliable object
	 */
	public Multipliable12f(Decimal<Scale12f> value) {
		this.value = Objects.requireNonNull(value, "value cannot be null");
	}
	
	/**
	 * Returns the value underlying this Multipliable12f.
	 * @return the Decimal value wrapped by this multipliable object
	 */
	public Decimal<Scale12f> getValue() {
		return value;
	}

	/**
	 * Returns a {@code Decimal} whose value is {@code (this * factor)}. The
	 * result is exact and has scale 12 which is the sum of the scales 
	 * of the Decimal that this multipliable object represents and the scale of
	 * the {@code factor} argument. An {@link ArithmeticException} is thrown if the 
	 * product is out of the possible range for a {@code Decimal12f}.
	 * <p>
	 * Note that the result is <i>always</i> a new instance.
	 * 
	 * @param factor
	 *            the factor to multiply with the Decimal that this multipliable represents
	 * @return <tt>(this * factor)</tt>
	 * @throws ArithmeticException
	 *             if an overflow occurs and product is out of the possible
	 *             range for a {@code Decimal12f}
	 */
	public Decimal12f by(Decimal0f factor) {
		return Decimal12f.valueOf(this.value.multiplyExact(factor));
	}
	/**
	 * Returns a {@code Decimal} whose value is {@code (this * factor)}. The
	 * result is exact and has scale 12 which is the sum of the scales 
	 * of the Decimal that this multipliable object represents and the scale of
	 * the {@code factor} argument. An {@link ArithmeticException} is thrown if the 
	 * product is out of the possible range for a {@code Decimal12f}.
	 * <p>
	 * Note that the result is <i>always</i> a new instance.
	 * 
	 * @param factor
	 *            the factor to multiply with the Decimal that this multipliable represents
	 * @return <tt>(this * factor)</tt>
	 * @throws ArithmeticException
	 *             if an overflow occurs and product is out of the possible
	 *             range for a {@code Decimal12f}
	 */
	public Decimal12f by(MutableDecimal0f factor) {
		return Decimal12f.valueOf(this.value.multiplyExact(factor));
	}

	/**
	 * Returns a {@code Decimal} whose value is {@code (this * factor)}. The
	 * result is exact and has scale 13 which is the sum of the scales 
	 * of the Decimal that this multipliable object represents and the scale of
	 * the {@code factor} argument. An {@link ArithmeticException} is thrown if the 
	 * product is out of the possible range for a {@code Decimal13f}.
	 * <p>
	 * Note that the result is <i>always</i> a new instance.
	 * 
	 * @param factor
	 *            the factor to multiply with the Decimal that this multipliable represents
	 * @return <tt>(this * factor)</tt>
	 * @throws ArithmeticException
	 *             if an overflow occurs and product is out of the possible
	 *             range for a {@code Decimal13f}
	 */
	public Decimal13f by(Decimal1f factor) {
		return Decimal13f.valueOf(this.value.multiplyExact(factor));
	}
	/**
	 * Returns a {@code Decimal} whose value is {@code (this * factor)}. The
	 * result is exact and has scale 13 which is the sum of the scales 
	 * of the Decimal that this multipliable object represents and the scale of
	 * the {@code factor} argument. An {@link ArithmeticException} is thrown if the 
	 * product is out of the possible range for a {@code Decimal13f}.
	 * <p>
	 * Note that the result is <i>always</i> a new instance.
	 * 
	 * @param factor
	 *            the factor to multiply with the Decimal that this multipliable represents
	 * @return <tt>(this * factor)</tt>
	 * @throws ArithmeticException
	 *             if an overflow occurs and product is out of the possible
	 *             range for a {@code Decimal13f}
	 */
	public Decimal13f by(MutableDecimal1f factor) {
		return Decimal13f.valueOf(this.value.multiplyExact(factor));
	}

	/**
	 * Returns a {@code Decimal} whose value is {@code (this * factor)}. The
	 * result is exact and has scale 14 which is the sum of the scales 
	 * of the Decimal that this multipliable object represents and the scale of
	 * the {@code factor} argument. An {@link ArithmeticException} is thrown if the 
	 * product is out of the possible range for a {@code Decimal14f}.
	 * <p>
	 * Note that the result is <i>always</i> a new instance.
	 * 
	 * @param factor
	 *            the factor to multiply with the Decimal that this multipliable represents
	 * @return <tt>(this * factor)</tt>
	 * @throws ArithmeticException
	 *             if an overflow occurs and product is out of the possible
	 *             range for a {@code Decimal14f}
	 */
	public Decimal14f by(Decimal2f factor) {
		return Decimal14f.valueOf(this.value.multiplyExact(factor));
	}
	/**
	 * Returns a {@code Decimal} whose value is {@code (this * factor)}. The
	 * result is exact and has scale 14 which is the sum of the scales 
	 * of the Decimal that this multipliable object represents and the scale of
	 * the {@code factor} argument. An {@link ArithmeticException} is thrown if the 
	 * product is out of the possible range for a {@code Decimal14f}.
	 * <p>
	 * Note that the result is <i>always</i> a new instance.
	 * 
	 * @param factor
	 *            the factor to multiply with the Decimal that this multipliable represents
	 * @return <tt>(this * factor)</tt>
	 * @throws ArithmeticException
	 *             if an overflow occurs and product is out of the possible
	 *             range for a {@code Decimal14f}
	 */
	public Decimal14f by(MutableDecimal2f factor) {
		return Decimal14f.valueOf(this.value.multiplyExact(factor));
	}

	/**
	 * Returns a {@code Decimal} whose value is {@code (this * factor)}. The
	 * result is exact and has scale 15 which is the sum of the scales 
	 * of the Decimal that this multipliable object represents and the scale of
	 * the {@code factor} argument. An {@link ArithmeticException} is thrown if the 
	 * product is out of the possible range for a {@code Decimal15f}.
	 * <p>
	 * Note that the result is <i>always</i> a new instance.
	 * 
	 * @param factor
	 *            the factor to multiply with the Decimal that this multipliable represents
	 * @return <tt>(this * factor)</tt>
	 * @throws ArithmeticException
	 *             if an overflow occurs and product is out of the possible
	 *             range for a {@code Decimal15f}
	 */
	public Decimal15f by(Decimal3f factor) {
		return Decimal15f.valueOf(this.value.multiplyExact(factor));
	}
	/**
	 * Returns a {@code Decimal} whose value is {@code (this * factor)}. The
	 * result is exact and has scale 15 which is the sum of the scales 
	 * of the Decimal that this multipliable object represents and the scale of
	 * the {@code factor} argument. An {@link ArithmeticException} is thrown if the 
	 * product is out of the possible range for a {@code Decimal15f}.
	 * <p>
	 * Note that the result is <i>always</i> a new instance.
	 * 
	 * @param factor
	 *            the factor to multiply with the Decimal that this multipliable represents
	 * @return <tt>(this * factor)</tt>
	 * @throws ArithmeticException
	 *             if an overflow occurs and product is out of the possible
	 *             range for a {@code Decimal15f}
	 */
	public Decimal15f by(MutableDecimal3f factor) {
		return Decimal15f.valueOf(this.value.multiplyExact(factor));
	}

	/**
	 * Returns a {@code Decimal} whose value is {@code (this * factor)}. The
	 * result is exact and has scale 16 which is the sum of the scales 
	 * of the Decimal that this multipliable object represents and the scale of
	 * the {@code factor} argument. An {@link ArithmeticException} is thrown if the 
	 * product is out of the possible range for a {@code Decimal16f}.
	 * <p>
	 * Note that the result is <i>always</i> a new instance.
	 * 
	 * @param factor
	 *            the factor to multiply with the Decimal that this multipliable represents
	 * @return <tt>(this * factor)</tt>
	 * @throws ArithmeticException
	 *             if an overflow occurs and product is out of the possible
	 *             range for a {@code Decimal16f}
	 */
	public Decimal16f by(Decimal4f factor) {
		return Decimal16f.valueOf(this.value.multiplyExact(factor));
	}
	/**
	 * Returns a {@code Decimal} whose value is {@code (this * factor)}. The
	 * result is exact and has scale 16 which is the sum of the scales 
	 * of the Decimal that this multipliable object represents and the scale of
	 * the {@code factor} argument. An {@link ArithmeticException} is thrown if the 
	 * product is out of the possible range for a {@code Decimal16f}.
	 * <p>
	 * Note that the result is <i>always</i> a new instance.
	 * 
	 * @param factor
	 *            the factor to multiply with the Decimal that this multipliable represents
	 * @return <tt>(this * factor)</tt>
	 * @throws ArithmeticException
	 *             if an overflow occurs and product is out of the possible
	 *             range for a {@code Decimal16f}
	 */
	public Decimal16f by(MutableDecimal4f factor) {
		return Decimal16f.valueOf(this.value.multiplyExact(factor));
	}

	/**
	 * Returns a {@code Decimal} whose value is {@code (this * factor)}. The
	 * result is exact and has scale 17 which is the sum of the scales 
	 * of the Decimal that this multipliable object represents and the scale of
	 * the {@code factor} argument. An {@link ArithmeticException} is thrown if the 
	 * product is out of the possible range for a {@code Decimal17f}.
	 * <p>
	 * Note that the result is <i>always</i> a new instance.
	 * 
	 * @param factor
	 *            the factor to multiply with the Decimal that this multipliable represents
	 * @return <tt>(this * factor)</tt>
	 * @throws ArithmeticException
	 *             if an overflow occurs and product is out of the possible
	 *             range for a {@code Decimal17f}
	 */
	public Decimal17f by(Decimal5f factor) {
		return Decimal17f.valueOf(this.value.multiplyExact(factor));
	}
	/**
	 * Returns a {@code Decimal} whose value is {@code (this * factor)}. The
	 * result is exact and has scale 17 which is the sum of the scales 
	 * of the Decimal that this multipliable object represents and the scale of
	 * the {@code factor} argument. An {@link ArithmeticException} is thrown if the 
	 * product is out of the possible range for a {@code Decimal17f}.
	 * <p>
	 * Note that the result is <i>always</i> a new instance.
	 * 
	 * @param factor
	 *            the factor to multiply with the Decimal that this multipliable represents
	 * @return <tt>(this * factor)</tt>
	 * @throws ArithmeticException
	 *             if an overflow occurs and product is out of the possible
	 *             range for a {@code Decimal17f}
	 */
	public Decimal17f by(MutableDecimal5f factor) {
		return Decimal17f.valueOf(this.value.multiplyExact(factor));
	}

	/**
	 * Returns a {@code Decimal} whose value is {@code (this * factor)}. The
	 * result is exact and has scale 18 which is the sum of the scales 
	 * of the Decimal that this multipliable object represents and the scale of
	 * the {@code factor} argument. An {@link ArithmeticException} is thrown if the 
	 * product is out of the possible range for a {@code Decimal18f}.
	 * <p>
	 * Note that the result is <i>always</i> a new instance.
	 * 
	 * @param factor
	 *            the factor to multiply with the Decimal that this multipliable represents
	 * @return <tt>(this * factor)</tt>
	 * @throws ArithmeticException
	 *             if an overflow occurs and product is out of the possible
	 *             range for a {@code Decimal18f}
	 */
	public Decimal18f by(Decimal6f factor) {
		return Decimal18f.valueOf(this.value.multiplyExact(factor));
	}
	/**
	 * Returns a {@code Decimal} whose value is {@code (this * factor)}. The
	 * result is exact and has scale 18 which is the sum of the scales 
	 * of the Decimal that this multipliable object represents and the scale of
	 * the {@code factor} argument. An {@link ArithmeticException} is thrown if the 
	 * product is out of the possible range for a {@code Decimal18f}.
	 * <p>
	 * Note that the result is <i>always</i> a new instance.
	 * 
	 * @param factor
	 *            the factor to multiply with the Decimal that this multipliable represents
	 * @return <tt>(this * factor)</tt>
	 * @throws ArithmeticException
	 *             if an overflow occurs and product is out of the possible
	 *             range for a {@code Decimal18f}
	 */
	public Decimal18f by(MutableDecimal6f factor) {
		return Decimal18f.valueOf(this.value.multiplyExact(factor));
	}

	
	/**
	 * Returns a hash code for this <tt>Multipliable12f</tt> which happens to be the 
	 * hash code of the underlying {@code Decimal12f} value.
	 * 
	 * @return a hash code value for this object
	 * @see Decimal#hashCode()
	 */
	@Override
	public int hashCode() {
		return value.hashCode();
	}

	/**
	 * Compares this Multipliable12f to the specified object. The result is {@code true}
	 * if and only if the argument is a {@code Multipliable12f} with an equal underlying 
	 * {@link #getValue() value}.
	 * 
	 * @param obj
	 *            the object to compare with
	 * @return {@code true} if the argument is a {@code Multipliable12f} and if its value
	 *         is equal to this multipliables's value; {@code false} otherwise
	 * @see #getValue()
	 * @see Decimal#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		return value.equals(((Multipliable12f)obj).value);
	}

	/**
	 * Returns a string representation of this {@code Multipliable12f} which is
	 * simply the string representation of the underlying Decimal {@link #getValue() value}.
	 * 
	 * @return a {@code String} Decimal representation of this {@code Multipliable12f}'s
	 *         value with all the fraction digits (including trailing zeros)
	 * @see #getValue()
	 * @see Decimal#toString()
	 */
	@Override
	public String toString() {
		return value.toString();
	}
}

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
import org.decimal4j.immutable.Decimal15f;
import org.decimal4j.immutable.Decimal16f;
import org.decimal4j.immutable.Decimal17f;
import org.decimal4j.immutable.Decimal18f;
import org.decimal4j.mutable.MutableDecimal0f;
import org.decimal4j.mutable.MutableDecimal1f;
import org.decimal4j.mutable.MutableDecimal2f;
import org.decimal4j.mutable.MutableDecimal3f;
import org.decimal4j.scale.Scale15f;

/**
 * A {@code Multipliable15f} encapsulates a Decimal of scale 15 and facilitates
 * exact typed multiplication. The multipliable object acts as first factor in the multiplication
 * and provides a set of overloaded methods for different scales. Each one of those methods 
 * delivers a different result scale which represents the appropriate scale for the product of
 * an exact multiplication.
 * <p>
 * A {@code Multipliable15f} object is returned by {@link Decimal15f#multiplyExact()},
 * hence an exact typed multiplication can be written as:
 * <pre>
 * Decimal15f value = ... //some value
 * Decimal17f product = value.multiplyExact().by(Decimal2f.FIVE);
 * </pre>
 */
public final class Multipliable15f {
	
	private final Decimal<Scale15f> value;
	
	/**
	 * Constructor with Decimal value to be encapsulated.
	 * @param value the decimal value to be wrapped as a multipliable object
	 */
	public Multipliable15f(Decimal<Scale15f> value) {
		this.value = Objects.requireNonNull(value, "value cannot be null");
	}
	
	/**
	 * Returns the value underlying this Multipliable15f.
	 * @return the Decimal value wrapped by this multipliable object
	 */
	public Decimal<Scale15f> getValue() {
		return value;
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
	public Decimal15f by(Decimal0f factor) {
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
	public Decimal15f by(MutableDecimal0f factor) {
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
	public Decimal16f by(Decimal1f factor) {
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
	public Decimal16f by(MutableDecimal1f factor) {
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
	public Decimal17f by(Decimal2f factor) {
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
	public Decimal17f by(MutableDecimal2f factor) {
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
	public Decimal18f by(Decimal3f factor) {
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
	public Decimal18f by(MutableDecimal3f factor) {
		return Decimal18f.valueOf(this.value.multiplyExact(factor));
	}

	
	/**
	 * Returns a hash code for this <tt>Multipliable15f</tt> which happens to be the 
	 * hash code of the underlying {@code Decimal15f} value.
	 * 
	 * @return a hash code value for this object
	 * @see Decimal#hashCode()
	 */
	@Override
	public int hashCode() {
		return value.hashCode();
	}

	/**
	 * Compares this Multipliable15f to the specified object. The result is {@code true}
	 * if and only if the argument is a {@code Multipliable15f} with an equal underlying 
	 * {@link #getValue() value}.
	 * 
	 * @param obj
	 *            the object to compare with
	 * @return {@code true} if the argument is a {@code Multipliable15f} and if its value
	 *         is equal to this multipliables's value; {@code false} otherwise
	 * @see #getValue()
	 * @see Decimal#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		return value.equals(((Multipliable15f)obj).value);
	}

	/**
	 * Returns a string representation of this {@code Multipliable15f} which is
	 * simply the string representation of the underlying Decimal {@link #getValue() value}.
	 * 
	 * @return a {@code String} Decimal representation of this {@code Multipliable15f}'s
	 *         value with all the fraction digits (including trailing zeros)
	 * @see #getValue()
	 * @see Decimal#toString()
	 */
	@Override
	public String toString() {
		return value.toString();
	}
}

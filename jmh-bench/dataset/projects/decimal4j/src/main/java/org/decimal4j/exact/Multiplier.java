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

import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.immutable.Decimal3f;
import org.decimal4j.immutable.Decimal4f;
import org.decimal4j.immutable.Decimal5f;
import org.decimal4j.immutable.Decimal6f;
import org.decimal4j.immutable.Decimal7f;
import org.decimal4j.immutable.Decimal8f;
import org.decimal4j.immutable.Decimal9f;
import org.decimal4j.immutable.Decimal10f;
import org.decimal4j.immutable.Decimal11f;
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
import org.decimal4j.mutable.MutableDecimal7f;
import org.decimal4j.mutable.MutableDecimal8f;
import org.decimal4j.mutable.MutableDecimal9f;
import org.decimal4j.mutable.MutableDecimal10f;
import org.decimal4j.mutable.MutableDecimal11f;
import org.decimal4j.mutable.MutableDecimal12f;
import org.decimal4j.mutable.MutableDecimal13f;
import org.decimal4j.mutable.MutableDecimal14f;
import org.decimal4j.mutable.MutableDecimal15f;
import org.decimal4j.mutable.MutableDecimal16f;
import org.decimal4j.mutable.MutableDecimal17f;
import org.decimal4j.mutable.MutableDecimal18f;

/**
 * {@code Multiplier} provides static {@code multiplyExact(..)} methods for 
 * {@link org.decimal4j.api.Decimal Decimal} values of different scales. The multipliable
 * object returned by those methods encapsulates the Decimal argument and facilitates
 * exact typed multiplication. The multipliable object acts as first factor in the multiplication
 * and provides a set of overloaded methods for different scales. Each one of those methods 
 * delivers a different result scale which represents the appropriate scale for the product of
 * an exact multiplication.
 * <p>
 * An exact typed multiplication can for instance be written as:
 * <pre>
 * Decimal&lt;Scale5f&gt; value = ... //some value
 * Decimal7f product7 = Multiplier.multiplyExact(value).by(Decimal2f.FIVE);
 * Decimal9f product9 = Multiplier.multiplyExact(value).by(Decimal4f.NINE);
 * </pre>
 */
public final class Multiplier {

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	public static Multipliable0f multiplyExact(Decimal0f value) {
		return new Multipliable0f(value);
	}

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	public static Multipliable0f multiplyExact(MutableDecimal0f value) {
		return new Multipliable0f(value);
	}

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	public static Multipliable1f multiplyExact(Decimal1f value) {
		return new Multipliable1f(value);
	}

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	public static Multipliable1f multiplyExact(MutableDecimal1f value) {
		return new Multipliable1f(value);
	}

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	public static Multipliable2f multiplyExact(Decimal2f value) {
		return new Multipliable2f(value);
	}

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	public static Multipliable2f multiplyExact(MutableDecimal2f value) {
		return new Multipliable2f(value);
	}

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	public static Multipliable3f multiplyExact(Decimal3f value) {
		return new Multipliable3f(value);
	}

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	public static Multipliable3f multiplyExact(MutableDecimal3f value) {
		return new Multipliable3f(value);
	}

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	public static Multipliable4f multiplyExact(Decimal4f value) {
		return new Multipliable4f(value);
	}

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	public static Multipliable4f multiplyExact(MutableDecimal4f value) {
		return new Multipliable4f(value);
	}

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	public static Multipliable5f multiplyExact(Decimal5f value) {
		return new Multipliable5f(value);
	}

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	public static Multipliable5f multiplyExact(MutableDecimal5f value) {
		return new Multipliable5f(value);
	}

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	public static Multipliable6f multiplyExact(Decimal6f value) {
		return new Multipliable6f(value);
	}

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	public static Multipliable6f multiplyExact(MutableDecimal6f value) {
		return new Multipliable6f(value);
	}

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	public static Multipliable7f multiplyExact(Decimal7f value) {
		return new Multipliable7f(value);
	}

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	public static Multipliable7f multiplyExact(MutableDecimal7f value) {
		return new Multipliable7f(value);
	}

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	public static Multipliable8f multiplyExact(Decimal8f value) {
		return new Multipliable8f(value);
	}

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	public static Multipliable8f multiplyExact(MutableDecimal8f value) {
		return new Multipliable8f(value);
	}

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	public static Multipliable9f multiplyExact(Decimal9f value) {
		return new Multipliable9f(value);
	}

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	public static Multipliable9f multiplyExact(MutableDecimal9f value) {
		return new Multipliable9f(value);
	}

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	public static Multipliable10f multiplyExact(Decimal10f value) {
		return new Multipliable10f(value);
	}

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	public static Multipliable10f multiplyExact(MutableDecimal10f value) {
		return new Multipliable10f(value);
	}

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	public static Multipliable11f multiplyExact(Decimal11f value) {
		return new Multipliable11f(value);
	}

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	public static Multipliable11f multiplyExact(MutableDecimal11f value) {
		return new Multipliable11f(value);
	}

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	public static Multipliable12f multiplyExact(Decimal12f value) {
		return new Multipliable12f(value);
	}

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	public static Multipliable12f multiplyExact(MutableDecimal12f value) {
		return new Multipliable12f(value);
	}

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	public static Multipliable13f multiplyExact(Decimal13f value) {
		return new Multipliable13f(value);
	}

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	public static Multipliable13f multiplyExact(MutableDecimal13f value) {
		return new Multipliable13f(value);
	}

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	public static Multipliable14f multiplyExact(Decimal14f value) {
		return new Multipliable14f(value);
	}

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	public static Multipliable14f multiplyExact(MutableDecimal14f value) {
		return new Multipliable14f(value);
	}

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	public static Multipliable15f multiplyExact(Decimal15f value) {
		return new Multipliable15f(value);
	}

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	public static Multipliable15f multiplyExact(MutableDecimal15f value) {
		return new Multipliable15f(value);
	}

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	public static Multipliable16f multiplyExact(Decimal16f value) {
		return new Multipliable16f(value);
	}

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	public static Multipliable16f multiplyExact(MutableDecimal16f value) {
		return new Multipliable16f(value);
	}

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	public static Multipliable17f multiplyExact(Decimal17f value) {
		return new Multipliable17f(value);
	}

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	public static Multipliable17f multiplyExact(MutableDecimal17f value) {
		return new Multipliable17f(value);
	}

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	public static Multipliable18f multiplyExact(Decimal18f value) {
		return new Multipliable18f(value);
	}

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	public static Multipliable18f multiplyExact(MutableDecimal18f value) {
		return new Multipliable18f(value);
	}

	//no instances
	private Multiplier() {}
}
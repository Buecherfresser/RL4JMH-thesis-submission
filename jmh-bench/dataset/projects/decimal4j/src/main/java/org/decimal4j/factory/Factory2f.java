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
package org.decimal4j.factory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.api.Decimal;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.mutable.MutableDecimal2f;
import org.decimal4j.scale.Scale2f;
import org.decimal4j.scale.ScaleMetrics;

/**
 * The factory for decimals with scale 2 creating {@link Decimal2f} and
 * {@link MutableDecimal2f} instances.
 */
public enum Factory2f implements DecimalFactory<Scale2f> {

	/**
	 * Singleton factory instance for immutable and mutable decimals with scale 2.
	 */
	INSTANCE;

	@Override
	public final Scale2f getScaleMetrics() {
		return Scale2f.INSTANCE;
	}
	
	@Override
	public final int getScale() {
		return Scale2f.SCALE;
	}

	@Override
	public final Class<Decimal2f> immutableType() {
		return Decimal2f.class;
	}

	@Override
	public final Class<MutableDecimal2f> mutableType() {
		return MutableDecimal2f.class;
	}

	@Override
	public final DecimalFactory<?> deriveFactory(int scale) {
		return Factories.getDecimalFactory(scale);
	}
	
	@Override
	public final <S extends ScaleMetrics> DecimalFactory<S> deriveFactory(S scaleMetrics) {
		return Factories.getDecimalFactory(scaleMetrics);
	}

	@Override
	public final Decimal2f valueOf(long value) {
		return Decimal2f.valueOf(value);
	}

	@Override
	public final Decimal2f valueOf(float value) {
		return Decimal2f.valueOf(value);
	}

	@Override
	public final Decimal2f valueOf(float value, RoundingMode roundingMode) {
		return Decimal2f.valueOf(value, roundingMode);
	}

	@Override
	public final Decimal2f valueOf(double value) {
		return Decimal2f.valueOf(value);
	}

	@Override
	public final Decimal2f valueOf(double value, RoundingMode roundingMode) {
		return Decimal2f.valueOf(value, roundingMode);
	}

	@Override
	public final Decimal2f valueOf(BigInteger value) {
		return Decimal2f.valueOf(value);
	}

	@Override
	public final Decimal2f valueOf(BigDecimal value) {
		return Decimal2f.valueOf(value);
	}

	@Override
	public final Decimal2f valueOf(BigDecimal value, RoundingMode roundingMode) {
		return Decimal2f.valueOf(value, roundingMode);
	}

	@Override
	public final Decimal2f valueOf(Decimal<?> value) {
		return Decimal2f.valueOf(value);
	}

	@Override
	public final Decimal2f valueOf(Decimal<?> value, RoundingMode roundingMode) {
		return Decimal2f.valueOf(value, roundingMode);
	}

	@Override
	public final Decimal2f parse(String value) {
		return Decimal2f.valueOf(value);
	}

	@Override
	public final Decimal2f parse(String value, RoundingMode roundingMode) {
		return Decimal2f.valueOf(value, roundingMode);
	}

	@Override
	public final Decimal2f valueOfUnscaled(long unscaledValue) {
		return Decimal2f.valueOfUnscaled(unscaledValue);
	}

	@Override
	public final Decimal2f valueOfUnscaled(long unscaledValue, int scale) {
		return Decimal2f.valueOfUnscaled(unscaledValue, scale);
	}

	@Override
	public final Decimal2f valueOfUnscaled(long unscaledValue, int scale, RoundingMode roundingMode) {
		return Decimal2f.valueOfUnscaled(unscaledValue, scale, roundingMode);
	}

	@Override
	public final Decimal2f[] newArray(int length) {
		return new Decimal2f[length];
	}

	@Override
	public final MutableDecimal2f newMutable() {
		return new MutableDecimal2f();
	}

	@Override
	public final MutableDecimal2f[] newMutableArray(int length) {
		return new MutableDecimal2f[length];
	}
}

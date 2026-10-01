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
import org.decimal4j.immutable.Decimal3f;
import org.decimal4j.mutable.MutableDecimal3f;
import org.decimal4j.scale.Scale3f;
import org.decimal4j.scale.ScaleMetrics;

/**
 * The factory for decimals with scale 3 creating {@link Decimal3f} and
 * {@link MutableDecimal3f} instances.
 */
public enum Factory3f implements DecimalFactory<Scale3f> {

	/**
	 * Singleton factory instance for immutable and mutable decimals with scale 3.
	 */
	INSTANCE;

	@Override
	public final Scale3f getScaleMetrics() {
		return Scale3f.INSTANCE;
	}
	
	@Override
	public final int getScale() {
		return Scale3f.SCALE;
	}

	@Override
	public final Class<Decimal3f> immutableType() {
		return Decimal3f.class;
	}

	@Override
	public final Class<MutableDecimal3f> mutableType() {
		return MutableDecimal3f.class;
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
	public final Decimal3f valueOf(long value) {
		return Decimal3f.valueOf(value);
	}

	@Override
	public final Decimal3f valueOf(float value) {
		return Decimal3f.valueOf(value);
	}

	@Override
	public final Decimal3f valueOf(float value, RoundingMode roundingMode) {
		return Decimal3f.valueOf(value, roundingMode);
	}

	@Override
	public final Decimal3f valueOf(double value) {
		return Decimal3f.valueOf(value);
	}

	@Override
	public final Decimal3f valueOf(double value, RoundingMode roundingMode) {
		return Decimal3f.valueOf(value, roundingMode);
	}

	@Override
	public final Decimal3f valueOf(BigInteger value) {
		return Decimal3f.valueOf(value);
	}

	@Override
	public final Decimal3f valueOf(BigDecimal value) {
		return Decimal3f.valueOf(value);
	}

	@Override
	public final Decimal3f valueOf(BigDecimal value, RoundingMode roundingMode) {
		return Decimal3f.valueOf(value, roundingMode);
	}

	@Override
	public final Decimal3f valueOf(Decimal<?> value) {
		return Decimal3f.valueOf(value);
	}

	@Override
	public final Decimal3f valueOf(Decimal<?> value, RoundingMode roundingMode) {
		return Decimal3f.valueOf(value, roundingMode);
	}

	@Override
	public final Decimal3f parse(String value) {
		return Decimal3f.valueOf(value);
	}

	@Override
	public final Decimal3f parse(String value, RoundingMode roundingMode) {
		return Decimal3f.valueOf(value, roundingMode);
	}

	@Override
	public final Decimal3f valueOfUnscaled(long unscaledValue) {
		return Decimal3f.valueOfUnscaled(unscaledValue);
	}

	@Override
	public final Decimal3f valueOfUnscaled(long unscaledValue, int scale) {
		return Decimal3f.valueOfUnscaled(unscaledValue, scale);
	}

	@Override
	public final Decimal3f valueOfUnscaled(long unscaledValue, int scale, RoundingMode roundingMode) {
		return Decimal3f.valueOfUnscaled(unscaledValue, scale, roundingMode);
	}

	@Override
	public final Decimal3f[] newArray(int length) {
		return new Decimal3f[length];
	}

	@Override
	public final MutableDecimal3f newMutable() {
		return new MutableDecimal3f();
	}

	@Override
	public final MutableDecimal3f[] newMutableArray(int length) {
		return new MutableDecimal3f[length];
	}
}

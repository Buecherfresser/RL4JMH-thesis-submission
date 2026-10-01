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
import org.decimal4j.immutable.Decimal5f;
import org.decimal4j.mutable.MutableDecimal5f;
import org.decimal4j.scale.Scale5f;
import org.decimal4j.scale.ScaleMetrics;

/**
 * The factory for decimals with scale 5 creating {@link Decimal5f} and
 * {@link MutableDecimal5f} instances.
 */
public enum Factory5f implements DecimalFactory<Scale5f> {

	/**
	 * Singleton factory instance for immutable and mutable decimals with scale 5.
	 */
	INSTANCE;

	@Override
	public final Scale5f getScaleMetrics() {
		return Scale5f.INSTANCE;
	}
	
	@Override
	public final int getScale() {
		return Scale5f.SCALE;
	}

	@Override
	public final Class<Decimal5f> immutableType() {
		return Decimal5f.class;
	}

	@Override
	public final Class<MutableDecimal5f> mutableType() {
		return MutableDecimal5f.class;
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
	public final Decimal5f valueOf(long value) {
		return Decimal5f.valueOf(value);
	}

	@Override
	public final Decimal5f valueOf(float value) {
		return Decimal5f.valueOf(value);
	}

	@Override
	public final Decimal5f valueOf(float value, RoundingMode roundingMode) {
		return Decimal5f.valueOf(value, roundingMode);
	}

	@Override
	public final Decimal5f valueOf(double value) {
		return Decimal5f.valueOf(value);
	}

	@Override
	public final Decimal5f valueOf(double value, RoundingMode roundingMode) {
		return Decimal5f.valueOf(value, roundingMode);
	}

	@Override
	public final Decimal5f valueOf(BigInteger value) {
		return Decimal5f.valueOf(value);
	}

	@Override
	public final Decimal5f valueOf(BigDecimal value) {
		return Decimal5f.valueOf(value);
	}

	@Override
	public final Decimal5f valueOf(BigDecimal value, RoundingMode roundingMode) {
		return Decimal5f.valueOf(value, roundingMode);
	}

	@Override
	public final Decimal5f valueOf(Decimal<?> value) {
		return Decimal5f.valueOf(value);
	}

	@Override
	public final Decimal5f valueOf(Decimal<?> value, RoundingMode roundingMode) {
		return Decimal5f.valueOf(value, roundingMode);
	}

	@Override
	public final Decimal5f parse(String value) {
		return Decimal5f.valueOf(value);
	}

	@Override
	public final Decimal5f parse(String value, RoundingMode roundingMode) {
		return Decimal5f.valueOf(value, roundingMode);
	}

	@Override
	public final Decimal5f valueOfUnscaled(long unscaledValue) {
		return Decimal5f.valueOfUnscaled(unscaledValue);
	}

	@Override
	public final Decimal5f valueOfUnscaled(long unscaledValue, int scale) {
		return Decimal5f.valueOfUnscaled(unscaledValue, scale);
	}

	@Override
	public final Decimal5f valueOfUnscaled(long unscaledValue, int scale, RoundingMode roundingMode) {
		return Decimal5f.valueOfUnscaled(unscaledValue, scale, roundingMode);
	}

	@Override
	public final Decimal5f[] newArray(int length) {
		return new Decimal5f[length];
	}

	@Override
	public final MutableDecimal5f newMutable() {
		return new MutableDecimal5f();
	}

	@Override
	public final MutableDecimal5f[] newMutableArray(int length) {
		return new MutableDecimal5f[length];
	}
}

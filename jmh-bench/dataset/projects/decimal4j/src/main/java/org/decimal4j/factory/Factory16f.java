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
import org.decimal4j.immutable.Decimal16f;
import org.decimal4j.mutable.MutableDecimal16f;
import org.decimal4j.scale.Scale16f;
import org.decimal4j.scale.ScaleMetrics;

/**
 * The factory for decimals with scale 16 creating {@link Decimal16f} and
 * {@link MutableDecimal16f} instances.
 */
public enum Factory16f implements DecimalFactory<Scale16f> {

	/**
	 * Singleton factory instance for immutable and mutable decimals with scale 16.
	 */
	INSTANCE;

	@Override
	public final Scale16f getScaleMetrics() {
		return Scale16f.INSTANCE;
	}
	
	@Override
	public final int getScale() {
		return Scale16f.SCALE;
	}

	@Override
	public final Class<Decimal16f> immutableType() {
		return Decimal16f.class;
	}

	@Override
	public final Class<MutableDecimal16f> mutableType() {
		return MutableDecimal16f.class;
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
	public final Decimal16f valueOf(long value) {
		return Decimal16f.valueOf(value);
	}

	@Override
	public final Decimal16f valueOf(float value) {
		return Decimal16f.valueOf(value);
	}

	@Override
	public final Decimal16f valueOf(float value, RoundingMode roundingMode) {
		return Decimal16f.valueOf(value, roundingMode);
	}

	@Override
	public final Decimal16f valueOf(double value) {
		return Decimal16f.valueOf(value);
	}

	@Override
	public final Decimal16f valueOf(double value, RoundingMode roundingMode) {
		return Decimal16f.valueOf(value, roundingMode);
	}

	@Override
	public final Decimal16f valueOf(BigInteger value) {
		return Decimal16f.valueOf(value);
	}

	@Override
	public final Decimal16f valueOf(BigDecimal value) {
		return Decimal16f.valueOf(value);
	}

	@Override
	public final Decimal16f valueOf(BigDecimal value, RoundingMode roundingMode) {
		return Decimal16f.valueOf(value, roundingMode);
	}

	@Override
	public final Decimal16f valueOf(Decimal<?> value) {
		return Decimal16f.valueOf(value);
	}

	@Override
	public final Decimal16f valueOf(Decimal<?> value, RoundingMode roundingMode) {
		return Decimal16f.valueOf(value, roundingMode);
	}

	@Override
	public final Decimal16f parse(String value) {
		return Decimal16f.valueOf(value);
	}

	@Override
	public final Decimal16f parse(String value, RoundingMode roundingMode) {
		return Decimal16f.valueOf(value, roundingMode);
	}

	@Override
	public final Decimal16f valueOfUnscaled(long unscaledValue) {
		return Decimal16f.valueOfUnscaled(unscaledValue);
	}

	@Override
	public final Decimal16f valueOfUnscaled(long unscaledValue, int scale) {
		return Decimal16f.valueOfUnscaled(unscaledValue, scale);
	}

	@Override
	public final Decimal16f valueOfUnscaled(long unscaledValue, int scale, RoundingMode roundingMode) {
		return Decimal16f.valueOfUnscaled(unscaledValue, scale, roundingMode);
	}

	@Override
	public final Decimal16f[] newArray(int length) {
		return new Decimal16f[length];
	}

	@Override
	public final MutableDecimal16f newMutable() {
		return new MutableDecimal16f();
	}

	@Override
	public final MutableDecimal16f[] newMutableArray(int length) {
		return new MutableDecimal16f[length];
	}
}

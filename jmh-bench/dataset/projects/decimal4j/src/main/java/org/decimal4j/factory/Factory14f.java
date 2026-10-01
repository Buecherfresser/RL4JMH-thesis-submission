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
import org.decimal4j.immutable.Decimal14f;
import org.decimal4j.mutable.MutableDecimal14f;
import org.decimal4j.scale.Scale14f;
import org.decimal4j.scale.ScaleMetrics;

/**
 * The factory for decimals with scale 14 creating {@link Decimal14f} and
 * {@link MutableDecimal14f} instances.
 */
public enum Factory14f implements DecimalFactory<Scale14f> {

	/**
	 * Singleton factory instance for immutable and mutable decimals with scale 14.
	 */
	INSTANCE;

	@Override
	public final Scale14f getScaleMetrics() {
		return Scale14f.INSTANCE;
	}
	
	@Override
	public final int getScale() {
		return Scale14f.SCALE;
	}

	@Override
	public final Class<Decimal14f> immutableType() {
		return Decimal14f.class;
	}

	@Override
	public final Class<MutableDecimal14f> mutableType() {
		return MutableDecimal14f.class;
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
	public final Decimal14f valueOf(long value) {
		return Decimal14f.valueOf(value);
	}

	@Override
	public final Decimal14f valueOf(float value) {
		return Decimal14f.valueOf(value);
	}

	@Override
	public final Decimal14f valueOf(float value, RoundingMode roundingMode) {
		return Decimal14f.valueOf(value, roundingMode);
	}

	@Override
	public final Decimal14f valueOf(double value) {
		return Decimal14f.valueOf(value);
	}

	@Override
	public final Decimal14f valueOf(double value, RoundingMode roundingMode) {
		return Decimal14f.valueOf(value, roundingMode);
	}

	@Override
	public final Decimal14f valueOf(BigInteger value) {
		return Decimal14f.valueOf(value);
	}

	@Override
	public final Decimal14f valueOf(BigDecimal value) {
		return Decimal14f.valueOf(value);
	}

	@Override
	public final Decimal14f valueOf(BigDecimal value, RoundingMode roundingMode) {
		return Decimal14f.valueOf(value, roundingMode);
	}

	@Override
	public final Decimal14f valueOf(Decimal<?> value) {
		return Decimal14f.valueOf(value);
	}

	@Override
	public final Decimal14f valueOf(Decimal<?> value, RoundingMode roundingMode) {
		return Decimal14f.valueOf(value, roundingMode);
	}

	@Override
	public final Decimal14f parse(String value) {
		return Decimal14f.valueOf(value);
	}

	@Override
	public final Decimal14f parse(String value, RoundingMode roundingMode) {
		return Decimal14f.valueOf(value, roundingMode);
	}

	@Override
	public final Decimal14f valueOfUnscaled(long unscaledValue) {
		return Decimal14f.valueOfUnscaled(unscaledValue);
	}

	@Override
	public final Decimal14f valueOfUnscaled(long unscaledValue, int scale) {
		return Decimal14f.valueOfUnscaled(unscaledValue, scale);
	}

	@Override
	public final Decimal14f valueOfUnscaled(long unscaledValue, int scale, RoundingMode roundingMode) {
		return Decimal14f.valueOfUnscaled(unscaledValue, scale, roundingMode);
	}

	@Override
	public final Decimal14f[] newArray(int length) {
		return new Decimal14f[length];
	}

	@Override
	public final MutableDecimal14f newMutable() {
		return new MutableDecimal14f();
	}

	@Override
	public final MutableDecimal14f[] newMutableArray(int length) {
		return new MutableDecimal14f[length];
	}
}

/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package net.imglib2.script.math;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.math.fn.BinaryOperation;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.type.numeric.RealType;

/** Xor two float values by first casting them to int.
 *  While the casting may look outrageous, that's what ImgJ does
 *  Xor is thus the expected behavior. In the future, we may be able
 *  to provide a type converter that reads actual integers from integer
 *  Imgs when possible. */
/**
 * TODO
 *
 */
public class Xor extends BinaryOperation
{
	public <S extends RealType<S>, R extends RealType<R>> Xor(final IterableRealInterval<S> left, final IterableRealInterval<R> right) {
		super(left, right);
	}

	public <R extends RealType<R>> Xor(final IFunction fn, final IterableRealInterval<R> right) {
		super(fn, right);
	}

	public <R extends RealType<R>> Xor(final IterableRealInterval<R> left, final IFunction fn) {
		super(left, fn);
	}

	public Xor(final IFunction fn1, final IFunction fn2) {
		super(fn1, fn2);
	}
	
	public <R extends RealType<R>> Xor(final IterableRealInterval<R> left, final Number val) {
		super(left, val);
	}

	public <R extends RealType<R>> Xor(final Number val,final IterableRealInterval<R> right) {
		super(val, right);
	}

	public Xor(final IFunction left, final Number val) {
		super(left, val);
	}

	public Xor(final Number val,final IFunction right) {
		super(val, right);
	}
	
	public Xor(final Number val1, final Number val2) {
		super(val1, val2);
	}

	public Xor(final Object... elems) throws Exception {
		super(elems);
	}

	@Override
	public final double eval() {
		return ((int)a().eval()) ^ ((int)b().eval());
	}
}

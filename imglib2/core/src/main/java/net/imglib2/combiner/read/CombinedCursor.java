/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.combiner.read;

import net.imglib2.Cursor;
import net.imglib2.combiner.AbstractCombinedCursor;
import net.imglib2.combiner.Combiner;
import net.imglib2.type.Type;

/**
 * 
 * @author Christian Dietz
 */
public class CombinedCursor< A, B, C extends Type< C > > extends AbstractCombinedCursor< A, B, C >
{
	final protected Combiner< A, B, C > combiner;

	final protected C combined;

	/**
	 * Creates a copy of b for conversion that can be accessed through
	 * {@link #get()}.
	 * 
	 * @param sourceA
	 * @param converter
	 * @param b
	 */
	public CombinedCursor( final Cursor< A > sourceA, final Cursor< B > sourceB, final Combiner< A, B, C > combiner, final C c )
	{
		super( sourceA, sourceB );

		this.combiner = combiner;
		this.combined = c.copy();
	}

	@Override
	public C get()
	{
		combiner.combine( sourceA.get(), sourceB.get(), combined );
		return combined;
	}

	@Override
	public CombinedCursor< A, B, C > copy()
	{
		return new CombinedCursor< A, B, C >( ( Cursor< A > ) sourceA.copy(), ( Cursor< B > ) sourceB.copy(), combiner, combined );
	}
}

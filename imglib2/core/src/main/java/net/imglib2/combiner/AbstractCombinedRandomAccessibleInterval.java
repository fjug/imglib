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

package net.imglib2.combiner;

import net.imglib2.Interval;
import net.imglib2.Positionable;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPositionable;

/**
 * @author Christian Dietz (University of Konstanz)
 */
abstract public class AbstractCombinedRandomAccessibleInterval< A, B, C > implements RandomAccessibleInterval< C >
{
	final protected RandomAccessibleInterval< A > sourceA;

	final protected RandomAccessibleInterval< B > sourceB;

	public AbstractCombinedRandomAccessibleInterval( final RandomAccessibleInterval< A > sourceA, final RandomAccessibleInterval< B > sourceB )
	{
		this.sourceA = sourceA;
		this.sourceB = sourceB;
	}

	@Override
	public int numDimensions()
	{
		return sourceA.numDimensions();
	}

	@Override
	abstract public AbstractCombinedRandomAccess< A, B, C > randomAccess();

	@Override
	abstract public AbstractCombinedRandomAccess< A, B, C > randomAccess( final Interval interval );

	@Override
	public long min( final int d )
	{
		return sourceA.min( d );
	}

	@Override
	public void min( final long[] min )
	{
		sourceA.min( min );
	}

	@Override
	public void min( final Positionable min )
	{
		sourceA.min( min );
	}

	@Override
	public long max( final int d )
	{
		return sourceA.max( d );
	}

	@Override
	public void max( final long[] max )
	{
		sourceA.max( max );
	}

	@Override
	public void max( final Positionable max )
	{
		sourceA.max( max );
	}

	@Override
	public void dimensions( final long[] dimensions )
	{
		sourceA.dimensions( dimensions );
	}

	@Override
	public long dimension( final int d )
	{
		return sourceA.dimension( d );
	}

	@Override
	public double realMin( final int d )
	{
		return sourceA.realMin( d );
	}

	@Override
	public void realMin( final double[] min )
	{
		sourceA.realMin( min );
	}

	@Override
	public void realMin( final RealPositionable min )
	{
		sourceA.realMin( min );
	}

	@Override
	public double realMax( final int d )
	{
		return sourceA.realMax( d );
	}

	@Override
	public void realMax( final double[] max )
	{
		sourceA.realMax( max );
	}

	@Override
	public void realMax( final RealPositionable max )
	{
		sourceA.realMax( max );
	}
}

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

package net.imglib2.img.planar;

import net.imglib2.AbstractCursorInt;
import net.imglib2.type.NativeType;

/**
 * Basic Iterator for {@link PlanarImg PlanarContainers}
 * @param <T>
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class PlanarCursor< T extends NativeType< T > > extends AbstractCursorInt< T > implements PlanarImg.PlanarContainerSampler
{
	protected final T type;

	protected final PlanarImg< T, ? > container;

	protected final int lastIndex, lastSliceIndex;
	protected int sliceIndex;
	
	/**
	 * The current index of the type.
	 * It is faster to duplicate this here than to access it through type.getIndex(). 
	 */
	protected int index;
	
	protected PlanarCursor( final PlanarCursor< T > cursor )
	{
		super( cursor.numDimensions() );

		container = cursor.container;
		this.type = container.createLinkedType();
		
		lastIndex = cursor.lastIndex;
		lastSliceIndex = cursor.lastSliceIndex;
		sliceIndex = cursor.sliceIndex;
		index = cursor.index;
		
		type.updateContainer( this );
		type.updateIndex( index );
	}
	
	public PlanarCursor( final PlanarImg< T, ? > container )
	{
		super( container.numDimensions() );

		this.type = container.createLinkedType();
		this.container = container;

		lastIndex = ( ( n > 1 ) ? container.dimensions[ 1 ] : 1 )  *  container.dimensions[ 0 ] - 1;
		lastSliceIndex = container.numSlices() - 1;
		
		reset();
	}

	@Override
	public int getCurrentSliceIndex() { return sliceIndex; }

	@Override
	public T get() { return type; }
	
	@Override
	public PlanarCursor< T > copy()
	{
		return new PlanarCursor< T >( this );
	}

	@Override
	public PlanarCursor< T > copyCursor()
	{
		return copy();
	}

	/**
	 * Note: This test is fragile in a sense that it returns true for elements
	 * after the last element as well.
	 * 
	 * @return false for the last element 
	 */
	@Override
	public boolean hasNext()
	{
		return ( sliceIndex < lastSliceIndex ) || ( index < lastIndex );		
	}

	@Override
	public void fwd()
	{
		if ( ++index > lastIndex )
		{
			index = 0;
			++sliceIndex;
			type.updateContainer( this );
		}
		type.updateIndex( index );
	}

	@Override
	public void jumpFwd( long steps )
	{
		long newIndex = index + steps;
		if ( newIndex > lastIndex )
		{
			final long s = newIndex / (lastIndex + 1);
			newIndex -= s * (lastIndex + 1);
			sliceIndex += s;
			type.updateContainer( this );
		}
		index = ( int ) newIndex;
		type.updateIndex( index );
	}

	@Override
	public void reset()
	{
		sliceIndex = 0;
		index = -1;
		type.updateIndex( -1 );
		type.updateContainer( this );
	}

	@Override
	public String toString() { return type.toString(); }

	@Override
	public void localize( final int[] position )
	{
		container.indexToGlobalPosition( sliceIndex, index, position );
	}

	@Override
	public int getIntPosition( final int dim )
	{
		return container.indexToGlobalPosition( sliceIndex, index, dim );
	}
}

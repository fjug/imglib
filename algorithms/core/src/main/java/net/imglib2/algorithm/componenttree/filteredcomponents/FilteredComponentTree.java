/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
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

package net.imglib2.algorithm.componenttree.filteredcomponents;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.componenttree.Component;
import net.imglib2.algorithm.componenttree.ComponentTree;
import net.imglib2.algorithm.componenttree.ComponentTreeAlgorithm;
import net.imglib2.algorithm.componenttree.pixellist.PixelList;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.LongType;

/**
 * Component tree of an image stored as a tree of {@link FilteredComponentTreeNode}s.
 * This class is used both to represent and build the tree. For building the
 * tree {@link Component.Handler} is implemented to gather
 * {@link FilteredComponent} emitted by {@link ComponentTreeAlgorithm}. Only
 * components in a specific size range are accepted. The tree contains only one
 * {@link FilteredComponentTreeNode} per branch. This is the component with the highest
 * threshold, i.e., right before the branch joins another.
 *
 * @param <T>
 *            value type of the input image.
 *
 * @author Tobias Pietzsch, Florian Jug
 */
public final class FilteredComponentTree< T extends Type< T > > implements ComponentTree< T, FilteredComponentTreeNode< T > >,
Component.Handler< FilteredComponent< T > >, Iterable< FilteredComponentTreeNode< T > >
{

	/**
	 * Build a component tree from an input image. Calls
	 * {@link #buildComponentTree(RandomAccessibleInterval, RealType, ImgFactory, boolean)}
	 * using an {@link ArrayImgFactory} or {@link CellImgFactory} depending on
	 * input image size.
	 *
	 * @param input
	 *            the input image.
	 * @param type
	 *            a variable of the input image type.
	 * @param minComponentSize
	 *            minimum allowed size for an accepted component.
	 * @param maxComponentSize
	 *            maximum allowed size for an accepted component.
	 * @param darkToBright
	 *            whether to apply thresholds from dark to bright (true) or
	 *            bright to dark (false)
	 * @return component tree of the image.
	 */
	public static < T extends RealType< T > > FilteredComponentTree< T > buildComponentTree( final RandomAccessibleInterval< T > input, final T type, final long minComponentSize, final long maxComponentSize, final boolean darkToBright ) {
		final int numDimensions = input.numDimensions();
		long size = 1;
		for ( int d = 0; d < numDimensions; ++d )
			size *= input.dimension( d );
		if ( size > Integer.MAX_VALUE ) {
			final int cellSize = ( int ) Math.pow( Integer.MAX_VALUE / new LongType().getEntitiesPerPixel(), 1.0 / numDimensions );
			return buildComponentTree( input, type, new CellImgFactory< LongType >( cellSize ), minComponentSize, maxComponentSize, darkToBright );
		} else
			return buildComponentTree( input, type, new ArrayImgFactory< LongType >(), minComponentSize, maxComponentSize, darkToBright );
	}

	/**
	 * Build a component tree from an input image.
	 *
	 * @param input
	 *            the input image.
	 * @param type
	 *            a variable of the input image type.
	 * @param imgFactory
	 *            used for creating the {@link PixelList} image {@see
	 *            FilteredComponentGenerator}.
	 * @param minComponentSize
	 *            minimum allowed size for an accepted component.
	 * @param maxComponentSize
	 *            maximum allowed size for an accepted component.
	 * @param darkToBright
	 *            whether to apply thresholds from dark to bright (true) or
	 *            bright to dark (false)
	 * @return component tree of the image.
	 */
	public static < T extends RealType< T > > FilteredComponentTree< T > buildComponentTree( final RandomAccessibleInterval< T > input, final T type, final ImgFactory< LongType > imgFactory, final long minComponentSize, final long maxComponentSize, final boolean darkToBright ) {
		final T max = type.createVariable();
		max.setReal( darkToBright ? type.getMaxValue() : type.getMinValue() );
		final FilteredComponentGenerator< T > generator = new FilteredComponentGenerator< T >( max, input, imgFactory );
		final FilteredComponentTree< T > tree = new FilteredComponentTree< T >( minComponentSize, maxComponentSize );
		ComponentTreeAlgorithm.buildComponentTree( input, generator, tree, darkToBright );
		return tree;
	}

	private final ArrayList< FilteredComponentTreeNode< T > > nodes;

	private final HashSet< FilteredComponentTreeNode< T > > roots;

	private final long minComponentSize;

	private final long maxComponentSize;

	private FilteredComponentTree( final long minComponentSize, final long maxComponentSize ) {
		roots = new HashSet< FilteredComponentTreeNode< T > >();
		nodes = new ArrayList< FilteredComponentTreeNode< T > >();
		this.minComponentSize = minComponentSize;
		this.maxComponentSize = maxComponentSize;
	}

	@Override
	public void emit( final FilteredComponent< T > intermediate )
	{
		final long size = intermediate.pixelList.size();
		if ( size >= minComponentSize && size <= maxComponentSize ) {
			int numChildren = 0;
			if ( intermediate.emittedComponent != null ) ++numChildren;
			for ( final FilteredComponent< T > c : intermediate.children )
				if ( c.emittedComponent != null ) ++numChildren;
			if ( numChildren == 1 ) {
				// update previously emitted node
				FilteredComponentTreeNode< T > component = intermediate.emittedComponent;
				if ( component == null )
					for ( final FilteredComponent< T > c : intermediate.children )
						if ( c.emittedComponent != null )
							component = c.emittedComponent;
				component.update( intermediate );
			} else {
				// create new node
				final FilteredComponentTreeNode< T > component = new FilteredComponentTreeNode< T >( intermediate );
				for ( final FilteredComponentTreeNode< T > c : component.getChildren() ) {
					roots.remove( c );
				}
				roots.add( component );
				nodes.add( component );
			}
		} else {
			//			for ( final FilteredComponentIntermediate< T > c : intermediate.children )
			//				c.freeForReuse();
			intermediate.children.clear();
		}
	}

	/**
	 * Returns an iterator over all connected components in the tree.
	 *
	 * @return iterator over all connected components in the tree.
	 */
	@Override
	public Iterator< FilteredComponentTreeNode< T > > iterator()
	{
		return nodes.iterator();
	}

	/**
	 * @see net.imglib2.algorithm.componenttree.ComponentTree#roots()
	 */
	@Override
	public Set< FilteredComponentTreeNode< T > > roots() {
		return roots;
	}
}

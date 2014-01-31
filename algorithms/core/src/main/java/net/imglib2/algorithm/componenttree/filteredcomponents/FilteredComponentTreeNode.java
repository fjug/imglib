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
import java.util.Iterator;
import java.util.List;

import net.imglib2.Localizable;
import net.imglib2.algorithm.componenttree.ComponentTreeNode;
import net.imglib2.algorithm.componenttree.pixellist.PixelList;
import net.imglib2.algorithm.componenttree.pixellist.PixelListComponentTree;
import net.imglib2.type.Type;

/**
 * A connected component of the image thresholded at {@link #maxValue()}. The set
 * of pixels can be accessed by iterating ({@link #iterator()}) the component.
 *
 * This is a node in a {@link FilteredComponentTree}. The child and parent
 * nodes can be accessed by {@link #getChildren()} and {@link #getParent()}.
 *
 * @param <T>
 *            value type of the input image.
 *
 * @author Tobias Pietzsch, Florian Jug
 */
public final class FilteredComponentTreeNode< T extends Type< T > > implements ComponentTreeNode< T, FilteredComponentTreeNode< T > >
{

	/**
	 * child nodes in the {@link FilteredComponentTree}.
	 */
	final List< FilteredComponentTreeNode< T > > children;

	/**
	 * parent node in the {@link FilteredComponentTree}.
	 */
	private FilteredComponentTreeNode< T > parent;

	/**
	 * Minimum threshold value of the connected component.
	 */
	private final T minValue;

	/**
	 * Maximum threshold value of the connected component.
	 */
	private final T maxValue;

	/**
	 * minimum size (maximum size can be determined from {@link #pixelList}).
	 */
	private final long minSize;

	/**
	 * Pixels in the component.
	 */
	private PixelList pixelList;

	void update( final FilteredComponent< T > intermediate ) {
		maxValue.set( intermediate.getValue() );
		pixelList = new PixelList( intermediate.pixelList );
		intermediate.emittedComponent = this;
		intermediate.children.clear();
	}

	FilteredComponentTreeNode( final FilteredComponent< T > intermediate ) {
		children = new ArrayList< FilteredComponentTreeNode< T > >();
		parent = null;
		minValue = intermediate.getValue().copy();
		maxValue = intermediate.getValue().copy();
		pixelList = new PixelList( intermediate.pixelList );
		minSize = pixelList.size();
		if ( intermediate.emittedComponent != null ) {
			children.add( intermediate.emittedComponent );
			intermediate.emittedComponent.parent = this;
		}
		for ( final FilteredComponent< T > c : intermediate.children )
			if ( c.emittedComponent != null ) {
				children.add( c.emittedComponent );
				c.emittedComponent.parent = this;
			}
		intermediate.emittedComponent = this;
		intermediate.children.clear();
	}

	/**
	 * Get the image threshold that created the maximum extremal region
	 * associated to this component.
	 *
	 * @return the image threshold that created the extremal region.
	 */
	public T maxValue() {
		return maxValue;
	}

	/**
	 * Get the image threshold that created the minimum extremal region
	 * associated to this component.
	 *
	 * @return the image threshold that created the extremal region.
	 */
	public T minValue() {
		return minValue;
	}

	/**
	 * Get the number of pixels in the maximum extremal region associated to
	 * this component.
	 *
	 * @return number of pixels in the extremal region.
	 */
	public long maxSize() {
		return pixelList.size();
	}

	/**
	 * Get the number of pixels in the minimum extremal region associated to
	 * this component.
	 *
	 * @return number of pixels in the extremal region.
	 */
	public long minSize() {
		return minSize;
	}

	/**
	 * Get an iterator over the pixel locations ({@link Localizable}) in this
	 * connected component.
	 *
	 * @return iterator over locations.
	 */
	@Override
	public Iterator< Localizable > iterator() {
		return pixelList.iterator();
	}

	/**
	 * Get the children of this node in the {@link PixelListComponentTree}.
	 *
	 * @return the children of this node in the {@link PixelListComponentTree}.
	 */
	@Override
	public List< FilteredComponentTreeNode< T > > getChildren() {
		return children;
	}

	/**
	 * Get the parent of this node in the {@link PixelListComponentTree}.
	 *
	 * @return the parent of this node in the {@link PixelListComponentTree}.
	 */
	@Override
	public FilteredComponentTreeNode< T > getParent() {
		return parent;
	}

	/**
	 * Adds a child to the node representation of this component.
	 */
	public void addChild( final FilteredComponentTreeNode< T > child ) {
		this.children.add( child );
	}

	/**
	 * Sets the parent of the node representing this component.
	 */
	public void setParent( final FilteredComponentTreeNode< T > parent ) {
		this.parent = parent;
	}

	/**
	 * Get the number of pixels in the maximum extremal region associated to
	 * this component. (By calling <code>maxSize()</code>.)
	 *
	 * @return number of pixels in the extremal region.
	 *
	 * @see net.imglib2.algorithm.componenttree.ComponentTreeNode#getSize()
	 */
	@Override
	public long getSize() {
		return maxSize();
	}

	/**
	 * Get the image threshold that created the maximum extremal region
	 * associated to this component. (By calling <code>maxValue()</code>.)
	 *
	 * @return the image threshold that created the extremal region.
	 *
	 * @see net.imglib2.algorithm.componenttree.ComponentTreeNode#getValue()
	 */
	@Override
	public T getValue() {
		return maxValue();
	}
}

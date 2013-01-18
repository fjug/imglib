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

package net.imglib2.algorithm.componenttree.mser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;

import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.componenttree.Component;
import net.imglib2.algorithm.componenttree.ComponentTree;
import net.imglib2.algorithm.componenttree.ComponentTreeAlgorithm;
import net.imglib2.algorithm.componenttree.pixellist.PixelList;
import net.imglib2.algorithm.componenttree.pixellist.PixelListComponentTreeNode;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.LongType;

/**
 * MSER tree of an image stored as a tree of {@link PixelListComponentTreeNode}s. This
 * class is used both to represent and build the tree. For building the tree
 * {@link Component.Handler} is implemented to gather
 * {@link MserComponent} emitted by {@link ComponentTreeAlgorithm}.
 *
 * <p>
 * Maximally Stable Extremal Regions (MSER) are selected from the component tree
 * as follows. For each component, an instability score is computed as
 * <!-- |R_i - R_{i-\Delta}| / |R_i| -->
 * <math style="display:block"><mi>s</mi><mfenced><msub><mi>R</mi><mi>i</mi></msub></mfenced><mo>=</mo><mfrac><mfenced open="|" close="|"><mrow><msub><mi>R</mi><mi>i</mi></msub><mo lspace=mediummathspace rspace=mediummathspace>\</mo><msub><mi>R</mi><mrow><mi>i</mi><mo>-</mo><mi>&Delta;</mi></mrow></msub></mrow></mfenced><mfenced open="|" close="|"><msub><mi>R</mi><mi>i</mi></msub></mfenced></mfrac></math>
 * </p>
 *
 * <p>
 * Regions whose score is a local minimum are selected as MSER candidates.
 * </p>
 *
 * <p>
 * A candidate region is discarded if its size (number of pixels) is smaller
 * than <em>minSize</em> or larger than <em>maxSize</em>. A candidate region is
 * discarded if its instability score is greater than <em>maxVar</em>.
 * </p>
 *
 * <p>
 * A tree is build of the remaining candidates. Finally, candidates are pruned
 * from the tree, if they are too similar to their parent: Let <em>A</em>,
 * <em>B</em> be a region and its parent. Then <em>A</em> is discarded if
 * <!-- |B - A| / |B| <= minDiversity -->
 * <math style="display:block"><mfrac><mfenced open="|" close="|"><mrow><mi>B</mi><mo lspace=mediummathspace rspace=mediummathspace>\</mo><mi>A</mi></mrow></mfenced><mfenced open="|" close="|"><mi>B</mi></mfenced></mfrac><mo>&le;</mo><mi>minDiversity</mi></math>
 * </p>
 *
 * <p>
 * <strong>TODO</strong> Add support for non-zero-min RandomAccessibleIntervals.
 * (Currently, we assume that the input image is a <em>zero-min</em> interval.)
 * </p>
 *
 * @param <T>
 *            value type of the input image.
 *
 * @author Tobias Pietzsch, Florian Jug
 */
public final class MserComponentTree< T extends Type< T > > implements ComponentTree< T, MserComponentTreeNode< T > >,
Component.Handler< MserComponent< T > >, Iterable< MserComponentTreeNode< T > >
{
    /**
     * Build a MSER tree from an input image. Calls
     * {@link #buildMserTree(RandomAccessibleInterval, RealType, long, long, double, double, ImgFactory, boolean)}
     * using an {@link ArrayImgFactory} or {@link CellImgFactory} depending on
     * input image size.
     *
     * @param input
     *            the input image.
     * @param delta
     *            delta for computing instability score.
     * @param minSize
     *            minimum size (in pixels) of accepted MSER.
     * @param maxSize
     *            maximum size (in pixels) of accepted MSER.
     * @param maxVar
     *            maximum instability score of accepted MSER.
     * @param minDiversity
     *            minimal diversity of adjacent accepted MSER.
     * @param darkToBright
     *            whether to apply thresholds from dark to bright (true) or
     *            bright to dark (false)
     * @return MSER tree of the image.
     */
    public static < T extends RealType< T > > MserComponentTree< T > buildMserTree( final RandomAccessibleInterval< T > input, final double delta, final long minSize, final long maxSize, final double maxVar, final double minDiversity, final boolean darkToBright )
    {
	return buildMserTree( input, MserComponentTree.getDeltaVariable( input, delta ), minSize, maxSize, maxVar, minDiversity, darkToBright );
    }

    /**
     * Build a MSER tree from an input image. Calls
     * {@link #buildMserTree(RandomAccessibleInterval, RealType, long, long, double, double, ImgFactory, boolean)}
     * using an {@link ArrayImgFactory} or {@link CellImgFactory} depending on
     * input image size.
     *
     * @param input
     *            the input image.
     * @param delta
     *            delta for computing instability score.
     * @param minSize
     *            minimum size (in pixels) of accepted MSER.
     * @param maxSize
     *            maximum size (in pixels) of accepted MSER.
     * @param maxVar
     *            maximum instability score of accepted MSER.
     * @param minDiversity
     *            minimal diversity of adjacent accepted MSER.
     * @param darkToBright
     *            whether to apply thresholds from dark to bright (true) or
     *            bright to dark (false)
     * @return MSER tree of the image.
     */
    public static < T extends RealType< T > > MserComponentTree< T > buildMserTree( final RandomAccessibleInterval< T > input, final T delta, final long minSize, final long maxSize, final double maxVar, final double minDiversity, final boolean darkToBright )
    {
	final int numDimensions = input.numDimensions();
	long size = 1;
	for ( int d = 0; d < numDimensions; ++d )
	    size *= input.dimension( d );
	if( size > Integer.MAX_VALUE ) {
	    final int cellSize = ( int ) Math.pow( Integer.MAX_VALUE / new LongType().getEntitiesPerPixel(), 1.0 / numDimensions );
	    return buildMserTree( input, delta, minSize, maxSize, maxVar, minDiversity, new CellImgFactory< LongType >( cellSize ), darkToBright );
	} else
	    return buildMserTree( input, delta, minSize, maxSize, maxVar, minDiversity, new ArrayImgFactory< LongType >(), darkToBright );
    }

    /**
     * Build a MSER tree from an input image.
     *
     * @param input
     *            the input image.
     * @param delta
     *            delta for computing instability score.
     * @param minSize
     *            minimum size (in pixels) of accepted MSER.
     * @param maxSize
     *            maximum size (in pixels) of accepted MSER.
     * @param maxVar
     *            maximum instability score of accepted MSER.
     * @param minDiversity
     *            minimal diversity of adjacent accepted MSER.
     * @param imgFactory
     *            used for creating the {@link PixelList} image {@see
     *            MserComponentGenerator}.
     * @param darkToBright
     *            whether to apply thresholds from dark to bright (true) or
     *            bright to dark (false)
     * @return MSER tree of the image.
     */
    public static < T extends RealType< T > > MserComponentTree< T > buildMserTree( final RandomAccessibleInterval< T > input, final T delta, final long minSize, final long maxSize, final double maxVar, final double minDiversity, final ImgFactory< LongType > imgFactory, final boolean darkToBright )
    {
	final T max = delta.createVariable();
	max.setReal( darkToBright ? delta.getMaxValue() : delta.getMinValue() );
	final MserComponentGenerator< T > generator = new MserComponentGenerator< T >( max, input, imgFactory );
	final Comparator< T > comparator = darkToBright ? new ComponentTreeAlgorithm.DarkToBright< T >() : new ComponentTreeAlgorithm.BrightToDark< T >();
	final ComputeDelta< T > computeDelta = darkToBright ? new ComputeDeltaDarkToBright< T >( delta ) : new ComputeDeltaBrightToDark< T >( delta );
	final MserComponentTree< T > tree = new MserComponentTree< T >( comparator, computeDelta, minSize, maxSize, maxVar, minDiversity );
	ComponentTreeAlgorithm.buildComponentTree( input, generator, tree, comparator );
	tree.pruneDuplicates();
	return tree;
    }

    /**
     * Build a MSER tree from an input image. Calls
     * {@link #buildMserTree(RandomAccessibleInterval, ComputeDelta, long, long, double, double, ImgFactory, Type, Comparator)}
     * using an {@link ArrayImgFactory} or {@link CellImgFactory} depending on
     * input image size.
     *
     * @param input
     *            the input image.
     * @param computeDelta
     *            to compute (value - delta).
     * @param minSize
     *            minimum size (in pixels) of accepted MSER.
     * @param maxSize
     *            maximum size (in pixels) of accepted MSER.
     * @param maxVar
     *            maximum instability score of accepted MSER.
     * @param minDiversity
     *            minimal diversity of adjacent accepted MSER.
     * @param maxValue
     *            a value (e.g., grey-level) greater than any occurring in the
     *            input image.
     * @param comparator
     *            determines ordering of threshold values.
     * @return MSER tree of the image.
     */
    public static < T extends Type< T > > MserComponentTree< T > buildMserTree( final RandomAccessibleInterval< T > input, final ComputeDelta< T > computeDelta, final long minSize, final long maxSize, final double maxVar, final double minDiversity, final T maxValue, final Comparator< T > comparator )
    {
	final int numDimensions = input.numDimensions();
	long size = 1;
	for ( int d = 0; d < numDimensions; ++d )
	    size *= input.dimension( d );
	if( size > Integer.MAX_VALUE ) {
	    final int cellSize = ( int ) Math.pow( Integer.MAX_VALUE / new LongType().getEntitiesPerPixel(), 1.0 / numDimensions );
	    return buildMserTree( input, computeDelta, minSize, maxSize, maxVar, minDiversity, new CellImgFactory< LongType >( cellSize ), maxValue, comparator );
	} else
	    return buildMserTree( input, computeDelta, minSize, maxSize, maxVar, minDiversity, new ArrayImgFactory< LongType >(), maxValue, comparator );
    }

    /**
     * Build a MSER tree from an input image.
     *
     * @param input
     *            the input image.
     * @param computeDelta
     *            to compute (value - delta).
     * @param minSize
     *            minimum size (in pixels) of accepted MSER.
     * @param maxSize
     *            maximum size (in pixels) of accepted MSER.
     * @param maxVar
     *            maximum instability score of accepted MSER.
     * @param minDiversity
     *            minimal diversity of adjacent accepted MSER.
     * @param imgFactory
     *            used for creating the {@link PixelList} image {@see
     *            MserComponentGenerator}.
     * @param maxValue
     *            a value (e.g., grey-level) greater than any occurring in the
     *            input image.
     * @param comparator
     *            determines ordering of threshold values.
     * @return MSER tree of the image.
     */
    public static < T extends Type< T > > MserComponentTree< T > buildMserTree( final RandomAccessibleInterval< T > input, final ComputeDelta< T > computeDelta, final long minSize, final long maxSize, final double maxVar, final double minDiversity, final ImgFactory< LongType > imgFactory, final T maxValue, final Comparator< T > comparator )
    {
	final MserComponentGenerator< T > generator = new MserComponentGenerator< T >( maxValue, input, imgFactory );
	final MserComponentTree< T > tree = new MserComponentTree< T >( comparator, computeDelta, minSize, maxSize, maxVar, minDiversity );
	ComponentTreeAlgorithm.buildComponentTree( input, generator, tree, comparator );
	tree.pruneDuplicates();
	return tree;
    }

    /**
     * Create a variable of type T with value delta by copying
     * and setting a value from the input {@link RandomAccessibleInterval}.
     */
    private static < T extends RealType< T > > T getDeltaVariable( final RandomAccessibleInterval< T > input, final double delta )
    {
	final RandomAccess< T > a = input.randomAccess();
	input.min( a );
	final T deltaT = a.get().createVariable();
	deltaT.setReal( delta );
	return deltaT;
    }

    private final HashSet< MserComponentTreeNode< T > > roots;

    private final ArrayList< MserComponentTreeNode< T > > nodes;

    private final Comparator< T > comparator;

    private final ComputeDelta< T > delta;

    /**
     * Minimum size (in pixels) of accepted MSER.
     */
    private final long minSize;

    /**
     * Maximum size (in pixels) of accepted MSER.
     */
    private final long maxSize;

    /**
     * Maximum instability score of accepted MSER.
     */
    private final double maxVar;

    /**
     * Minimal diversity of adjacent accepted MSER.
     */
    private final double minDiversity;

    /**
     * The number of minima found {@see #foundNewMinimum(MserEvaluationNode)}
     * since the last {@link #pruneDuplicates()}.
     */
    private int minimaFoundSinceLastPrune;

    private static final int pruneAfterNMinima = 1000;

    private MserComponentTree( final Comparator< T > comparator, final ComputeDelta< T > delta, final long minSize, final long maxSize, final double maxVar, final double minDiversity )
    {
	roots = new HashSet< MserComponentTreeNode< T > >();
	nodes = new ArrayList< MserComponentTreeNode< T > >();
	this.comparator = comparator;
	this.delta = delta;
	this.minSize = minSize;
	this.maxSize = maxSize;
	this.maxVar = maxVar;
	this.minDiversity = minDiversity;
	minimaFoundSinceLastPrune = 0;
    }

    /**
     * Remove from the tree candidates which are too similar to their parent.
     * Let <em>A</em>, <em>B</em> be a region and its parent.
     * Then <em>A</em> is discarded if |B - A| / |B| <= minDiversity.
     */
    private void pruneDuplicates()
    {
	nodes.clear();
	for ( final MserComponentTreeNode< T > node : roots )
	    pruneChildren ( node );
	nodes.addAll( roots );
    }

    private void pruneChildren( final MserComponentTreeNode< T > node )
    {
	final ArrayList< MserComponentTreeNode< T > > validChildren = new ArrayList< MserComponentTreeNode< T > >();
	for ( int i = 0; i < node.getChildren().size(); ++i )
	{
	    final MserComponentTreeNode< T > n = node.getChildren().get( i );
	    final double div = ( node.getSize() - n.getSize() ) / (double) node.getSize();
	    if ( div > minDiversity )
	    {
		validChildren.add( n );
		pruneChildren( n );
	    }
	    else
	    {
		node.getChildren().addAll( n.getChildren() );
		for ( final MserComponentTreeNode< T > n2 : n.getChildren() )
		    n2.setParent( node );
	    }
	}
	node.getChildren().clear();
	node.getChildren().addAll( validChildren );
	nodes.addAll( validChildren );
    }

    @Override
    public void emit( final MserComponent< T > component )
    {
	new MserEvaluationNode< T >( component, comparator, delta, this );
	component.children.clear();
    }

    /**
     * Called when a local minimal {@link MserEvaluationNode} (a MSER candidate)
     * is found.
     *
     * @param node
     *            MSER candidate.
     */
    void foundNewMinimum( final MserEvaluationNode< T > node )
    {
	if ( node.size >= minSize && node.size <= maxSize && node.score <= maxVar )
	{
	    final MserComponentTreeNode< T > mser = new MserComponentTreeNode< T >( node );
	    for ( final MserComponentTreeNode< T > m : node.mserThisOrChildren )
		mser.children.add( m );
	    node.mserThisOrChildren.clear();
	    node.mserThisOrChildren.add( mser );

	    for ( final MserComponentTreeNode< T > n : mser.getChildren() )
		roots.remove( n );
	    roots.add( mser );
	    nodes.add( mser );
	    if ( ++minimaFoundSinceLastPrune == pruneAfterNMinima )
	    {
		minimaFoundSinceLastPrune = 0;
		pruneDuplicates();
	    }
	}
    }

    /**
     * Get number of detected MSERs.
     *
     * @return number of detected MSERs.
     */
    public int size()
    {
	return nodes.size();
    }

    /**
     * Returns an iterator over all MSERs in the tree.
     *
     * @return iterator over all MSERss in the tree.
     */
    @Override
    public Iterator< MserComponentTreeNode< T > > iterator()
    {
	return nodes.iterator();
    }

    /**
     * Get the set of roots of the MSER tree (respectively forest...).
     *
     * @return set of roots.
     */
    @Override
    public HashSet< MserComponentTreeNode< T > > roots()
    {
	return roots;
    }
}

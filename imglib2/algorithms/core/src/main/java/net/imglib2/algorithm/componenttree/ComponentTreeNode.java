/**
 *
 */
package net.imglib2.algorithm.componenttree;

import java.util.List;

import net.imglib2.Localizable;
import net.imglib2.type.Type;

/**
 * This interface is used by the trees built to represent the component tree
 * found by {@link ComponentTreeAlgorithm}. Each <code>ComponentTreeNode</code>
 * represents a component the current implementation of {@link ComponentTree}
 * decides to save.
 *
 * It provides methods to get/set the size and value of the represented
 * component, and methods for navigating to parent and children of this node.
 *
 * @param <T>
 *            value type of the input image.
 *
 * @author Florian Jug
 */
public interface ComponentTreeNode< T extends Type< T >, N extends ComponentTreeNode< T, N > > extends Iterable< Localizable > {

	/**
	 * Get the number of pixels in the represented component.
	 *
	 * @return number of pixels in the extremal region.
	 */
	public long getSize();

	/**
	 * Get the image threshold that created the represented component.
	 *
	 * @return the image threshold that created the extremal region.
	 */
	public T getValue();

	/**
	 * Get the parent of this node in the {@link ComponentTree}.
	 *
	 * @return the parent of this node in the {@link ComponentTree}.
	 */
	public N getParent();

	// /**
	// * Set the parent of this node in the {@link ComponentTree}.
	// *
	// * @param parent the parent to be set.
	// */
	// public void setParent( N parent );

	/**
	 * Get the children of this node in the {@link ComponentTree}.
	 *
	 * @return the children of this node in the {@link ComponentTree}.
	 */
	public List< N > getChildren();
	// /**
	// * Adds a child to this node in the {@link ComponentTree}.
	// *
	// * @param child the child to be added.
	// */
	// public void addChild( N child );

}

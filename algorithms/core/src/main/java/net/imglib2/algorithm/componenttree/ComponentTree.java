/**
 *
 */
package net.imglib2.algorithm.componenttree;

import java.util.Set;

import net.imglib2.type.Type;

/**
 * This interface is implemented by all trees that represent the component tree
 * found by {@link ComponentTreeAlgorithm}.
 * 
 * @param <T>
 *            value type of the input image.
 * 
 * @author Florian Jug
 */
public interface ComponentTree< T extends Type< T >, N extends ComponentTreeNode< T, N > > {

	/**
	 * Get the set of root nodes of this component tree (respectively
	 * forest...).
	 * 
	 * @return set of roots.
	 */
	public Set< N > roots();

}

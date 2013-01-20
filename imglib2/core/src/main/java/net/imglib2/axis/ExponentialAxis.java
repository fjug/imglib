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


package net.imglib2.axis;

import net.imglib2.Axis;
import net.imglib2.function.scaling.ExponentialScalingFunction;

/**
 * 
 * @author Barry DeZonia
 *
 */
public class ExponentialAxis extends AbstractAxis<ExponentialScalingFunction>
{
	public ExponentialAxis(double offset, double scale, double base) {
		super(new ExponentialScalingFunction(offset, scale, base));
	}
	
	@Override
	public ExponentialAxis copy() {
		double offset = getFunction().getOffset();
		double scale = getFunction().getScale();
		double base = getFunction().getBase();
		return new ExponentialAxis(offset, scale, base);
	}

	@Override
	public boolean sameAs(Axis<?> otherAxis) {
		if (otherAxis instanceof ExponentialAxis) {
			ExponentialAxis axis = (ExponentialAxis) otherAxis;
			if (!same(getOffset(), axis.getOffset())) return false;
			if (!same(getScale(), axis.getScale())) return false;
			if (!same(getBase(), axis.getBase())) return false;
			return true;
		}
		return false;
	}
	
	public double getBase() { return getFunction().getBase(); }
	
	public void setBase(double base) { getFunction().setBase(base); }
}

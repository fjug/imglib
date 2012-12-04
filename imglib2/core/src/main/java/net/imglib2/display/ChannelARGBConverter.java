/**
 * Copyright (c) 2009--2012, ImgLib2 developers
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the Fiji project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * @author Tobias Pietzsch
 */
package net.imglib2.display;

import static net.imglib2.display.ChannelARGBConverter.Channel.A;
import static net.imglib2.display.ChannelARGBConverter.Channel.B;
import static net.imglib2.display.ChannelARGBConverter.Channel.G;
import static net.imglib2.display.ChannelARGBConverter.Channel.R;

import java.util.ArrayList;

import net.imglib2.converter.Converter;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

/**
 * Convert UnsignedByteType into one channel of {@link ARGBType}.
 *
 * {@link #converterListRGBA} can be used in {@link CompositeXYProjector} to
 * convert a 4-channel (R,G,B,A) {@link UnsignedByteType} into composite
 * {@link ARGBType}.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public final class ChannelARGBConverter implements Converter< UnsignedByteType, ARGBType >
{
	public ChannelARGBConverter( final Channel channel )
	{
		this.shift = channel.shift;
	}

	/**
	 * {@link #converterListRGBA} can be used in {@link CompositeXYProjector} to
	 * convert a 4-channel {@link UnsignedByteType} into composite
	 * {@link ARGBType}.
	 */
	public static final ArrayList< Converter< UnsignedByteType, ARGBType > > converterListRGBA;
	static
	{
		converterListRGBA = new ArrayList< Converter< UnsignedByteType, ARGBType > >();
		converterListRGBA.add( new ChannelARGBConverter( R ) );
		converterListRGBA.add( new ChannelARGBConverter( G ) );
		converterListRGBA.add( new ChannelARGBConverter( B ) );
		converterListRGBA.add( new ChannelARGBConverter( A ) );
	}

	public static enum Channel
	{
		A( 24 ), R( 16 ), G( 8 ), B( 0 );

		private final int shift;

		Channel( final int shift )
		{
			this.shift = shift;
		}
	}

	final private int shift;

	@Override
	public void convert( final UnsignedByteType input, final ARGBType output )
	{
		output.set( input.get() << shift );
	}
}

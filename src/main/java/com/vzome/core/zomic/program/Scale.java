
package com.vzome.core.zomic.program;

import com.vzome.core.zomic.ZomicException;

public class Scale extends Anything{

	private  int[] /*AlgebraicNumber*/ m_scale;

	public  Scale( int[] /*AlgebraicNumber*/ size ) {
		super();

			m_scale = size;
		}

	public  void accept( Visitor visitor ) throws ZomicException
    {
		visitor .visitScale( m_scale );
	}


}



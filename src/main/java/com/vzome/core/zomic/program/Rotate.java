
package com.vzome.core.zomic.program;

import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.zomic.ZomicException;

public class Rotate extends Permute{

	private  int steps;

	public  void accept( Visitor visitor ) throws ZomicException
	{
		visitor .visitRotate( getAxis(), steps );
	}

	public  Rotate( Axis axis, int steps ) {
		super( axis );

			this .steps = steps;
		}

	public  void setSteps( int steps ) {
		this .steps = steps;
	}

}



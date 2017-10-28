
package com.vzome.core.zomic.program;

import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.zomic.ZomicException;

public class Reflect extends Permute{

    @Override
	public  void accept( Visitor visitor ) throws ZomicException
	{
		visitor .visitReflect( getAxis() );
	}

	public Reflect()
	{
		super( null );
	}

    @Override
	public  void setAxis( Axis axis )
	{
		super .setAxis( axis );
	}
	
}



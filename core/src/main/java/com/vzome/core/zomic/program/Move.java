
package com.vzome.core.zomic.program;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.zomic.ZomicException;

public class Move extends ZomicStatement
{
	protected Axis axis;
	
	protected AlgebraicNumber length;

	public Move( Axis axis, AlgebraicNumber len )
	{
		super();

		this .axis = axis;
		this .length = len;
	}

    @Override
	public  void accept( Visitor visitor ) throws ZomicException
	{
		visitor .visitMove( axis, length );
	}

    /**
     * @return
     */
    public AlgebraicNumber getLength()
    {
        return length;
    }
    
    public Axis getAxis()
    {
        return axis;
    }

    /**
     * Needed only for Zomic XMLS2AST.  TODO: remove this by
     * rearranging the XML?
     * @param axis2
     * @param len
     */
    public void reset( Axis axis, AlgebraicNumber len )
    {
        this .axis = axis;
        this .length = len;
    }

}



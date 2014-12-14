
package com.vzome.core.zomic.program;

import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.zomic.ZomicException;

public class Move extends Anything
{
    public final static int[] VARIABLE_SIZE = { 99,0, 0,0 };  // zero divisors to make sure it is never used

	protected Axis axis;
	
	protected int[] /*AlgebraicNumber*/ length;

	public Move( Axis axis, int[] /*AlgebraicNumber*/ len )
	{
		super();

		this .axis = axis;
		this .length = len;
	}

	public  void accept( Visitor visitor ) throws ZomicException
	{
		visitor .visitMove( axis, length );
	}

    /**
     * @return
     */
    public int[] /*AlgebraicNumber*/ getLength()
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
    public void reset( Axis axis, int[] /*AlgebraicNumber*/ len )
    {
        this .axis = axis;
        this .length = len;
    }

}



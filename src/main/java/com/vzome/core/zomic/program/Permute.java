/*
 * Created on May 18, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.vzome.core.zomic.program;

import com.vzome.core.math.symmetry.Axis;

/**
 * @author vorth
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class Permute extends Anything {

	private Axis m_axis;

	public Permute( Axis axis ){
		m_axis = axis;
	}

	public  void setAxis( Axis axis ) {
		this .m_axis = axis;
	}

	public int getOrder()
	{
		if ( m_axis == null )
			return 2;  // should be one?
		return m_axis .getRotationPermutation() .getOrder();
	}

	public Axis getAxis(){
		return m_axis;
	}

}

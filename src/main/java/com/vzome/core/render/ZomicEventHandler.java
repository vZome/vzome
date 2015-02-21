/*
 * Created on Aug 8, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.vzome.core.render;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Permutation;

/**
 * @author vorth
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface ZomicEventHandler {
    
	/**
	 * Constants for use with save();
	 */
	int ALL = 15, LOCATION = 1, SCALE = 2, ORIENTATION = 4, ACTION = 8;

	/**
	 * Constants for use with action().
	 */
	int JUST_MOVE = 0, BUILD = 1, DESTROY = 2;

	void step( Axis axis, AlgebraicNumber length );
	
	/**
	 * 
	 * @param axis
	 * @param steps
	 */
	void rotate( Axis axis, int steps );
	
	/**
	 * Reflect through a blue axis, or through the current location point
	 * if blueAxis == null.
	 * @param blueAxis
	 */
	void reflect( Axis blueAxis );
	
	void permute( Permutation permutation, int sense );
	
	void scale( AlgebraicNumber scale );
	
	void action( int action );
	
	ZomicEventHandler save( int variables );
	
	void restore( ZomicEventHandler changes, int variables );
}

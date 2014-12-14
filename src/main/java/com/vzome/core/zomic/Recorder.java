

package com.vzome.core.zomic;

import java.util.Stack;

import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Permutation;
import com.vzome.core.render.ZomicEventHandler;
import com.vzome.core.zomic.program.Anything;
import com.vzome.core.zomic.program.Build;
import com.vzome.core.zomic.program.Move;
import com.vzome.core.zomic.program.Reflect;
import com.vzome.core.zomic.program.Rotate;
import com.vzome.core.zomic.program.Save;
import com.vzome.core.zomic.program.Scale;
import com.vzome.core.zomic.program.Untranslatable;
import com.vzome.core.zomic.program.Walk;

/**
 * @author Scott Vorthmann
 */
public class Recorder implements ZomicEventHandler
{
    public interface Output
    {
        void statement( Anything stmt );
    }
    
    protected Output mOutput;
    
    protected final Stack mSaves = new Stack();
    
    public void setOutput( Output output )
    {
        mOutput = output;
    }

    public void record( Anything stmt )
    {
        if ( ! mSaves .isEmpty() )
            ((Walk) mSaves .peek()) .addStatement( stmt );
        else if ( mOutput != null )
            mOutput .statement( stmt );
    }
    
    public void step( Axis axis, /*AlgebraicNumber*/ int[] length )
    {
        record( new Move( axis, length ) );
    }

    public void rotate( Axis axis, int steps )
    {
        record( new Rotate( axis, steps ) );
    }

    public void reflect( Axis blueAxis )
    {
        Reflect r = new Reflect();
        r .setAxis( blueAxis );
        record( r );
    }

    public void permute( Permutation permutation, int sense )
    {
        record( new Untranslatable( "permutation" ) );
    }

    public void scale( /*AlgebraicNumber*/ int[] scale )
    {
        record( new Scale( scale ) );
    }

    public void action( int action )
    {
        record( new Build( (action & BUILD) != 0, (action & DESTROY) != 0 ) );
    }

    public ZomicEventHandler save( int variables )
    {
        mSaves .push( new Walk() );
        return this;
    }

    public /*AlgebraicVector*/ int[] getLocation()
    {
        throw new UnsupportedOperationException();
    }

    public Permutation getPermutation()
    {
        throw new UnsupportedOperationException();
    }

    public void restore( ZomicEventHandler changes, int variables )
    {
        Walk walk = (Walk) mSaves .pop();
        Save save = new Save( variables );
        save .setBody( walk );
        record( save );
    }

}

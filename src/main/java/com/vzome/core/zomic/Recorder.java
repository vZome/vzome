

package com.vzome.core.zomic;

import java.util.Stack;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Permutation;
import com.vzome.core.render.ZomicEventHandler;
import com.vzome.core.zomic.program.ZomicStatement;
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
        void statement( ZomicStatement stmt );
    }
    
    protected Output mOutput;
    
    protected final Stack<Walk> mSaves = new Stack<>();
    
    public void setOutput( Output output )
    {
        mOutput = output;
    }

    public void record( ZomicStatement stmt )
    {
        if ( ! mSaves .isEmpty() )
            mSaves .peek() .addStatement( stmt );
        else if ( mOutput != null )
            mOutput .statement( stmt );
    }
    
    @Override
    public void step( Axis axis, AlgebraicNumber length )
    {
        record( new Move( axis, length ) );
    }

    @Override
    public void rotate( Axis axis, int steps )
    {
        record( new Rotate( axis, steps ) );
    }

    @Override
    public void reflect( Axis blueAxis )
    {
        Reflect r = new Reflect();
        r .setAxis( blueAxis );
        record( r );
    }

    @Override
    public void permute( Permutation permutation, int sense )
    {
        record( new Untranslatable( "permutation" ) );
    }

    @Override
    public void scale( AlgebraicNumber scale )
    {
        record( new Scale( scale ) );
    }

    @Override
    public void action( int action )
    {
        record( new Build( (action & BUILD) != 0, (action & DESTROY) != 0 ) );
    }

    @Override
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

    @Override
    public void restore( ZomicEventHandler changes, int variables )
    {
        Walk walk = mSaves .pop();
        Save save = new Save( variables );
        save .setBody( walk );
        record( save );
    }

}

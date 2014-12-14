package com.vzome.core.zomic;

import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Permutation;
import com.vzome.core.render.ZomicEventHandler;
import com.vzome.core.zomic.program.Nested;
import com.vzome.core.zomic.program.Permute;
import com.vzome.core.zomic.program.Save;
import com.vzome.core.zomic.program.Symmetry;
import com.vzome.core.zomic.program.Visitor;

/**
 * Implements the Zomic execution model while visiting a program.
 * 
 * @author Scott Vorthmann 2003
 */
public class Interpreter extends Visitor.Default
{
    protected ZomicEventHandler mEvents;

    protected final com.vzome.core.math.symmetry.Symmetry mSymmetry;

    public Interpreter( ZomicEventHandler renderer,
            com.vzome.core.math.symmetry.Symmetry symmetry )
    {
        super();

        this.mEvents = renderer;
        mSymmetry = symmetry;
    }

    public void visitMove( Axis axis, int[] /*AlgebraicNumber*/ length )
    {
        mEvents.step( axis, length );
    }

    public void visitRotate( Axis axis, int steps )
    {
        mEvents.rotate( axis, steps );
    }

    public void visitReflect( Axis blueAxis )
    {
        mEvents.reflect( blueAxis );
    }

    public void visitSymmetry( final Symmetry model, Permute permute )
            throws ZomicException
    {

        if ( permute != null ) {
            int repetitions = permute.getOrder();
            if ( repetitions == 1 )
                // this is runtime exception because program should have been
                // checked already
                throw new RuntimeException(
                        "no rotation symmetry around extended axes" );
            for ( int i = 0; i < repetitions; i++ ) {
                saveAndNest( model, ZomicEventHandler.ORIENTATION );
                permute.accept( this );
            }
        } else {
            // "global" symmetry... do all right-handed transformations
            for ( int i = 0; i < mSymmetry .getChiralOrder(); i++ ) {
                Permutation current = mSymmetry .getPermutation( i );
                ZomicEventHandler saved = mEvents;
                mEvents = mEvents.save( ZomicEventHandler.ALL );

                mEvents.permute( current,
                        com.vzome.core.math.symmetry.Symmetry.PLUS );
                try {
                    visitNested( model );
                } catch ( ZomicException e ) {
                    throw new RuntimeException( "error in global symmetry" );
                }

                saved.restore( mEvents, ZomicEventHandler.ALL );
                mEvents = saved;
            }
        }
    }

    private void saveAndNest( Nested stmt, int state ) throws ZomicException
    {
        ZomicEventHandler saved = mEvents;
        mEvents = mEvents.save( state );

        visitNested( stmt );

        saved.restore( mEvents, state );
        mEvents = saved;
    }

    public void visitSave( Save stmt, int state ) throws ZomicException
    {
        saveAndNest( stmt, state );
    }

    public void visitScale( int[] /*AlgebraicNumber*/ size )
    {
        mEvents.scale( size );
    }

    public void visitBuild( boolean build, boolean destroy )
    {
        int action = ZomicEventHandler.JUST_MOVE;
        if ( build )
            action |= ZomicEventHandler.BUILD;
        if ( destroy )
            action |= ZomicEventHandler.DESTROY;
        mEvents.action( action );
    }

    //
    // public static
    // void render( Anything program, StatelessRenderer renderer ) throws
    // ZomeException {
    //        render( program, new StatelessRendererAdapter( renderer ) );
    //    }
    //
    //    public static 
    //    void render( Anything program, MutableStructure structure ) throws ZomeException {
    //        render( program, new Renderer2Structure( structure ) );
    //    }

}

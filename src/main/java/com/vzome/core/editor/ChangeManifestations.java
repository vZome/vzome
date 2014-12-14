
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.HashMap;
import java.util.Map;

import com.vzome.core.construction.Construction;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

public abstract class ChangeManifestations extends ChangeSelection
{
    protected final RealizedModel mManifestations;
    
    public ChangeManifestations( Selection selection, RealizedModel realized, boolean groupInSelection )
    {
        super( selection, groupInSelection );
        
        mManifestations = realized;
        mManifestedNow = new HashMap();
    }
    
    /**
     * This records the NEW manifestations produced by manifestConstruction for this edit,
     * to avoid creating colliding manifestations.  It is never referenced after the last
     * call to manifestConstruction.
     * 
     * TODO: look at implications for unmanifestConstruction
     */
    private transient Map mManifestedNow;  // used only while calling manifest
    
    public void redo()
    {
        if ( mManifestedNow != null )
            mManifestedNow = new HashMap();
        super .redo();
//        System.out.print( " manifestations: " + mManifestations .size() );
    }
    
    public void undo()
    {
        if ( mManifestedNow != null )
            mManifestedNow = null;
        super .undo();
    }
    
    protected Manifestation getManifestation( Construction c )
    {
        return mManifestations .getManifestation( c );
        // TODO assert that the result is non-null
    }
    
    public Manifestation manifestConstruction( Construction c )
    {
//        if ( realizer != null )
//            return realizeConstruction( c );
//        
        Manifestation m = mManifestations .findConstruction( c );
        if ( m == null )
            return null;
        Manifestation made = (Manifestation) mManifestedNow .get( m );
        if ( made != null )
        	return made;
        if ( m .isUnnecessary() )  { // just manifested, not added yet
        	mManifestedNow .put( m, m );
        	plan( new ManifestConstruction( c, m, true ) );
        }
        else {
        	// already manifested, just make sure it shows
        	if ( m .getRenderedObject() == null )
        		plan( new RenderManifestation( m, true ) );
        }
        return m;
    }
    
//    /**
//     * This is adapting the current notion of Constructions to the future notion
//     * of Realizer.
//     * 
//     * @param c
//     * @return
//     */
//    private Manifestation realizeConstruction( Construction c )
//    {
//        if ( c instanceof Point )
//            return realizer .realizeBall( ((Point) c) .getLocation() );
//        if ( c instanceof Segment )
//        {
//            Segment segment = (Segment) c;
//            return realizer .realizeStrut( segment .getCenter(), segment .getLength(), segment .getAxis() );
//        }
//        return null;
//    }
    
    protected Manifestation unmanifestConstruction( Construction c )
    {
        Manifestation m = mManifestations .removeConstruction( c );
        if ( m == null )
            return null;
        plan( new ManifestConstruction( c, m, false ) );
        return m;
    }
    
    protected void showManifestation( Manifestation m )
    {
        plan( new RenderManifestation( m, true ) );
    }
    
    protected void hideManifestation( Manifestation m )
    {
        plan( new RenderManifestation( m, false ) );
    }
    

    private class ManifestConstruction implements SideEffect
    {
        private final Manifestation mManifestation;
        
        private final Construction mConstruction;

        private final boolean mShowing;
        
        public ManifestConstruction( Construction construction, Manifestation manifestation, boolean showing )
        {
            mConstruction = construction;
            mManifestation = manifestation;
            mShowing = showing;
        }

        public void redo()
        {
            if ( mShowing ) {
                if ( mManifestation .isUnnecessary() )
                    mManifestations .add(  mManifestation );
                // note the asymmetry... we want to unhide when adding
                mManifestations .show( mManifestation ); // TODO make this more immediate, call renderer here
                mManifestation .addConstruction( mConstruction );
            }
            else {
                mManifestation .removeConstruction( mConstruction );
                if ( mManifestation .isUnnecessary() ) {
                    mManifestations .hide( mManifestation ); // TODO make this more immediate, call renderer here
                    mManifestations .remove(  mManifestation );
                }
            }
        }

        public void undo()
        {
            if ( mShowing ) {
                mManifestation .removeConstruction( mConstruction );
                if ( mManifestation .isUnnecessary() ) {
                    mManifestations .hide( mManifestation ); // TODO make this more immediate, call renderer here
                    mManifestations .remove(  mManifestation );
                }
            }
            else {
                if ( mManifestation .isUnnecessary() )
                    mManifestations .add(  mManifestation );
                // note the asymmetry... we want to unhide when adding
                mManifestations .show( mManifestation ); // TODO make this more immediate, call renderer here
                mManifestation .addConstruction( mConstruction );
            }
        }
    }
    
    private class RenderManifestation implements SideEffect
    {
        private final Manifestation mManifestation;

        private final boolean mShowing;
        
        public RenderManifestation( Manifestation manifestation, boolean showing )
        {
            mManifestation = manifestation;
            mShowing = showing;
        }

        public void redo()
        {
        	mManifestation .setHidden( ! mShowing );
            if ( mShowing )
                mManifestations .show( mManifestation ); // TODO make this more immediate, call renderer here
            else
                mManifestations .hide( mManifestation );
        }

        public void undo()
        {
        	mManifestation .setHidden( mShowing );
            if ( mShowing )
                mManifestations .hide( mManifestation );
            else
                mManifestations .show( mManifestation );
        }
    }
}

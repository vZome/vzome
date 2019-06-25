
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.Map;

import org.w3c.dom.Element;

import com.vzome.core.commands.AttributeMap;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.Segment;
import com.vzome.core.math.DomUtils;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

public class SelectManifestation extends ChangeSelection
{
    private Manifestation mManifestation;
    
    private Construction construction;  // just for save
    
    private final RealizedModel mRealized;
    
    private boolean mReplace;

    public SelectManifestation( EditorModel editor )
    {
        super( editor .getSelection() );
        this.mRealized = editor .getRealizedModel();
    }
    
    @Override
    protected boolean groupingAware()
    {
        return true;
    }

    /**
     * Used by CommandEdit.
     * @param editor
     * @param m
     */
    public SelectManifestation( EditorModel editor, Manifestation m )
    {
        this( editor );
        this .mManifestation = m;
        if ( this.mManifestation != null )
        {
            // must record the construction for save, because if this gets undone, there's no
            //  guarantee that the manifestation will have any constructions!
            construction = this.mManifestation .getFirstConstruction();
        }
    }

    public void configure( Map<String, Object> props )
    {
        String mode = (String) props .get( "mode" );
        this.mReplace = "replace" .equals( mode );
        this.mManifestation = (Manifestation) props .get( "picked" );
        if ( this.mManifestation != null )
        {
            // must record the construction for save, because if this gets undone, there's no
            //  guarantee that the manifestation will have any constructions!
            construction = this.mManifestation .getFirstConstruction();
        }
    }
    
    @Override
    public void perform()
    {
        if ( mReplace ) {
            for (Manifestation man : mSelection) {
                unselect( man, true );
            }
            select( mManifestation );
        }
        else if ( mSelection .manifestationSelected( mManifestation ) )
            unselect( mManifestation );
        else
            select( mManifestation );
        redo();
    }

    @Override
    protected void getXmlAttributes( Element result )
    {
        if ( construction instanceof Point )
            XmlSaveFormat .serializePoint( result, "point", (Point) construction );
        else if ( construction instanceof Segment )
            XmlSaveFormat .serializeSegment( result, "startSegment", "endSegment", (Segment) construction );
        else if ( construction instanceof Polygon )
            XmlSaveFormat .serializePolygon( result, "polygonVertex", (Polygon) construction );

        if ( mReplace )
            DomUtils .addAttribute( result, "replace", "true" );
    }

    @Override
    public void setXmlAttributes( Element xml, XmlSaveFormat format )
    {
        if ( format .rationalVectors() )
        {
            construction = format .parsePoint( xml, "point" );
            if ( construction == null )
                construction = format .parseSegment( xml, "startSegment", "endSegment" );
            if ( construction == null )
            {
                Element kid = DomUtils .getFirstChildElement( xml, "polygon" );
                if ( kid != null ) // the interim 4.0.0 format (3.0 alpha 5 and before?)
                    construction = format .parsePolygon( kid, "vertex" );
                else
                    construction = format .parsePolygon( xml, "polygonVertex" );
            }
        }
        else
        {
            AttributeMap attrs = format .loadCommandAttributes( xml );
            construction = (Construction) attrs .get( "manifestation" );
            Boolean replaceVal = (Boolean) attrs .get( "replace" );
            if ( replaceVal != null && replaceVal )
                mReplace = true;
        }
        mManifestation = mRealized .getManifestation( construction );
        
        // accommodate old bug that did not distinguish polygon vertex orders.
        //  This is acceptable on the basis that only a bug would result in a null manifestation
        if ( mManifestation == null &&  format .rationalVectors() &&  construction instanceof Polygon )
        {
            construction = format .parsePolygonReversed( xml, "polygonVertex" );
            mManifestation = mRealized .getManifestation( construction );
            if ( mManifestation != null )
                logBugAccommodation( "reverse-oriented polygon" );
        }
    }

    @Override
    protected String getXmlElementName()
    {
        return "SelectManifestation";
    }
}

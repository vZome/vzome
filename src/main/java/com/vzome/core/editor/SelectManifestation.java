
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Element;

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
    
    private RealizedModel mRealized;
    
    private boolean mReplace;

    public SelectManifestation( Manifestation m, boolean replace, Selection selection, RealizedModel realized, boolean groupInSelection )
    {
        super( selection, groupInSelection );
        
        mRealized = realized;
        mManifestation = m;
        mReplace = replace;
        setDirty();
        if ( m != null )
        {
            // must record the construction for save, because if this gets undone, there's no
            //  guarantee that the manifestation will have any constructions!
            construction = (Construction) mManifestation .getConstructions() .next();
        }
    }
    
    public void perform()
    {
        if ( mReplace ) {
            for ( Iterator all = mSelection .iterator(); all .hasNext(); )
                unselect( (Manifestation) all .next(), true );
            select( mManifestation );
        }
        else if ( mSelection .manifestationSelected( mManifestation ) )
            unselect( mManifestation );
        else
            select( mManifestation );
        redo();
    }

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
            Map attrs = format .loadCommandAttributes( xml, true );
            construction = (Construction) attrs .get( "manifestation" );
            Boolean replaceVal = (Boolean) attrs .get( "replace" );
            if ( replaceVal != null && replaceVal .booleanValue() )
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

    protected String getXmlElementName()
    {
        return "SelectManifestation";
    }
}

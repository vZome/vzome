
//(c) Copyright 2011, Scott Vorthmann.

package com.vzome.core.editor;

import java.util.Iterator;

import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.math.DomUtils;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.model.Strut;

public class SelectSimilarSizeStruts extends ChangeSelection
{
    private Direction orbit;
    private AlgebraicNumber length;
    private RealizedModel model;
    private SymmetrySystem symmetry;

    public SelectSimilarSizeStruts( SymmetrySystem symmetry, Direction orbit, AlgebraicNumber length,
            Selection selection, RealizedModel model )
    {
        super( selection, false );
        this.symmetry = symmetry;
        this .model = model;
        this .orbit = orbit;
        this .length = length;
    }

    public void perform() throws Failure
    {
        for ( Iterator ms = model .getAllManifestations(); ms .hasNext(); ) {
            Manifestation man = (Manifestation) ms .next();
            if ( man .getRenderedObject() == null )
                continue;  // hidden!
            if ( man instanceof Strut ) {
                Strut strut = (Strut) man;
                AlgebraicVector offset = strut .getOffset();
                Axis zone = symmetry .getAxis( offset );
                Direction orbit = zone .getOrbit();
                if ( orbit != this .orbit )
                    continue;
                AlgebraicNumber length = zone .getLength( offset );
                if ( this .length .equals( length ) )
                    select( strut );
            }
        }
        super .perform();
    }

    protected String getXmlElementName()
    {
        return "SelectSimilarSize";
    }

    protected void getXmlAttributes( Element element )
    {
        if ( symmetry != null )
            DomUtils .addAttribute( element, "symmetry", symmetry .getName() );
        if ( orbit != null )
        	DomUtils .addAttribute( element, "orbit", orbit .getName() );
        if ( length != null )
            XmlSaveFormat .serializeNumber( element, "length", length );
    }

    protected void setXmlAttributes( Element xml, XmlSaveFormat format )
            throws Failure
    {
        length = format .parseNumber( xml, "length" );
        orbit = symmetry .getOrbits() .getDirection( xml .getAttribute( "orbit" ) );
    }
}

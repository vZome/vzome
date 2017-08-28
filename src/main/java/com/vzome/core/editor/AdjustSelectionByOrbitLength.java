
//(c) Copyright 2011, Scott Vorthmann.

package com.vzome.core.editor;


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
    private final RealizedModel model;
    private final SymmetrySystem symmetry;

    public SelectSimilarSizeStruts( SymmetrySystem symmetry, Direction orbit, AlgebraicNumber length,
            Selection selection, RealizedModel model )
    {
        super( selection, false );
        this.symmetry = symmetry;
        this .model = model;
        this .orbit = orbit;
        this .length = length;
    }

    /*
    Although this class is named SelectSimilarSizeStruts, it does not do exactly what its name implies.
    Specifically, it will only select struts of the same length AND ORBIT within the given symmetry.
    For example, if short blue struts radiate from the origin with icoshedral symmetry,
    then we switch to octahedral symmetry, some of the struts will then be on black axes
    according to the octahedral symmetry system. In that case, when one of the blue struts are selected
    as the reference for this command, only blue struts of the same length will be selected.
    None of the black struts will be selected even though they are the same length,
    since the black struts are no longer in a blue orbit.
    This behavior is intentional. The struts to be selected are dependent on the selected symmetry.
    */
    @Override
    public void perform() throws Failure
    {
        for (Manifestation man : model) {
            if ( man .getRenderedObject() == null )
                continue;  // hidden!
            if ( man instanceof Strut ) {
                Strut strut = (Strut) man;
                AlgebraicVector offset = strut .getOffset();
                Axis zone = symmetry .getAxis( offset );
                Direction zoneOrbit = zone .getOrbit();
                if ( zoneOrbit != this .orbit )
                    continue;
                AlgebraicNumber zoneLength = zone .getLength( offset );
                if ( this .length .equals( zoneLength ) )
                    select( strut );
            }
        }
        super .perform();
    }

    @Override
    protected String getXmlElementName()
    {
        return "SelectSimilarSize";
    }

    @Override
    protected void getXmlAttributes( Element element )
    {
        if ( symmetry != null )
            DomUtils .addAttribute( element, "symmetry", symmetry .getName() );
        if ( orbit != null )
        	DomUtils .addAttribute( element, "orbit", orbit .getName() );
        if ( length != null )
            XmlSaveFormat .serializeNumber( element, "length", length );
    }

    @Override
    protected void setXmlAttributes( Element xml, XmlSaveFormat format )
            throws Failure
    {
        length = format .parseNumber( xml, "length" );
        orbit = symmetry .getOrbits() .getDirection( xml .getAttribute( "orbit" ) );
    }
}

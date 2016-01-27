
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;


import org.w3c.dom.Element;

import com.vzome.core.commands.Command;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.SymmetryTransformation;
import com.vzome.core.construction.Transformation;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.model.Strut;

public class SymmetryTool extends TransformationTool
{
    protected Symmetry symmetry;
    
    public SymmetryTool( String name, Symmetry symmetry, Selection selection, RealizedModel realized, Tool.Registry tools, Point originPoint )
    {
        super( name, selection, realized, tools, originPoint );
        this.symmetry = symmetry;
    }
    
    public String getDefaultName( String baseName )
    {
        int nextDot = baseName .indexOf( "." );
        baseName = baseName .substring( 0, nextDot );
        return baseName + " around origin";
    }

    private boolean isTetrahedral()
    {
        return "tetrahedral" .equals( getCategory() );
    }

    public void perform() throws Command.Failure
    {
        boolean isTetrahedral = isTetrahedral();
        boolean isIcosahedral = "icosahedral" .equals( symmetry .getName() );
        Point center = null;
        Strut axis = null;
        boolean correct = true;
        if ( ! isAutomatic() )
            for (Manifestation man : mSelection) {
                unselect( man );
                if ( man instanceof Connector )
                {
                    if ( center != null )
                        throw new Command.Failure( "no unique symmetry center selected" );
                    center = (Point) ((Connector) man) .getConstructions() .next();
                }
                else if ( man instanceof Strut )
                {
                    if ( axis != null )
                        correct = false;
                    else
                        axis = (Strut) man;
                }
        }
        if ( center == null )
            center = this.originPoint;
        
        if ( isTetrahedral )
        {
            int[] closure = symmetry .subgroup( Symmetry.TETRAHEDRAL );
            if ( isIcosahedral && axis != null )
            {
                if ( !correct )
                    throw new Command.Failure( "no unique alignment strut selected." );
                // align the tetrahedral symmetry with this yellow, blue, or green strut
                IcosahedralSymmetry icosa = (IcosahedralSymmetry) symmetry;
                Axis zone = icosa .getAxis( axis .getOffset() );
                closure = icosa .subgroup( Symmetry.TETRAHEDRAL, zone );
                if ( closure == null )
                    throw new Command.Failure( "selected alignment strut is not a tetrahedral axis." );
            }
            int order = closure .length;
            this .transforms = new Transformation[ order-1 ];
            for ( int i = 0; i < order-1; i++ )
                transforms[ i ] = new SymmetryTransformation( symmetry, closure[ i+1 ], center );
        }
        else
        {
            int order = symmetry .getChiralOrder();
            this .transforms = new Transformation[ order-1 ];
            for ( int i = 0; i < order-1; i++ )
                transforms[ i ] = new SymmetryTransformation( symmetry, i+1, center );
        }
    
        defineTool();
    }

    protected String getXmlElementName()
    {
        return "SymmetryTool";
    }

    protected void getXmlAttributes( Element element )
    {
        element .setAttribute( "symmetry", symmetry .getName() );
        super .getXmlAttributes( element );
    }

    protected void setXmlAttributes( Element element, XmlSaveFormat format ) throws Failure
    {
        String symmName = element .getAttribute( "symmetry" );
        this.symmetry = format .parseSymmetry( symmName );
        super .setXmlAttributes( element, format );
    }

    public String getCategory()
    {
        String subgroup = getName();
        int nextDot = subgroup .indexOf( "." );
        subgroup = subgroup .substring( 0, nextDot );
        return subgroup;
    }
}

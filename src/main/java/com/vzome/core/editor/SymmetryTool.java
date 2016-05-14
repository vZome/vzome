
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;


import org.w3c.dom.Element;

import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
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
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((symmetry == null) ? 0 : symmetry.hashCode());
		return result;
	}

	@Override
	public boolean equals( Object that )
	{
		if (this == that) {
			return true;
		}
		if (!super.equals(that)) {
			return false;
		}
		if (getClass() != that.getClass()) {
			return false;
		}
		SymmetryTool other = (SymmetryTool) that;
		if (symmetry == null) {
			if (other.symmetry != null) {
				return false;
			}
		} else if (!symmetry.equals(other.symmetry)) {
			return false;
		}
		return true;
	}

	@Override
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

	@Override
    protected String checkSelection( boolean prepareTool )
    {
        boolean isTetrahedral = isTetrahedral();
        boolean isIcosahedral = "icosahedral" .equals( symmetry .getName() );
        Point center = null;
        Strut axis = null;
        boolean correct = true;
        for (Manifestation man : mSelection) {
        	if ( prepareTool ) // not just validating the selection
        		unselect( man );
        	if ( man instanceof Connector )
        	{
        		if ( center != null )
        			return "No unique symmetry center selected";
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
        if ( center == null ) {
        	if ( prepareTool ) // after validation, or when loading from a file
        		center = originPoint;
        	else // just validating the selection, not really creating a tool
        		return "No symmetry center selected";
        }
        
        if ( isTetrahedral )
        {
            int[] closure = symmetry .subgroup( Symmetry.TETRAHEDRAL );
            if ( isIcosahedral && axis != null )
            {
                if ( !correct )
                    return "no unique alignment strut selected.";
                // align the tetrahedral symmetry with this yellow, blue, or green strut
                IcosahedralSymmetry icosa = (IcosahedralSymmetry) symmetry;
                Axis zone = icosa .getAxis( axis .getOffset() );
                closure = icosa .subgroup( Symmetry.TETRAHEDRAL, zone );
                if ( closure == null )
                    return "selected alignment strut is not a tetrahedral axis.";
            }
            if ( prepareTool ) {
                int order = closure .length;
                this .transforms = new Transformation[ order-1 ];
                for ( int i = 0; i < order-1; i++ )
                    transforms[ i ] = new SymmetryTransformation( symmetry, closure[ i+1 ], center );
            }
        }
        else if ( prepareTool ) {
        	int order = symmetry .getChiralOrder();
        	this .transforms = new Transformation[ order-1 ];
        	for ( int i = 0; i < order-1; i++ )
        		transforms[ i ] = new SymmetryTransformation( symmetry, i+1, center );
        }
        else if ( isIcosahedral ) {
        	// just validating the selection
        	if ( axis != null )
        		return "No struts";
        }
        return null;
    }

    @Override
    protected String getXmlElementName()
    {
        return "SymmetryTool";
    }

    @Override
    protected void getXmlAttributes( Element element )
    {
        element .setAttribute( "symmetry", symmetry .getName() );
        super .getXmlAttributes( element );
    }

    @Override
    protected void setXmlAttributes( Element element, XmlSaveFormat format ) throws Failure
    {
        String symmName = element .getAttribute( "symmetry" );
        this.symmetry = format .parseSymmetry( symmName );
        super .setXmlAttributes( element, format );
    }

    @Override
    public String getCategory()
    {
        String subgroup = getName();
        int nextDot = subgroup .indexOf( "." );
        subgroup = subgroup .substring( 0, nextDot );
        return subgroup;
    }
}

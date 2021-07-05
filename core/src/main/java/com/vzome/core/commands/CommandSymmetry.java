

package com.vzome.core.commands;


import com.vzome.core.construction.Construction;
import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.ConstructionList;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.SymmetryTransformation;
import com.vzome.core.construction.Transformation;
import com.vzome.core.math.symmetry.Symmetry;

/**
 * @author Scott Vorthmann
 */
public class CommandSymmetry extends CommandTransform
{
    protected Symmetry mSymmetry;

    public CommandSymmetry()
    {
        this( null );
    }
    
    public CommandSymmetry( Symmetry symmetry )
    {
        mSymmetry = symmetry;
    }

    @Override
    public Object[][] getAttributeSignature()
    {
        return GROUP_ATTR_SIGNATURE;
    }
    
    protected Point setSymmetry( AttributeMap attributes )
    {
        if ( mSymmetry == null )
            mSymmetry = (Symmetry) attributes .get( SYMMETRY_GROUP_ATTR_NAME );
        else if ( ! attributes .containsKey( SYMMETRY_GROUP_ATTR_NAME ) ) // make sure it gets recorded in a save
            attributes .put( SYMMETRY_GROUP_ATTR_NAME, mSymmetry );
        if ( mSymmetry == null )
//            mSymmetry = IcosahedralSymmetry .INSTANCE;
            throw new IllegalStateException( "null symmetry no longer supported" );
        Point center = (Point) attributes .get( SYMMETRY_CENTER_ATTR_NAME );
        // TODO deal with null center?
        return center;
    }
    
    @Override
    public void setFixedAttributes( AttributeMap attributes, XmlSaveFormat format )
    {
        if ( ! attributes .containsKey( SYMMETRY_GROUP_ATTR_NAME ) )
        {
            Symmetry icosahedralSymmetry = ((XmlSymmetryFormat) format) .parseSymmetry( "icosahedral" );
            attributes .put( SYMMETRY_GROUP_ATTR_NAME, icosahedralSymmetry );
        }
        super .setFixedAttributes( attributes, format );
    }

    @Override
    public ConstructionList apply( final ConstructionList parameters, AttributeMap attributes, final ConstructionChanges effects ) throws Failure
    {
        Point center = setSymmetry( attributes );
        
        final Construction[] params = parameters .getConstructions();
        ConstructionList output = new ConstructionList();
        for (Construction param : params) {
            output.addConstruction(param);
        }
        
        for ( int i = 1; i < mSymmetry .getChiralOrder(); i++ )
        {
            Transformation transform = new SymmetryTransformation( mSymmetry, i, center );
            output .addAll( transform( params, transform, effects ) ); 
        }
        return output;
    }
}

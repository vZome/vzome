package com.vzome.core.kinds;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import com.vzome.api.Tool.Factory;
import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.SnubDodecField;
import com.vzome.core.commands.Command;
import com.vzome.core.commands.CommandTauDivision;
import com.vzome.core.commands.CommandUniformH4Polytope;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.math.symmetry.QuaternionicSymmetry;
import com.vzome.core.math.symmetry.WythoffConstruction.Listener;
import com.vzome.core.tools.AxialStretchTool;
import com.vzome.core.tools.IcosahedralToolFactory;

/**
 * Everything here is stateless, or at worst, a cache (like Shapes).
 * An instance of this can be shared by many DocumentModels.
 * This is why it does not have tool factories, though it does
 * dictate what tool factories will be present.
 * 
 * @author vorth
 *
 */
public class SnubDodecFieldApplication extends DefaultFieldApplication
{
    private final IcosahedralSymmetryPerspective icosahedralPerspective;

	public SnubDodecFieldApplication()
	{
        super( new SnubDodecField() );
        
        AlgebraicField field = this.getField();
        IcosahedralSymmetry icosaSymm = new IcosahedralSymmetry( field, "solid connectors" ) 
        {
            @Override
            protected void createOtherOrbits()
            {
                super .createOtherOrbits();
                /*
                  PENTAGON
                  4 + phi*-4 + xi*0 + phi*xi*0 + xi^2*-2 + phi*xi^2*2, -4 + phi*0 + xi*0 + phi*xi*0 + xi^2*2 + phi*xi^2*0, 0 + phi*0 + xi*0 + phi*xi*0 + xi^2*0 + phi*xi^2*2
                  4 -4 0 0 -2 2 -4 0 0 0 2 0 0 0 0 0 0 2
                  (2,-2,0,0,-4,4) (0,2,0,0,0,-4) (2,0,0,0,0,0)
                
                  TRIANGLE
                  0 + phi*-4 + xi*-2 + phi*xi*0 + xi^2*0 + phi*xi^2*2, -4 + phi*4 + xi*0 + phi*xi*-2 + xi^2*2 + phi*xi^2*-2, -4 + phi*0 + xi*-2 + phi*xi*-2 + xi^2*2 + phi*xi^2*0
                  0 -4 -2 0 0 2 -4 4 0 -2 2 -2 -4 0 -2 -2 2 0
                  (2,0,0,-2,-4,0) (-2,2,-2,0,4,-4) (0,2,-2,-2,0,-4)
                
                  DIAGONAL
                  8 + phi*0 + xi*0 + phi*xi*4 + xi^2*-4 + phi*xi^2*0, 0 + phi*-4 + xi*0 + phi*xi*0 + xi^2*0 + phi*xi^2*0, 0 + phi*0 + xi*0 + phi*xi*0 + xi^2*0 + phi*xi^2*0
                  8 0 0 4 -4 0 0 -4 0 0 0 0 0 0 0 0 0 0
                  (0,-4,4,0,0,8) (0,0,0,0,-4,0) (0,0,0,0,0,0)

                 */      
                    AlgebraicVector vSnubPentagon = mField.createIntegerVector(new int[][]{ { 4,-4, 0, 0,-2, 2},  {-4, 0, 0, 0, 2, 0},  { 0, 0, 0, 0, 0, 2} } );
                    AlgebraicVector vSnubTriangle = mField.createIntegerVector(new int[][]{ { 0,-4,-2, 0, 0, 2},  {-4, 4, 0,-2, 2,-2},  {-4, 0,-2,-2, 2, 0} } );
                    AlgebraicVector vSnubDiagonal = mField.createIntegerVector(new int[][]{ { 8, 0, 0, 4,-4, 0},  { 0,-4, 0, 0, 0, 0},  { 0, 0, 0, 0, 0, 0} } );
                    AlgebraicNumber scale = mField .createPower( -3 );

                    createZoneOrbit( "snubPentagon", 0, NO_ROTATION, vSnubPentagon, false, false, scale ) .withCorrection();
                    createZoneOrbit( "snubTriangle", 0, NO_ROTATION, vSnubTriangle, false, false, scale ) .withCorrection();
                    createZoneOrbit( "snubDiagonal", 0, NO_ROTATION, vSnubDiagonal, false, false, scale ) .withCorrection();
                }
            };
            this.icosahedralPerspective = new IcosahedralSymmetryPerspective( icosaSymm ); 
    }

	@Override
	public String getName()
	{
		return this .getField() .getName();
	}

	@Override
	public Collection<SymmetryPerspective> getSymmetryPerspectives()
	{
		return Arrays.asList( this .icosahedralPerspective, super .getDefaultSymmetryPerspective() );
	}

	@Override
	public SymmetryPerspective getDefaultSymmetryPerspective()
	{
		return this .icosahedralPerspective;
	}

	@Override
	public SymmetryPerspective getSymmetryPerspective( String symmName )
	{
		switch ( symmName ) {

		case "icosahedral":
			return this .icosahedralPerspective;

		default:
		    return super .getSymmetryPerspective( symmName );
		}
	}

    @Override
    public QuaternionicSymmetry getQuaternionSymmetry( String name )
    {
        return icosahedralPerspective .getQuaternionSymmetry( name );
    }

    @Override
    public void registerToolFactories( Map<String, Factory> toolFactories, ToolsModel tools )
    {
        // register the default tool factories
        super.registerToolFactories( toolFactories, tools );
        
        // add any tools that are unique for this field
        IcosahedralSymmetry symm = icosahedralPerspective .getSymmetry();
        // symm matters for this one, since it is final in the tool
        toolFactories .put( "AxialStretchTool", new AxialStretchTool.Factory( tools, symm, false, false, false ) );

        // this one has to replace the same-named factory in the base class
        toolFactories .put( "SymmetryTool", new IcosahedralToolFactory( tools, symm ) );
    }

    private CommandUniformH4Polytope h4Builder = null;
    
	@Override
    public void constructPolytope( String groupName, int index, int edgesToRender, AlgebraicNumber[] edgeScales, Listener listener )
    {
        switch ( groupName ) {

        case "H4":
            if ( this .h4Builder == null ) {
                QuaternionicSymmetry qsymm = new QuaternionicSymmetry( "H_4", "com/vzome/core/math/symmetry/H4roots.vef", this .getField() );
                this .h4Builder = new CommandUniformH4Polytope( this .getField(), qsymm, 0 );
            }
            this .h4Builder .generate( index, edgesToRender, edgeScales, listener );
            break;

        default:
            super .constructPolytope( groupName, index, edgesToRender, edgeScales, listener );
            break;
        }
    }

    private final Command cmdTauDivide = new CommandTauDivision();

    @Override
    public Command getLegacyCommand( String action )
    {
        switch ( action ) {
        case "tauDivide":
            return cmdTauDivide;
            
        default:
            return super.getLegacyCommand( action );
        }
    }
}

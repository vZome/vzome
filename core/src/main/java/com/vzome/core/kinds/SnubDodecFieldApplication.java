package com.vzome.core.kinds;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import com.vzome.api.Tool.Factory;
import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command;
import com.vzome.core.commands.CommandTauDivision;
import com.vzome.core.commands.CommandUniformH4Polytope;
import com.vzome.core.editor.SymmetryPerspective;
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

	public SnubDodecFieldApplication( AlgebraicField field )
	{
        super( field );
        
        IcosahedralSymmetry icosaSymm = new IcosahedralSymmetry( field ) 
        {
            @Override
            protected void createOtherOrbits()
            {
                super .createOtherOrbits();
                /*
                  PENTAGON EDGE
                  4 + phi*-4 + xi*0 + phi*xi*0 + xi^2*-2 + phi*xi^2*2, -4 + phi*0 + xi*0 + phi*xi*0 + xi^2*2 + phi*xi^2*0, 0 + phi*0 + xi*0 + phi*xi*0 + xi^2*0 + phi*xi^2*2
                  4 -4 0 0 -2 2 -4 0 0 0 2 0 0 0 0 0 0 2
                  (2,-2,0,0,-4,4) (0,2,0,0,0,-4) (2,0,0,0,0,0)
                
                  TRIANGLE EDGE
                  0 + phi*-4 + xi*-2 + phi*xi*0 + xi^2*0 + phi*xi^2*2, -4 + phi*4 + xi*0 + phi*xi*-2 + xi^2*2 + phi*xi^2*-2, -4 + phi*0 + xi*-2 + phi*xi*-2 + xi^2*2 + phi*xi^2*0
                  0 -4 -2 0 0 2 -4 4 0 -2 2 -2 -4 0 -2 -2 2 0
                  (2,0,0,-2,-4,0) (-2,2,-2,0,4,-4) (0,2,-2,-2,0,-4)
                
                  DIAGONAL EDGE
                  8 + phi*0 + xi*0 + phi*xi*4 + xi^2*-4 + phi*xi^2*0, 0 + phi*-4 + xi*0 + phi*xi*0 + xi^2*0 + phi*xi^2*0, 0 + phi*0 + xi*0 + phi*xi*0 + xi^2*0 + phi*xi^2*0
                  8 0 0 4 -4 0 0 -4 0 0 0 0 0 0 0 0 0 0
                  (0,-4,4,0,0,8) (0,0,0,0,-4,0) (0,0,0,0,0,0)

                  SNUB FACE NORMAL
                  -1 +xi -phi*xi +xi^2, 1, 1 -xi +2*phi*xi +phi*xi^2
                  -1 0 1 -1 1 0 1 0 0 0 0 0 1 0 -1 2 0 1
                  (0,1,-1,1,0,-1) (0,0,0,0,0,1) (1,0,2,-1,0,1)

                  SNUB VERTEX
                  1, 1 -xi +phi*xi -xi^2, 1 +phi*xi -xi^2 +phi*xi^2
                  1 0 0 0 0 0 1 0 -1 1 -1 0 1 0 0 1 -1 1
                  (0,0,0,0,0,1) (0,-1,1,-1,0,1) (1,-1,1,0,0,1)

                 */      
                    AlgebraicVector vSnubPentagon = mField.createIntegerVector(new int[][]{ { 4,-4, 0, 0,-2, 2},  {-4, 0, 0, 0, 2, 0},  { 0, 0, 0, 0, 0, 2} } );
                    AlgebraicVector vSnubTriangle = mField.createIntegerVector(new int[][]{ { 0,-4,-2, 0, 0, 2},  {-4, 4, 0,-2, 2,-2},  {-4, 0,-2,-2, 2, 0} } );
                    AlgebraicVector vSnubDiagonal = mField.createIntegerVector(new int[][]{ { 8, 0, 0, 4,-4, 0},  { 0,-4, 0, 0, 0, 0},  { 0, 0, 0, 0, 0, 0} } );
                    AlgebraicVector vSnubFaceNorm = mField.createIntegerVector(new int[][]{ {-1, 0, 1,-1, 1, 0},  { 1, 0, 0, 0, 0, 0},  { 1, 0,-1, 2, 0, 1} } );
                    AlgebraicVector vSnubVertex   = mField.createIntegerVector(new int[][]{ { 1, 0, 0, 0, 0, 0},  { 1, 0,-1, 1,-1, 0},  { 1, 0, 0, 1,-1, 1} } );
                    AlgebraicNumber scale = mField .createPower( -3 );

                    AlgebraicNumber scaleFaceNorm, scaleVertex = mField.one();
                    // These two vector specific scalars allow the unit strut lengths 
                    // to generate the same sized snubDodec as one generated with the 
                    // YELLOW strut.
                    scaleFaceNorm = mField .createAlgebraicNumber( new int[] { -3, 2, 2, -1, 5, -3 } ) .reciprocal();
                    scaleVertex = mField .createAlgebraicNumber( new int[] { -3, 2, 7, -4, 2, -1 }, 3 ) .reciprocal();
                    
//                    // These two vector specific scalars allow the unit strut lengths 
//                    // to generate the same sized snubDodec as one generated with the 
//                    // RED strut.
//                    scaleFaceNorm = mField .createAlgebraicNumber(new BigRational[] {
//                            new BigRational(-54, 5),
//                            new BigRational( 33, 5),
//                            new BigRational(-33, 5),
//                            new BigRational( 21, 5),
//                            new BigRational( 21, 5),
//                            new BigRational(-12, 5)
//                        }).reciprocal();
//                    scaleVertex = mField .createAlgebraicNumber(new BigRational[] {
//                            new BigRational( -4, 5),
//                            new BigRational(  3, 5),
//                            new BigRational( -4, 5),
//                            new BigRational(  3, 5),
//                            new BigRational(-11, 5),
//                            new BigRational(  7, 5)
//                        }).reciprocal();
                    
                    // DJH - I don't think it really matters if we use the YELLOW or the RED 
                    // scaling above (or neither) but I found that using the 
                    // YELLOW ones end up with only integer multipliers 
                    // for terms of AlgebraicNumbers in the unit strut lengths, whereas using the 
                    // RED ones end up mostly with denominators of 31 and 93.
                    // Therefore, I'm going to go with the YELLOW scalars for now, 
                    // but leave the RED values here as a comment 
                    // in case we want it some day since I already did the math.
                    
                    // actual colors are assigned in /core/src/main/resources/com/vzome/core/editor/defaultPrefs.properties
                    createZoneOrbit( "snubPentagon",   0, NO_ROTATION, vSnubPentagon, false, false, scale ) .withCorrection();
                    createZoneOrbit( "snubTriangle",   0, NO_ROTATION, vSnubTriangle, false, false, scale ) .withCorrection();
                    createZoneOrbit( "snubDiagonal",   0, NO_ROTATION, vSnubDiagonal, false, false, scale ) .withCorrection();
                    createZoneOrbit( "snubFaceNormal", 0, NO_ROTATION, vSnubFaceNorm, false, false, scale.times(scaleFaceNorm) ) .withCorrection();
                    // snubVertex is a standard direction since it's the easiest one to use for making a SnubDodec
                    createZoneOrbit( "snubVertex",     0, NO_ROTATION, vSnubVertex,   true,  false, scale.times(scaleVertex)   ) .withCorrection();
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
    public String getLabel()
    {
        return "Snub Dodecahedron";
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

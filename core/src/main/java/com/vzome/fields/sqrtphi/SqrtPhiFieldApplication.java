package com.vzome.fields.sqrtphi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.vzome.api.Tool;
import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command;
import com.vzome.core.commands.CommandAxialSymmetry;
import com.vzome.core.commands.CommandQuaternionSymmetry;
import com.vzome.core.commands.CommandSymmetry;
import com.vzome.core.commands.CommandTetrahedralSymmetry;
import com.vzome.core.commands.CommandUniformH4Polytope;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.kinds.DefaultFieldApplication;
import com.vzome.core.kinds.OctahedralSymmetryPerspective;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.math.symmetry.OctahedralSymmetry;
import com.vzome.core.math.symmetry.QuaternionicSymmetry;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.math.symmetry.WythoffConstruction.Listener;
import com.vzome.core.render.Shapes;
import com.vzome.core.tools.AxialStretchTool;
import com.vzome.core.tools.AxialSymmetryToolFactory;
import com.vzome.core.tools.IcosahedralToolFactory;
import com.vzome.core.tools.InversionTool;
import com.vzome.core.tools.LinearMapTool;
import com.vzome.core.tools.MirrorTool;
import com.vzome.core.tools.RotationTool;
import com.vzome.core.tools.ScalingTool;
import com.vzome.core.tools.SymmetryTool;
import com.vzome.core.tools.TetrahedralToolFactory;
import com.vzome.core.tools.TranslationTool;
import com.vzome.core.viewing.AbstractShapes;
import com.vzome.core.viewing.ExportedVEFShapes;
import com.vzome.core.viewing.OctahedralShapes;

/**
 * Everything here is stateless, or at worst, a cache (like Shapes).
 * An instance of this can be shared by many DocumentModels.
 * This is why it does not have tool factories, though it does
 * dictate what tool factories will be present.
 * 
 * @author vorth
 *
 */
public class SqrtPhiFieldApplication extends DefaultFieldApplication
{
	public SqrtPhiFieldApplication()
	{
		super( new SqrtPhiField() );
		AlgebraicField field = this .getField();

		OctahedralSymmetryPerspective octahedralPerspective = (OctahedralSymmetryPerspective) super .getDefaultSymmetryPerspective();
		OctahedralSymmetry symm = (OctahedralSymmetry) octahedralPerspective .getSymmetry();
		
		AlgebraicNumber scale = field .createPower( 6 );
		symm .getDirection( "blue" ) .setUnitLength( scale );
		symm .getDirection( "green" ) .setUnitLength( scale );
		symm .getDirection( "yellow" ) .setUnitLength( scale );
        
		AlgebraicNumber x = field .createAlgebraicNumber( new int[]{ 0, -1, 0, 0 } );
		AlgebraicNumber y = field .createAlgebraicNumber( new int[]{ -1, 0, 0, 0 } );
		AlgebraicNumber z = field .zero();
		AlgebraicNumber unitLength = field .createPower( 4 );
		AlgebraicVector norm = new AlgebraicVector( x, y, z );
		symm .createZoneOrbit( "slate", 0, Symmetry .NO_ROTATION, norm, true, false, unitLength );

	    x = field .createAlgebraicNumber( new int[]{ 0, 1, 0, -1 } );
		y = field .one();
		z = field .one();
		norm = new AlgebraicVector( x, y, z );
		symm .createZoneOrbit( "mauve", 0, Symmetry .NO_ROTATION, norm, true, false, unitLength );
		
	    x = field .createAlgebraicNumber( new int[]{ 1, 0, -1, 0 } );
		y = field .createAlgebraicNumber( new int[]{ 0, -1, 0, 0 } );
		z = field .createAlgebraicNumber( new int[]{ 0, -1, 0, 1 } );
		norm = new AlgebraicVector( x, y, z );
		symm .createZoneOrbit( "ivory", 0, Symmetry .NO_ROTATION, norm, true, false, unitLength );
		
		AbstractShapes defaultShapes = new OctahedralShapes( "octahedral", "octahedra", symm );
		defaultShapes = new ExportedVEFShapes( null, "sqrtPhi/octahedra", "octahedra", null, symm, defaultShapes );
		octahedralPerspective .setDefaultGeometry( defaultShapes );
		octahedralPerspective .addShapes( defaultShapes );
	}

    private final SymmetryPerspective icosahedralPerspective = new SymmetryPerspective()
    {
        private final IcosahedralSymmetry icosaSymm = new IcosahedralSymmetry( getField(), "small octahedra" );
        {
            icosaSymm .computeOrbitDots();
        }

        private final Command icosasymm = new CommandSymmetry( icosaSymm );
        private final Command tetrasymm = new CommandTetrahedralSymmetry( icosaSymm );
        private final Command axialsymm = new CommandAxialSymmetry( icosaSymm );
        private final Command h4symmetry = new CommandQuaternionSymmetry( H4, H4 );
        private final Command octasymm = new CommandSymmetry( new OctahedralSymmetry( getField(), "", "" ) );
        
//        private final AbstractShapes octahedralShapes = new OctahedralShapes( "octahedral", "octahedra", this .icosaSymm );
        private final AbstractShapes tinyIcosaShapes = new ExportedVEFShapes( null, "sqrtPhi/tinyIcosahedra", "tiny icosahedra", icosaSymm);
        private final AbstractShapes icosahedralShapes = new ExportedVEFShapes( null, "sqrtPhi/zome", "solid Zome", icosaSymm, tinyIcosaShapes);

		@Override
		public Symmetry getSymmetry()
		{
			return this .icosaSymm;
		}
		
		@Override
		public String getName()
		{
			return "icosahedral";
		}

		@Override
		public List<Shapes> getGeometries()
		{
		    // this is the order they will be shown on the dialog
			return Arrays.asList( icosahedralShapes, tinyIcosaShapes);
		}

		@Override
		public Shapes getDefaultGeometry()
		{
			return icosahedralShapes;
		}

		@Override
		public List<Tool.Factory> createToolFactories( Tool.Kind kind, ToolsModel tools )
		{
			List<Tool.Factory> result = new ArrayList<>();
			switch ( kind ) {

			case SYMMETRY:
				result .add( new IcosahedralToolFactory( tools, this .icosaSymm ) );
				result .add( new TetrahedralToolFactory( tools, this .icosaSymm ) );
				result .add( new InversionTool.Factory( tools ) );
				result .add( new MirrorTool.Factory( tools ) );
				result .add( new AxialSymmetryToolFactory( tools, this .icosaSymm ) );
				break;

			case TRANSFORM:
				result .add( new ScalingTool.Factory( tools, this .icosaSymm ) );
				result .add( new RotationTool.Factory( tools, this .icosaSymm ) );
				result .add( new TranslationTool.Factory( tools ) );
				break;

			case LINEAR_MAP:
				result .add( new AxialStretchTool.Factory( tools, this .icosaSymm, true, true, true ) );
				result .add( new AxialStretchTool.Factory( tools, this .icosaSymm, true, false, true ) );
				result .add( new AxialStretchTool.Factory( tools, this .icosaSymm, true, true, false ) );
				result .add( new AxialStretchTool.Factory( tools, this .icosaSymm, true, false, false ) );
				result .add( new AxialStretchTool.Factory( tools, this .icosaSymm, false, true, false ) );
				result .add( new AxialStretchTool.Factory( tools, this .icosaSymm, false, false, false ) );
				result .add( new LinearMapTool.Factory( tools, this .icosaSymm, false ) );
				break;

			default:
				break;
			}
			return result;
		}

		@Override
		public List<Tool> predefineTools( Tool.Kind kind, ToolsModel tools )
		{
			List<Tool> result = new ArrayList<>();
			switch ( kind ) {

			case SYMMETRY:
				result .add( new IcosahedralToolFactory( tools, this .icosaSymm ) .createPredefinedTool( "icosahedral around origin" ) );
				result .add( new TetrahedralToolFactory( tools, this .icosaSymm ) .createPredefinedTool( "tetrahedral around origin" ) );
				result .add( new InversionTool.Factory( tools ) .createPredefinedTool( "reflection through origin" ) );
				result .add( new MirrorTool.Factory( tools ) .createPredefinedTool( "reflection through XY plane" ) );
				result .add( new AxialSymmetryToolFactory( tools, this .icosaSymm ) .createPredefinedTool( "symmetry around red through origin" ) );
				break;

			case TRANSFORM:
				result .add( new ScalingTool.Factory( tools, this .icosaSymm ) .createPredefinedTool( "scale down" ) );
				result .add( new ScalingTool.Factory( tools, this .icosaSymm ) .createPredefinedTool( "scale up" ) );
				result .add( new RotationTool.Factory( tools, this .icosaSymm ) .createPredefinedTool( "rotate around red through origin" ) );
				result .add( new TranslationTool.Factory( tools ) .createPredefinedTool( "b1 move along +X" ) );
				break;

			default:
				break;
			}
			return result;
		}

		@Override
		public Command getLegacyCommand( String action )
		{
			switch ( action ) {
			case "icosasymm"    : return icosasymm;
			case "tetrasymm"    : return tetrasymm;
			case "axialsymm"    : return axialsymm;
			case "h4symmetry"   : return h4symmetry;
			case "octasymm"     : return octasymm;
			default:
				return null;
			}
		}

		@Override
		public String getModelResourcePath()
		{
			return "org/vorthmann/zome/app/icosahedral-vef.vZome";

//        if ( propertyIsTrue( "rzome.trackball" ) )
//            return "org/vorthmann/zome/app/rZomeTrackball-vef.vZome";
//        else if ( userHasEntitlement( "developer.extras" ) )
//        	  return "org/vorthmann/zome/app/icosahedral-developer.vZome";
		}
	};
		

    private final SymmetryPerspective pentagonalPerspective = new SymmetryPerspective()
    {
        private final PentagonalAntiprismSymmetry pentaSymm = new PentagonalAntiprismSymmetry( getField(), "small octahedra", null )
        		.createStandardOrbits( "blue" );

        private final Command axialsymm = new CommandAxialSymmetry( pentaSymm );
        private final Command octasymm = new CommandSymmetry( new OctahedralSymmetry( getField(), "", "" ) );
        
        private final AbstractShapes octahedralShapes = new ExportedVEFShapes( null, "sqrtPhi/octahedra", "octahedra", pentaSymm );
        private final AbstractShapes kostickShapes = new ExportedVEFShapes( null, "sqrtPhi/fivefold", "Kostick", pentaSymm, octahedralShapes );
		
        @Override
		public Symmetry getSymmetry()
		{
			return this .pentaSymm;
		}
		
		@Override
		public String getName()
		{
			return "pentagonal";
		}

		@Override
		public List<Shapes> getGeometries()
		{
		    // this is the order they will be shown on the dialog
			return Arrays.asList( kostickShapes, octahedralShapes );
		}

		@Override
		public Shapes getDefaultGeometry()
		{
			return kostickShapes;
		}

		@Override
		public List<Tool.Factory> createToolFactories( Tool.Kind kind, ToolsModel tools )
		{
			List<Tool.Factory> result = new ArrayList<>();
			switch ( kind ) {

			case SYMMETRY:
				result .add( new SymmetryTool.Factory( tools, this .pentaSymm ) );
				result .add( new MirrorTool.Factory( tools ) );
				result .add( new AxialSymmetryToolFactory( tools, this .pentaSymm ) );
				break;

			case TRANSFORM:
				result .add( new ScalingTool.Factory( tools, this .pentaSymm ) );
				result .add( new RotationTool.Factory( tools, this .pentaSymm ) );
				result .add( new TranslationTool.Factory( tools ) );
				break;

			case LINEAR_MAP:
				result .add( new LinearMapTool.Factory( tools, this .pentaSymm, false ) );
				break;

			default:
				break;
			}
			return result;
		}

		@Override
		public List<Tool> predefineTools( Tool.Kind kind, ToolsModel tools )
		{
			List<Tool> result = new ArrayList<>();
			switch ( kind ) {

			case SYMMETRY:
				result .add( new SymmetryTool.Factory( tools, this .pentaSymm ) .createPredefinedTool( "pentagonal antiprism around origin" ) );
				result .add( new AxialSymmetryToolFactory( tools, this .pentaSymm ) .createPredefinedTool( "fivefold symmetry through origin" ) );
				result .add( new MirrorTool.Factory( tools ) .createPredefinedTool( "reflection through red plane" ) );
				break;

			case TRANSFORM:
				result .add( new ScalingTool.Factory( tools, this .pentaSymm ) .createPredefinedTool( "scale down" ) );
				result .add( new ScalingTool.Factory( tools, this .pentaSymm ) .createPredefinedTool( "scale up" ) );
				result .add( new RotationTool.Factory( tools, this .pentaSymm ) .createPredefinedTool( "fivefold rotation through origin" ) );
				break;

			default:
				break;
			}
			return result;
		}

		@Override
		public Command getLegacyCommand( String action )
		{
			switch ( action ) {
			case "axialsymm"    : return axialsymm;
			case "octasymm"     : return octasymm;
			default:
				return null;
			}
		}

		@Override
		public String getModelResourcePath()
		{
			return "org/vorthmann/zome/app/pentagonal.vZome";

//        if ( propertyIsTrue( "rzome.trackball" ) )
//            return "org/vorthmann/zome/app/rZomeTrackball-vef.vZome";
//        else if ( userHasEntitlement( "developer.extras" ) )
//        	  return "org/vorthmann/zome/app/icosahedral-developer.vZome";
		}
	};
		
    private final QuaternionicSymmetry H4 = new QuaternionicSymmetry( "H_4", "com/vzome/core/math/symmetry/H4roots.vef", getField() );

	@Override
	public Collection<SymmetryPerspective> getSymmetryPerspectives()
	{
		return Arrays.asList( this .pentagonalPerspective, super .getDefaultSymmetryPerspective(), this .icosahedralPerspective );
	}

	@Override
	public SymmetryPerspective getDefaultSymmetryPerspective()
	{
		return this .pentagonalPerspective;
	}

	@Override
	public SymmetryPerspective getSymmetryPerspective( String symmName )
	{
		switch ( symmName ) {

		case "pentagonal":
			return this .pentagonalPerspective;

		case "icosahedral":
			return this .icosahedralPerspective;

		default:
			return super .getSymmetryPerspective( symmName );
		}
	}

	@Override
	public QuaternionicSymmetry getQuaternionSymmetry( String name )
	{
		switch ( name ) {

		case "H_4":
			return this .H4;

		default:
			return null;
		}
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
}

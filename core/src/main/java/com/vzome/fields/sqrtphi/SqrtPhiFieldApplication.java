package com.vzome.fields.sqrtphi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.vzome.api.Tool;
import com.vzome.api.Tool.Factory;
import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command;
import com.vzome.core.commands.CommandAxialSymmetry;
import com.vzome.core.commands.CommandQuaternionSymmetry;
import com.vzome.core.commands.CommandSymmetry;
import com.vzome.core.commands.CommandTetrahedralSymmetry;
import com.vzome.core.commands.CommandUniformH4Polytope;
import com.vzome.core.editor.AxialStretchTool;
import com.vzome.core.editor.AxialSymmetryToolFactory;
import com.vzome.core.editor.BookmarkTool;
import com.vzome.core.editor.IcosahedralToolFactory;
import com.vzome.core.editor.InversionTool;
import com.vzome.core.editor.LinearMapTool;
import com.vzome.core.editor.MirrorTool;
import com.vzome.core.editor.ModuleTool;
import com.vzome.core.editor.PlaneSelectionTool;
import com.vzome.core.editor.RotationTool;
import com.vzome.core.editor.ScalingTool;
import com.vzome.core.editor.TetrahedralToolFactory;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.editor.TranslationTool;
import com.vzome.core.kinds.DefaultFieldApplication;
import com.vzome.core.kinds.OctahedralSymmetryPerspective;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.math.symmetry.OctahedralSymmetry;
import com.vzome.core.math.symmetry.QuaternionicSymmetry;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.math.symmetry.WythoffConstruction.Listener;
import com.vzome.core.render.Shapes;
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
		defaultShapes = new ExportedVEFShapes( null, "sqrtPhiOcta", "octahedra", null, symm, defaultShapes );
		octahedralPerspective .setDefaultGeometry( defaultShapes );
		octahedralPerspective .addShapes( defaultShapes );
	}

    private final SymmetryPerspective icosahedralPerspective = new SymmetryPerspective()
    {
        private final IcosahedralSymmetry icosaSymm = new IcosahedralSymmetry( getField(), "small octahedra" );
                
        private final Command icosasymm = new CommandSymmetry( icosaSymm );
        private final Command tetrasymm = new CommandTetrahedralSymmetry( icosaSymm );
        private final Command axialsymm = new CommandAxialSymmetry( icosaSymm );
        private final Command h4symmetry = new CommandQuaternionSymmetry( H4, H4 );
        private final Command octasymm = new CommandSymmetry( icosaSymm );
        
//        private final AbstractShapes octahedralShapes = new OctahedralShapes( "octahedral", "octahedra", this .icosaSymm );
        private final AbstractShapes tinyIcosaShapes = new ExportedVEFShapes( null, "sqtrPhiTinyIcosa", "tiny icosahedra", icosaSymm);
        private final AbstractShapes icosahedralShapes = new ExportedVEFShapes( null, "sqrtPhi", "solid connectors", icosaSymm, tinyIcosaShapes);

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
		
    private final QuaternionicSymmetry H4 = new QuaternionicSymmetry( "H_4", "com/vzome/core/math/symmetry/H4roots.vef", getField() );

//	@Override
//	public Collection<SymmetryPerspective> getSymmetryPerspectives()
//	{
//		return Arrays.asList( this .icosahedralPerspective, super .getDefaultSymmetryPerspective() );
//	}
//
//	@Override
//	public SymmetryPerspective getDefaultSymmetryPerspective()
//	{
//		return this .icosahedralPerspective;
//	}

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
		switch ( name ) {

		case "H_4":
			return this .H4;

		default:
			return null;
		}
	}

    @Override
    public void registerToolFactories( Map<String, Factory> toolFactories, ToolsModel tools )
    {
        IcosahedralSymmetry symm = (IcosahedralSymmetry) icosahedralPerspective .getSymmetry();
        // symm matters for this one, since it is final in the tool
        toolFactories .put( "AxialStretchTool", new AxialStretchTool.Factory( tools, symm, false, false, false ) );
        
        // We might as well use symm in the rest, though it will be overwritten by SymmetryTool.setXmlAttributes()
        toolFactories .put( "SymmetryTool", new IcosahedralToolFactory( tools, symm ) );
        toolFactories .put( "RotationTool", new RotationTool.Factory( tools, symm ) );
        toolFactories .put( "ScalingTool", new ScalingTool.Factory( tools, symm ) );
        toolFactories .put( "InversionTool", new InversionTool.Factory( tools ) );
        toolFactories .put( "MirrorTool", new MirrorTool.Factory( tools ) );
        toolFactories .put( "TranslationTool", new TranslationTool.Factory( tools ) );
        toolFactories .put( "BookmarkTool", new BookmarkTool.Factory( tools ) );
	    toolFactories .put( "LinearTransformTool", new LinearMapTool.Factory( tools, null, false ) );
		
	    // These tool factories have to be available for loading legacy documents.
	    
	    toolFactories .put( "LinearMapTool", new LinearMapTool.Factory( tools, null, true ) );
        toolFactories .put( "ModuleTool", new ModuleTool.Factory( tools ) );
        toolFactories .put( "PlaneSelectionTool", new PlaneSelectionTool.Factory( tools ) );
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

package com.vzome.core.kinds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.vzome.api.Tool;
import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.HeptagonField;
import com.vzome.core.editor.AxialSymmetryToolFactory;
import com.vzome.core.editor.FieldApplication;
import com.vzome.core.editor.InversionTool;
import com.vzome.core.editor.LinearMapTool;
import com.vzome.core.editor.MirrorTool;
import com.vzome.core.editor.OctahedralToolFactory;
import com.vzome.core.editor.RotationTool;
import com.vzome.core.editor.ScalingTool;
import com.vzome.core.editor.SymmetryTool;
import com.vzome.core.editor.TetrahedralToolFactory;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.editor.TranslationTool;
import com.vzome.core.math.symmetry.OctahedralSymmetry;
import com.vzome.core.math.symmetry.QuaternionicSymmetry;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.render.Shapes;
import com.vzome.core.viewing.AbstractShapes;
import com.vzome.core.viewing.ExportedVEFShapes;
import com.vzome.core.viewing.OctahedralShapes;
import com.vzome.fields.heptagon.HeptagonalAntiprismSymmetry;

/**
 * Everything here is stateless, or at worst, a cache (like Shapes).
 * An instance of this can be shared by many DocumentModels.
 * This is why it does not have tool factories, though it does
 * dictate what tool factories will be present.
 * 
 * @author Scott Vorthmann
 *
 */
public class HeptagonFieldApplication implements FieldApplication
{
	private final AlgebraicField field = new HeptagonField();

	@Override
	public String getName()
	{
		return this .field .getName();
	}

	@Override
	public AlgebraicField getField()
	{
		return this .field;
	}

    private final SymmetryPerspective heptAntiprismPerspective = new SymmetryPerspective()
    {
        private final HeptagonalAntiprismSymmetry symmetry = new HeptagonalAntiprismSymmetry( field, "blue", "heptagonal antiprism", true )
        															.createStandardOrbits( "blue" );
        
        private final AbstractShapes octahedralShapes = new OctahedralShapes( "octahedral", "triangular antiprism", symmetry );
    	private final AbstractShapes antiprismShapes = new ExportedVEFShapes( null, "heptagon/antiprism", "heptagonal antiprism", symmetry, octahedralShapes );
    			
		@Override
		public Symmetry getSymmetry()
		{
			return this .symmetry;
		}
		
		@Override
		public String getName()
		{
			return "heptagonal antiprism corrected";
		}

		@Override
		public List<Shapes> getGeometries()
		{
			return Arrays.asList( antiprismShapes, octahedralShapes );
		}
		
		@Override
		public Shapes getDefaultGeometry()
		{
			return this .antiprismShapes;
		}

		@Override
		public List<Tool.Factory> createToolFactories( Tool.Kind kind, ToolsModel tools )
		{
			List<Tool.Factory> result = new ArrayList<>();
			switch ( kind ) {

			case SYMMETRY:
				result .add( new SymmetryTool.Factory( tools, this .symmetry ) );
				result .add( new MirrorTool.Factory( tools ) );
				result .add( new AxialSymmetryToolFactory( tools, this .symmetry ) );
				break;

			case TRANSFORM:
				result .add( new ScalingTool.Factory( tools, this .symmetry ) );
				result .add( new RotationTool.Factory( tools, this .symmetry ) );
				result .add( new TranslationTool.Factory( tools ) );
				break;

			case LINEAR_MAP:
				result .add( new LinearMapTool.Factory( tools, this .symmetry ) );
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
				result .add( new SymmetryTool.Factory( tools, this .symmetry ) .createPredefinedTool( "heptagonal antiprism around origin" ) );
				result .add( new MirrorTool.Factory( tools ) .createPredefinedTool( "reflection through XY plane" ) );
				result .add( new AxialSymmetryToolFactory( tools, this .symmetry ) .createPredefinedTool( "symmetry around red through origin" ) );
				break;

			case TRANSFORM:
				result .add( new ScalingTool.Factory( tools, this .symmetry ) .createPredefinedTool( "scale down" ) );
				result .add( new ScalingTool.Factory( tools, this .symmetry ) .createPredefinedTool( "scale up" ) );
				result .add( new RotationTool.Factory( tools, this .symmetry ) .createPredefinedTool( "rotate around red through origin" ) );
				result .add( new TranslationTool.Factory( tools ) .createPredefinedTool( "b1 move along +X" ) );
				break;

			default:
				break;
			}
			return result;
		}
	};

	private final SymmetryPerspective originalPerspective = new SymmetryPerspective()
    {
        private final HeptagonalAntiprismSymmetry symmetry = new HeptagonalAntiprismSymmetry( field, "blue", "heptagonal antiprism" )
        															.createStandardOrbits( "blue" );

        private final AbstractShapes defaultShapes = new OctahedralShapes( "octahedral", "triangular antiprism", symmetry );
        private final AbstractShapes antiprismShapes = new ExportedVEFShapes( null, "heptagon/antiprism", "heptagonal antiprism", symmetry, defaultShapes );
    			
		@Override
		public Symmetry getSymmetry()
		{
			return this .symmetry;
		}
		
		@Override
		public String getName()
		{
			return "heptagonal antiprism";
		}

		@Override
		public List<Shapes> getGeometries()
		{
			return Arrays.asList( defaultShapes, antiprismShapes );
		}
		
		@Override
		public Shapes getDefaultGeometry()
		{
			return this .defaultShapes;
		}

		@Override
		public List<Tool.Factory> createToolFactories( Tool.Kind kind, ToolsModel tools )
		{
			List<Tool.Factory> result = new ArrayList<>();
			return result;
		}

		@Override
		public List<Tool> predefineTools( Tool.Kind kind, ToolsModel tools )
		{
			List<Tool> result = new ArrayList<>();
			return result;
		}
	};
	
	private final SymmetryPerspective octahedralPerspective = new SymmetryPerspective()
    {
        private final OctahedralSymmetry symmetry = new OctahedralSymmetry( field, "blue", "octahedral" );
        
        private final AbstractShapes defaultShapes = new OctahedralShapes( "octahedral", "octahedra", symmetry );
    			
		@Override
		public Symmetry getSymmetry()
		{
			return this .symmetry;
		}
		
		@Override
		public String getName()
		{
			return "octahedral";
		}

		@Override
		public List<Shapes> getGeometries()
		{
			return Arrays.asList( defaultShapes );
		}
		
		@Override
		public Shapes getDefaultGeometry()
		{
			return this .defaultShapes;
		}

		@Override
		public List<Tool.Factory> createToolFactories( Tool.Kind kind, ToolsModel tools )
		{
			List<Tool.Factory> result = new ArrayList<>();
			switch ( kind ) {

			case SYMMETRY:
				result .add( new OctahedralToolFactory( tools, this .symmetry ) );
				result .add( new TetrahedralToolFactory( tools, this .symmetry ) );
				result .add( new InversionTool.Factory( tools ) );
				result .add( new MirrorTool.Factory( tools ) );
				result .add( new AxialSymmetryToolFactory( tools, this .symmetry ) );
				break;

			case TRANSFORM:
				result .add( new ScalingTool.Factory( tools, this .symmetry ) );
				result .add( new RotationTool.Factory( tools, this .symmetry ) );
				result .add( new TranslationTool.Factory( tools ) );
				break;

			case LINEAR_MAP:
				result .add( new LinearMapTool.Factory( tools, this .symmetry ) );
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
				result .add( new OctahedralToolFactory( tools, this .symmetry ) .createPredefinedTool( "octahedral around origin" ) );
				result .add( new TetrahedralToolFactory( tools, this .symmetry ) .createPredefinedTool( "tetrahedral around origin" ) );
				result .add( new InversionTool.Factory( tools ) .createPredefinedTool( "reflection through origin" ) );
				result .add( new MirrorTool.Factory( tools ) .createPredefinedTool( "reflection through XY plane" ) );
				result .add( new AxialSymmetryToolFactory( tools, this .symmetry ) .createPredefinedTool( "symmetry around green through origin" ) );
				break;

			case TRANSFORM:
				result .add( new ScalingTool.Factory( tools, this .symmetry ) .createPredefinedTool( "scale down" ) );
				result .add( new ScalingTool.Factory( tools, this .symmetry ) .createPredefinedTool( "scale up" ) );
				result .add( new RotationTool.Factory( tools, this .symmetry ) .createPredefinedTool( "rotate around green through origin" ) );
				result .add( new TranslationTool.Factory( tools ) .createPredefinedTool( "b1 move along +X" ) );
				break;

			default:
				break;
			}
			return result;
		}
	};

//		      Symmetry symmetry = new TriangularAntiprismSymmetry( kind, "blue", "triangular antiprism" );
//		      mStyles.put( symmetry, new ArrayList<>() );
//		      defaultShapes = new OctahedralShapes( "octahedral", "triangular antiprism", symmetry );
//		      addStyle( defaultShapes );
//		  }

	@Override
	public Collection<SymmetryPerspective> getSymmetryPerspectives()
	{
		return Arrays.asList( this .heptAntiprismPerspective, this .octahedralPerspective, this .originalPerspective );
	}

	@Override
	public SymmetryPerspective getDefaultSymmetryPerspective()
	{
		return this .heptAntiprismPerspective;
	}

	@Override
	public SymmetryPerspective getSymmetryPerspective( String symmName )
	{
		switch ( symmName ) {

		case "heptagonal antiprism":
			return this .heptAntiprismPerspective;

		case "octahedral":
			return this .octahedralPerspective;

		default:
			return null;
		}
	}

	@Override
	public QuaternionicSymmetry getQuaternionSymmetry( String name )
	{
		return null;
	}
}

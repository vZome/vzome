package com.vzome.core.kinds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.vzome.api.Tool;
import com.vzome.api.Tool.Factory;
import com.vzome.core.algebra.HeptagonField;
import com.vzome.core.commands.Command;
import com.vzome.core.commands.CommandAxialSymmetry;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.math.symmetry.QuaternionicSymmetry;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.render.Shapes;
import com.vzome.core.tools.AxialSymmetryToolFactory;
import com.vzome.core.tools.BookmarkTool;
import com.vzome.core.tools.InversionTool;
import com.vzome.core.tools.LinearMapTool;
import com.vzome.core.tools.MirrorTool;
import com.vzome.core.tools.ModuleTool;
import com.vzome.core.tools.OctahedralToolFactory;
import com.vzome.core.tools.PlaneSelectionTool;
import com.vzome.core.tools.ProjectionTool;
import com.vzome.core.tools.RotationTool;
import com.vzome.core.tools.ScalingTool;
import com.vzome.core.tools.SymmetryTool;
import com.vzome.core.tools.TranslationTool;
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
public class HeptagonFieldApplication extends DefaultFieldApplication
{
	public HeptagonFieldApplication()
	{
		super( new HeptagonField() );
	}

    private final SymmetryPerspective heptAntiprismPerspective = new SymmetryPerspective()
    {
        private final HeptagonalAntiprismSymmetry symmetry = new HeptagonalAntiprismSymmetry( getField(), "blue", "heptagonal antiprism", true )
        															.createStandardOrbits( "blue" );
        
        private final AbstractShapes octahedralShapes = new OctahedralShapes( "octahedral", "triangular antiprism", symmetry );
        private final AbstractShapes antiprismShapes = new ExportedVEFShapes( null, "heptagon/antiprism", "heptagonal antiprism", symmetry, octahedralShapes );
    	
        private final Command axialsymm = new CommandAxialSymmetry( symmetry );

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
				result .add( new LinearMapTool.Factory( tools, this .symmetry, false ) );
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

		@Override
		public Command getLegacyCommand( String action )
		{
			switch ( action ) {
			case "axialsymm"    : return axialsymm;
			default:
				return null;
			}
		}

		@Override
		public String getModelResourcePath()
		{
			return "org/vorthmann/zome/app/heptagonal antiprism.vZome";
		}
	};

	private final SymmetryPerspective originalPerspective = new SymmetryPerspective()
    {
        private final HeptagonalAntiprismSymmetry symmetry = new HeptagonalAntiprismSymmetry( getField(), "blue", "heptagonal antiprism" )
        															.createStandardOrbits( "blue" );

        private final AbstractShapes defaultShapes = new OctahedralShapes( "octahedral", "triangular antiprism", symmetry );
        private final AbstractShapes antiprismShapes = new ExportedVEFShapes( null, "heptagon/antiprism", "heptagonal antiprism", symmetry, defaultShapes );
    	    
        private final Command axialsymm = new CommandAxialSymmetry( symmetry );

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

		@Override
		public Command getLegacyCommand( String action )
		{
			switch ( action ) {
			case "axialsymm"    : return axialsymm;
			default:
				return null;
			}
		}

		@Override
		public String getModelResourcePath()
		{
			return "org/vorthmann/zome/app/heptagonal antiprism.vZome";
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
		return Arrays.asList( this .heptAntiprismPerspective, super .getDefaultSymmetryPerspective(), this .originalPerspective );
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

		case "heptagonal antiprism corrected":
			return this .heptAntiprismPerspective;

		case "heptagonal antiprism":
			return this .originalPerspective;

		default:
			return super .getSymmetryPerspective( symmName );
		}
	}

	@Override
	public QuaternionicSymmetry getQuaternionSymmetry( String name )
	{
		return null;
	}

    @Override
    public void registerToolFactories( Map<String, Factory> toolFactories, ToolsModel tools )
    {
        // Any SymmetryTool factory here is good enough
        toolFactories .put( "SymmetryTool", new OctahedralToolFactory( tools, null ) );
        toolFactories .put( "RotationTool", new RotationTool.Factory( tools, null ) );
        toolFactories .put( "ScalingTool", new ScalingTool.Factory( tools, null ) );
        toolFactories .put( "InversionTool", new InversionTool.Factory( tools ) );
        toolFactories .put( "MirrorTool", new MirrorTool.Factory( tools ) );
        toolFactories .put( "TranslationTool", new TranslationTool.Factory( tools ) );
        toolFactories .put( "ProjectionTool", new ProjectionTool.Factory( tools ) );
        toolFactories .put( "BookmarkTool", new BookmarkTool.Factory( tools ) );
	    toolFactories .put( "LinearTransformTool", new LinearMapTool.Factory( tools, null, false ) );
		
	    // These tool factories have to be available for loading legacy documents.
	    
	    toolFactories .put( "LinearMapTool", new LinearMapTool.Factory( tools, null, true ) );
        toolFactories .put( "ModuleTool", new ModuleTool.Factory( tools ) );
        toolFactories .put( "PlaneSelectionTool", new PlaneSelectionTool.Factory( tools ) );
    }
}

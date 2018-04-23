package com.vzome.core.kinds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.vzome.api.Tool;
import com.vzome.core.algebra.RootThreeField;
import com.vzome.core.commands.Command;
import com.vzome.core.commands.CommandSymmetry;
import com.vzome.core.editor.AxialSymmetryToolFactory;
import com.vzome.core.editor.InversionTool;
import com.vzome.core.editor.LinearMapTool;
import com.vzome.core.editor.MirrorTool;
import com.vzome.core.editor.RotationTool;
import com.vzome.core.editor.ScalingTool;
import com.vzome.core.editor.SymmetryTool;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.editor.TranslationTool;
import com.vzome.core.math.symmetry.AbstractSymmetry;
import com.vzome.core.math.symmetry.DodecagonalSymmetry;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.render.Shapes;
import com.vzome.core.viewing.AbstractShapes;
import com.vzome.core.viewing.DodecagonalShapes;
import com.vzome.core.viewing.ExportedVEFShapes;

/**
 * Everything here is stateless, or at worst, a cache (like Shapes).
 * An instance of this can be shared by many DocumentModels.
 * This is why it does not have tool factories, though it does
 * dictate what tool factories will be present.
 * 
 * @author Scott Vorthmann
 *
 */
public class RootThreeFieldApplication extends DefaultFieldApplication
{
	public RootThreeFieldApplication()
	{
		super( new RootThreeField() );
		OctahedralSymmetryPerspective octahedralPerspective = (OctahedralSymmetryPerspective) super .getDefaultSymmetryPerspective();
		AbstractSymmetry symm = (AbstractSymmetry) octahedralPerspective .getSymmetry();
		
		symm .createZoneOrbit( "red",   0, Symmetry .NO_ROTATION, new int[][] { {1,1, 1,2}, {1,2, 0,1}, {0,1, 0,1} }, true );
		symm .createZoneOrbit( "brown", 0, Symmetry .NO_ROTATION, new int[][] { {1,1, 0,1}, {1,1, 0,1}, {2,1, 0,1} } );

		AbstractShapes defaultShapes = new ExportedVEFShapes( null, "rootThreeOctaSmall", "small octahedra", "small connectors", symm );
		octahedralPerspective .addShapes( defaultShapes );
		octahedralPerspective .setDefaultGeometry( defaultShapes );
	}

	final SymmetryPerspective dodecagonalPerspective = new SymmetryPerspective()
    {
        private final DodecagonalSymmetry symmetry = new DodecagonalSymmetry( getField(), "prisms" );
        
        private final AbstractShapes defaultShapes = new ExportedVEFShapes( null, "dodecagon3d", "prisms", symmetry );
    	private final AbstractShapes hexagonShapes = new DodecagonalShapes( "dodecagonal", "hexagons", "flat hexagons", symmetry );
    	
    	private final Command dodecagonsymm = new CommandSymmetry( this .symmetry );
    			
		@Override
		public Symmetry getSymmetry()
		{
			return this .symmetry;
		}
		
		@Override
		public String getName()
		{
			return "dodecagonal";
		}

		@Override
		public List<Shapes> getGeometries()
		{
			return Arrays.asList( defaultShapes, hexagonShapes );
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
				result .add( new SymmetryTool.Factory( tools, this .symmetry ) );
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
				result .add( new SymmetryTool.Factory( tools, this .symmetry ) .createPredefinedTool( "dodecagonal antiprism around origin" ) );
				result .add( new InversionTool.Factory( tools ) .createPredefinedTool( "reflection through origin" ) );
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

			case "dodecagonsymm":
				return this .dodecagonsymm;

			default:
				return null;
			}
		}

		@Override
		public String getModelResourcePath()
		{
			return "org/vorthmann/zome/app/dodecagonal.vZome";
		}
    };
    
	@Override
	public SymmetryPerspective getDefaultSymmetryPerspective()
	{
		return this .dodecagonalPerspective;
	}

	@Override
	public Collection<SymmetryPerspective> getSymmetryPerspectives()
	{
		return Arrays.asList( super .getDefaultSymmetryPerspective(), this .dodecagonalPerspective );
	}

	@Override
	public SymmetryPerspective getSymmetryPerspective( String symmName )
	{
		switch ( symmName ) {

		case "dodecagonal":
			return this .dodecagonalPerspective;

		default:
			return super .getSymmetryPerspective( symmName );
		}
	}
}

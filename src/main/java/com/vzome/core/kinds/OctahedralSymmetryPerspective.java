package com.vzome.core.kinds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vzome.api.Tool;
import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.commands.Command;
import com.vzome.core.commands.CommandAxialSymmetry;
import com.vzome.core.commands.CommandSymmetry;
import com.vzome.core.editor.AxialSymmetryToolFactory;
import com.vzome.core.editor.FieldApplication.SymmetryPerspective;
import com.vzome.core.editor.InversionTool;
import com.vzome.core.editor.LinearMapTool;
import com.vzome.core.editor.MirrorTool;
import com.vzome.core.editor.OctahedralToolFactory;
import com.vzome.core.editor.RotationTool;
import com.vzome.core.editor.ScalingTool;
import com.vzome.core.editor.TetrahedralToolFactory;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.editor.TranslationTool;
import com.vzome.core.math.symmetry.OctahedralSymmetry;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.render.Shapes;
import com.vzome.core.viewing.OctahedralShapes;

public final class OctahedralSymmetryPerspective implements SymmetryPerspective
{
	private final AlgebraicField field;

	private final OctahedralSymmetry symmetry;
	
	private final List<Shapes> geometries = new ArrayList<>();
	
	private final Map<String, Command> commands = new HashMap<String, Command>();

	private Shapes defaultShapes = null;

	public OctahedralSymmetryPerspective( AlgebraicField field )
	{
		super();
		this .field = field;
		this .symmetry = new OctahedralSymmetry( this .field, "blue", "small octahedra" );
		
		this .commands .put( "octasymm", new CommandSymmetry( this .symmetry ) );
		this .commands .put( "axialsymm", new CommandAxialSymmetry( this .symmetry ) );
	}
	
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
	
	void addShapes( Shapes shapes )
	{
		this .geometries .add( shapes );
	}

	@Override
	public List<Shapes> getGeometries()
	{
		return this .geometries;
	}

	public void setDefaultGeometry( Shapes shapes )
	{
		this .defaultShapes = shapes;
	}

	@Override
	public Shapes getDefaultGeometry()
	{
		if ( this .defaultShapes == null ) {
			this .defaultShapes = new OctahedralShapes( "octahedral", "octahedra", symmetry );
			this .geometries .add( this .defaultShapes );
		}
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

	@Override
	public Command getLegacyCommand( String action )
	{
		return null;
	}

	@Override
	public String getModelResourcePath()
	{
		return "org/vorthmann/zome/app/octahedral-vef.vZome";
	}
}
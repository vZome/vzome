package com.vzome.core.kinds;

import java.util.ArrayList;
import java.util.List;

import com.vzome.api.Tool;
import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.math.symmetry.OctahedralSymmetry;
import com.vzome.core.tools.AxialSymmetryToolFactory;
import com.vzome.core.tools.InversionTool;
import com.vzome.core.tools.LinearMapTool;
import com.vzome.core.tools.MirrorTool;
import com.vzome.core.tools.OctahedralToolFactory;
import com.vzome.core.tools.ProjectionTool;
import com.vzome.core.tools.RotationTool;
import com.vzome.core.tools.ScalingTool;
import com.vzome.core.tools.TetrahedralToolFactory;
import com.vzome.core.tools.TranslationTool;
import com.vzome.core.viewing.OctahedralShapes;

public final class OctahedralSymmetryPerspective extends AbstractSymmetryPerspective
{
	public OctahedralSymmetryPerspective( AlgebraicField field ) {
		super(new OctahedralSymmetry( field ) );
        setDefaultGeometry( new OctahedralShapes( "octahedral", "octahedra", symmetry ) );
	}
	
    @Override
    public OctahedralSymmetry getSymmetry() {
        return (OctahedralSymmetry) this.symmetry;
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
            result .add( new ProjectionTool.Factory( tools ) );
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
	public String getModelResourcePath()
	{
		return "org/vorthmann/zome/app/octahedral-vef.vZome";
	}
}

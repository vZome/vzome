package com.vzome.core.kinds;

import java.util.ArrayList;
import java.util.List;

import com.vzome.api.Tool;
import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.math.symmetry.OctahedralSymmetry;
import com.vzome.core.tools.AxialSymmetryToolFactory;
import com.vzome.core.tools.InversionToolFactory;
import com.vzome.core.tools.LineReflectionToolFactory;
import com.vzome.core.tools.LinearMapToolFactory;
import com.vzome.core.tools.MirrorToolFactory;
import com.vzome.core.tools.OctahedralToolFactory;
import com.vzome.core.tools.PerspectiveProjectionToolFactory;
import com.vzome.core.tools.ProjectionToolFactory;
import com.vzome.core.tools.RotationToolFactory;
import com.vzome.core.tools.ScalingToolFactory;
import com.vzome.core.tools.TetrahedralToolFactory;
import com.vzome.core.tools.TranslationToolFactory;
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
			result .add( new InversionToolFactory( tools ) );
            result .add( new LineReflectionToolFactory( tools ) );
            result .add( new MirrorToolFactory( tools ) );
			result .add( new AxialSymmetryToolFactory( tools, this .symmetry ) );
			break;

		case TRANSFORM:
			result .add( new ScalingToolFactory( tools, this .symmetry ) );
			result .add( new RotationToolFactory( tools, this .symmetry ) );
            result .add( new TranslationToolFactory( tools ) );
            result .add( new ProjectionToolFactory( tools ) );
            result .add( new PerspectiveProjectionToolFactory( tools ) );
			break;

		case LINEAR_MAP:
			result .add( new LinearMapToolFactory( tools, this .symmetry, false ) );
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
			result .add( new InversionToolFactory( tools ) .createPredefinedTool( "reflection through origin" ) );
			result .add( new MirrorToolFactory( tools ) .createPredefinedTool( "reflection through XY plane" ) );
            result .add( new MirrorToolFactory( tools ) .createPredefinedTool( "reflection through X=Y green plane" ) );
			result .add( new AxialSymmetryToolFactory( tools, this .symmetry ) .createPredefinedTool( "symmetry around green through origin" ) );
			break;

		case TRANSFORM:
			result .add( new ScalingToolFactory( tools, this .symmetry ) .createPredefinedTool( "scale down" ) );
			result .add( new ScalingToolFactory( tools, this .symmetry ) .createPredefinedTool( "scale up" ) );
			result .add( new RotationToolFactory(tools, this.symmetry, true) .createPredefinedTool( "rotate around green through origin" ) );
			result .add( new TranslationToolFactory( tools ) .createPredefinedTool( "b1 move along +X" ) );
			break;

		default:
			break;
		}
		return result;
	}

	@Override
	public String getModelResourcePath()
	{
        return modelResourcePath;
    }

    private String modelResourcePath = "org/vorthmann/zome/app/octahedral-vef.vZome";

    public void setModelResourcePath(String resourcePath)
    {
        this.modelResourcePath = resourcePath;
    }
}

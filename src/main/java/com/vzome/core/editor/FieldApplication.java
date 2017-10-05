package com.vzome.core.editor;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.vzome.api.Tool;
import com.vzome.api.Tool.Factory;
import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.math.symmetry.QuaternionicSymmetry;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.render.Shapes;

public interface FieldApplication
{
	public interface SymmetryPerspective
	{
		List<Shapes> getGeometries();
		
		Shapes getDefaultGeometry();
		
		String getName();
		
		Symmetry getSymmetry();

		List<Tool.Factory> createToolFactories( Tool.Kind kind, ToolsModel model );
		
		List<Tool> predefineTools( Tool.Kind kind, ToolsModel model );
	}
	
	AlgebraicField getField();

	Collection<SymmetryPerspective> getSymmetryPerspectives();

	SymmetryPerspective getDefaultSymmetryPerspective();

	SymmetryPerspective getSymmetryPerspective( String name );

	QuaternionicSymmetry getQuaternionSymmetry( String name );

	String getName();

    void registerToolFactories( Map<String, Factory> toolFactories, ToolsModel tools );
}

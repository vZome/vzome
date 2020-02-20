package com.vzome.core.editor;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.vzome.api.Tool;
import com.vzome.api.Tool.Factory;
import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.commands.Command;
import com.vzome.core.math.symmetry.QuaternionicSymmetry;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.math.symmetry.WythoffConstruction;
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

		/**
		 * These commands should all be symmetry-DEPENDANT. 
		 * Contrast with {@code FieldApplication.getLegacyCommand(action) }.
		 * @param action
		 * @return
		 */
		Command getLegacyCommand( String action );

		String getModelResourcePath();
	}
	
	AlgebraicField getField();
	
	void constructPolytope( String groupName, int index, int edgesToRender, AlgebraicNumber[] edgeScales, WythoffConstruction.Listener listener );

	Collection<SymmetryPerspective> getSymmetryPerspectives();

	SymmetryPerspective getDefaultSymmetryPerspective();

	SymmetryPerspective getSymmetryPerspective( String name );

	QuaternionicSymmetry getQuaternionSymmetry( String name );

	String getName();

    void registerToolFactories( Map<String, Factory> toolFactories, ToolsModel tools );

    /**
     * These commands should all be symmetry-INDEPENDANT. 
     * Contrast with {@code FieldApplication.SymmetryPerspective.getLegacyCommand(action) }.
     * @param action
     * @return
     */
	Command getLegacyCommand( String action );
}

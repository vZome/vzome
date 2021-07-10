package com.vzome.core.editor;

import java.util.List;

import com.vzome.api.Tool;
import com.vzome.api.Tool.Factory;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.commands.Command;
import com.vzome.core.editor.api.Shapes;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Symmetry;

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

    boolean orbitIsStandard( Direction orbit );

    boolean orbitIsBuildDefault( Direction orbit );

    AlgebraicNumber getOrbitUnitLength( Direction orbit );
}
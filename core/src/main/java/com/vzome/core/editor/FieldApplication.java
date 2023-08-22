package com.vzome.core.editor;

import java.util.Collection;
import java.util.Map;

import com.vzome.api.Tool;
import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.commands.Command;
import com.vzome.core.math.symmetry.Symmetries4D;

public interface FieldApplication extends Symmetries4D
{
	AlgebraicField getField();
	
	Collection<SymmetryPerspective> getSymmetryPerspectives();

	SymmetryPerspective getDefaultSymmetryPerspective();

	SymmetryPerspective getSymmetryPerspective( String name );

    String getName();

    String getLabel();

    void registerToolFactories( Map<String, Tool.Factory> toolFactories, ToolsModel tools );

    /**
     * These commands should all be symmetry-INDEPENDANT. 
     * Contrast with {@code FieldApplication.SymmetryPerspective.getLegacyCommand(action) }.
     * @param action
     * @return
     */
	Command getLegacyCommand( String action );
}

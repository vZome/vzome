package com.vzome.core.editor;


public class AxialSymmetryToolFactory extends RotationTool.Factory
{
	public AxialSymmetryToolFactory( EditorModel model, UndoableEdit.Context context )
	{
		super( model, context );
	}

	@Override
	public Tool createToolInternal( int index )
	{
		// Just like the superclass, but with "true" for full rotation.
		return new RotationTool( "axial symmetry." + index, getSymmetry(), getSelection(), getModel(), null, true );
	}
}
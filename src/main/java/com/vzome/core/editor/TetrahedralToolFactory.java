package com.vzome.core.editor;


public class TetrahedralToolFactory extends OctahedralToolFactory
{
	public TetrahedralToolFactory( EditorModel model, UndoableEdit.Context context )
	{
		super( model, context );
	}

	@Override
	public Tool createToolInternal( int index )
	{
		return new SymmetryTool( "tetrahedral." + index, getSymmetry(), getSelection(), getModel(), null );
	}
}
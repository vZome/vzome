package com.vzome.core.editor;

import com.vzome.core.math.symmetry.IcosahedralSymmetry;

public class IcosahedralToolFactory extends AbstractToolFactory implements ToolFactory
{
	private final IcosahedralSymmetry symmetry;

	public IcosahedralToolFactory( EditorModel model, UndoableEdit.Context context, IcosahedralSymmetry symmetry )
	{
		super( model, context );
		this .symmetry = symmetry;
	}

	@Override
	protected boolean countsAreValid( int total, int balls, int struts, int panels )
	{
		return ( total == 1 && balls == 1 );
	}

	@Override
	public Tool createToolInternal( int index )
	{
		return new SymmetryTool( "icosahedral." + index, symmetry, getSelection(), getModel(), null, null );
	}

	@Override
	protected boolean bindParameters(Selection selection, SymmetrySystem symmetry) {
		// TODO Auto-generated method stub
		return false;
	}
}
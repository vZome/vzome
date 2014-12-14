
//(c) Copyright 2006, Scott Vorthmann

package com.vzome.core.editor;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.construction.Segment;

public class SymmetryAxisChange implements UndoableEdit
{
    private Segment mOldAxis, mNewAxis;
    private final EditorModel mEditor;
	private Context context;

    public SymmetryAxisChange( EditorModel editor, Segment newAxis )
    {
        mOldAxis = editor .getSymmetrySegment();
        mNewAxis = newAxis;
        mEditor = editor;
    }
    
    public boolean isVisible()
    {
    	return false;
    }

    public void redo()
    {
        mEditor .setSymmetrySegment( mNewAxis );
    }

    public void undo()
    {
        mEditor .setSymmetrySegment( mOldAxis );
    }

    public Element getXml( Document doc )
    {
        Element result = doc .createElement( "SymmetryAxisChange" );
        XmlSaveFormat .serializeSegment( result, "start", "end", mNewAxis );
        return result;
    }

    public void loadAndPerform( Element xml, XmlSaveFormat format, Context context ) throws Failure
    {
        if ( format .rationalVectors() )
        {
            mNewAxis = format .parseSegment( xml, "start", "end" );
        }
        else
        {
            Map attrs = format .loadCommandAttributes( xml, false ); // get all four dimensions
            mNewAxis = (Segment) attrs .get( "new" );
        }
        
        context .performAndRecord( this );
    }

    public void perform()
    {
        redo();
    }

	public Context getContext()
	{
		return this .context;
	}

	public void setContext( Context context )
	{
		this .context = context;
	}

    public void releaseState()
    {
        setContext( null );
    }

    public boolean isDestructive()
    {
        return true;
    }

    public boolean isSticky()
    {
        return false;
    }
}

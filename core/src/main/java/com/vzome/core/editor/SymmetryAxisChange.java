
//(c) Copyright 2006, Scott Vorthmann

package com.vzome.core.editor;

import java.util.Properties;

import com.vzome.core.commands.AttributeMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.construction.Segment;

public class SymmetryAxisChange implements UndoableEdit
{
    private Segment mOldAxis, mNewAxis;
    private final EditorModel mEditor;

    public SymmetryAxisChange( EditorModel editor )
    {
        this( editor, null );
    }
    
    public SymmetryAxisChange( EditorModel editor, Segment newAxis )
    {
        mOldAxis = editor .getSymmetrySegment();
        mNewAxis = newAxis;
        mEditor = editor;
    }
    
    @Override
    public boolean isVisible()
    {
    	return false;
    }

    @Override
    public void redo()
    {
        mEditor .setSymmetrySegment( mNewAxis );
    }

    @Override
    public void undo()
    {
        mEditor .setSymmetrySegment( mOldAxis );
    }

    @Override
    public void configure( Properties props ) {}

    @Override
    public Element getXml( Document doc )
    {
        Element result = doc .createElement( "SymmetryAxisChange" );
        XmlSaveFormat .serializeSegment( result, "start", "end", mNewAxis );
        return result;
    }

    @Override
    public Element getDetailXml( Document doc )
    {
        return getXml( doc );
    }

    @Override
    public void loadAndPerform( Element xml, XmlSaveFormat format, Context context ) throws Failure
    {
        if ( format .rationalVectors() )
        {
            mNewAxis = format .parseSegment( xml, "start", "end" );
        }
        else
        {
            AttributeMap attrs = format .loadCommandAttributes( xml );
            mNewAxis = (Segment) attrs .get( "new" );
        }
        
        context .performAndRecord( this );
    }

    @Override
    public void perform()
    {
        redo();
    }

    @Override
    public boolean isDestructive()
    {
        return true;
    }

    @Override
    public boolean isSticky()
    {
        return false;
    }
}

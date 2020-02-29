
//(c) Copyright 2006, Scott Vorthmann

package com.vzome.core.edits;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.commands.AttributeMap;
import com.vzome.core.commands.Command;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.construction.Segment;
import com.vzome.core.editor.EditorModel;
import com.vzome.core.editor.UndoableEdit;
import com.vzome.core.model.Manifestation;

public class SymmetryAxisChange extends UndoableEdit
{
    private Segment mOldAxis, mNewAxis;
    private final EditorModel mEditor;

    public SymmetryAxisChange( EditorModel editor )
    {
        this( editor, null );
    }
    
    /**
     * Used by CommandEdit.
     * @param editor
     * @param m
     */
    public SymmetryAxisChange( EditorModel editor, Segment newAxis )
    {
        mOldAxis = editor .getSymmetrySegment();
        mNewAxis = newAxis;
        mEditor = editor;
    }

    public void configure( Map<String, Object> props )
    {
        Manifestation man = (Manifestation) props .get( "picked" );
        if ( man != null )
            this.mNewAxis = (Segment) man .getConstructions() .next();
    }

    @Override
    public boolean isVisible()
    {
    	return false;
    }

    @Override
    public boolean isNoOp()
    {
        return this.mNewAxis == null ||
               ( this.mOldAxis != null
               && this.mNewAxis .getStart() .equals( this.mOldAxis .getStart() )
               && this.mNewAxis .getEnd() .equals( this.mOldAxis .getEnd() ) ) ;
    }

    @Override
    public void redo()
    {
        if ( this .isNoOp() )
            return;

        mEditor .setSymmetrySegment( mNewAxis );
    }

    @Override
    public void undo()
    {
        if ( this .isNoOp() )
            return;

        mEditor .setSymmetrySegment( mOldAxis );
    }

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
    public void perform() throws Command.Failure
    {
        if ( this.mNewAxis == null )
        {
            this.mNewAxis = (Segment) this.mEditor .getSelectedConstruction( Segment.class );
            if ( this.mNewAxis == null )
                throw new Command.Failure( "Selection is not a single strut." );
        }
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

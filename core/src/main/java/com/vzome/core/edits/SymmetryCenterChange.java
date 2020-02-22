
//(c) Copyright 2006, Scott Vorthmann

package com.vzome.core.edits;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.commands.AttributeMap;
import com.vzome.core.commands.Command;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Point;
import com.vzome.core.editor.EditorModel;
import com.vzome.core.editor.UndoableEdit;
import com.vzome.core.model.Manifestation;

public class SymmetryCenterChange extends UndoableEdit
{
    private Point mOldCenter, mNewCenter;
    private final EditorModel editor;

    public SymmetryCenterChange( EditorModel editor )
    {
        this( editor, null );
    }

    /**
     * Used by CommandEdit.
     * @param editor
     * @param m
     */
    public SymmetryCenterChange( EditorModel editor, Point newCenter )
    {
        this.mOldCenter = editor .getCenterPoint();
        this.mNewCenter = newCenter;
        this.editor = editor;
    }

    public void configure( Map<String, Object> props )
    {
        Manifestation man = (Manifestation) props .get( "picked" );
        if ( man != null )
            this .mNewCenter = (Point) man .getConstructions() .next();
    }

    @Override
    public boolean isNoOp()
    {
        return this.mNewCenter == null || this.mNewCenter .getLocation() .equals( this.mOldCenter .getLocation() ) ;
    }

    @Override
    public boolean isVisible()
    {
        return false;
    }

    @Override
    public void redo()
    {
        if ( this .isNoOp() )
            return;

        this .editor .setCenterPoint( mNewCenter );
    }

    @Override
    public void undo()
    {
        if ( this .isNoOp() )
            return;

        this .editor .setCenterPoint( mOldCenter );
    }

    @Override
    public Element getXml( Document doc )
    {
        Element result = doc .createElement( "SymmetryCenterChange" );
        XmlSaveFormat .serializePoint( result, "new", mNewCenter );
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
            mNewCenter = format .parsePoint( xml, "new" );
        }
        else
        {
            AttributeMap attrs = format .loadCommandAttributes( xml );
            Point center = (Point) attrs .get( "new" );
            mNewCenter = new FreePoint( center .getLocation() .projectTo3d( true ) );
        }
        
        context .performAndRecord( this );
    }

    @Override
    public void perform() throws Command.Failure
    {
        if ( this.mNewCenter == null )
        {
            this.mNewCenter = (Point) this .editor .getSelectedConstruction( Point.class );
            if ( this.mNewCenter == null )
                throw new Command.Failure( "Selection is not a single ball." );
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


//(c) Copyright 2010, Scott Vorthmann.

package com.vzome.core.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.commands.Command.Failure;

public class Branch implements UndoableEdit
{
    private List edits = new ArrayList();
    private Context context;
    private XmlSaveFormat format;
    private Element xml;
    
    public void addEdit( UndoableEdit edit )
    {
        edits .add( edit );
    }

    public boolean isDestructive()
    {
        return false;
    }

    public void perform() throws Failure
    {
        final Stack toUndo = new Stack();
        NodeList nodes = xml .getChildNodes();
        for ( int i = 0; i < nodes .getLength(); i++ ) {
            Node kid = nodes .item( i );
            if ( kid instanceof Element ) {
                Element editElem = (Element) kid;

                // the remainder is essentially identical to DeferredEdit .redo()

                UndoableEdit edit = context .createEdit( editElem .getLocalName(), format );
                addEdit( edit );
                edit. loadAndPerform( editElem, format, new UndoableEdit.Context()
                {
                    public void performAndRecord( UndoableEdit edit )
                    {
                        // realized is responsible for inserting itself, or any replacements (migration)
                        try {
                            edit .perform();
                        } catch (Failure e) {
                            // really hacky tunneling
                            throw new RuntimeException( e );
                        }
                        toUndo .push( edit );
                    }

                    public UndoableEdit createEdit( String type, XmlSaveFormat format )
                    {
                        return context .createEdit( type, format );
                    }
                } );
            }
        }
        while ( ! toUndo .isEmpty() )
        {
            UndoableEdit edit = (UndoableEdit) toUndo .pop();
            edit .undo();
        }
    }

    public void undo() {}

    public void redo() {}

    public boolean isVisible()
    {
        return false;
    }

    public Element getXml( Document doc )
    {
        Element branch = doc .createElement( "Branch" );
        for (Iterator iterator = edits .iterator(); iterator.hasNext(); ) {
            UndoableEdit edit = (UndoableEdit) iterator.next();
            branch .appendChild( edit .getXml( doc ) );
        }
        return branch;
    }

    public void loadAndPerform( Element xml, XmlSaveFormat format, final Context context ) throws Failure
    {
        this .xml = xml;
        this .format = format;
        context .performAndRecord( this );
    }

    public void setContext( Context context )
    {
        this .context = context;
    }

    public Context getContext()
    {
        return this .context;
    }

    public void releaseState() {}

    public boolean isSticky()
    {
        return true;
    }
}

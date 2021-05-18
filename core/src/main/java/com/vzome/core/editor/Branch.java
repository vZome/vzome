
//(c) Copyright 2010, Scott Vorthmann.

package com.vzome.core.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vzome.core.commands.Command;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.editor.api.Context;
import com.vzome.core.editor.api.UndoableEdit;
import com.vzome.core.commands.XmlSaveFormat;

public class Branch extends UndoableEdit
{
	private final Context context;
	
    public Branch( Context context )
    {
		super();
		this .context = context;
	}

    @Override
    public boolean isNoOp()
    {
        return false;
    }

	private List<UndoableEdit> edits = new ArrayList<>();
    private XmlSaveFormat format;
    private Element xml;
    
    public void addEdit( UndoableEdit edit )
    {
        edits .add( edit );
    }

    @Override
    public boolean isDestructive()
    {
        return false;
    }

    @Override
    public void perform() throws Failure
    {
        final Stack<UndoableEdit> toUndo = new Stack<>();
        NodeList nodes = xml .getChildNodes();
        for ( int i = 0; i < nodes .getLength(); i++ ) {
            Node kid = nodes .item( i );
            if ( kid instanceof Element ) {
                Element editElem = (Element) kid;

                // the remainder is essentially identical to DeferredEdit .redo()

                UndoableEdit edit = context .createEdit( editElem );
                addEdit( edit );
                edit. loadAndPerform(editElem, format, new Context()
                {
                    @Override
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

                    @Override
                    public UndoableEdit createEdit( Element xml )
                    {
                        return context .createEdit( xml );
                    }

                    @Override
                    public Command createLegacyCommand( String cmdName ) throws Failure
                    {
                        return context .createLegacyCommand( cmdName );
                    }
                } );
            }
        }
        while ( ! toUndo .isEmpty() )
        {
            UndoableEdit edit = toUndo .pop();
            edit .undo();
        }
    }

    @Override
    public void undo() {}

    @Override
    public void redo() {}

    @Override
    public void configure( Map<String,Object> props ) {}

    @Override
    public boolean isVisible()
    {
        return false;
    }

    @Override
    public Element getXml( Document doc )
    {
        Element branch = doc .createElement( "Branch" );
        for (UndoableEdit edit : edits) {
            branch .appendChild( edit .getXml( doc ) );
        }
        return branch;
    }

    @Override
    public Element getDetailXml( Document doc )
    {
        Element branch = doc .createElement( "Branch" );
        for (UndoableEdit edit : edits) {
            branch .appendChild( edit .getDetailXml( doc ) );
        }
        return branch;
    }

    @Override
    public void loadAndPerform( Element xml, XmlSaveFormat format, final Context context ) throws Failure
    {
        this .xml = xml;
        this .format = format;
        context .performAndRecord( this );
    }

    @Override
    public boolean isSticky()
    {
        return true;
    }
}

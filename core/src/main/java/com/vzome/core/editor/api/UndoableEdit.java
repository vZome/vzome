
package com.vzome.core.editor.api;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.commands.Command;
import com.vzome.core.commands.XmlSaveFormat;


public abstract class UndoableEdit
{
    public abstract void configure( Map<String,Object> props );
    
    public abstract void perform() throws Command.Failure;

    public abstract void undo();

    public abstract void redo() throws Command.Failure;

    public abstract boolean isNoOp();

    public abstract boolean isVisible();

    public abstract Element getXml( Document doc );

    public abstract void loadAndPerform( Element xml, XmlSaveFormat format, Context context ) throws Command.Failure;

    /**
     * True when this edit must cause a persistent history branch.
     * @return
     */
    public abstract boolean isSticky();

    /**
     * True when this edit invalidates redoable edits.
     * @return
     */
    public abstract boolean isDestructive();

    public abstract Element getDetailXml( Document doc );
    
    private boolean hasBreakpoint = false;
    private int lineNumber = -1;
    
    public boolean hasBreakpoint()
    {
        return this .hasBreakpoint;
    }
    
    public void setBreakpoint( boolean value )
    {
        this .hasBreakpoint = value;
    }
    
    public int getLineNumber()
    {
        return this .lineNumber ;
    }
    
    public void setLineNumber( int lineNumber )
    {
        this .lineNumber = lineNumber;
    }
}

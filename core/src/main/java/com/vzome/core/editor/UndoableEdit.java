//(c) Copyright 2006, Scott Vorthmann

package com.vzome.core.editor;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.commands.Command;
import com.vzome.core.commands.XmlSaveFormat;


public interface UndoableEdit
{
    public void configure( Properties props );
    
    public void perform() throws Command.Failure;

    public void undo();

    public void redo() throws Command.Failure;

    public boolean isVisible();

    public Element getXml( Document doc );

    public void loadAndPerform( Element xml, XmlSaveFormat format, Context context ) throws Command.Failure;

    //    public interface History
    //    {
    //        void performAndRecord( UndoableEdit edit );
    //    }
    //    
    public interface Context
    {
        UndoableEdit createEdit( Element xml );

        void performAndRecord( UndoableEdit edit );
    }

    /**
     * True when this edit must cause a persistent history branch.
     * @return
     */
    public boolean isSticky();

    /**
     * True when this edit invalidates redoable edits.
     * @return
     */
    public boolean isDestructive();

    public Element getDetailXml( Document doc );
}

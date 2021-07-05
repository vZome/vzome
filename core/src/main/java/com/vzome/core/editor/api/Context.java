package com.vzome.core.editor.api;

import org.w3c.dom.Element;

import com.vzome.core.commands.Command;

//    public interface History
//    {
//        void performAndRecord( UndoableEdit edit );
//    }
//    
public interface Context
{
    UndoableEdit createEdit( Element xml );
    
    Command createLegacyCommand( String cmdName ) throws Command.Failure;

    void performAndRecord( UndoableEdit edit );
}
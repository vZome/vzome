package com.vzome.core.editor.api;

import java.util.Map;

import org.w3c.dom.Element;

import com.vzome.core.commands.Command;

public interface Context
{
    UndoableEdit createEdit( Element xml );
    
    Command createLegacyCommand( String cmdName ) throws Command.Failure;

    void performAndRecord( UndoableEdit edit );
    
    boolean doEdit( String action, Map<String,Object> props );
}
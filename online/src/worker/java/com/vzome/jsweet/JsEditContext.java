package com.vzome.jsweet;

import java.util.Map;

import org.w3c.dom.Element;

import com.vzome.core.commands.Command;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.editor.api.UndoableEdit;
import com.vzome.core.editor.api.Context;

public class JsEditContext implements Context
{
    public JsEditContext()
    {}

    @Override
    public UndoableEdit createEdit( Element xml )
    {
        throw new RuntimeException( "unimplemented createEdit" );
    }

    @Override
    public void performAndRecord( UndoableEdit edit )
    {
        throw new RuntimeException( "unimplemented performAndRecord" );
    }

    @Override
    public Command createLegacyCommand( String cmdName ) throws Failure
    {
        throw new RuntimeException( "unimplemented createLegacyCommand" );
    }
    
    @Override
    public boolean doEdit( String action, Map<String,Object> props )
    {
        throw new RuntimeException( "unimplemented doEdit" );
    }
}

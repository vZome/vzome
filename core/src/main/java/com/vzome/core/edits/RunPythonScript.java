
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.edits;

import java.util.Map;
import java.util.Properties;

import org.python.core.PyException;
import org.python.util.InteractiveConsole;
import org.python.util.PythonInterpreter;
import org.w3c.dom.Element;

import com.vzome.api.Command;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.editor.ApiEdit;
import com.vzome.core.editor.EditorModel;
import com.vzome.core.commands.XmlSaveFormat;

public class RunPythonScript extends ApiEdit
{
	private String programText;

    public RunPythonScript( EditorModel editor )
    {
        super( editor );
    }
    
    @Override
    public void configure( Map<String,Object> props ) 
    {
        this.programText = (String) props .get( "script" );
    }

    @Override
    protected String getXmlElementName()
    {
        return "RunPythonScript";
    }

    @Override
    protected void getXmlAttributes( Element element )
    {
        element .setTextContent( XmlSaveFormat .escapeNewlines( programText ) );
    }

    @Override
    protected void setXmlAttributes( Element xml, XmlSaveFormat format )
            throws Failure
    {
        this .programText = xml .getTextContent();
    }
    
    @Override
    public void perform() throws Failure
    {
        // The delegate interface is carefully designed to keep the API public,
        //  but keep the knowledge of ModelRoot, Selection, and ChangeConstructions, etc. here.

        Command.Delegate delegate = this .createDelegate();

        Properties props = new Properties();
        props .setProperty( "python.path", "/Library/Python/2.7/site-packages" );

        // TODO this could be done once, when the python console is opened
        InteractiveConsole.initialize( System.getProperties(), props, new String[0] );

        PythonInterpreter interp = new PythonInterpreter();
        interp .setOut( System.out );
        interp .setErr( System.err );
        interp .set( "javaCommand", new Command( delegate ) );
        try {
            interp .exec( "import vzome" );
            interp .exec( "command = vzome.Command( cmd=javaCommand )" );
            interp .exec( this .programText );
        } catch ( PyException e ) {
            e.printStackTrace();
            throw new Failure( e.toString() );
        } finally {
            interp .close();
        }
        redo();
    }
}


//(c) Copyright 2006, Scott Vorthmann

package org.vorthmann.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import org.vorthmann.j3d.MouseTool;

public interface Controller extends ActionListener
{
    // TODO replace ErrorChannel with JDK logging
    public interface ErrorChannel
    {
        void reportError( String errorCode, Object[] arguments );
        
        void clearError();
    }

    String USER_ERROR_CODE = "user.command.error";

    String UNKNOWN_ERROR_CODE = "unknown.exception";
    
    String UNKNOWN_ACTION = "unknown.action";
    
    String UNKNOWN_COMMAND = "unknown.command";

    void setErrorChannel( ErrorChannel errors );
    
    void setNextController( Controller controller );
    
    String[] getCommandList( String listName );

    boolean[] enableContextualCommands( String[] menu, MouseEvent e );

    void actionPerformed( ActionEvent e );

    void doAction( String action, ActionEvent e ) throws Exception;

    boolean[] getCommandListDefaultStates( String string );

    void doFileAction( String command, File file );
    
    void doScriptAction( String command, String script );

    void repaintGraphics( String panelName, Graphics graphics, Dimension size );

	String getProperty(String string);

	void setProperty( String cmd, Object value );
	
    boolean propertyIsTrue( String propName );

    boolean userHasEntitlement( String propName );

    void addPropertyListener( PropertyChangeListener listener );

    void removePropertyListener( PropertyChangeListener listener );

    Controller getSubController( String string );
    
    MouseTool getMouseTool();
}
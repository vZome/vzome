
package com.vzome.desktop.api;

import java.beans.PropertyChangeListener;
import java.io.File;

/**
 * Controller portion of model-view-controller architecture.
 * 
 * MVC principles in vZome:
 * 
 *   - UI code can know other UI classes, preferably top-down only (no knowledge of parent context)
 *   - UI code only knows this generic Controller interface
 *   - UI code gets Controllers using Controller .getSubController()
 *   - UI code cannot know of any specific Controller subclasses or any model classes
 *   - Controller code cannot know any UI classes; ActionListeners let the controller trigger UI effects
 *   - Controller code can know other Controller subclasses, and Model classes
 *   - Model classes can only know other Model classes, preferably top-down only (no knowledge of parent context)
 *   - Model classes can trigger PropertyChangeEvents, but usually the Controllers do it
 *   
 * @author vorth
 *
 */
public interface Controller
{
    // TODO replace ErrorChannel with JDK logging
    public interface ErrorChannel
    {
        // TODO: DJH: Use typesafe overloads???
        void reportError( String errorCode, Object[] args );
        
        void clearError();
    }

    String USER_ERROR_CODE = "user.command.error";

    String UNKNOWN_ERROR_CODE = "unknown.exception";
    
    String UNKNOWN_ACTION = "unknown.action";
    
    String UNKNOWN_PROPERTY = "unknown.property";

    void setErrorChannel( ErrorChannel errors );
        
    String[] getCommandList( String listName );

    void actionPerformed( Object source, String action );

    boolean[] getCommandListDefaultStates( String string );

    void doFileAction( String command, File file );
    
    void doScriptAction( String command, String script );

	String getProperty(String string);

	void setProperty( String cmd, Object value );
	
    boolean propertyIsTrue( String propName );

    boolean userHasEntitlement( String propName );

    void addPropertyListener( PropertyChangeListener listener );

    void removePropertyListener( PropertyChangeListener listener );

    Controller getSubController( String string );

    void addSubController( String name, Controller sub );
}
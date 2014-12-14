package com.vzome.core.commands;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.ConstructionList;

/**
 * @author Scott Vorthmann
 */
public interface Command
{
    public static final String LOADING_FROM_FILE = "org.vorthmann.zome.editor.Command.LOADING_FROM_FILE";

    public static final String MODEL_ROOT_ATTR_NAME = "org.vorthmann.zome.editor.Command.MODEL_ROOT";

    public static final String FIELD_ATTR_NAME = "org.vorthmann.zome.commands.Command.ALGEBRAIC_FIELD";
    
    public static final String GENERIC_PARAM_NAME = "org.vorthmann.zome.editor.Command.GENERIC_PARAM";

    /**
     * Get the parameter signature for this command.
     * Parameter are an ordered list of pre-existing Constructions.
     * Each parameter has a name (for UI purposes), and an abstract Construction type
     * (Point, Line, Segment, Plane, ...).
     * @return an array of { String, Class } pairs, one for each parameter.
     */
    Object[][] getParameterSignature();

    /**
     * Get the attribute signature for this command.
     * Attributes are an unordered set of primitive values.
     * Each attribute has a name , and a primitive type
     * (GoldenNumber, GoldenVector, Axis, Direction, GoldenMatrix, ...).
     * @return an array of { String, Class } pairs, one for each attribute.
     */
    Object[][] getAttributeSignature();
    
    
    ConstructionList apply( ConstructionList parameters, Map attributes, ConstructionChanges effects )
    	throws Failure;

    
    public interface Registry
    {
        Command getCommand( String name );
    }
    
    public interface FailureChannel
    {
    	void reportFailure( Failure f );
    }

    public static class Failure extends Exception
    {
        private static Logger logger = Logger .getLogger( "org.vorthmann.zome.commands" );
        
            /**
         * 
         */
        public Failure()
        {
            super();
        }
        
        /**
         * @param message
         */
        public Failure( String message )
        {
            super( message );
            if ( logger .isLoggable( Level.FINE ) )
                logger .log( Level.FINE, "command failure: " + message );
        }
        
        /**
         * @param message
         * @param cause
         */
        public Failure( String message, Throwable cause )
        {
            super( message, cause );
            logger .log( Level.INFO, "command failure: " + message, cause );
        }
        
        /**
         * @param cause
         */
        public Failure( Throwable cause )
        {
            super( cause );
            logger .log( Level.INFO, "command failure", cause );
        }
}
}
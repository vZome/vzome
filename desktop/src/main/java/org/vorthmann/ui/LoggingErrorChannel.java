
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package org.vorthmann.ui;

import java.util.logging.Level;
import java.util.logging.Logger;


public class LoggingErrorChannel implements Controller .ErrorChannel
{
    private final Logger logger;

    public LoggingErrorChannel( Logger logger )
    {
        this.logger = logger;
    }

    @Override
    public void reportError( String errorCode, Object[] arguments )
    {
        if ( Controller.USER_ERROR_CODE .equals( errorCode ) )
        {
            errorCode = ((Exception) arguments[0]) .getMessage();
            // don't want a stack trace for a user error
            logger .log( Level.WARNING, errorCode );
        }
        else if ( Controller.UNKNOWN_ERROR_CODE .equals( errorCode ) )
        {
            errorCode = ((Throwable) arguments[0]) .getMessage();
            logger .log( Level.WARNING, "internal error: " + errorCode, ((Throwable) arguments[0]) );
        }
        else
        {
            logger .log( Level.WARNING, "reporting error: " + errorCode, arguments );
            ; // TODO use resources
        }
        logger .severe( "unable to open model file" );
    }

    @Override
    public void clearError()
    {}
}
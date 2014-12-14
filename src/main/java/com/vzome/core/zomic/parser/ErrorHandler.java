package com.vzome.core.zomic.parser;

import java.util.*;

/**
 * Description here.
 * 
 * @author Scott Vorthmann 2003
 */
public interface ErrorHandler {

    int UNKNOWN = -1;

    void parseError( int line, int column, String message );


    public static class Default implements ErrorHandler {
        
        List mErrors = new ArrayList();
        
        public Default( List errors ){
            mErrors = errors;
        }
        
        public Default(){
        }
        
        public void parseError( int line, int column, String message ){
            StringBuffer msg = new StringBuffer( "error: ");
            if( line != UNKNOWN ) {
                msg .append( "line " + line );
                if( column != UNKNOWN )
                    msg .append( ", column " + column );
                msg .append( ": " );
            }
            msg .append( message );
            mErrors .add( msg .toString() );
        }
        
        public String[] getErrors() {
            return (String[]) mErrors.toArray( new String[0] );
        }
    }
}

package com.vzome.core.zomic.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Description here.
 * 
 * @author Scott Vorthmann 2003
 */
public interface ErrorHandler {

	final int UNKNOWN = -1;

    void parseError( int line, int column, String message );

    public static class Default implements ErrorHandler {
        
        final private List<String> mErrors;
        
        public Default( List<String> errors ) {
            mErrors = errors;
        }
        
        public Default(){
			this(new ArrayList<String>());
        }
        
		@Override
        public void parseError( int line, int column, String message ) {
            StringBuilder msg = new StringBuilder( "error: ");
            if( line != UNKNOWN ) {
                msg.append("line ") .append(line);
                if( column != UNKNOWN )
                    msg.append(", column ") .append(column);
                msg .append( ": " );
            }
            msg .append( message );
            mErrors .add( msg .toString() );
        }
        
        public String[] getErrors() {
            return mErrors.toArray( new String[0] );
        }
    }
}

package com.vzome.desktop.controller;

import java.util.stream.Collectors;

import com.vzome.core.editor.EditHistory;

public class UndoRedoController extends DefaultController
{
    private final EditHistory model;

    public UndoRedoController( EditHistory model )
    {
        super();
        this.model = model;
    }

    @Override
    public String getProperty( String key )
    {
        switch (key) {

        case "line.number":
            return Integer .toString( this .model .getNextLineNumber() );

        case "breakpoints":
            return this .model .getBreakpoints() .stream() .map( x -> x .toString() )
                    .collect( Collectors .joining( "," ) );

        default:
            return super.getProperty( key );
        }
    }

    @Override
    public void doAction( String action ) throws Exception
    {
        switch ( action ) {

        case "undo":
            this .model .undo( true );
            break;

        case "redo":
            this .model .redo( true );
            break;

        case "redoToBreakpoint":
            this .model .redoToBreakpoint();
            break;

        case "undoAll":
            this .model .undoAll();
            break;

        case "redoAll":
            this .model .redoAll( - 1 );
            break;

        default:
            if ( action.startsWith( "setBreakpoints." ) )
            {
                String breakpointList = action .substring( "setBreakpoints.".length() ) .trim();
                String[] breakpoints = breakpointList .split( "," );
                int[] breakpointInts = new int[ breakpoints .length ];
                for ( int i = 0; i < breakpointInts.length; i++ ) {
                    String breakpoint = breakpoints[ i ];
                    int lineNum = -1;
                    try {
                        lineNum = Integer.parseInt( breakpoint );
                    } catch (Exception ex) {
                        mErrors.reportError( "'" + breakpoint + "' is not a valid integer. Line number must be a positive integer.", new Object[] {} );
                    }
                    if( lineNum <= 0 ) {
                        mErrors.reportError( "Edit number must be a positive integer.", new Object[] {} );
                    } else {
                        breakpointInts[ i ] = lineNum;
                    }
                }
                this .model .setBreakpoints( breakpointInts );
            }
            else if ( action.startsWith( "redoUntilEdit." ) )
            {
                String editNum = action .substring( "redoUntilEdit.".length() ) .trim();
                // editNum will be "null" if user canceled the numeric input dialog
                // We'll also treat an empty string the same as canceling
                if(! (editNum.equals("null") || editNum.equals("") ) ) {
                    int eNum = -1;
                    try {
                        eNum = Integer.parseInt( editNum );
                    } catch (Exception ex) {
                        mErrors.reportError( "'" + editNum + "' is not a valid integer. Edit number must be a positive integer.", new Object[] {} );
                    }
                    if(eNum <= 0) {
                        mErrors.reportError( "Edit number must be a positive integer.", new Object[] {} );
                    } else {
                        this .model .redoAll( eNum );
                    }
                }
            }
            else
                super .doAction( action );
            break;
        }
    }

}

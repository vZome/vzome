package org.vorthmann.zome.app.impl;

import java.awt.event.ActionEvent;

import org.vorthmann.ui.Controller;
import org.vorthmann.ui.DefaultController;

import com.vzome.core.editor.EditHistory;

public class UndoRedoController extends DefaultController implements Controller
{
    private final EditHistory model;

    public UndoRedoController( EditHistory model )
    {
        super();
        this.model = model;
    }

    @Override
    public void doAction( String action, ActionEvent e ) throws Exception
    {
        switch ( action ) {

        case "undo":
            this .model .undo( true );
            break;

        case "redo":
            this .model .redo( true );
            break;

        case "undoToBreakpoint":
            this .model .undoToBreakpoint();
            break;

        case "redoToBreakpoint":
            this .model .redoToBreakpoint();
            break;

        case "setBreakpoint":
            this .model .setBreakpoint();
            break;

        case "undoAll":
            this .model .undoAll();
            break;

        case "redoAll":
            this .model .redoAll( - 1 );
            break;

        default:
            if ( action.startsWith( "redoUntilEdit." ) ) {
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
                super .doAction( action, e );
            break;
        }
    }

}


package com.vzome.unity;

import com.vzome.core.editor.DocumentModel;

public class Controller
{
    private final DocumentModel model;
    private final Adapter adapter;

    public Controller( DocumentModel model, Adapter adapter )
    {
        super();
        this.model = model;
        this.adapter = adapter;
    }
    
    public void doAction( String action )
    {
        try {
            switch ( action ) {

            case "undo":
                this .model .getHistoryModel() .undo( true );
                break;

            case "redo":
                this .model .getHistoryModel() .redo( true );
                break;

            case "undoAll":
                this .model .getHistoryModel() .undoAll();
                break;

            case "redoAll":
                this .model .getHistoryModel() .redoAll( - 1 );
                break;

            default:
                this .model .doEdit( action );
                break;
            }
        }
        catch ( Exception e )
        {
            e .printStackTrace();
            this .adapter .logException( e );
        }
    }
}

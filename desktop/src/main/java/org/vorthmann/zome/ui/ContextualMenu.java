
package org.vorthmann.zome.ui;

import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.vzome.desktop.awt.GraphicsController;

public class ContextualMenu extends JPopupMenu
{
    private static final long serialVersionUID = 1L;
    
    private final transient Map<String, JMenuItem> mActions = new HashMap<>();
    
    private String[] mActionNames = null;

    public void enableActions( GraphicsController controller, MouseEvent e )
    {
        if ( mActionNames == null ) {
            mActionNames = new String[ mActions .size() ];
            int i = 0;
            for (String name : mActions .keySet()) {
                mActionNames[ i ] = name;
                ++i;
            }
        }
        boolean[] enables = controller .enableContextualCommands( mActionNames, e );
        for ( int j = 0; j < enables.length; j++ ) {
            JMenuItem item = mActions .get( mActionNames[ j ] );
            item .setEnabled( enables[ j ] );
        }
    }

    @Override
    public JMenuItem add( JMenuItem item )
    {
        String action = item .getActionCommand();
        if ( action != null )
            mActions .put( action, item );
        
        return super.add( item );
    }
}
package org.vorthmann.zome.ui;

import javax.swing.AbstractButton;
import javax.swing.JMenuItem;

public interface ControlActions
{
	AbstractButton setButtonAction( String command, AbstractButton control );
	
	JMenuItem setMenuAction( String command, JMenuItem menuItem );
}
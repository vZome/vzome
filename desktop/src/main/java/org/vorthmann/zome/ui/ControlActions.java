package org.vorthmann.zome.ui;

import javax.swing.AbstractButton;
import javax.swing.JMenuItem;

import com.vzome.desktop.api.Controller;

public interface ControlActions
{
	AbstractButton setButtonAction( String command, Controller controller, AbstractButton control );
	
	JMenuItem setMenuAction( String command, Controller controller, JMenuItem menuItem );
}
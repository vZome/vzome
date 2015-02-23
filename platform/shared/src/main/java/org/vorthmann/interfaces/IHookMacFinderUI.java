
//(c) Copyright 2015, Scott Vorthmann.  All rights reserved.

package org.vorthmann.interfaces;

public interface IHookMacFinderUI
{
	void openFile( java.io.File file );
	
	void newActivation( String[] args, java.net.URL codebase );
	
	void about();
	
	boolean quit();
}

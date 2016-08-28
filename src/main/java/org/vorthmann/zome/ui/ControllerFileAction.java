
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package org.vorthmann.zome.ui;

import java.awt.FileDialog;
import java.io.File;

import org.vorthmann.ui.Controller;

public class ControllerFileAction extends FileAction
{
    private final Controller mController;
    private final String mCommand;

    public ControllerFileAction( FileDialog chooser, boolean open, String command, String extension, Controller controller )
    {
        super( chooser, open, extension, controller .getProperty( "file-dialog-title." + command ) );

        mController = controller;
        mCommand = command;
    }

    @Override
    protected void actOnFile( File file )
    {
        mController .doFileAction( mCommand, file );
    }
}

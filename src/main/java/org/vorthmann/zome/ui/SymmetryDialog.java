
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package org.vorthmann.zome.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.vorthmann.ui.Configuration;
import org.vorthmann.ui.Controller;

public class SymmetryDialog extends EscapeDialog
{
	private final OrbitPanel availableOrbitsPanel, snapOrbitsPanel;
	
    public SymmetryDialog( Frame frame, Configuration config )
    {
        super( frame, "Direction Configuration", true );
        {
            Container content = getContentPane();
            content .setLayout( new BorderLayout() );
//            {
//                JCheckBox checkbox = new JCheckBox( "Use graphical views" );
//                checkbox .addActionListener( controller );
//                checkbox .setActionCommand( "toggleOrbitViews" );
//                boolean setting = "true" .equals( controller .getProperty( "useGraphicalViews" ) );
//                checkbox .setSelected( setting );
//                content .add( checkbox, BorderLayout.NORTH );
//            }
            {
                JPanel mainPanel = new JPanel();
                mainPanel .setLayout( new BoxLayout( mainPanel, BoxLayout.LINE_AXIS ) );
                {
                	availableOrbitsPanel = new OrbitPanel( config, null, "availableOrbits", false );
                	availableOrbitsPanel .setBorder( BorderFactory .createTitledBorder( "available directions" ) );
                    mainPanel .add( availableOrbitsPanel );
                }
                {
                	snapOrbitsPanel = new OrbitPanel( config, "availableOrbits", "snapOrbits", false );
                	snapOrbitsPanel .setBorder( BorderFactory .createTitledBorder( "snap directions" ) );
                    mainPanel .add( snapOrbitsPanel );
                }
                content .add( mainPanel, BorderLayout.CENTER );
            }
        }
//        setContentPane( new SymmetryPanel( controller ) );
        
        setSize( new Dimension( 600, 250 ) );
        setLocationRelativeTo( frame );
        
        new SwingWorker<Controller, Object>()
        {
			@Override
			protected Controller doInBackground() throws Exception
			{
				return config .asController();
			}
			
			@Override
			protected void done()
			{
				try {
					setController( get() );
				} catch ( InterruptedException | ExecutionException e ) {
					e.printStackTrace();
				}
				super.done();
			}
		} .execute();
    }
    
    public void setController( Controller controller )
    {
    	availableOrbitsPanel .setController( controller );
    	snapOrbitsPanel .setController( controller );
    }
}

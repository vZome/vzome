/*
 * Created on Apr 11, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.vorthmann.zome.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.vecmath.Quat4d;

import org.vorthmann.j3d.MouseToolDefault;
import org.vorthmann.j3d.Tool;
import org.vorthmann.j3d.Trackball;
import org.vorthmann.ui.Controller;

/**
 * @author vorth
 *
 * So far, just a stripped-down ZomicEditorPanel with names changed.
 */
public class PythonConsolePanel extends JPanel implements Tool {

    private final FileDialog mFileChooser;

	private static final String OPEN = "Select a .py file to open";
	
	private static final String SAVE = "Save a .py file";

    private final JTextArea mTextArea = new JTextArea();

    private static final String PYTHON_HINT = "\n# Type or paste a Python script here, \n" +
                                            "#  then select all or part and execute it.\n";
    

    public abstract class PythonOutputAction implements ActionListener {

        public abstract void outputScript( String script );

        public void actionPerformed( ActionEvent e ) {
            String script = mTextArea .getSelectedText();
            if ( script  != null && script .length() > 0 )
                outputScript( script );
            mTextArea .requestFocus();
        }
    }

    public abstract class PythonInputAction implements ActionListener {

        public abstract String inputScript();

        public void actionPerformed( ActionEvent e ) {
            String script = inputScript();
            if ( script  != null && script .length() > 0 ) {
                int start = mTextArea .getSelectionStart();
                mTextArea .replaceSelection( script );
//                int end = mTextArea .getSelectionEnd();
                mTextArea .setSelectionStart( start );
            }
            mTextArea .requestFocus();
        }
    };

    private String readFromFile()
    {
        mFileChooser .setTitle( OPEN );
		mFileChooser .setMode( FileDialog .LOAD );
        mFileChooser .setVisible( true );
        String fileName = mFileChooser .getFile();
        if ( fileName != null ) {
            File file = new File( mFileChooser .getDirectory(), fileName );
			String result = readTextFromFile( file );
            if ( result == null ) {
                JOptionPane .showMessageDialog( PythonConsolePanel.this,
                    "An exception prevented successful reading of the file.",
                   "File read error", JOptionPane.ERROR_MESSAGE );
                return null;
            }
            else
                return result;
        }
        return null;
    }
    

    private static String readTextFromFile( File file )
    {
        try {
            InputStream input = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int num;
            while ( ( num = input .read( buf, 0, 1024 )) > 0 )
                    out .write( buf, 0, num );
            return new String( out .toByteArray() );
        }
        catch (IOException exc) {
            exc.printStackTrace();
            return null;
        }
    }

    
    private void writeToFile( String script )
    {
		mFileChooser .setTitle( SAVE );
		mFileChooser .setMode( FileDialog .SAVE );
		mFileChooser .setVisible( true );
		String fileName = mFileChooser .getFile();
		if ( fileName != null ) {
			File file = new File( mFileChooser .getDirectory(), fileName );
            try {
                Writer out = new FileWriter( file );
                out .write( script );
                out .close();
            }
            catch (Exception exc) {
                JOptionPane .showMessageDialog( PythonConsolePanel.this,
                    "An exception prevented successful writing of the file.",
                   "File save error", JOptionPane.ERROR_MESSAGE );
                exc.printStackTrace();
            }
        }
    }
            
    private final Controller mController;
    
    
    /**
     * @param viewer
     */
    public PythonConsolePanel( Frame frame, Controller controller )
    {
        mController = controller;
        
        mFileChooser = new FileDialog( frame, OPEN );

        mTextArea .setTabSize( 4 );
        JScrollPane areaScrollPane = new JScrollPane(mTextArea);
        areaScrollPane .setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED );
        areaScrollPane .setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );

        mTextArea .setText( PYTHON_HINT );
        mTextArea .selectAll();

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout( new BoxLayout( controlPanel, BoxLayout.Y_AXIS ) );

        JPanel upDownPanel = new JPanel();
        upDownPanel.setLayout( new BoxLayout( upDownPanel, BoxLayout.X_AXIS ) );

        JButton button = new JButton( "run" );
        Dimension size = button .getPreferredSize();
        size .width = 50;
        button .setPreferredSize( size );
         button .addActionListener( new PythonOutputAction() {
            public void outputScript( String script ){
                mController .doScriptAction( "runPythonScript" , script );
            }
        } );
        button .setToolTipText( "Execute the selection on the model." );
        upDownPanel .add( button );
        
        button = new JButton( "load" );
        button .addActionListener( new PythonInputAction() {
            public String inputScript(){
                return readFromFile();
            }
        } );
        button .setToolTipText( "Replace the selection from the script file." );
        upDownPanel .add( button );
        button = new JButton( "save" );
        button .addActionListener( new PythonOutputAction() {
            public void outputScript( String script ){
                writeToFile( script );
            }
        } );
        button .setToolTipText( "Save the selection to the script file." );
        upDownPanel .add( button );
        controlPanel .add( upDownPanel );

        JPanel top = new JPanel();
        BoxLayout box = new BoxLayout( top, BoxLayout.Y_AXIS );
        top .setLayout( box );
        top .add( controlPanel );
        top .add( areaScrollPane );
        top .setBorder(
                new TitledBorder(
                    null,
                    "script editing",
                    4,
                    2,
                    new java.awt.Font("Dialog", 1, 12),
                    new java.awt.Color(0, 0, 0)));

        controlPanel = new JPanel();
        controlPanel .setLayout( new BoxLayout( controlPanel, BoxLayout.X_AXIS ) );

        top .setMinimumSize( new Dimension( 300, 300 ) );
        
        setLayout( new BorderLayout() );
        add( top, BorderLayout.CENTER );
        
        setPreferredSize( new Dimension( 500, 800 ) );
    }

    public String getToolName() {
        return "script";
    }

    public void toolSelected()
    {
        mTextArea .requestFocus();
    }
    
    public void toolUnselected(){}

	public MouseToolDefault getMouseTool()
	{
		return new Trackball()
        {
            protected void trackballRolled( Quat4d roll )
            {
                // TODO remove this?
            }
            
        };
	}

}

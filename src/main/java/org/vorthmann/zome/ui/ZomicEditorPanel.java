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
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ZomicEditorPanel extends JPanel implements Tool {

    private final FileDialog mFileChooser;

	private static final String OPEN = "Select a .zomic file to open";
	
	private static final String SAVE = "Save a .zomic file";

    private final JTextArea mTextArea = new JTextArea();

    private static final String ZOMIC_HINT = "\n/* Type or paste a Zomic script here, \n" +
                                            "then select all or part and execute it. */\n";
    

//    private Document mDocument;
    
//    private class RecorderOutput implements Recorder.Output
//    {        
//        public void statement( Anything stmt )
//        {
//            StringWriter sw = new StringWriter();
//            PrintVisitor pv = new PrintVisitor( new PrintWriter( sw ) );
//            try{
//                stmt .accept( pv );
//                sw .close();
//                mRecordArea .append( sw .toString() );
//            } catch ( Exception e ) {
//                e.printStackTrace();
//                mRecordArea .append( "// problem with Zomic capture; see console" );
//            }
//            mRecordArea .repaint();
//        }
//    }

    public abstract class ZomicOutputAction implements ActionListener {

        public abstract void outputScript( String script );

        public void actionPerformed( ActionEvent e ) {
            String script = mTextArea .getSelectedText();
            if ( script  != null && script .length() > 0 )
                outputScript( script );
            mTextArea .requestFocus();
        }
    }

    public abstract class ZomicInputAction implements ActionListener {

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

	//	int returnVal = mFileChooser.showOpenDialog( ZomicEditorPanel.this );
    //    if (returnVal == JFileChooser.APPROVE_OPTION) {
    //        File file = mFileChooser.getSelectedFile();
	//File file = mFileChooser.getSelectedFile();

        mFileChooser .setTitle( OPEN );
		mFileChooser .setMode( FileDialog .LOAD );
        mFileChooser .setVisible( true );
        String fileName = mFileChooser .getFile();
        if ( fileName != null ) {
            File file = new File( mFileChooser .getDirectory(), fileName );
			String result = readTextFromFile( file );
            if ( result == null ) {
                JOptionPane .showMessageDialog( ZomicEditorPanel.this,
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
//        int returnVal = mFileChooser.showSaveDialog( ZomicEditorPanel.this );
//        if (returnVal == JFileChooser.APPROVE_OPTION) {
//            File file = mFileChooser.getSelectedFile();
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
                JOptionPane .showMessageDialog( ZomicEditorPanel.this,
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
    public ZomicEditorPanel( Frame frame, Controller controller )
    {
//        mDocument = document;
        mController = controller;
        
        mFileChooser = new FileDialog( frame, OPEN );

        //textArea.setFont(new Font("Serif", Font.ITALIC, 16));
        //mTextArea.setLineWrap(true);
        //mTextArea.setWrapStyleWord(true);
        mTextArea .setTabSize( 4 );
        //mTextArea .setPreferredSize( new Dimension( 300, 300 ) );
        JScrollPane areaScrollPane = new JScrollPane(mTextArea);
        areaScrollPane .setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED );
        areaScrollPane .setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        //areaScrollPane.setMinimumSize(new Dimension(300, 300));

        mTextArea .setText( ZOMIC_HINT );
        mTextArea .selectAll();

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout( new BoxLayout( controlPanel, BoxLayout.Y_AXIS ) );
//        controlPanel.setBorder(
//            BorderFactory.createCompoundBorder(
//                BorderFactory.createEmptyBorder(5,5,5,5),
//                controlPanel.getBorder()));

        JPanel upDownPanel = new JPanel();
        upDownPanel.setLayout( new BoxLayout( upDownPanel, BoxLayout.X_AXIS ) );

        JButton button = new JButton( "run" );
        Dimension size = button .getPreferredSize();
        size .width = 50;
        button .setPreferredSize( size );
         button .addActionListener( new ZomicOutputAction() {
            public void outputScript( String script ){
                mController .doScriptAction( "runZomicScript" , script );
            }
        } );
        button .setToolTipText( "Execute the selection on the model." );
        upDownPanel .add( button );
        
        button = new JButton( "load" );
        button .addActionListener( new ZomicInputAction() {
            public String inputScript(){
                return readFromFile();
            }
        } );
        button .setToolTipText( "Replace the selection from the script file." );
        upDownPanel .add( button );
        button = new JButton( "save" );
        button .addActionListener( new ZomicOutputAction() {
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


//        JPanel modelPanel = new JPanel();
//        modelPanel.setLayout( new BoxLayout( modelPanel, BoxLayout.Y_AXIS ) );

//        modelPanel .add( button );
//
//        button = new JButton( ">" );
//        button .setPreferredSize( size );
//        button .addActionListener( new ZomicInputAction() {
//            public String inputScript(){
//                return ""; //readFromModel();
//            }
//        } );
//        button .setToolTipText( "Replace the selection with a script extracted from the model." );
//        modelPanel .add( button );

        controlPanel = new JPanel();
        controlPanel .setLayout( new BoxLayout( controlPanel, BoxLayout.X_AXIS ) );


//        mDocument .getRecorder() .setOutput( mRecorder );
        
        
//        JCheckBox checkbox = new JCheckBox( "recording", mRecorder != null );
//        //checkbox .setHorizontalAlignment( SwingConstants.LEFT );
//        checkbox.addActionListener(new ActionListener() {
//
//            public void actionPerformed( ActionEvent ae )
//            {
//                if ( mRecorder == null )
//                    mRecorder = new RecorderOutput();
//                else
//                    mRecorder = null;
//                mZomeApp .getRecorder() .setOutput( mRecorder );
//            }
//
//        });        
//		controlPanel .add( checkbox );
        
        top .setMinimumSize( new Dimension( 300, 300 ) );
        
        setLayout( new BorderLayout() );
        add( top, BorderLayout.CENTER );
        
        setPreferredSize( new Dimension( 500, 800 ) );
    }

    /* (non-Javadoc)
     * @see org.vorthmann.j3d.Tool#getToolName()
     */
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

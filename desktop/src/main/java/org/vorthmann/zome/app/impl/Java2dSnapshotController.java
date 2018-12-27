package org.vorthmann.zome.app.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;

import org.vorthmann.ui.DefaultController;

import com.vzome.core.exporters2d.Java2dExporter;
import com.vzome.core.exporters2d.Java2dSnapshot;
import com.vzome.core.exporters2d.PDFExporter;
import com.vzome.core.exporters2d.PostScriptExporter;
import com.vzome.core.exporters2d.SVGExporter;
import com.vzome.core.exporters2d.SnapshotExporter;

/**
 * Renders out to PDF, Postscript, or SVG.
 * 
 * TODO extract model from controller, to support headless export.
 * 
 * @author vorth
 */
public class Java2dSnapshotController extends DefaultController
{    
    private Java2dExporter exporter;
    
    private boolean current;
        
    private Java2dSnapshot snapshot = new Java2dSnapshot( new ArrayList<>(), new ArrayList<>() );

    
    public Java2dSnapshotController( Java2dExporter exporter )
    {
        this.setExporter( exporter );
    }

    private final static String[] DRAW_STYLES = new String[]{ "outlined shapes", "shaded shapes", "shaded, outlined shapes", "colored lines", "black lines" };
    
    @Override
    public String[] getCommandList( String listName )
    {
        if ( "draw.styles" .equals( listName ) )
        {
            return DRAW_STYLES;
        }
        else
            return super .getCommandList( listName );
    }

    @Override
    public String getProperty( String propName )
    {
        if ( "showBackground" .equals( propName ) )
            return Boolean .toString( snapshot.showBackground() );
        if ( "lineDrawing" .equals( propName ) )
            return Boolean .toString( snapshot .isLineDrawing() );
        if ( "monochrome" .equals( propName ) )
            return Boolean .toString( snapshot .isMonochrome() );
        if ( "drawStyle" .equals( propName ) )
        {
            if ( this .snapshot .isLineDrawing() )
                if ( this .snapshot .isMonochrome() )
                    return "black lines";
                else
                    return "colored lines";
            else if ( this .snapshot .hasLighting() )
                if ( this .snapshot .isDoOutlines() )
                    return "shaded, outlined shapes";
                else
                    return "shaded shapes";
            else
                return "outlined shapes";
        }
        return super.getProperty( propName );
    }

    @Override
    public void actionPerformed( ActionEvent e )
    {
        String action = e .getActionCommand();

        if ( action .equals( "refresh" ) )
        {
            current = false;
        }
        else if ( action .equals( "toggleBackground" ) )
        {
            current = false;
            snapshot .toggleBackground();
        }
        else if ( action .equals( "toggleLineDrawing" ) )
        {
            current = false;
            snapshot .toggleLineDrawing();
        }
        else if ( action .equals( "toggleMonochrome" ) )
        {
            snapshot .toggleMonochrome();
            if ( snapshot .isLineDrawing() )
                current = false;
        }
        else if ( action .startsWith( "setDrawStyle." ) )
        {
            String drawStyle = action .substring( "setDrawStyle." .length() );
            if ( "black lines" .equals(  drawStyle ) ) {
                this .snapshot .setMonochrome( true );
                this .snapshot .setLineDrawing( true );
            }
            else if ( "colored lines" .equals(  drawStyle ) ) {
                this .snapshot.setMonochrome( false );
                this .snapshot .setLineDrawing( true );
            }
            else if ( "outlined shapes" .equals(  drawStyle ) ) {
                this .snapshot .setLineDrawing( false );
                this .snapshot .setDoLighting( false );
                this .snapshot .setDoOutlines( true );
            }
            else if ( "shaded shapes" .equals(  drawStyle ) ) {
                this .snapshot .setLineDrawing( false );
                this .snapshot .setDoLighting( true );
                this .snapshot .setDoOutlines( false );
            }
            else if ( "shaded, outlined shapes" .equals(  drawStyle ) ) {
                this .snapshot .setLineDrawing( false );
                this .snapshot .setDoLighting( true );
                this .snapshot .setDoOutlines( true );
            }
            current = false;
        }
        else
            super.actionPerformed( e );
    }
    
//    private void refresh()
//    {
//
//        mSnapshot .setBackgroundColor( new java.awt.Color( colors .getColor( Colors.BACKGROUND ) .getRGB() ) );    
//    }

    @Override
    public void doFileAction( String command, File file )
    {
        try {
            SnapshotExporter snapshotExporter = null;
            switch (command) {
                case "export.2d.pdf":
                    snapshotExporter = new PDFExporter();
                    break;
                case "export.2d.ps":
                    snapshotExporter = new PostScriptExporter();
                    break;
                case "export.2d.svg":
                    snapshotExporter = new SVGExporter();
                    break;
                default:
                    break;
            }
            if ( snapshotExporter == null )
                return;
            // A try-with-resources block closes the resource even if an exception occurs
            try (Writer out = new FileWriter( file )) {
                snapshotExporter .export( this.snapshot, out );
            }
            openApplication( file );
        } catch ( Exception e ) {
            mErrors .reportError( UNKNOWN_ERROR_CODE, new Object[]{ e } );
        }
    }

    @Override
    public void repaintGraphics( String panelName, Graphics graphics, Dimension size )
    {
        if ( ! current )
            try {
                snapshot .clear();
                this .exporter .doExport( new File(""), null, size.height, size.width );
                current = true;
            } catch ( Exception e1 ) {
                mErrors .reportError( UNKNOWN_ERROR_CODE, new Object[]{ e1 } );
            }
        
        Graphics2D g2d = (Graphics2D) graphics;

        g2d .setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2d .clearRect( 0, 0, (int) size .getWidth(), (int) size .getHeight() );

        if ( this .snapshot .showBackground() ) {
            g2d .setPaint( this .snapshot .getBackgroundColor() );
            Rectangle2D rect = new Rectangle2D.Float();
            rect .setFrame( new Point2D.Float(), size );
            g2d .fill( rect );
        }

        if ( this .snapshot .isLineDrawing() ) {
            g2d .setStroke( new BasicStroke( 3*snapshot.getStrokeWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
            g2d .setPaint( java.awt.Color.BLACK );
            
            for ( Java2dSnapshot.LineSegment line : snapshot.getLines() ) {
                if ( snapshot.isMonochrome() )
                    if ( snapshot.showBackground() )
                        g2d .setPaint( java.awt.Color.WHITE );
                    else
                        g2d .setPaint( java.awt.Color.BLACK );
                else
                    g2d .setPaint( line .getColor() );
                g2d .draw( line .getPath() );
            }
        }
        else {
            g2d .setStroke( new BasicStroke( snapshot.getStrokeWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
            for ( Java2dSnapshot.Polygon poly : snapshot.getPolygons() ){
                g2d .setPaint( poly .getColor() );
                g2d .fill( poly .getPath() );
                if ( this .snapshot .isDoOutlines() ) {
                    g2d .setPaint( java.awt.Color.BLACK );
                    g2d .draw( poly .getPath() );
                }
            }
        }

        g2d.dispose(); //clean up
    }

    public void setExporter( Java2dExporter exporter )
    {
        current = false;
        this.exporter = exporter;
        exporter .setSnapshot( this .snapshot );
    }

    public Color getBackgroundColor()
    {
        if ( this .snapshot .showBackground() )
            return this .exporter .getBackgroundColor();
        else
            return null;
    }
}



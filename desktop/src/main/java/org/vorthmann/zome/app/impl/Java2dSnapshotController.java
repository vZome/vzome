package org.vorthmann.zome.app.impl;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;

import org.vorthmann.ui.DefaultController;

import com.vzome.core.editor.DocumentModel;
import com.vzome.core.exporters2d.Java2dSnapshot;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.Camera;
import com.vzome.core.viewing.Lights;

/**
 * Renders out to PDF, Postscript, or SVG.
 * 
 * TODO extract model from controller, to support headless export.
 * 
 * @author vorth
 */
public class Java2dSnapshotController extends DefaultController
{    
    private boolean current = false;
 
    private boolean showBackground = false, monochrome = true, lineDrawing = false, doLighting = true, outlinePanels = true;

    private transient Java2dSnapshot snapshot;

    private final DocumentModel document;

    private Camera camera;

    private Lights lights;

    private RenderedModel model;

    
    public Java2dSnapshotController( DocumentModel document, Camera camera, Lights lights, RenderedModel model )
    {
        this .document = document;
        this .camera = camera;
        this .lights = lights;
        this .model = model;
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
            return Boolean .toString( this .showBackground );
        if ( "lineDrawing" .equals( propName ) )
            return Boolean .toString( this .lineDrawing );
        if ( "monochrome" .equals( propName ) )
            return Boolean .toString( this .monochrome );
        if ( "drawStyle" .equals( propName ) )
        {
            if ( this .lineDrawing )
                if ( this .monochrome )
                    return "black lines";
                else
                    return "colored lines";
            else if ( this .doLighting )
                if ( this .outlinePanels )
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
            this .showBackground = ! this .showBackground;
        }
        else if ( action .equals( "toggleLineDrawing" ) )
        {
            current = false;
            this .lineDrawing = ! this .lineDrawing;
        }
        else if ( action .equals( "toggleMonochrome" ) )
        {
            this .monochrome = ! this .monochrome;
            if ( this .lineDrawing )
                current = false;
        }
        else if ( action .startsWith( "setDrawStyle." ) )
        {
            String drawStyle = action .substring( "setDrawStyle." .length() );
            if ( "black lines" .equals(  drawStyle ) ) {
                this .monochrome = true;
                this .lineDrawing = true;
            }
            else if ( "colored lines" .equals(  drawStyle ) ) {
                this .monochrome = false;
                this .lineDrawing = true;
            }
            else if ( "outlined shapes" .equals(  drawStyle ) ) {
                this .lineDrawing = false;
                this .doLighting = false;
                this .outlinePanels = true;
            }
            else if ( "shaded shapes" .equals(  drawStyle ) ) {
                this .lineDrawing = false;
                this .doLighting = true;
                this .outlinePanels = false;
            }
            else if ( "shaded, outlined shapes" .equals(  drawStyle ) ) {
                this .lineDrawing = false;
                this .doLighting = true;
                this .outlinePanels = true;
            }
            current = false;
        }
        else
            super.actionPerformed( e );
    }
    
    @Override
    public void doFileAction( String command, File file )
    {
        try {
            String format = command .substring( "export.2d." .length() ) .toLowerCase();
            this .document .export2d( snapshot, format, file, this .outlinePanels, this .monochrome );
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
                snapshot = this .document .capture2d( this .model, size.height, size.width, this .camera,
                        this .lights, this .lineDrawing, this .doLighting );
                current = true;
            } catch ( Exception e1 ) {
                mErrors .reportError( UNKNOWN_ERROR_CODE, new Object[]{ e1 } );
            }
        
        Graphics2D g2d = (Graphics2D) graphics;

        g2d .setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2d .clearRect( 0, 0, (int) size .getWidth(), (int) size .getHeight() );

        if ( this .showBackground ) {
            g2d .setPaint( this .snapshot .getBackgroundColor() );
            Rectangle2D rect = new Rectangle2D.Float();
            rect .setFrame( new Point2D.Float(), size );
            g2d .fill( rect );
        }

        if ( this .lineDrawing ) {
            g2d .setStroke( new BasicStroke( 3*snapshot.getStrokeWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
            g2d .setPaint( java.awt.Color.BLACK );
            
            for ( Java2dSnapshot.LineSegment line : snapshot.getLines() ) {
                if ( this .monochrome )
                    if ( this .showBackground )
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
                if ( this .outlinePanels ) {
                    g2d .setPaint( java.awt.Color.BLACK );
                    g2d .draw( poly .getPath() );
                }
            }
        }

        g2d.dispose(); //clean up
    }


    public void setScene( Camera camera, Lights lights, RenderedModel model )
    {
        this .camera = camera;
        this .lights = lights;
        this .model = model;
        this .current = false;
    }
}



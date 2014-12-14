
//(c) Copyright 2011, Scott Vorthmann.

package com.vzome.core.exporters;

import java.awt.Dimension;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.media.jai.JAI;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.editor.DocumentModel;
import com.vzome.core.editor.PageModel;
import com.vzome.core.editor.Snapshot;
import com.vzome.core.math.DomUtils;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.Lights;

public class ZomespaceExporter
{
    private final File file;

    public ZomespaceExporter( File file )
    {
        this .file = file;
    }
    
    private final static String FOLDER = "zomespaceContribution/";

    public void exportArticle( DocumentModel model, final Colors colors, final Lights sceneLighting, final Document vZomeXML, String edition, String version ) throws IOException, TransformerException
    {
        // Create the ZIP file
        FileOutputStream fout = new FileOutputStream( file );
        ZipOutputStream out = new ZipOutputStream( fout );
                
        boolean snapshotsSeparate = false;

        Element modelRoot = vZomeXML .createElement( "model" );
        DomUtils .addAttribute( modelRoot, "format", "1" );

        Element e = vZomeXML .createElement( "modeler" );
        e .appendChild( vZomeXML .createTextNode( edition ) );
        DomUtils .addAttribute( e, "version", version );
        modelRoot .appendChild( e );

        Element snaps = vZomeXML .createElement( "snapshots" );
        if ( snapshotsSeparate )
            modelRoot .appendChild( snaps );

        Element steps = vZomeXML .createElement( "sequence" );
        
        Map usedSnapshots = new HashMap();

        int pageNum = 0;
        Element lastStep = null;
        for ( Iterator iterator = model .getLesson() .iterator(); iterator .hasNext(); )
        {
            PageModel page = (PageModel) iterator .next();
            Integer snapshot = new Integer( page .getSnapshot() );

            Element step = vZomeXML .createElement( "step" );
            steps .appendChild( step );
            DomUtils .addAttribute( step, "number", Integer .toString( pageNum + 1 ) );
            DomUtils .addAttribute( step, "algorithm", "true" );

            e = vZomeXML .createElement( "title" );
            e .appendChild( vZomeXML .createTextNode( page .getTitle() ) );
            step .appendChild( e );

            e = vZomeXML .createElement( "description" );
            e .appendChild( vZomeXML .createTextNode( page .getContent() ) );
            step .appendChild( e );

            Element snap = (Element) usedSnapshots .get( snapshot );
            if ( snap == null )
            {
                final Element newSnap = vZomeXML .createElement( "snapshot" );
                if ( snapshotsSeparate )
                    snaps .appendChild( newSnap );
                DomUtils .addAttribute( newSnap, "number", snapshot .toString() );
                usedSnapshots .put( snapshot, newSnap );

                model .actOnSnapshot( snapshot .intValue(), new Snapshot.SnapshotAction()
                {
					public void actOnSnapshot( RenderedModel snapshotModel )
					{
		                exportPartsData( snapshotModel, newSnap );
					}
				});

                model .actOnSnapshot( snapshot .intValue(), new Snapshot.SnapshotAction()
                {
					public void actOnSnapshot( RenderedModel snapshotModel )
					{
                        Element e = vZomeXML .createElement( "rendering" );
                        Web3dExporter exporter = new Web3dExporter( sceneLighting .getBackgroundColor(), snapshotModel );
                        StringWriter writer = new StringWriter();
                        try {
							exporter .doExport( new File( "unused" ), writer, new Dimension( 0,0 ) );
	                        writer .close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
                        e .appendChild( vZomeXML .createTextNode( writer .toString() ) );
                        newSnap .appendChild( e );
					}
				});

                snap = newSnap;
            }
            if ( snapshotsSeparate )
                DomUtils .addAttribute( step, "snapshot", snapshot .toString() );
            else
            {
                step .appendChild( DomUtils .getChild( snap, 0 ) .cloneNode( true ) );
                step .appendChild( DomUtils .getChild( snap, 1 ) .cloneNode( true ) );
                step .appendChild( DomUtils .getChild( snap, 2 ) .cloneNode( true ) );
                step .appendChild( DomUtils .getChild( snap, 3 ) .cloneNode( true ) );
            }

            if ( snapshotsSeparate )
            {
                e = vZomeXML .createElement( "view" );
                e .appendChild( vZomeXML .createTextNode( "more json here for webgl viewing parameters" ) );
                step .appendChild( e );
            }
            else
            {
                lastStep = step;
            }

            
            // Add ZIP entry to output stream.
            out .putNextEntry( new ZipEntry( FOLDER + "step_" + (++pageNum) + ".jpg" ) );
            RenderedImage thumbnail = (RenderedImage) page .getThumbnail();
            JAI .create( "encode", thumbnail, out, "JPEG", null );
            // Complete the entry
            out .closeEntry();
        }
        if ( snapshotsSeparate )
        {
            Document doc = (Document) vZomeXML .cloneNode( true );
            out .putNextEntry( new ZipEntry( FOLDER + "source.vZome" ) );
            DomUtils .serialize( doc, out );
            out .closeEntry();
        }
        else
        {
            modelRoot .appendChild( DomUtils .getChild( lastStep, 0 ) .cloneNode( true ) ); // title
            modelRoot .appendChild( DomUtils .getChild( lastStep, 1 ) .cloneNode( true ) ); // description

            e = vZomeXML .createElement( "language" );
            e .appendChild( vZomeXML .createTextNode( "English" ) );
            modelRoot .appendChild( e );

            modelRoot .appendChild( DomUtils .getChild( lastStep, 2 ) .cloneNode( true ) ); // strut_count
            modelRoot .appendChild( DomUtils .getChild( lastStep, 3 ) .cloneNode( true ) ); // node_count

            Element sf = vZomeXML .createElement( "model_fingerprint" );
            sf .appendChild( vZomeXML .createTextNode( "nothingYet" ) );
            modelRoot .appendChild( sf );

            modelRoot .appendChild( DomUtils .getChild( lastStep, 5 ) .cloneNode( true ) ); // rendering
        }
        modelRoot .appendChild( steps );

        Document doc = (Document) vZomeXML .cloneNode( false );
        doc .appendChild( modelRoot );
        out .putNextEntry( new ZipEntry( FOLDER + "model.xml" ) );
        DomUtils .serialize( doc, out );
        out .closeEntry();

        // Complete the ZIP file
        out .close();
        return;
    }

    private void exportPartsData( RenderedModel snapshotModel, Element snap )
    {
        int numBalls = 0;
        Map[] orbits = new Map[]{ new HashMap(), new HashMap() };
        for ( Iterator rms = snapshotModel .getRenderedManifestations(); rms .hasNext(); )
        {
            RenderedManifestation rm = (RenderedManifestation) rms .next();
            int[] /*AlgebraicNumber*/ len = rm .getStrutLength();
            if ( len == null ) {
                // TODO this will get panels too
                ++ numBalls;
            }
            else {
                boolean flip = rm .reverseOrder(); // part is left-handed
                Polyhedron shape = rm .getShape();
                Direction orbit = shape .getOrbit();
                if ( orbit .isStandard() )
                {
                    String key = orbit .getRZomeLength( len );
                    
                    // faking it so we get some data!  Server may be barfing with no data to chew.
                    key = "1";
                    
                    // a null rZome length means it is nonstandard
                    if ( key != null )
                    {
                        Map orbitHistogram = (Map) orbits[ flip?1:0 ] .get( orbit );
                        if ( orbitHistogram == null )
                        {
                            orbitHistogram = new HashMap();
                            orbits[ flip?1:0 ] .put( orbit, orbitHistogram );
                        }
                        Integer lengthCount = (Integer) orbitHistogram .get( key );
                        if ( lengthCount == null )
                            lengthCount = new Integer( 1 );
                        else
                            lengthCount = new Integer( lengthCount .intValue() + 1 );
                        orbitHistogram .put( key, lengthCount );
                    }
                }
            }
        }

    /*
          <strut_count>
            <strut identifier="B" name="Blue" standard="true">
              <color r="0" g="0" b="255" name="Blue" />
              <shape>rectangle</shape>
              <size>2</size>
              <quantity>12</quantity>
            </strut>
          </strut_count>
     */
        Document doc = snap .getOwnerDocument();
        Element sc = doc .createElement( "strut_count" );
        for ( int i = 0; i < orbits.length; i++ ) {
            for ( Iterator iterator = orbits[i].keySet().iterator(); iterator.hasNext(); ) {
                Direction orbit = (Direction) iterator.next();
                String name = orbit .getName();
                char first = Character .toUpperCase( name .charAt( 0 ) );
                name = first + name .substring( 1 );
                Map histogram = (Map) orbits[i] .get( orbit );
                for ( Iterator iterator2 = histogram .keySet().iterator(); iterator2.hasNext(); ) {
                    Element strut = doc .createElement( "strut" );
                    DomUtils .addAttribute( strut, "identifier", Character .toString( first ) );
                    DomUtils .addAttribute( strut, "name", name );
                    DomUtils .addAttribute( strut, "standard", "true" );

                    Element color = doc .createElement( "color" );
                    strut .appendChild( color );
                    DomUtils .addAttribute( color, "r", "255" );
                    DomUtils .addAttribute( color, "g", "255" );
                    DomUtils .addAttribute( color, "b", "255" );
                    DomUtils .addAttribute( color, "name", name );

                    String key = (String) iterator2.next();
                    Element size = doc .createElement( "size" );
                    size .appendChild( doc .createTextNode( key ) );
                    strut .appendChild( size );
                    Element shape = doc .createElement( "shape" );
                    if ( "Red" .equals( name ) )
                    	shape .appendChild( doc .createTextNode( "pentagon" ) );
                    else if ( "Yellow" .equals( name ) )
                    	shape .appendChild( doc .createTextNode( "triangle" ) );
                    else if ( "Blue" .equals( name ) )
                    	shape .appendChild( doc .createTextNode( "rectangle" ) );
                    strut .appendChild( shape );
                    Integer count = (Integer) histogram .get( key );
                    Element qty = doc .createElement( "quantity" );
                    qty .appendChild( doc .createTextNode( count .toString() ) );
                    strut .appendChild( qty );
                    sc .appendChild( strut );
                }
            }
        }
        snap .appendChild( sc );

        /*
            <node>
              <color r="255" g="255" b="255" name="White" />
              <quantity>8</quantity>
            </node>
         */
        Element nc = doc .createElement( "node_count" );
        Element node = doc .createElement( "node" );
        nc .appendChild( node );
        Element color = doc .createElement( "color" );
        node .appendChild( color );
        DomUtils .addAttribute( color, "r", "255" );
        DomUtils .addAttribute( color, "g", "255" );
        DomUtils .addAttribute( color, "b", "255" );
        DomUtils .addAttribute( color, "name", "White" );
        Element qty = doc .createElement( "quantity" );
        qty .appendChild( doc .createTextNode( Integer .toString( numBalls ) ) );
        node .appendChild( qty );
        snap .appendChild( nc );

        Element e = doc .createElement( "step_fingerprint" );
        e .appendChild( doc .createTextNode( "nothingYet" ) );
        snap .appendChild( e );
    }

}

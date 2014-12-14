package com.vzome.core.exporters;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.RealVector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Strut;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.Lights;
import com.vzome.core.viewing.ViewModel;


public class PdbExporter extends Exporter3d
{
	public PdbExporter( ViewModel scene, Colors colors, Lights lights, RenderedModel model )
	{
	    super( scene, colors, lights, model );
	}


	public void doExport( File directory, Writer writer, Dimension screenSize ) throws IOException
	{
        Map atoms = new HashMap();
        List atomsList = new ArrayList();
        int indices = 0;

        for ( Iterator rms = mModel .getRenderedManifestations(); rms .hasNext(); )
        {
            Manifestation man = ((RenderedManifestation) rms .next()) .getManifestation();
            if ( man instanceof Strut ) {
                AlgebraicVector startLoc = new AlgebraicVector( ((Strut) man) .getLocation() );
                AlgebraicVector endLoc = new AlgebraicVector( ((Strut) man) .getEnd() );
                Atom startAtom = (Atom) atoms .get( startLoc );
                if ( startAtom == null )
                {
                    startAtom = new Atom( startLoc, ++ indices );
                    atoms .put( startLoc, startAtom );
                    atomsList .add( startAtom );
                }
                Atom endAtom = (Atom) atoms .get( endLoc );
                if ( endAtom == null )
                {
                    endAtom = new Atom( endLoc, ++ indices );
                    atoms .put( endLoc, endAtom );
                    atomsList .add( endAtom );
                }
                startAtom .neighbors .add( endAtom );
                endAtom .neighbors .add( startAtom );
            }
        }
        
        AlgebraicField field = mModel .getField();
        // scale things so that a medium blue strut has length 5.0
        final int[] scale = field .createAlgebraicNumber( 4, 6, 1, 0 );
        double scaleFactor = 5.0d / field .evaluateNumber( scale );

        StringBuilder locations = new StringBuilder();
        StringBuilder neighbors = new StringBuilder();
        for (Iterator iterator = atomsList .iterator(); iterator .hasNext(); ) {
            Atom atom = (Atom) iterator .next();
            RealVector rv = atom .location .toRealVector( field );
            System .out .println( atom .location .toString( field ) );
            locations .append( String .format( "HETATM%5d He   UNK  0001     %7.3f %7.3f %7.3f\n",
                new Object[]{ new Integer(atom .index), new Float( rv.x * scaleFactor ), new Float( rv.y * scaleFactor ), new Float( rv.z * scaleFactor ) } ) );
            neighbors .append( String .format( "CONECT%5d", new Object[]{ new Integer( atom .index ) } ) );
            for (Iterator iterator2  = atom .neighbors .iterator(); iterator2 .hasNext(); ) {
                Atom neighbor = (Atom) iterator2 .next();
                neighbors .append( String .format( "%5d", new Object[]{ new Integer( neighbor .index ) } ) );
            }
            neighbors .append( "\n" );
        }
        
        output = new PrintWriter( writer );
        output .println( "HEADER" );
        output .println( "REMARK vZome" );
        output .print( locations );
        output .print( neighbors );
        output .println( "END" );

		output .flush();
	}

    public String getFileExtension()
    {
        return "pdb";
    }

    private class Atom
    {
        public Atom( AlgebraicVector location, int i )
        {
            this .location = location;
            this .index = i;
        }
        AlgebraicVector location;
        int index;
        Set neighbors = new HashSet();
    }
}



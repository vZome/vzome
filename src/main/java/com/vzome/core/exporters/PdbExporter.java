package com.vzome.core.exporters;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
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


	public void doExport( File directory, Writer writer, int height, int width ) throws IOException
	{
        Map<AlgebraicVector, Atom> atoms = new HashMap<>();
        List<Atom> atomsList = new ArrayList<>();
        int indices = 0;

        for (RenderedManifestation rm : mModel) {
            Manifestation man = rm .getManifestation();
            if ( man instanceof Strut ) {
                AlgebraicVector startLoc = ((Strut) man) .getLocation();
                AlgebraicVector endLoc = ((Strut) man) .getEnd();
                Atom startAtom = atoms .get( startLoc );
                if ( startAtom == null )
                {
                    startAtom = new Atom( startLoc, ++ indices );
                    atoms .put( startLoc, startAtom );
                    atomsList .add( startAtom );
                }
                Atom endAtom = atoms .get( endLoc );
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
        final AlgebraicNumber scale = field .createAlgebraicNumber( 4, 6, 1, 0 );
        double scaleFactor = 5.0d / scale .evaluate();

        StringBuilder locations = new StringBuilder();
        StringBuilder neighbors = new StringBuilder();
        for (Atom atom : atomsList) {
            RealVector rv = atom .location .toRealVector();
            System .out .println( atom .location .toString() );
            locations .append( String .format( "HETATM%5d He   UNK  0001     %7.3f %7.3f %7.3f\n",
                    atom .index, (float) rv.x * scaleFactor, (float) rv.y * scaleFactor, (float) rv.z * scaleFactor ) );
            neighbors .append( String .format( "CONECT%5d", atom .index ) );
            for (Atom neighbor : atom .neighbors) {
                neighbors .append( String .format( "%5d", neighbor .index ) );
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
        Set<Atom> neighbors = new HashSet<>();
    }
}



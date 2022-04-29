
package com.vzome.core.model;

import java.io.Writer;
import java.util.ArrayList;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.VefVectorExporter;

public class VefModelExporter extends VefVectorExporter implements Exporter
{
    public VefModelExporter( Writer writer, AlgebraicField field )
    {
        this( writer, field, null, false );
    }

    public VefModelExporter( Writer writer, AlgebraicField field, AlgebraicNumber scale, boolean withOffset )
    {
        super( writer, field, scale, withOffset );
    }
    
    @Override
    public void exportManifestation( Manifestation man )
    {
        if ( man instanceof Connector )
        {
            this .exportPoint( man .getLocation() );
        }
        else if ( man instanceof Strut )
        {
            Strut strut = (Strut) man;
            // we must preserve the strut direction since it may be significant later
            // such as when it's used for a translation tool's direction parameter
            // so don't use getCanonicalLesserEnd() and getCanonicalGreaterEnd() here
            this .exportSegment( strut .getLocation(), strut .getEnd() );
        }
        else if ( man instanceof Panel )
        {
            Panel panel = (Panel) man;
            // Unsorted list retains the panel vertex order
            ArrayList<AlgebraicVector> corners = new ArrayList<>(panel.getVertexCount());
            for (AlgebraicVector vertex : panel) {
                corners .add( vertex );
            }
            this .exportPolygon( corners );
        }
    }

    /**
     * This is used only for vZome part geometry export    
     * @param man
     */
    public void exportSelectedManifestation( Manifestation man )
    {
		if ( man == null ) 
		{
			// This is called to append a newline after all of the panels are exported
			// to provide readability and separation of whatever new field may eventually be appended.
			output .println();
			output .flush();
		}
		else if ( man instanceof Connector )
		{
			if ( strTip != null ) {
				output .print( strTip );
				strTip = null; // indicates that it's already been written
			}
			AlgebraicVector loc = man .getLocation();
			output .println( " " + sortedVertexList.indexOf(loc));
		}
        else if ( man instanceof Panel ) {
        	if ( strMiddle != null ) {
        		output .println( strMiddle );
        		strMiddle = null; // indicates that it's already been written
        	}
            Panel panel = (Panel) man;
            for (AlgebraicVector corner : panel) {
                output .print( sortedVertexList.indexOf( corner ) + " " );
            }
            output.println();
        }
    }

    @Override
    public void finish()
    {
        super .finishExport();
    }
}

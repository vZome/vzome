package org.vorthmann.zome.app.impl;

import java.text.NumberFormat;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.vorthmann.ui.DefaultController;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.AlgebraicVectors;
import com.vzome.core.editor.EditorModelImpl;
import com.vzome.core.editor.SelectionSummary;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.editor.api.Manifestations;
import com.vzome.core.editor.api.OrbitSource;
import com.vzome.core.editor.api.Selection;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;
import com.vzome.core.render.RenderedModel;

public class MeasureController extends DefaultController implements SelectionSummary.Listener
{
	private final Selection selection;
	private final EditorModel editorModel;
	// LinkedHashMap preserves insertion order rather than auto-sorting
	private final Map<String, String> measurements = new LinkedHashMap<>();
	
	private final RenderedModel renderedModel;

    private final NumberFormat twoPlaces = NumberFormat .getInstance();
    private final NumberFormat fourPlaces = NumberFormat .getInstance();

	public MeasureController( EditorModel model, RenderedModel renderedModel )
	{
	    this .renderedModel = renderedModel;
	    this .selection = model .getSelection();
	    this .editorModel = model; // allow run time access to the current editorModel.symmetrySystem
	    ((EditorModelImpl) model) .addSelectionSummaryListener( this );
        this .twoPlaces .setMaximumFractionDigits( 2 );
        this .fourPlaces .setMaximumFractionDigits( 4 );
	}

	@Override
	public String[] getCommandList( String listName )
	{
		return this .measurements .keySet() .toArray( new String[this .measurements .keySet().size()] );
	}

	@Override
	public String getProperty( String key )
	{
		return this .measurements .get( key );
	}

	@Override
	public void selectionChanged( int total, int balls, int struts, int panels )
	{
        this .measurements .clear();
	    if( total != 0 ) {
	        if(balls != 0) {
	            this .measurements .put( "balls", Integer.toString(balls) );
	        }
	        if(struts != 0) {
	            this .measurements .put( "struts", Integer.toString(struts) );
	        }
	        if(panels != 0) {
	            this .measurements .put( "panels", Integer.toString(panels) );
	        }
	        
	        if( total == 1 || total == 2 ) {
	            this .measurements .put( "", "" ); // visual separator
	        }
            
            if( total == 1 ) {
                if( panels == 1 ) {
                    Panel panel = Manifestations.getPanels( this .selection ).next();
                    this .measurements .put( "vertices", Integer.toString(panel.getVertexCount()) );
                } else if ( struts == 1 ) {
                    Strut strut = Manifestations.getStruts( this .selection ).next();
                    double cm = this .renderedModel .measureLengthCm( strut );
                    this .measurements .put( "length (cm)", twoPlaces .format( cm ) + " cm" );
                    double in = cm / 2.54;
                    this .measurements .put( "length (in)", twoPlaces .format( in ) + " in" );
                } else if ( balls == 1 ) {
                    Connector conn = Manifestations.getConnectors( this .selection ).next();
                    this .measurements .put( "location", conn.getLocation().toString());
                }
            } else if( total == 2 ) {
        		if( panels == 2 ) {
        			Panel p1 = null, p2 = null;
        			for ( Panel panel : Manifestations.getPanels( this .selection ) ) {
        				if ( p1 == null )
        					p1 = panel;
        				else
        					p2 = panel;
        			}
        			double radians = this .renderedModel .measureDihedralAngle( p1, p2 );
        			this .reportAngles( radians );
        		} else if ( struts == 2 ) {
        	        Strut s1 = null, s2 = null;
        	        for ( Strut strut : Manifestations.getStruts( this .selection ) ) {
        	            if ( s1 == null )
        	                s1 = strut;
        	            else
        	                s2 = strut;
        	        }
                    Set<AlgebraicVector> points = new HashSet<>();
                    points.add(s1.getLocation());
                    points.add(s1.getEnd());
                    points.add(s2.getLocation());
                    points.add(s2.getEnd());
                    if(points.size() > 3) {
                        this .measurements .put( "coplanar", AlgebraicVectors.areCoplanar(points) ? "yes" : "no" );
                    }
                    double radians = this .renderedModel .measureAngle( s1, s2 );
                    this .reportAngles( radians );
                    this.reportRatio(s1, s2);
        		} else if ( balls == 2 ) {
        			Connector b1 = null, b2 = null;
        			for ( Connector conn : Manifestations.getConnectors( this .selection ) ) {
        				if ( b1 == null )
        					b1 = conn;
        				else
        					b2 = conn;
        			}
        			double cm = this .renderedModel .measureDistanceCm( b1, b2 );
        			this .measurements .put( "distance (cm)", twoPlaces .format( cm ) + " cm" );
        			double in = cm / 2.54;
        			this .measurements .put( "distance (in)", twoPlaces .format( in ) + " in" );
        		}
            }
        }
		this .firePropertyChange( "measures", false, true );
	}

	private void reportAngles( double radians )
	{
	    if(Double.isFinite(radians)) {
    	    double fraction = radians / Math.PI;
            double supplement = 1.0 - fraction;
    		this .measurements .put( "radians", fourPlaces .format( fraction ) + "\u03C0" );
            this .measurements .put( "radians (supplement)", fourPlaces .format( supplement ) + "\u03C0" );
            this .measurements .put( "degrees", twoPlaces .format( fraction * 180 ) + "\u00B0" );
            this .measurements .put( "degrees (supplement)", twoPlaces .format( supplement * 180 ) + "\u00B0" );
	    } else {
	        // This shouldn't happen, but if it does, at least we get a visual clue
            this .measurements .put( "angle", Double.toString(radians) );
	    }
	}
	
	private void reportRatio(Strut s1, Strut s2)
	{
        AlgebraicVector v1 = s1.getOffset();
        AlgebraicVector v2 = s2.getOffset();
        OrbitSource ss = ((EditorModelImpl) editorModel).getSymmetrySystem();
        Axis axis1 = ss .getAxis( v1 );
        Axis axis2 = ss .getAxis( v2 );
        Direction dir1 = axis1.getDirection();
        Direction dir2 = axis2.getDirection();
        boolean sameOrbit = dir1.equals(dir2);
        String name1 = dir1.getName();
        String name2 = dir2.getName();
        final String auto = "auto";
        if(dir1.isAutomatic()) {
            name1 = auto + name1;
        }
        if(dir2.isAutomatic()) {
            name2 = auto + name2;
        }
        if(sameOrbit) {
            // append subscripted numbers since auto direction names are numeric
            name1 += "\u2081"; // subscripted 1
            name2 += "\u2082"; // subscripted 2
        }
        String n1n2 = name1 + " / " + name2;
        String n2n1 = name2 + " / " + name1;

        // second visual separator needs unique key since measurements is a map, so use one space
        this .measurements .put( " ", " " );
        // we can't use AlgebraicNumber math unless the two struts are in the same orbit 
        // but we can still show approximated decimal values
        double length1 = Math.sqrt(AlgebraicVectors.getMagnitudeSquared(v1).evaluate());
        double length2 = Math.sqrt(AlgebraicVectors.getMagnitudeSquared(v2).evaluate());
        Double ratio = length1/length2;
        String inequality = "equal"; 
        if(length1 != length2) {
            inequality = name1 + " " + (length1 > length2 ? ">" : "<") + " " + name2;
        }
        this .measurements .put( "relative strut lengths", inequality);
        if(length1 != length2) {
            Double recip = 1/ratio;
            this .measurements .put( n1n2 + " (approx)", fourPlaces .format(ratio));
            this .measurements .put( n2n1 + " (approx)", fourPlaces .format(recip));
            if(sameOrbit) {
                // if the two struts are in the same orbit, we can show the exact ratios as AlgebraicNumbers. 
                // Sine axis.getLength() returns a length relative to the normal of the axis 
                // instead of an absolute length, this part only makes sense 
                // when both struts are in the same orbit (i.e. both blue)
                AlgebraicNumber exactLength1 = axis1 .getLength( v1 );
                AlgebraicNumber exactength2 = axis2 .getLength( v2 );
                AlgebraicNumber exactRatio = exactLength1.dividedBy(exactength2);
                AlgebraicNumber exactRecip = exactRatio.reciprocal();
                this .measurements .put( n1n2, exactRatio.toString());
                this .measurements .put( n2n1, exactRecip.toString());
                System.out.println( n1n2 + " = " + exactRatio.toString());
                System.out.println( n2n1 + " = " + exactRecip.toString());
            }
        }
	}
}

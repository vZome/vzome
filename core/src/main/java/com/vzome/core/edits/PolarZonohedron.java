package com.vzome.core.edits;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.PolygonFromVertices;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentEndPoint;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.construction.SymmetryTransformation;
import com.vzome.core.construction.Transformation;
import com.vzome.core.construction.TransformedPoint;
import com.vzome.core.construction.TransformedSegment;
import com.vzome.core.editor.api.ChangeManifestations;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.editor.api.OrbitSource;
import com.vzome.core.editor.api.SymmetryAware;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Permutation;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Strut;
import com.vzome.xml.DomUtils;

public class PolarZonohedron extends ChangeManifestations
{
    private OrbitSource symmetry;
    private final EditorModel editor;
    
    public PolarZonohedron( EditorModel editor )
    {
        super( editor .getSelection(), editor .getRealizedModel() );
        this .editor = editor;
        this .symmetry = ((SymmetryAware) editor) .getSymmetrySystem();
    }

    @Override
    protected String getXmlElementName()
    {
        return "PolarZonohedron";
    }

    @Override
    protected void getXmlAttributes(Element element)
    {
        if (symmetry != null) {
            DomUtils.addAttribute(element, "symmetry", symmetry.getName());
        }
    }

    // Note that symmetry is read from the XML and passed to the c'tor
    // unlike the normal pattern of deserializing the XML here.
    // See the explanation in DocumentModel.createEdit()
    // There's really no need to override setXmlAttributes in this case
    // since there is nothing else to deserialize. but it does serve as a reminder
    // if this code is used as a pattern for another subclass of ChangeSelection.
    @Override
    protected void setXmlAttributes( Element xml, XmlSaveFormat format ) 
            throws Failure
    {
        this .symmetry = ((SymmetryAware) this .editor) .getSymmetrySystem( xml .getAttribute( "symmetry" ) );
    }
    
    @Override
    public void perform() throws Command.Failure
    {
        final StringBuilder errorMsg = new StringBuilder();
        errorMsg.append("The Polar Zonohedron command requires either of the following selections:\n\n" + 
                "1) Two non-collinear struts with a common end point.\n" +
                "   The first strut must have more than 2-fold rotational symmetry.\n" + 
                "   The second strut will be rotated around the first.\n\n" +
                "2) Any three or more struts having a common end point.\n") ;

        List<Strut> struts = new ArrayList<>();
        for (Manifestation man : mSelection) {
            if ( man instanceof Strut ) {
                struts.add((Strut) man);
            }
            // balls and panels are also unselected
            unselect( man );
        }
        
        if(struts.size() < 2) {
            errorMsg.append(struts.size() == 1 ? "\nonly one strut is selected." : "\nno struts are selected.");
            fail( errorMsg.toString() ); // throw an exception and we're done.
        }
        
        AlgebraicVector common =  struts.size() == 2 
                ? useRotationalSymmetry(struts, errorMsg)
                : useRadialSelection(struts);
        
        // redo() should have already been called if common is not null
                
        if( common == null ) {
            // throw an exception and we're done.
            fail( errorMsg.append("\nselected struts do not have a common end point").toString() );
        }

        // At this point, struts contains all the struts we want to use, 
        // but they may not all be oriented with start at center
        

        final int L1 = 0;
        final int L2 = 1;
        int layers = struts.size();
    
        List<AlgebraicVector> offsets = new ArrayList<>(layers);
        AlgebraicVector[][] vertices = new AlgebraicVector[2][];
        vertices[L1] = new AlgebraicVector[layers];
        for( int i=0; i < layers; i++ ) {
            Strut strut = struts.get(i);
            AlgebraicVector start = strut.getLocation();
            AlgebraicVector end = strut.getEnd();
            AlgebraicVector offset = strut.getOffset();
            if( start.equals(common) ) {
                vertices[L1] [i] = end;
                offsets.add(offset);
            } else {
                vertices[L1] [i] = start;
                offsets.add(offset.negate());
            }
        }
        
        for(int layer = 1; layer < layers; layer++) {
            vertices[L2] = new AlgebraicVector[layers];
            for(int i = 0; i < layers; i++) {
                int off = (i+layer) % layers;
                AlgebraicVector offset = offsets.get(off);
                AlgebraicVector v1 = vertices[L1] [i];
                AlgebraicVector v2 = v1.plus(offset);
                AlgebraicVector v3 = vertices[L1] [(i+1) % layers];
                AlgebraicVector v0 = v3.minus(offset);
                vertices[L2] [i] = v2;
                Point p0 = new FreePoint( v0 );
                Point p1 = new FreePoint( v1 );
                Point p2 = new FreePoint( v2 );
                Point p3 = new FreePoint( v3 );
                select( manifestConstruction( p0 ) );
                select( manifestConstruction( p1 ) );
                select( manifestConstruction( p2 ) );
                select( manifestConstruction( p3 ) );
                select( manifestConstruction( new SegmentJoiningPoints( p1, p2 ) ) );
                select( manifestConstruction( new SegmentJoiningPoints( p2, p3 ) ) );
                select( manifestConstruction( new PolygonFromVertices( new Point[]{ p0, p1, p2, p3 } ) ) );
            }
            vertices[L1] = vertices[L2];
        }
        
        redo(); // commit our selects
    }

    public static AlgebraicVector getCommonEndpoint(Strut strut1, Strut strut2) {
        if( strut1.equals(strut2) ) {
            throw new IllegalArgumentException("Identical struts have both end points in common.");
        }
        AlgebraicVector start1 = strut1 .getLocation();
        AlgebraicVector end1 = strut1 .getEnd();
        AlgebraicVector start2 = strut2 .getLocation();
        AlgebraicVector end2 = strut2 .getEnd();
        if( start1.equals(start2) || start1.equals(end2) )
            return start1;
        if( end1.equals(start2) || end1.equals(end2) )
            return end1;
        // no match
        return null;
    }
    
    private AlgebraicVector useRotationalSymmetry(List<Strut> struts, StringBuilder errorMsg) throws Failure {
        // try to use rotational symmetry
        Strut axisStrut = struts.get(0);
        Segment axisSegment = (Segment) axisStrut .getFirstConstruction();
        AlgebraicVector v1 = axisSegment .getOffset();
        v1 = axisSegment .getField() .projectTo3d( v1, true );
        Axis axis1 = symmetry .getAxis( v1 );
        if ( axis1 == null ) {
            // throw an exception and we're done.
            fail( errorMsg.append("\nfirst selected strut is not an axis of rotational symmetry").toString() );
        }
        Permutation perm = axis1 .getRotationPermutation();
        if ( perm == null ) {
            // throw an exception and we're done.
            fail( errorMsg.append("\nfirst selected strut is not an axis of rotation").toString() );
        }
        int rotation = perm .mapIndex( 0 );
        int order = perm .getOrder();
        if(order <= 2) {
            // throw an exception and we're done.
            fail( errorMsg.append("\nfirst selected strut has " + order + "-fold symmetry").toString() );
        }
    
        // Check that the second strut is not collinear with the first
        Strut spokeStrut = struts.get(1);
        Segment spokeSegment = (Segment) spokeStrut .getFirstConstruction();
        AlgebraicVector v2 = spokeSegment .getOffset();
        if( v1.equals(v2) || v1.equals( v2.negate() ) ) {
            // throw an exception and we're done.
            fail( errorMsg.append("\nselected struts are collinear").toString() );
        }
        
        // ensure the two struts have a common end point
        AlgebraicVector common = getCommonEndpoint(axisStrut, spokeStrut);
        if( common == null ) {
            // throw an exception and we're done.
            fail( errorMsg.append("\nselected struts do not have a common end point").toString() );
        }

        AlgebraicVector s1 = axisSegment .getStart();
        AlgebraicVector e1 = axisSegment .getEnd();
        Point center = new SegmentEndPoint( axisSegment, common.equals(e1) );
        AlgebraicVector s2 = spokeSegment .getStart();
        AlgebraicVector e2 = spokeSegment .getEnd();

        if(common.equals(s1)) {
            if(common.equals(e2)) {
                v2 = v2.negate();
                e2 = s2;
                s2 = common;
            }            
        } else { // common must equal e1
            v1 = v1.negate();
            e1 = s1;
            s1 = common;
            if(common.equals(e2)) {
                v2 = v2.negate();
                e2 = s2;
                s2 = common;
            }                    
        }
        
        redo(); // commit the unselects made so far so we can reselect some of them later

        struts.remove(axisStrut);
        select(spokeStrut);

        // Be sure the balls at both ends of spokeStrut have not been deleted or hidden.
        // Restore them just in case. No need to test if they already exists.
        Point p0 = new FreePoint( s2 );
        Point p1 = new FreePoint( e2 );
        select( manifestConstruction( p0 ) );
        select( manifestConstruction( p1 ) );
        
        // Rotate the second strut around the first to create or select the additional struts to be used as initial edges
        for (int i = 0; i < order - 1; i++) {
            Transformation transform = new SymmetryTransformation( symmetry.getSymmetry(), rotation, center );
            rotation = perm .mapIndex( rotation );
            Connector ball = (Connector) manifestConstruction( new TransformedPoint(transform, p1) );
            Strut strut = (Strut) manifestConstruction( new TransformedSegment(transform, spokeSegment) );
            struts.add(strut);
            select( ball );
            select( strut );
        }
    
        redo();
        
        // At this point, common is not null,
        // struts contains only spokeStrut and its rotations around axisStrut.
        // All struts and the balls at both ends are selected
        return common;
    }

    private AlgebraicVector useRadialSelection(List<Strut> struts) {
        // ensure that all struts have a common end point
        Strut first = struts.get(0);
        AlgebraicVector common = getCommonEndpoint(first, struts.get(1));
        if(common == null) {
            return null;
        }
        for( int i = 1; i < struts.size(); i++) {
            if( !common.equals( getCommonEndpoint(first, struts.get(i) ) ) ) {
                return null;
            }
        }

        // at this point, common is not null and struts contains all of the struts we intend to use.
        
        // Be sure the balls at both ends of all struts have not been deleted or hidden.
        // Restore them just in case. No need to test if they already exists.
        
        redo();
        
        select( manifestConstruction( new FreePoint( common ) ) );
        for( Strut strut : struts ) {
            select( strut );
            AlgebraicVector start = strut.getLocation();
            if( common.equals( start ) ) {
                select( manifestConstruction( new FreePoint( strut.getEnd() ) ) );
            } else {
                select( manifestConstruction( new FreePoint( start ) ) ); 
            }
        }

        redo();
        
        // At this point, common is not null,
        // struts contains all of the originally selected struts in the originally selected order.
        // All struts and the balls at both ends are selected.
        return common;
    }
        
}

package com.vzome.core.edits;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.AlgebraicVectors;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;
import com.vzome.xml.DomUtils;

public class SelectCoplanar extends SelectByBoundary {

    public SelectCoplanar(EditorModel editor) {
        super(editor);
    }

    @Override
    public String usage() {
        return "Select any combination of connectors, struts or panels to specify\n"
                + "3 or more points that are all coplanar, but not simply collinear.\n\n"
                + "All parts that are completely on the corresponding plane will be selected.\n";
    }

    // Some manifestations may share common vectors (e.g. a ball at the end of a strut).
    // Collect them all in a set to remove duplicates.
    protected List<AlgebraicVector> pickedVectors = new ArrayList<>(); // vertices of a single panel picked from the context menu
    protected Set<AlgebraicVector> vectors = new HashSet<>();
    protected AlgebraicVector pointOnPlane = null;
    protected AlgebraicVector normal = null;

    @Override
    public void configure( Map<String,Object> props ) {
        Panel panel = (Panel) props .get( "picked" );
        if ( panel != null ) { // first creation from the editor
        	for (AlgebraicVector v : panel) {
        		// picked vertices are unkown when loading from a file
        		// so save them in the XML
        		pickedVectors.add(v);  
            }
        }
    }
    
    @Override
    protected String setBoundary() {
        // Note that pickedVectors may be populated from a single panel's context menu 
        // or when loading the model from a file or not at all.
        // The ability to invoke SelectCoplanar from the context menu
        // was added several releases after it was available from the main menu.
        // The difference in the vZome file is that a "picked" panel 
        // must be serialized to and from the XML. 
        // If there is no picked panel, then the selection is used 
    	// and an older version will be able to open the file.
    	if(pickedVectors.isEmpty()) {
    		// The plane is based on the selection
	        for (Manifestation man : mSelection) {
	            if (man instanceof Connector) {
	                vectors.add(man.getLocation());
	            } else if (man instanceof Strut) {
	                vectors.add(man.getLocation());
	                vectors.add(((Strut) man).getEnd());
	            } else if (man instanceof Panel) {
	                for (AlgebraicVector v : (Panel) man) {
	                    vectors.add(v);
	                }
	            } else {
	                // future-proof... shouldn't ever happen
	                throw new IllegalStateException("Unknown manifestation: " + man.getClass().getSimpleName());
	            }
	        }
	        if (vectors.size() < 3) {
	            return "Additional connectors, struts or panels must be selected to define a plane.";
	        }
	        if (AlgebraicVectors.areCollinear(vectors)) {
	            return "Selected items are collinear. Select another non-collinear ball to specify the plane.";
	        }
	        if (!AlgebraicVectors.areCoplanar(vectors)) {
	            return "Selected items are not coplanar.";
	        }
    	} else {
	        // load vectors from pickedVectors rather than from the selection.
	        vectors.addAll(pickedVectors);
    		// The plane is based solely on the picked panel.
    		// The selection is unused so it needn't be coplanar.
	        // unselectAll is not really necessary.
	        // It's just here so the behavior is consistent with SelectCollinear. 
	        unselectAll(); 
	        redo(); // commit the unselects so they can be reselected later if necessary. 
    	}
        // All validated. Now just save the values to be used later in boundaryContains()
        for (AlgebraicVector v : vectors) {
            pointOnPlane = v;
            break; // We can use any one of the vectors in the set
        }
        normal = AlgebraicVectors.getNormal(vectors);
        return null;
    }

    @Override
    protected boolean boundaryContains(AlgebraicVector v) {
        return vectors.contains(v) || pointOnPlane.minus(v).dot(normal).isZero();
    }

    public static final String NAME = "SelectCoplanar";

    @Override
    protected String getXmlElementName() {
        return NAME;
    }
    
    @Override
    protected void getXmlAttributes(Element element) {
    	if(!pickedVectors.isEmpty()) {
    		DomUtils.addAttribute(element, "nPickedVectors", Integer.toString(pickedVectors.size()));
    		for(int i = 0; i < pickedVectors.size(); i++) {
    			DomUtils.addAttribute(element, "vector" + i, pickedVectors.get(i).toParsableString());
    		}
    	}
    }

    @Override
    protected void setXmlAttributes(Element xml, XmlSaveFormat format) throws Failure {
    	String nPickedVectors = xml .getAttribute( "nPickedVectors" );
        if( nPickedVectors != null && ! nPickedVectors.isEmpty() ) {
        	int nPicked = Integer.parseInt(nPickedVectors);
        	for(int i = 0; i < nPicked; i++) {
        		pickedVectors.add(format.parseRationalVector(xml, "vector" + i));
        	}
        }
    }

}

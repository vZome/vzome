package com.vzome.core.edits;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;

import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.AlgebraicVectors;
import com.vzome.core.algebra.PolygonField;
import com.vzome.core.commands.Command;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentEndPoint;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.construction.TransformedPoint;
import com.vzome.core.construction.Translation;
import com.vzome.core.editor.api.ChangeManifestations;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Strut;

public class AffinePolygon extends ChangeManifestations {
	
	private final StringBuilder errorMsg = new StringBuilder();
	private final AlgebraicField field;
	private AlgebraicNumber chordRatio;
	private String mode = null;
	private int nSides = 0;
	
	/**
	 * Intended to be used for listing the valid modes for making an "Affine Polygon" submenu
	 * @param field
	 * @return
	 */
	public static SortedMap<Integer, String> getPolygonModes(AlgebraicField field) {
		// Use Integer as the key so they will be sorted correctly.
		// Using String as the key would incorrectly sort alphabetically,
		// so "10" would be before "2"
		SortedMap<Integer, String> modes = new TreeMap<>();
		modes.put(2, "Parabola");
		modes.put(3, "Triangle");
		modes.put(4, "Square");
		if(field.getGoldenRatio() != null) {
			modes.put(5, "Pentagon");
		}
		modes.put(6, "Hexagon");
		if(field.getName().equals("heptagon")) {
			modes.put(7, "Heptagon");
		} else if(field.getName().startsWith("polygon")) {
			int polygonSides = ((PolygonField)field).polygonSides();
			for(int nSides = 7; nSides <= polygonSides; nSides++) {
				// Trying every integer is not a very efficient way to get the remaining prime factors,
				// but this is seldom called and the numbers are small, so I'll let it slide.
				if( polygonSides / nSides * nSides == polygonSides) {
					// polygonSides is an even multiple of nSides
					String label;
					switch(nSides) {
					case 7:
						label = "Heptagon";
						break;
					case 8:
						label = "Octagon";
						break;
					case 9:
						label = "Nonagon";
						break;
					case 10:
						label = "Decagon";
						break;
					case 12:
						label = "Dodecagon";
						break;
					default:
						label = nSides + "-gon";
						break;
					}
					modes.put(nSides, label);
				}
			}
		}
		return modes;
	}
	
    @Override
    public void configure( Map<String,Object> props ) 
    {
        setMode( (String) props .get( "mode" ) );
        if(mode == null || mode.isBlank()) {
        	errorMsg.append("\nMode is not specified.");
		}
        if(nSides < 2) {
        	errorMsg.append("\nThe number of sides must be greater than 1.");
		}
        if(chordRatio == null) {
        	errorMsg.append("\nUnsupported chord ratio.");
		}
        if(!errorMsg.isEmpty() ) {
        	errorMsg.insert(0, " configuration error(s)");
        	errorMsg.insert(0, getXmlElementName());
        }
    }
    
	private void setMode(String newMode) {
		mode = newMode;
		if (mode != null && ! mode.isEmpty()) {
			try {
				nSides = Integer.parseInt(mode);
			} catch (NumberFormatException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning(getXmlElementName() + ": Invalid mode \"" 
							+ mode + "\".\nMode should be an integer greater than one.");
				}
			}
		}
		switch(nSides) {
		case 2:
			chordRatio = field.createRational(3); // Parabola
			break;
		case 3:
			chordRatio = field.zero(); // Triangle
			break;
		case 4:
			chordRatio = field.one();
			break;
		case 5:
			chordRatio = field.getGoldenRatio();
			break;
		case 6:
			chordRatio = field.createRational(2);
			break;
		default:
			if(nSides == 7 && field.getName().equals("heptagon")) {
				chordRatio = field.getUnitTerm(2); // sigma
			} else if(nSides >= 7 && field.getName().startsWith("polygon")) {
				PolygonField pField = (PolygonField)field;
				int polygonSides = pField.polygonSides();
				int step = polygonSides / nSides;
				if(step * nSides == polygonSides) {
					AlgebraicNumber diag0 = pField.getUnitDiagonal(step-1);
					AlgebraicNumber diag2 = pField.getUnitDiagonal((step*3)-1);
					chordRatio = diag2.dividedBy(diag0);
				}
			}
			break;
		}
	}

    @Override
    public void perform() throws Command.Failure
    {
		if(!errorMsg.isEmpty()) {
			// Any configuration errors are reported here (e.g. from invalid custom menu modes)
			// throw an exception and we're done.
        	fail( errorMsg.toString() );
		}
		// prep for reporting any user input errors
    	errorMsg.append(getXmlElementName())
        	.append(" requires two non-parallel struts with a common end point.\n");
        
        Strut strut1 = null;
        Strut strut2 = null;
        
        for (Manifestation man : mSelection) {
            if ( man instanceof Strut ) {
            	if(strut1 == null) {
            		strut1 = (Strut)man;
            	} else if(strut2 == null) {
            		strut2 = (Strut)man;
            	} else {
                	// throw an exception and we're done.
                	fail( errorMsg.append("\nToo many struts are selected.").toString() );
                }
            }
            // balls and panels are also unselected
            unselect( man );
        }
        
        if(strut2 == null) {
            fail( errorMsg.append(strut1 == null 
            	? "\nNo struts are selected."
            	:"\nOnly one strut is selected."
            	).toString() ); // throw an exception and we're done.
        }
        
        AlgebraicVector offset1 = strut1.getOffset();
        AlgebraicVector offset2 = strut2.getOffset();
        if(AlgebraicVectors.areParallel(offset1, offset2)) {
        	 // throw an exception and we're done.
        	fail( errorMsg.append("\nStruts are parallel or collinear.").toString() );
        }

    	Point pCommon = null;
        Point pStart = null;
        Point pEnd = null;
        Segment seg1 = (Segment) strut1.getFirstConstruction();
        Segment seg2 = (Segment) strut2.getFirstConstruction();
        {
        	AlgebraicVector s1 = strut1 .getLocation();
            AlgebraicVector e1 = strut1 .getEnd();
            AlgebraicVector s2 = strut2 .getLocation();
            AlgebraicVector e2 = strut2 .getEnd();
            if( s1.equals(s2) ) { // <---strut1---< common >---strut2--->
                pStart = new SegmentEndPoint(seg1, false);
                pCommon = new SegmentEndPoint(seg2, true);
                pEnd = new SegmentEndPoint(seg2, false);
            } else if( s1.equals(e2) ) { // <---strut1---< common <---strut2---<
            	offset2 = offset2.negate();
                pStart = new SegmentEndPoint(seg1, false);
                pCommon = new SegmentEndPoint(seg2, false);
                pEnd = new SegmentEndPoint(seg2, true);
            } else if( e1.equals(s2)) { // >---strut1---> common >---strut2--->
                offset1 = offset1.negate();
                pStart = new SegmentEndPoint(seg1, true);
                pCommon = new SegmentEndPoint(seg2, true);
                pEnd = new SegmentEndPoint(seg2, false);
            } else if( e1.equals(e2) ) { // >---strut1---> common <---strut2--->
                offset1 = offset1.negate();
                offset2 = offset2.negate();
                pStart = new SegmentEndPoint(seg1, true);
                pCommon = new SegmentEndPoint(seg2, false);
                pEnd = new SegmentEndPoint(seg2, true);
            } else {
	        	errorMsg.append("\nStruts do not have a common end point.");
	            fail( errorMsg.toString() ); // throw an exception and we're done.
            }
        }
        AlgebraicVector vBegin = pStart.getLocation();
        
        // All inputs are validated, so commit any unselects 
        // so they can be re-selected in the desired order later,
        redo();
        
        /*
			pStart                                 pNew
         	@>------ translateByChordRatio ------->@
           	^                                     ^
           	|                                    /
           	| offset1 (from seg1)               / segNew
           	|                                  /
           	|                                 /
           	^                                ^
            @>---- offset2 (from seg2) ---->@
            pCommon                         pEnd
            
        */
        
        // ensure that there are balls selected at the end points of the two original struts.
        // select the balls and struts in sequence around the polygon.
        // Except in the case of an affine parabola (when nSides == 2). 
        // In that case, don't reselect the first ball or strut in the series.
        // That way, the two struts that are selected 
        // will be prepared to repeat the command ad infinitum.
        Manifestation man0 = manifestConstruction(pStart);
        Manifestation man1 = manifestConstruction(seg1);
        if(nSides != 2) {
        	// not a parabola
        	select(man0);
        	select(man1);
        }
        // Now select the remaining balls and struts
        select(manifestConstruction(pCommon));
        select(manifestConstruction(seg2));
        select(manifestConstruction(pEnd));

        // Start with 2 to account for the 2 input struts
        // except for the parabola case (when nSides == 2)
        // in which case we start with 1
        for(int i = (nSides == 2) ? 1 : 2; i < this.nSides; i++) {
        	AlgebraicVector translateByChordRatio = offset2.scale(this.chordRatio);
        	Point pNew = new TransformedPoint(new Translation( translateByChordRatio ), pStart);
        	Segment segNew = new SegmentJoiningPoints(pEnd, pNew);
        	select( manifestConstruction( segNew ) );
        	select( manifestConstruction( pNew ) );
        	if(pNew.getLocation().equals(vBegin)) {
        		if(i+1 < this.nSides) {
        			if (logger.isLoggable(Level.INFO)) {
    					logger.info(getXmlElementName() + ": actual reps = " + (i+1) + ", not " + this.nSides + " as specified.");
    				}
        		}
        	}
        	// prep for next iteration
        	pStart = pCommon;
        	offset1 = offset2.negate();
        	pCommon = pEnd;
        	offset2 = segNew.getOffset();
        	pEnd = pNew;
        }
        // commit the additions and selections
        redo();
    }
    
    public AffinePolygon( EditorModel editorModel )
    {
        super( editorModel );
        setOrderedSelection(true);
        this.field = editorModel.getSymmetrySystem().getSymmetry().getField();
    }

    @Override
    protected String getXmlElementName() {
        return "AffinePolygon";
    }
    
    @Override
    protected void getXmlAttributes( Element element )
    {
        if(mode != null) {
            element .setAttribute( "mode", mode );
        }
    }

    @Override
    protected void setXmlAttributes( Element xml, XmlSaveFormat format ) throws Command.Failure
    {
        setMode( xml.getAttribute( "mode" ) );
    }

}

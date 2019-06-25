//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.
package com.vzome.core.editor;

import java.util.ArrayList;
import java.util.Map;

import org.w3c.dom.Element;

import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

public class JoinPoints extends ChangeManifestations {

    public enum JoinModeEnum {

        CHAIN_BALLS,
        CLOSED_LOOP,
        ALL_TO_FIRST,
        ALL_TO_LAST,
        ALL_POSSIBLE
//      CONVEX_HULL // TODO: possible future
    }

    protected JoinModeEnum joinMode = JoinModeEnum.CLOSED_LOOP;
//	protected ToolBehavior toolBehavior = new ToolBehavior();

    // Either configure() or setXmlAttributes() will be called before perform().
    public JoinPoints( Selection selection, RealizedModel realized ) 
    {
        super( selection, realized );
    }
    
    @Override
    protected boolean groupingAware()
    {
        return true;
    }

    // TODO: Implement a new ToolBehavior class as a parameter, behaving similar to the modes parmeter in the 
    // ApplyTool c'tor. It should effect all the various joinXxx methods consistently, with the default behavior 
    //  being identical to the earlier versions of JoinPoints which didn't support that feature.
    // It mainly affects if and when unselect and redo should be called along the way.
    // Be sure to serialize the new ToolBehavior parameter to and from the xml.
    // ToolBehavior could also replace the modes parameter in the ApplyTool c'tor and still have the same effects.
    // It could become a standard API for passing such info into similar tools.
    // UPDATE: I can't figure out how to get the run-time control key mask into perform(), 
    //  so I'm going to put the idea of a toolBehavior param on hold for now.
    // At this point, I've added the code to serialize to and from the xml if it is non-zero, but it's commented out.
    // Once I figure out how to get the parameter into the JoinPoints c'tor, then I can implement the appropriate behaviors.
    
    @Override
    public void configure( Map<String,Object> props ) 
    {
        String mode = (String) props .get( "mode" );
        if ( mode != null )
            this .joinMode = JoinModeEnum.valueOf( mode );
    }

    // A few constant strings to help avoid typos...
    public static final String ATTRNAME_CLOSEDLOOP = "closedLoop";
    public static final String ATTRNAME_JOINMODE = "joinMode";
//  public static final String ATTRNAME_TOOLBEHAVIOR = "toolBehavior";

    @Override
    protected void getXmlAttributes(Element element)
    {	
    	if ( joinMode == JoinModeEnum.CLOSED_LOOP ) {
        	// For closedLoop mode, serialize as before.  The setXmlAttributes fallback will do the right thing.
    	}
    	else {
    		// Retain the boolean ATTRNAME_CLOSEDLOOP attribute in the Xml for backward compatibility.
            // Older versions will incorrectly handle the new joinModes, treating them as CLOSED_LOOP
            // In some simplistic cases, such as with two or three balls selected, the results may be similar.
            // In most cases where the new joinModes are used, the older versions will not produce the desired results.
    		element.setAttribute( ATTRNAME_CLOSEDLOOP, "false" );
    		if ( joinMode != JoinModeEnum.CHAIN_BALLS )
    			// don't emit the joinMode unless we have to
    			element.setAttribute( ATTRNAME_JOINMODE, joinMode.name());
    	}
//		if(toolBehavior.hasAnyModifiers())
//		{
//			element .setAttribute( ATTRNAME_TOOLBEHAVIOR, toolBehavior.getModifiers().toString() );
//		}
    }

    @Override
    protected void setXmlAttributes(Element xml, XmlSaveFormat format) throws Failure {
        String attr = xml.getAttribute(ATTRNAME_JOINMODE);
        if (attr != null && !attr.isEmpty()) {
            joinMode = JoinModeEnum.valueOf(attr);
        } else {
            // Fall back to the old boolean ATTRNAME_CLOSEDLOOP attribute for backward compatibility.
            attr = xml.getAttribute(ATTRNAME_CLOSEDLOOP);
            if (attr != null && !attr.isEmpty()) {
                joinMode = Boolean.parseBoolean(attr) ? JoinModeEnum.CLOSED_LOOP : JoinModeEnum.CHAIN_BALLS;
            } else {
                // Older versions default to ATTRNAME_CLOSEDLOOP=true when the ATTRNAME_CLOSEDLOOP attribute is not present.
                joinMode = JoinModeEnum.CLOSED_LOOP;
            }
        }
//		attr = xml .getAttribute( ATTRNAME_TOOLBEHAVIOR );
//		if ( attr != null && ! attr.isEmpty() ) {
//			toolBehavior = new ToolBehavior(Integer.valueOf(attr));
//		}
    }

    @Override
    public void perform() throws Failure {
        ArrayList<Point> inputs = new ArrayList<>();
        
        if ( joinMode != JoinModeEnum.ALL_POSSIBLE )
        	setOrderedSelection( true );
        
        for (Manifestation man : mSelection) {
            if (man instanceof Connector) {
                inputs.add((Point) ((Connector) man).getConstructions().next());
            }
            unselect(man);
        }
        redo();  // commit the initial unselect operations

        int last = inputs.size() - 1;
        if (last > 0) {
            Point[] points = inputs.toArray(new Point[]{});

            switch (joinMode) {
                case CHAIN_BALLS:
                    for (int i = 0; i < last; i++) {
                        addSegment(points, i, i + 1);
                    }
                    break;
                case CLOSED_LOOP:
                    for (int i = 0; i < last; i++) {
                        addSegment(points, i, i + 1);
                    }
                    if (last > 1) {
                        addSegment(points, last, 0);
                    }
                    break;
                case ALL_TO_FIRST:
                    for (int i = 1; i <= last; i++) {
                        addSegment(points, 0, i);
                    }
                    break;
                case ALL_TO_LAST:
                    for (int i = 0; i < last; i++) {
                        addSegment(points, i, last);
                    }
                    break;
                case ALL_POSSIBLE:
                    for (int start = 0; start < last; start++) {
                        for (int end = start + 1; end <= last; end++) {
                            addSegment(points, start, end);
                        }
                    }
                    break;
                default:
                    throw new Failure("Unsupported JoinModeEnum: " + joinMode.toString());
            }
            // commit the changes
            redo();
        }
    }

    protected void addSegment(Point[] points, int start, int end) {
        if ((start != end) && !(points[start].getLocation().equals(points[end].getLocation()))) {
            Segment segment = new SegmentJoiningPoints(points[start], points[end]);
            select(manifestConstruction(segment));
        }
    }

    @Override
    protected String getXmlElementName() {
        return "JoinPoints";
    }

}

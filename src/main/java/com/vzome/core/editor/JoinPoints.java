
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import org.w3c.dom.Element;

import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;
import java.util.ArrayList;

public class JoinPoints extends ChangeConstructions
{
	public enum joinModeEnum {
		chainBalls,
		closedLoop,
		allToFirst,
		allToLast,
		fullMesh
		//convexHull // TODO: possible future
	}
	
	protected joinModeEnum joinMode = joinModeEnum.closedLoop;
//	protected ToolBehavior toolBehavior = new ToolBehavior();

	// Any code that calls this c'tor must call setXmlAttributes() to set the joinMode (and ToolBehavior) before calling perform().
    public JoinPoints( Selection selection, RealizedModel realized, boolean groupInSelection)
    {
        super( selection, realized, groupInSelection );
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
    public JoinPoints( Selection selection, RealizedModel realized, boolean groupInSelection, joinModeEnum joinMode ) //, ToolBehavior behavior )
    {
        super( selection, realized, groupInSelection );
		this.joinMode = joinMode;
		//if(behavior!= null) { this.toolBehavior = behavior; }
    }
	
	// A few constant strings to help avoid typos...
	public static final String attrName_CLOSEDLOOP = "closedLoop";
	public static final String attrName_JOINMODE = "joinMode";
//	public static final String attrName_TOOLBEHAVIOR = "toolBehavior";

	@Override
    protected void getXmlAttributes( Element element )
    {
		// Retain the boolean closedLoop attribute in the Xml for backward compatibility.
		// Older versions will incorrectly handle the new joinModes, treating them as closedLoop
		// In some simplistic cases, such as with two or three balls as input, the results may be similar.
		// In most cases where the new joinModes are used, the older versions will not render the desired results.
        element .setAttribute( attrName_CLOSEDLOOP, joinMode == joinModeEnum.closedLoop ? "true" : "false" );
        element .setAttribute( attrName_JOINMODE, joinMode.name() );
//		if(toolBehavior.hasAnyModifiers())
//		{
//			element .setAttribute( attrName_TOOLBEHAVIOR, toolBehavior.getModifiers().toString() );
//		}
	}
	
	// Any code that calls setXmlAttributes() should have already used the c'tor with an xml Element
	// The second call is unnecessary in this case, but it is the normal pattern for the base class.
	@Override
    protected void setXmlAttributes( Element xml, XmlSaveFormat format ) throws Failure
    {
		String attr = xml .getAttribute( attrName_JOINMODE );
		if ( attr != null && ! attr.isEmpty() ) {
			joinMode = joinModeEnum.valueOf(attr);
		} else {
			// Fall back to the old boolean closedLoop attribute for backward compatibility.
			attr = xml .getAttribute( attrName_CLOSEDLOOP );
			if ( attr != null && ! attr.isEmpty() ) {
				joinMode = Boolean.parseBoolean(attr) ? joinModeEnum.closedLoop : joinModeEnum.chainBalls;
			}
			else {
				// Older versions default to closedLoop=true when the closedLoop attribute is not present.
				joinMode = joinModeEnum.closedLoop;
			}
		}
//		attr = xml .getAttribute( attrName_TOOLBEHAVIOR );
//		if ( attr != null && ! attr.isEmpty() ) {
//			toolBehavior = new ToolBehavior(Integer.valueOf(attr));
//		}
    }

	@Override
    public void perform() throws Failure
    {
//		logSelectionSet("JoinBalls(" + joinMode.toString() + ")\t{ inputs: ");
		ArrayList<Point> inputs = new ArrayList<>();
		for (Manifestation man : mSelection) {
			if ( man instanceof Connector )
			{
				inputs.add((Point)((Connector) man).getConstructions().next());
			}
			unselect( man );
		}        
        redo();  // commit the initial unselect operations

		int last = inputs.size() - 1;
		if(last > 0) {
			Point[] points = inputs.toArray(new Point[]{});
			
			switch (joinMode) {
				case chainBalls:
					for(int i=0; i<last; i++) {
						addSegment( points, i, i+1 );
					}
					break;
				case closedLoop:
					for(int i=0; i<last; i++) {
						addSegment( points, i, i+1 );
					}
					if(last > 1) {
						addSegment( points, last, 0 );
					}
					break;
				case allToFirst:
					for(int i=1; i<=last; i++) {
						addSegment( points, 0, i );
					}
					break;
				case allToLast:
					for(int i=0; i<last; i++) {
						addSegment( points, i, last );
					}
					break;
				case fullMesh:
					for(int start=0; start<last; start++) {
						for(int end=start+1; end<=last; end++) {
							addSegment( points, start, end );
						}
					}
					break;
				default:
					throw new Failure("Unsupported joinMode: " + joinMode.toString() );
			}
			// commit the changes
			redo();
		}
//		logSelectionSet("}\t\t\t  result: ");
	}

	protected void addSegment(Point[] points, int start, int end)
	{
		if( (start != end) && ! (points[start].getLocation().equals(points[end].getLocation())) ) 
		{
			Segment segment = new SegmentJoiningPoints( points[start], points[end] );
			addConstruction( segment );
			select( manifestConstruction( segment ) );
		}
	}
	
	@Override
    protected String getXmlElementName()
    {
        return "JoinPoints";
    }
	
//	@Override
//	public void undo() {
//		logSelectionSet("undo()\t\t\t{ inputs: ");
//		super.undo();
//		logSelectionSet("}\t\t\t  result: ");
//	}
//
//	@Override
//	public void redo() {
//		logSelectionSet("redo()\t\t\t{ inputs: ");
//		super.redo();
//		logSelectionSet("}\t\t\t  result: ");
//	}
//	
//	private void logSelectionSet(String msg)
//	{
//		StringBuilder log = new StringBuilder(msg + " ");
//		String delim = "";
//		for (Manifestation man : mSelection) {
//			log.append(delim);
//			log.append(man.toString());
//			delim = ",  ";
//		}
//		// I suppose this should be written to a logger, but this is quicker
//		// and I'll remove this code before merging into the main git branch.
//		System.out.println(log.toString());
//	}	

}

package com.vzome.core.tools;

import com.vzome.core.construction.LineReflection;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.Transformation;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Strut;

public class LineReflectionTool extends TransformationTool {
    public LineReflectionTool(String id, ToolsModel tools) {
        super(id, tools);
        this. setCopyColors( false );
    }

    @Override
    protected String checkSelection(boolean prepareTool) {
        Segment axis = null;
        for (Manifestation man : mSelection) {
            if (prepareTool) {
                unselect(man);
            }
            if (man instanceof Strut) {
                if (axis != null) {
                    if (prepareTool) {
                        break;
                    } else {
                        return "Only one mirror axis strut may be selected";
                    }
                }
                axis = (Segment) ((Strut) man).getFirstConstruction();
            }
        }
        if (axis == null) {
            return "line reflection tool requires a single strut";
        }

        if (prepareTool) {
            this.transforms = new Transformation[1];
            transforms[0] = new LineReflection(axis);
        }

        return null;
    }

    @Override
    protected String getXmlElementName() {
        return "LineReflectionTool";
    }

}

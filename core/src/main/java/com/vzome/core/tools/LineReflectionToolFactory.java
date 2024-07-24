package com.vzome.core.tools;

import com.vzome.core.editor.AbstractToolFactory;
import com.vzome.core.editor.Tool;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.editor.api.Selection;

public class LineReflectionToolFactory extends AbstractToolFactory {
    static final String ID = "line reflection";
    static final String LABEL = "Create a line reflection tool";
    static final String TOOLTIP = "<p>" 
            + "Each tool duplicates the selection by reflecting<br>"
            + "each object in a line.  To create a tool,<br>" 
            + "define the mirror line by selecting a strut.<br>"
            + "</p>";

    public LineReflectionToolFactory(ToolsModel tools) {
        super(tools, null, ID, LABEL, TOOLTIP);
    }

    @Override
    public Tool createToolInternal(String id) {
        return new LineReflectionTool(id, getToolsModel());
    }

    @Override
    protected boolean countsAreValid(int total, int balls, int struts, int panels) {
        return (total == 1 && struts == 1);
    }

    @Override
    protected boolean bindParameters(Selection selection) {
        return true;
    }

}

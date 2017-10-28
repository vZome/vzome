package org.vorthmann.j3d;



/**
 * Description here.
 * 
 * @author Scott Vorthmann 2003
 */
public interface Tool
{
    public abstract String getToolName();

    public abstract void toolSelected();
    
    public abstract void toolUnselected();

    public abstract MouseToolDefault getMouseTool();
}

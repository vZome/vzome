
//(c) Copyright 2010, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import com.vzome.core.construction.Construction;
import com.vzome.core.model.Manifestation;

public interface Tool
{
	void prepare( ChangeConstructions applyTool );
    
    void performEdit( Construction c, ChangeConstructions edit );

	void performSelect( Manifestation man, ChangeConstructions applyTool );

	void complete( ChangeConstructions applyTool );
	
    String getName();
    
    String getCategory();
    
    boolean needsInput();
    
    interface Registry
    {
        Tool getTool( String name );

        void addTool( Tool tool );

        void removeTool( Tool tool );
        
        void useTool( Tool tool );
    }
}

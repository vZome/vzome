
//(c) Copyright 2010, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import com.vzome.core.construction.Construction;
import com.vzome.core.model.Manifestation;

public interface Tool
{
	void prepare( ChangeManifestations applyTool );
    
    void performEdit( Construction c, ChangeManifestations edit );

	void performSelect( Manifestation man, ChangeManifestations applyTool );

	void complete( ChangeManifestations applyTool );
	
    String getName();
    
    String getCategory();
    
    boolean needsInput();
    
    interface Registry
    {
        Tool getTool( String name );

        void addTool( Tool tool );

        Tool findEquivalent( Tool tool );
    }

	boolean isValidForSelection();
}

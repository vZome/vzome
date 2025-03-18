
package com.vzome.core.edits;

import com.vzome.core.editor.api.EditorModel;

public class RunZomodScript extends RunZomicScript
{

	@Override
    protected String getXmlElementName()
    {
        return "RunZomodScript";
    }
    
    protected String getScriptDialect()
    {
        return "zomod";
    }

    public RunZomodScript( EditorModel editor )
    {
        super( editor );
    }

}

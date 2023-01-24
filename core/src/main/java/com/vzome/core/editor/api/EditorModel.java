package com.vzome.core.editor.api;

import com.vzome.core.editor.SelectionSummary;
import com.vzome.core.model.RealizedModel;

public interface EditorModel extends SymmetryAware
{
    RealizedModel getRealizedModel();

    Selection getSelection();
    
    void addSelectionSummaryListener( SelectionSummary.Listener listener );
}

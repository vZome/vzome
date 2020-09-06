package com.vzome.core.editor.api;

import com.vzome.core.model.RealizedModel;

public interface EditorModel
{
    RealizedModel getRealizedModel();

    Selection getSelection();
}

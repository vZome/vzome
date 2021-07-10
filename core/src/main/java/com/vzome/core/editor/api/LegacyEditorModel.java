package com.vzome.core.editor.api;

import com.vzome.core.construction.Construction;

public interface LegacyEditorModel extends ImplicitSymmetryParameters
{
    void addFailedConstruction( Construction cons );

    boolean hasFailedConstruction( Construction cons );
}

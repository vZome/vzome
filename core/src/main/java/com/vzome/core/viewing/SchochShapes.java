package com.vzome.core.viewing;

import java.io.File;

import com.vzome.core.math.symmetry.AbstractSymmetry;

public class SchochShapes extends ExportedVEFShapes
{
    private static final long serialVersionUID = 1L;

    public SchochShapes( File prefsFolder, String name, String alias, AbstractSymmetry symmetry, AbstractShapes defaultShapes)
    {
      super( prefsFolder, name, alias, symmetry, defaultShapes );
    }

    @Override
    public double getCmScaling()
    {
      return 1d; // "cubic" blue strut has internal length 2, and Linus wants that to be 2cm in reality
    }
}


package com.vzome.core.math;

import java.util.HashMap;
import java.util.Map;

import com.vzome.core.math.symmetry.Symmetry;

public interface IntegralNumberField extends GoldenMatrix.Factory
{
    String getName();
    
    void addSymmetry( Symmetry symm );
    
    Symmetry[] getSymmetries();
    
    Map FIELDS = new HashMap();
}

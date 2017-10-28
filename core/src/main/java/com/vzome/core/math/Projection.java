
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.math;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;

public interface Projection
{
    AlgebraicVector projectImage( AlgebraicVector source, boolean wFirst );
    
    public static class Default implements Projection
    {
        private final AlgebraicField field;
        
        public Default( AlgebraicField field )
        {
            super();
            this.field = field;
        }

        @Override
        public AlgebraicVector projectImage( AlgebraicVector source, boolean wFirst )
        {
            // we ignore wFirst here, since it only applies to Quaternion projection
            return field .projectTo3d( source, wFirst );
        }
    }
}

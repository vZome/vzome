
package com.vzome.core.math;

import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;

public interface Projection
{
    AlgebraicVector projectImage( AlgebraicVector source, boolean wFirst );
    void getXmlAttributes( Element element );
    void setXmlAttributes( Element xml );
    String getProjectionName();
    
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
        
        @Override
        public void getXmlAttributes( Element element ) {}

        @Override
        public void setXmlAttributes( Element xml ) {}

        @Override
        public String getProjectionName()
        {
            return "";
        }
    }
}

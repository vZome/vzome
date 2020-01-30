package com.vzome.core.math;

import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.XmlSaveFormat;

/**
 * @author Scott Vorthmann
 */
public class SixCubeProjection implements Projection
{
    private final AlgebraicField field;
    private final AlgebraicVector[] basis;

    public SixCubeProjection( AlgebraicField field )
    {
        this.field = field;

        AlgebraicNumber zero = field.zero();
        AlgebraicNumber one = field.one();
        AlgebraicNumber nOne = one.negate();
        AlgebraicNumber phi = field .createPower( 1 );

        basis = new AlgebraicVector[ 6 ];
        basis[0] = new AlgebraicVector( phi, one, zero );
        basis[1] = new AlgebraicVector( phi, nOne, zero );
        basis[2] = new AlgebraicVector( zero, phi, one );
        basis[3] = new AlgebraicVector( zero, phi, nOne );
        basis[4] = new AlgebraicVector( one, zero, phi );
        basis[5] = new AlgebraicVector( nOne, zero, phi );
    }

    @Override
    public AlgebraicVector projectImage( AlgebraicVector source, boolean wFirst )
    {
        AlgebraicVector result = field.origin(basis[0].dimension());
        int pos = wFirst ? 0 : basis.length - 1;
        for ( AlgebraicVector unitVector : basis ) {
            AlgebraicNumber scalar = source.getComponent( pos );
            result = result.plus( unitVector.scale( scalar ) );
            pos = (pos+1) % basis.length;
        }
        return result;
    }

    @Override
    public void getXmlAttributes( Element element ) {}

    @Override
    public void setXmlAttributes( Element xml, XmlSaveFormat format ) {}

    @Override
    public String getProjectionName()
    {
        return "SixCube";
    }

}

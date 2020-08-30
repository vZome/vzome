package com.vzome.core.model;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Color;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.FreePoint;

/**
 * @author Scott Vorthmann
 */
public class ConnectorImpl extends ManifestationImpl implements Comparable<ConnectorImpl>
{

	public ConnectorImpl( AlgebraicVector loc )
	{
		super();
		this.setColor(Color.WHITE);
		m_center = loc;
	}

	private final AlgebraicVector m_center;

	@Override
	public AlgebraicVector getLocation()
    {
		return m_center;
	}

	@Override
	public AlgebraicVector getCentroid()
    {
		return m_center;
	}

    @Override
    public Construction toConstruction()
    {
        Construction first = this .getFirstConstruction();
        if ( first .is3d() )
            return first;
        
        AlgebraicField field = m_center .getField();
        return new FreePoint( field .projectTo3d( m_center, true ) );
    }

	@Override
	public int hashCode()
	{
	    return m_center .hashCode();
	}

	@Override
	public  boolean equals( Object other )
	{
		if ( other == null )
			return false;
		if ( other == this )
			return true;
		if ( ! ( other instanceof ConnectorImpl ) )
			return false;
		ConnectorImpl conn = (ConnectorImpl) other;
		return this .getLocation() .equals( conn .getLocation() );
	}

	@Override
	public int compareTo(ConnectorImpl other) {
        if ( this == other ) {
            return 0;
        }
        if (other.equals(this)) { // intentionally throws a NullPointerException if other is null
            return 0;
        }
		return this.getLocation().compareTo( other.getLocation() );
	}
	
	@Override
    public String toString()
    {
        return "connector at " + m_center .toString();
    }
}

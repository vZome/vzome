
//(c) Copyright 2013, Scott Vorthmann.

package com.vzome.core.algebra;

public class Bivector3d
{
	private final AlgebraicNumber a, b, c;

	public Bivector3d( AlgebraicNumber a, AlgebraicNumber b, AlgebraicNumber c )
	{
		this .a = a;
		this .b = b;
		this .c = c;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a == null) ? 0 : a.hashCode());
		result = prime * result + ((b == null) ? 0 : b.hashCode());
		result = prime * result + ((c == null) ? 0 : c.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Bivector3d other = (Bivector3d) obj;
		if (a == null) {
			if (other.a != null) {
				return false;
			}
		} else if (!a.equals(other.a)) {
			return false;
		}
		if (b == null) {
			if (other.b != null) {
				return false;
			}
		} else if (!b.equals(other.b)) {
			return false;
		}
		if (c == null) {
			if (other.c != null) {
				return false;
			}
		} else if (!c.equals(other.c)) {
			return false;
		}
		return true;
	}

	/**
	 * The pseudoscalar is implied in the result.
	 * @param v
	 * @return
	 */
	public AlgebraicNumber outer( Vector3d v )
	{
	    AlgebraicNumber a = this.a .times( v .c );
	    AlgebraicNumber b = this.b .times( v .a );
	    AlgebraicNumber c = this.c .times( v .b );
		return a .plus( b ) .plus( c );
	}

}

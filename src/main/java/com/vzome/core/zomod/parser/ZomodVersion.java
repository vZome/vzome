/*
 * Created on Jun 9, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.vzome.core.zomod.parser;


/**
 * @author vorth
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ZomodVersion
{
	public static final String ZOMOD_INPUT_VERSION = "zomod.input.version";
	
	public static final String ZOMOD_OUTPUT_VERSION = "zomod.output.version";

	public static final String ZOMOD_VERSION_1_4 = "1.4";

	public static final String ZOMOD_VERSION_1_5 = "1.5";

	public static final String ZOMOD_VERSION_1_5_1 = "1.5.1";

	private final String m_versionNum;
	
	private final boolean m_input;

	
	public boolean sizeSupported( int size ){
		if ( size < 1 )
			throw new Limitation( m_versionNum + ": sizes < 1 not available");
		if ( size > 9 )
			throw new Limitation( m_versionNum + ": sizes > 9 not available");
		if ( size > 3 && ! m_versionNum .equals( ZOMOD_VERSION_1_5_1 ) )
			throw new Limitation( m_versionNum + ": sizes > 3 not available");
		return true;
	}
	
	public boolean supportsHalfStruts(){
		if ( m_versionNum .equals( ZOMOD_VERSION_1_4 ) )
			throw new Limitation( m_versionNum + ": half struts not available");
		else
			return true;
	}

	public boolean supportsLine( int color )
	{
//		switch (color) {
//			case ORANGE :
//			case BLACK  :
//			case PURPLE :
//				throw new Limitation( m_versionNum + ": "
//					+ Utilities .describeColor(color) + " direction not available");
//
//			default :
//				return true;
//		}
	    return true; // TODO fix this if it is still used
	}

	public static final int[] GREEN_AXIS_PERMUTATION = new int[]{ 35, 57, 49, 48 };

	public int mapGreenAxisName( int axis ){
		if ( m_versionNum .equals( ZOMOD_VERSION_1_4 ) )
			for ( int i = 0; i < 4; i++ )
				if ( m_input ) {
					if ( axis == GREEN_AXIS_PERMUTATION[ (i+1) % 4 ] ) {
						axis = GREEN_AXIS_PERMUTATION[ i ];
						break;
					}
				}
				else
					if ( axis == GREEN_AXIS_PERMUTATION[i] ) {
						axis = GREEN_AXIS_PERMUTATION[ (i+1) % 4 ];
						break;
					}
		return axis;
	}
	
	private ZomodVersion( String pref, String direction ){
		m_versionNum = pref == null ? ZOMOD_VERSION_1_5_1 : pref;
		m_input = direction .equals( ZOMOD_INPUT_VERSION );
	}

	public static ZomodVersion getInputVersion( String version )
	{
		return new ZomodVersion( version, ZOMOD_INPUT_VERSION );
	}

	public static ZomodVersion getOutputVersion( String version )
	{
		return new ZomodVersion( version, ZOMOD_OUTPUT_VERSION );
	}
	
	public static class Limitation extends RuntimeException{
		
		public Limitation( String mesg ){
			super( "Zomod " + mesg );
		}
	}
}

package com.vzome.core.editor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class VersionComparisonTest {

	@Test
	public void testVersionComparison()
	{
		Application app = new Application( false, null, null )
		{
			@Override
			public String getCoreVersion()
			{
				return "0.8.10";
			}
		};
		DocumentModel doc = app .createDocument( "golden" );

		assertTrue( doc .fileIsTooNew( "100.100.100" ) );
		
		assertFalse( doc .fileIsTooNew( "0.8.9" ) );
		
		assertTrue( doc .fileIsTooNew( "0.8.19" ) );
		
		assertFalse( doc .fileIsTooNew( "fred.joe" ) );
		
		assertFalse( doc .fileIsTooNew( null ) );
	}

}

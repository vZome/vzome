package com.vzome.core.apps;

import java.util.Properties;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vzome.core.editor.FieldApplication;
import com.vzome.core.editor.SymmetrySystem;
import com.vzome.core.kinds.GoldenFieldApplication;
import com.vzome.core.render.Colors;

public class DumpSymmetrySystemJson
{
	public static void main(String[] args)
	{
		FieldApplication app = new GoldenFieldApplication();
		Colors colors = new Colors( new Properties() );
		SymmetrySystem system = new SymmetrySystem( null, app .getDefaultSymmetryPerspective(), null, colors, true );
		ObjectMapper mapper = new ObjectMapper();
		try {
			System .out .println( mapper .writeValueAsString( system ) );
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

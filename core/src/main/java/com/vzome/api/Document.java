
//(c) Copyright 2011, Scott Vorthmann.

package com.vzome.api;

import java.util.HashSet;
import java.util.Set;

import com.vzome.core.editor.DocumentModel;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.render.Colors;
import com.vzome.core.render.OpenGlSceneLoader;
import com.vzome.core.viewing.Camera;
import com.vzome.opengl.Scene;

public class Document
{
	final DocumentModel delegate;
	private final Set<Ball> balls = new HashSet<>();
	private final Set<Strut> struts = new HashSet<>();

	public Document( DocumentModel delegate )
	{
		this .delegate = delegate;		
		for ( Manifestation manifestation : this .delegate .getEditorModel() .getRealizedModel() )
			if ( ! manifestation .isHidden() )
			{
				if ( manifestation instanceof Connector )
					balls .add( new Ball( (Connector) manifestation ) );
				else if ( manifestation instanceof com.vzome.core.model.Strut )
					struts .add( new Strut( (com.vzome.core.model.Strut) manifestation ) );
			}
	}

	public Set<Ball> getBalls()
	{
		return this .balls;
	}

	public Set<Strut> getStruts()
	{
		return this .struts;
	}
	
//	public Command newCommand()
//	{
//		ApiEdit edit = (ApiEdit) this .delegate .createEdit( "apiProxy" );
//		return new Command( edit .createDelegate() );
//	}
	
	public Camera getCamera()
	{
	    return this .delegate .getCamera();
	}
	
	public Scene getOpenGlScene( Colors colors )
	{
		return OpenGlSceneLoader .getOpenGlScene( this .delegate .getRenderedModel(), colors, 0.05f );
	}

	public DocumentModel getDocumentModel()
	{
		return this .delegate;
	}
}


//(c) Copyright 2011, Scott Vorthmann.

package com.vzome.core.editor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.math.DomUtils;
import com.vzome.core.viewing.Camera;

public class PageModel
{
	private String title;
	private String content;
	private Camera view;
	private int snapshot;
	private boolean thumbnailCurrent = false;
	private Object thumbnail;

	public PageModel( String title, String content, Camera view, int snapshot )
	{
		this.setTitle(title);
		this.setContent(content);
		this .view = view;
		this .snapshot = snapshot;
	}

    public Element getXml( Document doc )
    {
        Element result = doc .createElement( "page" );
        DomUtils .addAttribute( result, "title", title );
        DomUtils .addAttribute( result, "snapshot", Integer .toString( snapshot ) );
        result .appendChild( this .view .getXML( doc ) );
        Element contentElem = doc .createElement( "content" );
        DomUtils .preserveSpace( contentElem );
        contentElem .appendChild( doc .createTextNode( content ) );
        result .appendChild( contentElem );
        return result;
    }

	public int getSnapshot()
	{
		return snapshot;
	}

	public void setSnapshot( int snapshot )
	{
		this.snapshot = snapshot;
	}

	public Camera getView()
	{
		return view;
	}

	public void setView( Camera view )
	{
		this.view = view;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent( String content )
	{
		this.content = content;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle( String title )
	{
		this.title = title;
	}

	public void setThumbnailCurrent( boolean b )
	{
		this .thumbnailCurrent = b;
	}
	
	public boolean thumbnailIsCurrent()
	{
		return this .thumbnailCurrent;
	}

	public void setThumbnail( Object thumbnail )
	{
		this .thumbnail = thumbnail;
	}
	
	public Object getThumbnail()
	{
		return this .thumbnail;
	}
}

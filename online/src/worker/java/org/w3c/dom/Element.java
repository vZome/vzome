package org.w3c.dom;

public interface Element extends Node
{
  public Document getOwnerDocument();

  public void setAttribute( String name, String value );

  public NodeList getElementsByTagName( String name );

  public void setAttributeNS( String namespaceURI, String qualifiedName, String value );

  public String getAttribute(String name);

}

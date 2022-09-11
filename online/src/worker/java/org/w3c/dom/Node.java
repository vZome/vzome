package org.w3c.dom;

public interface Node
{
  public Node appendChild( Node newChild );

  public NodeList getChildNodes();

  public String getTextContent();

  public String getLocalName();
}

package com.vzome.core.commands;

import java.util.TreeMap;

/**
 * @author David Hall
 * This class doesn't add anything to the TreeMap, 
 * but there were so many places that were using Map<String, Object>,
 * that I decided to use this class to clarify which ones were using it 
 * for managing attributes. It also avoids cluttering the code 
 * with Map<String, Object> which is just needed for type safety, 
 * but which does nothing for describing the intended use of the variables.
 * 
 * Note that in XmlSaveFormat, AttributeMap replaces TreeMap<>, 
 * but in other places (such as CommandEdit), it replaces HashMap<>. 
 * XmlSaveFormat requires the Map<> to be ordered, but I assume 
 * that other places can safely use any implementation of Map<>.
 * Therefore, I have used TreeMap<> rather than HashMap<> as the basis 
 * for AttributeMap across the board.
 */
public class AttributeMap
// XmlSaveFormat.loadCommandAttributes() needs this to be ordered for the purpose of regression testing
extends TreeMap<String, Object>
{    
}

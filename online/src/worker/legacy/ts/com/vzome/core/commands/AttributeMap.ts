/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.commands {
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
     * @class
     * @extends java.util.TreeMap
     */
    export class AttributeMap extends java.util.TreeMap<string, any> {
        static __com_vzome_core_commands_AttributeMap_serialVersionUID: number = 1;

        constructor() {
            super();
        }
    }
    AttributeMap["__class"] = "com.vzome.core.commands.AttributeMap";
    AttributeMap["__interfaces"] = ["java.lang.Cloneable","java.util.Map","java.util.NavigableMap","java.util.SortedMap","java.io.Serializable"];


}


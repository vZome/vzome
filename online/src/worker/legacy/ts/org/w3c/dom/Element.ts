/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace org.w3c.dom {
    export interface Element extends org.w3c.dom.Node {
        getOwnerDocument(): org.w3c.dom.Document;

        setAttribute(name: string, value: string);

        getElementsByTagName(name: string): org.w3c.dom.NodeList;

        setAttributeNS(namespaceURI: string, qualifiedName: string, value: string);

        getAttribute(name: string): string;

        setTextContent(text: string);

        getUserData(key: string): any;
    }
}


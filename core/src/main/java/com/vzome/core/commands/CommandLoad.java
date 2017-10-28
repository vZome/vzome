
package com.vzome.core.commands;


import org.w3c.dom.Element;

import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.ConstructionList;

/**
 * @author Scott Vorthmann
 *
 */
public class CommandLoad extends AbstractCommand
{
    public static final String XML_ATTR = "xml";

    private static final Object[][] PARAM_SIGNATURE = new Object[][]{};

    private static final Object[][] ATTR_SIGNATURE = new Object[][]{ { XML_ATTR, Element.class } };


    @Override
    public Object[][] getParameterSignature()
    {
        return PARAM_SIGNATURE;
    }


    @Override
    public Object[][] getAttributeSignature()
    {
        return ATTR_SIGNATURE;
    }


    @Override
    public ConstructionList apply( ConstructionList parameters, AttributeMap attributes,
                                    ConstructionChanges effects )
    throws Failure
    {
        ConstructionList result = new ConstructionList();
//        
//        Element element = (Element) attributes .get( XML_ATTR );
//        ModelRoot root = (ModelRoot) attributes .get( MODEL_ROOT_ATTR_NAME );
//
//        Map<String, ModelRoot> index = new HashMap<>();
//        index .put( "root", root );
//        
//        Elements children = element .getChildElements();
//        for ( int i = 0; i < children .size(); i++ ) {
//            Element child = children .get( i );
//            String name = child .getLocalName();
//            Construction c = null;
//            if ( name .equals( "FreePoint" ) )
//                c = FreePoint .load( child, index );
//            else if ( name .equals( "SegmentJoiningPoints" ) )
//                c = SegmentJoiningPoints .load( child, index );
//            if ( c != null ) {
//                effects .constructionAdded( c );
//                result .addConstruction( c );
//            }
//        }
    
        return result;
    }

}

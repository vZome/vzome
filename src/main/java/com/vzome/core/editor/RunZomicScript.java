
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;

import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.commands.ZomicVirtualMachine;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.construction.Point;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.zomic.Interpreter;
import com.vzome.core.zomic.ZomicException;
import com.vzome.core.zomic.parser.ErrorHandler;
import com.vzome.core.zomic.parser.Parser;
import com.vzome.core.zomic.program.Anything;

public class RunZomicScript extends ChangeConstructions
{
    private String programText;
    private Anything zomicProgram;
    private Point origin;
    private IcosahedralSymmetry symm;

    public RunZomicScript( Selection selection, RealizedModel realized, String text, Point origin )
    {
        super( selection, realized, false );
        this .programText = text;
        this .origin = origin;
        if ( origin != null )
            this .symm = (IcosahedralSymmetry) origin .getField() .getSymmetry( "icosahedral" );
    }

    protected String getXmlElementName()
    {
        return "RunZomicScript";
    }

    protected void getXmlAttributes( Element element )
    {
        element .setTextContent( XmlSaveFormat .escapeNewlines( programText ) );
    }

    protected void setXmlAttributes( Element xml, XmlSaveFormat format )
            throws Failure
    {
        programText = xml .getTextContent();
        this .symm = (IcosahedralSymmetry) format .parseSymmetry( "icosahedral" );
        zomicProgram = parseScript( programText );
    }
    
    protected Anything parseScript( String script ) throws Failure
    {
        Parser parser = new Parser( symm );
        List errors = new ArrayList();
        Anything program = parser .parse(
            new ByteArrayInputStream( script .getBytes() ), new ErrorHandler.Default( errors ), "" );
        if ( errors.size() > 0 )
            throw new Failure( (String) errors .get(0) );
        return program;
    }

    public void perform() throws Failure
    {
        Point offset = null;
        boolean pointFound = false;
        for ( Iterator mans = mSelection .iterator(); mans .hasNext(); ) {
            Manifestation man = (Manifestation) mans .next();
            if ( man instanceof Connector )
            {
                Point nextPoint = (Point) ((Connector) man) .getConstructions() .next();
                if ( ! pointFound )
                {
                    pointFound = true;
                    offset = nextPoint;
                }
                else
                {
                    offset = null;
                }
            }
        }
        if ( offset == null )
            offset = origin;

        if ( zomicProgram == null )
            zomicProgram = parseScript( programText );

        ZomicVirtualMachine builder = new ZomicVirtualMachine( offset, new NewConstructions(), symm );
        try {
            zomicProgram .accept( new Interpreter( builder, symm ) );
        } catch ( ZomicException e ) {
            throw new Failure( e );
        }
        redo();
    }
}


//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.edits;

import java.util.ArrayList;
import java.util.Map;

import org.w3c.dom.Element;

import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.commands.ZomicVirtualMachine;
import com.vzome.core.construction.Point;
import com.vzome.core.editor.ChangeManifestations;
import com.vzome.core.editor.EditorModel;
import com.vzome.core.editor.ManifestConstructions;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.zomic.Interpreter;
import com.vzome.core.zomic.ZomicASTCompiler;
import com.vzome.core.zomic.ZomicException;
import com.vzome.core.zomic.parser.ErrorHandler;
import com.vzome.core.zomic.program.Walk;
import com.vzome.core.zomic.program.ZomicStatement;

public class RunZomicScript extends ChangeManifestations
{
    private String programText;
    private ZomicStatement zomicProgram;
    private final Point origin;
    private IcosahedralSymmetry symm;

    public RunZomicScript( EditorModel editor )
    {
        super( editor .getSelection(), editor .getRealizedModel() );
        this.origin = editor .getCenterPoint();
		this.symm = (IcosahedralSymmetry) editor .getSymmetrySystem() .getSymmetry();
    }
    
    @Override
    public void configure( Map<String,Object> props ) 
    {
        this.programText = (String) props .get( "script" );
    }

	@Override
    protected String getXmlElementName()
    {
        return "RunZomicScript";
    }

	@Override
    protected void getXmlAttributes( Element element )
    {
        element .setTextContent( XmlSaveFormat .escapeNewlines( programText ) );
    }

	@Override
    protected void setXmlAttributes( Element xml, XmlSaveFormat format )
            throws Failure
    {
        programText = xml .getTextContent();
        this .symm = (IcosahedralSymmetry) format .parseSymmetry( "icosahedral" );
        zomicProgram = parseScript( programText );
    }
    
    protected ZomicStatement parseScript( String script ) throws Failure
    {
        ArrayList<String> errors = new ArrayList<>();
		ErrorHandler errorHandler = new ErrorHandler.Default( errors );
//        Parser parser = new Parser( symm );
//        ZomicStatement program = parser .parse(
//            new ByteArrayInputStream( script .getBytes() ), errorHandler, "" );
		ZomicASTCompiler compiler = new ZomicASTCompiler(symm);
        Walk program = compiler.compile( script, errorHandler );
        if ( errors.size() > 0 )
            throw new Failure( errors .get(0) );
        return program;
    }

	@Override
    public void perform() throws Failure
    {
        Point offset = null;
        boolean pointFound = false;
		for (Manifestation man : mSelection) {
			if ( man instanceof Connector )
			{
				Point nextPoint = (Point) ((Connector) man) .getFirstConstruction();
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

        ZomicVirtualMachine builder = new ZomicVirtualMachine( offset, new ManifestConstructions( this ), symm );
        try {
            zomicProgram .accept( new Interpreter( builder, symm ) );
        } catch ( ZomicException e ) {
            throw new Failure( e );
        }
        redo();
    }
}

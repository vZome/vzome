
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import com.vzome.core.algebra.PentagonField;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.edits.RunZomicScript;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.zomic.parser.ErrorHandler;
import com.vzome.core.zomic.program.ZomicStatement;
import com.vzome.core.zomod.parser.Parser;

public class RunZomodScript extends RunZomicScript
{

	@Override
    protected String getXmlElementName()
    {
        return "RunZomodScript";
    }

	@Override
    protected ZomicStatement parseScript( String script ) throws Failure
    {
        Parser parser = new Parser( new IcosahedralSymmetry( new PentagonField(), "default" ));
        List<String> errors = new ArrayList<>();
        ZomicStatement program = parser .parse(
            new ByteArrayInputStream( script .getBytes() ), new ErrorHandler.Default( errors ), "" );
        if ( errors.size() > 0 )
            throw new Failure( errors .get(0) );
        return program;
    }

    public RunZomodScript( EditorModel editor )
    {
        super( editor );
    }

}

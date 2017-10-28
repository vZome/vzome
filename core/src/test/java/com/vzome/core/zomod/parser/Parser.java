package com.vzome.core.zomod.parser;

import java.io.DataInputStream;
import java.io.InputStream;

import com.vzome.core.antlr.ANTLR2XML;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.zomic.parser.ErrorHandler;

import antlr.RecognitionException;
import antlr.TokenStreamException;

@Deprecated
public class Parser extends com.vzome.core.zomic.parser.Parser{


    public Parser( IcosahedralSymmetry symm )
    {
        super( symm );
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void parse( InputStream input, ANTLR2XML xml, ErrorHandler errors, String version )
    throws RecognitionException, TokenStreamException
    {
        ZomodLexer lexer = new ZomodLexer( new DataInputStream( input ) );
        ZomodParser parser = new ZomodParser( lexer );
        parser .connectHandlers( xml, errors, ZomodVersion .getInputVersion( version ) );         
        parser .program();
    }


}



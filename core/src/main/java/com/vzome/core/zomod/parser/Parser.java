package com.vzome.core.zomod.parser;

import java.io.DataInputStream;
import java.io.InputStream;

import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.zomic.parser.ErrorHandler;
import com.vzome.core.zomic.parser.XML2AST;
import com.vzome.core.zomic.program.ZomicStatement;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.TokenStreamRecognitionException;

//@SuppressWarnings( "deprecation" )
@Deprecated
public class Parser extends com.vzome.core.zomic.parser.Parser{

    private final IcosahedralSymmetry symmetry;

    public Parser( IcosahedralSymmetry symm )
    {
        super( symm );
        this.symmetry = symm;
    }

    @Override
    protected void parse( InputStream input, com.vzome.core.antlr.ANTLR2XML xml, ErrorHandler errors, String version )
    throws RecognitionException, TokenStreamException
    {
        ZomodLexer lexer = new ZomodLexer( new DataInputStream( input ) );
        ZomodParser parser = new ZomodParser( lexer );
        parser .connectHandlers( xml, errors, ZomodVersion .getInputVersion( version ) );         
        parser .program();
    }

    public 
    ZomicStatement parse( InputStream input, ErrorHandler errors, String version ) {
        XML2AST handler = new XML2AST( symmetry );
        try  {
            com.vzome.core.antlr.ANTLRContentSupplier xml = new com.vzome.core.antlr.ANTLRContentSupplier( handler );
            parse( input, xml, errors, version );
        } catch( RecognitionException e ) {
            errors .parseError( e .getLine(), e .getColumn(), e .getMessage() );
        } catch (TokenStreamRecognitionException e) {
            RecognitionException re = e.recog;
            errors .parseError( re .getLine(), re .getColumn(), re .getMessage() );
        } catch (TokenStreamException e) {
            errors .parseError( ErrorHandler.UNKNOWN, ErrorHandler.UNKNOWN, e .getMessage() );
        }
        return handler .getProgram();
    }

    public static 
    ZomicStatement parse( InputStream input, IcosahedralSymmetry symm ) {
        ErrorHandler.Default errors = new ErrorHandler.Default();
        ZomicStatement program = new Parser( symm ) .parse( input, errors, null );
        program .setErrors( errors .getErrors() );
        return program;
    }

}



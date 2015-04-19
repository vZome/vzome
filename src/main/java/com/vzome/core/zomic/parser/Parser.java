
package com.vzome.core.zomic.parser;

import java.io.DataInputStream;
import java.io.InputStream;

import com.vzome.core.antlr.ANTLR2XML;
import com.vzome.core.antlr.ANTLRContentSupplier;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.zomic.program.ZomicStatement;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.TokenStreamRecognitionException;

public class Parser
{
    private final IcosahedralSymmetry symm;

    public Parser( IcosahedralSymmetry symm )
    {
        this.symm = symm;
    }

    public 
    ZomicStatement parse( InputStream input, ErrorHandler errors, String version ) {
        XML2AST handler = new XML2AST( symm );
        try  {
            ANTLRContentSupplier xml = new ANTLRContentSupplier( handler );
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

    protected void parse( InputStream input, ANTLR2XML xml, ErrorHandler errors, String version )
    throws RecognitionException, TokenStreamException
    {
        ZomicLexer lexer = new ZomicLexer( new DataInputStream( input ) );
        ZomicParser parser = new ZomicParser( lexer );
        parser .connectHandlers( xml, errors );         
        parser .compound_stmt();
    }

    public static 
    ZomicStatement parse( InputStream input, IcosahedralSymmetry symm ) {
        ErrorHandler.Default errors = new ErrorHandler.Default();
        ZomicStatement program = new Parser( symm ) .parse( input, errors, null );
        program .setErrors( errors .getErrors() );
        return program;
    }


}



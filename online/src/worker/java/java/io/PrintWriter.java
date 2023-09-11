package java.io;

public class PrintWriter extends Writer
{
    private final Writer w;

    public PrintWriter( Writer w )
    {
        super();
        this .w = w;
    }
    
    public void flush()
    {
        try {
            this .w .flush();
        } catch ( java.io.IOException e ) {
            e .printStackTrace();
        }
    }

    public void close()
    {
        try {
            this .w .close();
        } catch ( java.io.IOException e ) {
            e .printStackTrace();
        }
    }
    
    public void write( char cbuf[], int off, int len )
    {
        try {
            this .w .write( cbuf, off, len );
        } catch ( java.io.IOException e ) {
            e .printStackTrace();
        }
    }
    
    public void println()
    {
        print( "\n" );
    }
    
    public void print( Object x )
    {
        if ( ! (x instanceof String) )
            x = x .toString();
        char[] chars = ((String) x) .toCharArray();
        this .write( chars, 0, chars.length );
    }
    
    public void println( Object x )
    {
        print( x );
        println();
    }
}

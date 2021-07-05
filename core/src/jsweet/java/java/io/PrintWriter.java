package java.io;

public class PrintWriter extends Writer
{
    public PrintWriter( Writer w )
    {
        super();
    }
    
    public void flush() {}

    public void close() {}
    
    public void write( char cbuf[], int off, int len ) {}
    
    public void println() {}
    
    public void print( Object x ) {}
    
    public void println( Object x ) {}
}

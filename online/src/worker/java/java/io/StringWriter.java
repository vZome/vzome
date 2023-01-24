package java.io;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

public class StringWriter extends Writer
{
    private final ByteArrayOutputStream baos;
    private final OutputStreamWriter w;

    public StringWriter()
    {
        super();
        this .baos = new ByteArrayOutputStream();
        this .w = new OutputStreamWriter( baos );
    }

    public String toString()
    {
        return this .baos .toString();
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
}

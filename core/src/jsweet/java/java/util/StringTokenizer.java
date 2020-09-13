package java.util;

import def.js.RegExp;
import java.util.Arrays;
import jsweet.lang.Replace;

public class StringTokenizer
{
    private final Iterator<String> tokens;

    public StringTokenizer( String str )
    {
        this( str, null );
    }

    public StringTokenizer( String str, String seps )
    {
        String pattern = ( seps == null ) ? "\\s+" : "[" + seps + "]+";
        RegExp regExp = new RegExp( pattern );
        String[] strs = splitRegExp( str, regExp );
        
        // JS split can leave a trailing "" when there is whitespace after the last token
        int last = strs.length - 1;
        if ( strs[ last ] == "" )
            strs = Arrays.copyOf( strs, last );

        this .tokens = Arrays.asList( strs ) .iterator();
    }
    
    @Replace("return str.split( regExp )")
    private String[] splitRegExp( String str, RegExp regExp )
    {
        return null; // this gets replaced by JSweet due to the annotation
    }

    public boolean hasMoreTokens()
    {
        return this .tokens .hasNext();
    }
    
    public String nextToken()
    {
        return this .tokens .next();
    }
}

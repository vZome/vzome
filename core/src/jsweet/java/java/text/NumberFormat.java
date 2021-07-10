package java.text;

import java.util.Locale;

public class NumberFormat
{
    public static NumberFormat getNumberInstance( Locale us )
    {
        return new NumberFormat();
    }

    public void setMaximumFractionDigits( int i ) {}

    public void setMinimumFractionDigits(int i) {}

    public String format( float x )
    {
        return Float .toString( x );
    }
}

package java.text;

import java.util.Locale;

public class NumberFormat
{
    public static NumberFormat getNumberInstance( Locale us )
    {
        return new NumberFormat();
    }
    
    public static NumberFormat getInstance()
    {
        return new NumberFormat();
    }

    public void setMaximumFractionDigits( int i ) {}

    public void setMinimumFractionDigits(int i) {}

    public String format( double x )
    {
        return Double .toString( x );
    }

    public void setGroupingUsed( boolean newValue ) {}
}

import { java, javaemul } from "../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";

/**
 * A stand-in for java.text.NumberFormat, which the j4ts runtime does not provide.
 *
 * The fraction-digit setters used to be empty stubs and format() was string
 * concatenation, so every exporter that asked for a fixed number of digits silently
 * got the full float -- POV-Ray asks for 8 and got "4.236067771911621" where Java
 * writes "4.23606798".  That made every exported file differ from its Java
 * counterpart.  Twelve classes construct one of these (DxfExporter, OffExporter,
 * VRMLExporter, STEPExporter, RealVector, ...), so this is not POV-Ray-specific.
 *
 * Two deliberate departures from java.text.NumberFormat:
 *
 *  - Grouping separators are never emitted.  Java's default is to group, so it would
 *    write "1,234,567.891", which none of these machine-readable formats can parse.
 *    Only SnapshotExporter calls setGroupingUsed(false) explicitly; the rest simply
 *    never exercise values large enough to notice.
 *  - NaN and Infinity fall back to plain string conversion ("NaN", "Infinity") rather
 *    than Java's "NaN" and the Unicode infinity sign.  No caller should reach them.
 */
export class NumberFormat {

    /*private*/ maximumFractionDigits: number = 3;   // java.text.NumberFormat's default
    /*private*/ minimumFractionDigits: number = 0;

    public static getNumberInstance(us: java.util.Locale): NumberFormat {
        return new NumberFormat();
    }

    public static getInstance(): NumberFormat {
        return new NumberFormat();
    }

    //  Java keeps min <= max by pushing the *other* bound, in both directions.  This
    //  matters: OffExporter only calls setMinimumFractionDigits(16), and relies on that
    //  raising the maximum from the default 3 to 16.
    public setMaximumFractionDigits(i: number) {
        this.maximumFractionDigits = Math.max( 0, i );
        if ( this.minimumFractionDigits > this.maximumFractionDigits )
            this.minimumFractionDigits = this.maximumFractionDigits;
    }

    public setMinimumFractionDigits(i: number) {
        this.minimumFractionDigits = Math.max( 0, i );
        if ( this.maximumFractionDigits < this.minimumFractionDigits )
            this.maximumFractionDigits = this.minimumFractionDigits;
    }

    public format(x: number): string {
        const value: number = Number( x );
        if ( ! isFinite( value ) )
            return '' + x;

        const negative: boolean = value < 0 || Object.is( value, -0 );
        const magnitude: number = Math.abs( value );

        //  Format from the SHORTEST round-trip decimal, not from toFixed().
        //
        //  This is the whole trick.  Java formats via Double.toString, which yields the
        //  shortest decimal that round-trips, and String(x) in JavaScript yields exactly
        //  the same digits.  Calling toFixed() instead expands the true binary value, so
        //  the two diverge past ~15 significant digits: for 4.23606797749979 at 16 digits
        //  Java writes 4.2360679774997900 and toFixed(16) gives 4.2360679774997898.
        //  OffExporter asks for 16, so that path is live, not hypothetical.
        let text: string = String( magnitude );
        if ( text.indexOf( 'e' ) >= 0 || text.indexOf( 'E' ) >= 0 ) {
            //  Values >= 1e21 (and very small ones) stringify in exponential form, which
            //  no consumer of these exporters can parse.  toFixed caps at 100 fraction
            //  digits, so the large case needs an integer expansion instead.
            text = magnitude >= 1e21
                 ? BigInt( magnitude ).toString()
                 : magnitude.toFixed( Math.min( this.maximumFractionDigits, 100 ) );
        }

        let dot: number = text.indexOf( '.' );
        let whole: string = dot < 0 ? text : text.substring( 0, dot );
        let fraction: string = dot < 0 ? '' : text.substring( dot + 1 );

        if ( fraction.length > this.maximumFractionDigits ) {
            const rounded: string = Number( text ).toFixed( Math.min( this.maximumFractionDigits, 100 ) );
            dot = rounded.indexOf( '.' );
            whole = dot < 0 ? rounded : rounded.substring( 0, dot );
            fraction = dot < 0 ? '' : rounded.substring( dot + 1 );
        }

        //  Trim trailing zeros, but never below minimumFractionDigits.
        let end: number = fraction.length;
        while ( end > this.minimumFractionDigits && fraction.charAt( end - 1 ) === '0' )
            end --;
        fraction = fraction.substring( 0, end );
        while ( fraction.length < this.minimumFractionDigits )
            fraction += '0';

        const body: string = fraction.length > 0 ? whole + '.' + fraction : whole;
        return negative ? '-' + body : body;
    }

    public setGroupingUsed(newValue: boolean) {
        //  Never grouped; see the class comment.
    }
}
NumberFormat["__class"] = "java.text.NumberFormat";

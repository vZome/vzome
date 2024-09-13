/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    /**
     * @author Scott Vorthmann
     * @param {number} r
     * @param {number} g
     * @param {number} b
     * @param {number} a
     * @class
     */
    export class Color {
        public static BLACK: Color; public static BLACK_$LI$(): Color { if (Color.BLACK == null) { Color.BLACK = new Color(0, 0, 0); }  return Color.BLACK; }

        public static WHITE: Color; public static WHITE_$LI$(): Color { if (Color.WHITE == null) { Color.WHITE = new Color(255, 255, 255); }  return Color.WHITE; }

        public static GREY_TRANSPARENT: Color; public static GREY_TRANSPARENT_$LI$(): Color { if (Color.GREY_TRANSPARENT == null) { Color.GREY_TRANSPARENT = new Color(25, 25, 25, 50); }  return Color.GREY_TRANSPARENT; }

        /*private*/ red: number;

        /*private*/ green: number;

        /*private*/ blue: number;

        /*private*/ alpha: number;

        public constructor(r?: any, g?: any, b?: any, a?: any) {
            if (((typeof r === 'number') || r === null) && ((typeof g === 'number') || g === null) && ((typeof b === 'number') || b === null) && ((typeof a === 'number') || a === null)) {
                let __args = arguments;
                if (this.red === undefined) { this.red = 0; } 
                if (this.green === undefined) { this.green = 0; } 
                if (this.blue === undefined) { this.blue = 0; } 
                if (this.alpha === undefined) { this.alpha = 0; } 
                this.red = r > 255 ? 255 : (r < 0 ? 0 : r);
                this.green = g > 255 ? 255 : (g < 0 ? 0 : g);
                this.blue = b > 255 ? 255 : (b < 0 ? 0 : b);
                this.alpha = a > 255 ? 255 : (a < 0 ? 0 : a);
            } else if (((typeof r === 'number') || r === null) && ((typeof g === 'number') || g === null) && ((typeof b === 'number') || b === null) && a === undefined) {
                let __args = arguments;
                {
                    let __args = arguments;
                    let a: any = 255;
                    if (this.red === undefined) { this.red = 0; } 
                    if (this.green === undefined) { this.green = 0; } 
                    if (this.blue === undefined) { this.blue = 0; } 
                    if (this.alpha === undefined) { this.alpha = 0; } 
                    this.red = r > 255 ? 255 : (r < 0 ? 0 : r);
                    this.green = g > 255 ? 255 : (g < 0 ? 0 : g);
                    this.blue = b > 255 ? 255 : (b < 0 ? 0 : b);
                    this.alpha = a > 255 ? 255 : (a < 0 ? 0 : a);
                }
            } else if (((typeof r === 'string') || r === null) && g === undefined && b === undefined && a === undefined) {
                let __args = arguments;
                let rgbaHex: any = __args[0];
                if (this.red === undefined) { this.red = 0; } 
                if (this.green === undefined) { this.green = 0; } 
                if (this.blue === undefined) { this.blue = 0; } 
                if (this.alpha === undefined) { this.alpha = 0; } 
                const padded: string = "00000000" + rgbaHex;
                rgbaHex = padded.substring(padded.length - 8);
                const r: number = javaemul.internal.IntegerHelper.parseInt(rgbaHex.substring(0, 2), 16);
                const g: number = javaemul.internal.IntegerHelper.parseInt(rgbaHex.substring(2, 4), 16);
                const b: number = javaemul.internal.IntegerHelper.parseInt(rgbaHex.substring(4, 6), 16);
                const a: number = javaemul.internal.IntegerHelper.parseInt(rgbaHex.substring(6, 8), 16);
                this.red = r > 255 ? 255 : (r < 0 ? 0 : r);
                this.green = g > 255 ? 255 : (g < 0 ? 0 : g);
                this.blue = b > 255 ? 255 : (b < 0 ? 0 : b);
                this.alpha = a > 255 ? 255 : (a < 0 ? 0 : a);
            } else if (((typeof r === 'number') || r === null) && g === undefined && b === undefined && a === undefined) {
                let __args = arguments;
                let rgb: any = __args[0];
                {
                    let __args = arguments;
                    let r: any = (rgb >> 16) & 255;
                    let g: any = (rgb >> 8) & 255;
                    let b: any = rgb & 255;
                    {
                        let __args = arguments;
                        let a: any = 255;
                        if (this.red === undefined) { this.red = 0; } 
                        if (this.green === undefined) { this.green = 0; } 
                        if (this.blue === undefined) { this.blue = 0; } 
                        if (this.alpha === undefined) { this.alpha = 0; } 
                        this.red = r > 255 ? 255 : (r < 0 ? 0 : r);
                        this.green = g > 255 ? 255 : (g < 0 ? 0 : g);
                        this.blue = b > 255 ? 255 : (b < 0 ? 0 : b);
                        this.alpha = a > 255 ? 255 : (a < 0 ? 0 : a);
                    }
                }
            } else throw new Error('invalid overload');
        }

        public getRGBColorComponents(rgb: number[]): number[] {
            const len: number = rgb.length;
            if (len < 3 || len > 4){
                throw new java.lang.IllegalArgumentException("Expected rgb.length to be 3 or 4. Found " + len + ".");
            }
            rgb[0] = (<any>Math).fround(this.red / 255.0);
            rgb[1] = (<any>Math).fround(this.green / 255.0);
            rgb[2] = (<any>Math).fround(this.blue / 255.0);
            if (len === 4){
                rgb[3] = (<any>Math).fround(this.alpha / 255.0);
            }
            return rgb;
        }

        /**
         * 
         * @return {number}
         */
        public hashCode(): number {
            return this.getRGBA();
        }

        /**
         * 
         * @param {*} other
         * @return {boolean}
         */
        public equals(other: any): boolean {
            if (this === other)return true;
            if (other == null)return true;
            if (!(other != null && other instanceof <any>com.vzome.core.construction.Color))return false;
            const c: Color = <Color>other;
            return this.red === c.red && this.green === c.green && this.blue === c.blue && this.alpha === c.alpha;
        }

        public getPastel(): Color {
            const r: number = this.red + ((255 - this.red) / 2|0);
            const g: number = this.green + ((255 - this.green) / 2|0);
            const b: number = this.blue + ((255 - this.blue) / 2|0);
            return new Color(r, g, b, this.alpha);
        }

        /**
         * @return
         * @return {number}
         */
        public getRGBA(): number {
            return this.red * 16777216 + this.green * 65536 + this.blue * 256 + this.alpha;
        }

        public getRGB(): number {
            return this.red * 65536 + this.green * 256 + this.blue;
        }

        /**
         * 
         * @return {string}
         */
        public toString(): string {
            return this.red + "," + this.green + "," + this.blue + ((this.alpha < 255) ? "," + this.alpha : "");
        }

        public toWebString(): string {
            return javaemul.internal.StringHelper.format("#%02X%02X%02X", this.red, this.green, this.blue);
        }

        public static parseColor(str: string): Color {
            const toks: java.util.StringTokenizer = new java.util.StringTokenizer(str, ",");
            const red: string = toks.nextToken();
            const green: string = toks.nextToken();
            const blue: string = toks.nextToken();
            return new Color(javaemul.internal.IntegerHelper.parseInt(red), javaemul.internal.IntegerHelper.parseInt(green), javaemul.internal.IntegerHelper.parseInt(blue));
        }

        public static parseWebColor(colorStr: string): Color {
            return new Color(/* intValue */(javaemul.internal.IntegerHelper.valueOf(colorStr.substring(1, 3), 16)|0), /* intValue */(javaemul.internal.IntegerHelper.valueOf(colorStr.substring(3, 5), 16)|0), /* intValue */(javaemul.internal.IntegerHelper.valueOf(colorStr.substring(5, 7), 16)|0));
        }

        public getRed(): number {
            return this.red;
        }

        public getGreen(): number {
            return this.green;
        }

        public getBlue(): number {
            return this.blue;
        }

        public getAlpha(): number {
            return this.alpha;
        }

        public static getComplement(color: Color): Color {
            return (color == null) ? null : new Color((128 + color.red) % 256, (128 + color.green) % 256, (128 + color.blue) % 256, color.alpha);
        }

        public static getInverted(color: Color): Color {
            return (color == null) ? null : new Color(255 - color.red, 255 - color.green, 255 - color.blue, color.alpha);
        }

        /**
         * @param {com.vzome.core.construction.Color} color color to be modified.
         * @param {number} scale0to1 is adjusted internally to be between 0 and 1.
         * @return {com.vzome.core.construction.Color} The original color maximized then having each component
         * multiplied by the specified scale (between 0 and 1).
         * Multiplying by 0 returns BLACK.
         * Multiplying by 1 returns the maximized color.
         */
        public static getScaledTo(color: Color, scale0to1: number): Color {
            if (color == null){
                return null;
            }
            const maxColor: Color = Color.getMaximum(color);
            const scale: number = Math.min(Math.max(0.0, scale0to1), 1.0);
            if (scale === 0.0)return Color.BLACK_$LI$();
            if (scale === 1.0)return maxColor;
            const red: number = maxColor.getRed() * scale;
            const green: number = maxColor.getGreen() * scale;
            const blue: number = maxColor.getBlue() * scale;
            return new Color(/* intValue */(red|0), /* intValue */(green|0), /* intValue */(blue|0), color.getAlpha());
        }

        /**
         * @param {com.vzome.core.construction.Color} color
         * @return {com.vzome.core.construction.Color} A new color where each of the RGB components are proportional to the parameter
         * but scaled so that the component with the highest value becomes 0xFF.
         * Other components are scaled proportionally. The alpha component is unchanged.
         * If the color is null or BLACK (0,0,0) or if one or more elements are already at 0xFF
         * then the original value is returned unchanged.
         */
        public static getMaximum(color: Color): Color {
            if (color == null){
                return null;
            }
            const most: number = Math.max(Math.max(color.red, color.green), color.blue);
            return (most === 0 || most === 255) ? color : new Color((255 * color.red / most|0), (255 * color.green / most|0), (255 * color.blue / most|0), color.alpha);
        }

        public static getPastel(color: Color): Color {
            return (color == null) ? null : color.getPastel();
        }
    }
    Color["__class"] = "com.vzome.core.construction.Color";

}


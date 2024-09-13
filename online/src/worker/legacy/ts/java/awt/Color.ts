/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace java.awt {
    export class Color {
        public static WHITE: Color; public static WHITE_$LI$(): Color { if (Color.WHITE == null) { Color.WHITE = new Color(255, 255, 255); }  return Color.WHITE; }

        public static BLACK: Color; public static BLACK_$LI$(): Color { if (Color.BLACK == null) { Color.BLACK = new Color(0, 0, 0); }  return Color.BLACK; }

        /*private*/ value: number;

        public constructor(r?: any, g?: any, b?: any, a?: any) {
            if (((typeof r === 'number') || r === null) && ((typeof g === 'number') || g === null) && ((typeof b === 'number') || b === null) && ((typeof a === 'number') || a === null)) {
                let __args = arguments;
                if (this.value === undefined) { this.value = 0; } 
                this.value = ((a & 255) << 24) | ((r & 255) << 16) | ((g & 255) << 8) | ((b & 255) << 0);
            } else if (((typeof r === 'number') || r === null) && ((typeof g === 'number') || g === null) && ((typeof b === 'number') || b === null) && a === undefined) {
                let __args = arguments;
                {
                    let __args = arguments;
                    let a: any = 255;
                    if (this.value === undefined) { this.value = 0; } 
                    this.value = ((a & 255) << 24) | ((r & 255) << 16) | ((g & 255) << 8) | ((b & 255) << 0);
                }
            } else if (((typeof r === 'number') || r === null) && g === undefined && b === undefined && a === undefined) {
                let __args = arguments;
                let rgb: any = __args[0];
                if (this.value === undefined) { this.value = 0; } 
                this.value = -16777216 | rgb;
            } else throw new Error('invalid overload');
        }

        public getRed(): number {
            return (this.value >> 16) & 255;
        }

        public getGreen(): number {
            return (this.value >> 8) & 255;
        }

        public getBlue(): number {
            return (this.value >> 0) & 255;
        }

        public getRGB(): number {
            return this.value;
        }

        public getRGBColorComponents(compArray: number[]): number[] {
            const f: number[] = [0, 0, 0];
            f[0] = (<any>Math).fround((<number>this.getRed()) / 255.0);
            f[1] = (<any>Math).fround((<number>this.getGreen()) / 255.0);
            f[2] = (<any>Math).fround((<number>this.getBlue()) / 255.0);
            return f;
        }
    }
    Color["__class"] = "java.awt.Color";

}


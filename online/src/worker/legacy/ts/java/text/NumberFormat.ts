/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace java.text {
    export class NumberFormat {
        public static getNumberInstance(us: java.util.Locale): NumberFormat {
            return new NumberFormat();
        }

        public static getInstance(): NumberFormat {
            return new NumberFormat();
        }

        public setMaximumFractionDigits(i: number) {
        }

        public setMinimumFractionDigits(i: number) {
        }

        public format(x: number): string {
            return /* toString */(''+(x));
        }

        public setGroupingUsed(newValue: boolean) {
        }
    }
    NumberFormat["__class"] = "java.text.NumberFormat";

}


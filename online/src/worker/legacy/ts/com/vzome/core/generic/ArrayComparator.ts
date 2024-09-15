/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.generic {
    /**
     * @author David Hall
     * @class
     */
    export class ArrayComparator<T extends java.lang.Comparable<T>> {
        public getContentFirstArrayComparator(): ArrayComparator.ContentFirstArrayComparator<T> {
            return <any>(new ArrayComparator.ContentFirstArrayComparator<any>());
        }

        public getLengthFirstArrayComparator(): ArrayComparator.LengthFirstArrayComparator<T> {
            return <any>(new ArrayComparator.LengthFirstArrayComparator<any>());
        }
    }
    ArrayComparator["__class"] = "com.vzome.core.generic.ArrayComparator";


    export namespace ArrayComparator {

        export class ContentFirstArrayComparator<T extends java.lang.Comparable<T>> {
            /**
             * 
             * @param {T[]} array1
             * @param {T[]} array2
             * @return {number}
             */
            public compare(array1: T[], array2: T[]): number {
                const len1: number = array1.length;
                const len2: number = array2.length;
                const smaller: number = len1 < len2 ? len1 : len2;
                for(let i: number = 0; i < smaller; i++) {{
                    const element1: T = array1[i];
                    const element2: T = array2[i];
                    const comparison: number = element1.compareTo(element2);
                    if (comparison !== 0){
                        return comparison;
                    }
                };}
                return /* compareTo */(<any>((o1: any, o2: any) => { if (o1 && o1.compareTo) { return o1.compareTo(o2); } else { return o1 < o2 ? -1 : o2 < o1 ? 1 : 0; } })(len1,len2));
            }

            constructor() {
            }
        }
        ContentFirstArrayComparator["__class"] = "com.vzome.core.generic.ArrayComparator.ContentFirstArrayComparator";
        ContentFirstArrayComparator["__interfaces"] = ["java.util.Comparator"];



        export class LengthFirstArrayComparator<T extends java.lang.Comparable<T>> {
            /**
             * 
             * @param {T[]} array1
             * @param {T[]} array2
             * @return {number}
             */
            public compare(array1: T[], array2: T[]): number {
                const len1: number = array1.length;
                const len2: number = array2.length;
                let comparison: number = /* compareTo */(<any>((o1: any, o2: any) => { if (o1 && o1.compareTo) { return o1.compareTo(o2); } else { return o1 < o2 ? -1 : o2 < o1 ? 1 : 0; } })(len1,len2));
                if (comparison !== 0){
                    return comparison;
                }
                for(let i: number = 0; i < len1; i++) {{
                    const element1: T = array1[i];
                    const element2: T = array2[i];
                    comparison = element1.compareTo(element2);
                    if (comparison !== 0){
                        return comparison;
                    }
                };}
                return comparison;
            }

            constructor() {
            }
        }
        LengthFirstArrayComparator["__class"] = "com.vzome.core.generic.ArrayComparator.LengthFirstArrayComparator";
        LengthFirstArrayComparator["__interfaces"] = ["java.util.Comparator"];


    }

}


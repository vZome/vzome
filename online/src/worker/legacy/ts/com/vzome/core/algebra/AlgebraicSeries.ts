/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.algebra {
    export class AlgebraicSeries {
        public series: com.vzome.core.algebra.AlgebraicNumber[];

        public constructor(field: com.vzome.core.algebra.AbstractAlgebraicField, power: number) {
            if (this.series === undefined) { this.series = null; }
            let sequence: java.util.List<number> = <any>(new java.util.ArrayList<any>());
            sequence.add(0);
            let divisor: com.vzome.core.algebra.AlgebraicNumber = field.getUnitTerm(1);
            divisor = divisor['times$com_vzome_core_algebra_AlgebraicNumber'](divisor);
            for(let i: number = 0; i < power + 2; i++) {{
                sequence = field.recurrence(sequence);
            };}
            this.series = (s => { let a=[]; while(s-->0) a.push(null); return a; })(sequence.size() + 1);
            this.series[0] = field.zero();
            let prevIndex: number = 0;
            for(let index=sequence.iterator();index.hasNext();) {
                let integer = index.next();
                {
                    const prev: com.vzome.core.algebra.AlgebraicNumber = this.series[prevIndex++];
                    const step: com.vzome.core.algebra.AlgebraicNumber = field.getUnitTerm(integer).dividedBy(divisor);
                    this.series[prevIndex] = prev['plus$com_vzome_core_algebra_AlgebraicNumber'](step);
                }
            }
        }

        public nearestAlgebraicNumber(target: number): com.vzome.core.algebra.AlgebraicNumber {
            const negative: boolean = target < 0.0;
            if (negative)target = -target;
            const positive: com.vzome.core.algebra.AlgebraicNumber = this.checkRange(0, this.series.length - 1, target);
            if (negative)return positive.negate(); else return positive;
        }

        /*private*/ checkRange(minIndex: number, maxIndex: number, target: number): com.vzome.core.algebra.AlgebraicNumber {
            if (minIndex >= maxIndex)return this.series[maxIndex]; else {
                const lowDiff: number = target - this.series[minIndex].evaluate();
                const highDiff: number = this.series[maxIndex].evaluate() - target;
                if (maxIndex === minIndex + 1){
                    return (highDiff < lowDiff) ? this.series[maxIndex] : this.series[minIndex];
                } else {
                    const midIndex: number = (<number>Math.floor(((maxIndex + minIndex) / 2|0))|0);
                    return (highDiff < lowDiff) ? this.checkRange(midIndex, maxIndex, target) : this.checkRange(minIndex, midIndex, target);
                }
            }
        }

        /**
         * 
         * @return {string}
         */
        public toString(): string {
            let result: string = "";
            for(let index = 0; index < this.series.length; index++) {
                let algebraicNumber = this.series[index];
                {
                    result += algebraicNumber.toString() + ", ";
                }
            }
            return result;
        }
    }
    AlgebraicSeries["__class"] = "com.vzome.core.algebra.AlgebraicSeries";

}


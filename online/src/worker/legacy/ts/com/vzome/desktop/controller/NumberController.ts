/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.desktop.controller {
    export class NumberController extends com.vzome.desktop.controller.DefaultController {
        /*private*/ value: com.vzome.core.algebra.AlgebraicNumber;

        /*private*/ field: com.vzome.core.algebra.AlgebraicField;

        public constructor(field: com.vzome.core.algebra.AlgebraicField) {
            super();
            if (this.value === undefined) { this.value = null; }
            if (this.field === undefined) { this.field = null; }
            this.field = field;
            this.value = field.one();
        }

        /**
         * 
         * @param {string} listName
         * @return {java.lang.String[]}
         */
        public getCommandList(listName: string): string[] {
            switch((listName)) {
            case "labels":
                const order: number = this.field.getOrder();
                let result: string[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(order + 1);
                result[0] = "1";
                result[order] = "/";
                for(let i: number = 1; i < order; i++) {result[i] = this.field['getIrrational$int'](i);}
                return result;
            case "values":
                const td: string[] = this.value.toTrailingDivisor();
                result = (s => { let a=[]; while(s-->0) a.push(null); return a; })(td.length);
                for(let i: number = 0; i < td.length; i++) {result[i] = td[i];}
                return result;
            case "named-values":
                return this.getNamedValues();
            case "math.operations":
                return NumberController.MATH_OPS_$LI$();
            default:
                return super.getCommandList(listName);
            }
        }

        static OPTIONAL_NAMED_VALUES: string[]; public static OPTIONAL_NAMED_VALUES_$LI$(): string[] { if (NumberController.OPTIONAL_NAMED_VALUES == null) { NumberController.OPTIONAL_NAMED_VALUES = ["phi", "rho", "sigma", "alpha", "beta", "gamma", "delta", "epsilon", "theta", "kappa", "lambda", "mu", "seperator", "\u221a2", "\u221a3", "\u221a5", "\u221a6", "\u221a7", "\u221a8", "\u221a10"]; }  return NumberController.OPTIONAL_NAMED_VALUES; }

        /*private*/ getNamedValues(): string[] {
            let seperateNext: boolean = false;
            const list: java.util.List<string> = <any>(new java.util.ArrayList<any>());
            list.add("zero");
            list.add("one");
            for(let index = 0; index < NumberController.OPTIONAL_NAMED_VALUES_$LI$().length; index++) {
                let test = NumberController.OPTIONAL_NAMED_VALUES_$LI$()[index];
                {
                    if (test === ("seperator")){
                        seperateNext = true;
                    } else {
                        if (this.field.getNumberByName(test) != null){
                            if (seperateNext){
                                seperateNext = false;
                                list.add("seperator");
                            }
                            list.add(test);
                        }
                    }
                }
            }
            return list.toArray<any>((s => { let a=[]; while(s-->0) a.push(null); return a; })(list.size()));
        }

        /**
         * 
         * @param {string} property
         * @param {*} value
         */
        public setModelProperty(property: string, value: any) {
            switch((property)) {
            case "values":
                const values: java.util.StringTokenizer = new java.util.StringTokenizer(<string>value);
                const inputs: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(this.field.getOrder());
                let divisor: number = 1;
                for(let i: number = 0; values.hasMoreTokens(); i++) {if (i < inputs.length)inputs[i] = javaemul.internal.IntegerHelper.parseInt(values.nextToken()); else divisor = javaemul.internal.IntegerHelper.parseInt(values.nextToken());;}
                this.value = this.field['createAlgebraicNumber$int_A'](inputs).dividedBy(this.field['createRational$long'](divisor));
                return;
            case "named-value":
                this.setValueByName(/* valueOf */String(value).toString());
                return;
            case "math.operation":
                if (this.doMath(/* valueOf */String(value).toString())){
                    return;
                }
            }
            super.setModelProperty(property, value);
        }

        /*private*/ setValueByName(name: string) {
            const n: com.vzome.core.algebra.AlgebraicNumber = this.field.getNumberByName(name);
            if (n != null){
                this.setValue(n);
            }
        }

        static MATH_OPS: string[]; public static MATH_OPS_$LI$(): string[] { if (NumberController.MATH_OPS == null) { NumberController.MATH_OPS = ["Negate", "Reciprocal", "Square"]; }  return NumberController.MATH_OPS; }

        /*private*/ doMath(operation: string): boolean {
            switch((operation)) {
            case "Negate":
                this.setValue(this.getValue().negate());
                return true;
            case "Reciprocal":
                if (!this.getValue().isZero()){
                    this.setValue(this.getValue().reciprocal());
                }
                return true;
            case "Square":
                this.setValue(this.getValue()['times$com_vzome_core_algebra_AlgebraicNumber'](this.getValue()));
                return true;
            }
            return false;
        }

        /**
         * 
         * @param {string} property
         * @return {string}
         */
        public getProperty(property: string): string {
            switch((property)) {
            case "value":
                return this.value.toString(com.vzome.core.algebra.AlgebraicField.DEFAULT_FORMAT);
            case "evaluate":
                return /* valueOf */String(this.value.evaluate()).toString();
            default:
                return super.getProperty(property);
            }
        }

        public getValue(): com.vzome.core.algebra.AlgebraicNumber {
            return this.value;
        }

        public setValue(value: com.vzome.core.algebra.AlgebraicNumber) {
            this.value = value;
        }
    }
    NumberController["__class"] = "com.vzome.desktop.controller.NumberController";
    NumberController["__interfaces"] = ["com.vzome.desktop.api.Controller"];


}


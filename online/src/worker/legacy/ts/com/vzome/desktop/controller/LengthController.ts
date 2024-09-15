/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.desktop.controller {
    export class LengthController extends com.vzome.desktop.controller.DefaultController {
        /**
         * This is a permanent adjustment of the scale slider.  When the scale reads 0 for the user,
         * the actual scale used internally will be SCALE_OFFSET.
         */
        public static SCALE_OFFSET: number; public static SCALE_OFFSET_$LI$(): number { if (LengthController.SCALE_OFFSET == null) { LengthController.SCALE_OFFSET = com.vzome.core.math.symmetry.Direction.USER_SCALE; }  return LengthController.SCALE_OFFSET; }

        currentScales: LengthController.ScaleController[];

        unitController: com.vzome.desktop.controller.NumberController;

        /**
         * This is the internal factor applied, determined by the orbit, and fixed.
         */
        fixedFactor: com.vzome.core.algebra.AlgebraicNumber;

        /**
         * This is the user's basis for scale... when the slider is centered on "unit", this is the length value.
         */
        unitFactor: com.vzome.core.algebra.AlgebraicNumber;

        multiplier: number;

        standardUnitFactor: com.vzome.core.algebra.AlgebraicNumber;

        /*private*/ half: boolean;

        field: com.vzome.core.algebra.AlgebraicField;

        public constructor(fixedFactor?: any, standardUnitFactor?: any, field?: any) {
            if (((fixedFactor != null && (fixedFactor.constructor != null && fixedFactor.constructor["__interfaces"] != null && fixedFactor.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || fixedFactor === null) && ((standardUnitFactor != null && (standardUnitFactor.constructor != null && standardUnitFactor.constructor["__interfaces"] != null && standardUnitFactor.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || standardUnitFactor === null) && ((field != null && (field.constructor != null && field.constructor["__interfaces"] != null && field.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicField") >= 0)) || field === null)) {
                let __args = arguments;
                super();
                if (this.currentScales === undefined) { this.currentScales = null; } 
                if (this.unitController === undefined) { this.unitController = null; } 
                if (this.fixedFactor === undefined) { this.fixedFactor = null; } 
                if (this.unitFactor === undefined) { this.unitFactor = null; } 
                if (this.multiplier === undefined) { this.multiplier = 0; } 
                if (this.standardUnitFactor === undefined) { this.standardUnitFactor = null; } 
                if (this.field === undefined) { this.field = null; } 
                this.half = false;
                this.fixedFactor = fixedFactor;
                this.standardUnitFactor = standardUnitFactor;
                this.field = field;
                this.multiplier = 0;
                this.unitFactor = standardUnitFactor;
                this.currentScales = (s => { let a=[]; while(s-->0) a.push(null); return a; })(field.getNumMultipliers());
                for(let i: number = 0; i < this.currentScales.length; i++) {{
                    this.currentScales[i] = new LengthController.ScaleController(this);
                    this.addSubController("scale." + i, this.currentScales[i]);
                };}
                this.unitController = new com.vzome.desktop.controller.NumberController(field);
                this.addSubController("unit", this.unitController);
            } else if (((fixedFactor != null && (fixedFactor.constructor != null && fixedFactor.constructor["__interfaces"] != null && fixedFactor.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicField") >= 0)) || fixedFactor === null) && standardUnitFactor === undefined && field === undefined) {
                let __args = arguments;
                let field: any = __args[0];
                {
                    let __args = arguments;
                    let fixedFactor: any = __args[2].one();
                    let standardUnitFactor: any = __args[2].one();
                    super();
                    if (this.currentScales === undefined) { this.currentScales = null; } 
                    if (this.unitController === undefined) { this.unitController = null; } 
                    if (this.fixedFactor === undefined) { this.fixedFactor = null; } 
                    if (this.unitFactor === undefined) { this.unitFactor = null; } 
                    if (this.multiplier === undefined) { this.multiplier = 0; } 
                    if (this.standardUnitFactor === undefined) { this.standardUnitFactor = null; } 
                    if (this.field === undefined) { this.field = null; } 
                    this.half = false;
                    this.fixedFactor = fixedFactor;
                    this.standardUnitFactor = standardUnitFactor;
                    this.field = field;
                    this.multiplier = 0;
                    this.unitFactor = standardUnitFactor;
                    this.currentScales = (s => { let a=[]; while(s-->0) a.push(null); return a; })(field.getNumMultipliers());
                    for(let i: number = 0; i < this.currentScales.length; i++) {{
                        this.currentScales[i] = new LengthController.ScaleController(this);
                        this.addSubController("scale." + i, this.currentScales[i]);
                    };}
                    this.unitController = new com.vzome.desktop.controller.NumberController(field);
                    this.addSubController("unit", this.unitController);
                }
            } else throw new Error('invalid overload');
        }

        /**
         * 
         * @param {string} name
         * @return {*}
         */
        public getSubController(name: string): com.vzome.desktop.api.Controller {
            switch((name)) {
            case "unit":
                return this.unitController;
            case "scale":
                return this.currentScales[this.multiplier];
            default:
                return super.getSubController(name);
            }
        }

        public fireLengthChange() {
            this.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object("length", true, false);
        }

        resetScales() {
            for(let i: number = 0; i < this.currentScales.length; i++) {{
                this.currentScales[i].setScale(0);
            };}
        }

        /**
         * 
         * @param {string} action
         */
        public doAction(action: string) {
            switch((action)) {
            case "setCustomUnit":
                this.unitController.setValue(this.unitFactor);
                return;
            case "getCustomUnit":
                this.unitFactor = this.unitController.getValue();
                this.resetScales();
                this.multiplier = 0;
                this.fireLengthChange();
                return;
            case "toggleHalf":
                {
                    this.half = !this.half;
                    this.fireLengthChange();
                    return;
                };
            case "reset":
            case "short":
                {
                    this.unitFactor = this.standardUnitFactor;
                    this.resetScales();
                    this.multiplier = 0;
                    this.fireLengthChange();
                    return;
                };
            case "supershort":
                {
                    this.unitFactor = this.standardUnitFactor;
                    this.resetScales();
                    this.currentScales[0].setScale(-1);
                    this.multiplier = 0;
                    this.fireLengthChange();
                    return;
                };
            case "medium":
                {
                    this.unitFactor = this.standardUnitFactor;
                    this.resetScales();
                    this.currentScales[0].setScale(1);
                    this.multiplier = 0;
                    this.fireLengthChange();
                    return;
                };
            case "long":
                {
                    this.unitFactor = this.standardUnitFactor;
                    this.resetScales();
                    this.currentScales[0].setScale(2);
                    this.multiplier = 0;
                    this.fireLengthChange();
                    return;
                };
            case "scaleUp":
            case "scaleDown":
                this.currentScales[this.multiplier].doAction(action);
                return;
            case "newZeroScale":
                {
                    this.unitFactor = this.applyScales(this.unitFactor);
                    this.resetScales();
                    this.multiplier = 0;
                    this.fireLengthChange();
                    return;
                };
            default:
                if (/* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(action, "setMultiplier.")){
                    action = action.substring("setMultiplier.".length);
                    const i: number = javaemul.internal.IntegerHelper.parseInt(action);
                    this.multiplier = i;
                    this.fireLengthChange();
                } else super.doAction(action);
            }
        }

        /**
         * 
         * @param {string} property
         * @param {*} value
         */
        public setModelProperty(property: string, value: any) {
            switch((property)) {
            case "half":
                {
                    const oldHalf: boolean = this.half;
                    this.half = javaemul.internal.BooleanHelper.parseBoolean(<string>value);
                    if (this.half !== oldHalf)this.fireLengthChange();
                    break;
                };
            case "scale":
                {
                    this.currentScales[this.multiplier].setModelProperty(property, value);
                    return;
                };
            default:
                super.setModelProperty(property, value);
            }
        }

        /**
         * 
         * @param {string} name
         * @return {string}
         */
        public getProperty(name: string): string {
            switch((name)) {
            case "multiplier":
                return /* toString */(''+(this.multiplier));
            case "half":
                return javaemul.internal.BooleanHelper.toString(this.half);
            case "scale":
                return this.currentScales[this.multiplier].getProperty(name);
            case "unitText":
                return this.readable(this.unitFactor);
            case "unitIsCustom":
                return javaemul.internal.BooleanHelper.toString(!/* equals */(<any>((o1: any, o2: any) => { if (o1 && o1.equals) { return o1.equals(o2); } else { return o1 === o2; } })(this.unitFactor,this.standardUnitFactor)));
            case "lengthText":
                {
                    let result: com.vzome.core.algebra.AlgebraicNumber = this.unitFactor;
                    result = this.applyScales(result);
                    return this.readable(result);
                };
            case "lengthMathML":
                {
                    let result: com.vzome.core.algebra.AlgebraicNumber = this.unitFactor;
                    result = this.applyScales(result);
                    return result.getMathML();
                };
            case "scaleFactorHtml":
                {
                    let html: string = "";
                    for(let i: number = 0; i < this.currentScales.length; i++) {{
                        html += this.field['getIrrational$int'](i + 1) + this.currentScales[i].getProperty("scaleHtml") + "  \u2715  ";
                    };}
                    return html;
                };
            default:
                return super.getProperty(name);
            }
        }

        applyScales(value: com.vzome.core.algebra.AlgebraicNumber): com.vzome.core.algebra.AlgebraicNumber {
            for(let i: number = 0; i < this.currentScales.length; i++) {{
                const scale: number = this.currentScales[i].getScale();
                value = value['times$com_vzome_core_algebra_AlgebraicNumber'](this.field['createPower$int$int'](scale, i + 1));
            };}
            return value;
        }

        readable(unitFactor2: com.vzome.core.algebra.AlgebraicNumber): string {
            const buf: java.lang.StringBuffer = new java.lang.StringBuffer();
            unitFactor2.getNumberExpression(buf, com.vzome.core.algebra.AlgebraicField.DEFAULT_FORMAT);
            return buf.toString();
        }

        /**
         * Get the actual length value to use when rendering.  This value will multiply the zone's normal vector,
         * whatever its length is (not necessarily a unit vector).
         * @return {*}
         */
        public getValue(): com.vzome.core.algebra.AlgebraicNumber {
            let result: com.vzome.core.algebra.AlgebraicNumber = this.unitFactor['times$com_vzome_core_algebra_AlgebraicNumber'](this.fixedFactor);
            if (this.half)result = result['times$com_vzome_core_algebra_AlgebraicNumber'](this.field['createRational$long$long'](1, 2));
            result = result['times$com_vzome_core_algebra_AlgebraicNumber'](this.field['createPower$int'](LengthController.SCALE_OFFSET_$LI$()));
            result = this.applyScales(result);
            return result;
        }

        public getScale(): number {
            return this.currentScales[this.multiplier].getScale();
        }

        public setScale(amt: number) {
            this.currentScales[this.multiplier].setScale(amt);
        }

        /**
         * This is basically an inverse of getValue(), but with scale fixed at zero,
         * thus forcing unitFactor to float.
         * 
         * @param {*} length
         */
        public setActualLength(length: com.vzome.core.algebra.AlgebraicNumber) {
            this.half = false;
            this.resetScales();
            length = length['times$com_vzome_core_algebra_AlgebraicNumber'](this.field['createPower$int'](-LengthController.SCALE_OFFSET_$LI$()));
            this.unitFactor = length.dividedBy(this.fixedFactor);
            this.fireLengthChange();
        }
    }
    LengthController["__class"] = "com.vzome.desktop.controller.LengthController";
    LengthController["__interfaces"] = ["com.vzome.desktop.api.Controller"];



    export namespace LengthController {

        /**
         * A model for a scale slider.  Value range centers on scale 0.
         * 
         * Actual scale
         * 
         * @author Scott Vorthmann
         * @extends com.vzome.desktop.controller.DefaultController
         * @class
         */
        export class ScaleController extends com.vzome.desktop.controller.DefaultController {
            public __parent: any;
            static MAX_SCALE: number = 6;

            static MIN_SCALE: number = -6;

            /**
             * 
             * @param {string} action
             */
            public doAction(action: string) {
                if ("scaleUp" === action)this.setScale(this.scale + 1); else if ("scaleDown" === action)this.setScale(this.scale - 1); else super.doAction(action);
            }

            scale: number;

            /**
             * 
             * @param {string} property
             * @param {*} value
             */
            public setModelProperty(property: string, value: any) {
                if ("scale" === property){
                    this.setScale(javaemul.internal.IntegerHelper.parseInt(<string>value));
                    return;
                } else super.setModelProperty(property, value);
            }

            /**
             * 
             * @param {string} string
             * @return {string}
             */
            public getProperty(string: string): string {
                if ("scale" === string)return /* toString */(''+(this.scale));
                if ("scaleHtml" === string){
                    if (this.scale === 0)return "\u2070";
                    const absScale: number = Math.abs(this.scale);
                    let result: string = (absScale === this.scale) ? "" : "\u207b";
                    switch((absScale)) {
                    case 1:
                        result += "\u00b9";
                        break;
                    case 2:
                        result += "\u00b2";
                        break;
                    case 3:
                        result += "\u00b3";
                        break;
                    case 4:
                        result += "\u2074";
                        break;
                    case 5:
                        result += "\u2075";
                        break;
                    case 6:
                        result += "\u2076";
                        break;
                    case 7:
                        result += "\u2077";
                        break;
                    case 8:
                        result += "\u2078";
                        break;
                    case 9:
                        result += "\u2079";
                        break;
                    default:
                        result += "\u207f";
                        break;
                    }
                    return result;
                }
                return super.getProperty(string);
            }

            public setScale(amt: number) {
                const oldScale: number = this.scale;
                this.scale = amt;
                if (this.scale > ScaleController.MAX_SCALE)this.scale = ScaleController.MAX_SCALE; else if (this.scale < ScaleController.MIN_SCALE)this.scale = ScaleController.MIN_SCALE;
                if (oldScale !== this.scale)this.__parent.fireLengthChange();
            }

            getScale(): number {
                return this.scale;
            }

            constructor(__parent: any) {
                super();
                this.__parent = __parent;
                this.scale = 0;
            }
        }
        ScaleController["__class"] = "com.vzome.desktop.controller.LengthController.ScaleController";
        ScaleController["__interfaces"] = ["com.vzome.desktop.api.Controller"];


    }

}


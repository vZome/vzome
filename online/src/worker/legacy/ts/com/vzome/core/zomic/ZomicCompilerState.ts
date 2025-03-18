/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.zomic {
    export class ZomicCompilerState {
        public constructor(icosaSymm: com.vzome.core.math.symmetry.IcosahedralSymmetry) {
            if (this.icosaSymmetry === undefined) { this.icosaSymmetry = null; }
            if (this.namingConvention === undefined) { this.namingConvention = null; }
            this.statements = <any>(new java.util.Stack<any>());
            this.templates = <any>(new java.util.Stack<any>());
            this.icosaSymmetry = icosaSymm;
            this.namingConvention = new com.vzome.core.zomic.ZomicNamingConvention(icosaSymm);
        }

        /*private*/ icosaSymmetry: com.vzome.core.math.symmetry.IcosahedralSymmetry;

        /*private*/ namingConvention: com.vzome.core.zomic.ZomicNamingConvention;

        /*private*/ statements: java.util.Stack<com.vzome.core.zomic.program.ZomicStatement>;

        /*private*/ templates: java.util.Stack<ZomicCompilerState.ZomicStatementTemplate<any>>;

        public getProgram(): com.vzome.core.zomic.program.Walk {
            return this.statements.size() === 0 ? new com.vzome.core.zomic.program.Walk() : <com.vzome.core.zomic.program.Walk>this.statements.firstElement();
        }

        public prepareStatement(statement: com.vzome.core.zomic.program.ZomicStatement) {
            this.statements.push(statement);
        }

        public peekTemplate(): ZomicCompilerState.ZomicStatementTemplate<any> {
            return this.templates.peek();
        }

        public popTemplate(): ZomicCompilerState.ZomicStatementTemplate<any> {
            return this.templates.pop();
        }

        public commitLastStatement() {
            const statement: com.vzome.core.zomic.program.ZomicStatement = this.statements.pop();
            if (statement != null && statement instanceof <any>com.vzome.core.zomic.program.Nested){
                const body: com.vzome.core.zomic.program.ZomicStatement = (<com.vzome.core.zomic.program.Nested>statement).getBody();
                if ((body == null) || ((body != null && body instanceof <any>com.vzome.core.zomic.program.Walk) && (<com.vzome.core.zomic.program.Walk>body).size() === 0)){
                    return;
                }
            }
            this.commit(statement);
        }

        public commit(newStatement: com.vzome.core.zomic.program.ZomicStatement) {
            const currentStatement: com.vzome.core.zomic.program.ZomicStatement = this.statements.peek();
            if (currentStatement != null && currentStatement instanceof <any>com.vzome.core.zomic.program.Walk){
                (<com.vzome.core.zomic.program.Walk>currentStatement).addStatement(newStatement);
            } else {
                (<com.vzome.core.zomic.program.Nested>currentStatement).setBody(newStatement);
            }
        }

        public reset() {
            this.statements.clear();
            this.templates.clear();
        }

        public setCurrentScale(scale: number) {
            (<ZomicCompilerState.ScaleInfo><any>this.templates.peek()).scale = scale;
        }

        public prepareSymmetryTemplate(symmetryMode: ZomicCompilerState.SymmetryModeEnum) {
            const template: ZomicCompilerState.SymmetryTemplate = new ZomicCompilerState.SymmetryTemplate(this, symmetryMode);
            this.templates.push(template);
        }

        public prepareMoveTemplate() {
            const template: ZomicCompilerState.MoveTemplate = new ZomicCompilerState.MoveTemplate(this);
            this.templates.push(template);
        }

        public prepareScaleTemplate() {
            const template: ZomicCompilerState.ScaleTemplate = new ZomicCompilerState.ScaleTemplate(this);
            this.templates.push(template);
        }

        public prepareReflectTemplate(isThruCenter: boolean) {
            const template: ZomicCompilerState.ReflectTemplate = new ZomicCompilerState.ReflectTemplate(this, isThruCenter);
            this.templates.push(template);
        }

        public prepareRotateTemplate() {
            const template: ZomicCompilerState.RotateTemplate = new ZomicCompilerState.RotateTemplate(this);
            this.templates.push(template);
        }
    }
    ZomicCompilerState["__class"] = "com.vzome.core.zomic.ZomicCompilerState";


    export namespace ZomicCompilerState {

        export interface ZomicStatementTemplate<T extends com.vzome.core.zomic.program.ZomicStatement> {
            generate(): T;
        }

        export interface IHaveAxisInfo {
            axisColor(): string;

            setAxisColor(s: string);

            indexNumber(): string;

            setIndexNumber(s: string);

            handedness(): string;

            setHandedness(s: string);

            indexFullName(): string;
        }

        export class AxisInfo implements ZomicCompilerState.IHaveAxisInfo {
            public __parent: any;
            __axisColor: string;

            __indexNumber: string;

            __handedness: string;

            generate(): com.vzome.core.math.symmetry.Axis {
                try {
                    const axis: com.vzome.core.math.symmetry.Axis = this.__parent.namingConvention.getAxis(this.__axisColor, this.indexFullName());
                    if (axis == null){
                        const msg: string = "bad axis specification: \'" + this.__axisColor + " " + this.indexFullName() + "\'";
                        throw new Error(msg);
                    }
                    return axis;
                } catch(ex) {
                    const msg: string = "bad axis specification: \'" + this.__axisColor + " " + this.indexFullName() + "\'";
                    throw new Error(msg);
                }
            }

            /**
             * 
             * @return {string}
             */
            public axisColor(): string {
                return this.__axisColor;
            }

            /**
             * 
             * @param {string} s
             */
            public setAxisColor(s: string) {
                this.__axisColor = s;
            }

            /**
             * 
             * @return {string}
             */
            public indexNumber(): string {
                return this.__indexNumber;
            }

            /**
             * 
             * @param {string} s
             */
            public setIndexNumber(s: string) {
                this.__indexNumber = s;
            }

            /**
             * 
             * @return {string}
             */
            public handedness(): string {
                return this.__handedness;
            }

            /**
             * 
             * @param {string} s
             */
            public setHandedness(s: string) {
                this.__handedness = s;
            }

            /**
             * 
             * @return {string}
             */
            public indexFullName(): string {
                return this.__indexNumber + this.__handedness;
            }

            constructor(__parent: any) {
                this.__parent = __parent;
                this.__axisColor = "";
                this.__indexNumber = "";
                this.__handedness = "";
            }
        }
        AxisInfo["__class"] = "com.vzome.core.zomic.ZomicCompilerState.AxisInfo";
        AxisInfo["__interfaces"] = ["com.vzome.core.zomic.ZomicCompilerState.IHaveAxisInfo"];



        export class ScaleInfo {
            public __parent: any;
            public ones: number;

            public phis: number;

            public scale: number;

            generate(symmetry: com.vzome.core.math.symmetry.IcosahedralSymmetry): com.vzome.core.algebra.AlgebraicNumber {
                return symmetry.getField()['createAlgebraicNumber$int$int$int$int'](this.ones, this.phis, 1, this.scale);
            }

            constructor(__parent: any) {
                this.__parent = __parent;
                this.ones = 1;
                this.phis = 0;
                this.scale = 1;
            }
        }
        ScaleInfo["__class"] = "com.vzome.core.zomic.ZomicCompilerState.ScaleInfo";


        export class RotateTemplate implements ZomicCompilerState.ZomicStatementTemplate<com.vzome.core.zomic.program.Rotate>, ZomicCompilerState.IHaveAxisInfo {
            public __parent: any;
            axisInfo: ZomicCompilerState.AxisInfo;

            public steps: number;

            /**
             * 
             * @return {com.vzome.core.zomic.program.Rotate}
             */
            public generate(): com.vzome.core.zomic.program.Rotate {
                const axis: com.vzome.core.math.symmetry.Axis = this.axisInfo.generate();
                return new com.vzome.core.zomic.program.Rotate(axis, this.steps);
            }

            /**
             * 
             * @return {string}
             */
            public axisColor(): string {
                return this.axisInfo.__axisColor;
            }

            /**
             * 
             * @param {string} s
             */
            public setAxisColor(s: string) {
                this.axisInfo.setAxisColor(s);
            }

            /**
             * 
             * @return {string}
             */
            public indexNumber(): string {
                return this.axisInfo.__indexNumber;
            }

            /**
             * 
             * @param {string} s
             */
            public setIndexNumber(s: string) {
                this.axisInfo.setIndexNumber(s);
            }

            /**
             * 
             * @return {string}
             */
            public handedness(): string {
                return this.axisInfo.__handedness;
            }

            /**
             * 
             * @param {string} s
             */
            public setHandedness(s: string) {
                this.axisInfo.setHandedness(s);
            }

            /**
             * 
             * @return {string}
             */
            public indexFullName(): string {
                return this.axisInfo.indexFullName();
            }

            constructor(__parent: any) {
                this.__parent = __parent;
                this.axisInfo = new ZomicCompilerState.AxisInfo(this.__parent);
                this.steps = 1;
            }
        }
        RotateTemplate["__class"] = "com.vzome.core.zomic.ZomicCompilerState.RotateTemplate";
        RotateTemplate["__interfaces"] = ["com.vzome.core.zomic.ZomicCompilerState.IHaveAxisInfo","com.vzome.core.zomic.ZomicCompilerState.ZomicStatementTemplate"];



        export class ReflectTemplate implements ZomicCompilerState.ZomicStatementTemplate<com.vzome.core.zomic.program.Reflect>, ZomicCompilerState.IHaveAxisInfo {
            public __parent: any;
            axisInfo: ZomicCompilerState.AxisInfo;

            isThroughCenter: boolean;

            public constructor(__parent: any, isThruCenter: boolean) {
                this.__parent = __parent;
                this.axisInfo = new ZomicCompilerState.AxisInfo(this.__parent);
                if (this.isThroughCenter === undefined) { this.isThroughCenter = false; }
                this.isThroughCenter = isThruCenter;
            }

            /**
             * 
             * @return {com.vzome.core.zomic.program.Reflect}
             */
            public generate(): com.vzome.core.zomic.program.Reflect {
                const result: com.vzome.core.zomic.program.Reflect = new com.vzome.core.zomic.program.Reflect();
                if (!this.isThroughCenter){
                    if (("" === this.axisColor()) && !("" === this.indexNumber())){
                        this.setAxisColor("blue");
                    }
                    const axis: com.vzome.core.math.symmetry.Axis = this.axisInfo.generate();
                    result.setAxis(axis);
                }
                return result;
            }

            /**
             * 
             * @return {string}
             */
            public axisColor(): string {
                return this.axisInfo.__axisColor;
            }

            /**
             * 
             * @param {string} s
             */
            public setAxisColor(s: string) {
                if (!("blue" === s)){
                    this.enforceBlueAxis();
                } else {
                    this.axisInfo.setAxisColor(s);
                }
            }

            /**
             * 
             * @return {string}
             */
            public indexNumber(): string {
                return this.axisInfo.__indexNumber;
            }

            /**
             * 
             * @param {string} s
             */
            public setIndexNumber(s: string) {
                if (/* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(s, "-"))s = s.substring(1);
                this.axisInfo.setIndexNumber(s);
                if (this.isThroughCenter && !("" === this.indexNumber())){
                    this.setAxisColor("blue");
                }
            }

            /**
             * 
             * @return {string}
             */
            public handedness(): string {
                return this.axisInfo.__handedness;
            }

            /**
             * 
             * @param {string} s
             */
            public setHandedness(s: string) {
                this.enforceBlueAxis();
            }

            /**
             * 
             * @return {string}
             */
            public indexFullName(): string {
                return this.axisInfo.indexFullName();
            }

            enforceBlueAxis() {
                throw new java.lang.IllegalStateException("Only \'center\' or blue axis indexes are allowed.");
            }
        }
        ReflectTemplate["__class"] = "com.vzome.core.zomic.ZomicCompilerState.ReflectTemplate";
        ReflectTemplate["__interfaces"] = ["com.vzome.core.zomic.ZomicCompilerState.IHaveAxisInfo","com.vzome.core.zomic.ZomicCompilerState.ZomicStatementTemplate"];



        export enum SymmetryModeEnum {
            Icosahedral, RotateAroundAxis, MirrorThroughBlueAxis, ReflectThroughOrigin
        }

        export class SymmetryTemplate implements ZomicCompilerState.ZomicStatementTemplate<com.vzome.core.zomic.program.Symmetry>, ZomicCompilerState.IHaveAxisInfo {
            public __parent: any;
            axisInfo: ZomicCompilerState.AxisInfo;

            symmetryMode: ZomicCompilerState.SymmetryModeEnum;

            public constructor(__parent: any, mode: ZomicCompilerState.SymmetryModeEnum) {
                this.__parent = __parent;
                this.axisInfo = new ZomicCompilerState.AxisInfo(this.__parent);
                if (this.symmetryMode === undefined) { this.symmetryMode = null; }
                this.symmetryMode = mode;
            }

            /**
             * 
             * @return {com.vzome.core.zomic.program.Symmetry}
             */
            public generate(): com.vzome.core.zomic.program.Symmetry {
                const result: com.vzome.core.zomic.program.Symmetry = <com.vzome.core.zomic.program.Symmetry>this.__parent.statements.peek();
                switch((this.symmetryMode)) {
                case com.vzome.core.zomic.ZomicCompilerState.SymmetryModeEnum.Icosahedral:
                    break;
                case com.vzome.core.zomic.ZomicCompilerState.SymmetryModeEnum.RotateAroundAxis:
                    {
                        const rotate: com.vzome.core.zomic.program.Rotate = new com.vzome.core.zomic.program.Rotate(null, -1);
                        const axis: com.vzome.core.math.symmetry.Axis = this.axisInfo.generate();
                        rotate.setAxis(axis);
                        result.setPermute(rotate);
                    };
                    break;
                case com.vzome.core.zomic.ZomicCompilerState.SymmetryModeEnum.MirrorThroughBlueAxis:
                    {
                        const reflect: com.vzome.core.zomic.program.Reflect = new com.vzome.core.zomic.program.Reflect();
                        const axis: com.vzome.core.math.symmetry.Axis = this.axisInfo.generate();
                        reflect.setAxis(axis);
                        result.setPermute(reflect);
                    };
                    break;
                case com.vzome.core.zomic.ZomicCompilerState.SymmetryModeEnum.ReflectThroughOrigin:
                    result.setPermute(new com.vzome.core.zomic.program.Reflect());
                    break;
                default:
                    throw new java.lang.IllegalStateException("Unexpected SymmetryModeEnum: " + this.symmetryMode == null ? "<null>" : com.vzome.core.zomic.ZomicCompilerState.SymmetryModeEnum["_$wrappers"][this.symmetryMode].toString());
                }
                return result;
            }

            /**
             * 
             * @return {string}
             */
            public axisColor(): string {
                return this.axisInfo.__axisColor;
            }

            /**
             * 
             * @param {string} s
             */
            public setAxisColor(s: string) {
                this.axisInfo.setAxisColor(s);
            }

            /**
             * 
             * @return {string}
             */
            public indexNumber(): string {
                return this.axisInfo.__indexNumber;
            }

            /**
             * 
             * @param {string} s
             */
            public setIndexNumber(s: string) {
                if (this.symmetryMode === ZomicCompilerState.SymmetryModeEnum.MirrorThroughBlueAxis){
                    if (/* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(s, "-"))s = s.substring(1);
                }
                this.axisInfo.setIndexNumber(s);
                if (this.symmetryMode === ZomicCompilerState.SymmetryModeEnum.MirrorThroughBlueAxis){
                    this.setAxisColor("blue");
                }
            }

            /**
             * 
             * @return {string}
             */
            public handedness(): string {
                return this.axisInfo.__handedness;
            }

            /**
             * 
             * @param {string} s
             */
            public setHandedness(s: string) {
                this.axisInfo.setHandedness(s);
            }

            /**
             * 
             * @return {string}
             */
            public indexFullName(): string {
                return this.axisInfo.indexFullName();
            }
        }
        SymmetryTemplate["__class"] = "com.vzome.core.zomic.ZomicCompilerState.SymmetryTemplate";
        SymmetryTemplate["__interfaces"] = ["com.vzome.core.zomic.ZomicCompilerState.IHaveAxisInfo","com.vzome.core.zomic.ZomicCompilerState.ZomicStatementTemplate"];



        export class ScaleTemplate extends ZomicCompilerState.ScaleInfo implements ZomicCompilerState.ZomicStatementTemplate<com.vzome.core.zomic.program.Scale> {
            public __parent: any;
            public generate(symmetry?: any): any {
                if (((symmetry != null && symmetry instanceof <any>com.vzome.core.math.symmetry.IcosahedralSymmetry) || symmetry === null)) {
                    return super.generate(symmetry);
                } else if (symmetry === undefined) {
                    return <any>this.generate$();
                } else throw new Error('invalid overload');
            }

            public generate$(): com.vzome.core.zomic.program.Scale {
                const algebraicNumber: com.vzome.core.algebra.AlgebraicNumber = this.generate(this.__parent.icosaSymmetry);
                return new com.vzome.core.zomic.program.Scale(algebraicNumber);
            }

            constructor(__parent: any) {
                super(__parent);
                this.__parent = __parent;
            }
        }
        ScaleTemplate["__class"] = "com.vzome.core.zomic.ZomicCompilerState.ScaleTemplate";
        ScaleTemplate["__interfaces"] = ["com.vzome.core.zomic.ZomicCompilerState.ZomicStatementTemplate"];



        export class MoveTemplate extends ZomicCompilerState.ScaleInfo implements ZomicCompilerState.ZomicStatementTemplate<com.vzome.core.zomic.program.Move>, ZomicCompilerState.IHaveAxisInfo {
            public __parent: any;
            axisInfo: ZomicCompilerState.AxisInfo;

            public denominator: number;

            public sizeRef: string;

            public constructor(__parent: any) {
                super(__parent);
                this.__parent = __parent;
                this.axisInfo = new ZomicCompilerState.AxisInfo(this.__parent);
                this.denominator = 1;
                this.sizeRef = null;
                this.__isVariableLength = false;
                this.scale = com.vzome.core.zomic.ZomicNamingConvention.MEDIUM;
            }

            __isVariableLength: boolean;

            public isVariableLength$(): boolean {
                return (this.__isVariableLength || (-99 === this.scale));
            }

            public isVariableLength$boolean(is: boolean) {
                this.__isVariableLength = is;
            }

            public isVariableLength(is?: any) {
                if (((typeof is === 'boolean') || is === null)) {
                    return <any>this.isVariableLength$boolean(is);
                } else if (is === undefined) {
                    return <any>this.isVariableLength$();
                } else throw new Error('invalid overload');
            }

            public generate$java_lang_String(axisColor: string): com.vzome.core.algebra.AlgebraicNumber {
                if (this.denominator !== 1){
                    const direction: com.vzome.core.math.symmetry.Direction = this.__parent.icosaSymmetry.getDirection(axisColor);
                    if (direction == null || !direction.hasHalfSizes()){
                        const msg: string = "half struts are not allowed on \'" + axisColor + "\' axes.";
                        throw new Error(msg);
                    }
                }
                if (this.isVariableLength$()){
                    return this.__parent.icosaSymmetry.getField().zero();
                }
                let lengthFactor: number = 1;
                let scaleOffset: number = 0;
                switch((axisColor)) {
                case "blue":
                    lengthFactor = 2;
                    break;
                case "green":
                    lengthFactor = 2;
                    break;
                case "yellow":
                    scaleOffset = -1;
                    break;
                case "purple":
                    scaleOffset = -1;
                    break;
                default:
                    break;
                }
                return this.__parent.icosaSymmetry.getField()['createAlgebraicNumber$int$int$int$int'](this.ones * lengthFactor, this.phis * lengthFactor, this.denominator, this.scale + scaleOffset);
            }

            public generate(axisColor?: any): any {
                if (((typeof axisColor === 'string') || axisColor === null)) {
                    return <any>this.generate$java_lang_String(axisColor);
                } else if (((axisColor != null && axisColor instanceof <any>com.vzome.core.math.symmetry.IcosahedralSymmetry) || axisColor === null)) {
                    return super.generate(axisColor);
                } else if (axisColor === undefined) {
                    return <any>this.generate$();
                } else throw new Error('invalid overload');
            }

            public generate$(): com.vzome.core.zomic.program.Move {
                const axis: com.vzome.core.math.symmetry.Axis = this.axisInfo.generate();
                const strutLength: com.vzome.core.algebra.AlgebraicNumber = this.generate$java_lang_String(this.axisColor());
                return new com.vzome.core.zomic.program.Move(axis, strutLength);
            }

            /**
             * 
             * @return {string}
             */
            public axisColor(): string {
                return this.axisInfo.__axisColor;
            }

            /**
             * 
             * @param {string} s
             */
            public setAxisColor(s: string) {
                this.axisInfo.setAxisColor(s);
            }

            /**
             * 
             * @return {string}
             */
            public indexNumber(): string {
                return this.axisInfo.__indexNumber;
            }

            /**
             * 
             * @param {string} s
             */
            public setIndexNumber(s: string) {
                this.axisInfo.setIndexNumber(s);
            }

            /**
             * 
             * @return {string}
             */
            public handedness(): string {
                return this.axisInfo.__handedness;
            }

            /**
             * 
             * @param {string} s
             */
            public setHandedness(s: string) {
                this.axisInfo.setHandedness(s);
            }

            /**
             * 
             * @return {string}
             */
            public indexFullName(): string {
                return this.axisInfo.indexFullName();
            }
        }
        MoveTemplate["__class"] = "com.vzome.core.zomic.ZomicCompilerState.MoveTemplate";
        MoveTemplate["__interfaces"] = ["com.vzome.core.zomic.ZomicCompilerState.IHaveAxisInfo","com.vzome.core.zomic.ZomicCompilerState.ZomicStatementTemplate"];


    }

}


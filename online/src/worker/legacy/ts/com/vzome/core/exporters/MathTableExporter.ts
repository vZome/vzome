/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.exporters {
    export class MathTableExporter extends com.vzome.core.exporters.GeometryExporter {
        static X: number; public static X_$LI$(): number { if (MathTableExporter.X == null) { MathTableExporter.X = com.vzome.core.algebra.AlgebraicVector.X; }  return MathTableExporter.X; }

        static Y: number; public static Y_$LI$(): number { if (MathTableExporter.Y == null) { MathTableExporter.Y = com.vzome.core.algebra.AlgebraicVector.Y; }  return MathTableExporter.Y; }

        /**
         * 
         * @param {java.io.File} file
         * @param {java.io.Writer} writer
         * @param {number} height
         * @param {number} width
         */
        public doExport(file: java.io.File, writer: java.io.Writer, height: number, width: number) {
            const field: com.vzome.core.algebra.AlgebraicField = this.mModel.getField();
            const buf: java.lang.StringBuilder = new java.lang.StringBuilder();
            buf.append("{\n");
            MathTableExporter.writeFieldData(field, buf);
            MathTableExporter.writeUnitTermsOrDiagonals(field, buf);
            MathTableExporter.writeMultiplicationTable(field, buf);
            MathTableExporter.writeDivisionTable(field, buf);
            MathTableExporter.writeExponentsTable(field, buf);
            if (field != null && field instanceof <any>com.vzome.core.algebra.PolygonField){
                MathTableExporter.writeNamedNumbers(<com.vzome.core.algebra.PolygonField><any>field, buf);
                MathTableExporter.writeEmbedding(<com.vzome.core.algebra.PolygonField><any>field, buf);
                MathTableExporter.writeTrigTable(<com.vzome.core.algebra.PolygonField><any>field, buf);
            }
            buf.setLength(buf.length() - 2);
            buf.append("\n}\n");
            this.output = new java.io.PrintWriter(writer);
            this.output.println$java_lang_Object(/* replace */buf.toString().split("\'").join("\""));
            this.output.flush();
        }

        /*private*/ static getUnitTermOrDiagonal(field: com.vzome.core.algebra.AlgebraicField, i: number): com.vzome.core.algebra.AlgebraicNumber {
            return (field != null && field instanceof <any>com.vzome.core.algebra.PolygonField) ? (<com.vzome.core.algebra.PolygonField><any>field).getUnitDiagonal(i) : field.getUnitTerm(i);
        }

        /*private*/ static getFieldOrderOrDiagonalCount(field: com.vzome.core.algebra.AlgebraicField): number {
            return (field != null && field instanceof <any>com.vzome.core.algebra.PolygonField) ? (<com.vzome.core.algebra.PolygonField><any>field).diagonalCount() : field.getOrder();
        }

        /*private*/ static writeFieldData(field: com.vzome.core.algebra.AlgebraicField, buf: java.lang.StringBuilder) {
            buf.append(" \'field\': { ").append("\'name\': \'").append(field.getName()).append("\', ").append("\'order\': ").append(field.getOrder());
            if (field != null && field instanceof <any>com.vzome.core.algebra.PolygonField){
                const pfield: com.vzome.core.algebra.PolygonField = <com.vzome.core.algebra.PolygonField><any>field;
                buf.append(", \'parity\': \'").append(pfield.isOdd() ? "odd" : "even").append("\', ").append("\'diagonalCount\': ").append(pfield.diagonalCount()).append(", ").append("\'polygonSides\': ").append(pfield.polygonSides());
            }
            buf.append(" },\n");
        }

        /*private*/ static writeEmbedding(field: com.vzome.core.algebra.PolygonField, buf: java.lang.StringBuilder) {
            const symm: com.vzome.core.math.symmetry.AntiprismSymmetry = new com.vzome.core.math.symmetry.AntiprismSymmetry(field);
            const embeddingRows: number[] = [1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1];
            for(let i: number = 0; i < 3; i++) {{
                const column: com.vzome.core.math.RealVector = symm.embedInR3(field.basisVector(3, i));
                embeddingRows[0 + i] = column.x;
                embeddingRows[4 + i] = column.y;
                embeddingRows[8 + i] = column.z;
            };}
            buf.append(" \'embedding\': [ ");
            let delim: string = "";
            for(let index = 0; index < embeddingRows.length; index++) {
                let f = embeddingRows[index];
                {
                    buf.append(delim).append(f);
                    delim = ", ";
                }
            }
            buf.append(" ],\n");
        }

        /*private*/ static writeUnitTermsOrDiagonals(field: com.vzome.core.algebra.AlgebraicField, buf: java.lang.StringBuilder) {
            const limit: number = MathTableExporter.getFieldOrderOrDiagonalCount(field);
            buf.append(" \'unitTerms\': [ ");
            let delim: string = "\n";
            for(let i: number = 0; i < limit; i++) {{
                const number: com.vzome.core.algebra.AlgebraicNumber = MathTableExporter.getUnitTermOrDiagonal(field, i);
                const name: string = (i === 0) ? "1" : field['getIrrational$int'](i);
                buf.append(delim);
                delim = ",\n";
                buf.append("  { \'name\': \'").append(name).append("\'");
                buf.append(", \'value\': ").append(MathTableExporter.formatAN(number));
                buf.append(" }");
            };}
            buf.append("\n ],\n");
        }

        public static OPTIONAL_NAMED_VALUES: string[]; public static OPTIONAL_NAMED_VALUES_$LI$(): string[] { if (MathTableExporter.OPTIONAL_NAMED_VALUES == null) { MathTableExporter.OPTIONAL_NAMED_VALUES = ["phi", "rho", "sigma", "alpha", "beta", "gamma", "delta", "epsilon", "theta", "kappa", "lambda", "mu", "\u221a2", "\u221a3", "\u221a5", "\u221a6", "\u221a7", "\u221a8", "\u221a10"]; }  return MathTableExporter.OPTIONAL_NAMED_VALUES; }

        /*private*/ static writeNamedNumbers(field: com.vzome.core.algebra.PolygonField, buf: java.lang.StringBuilder) {
            buf.append(" \'namedNumbers\': [");
            let delim: string = "\n";
            for(let index = 0; index < MathTableExporter.OPTIONAL_NAMED_VALUES_$LI$().length; index++) {
                let name = MathTableExporter.OPTIONAL_NAMED_VALUES_$LI$()[index];
                {
                    const number: com.vzome.core.algebra.AlgebraicNumber = field.getNumberByName(name);
                    if (number != null){
                        buf.append(delim);
                        delim = ",\n";
                        buf.append("  { \'name\': \'").append(name).append("\', ");
                        buf.append("\'value\': ").append(MathTableExporter.formatAN(number)).append(", ");
                        switch((name)) {
                        case "phi":
                            MathTableExporter.writeDiagonalRatio(field, 5, buf);
                            break;
                        case "rho":
                            MathTableExporter.writeDiagonalRatio(field, 7, buf);
                            break;
                        case "sigma":
                            MathTableExporter.writeDiagonalRatio(field, 7, buf, 3);
                            break;
                        case "\u221a2":
                            MathTableExporter.writeDiagonalRatio(field, 4, buf);
                            break;
                        case "\u221a3":
                            MathTableExporter.writeDiagonalRatio(field, 6, buf);
                            break;
                        default:
                            break;
                        }
                        buf.append("\'reciprocal\': ").append(MathTableExporter.formatAN(number.reciprocal()));
                        buf.append(" }");
                    }
                }
            }
            buf.append("\n ],\n");
        }

        /*private*/ static writeTrigTable(field: com.vzome.core.algebra.PolygonField, buf: java.lang.StringBuilder) {
            const rotationMatrix: com.vzome.core.algebra.AlgebraicMatrix = (new com.vzome.core.math.symmetry.AntiprismSymmetry(field)).getRotationMatrix();
            const vX: com.vzome.core.algebra.AlgebraicVector = field.basisVector(3, MathTableExporter.X_$LI$());
            const v1: com.vzome.core.algebra.AlgebraicVector = rotationMatrix.timesColumn(vX);
            let bisector: com.vzome.core.algebra.AlgebraicVector = vX.plus(v1).scale(field.getUnitTerm(1).reciprocal());
            let v: com.vzome.core.algebra.AlgebraicVector = vX;
            const nSides: number = field.polygonSides();
            buf.append(" \'trig\': [\n");
            for(let i: number = 0; i < nSides; i++) {{
                MathTableExporter.writeTrigEntry(i, nSides, v, bisector, buf);
                buf.append(i === nSides - 1 ? "\n" : ",\n");
                v = rotationMatrix.timesColumn(v);
                bisector = rotationMatrix.timesColumn(bisector);
            };}
            buf.append(" ],\n");
        }

        /*private*/ static writeMultiplicationTable(field: com.vzome.core.algebra.AlgebraicField, buf: java.lang.StringBuilder) {
            MathTableExporter.writeTable(field, buf, "multiplication", (n1, n2) => n1['times$com_vzome_core_algebra_AlgebraicNumber'](n2));
        }

        /*private*/ static writeDivisionTable(field: com.vzome.core.algebra.AlgebraicField, buf: java.lang.StringBuilder) {
            MathTableExporter.writeTable(field, buf, "division", (n1, n2) => n1.dividedBy(n2));
        }

        /*private*/ static writeTable(field: com.vzome.core.algebra.AlgebraicField, buf: java.lang.StringBuilder, tableName: string, op: (p1: com.vzome.core.algebra.AlgebraicNumber, p2: com.vzome.core.algebra.AlgebraicNumber) => com.vzome.core.algebra.AlgebraicNumber) {
            const operandFactory: (p1: number) => com.vzome.core.algebra.AlgebraicNumber = (field != null && field instanceof <any>com.vzome.core.algebra.PolygonField) ? (instance$PolygonField,n) => { return instance$PolygonField.getUnitDiagonal(n) } : (n) => { return field.getUnitTerm(n) };
            const limit: number = (field != null && field instanceof <any>com.vzome.core.algebra.PolygonField) ? (<com.vzome.core.algebra.PolygonField><any>field).diagonalCount() : field.getOrder();
            buf.append(" \'").append(tableName).append("\': [\n");
            let delim1: string = "";
            for(let i: number = 0; i < limit; i++) {{
                const n1: com.vzome.core.algebra.AlgebraicNumber = (target => (typeof target === 'function') ? target(i) : (<any>target).apply(i))(operandFactory);
                buf.append(delim1).append("  [ ");
                delim1 = ",\n";
                let delim2: string = "";
                for(let j: number = 0; j < limit; j++) {{
                    const n2: com.vzome.core.algebra.AlgebraicNumber = (target => (typeof target === 'function') ? target(j) : (<any>target).apply(j))(operandFactory);
                    const result: com.vzome.core.algebra.AlgebraicNumber = (target => (typeof target === 'function') ? target(n1, n2) : (<any>target).apply(n1, n2))(op);
                    buf.append(delim2);
                    delim2 = ", ";
                    buf.append(MathTableExporter.formatAN(result));
                };}
                buf.append(" ]");
            };}
            buf.append("\n ],\n");
        }

        /*private*/ static writeExponentsTable(field: com.vzome.core.algebra.AlgebraicField, buf: java.lang.StringBuilder) {
            const limit: number = MathTableExporter.getFieldOrderOrDiagonalCount(field);
            const range: number = 6;
            buf.append(" \'exponents\': [\n");
            let delim1: string = "";
            for(let i: number = 1; i < limit; i++) {{
                buf.append(delim1).append("  {");
                delim1 = ",\n";
                const name: string = field['getIrrational$int'](i);
                buf.append(" \'base\': \'").append(name).append("\'");
                {
                    buf.append(",\n    \'positivePowers\': [ ");
                    let delim2: string = "";
                    const base: com.vzome.core.algebra.AlgebraicNumber = MathTableExporter.getUnitTermOrDiagonal(field, i);
                    let result: com.vzome.core.algebra.AlgebraicNumber = base;
                    for(let power: number = 1; power <= range; power++) {{
                        buf.append(delim2);
                        delim2 = ", ";
                        buf.append(MathTableExporter.formatAN(result));
                        result = result['times$com_vzome_core_algebra_AlgebraicNumber'](base);
                    };}
                    buf.append(" ]");
                };
                {
                    buf.append(",\n    \'negativePowers\': [ ");
                    let delim2: string = "";
                    const base: com.vzome.core.algebra.AlgebraicNumber = MathTableExporter.getUnitTermOrDiagonal(field, i).reciprocal();
                    let result: com.vzome.core.algebra.AlgebraicNumber = base;
                    for(let power: number = 1; power <= range; power++) {{
                        buf.append(delim2);
                        delim2 = ", ";
                        buf.append(MathTableExporter.formatAN(result));
                        result = result['times$com_vzome_core_algebra_AlgebraicNumber'](base);
                    };}
                    buf.append(" ]");
                };
                buf.append("\n  }");
            };}
            buf.append("\n ],\n");
        }

        public static writeDiagonalRatio(field: com.vzome.core.algebra.PolygonField, divisor: number, buf: java.lang.StringBuilder, step: number = 2) {
            if (field.polygonSides() % divisor === 0){
                const n: number = (field.polygonSides() / divisor|0);
                const denominator: com.vzome.core.algebra.AlgebraicNumber = field.getUnitDiagonal(n - 1);
                const numerator: com.vzome.core.algebra.AlgebraicNumber = field.getUnitDiagonal((step * n) - 1);
                buf.append("\'numerator\': ").append(MathTableExporter.formatAN(numerator)).append(", ");
                buf.append("\'denominator\': ").append(MathTableExporter.formatAN(denominator)).append(", ");
            } else {
                throw new java.lang.IllegalStateException("shouldn\'t ever get here");
            }
        }

        /*private*/ static writeTrigEntry(i: number, nSides: number, vStep: com.vzome.core.algebra.AlgebraicVector, bisector: com.vzome.core.algebra.AlgebraicVector, buf: java.lang.StringBuilder) {
            const delim1: string = "\', ";
            const delim2: string = ", ";
            const infinite: string = "{ \'alg\': \'\u221e\', \'dec\': \'\u221e\', \'tdf\': \'\u221e\' }";
            let v: com.vzome.core.algebra.AlgebraicVector = vStep;
            for(let n: number = 0; n < 2; n++) {{
                const k: number = (i * 2) + n;
                const degrees: number = k * 180.0 / nSides;
                const sin: com.vzome.core.algebra.AlgebraicNumber = v.getComponent(MathTableExporter.Y_$LI$());
                const cos: com.vzome.core.algebra.AlgebraicNumber = v.getComponent(MathTableExporter.X_$LI$());
                buf.append("  { ");
                buf.append("\'rot\': \'").append(k).append("/").append(nSides * 2).append(delim1);
                buf.append("\'rad\': \'").append(k).append("\u03c0/").append(nSides).append(delim1);
                buf.append("\'deg\': ").append(degrees).append(delim2);
                buf.append("\'sin\': ").append(MathTableExporter.formatAN(sin)).append(delim2);
                buf.append("\'cos\': ").append(MathTableExporter.formatAN(cos)).append(delim2);
                buf.append("\'tan\': ").append(cos.isZero() ? infinite : MathTableExporter.formatAN(sin.dividedBy(cos))).append(delim2);
                buf.append("\'csc\': ").append(sin.isZero() ? infinite : MathTableExporter.formatAN(sin.reciprocal())).append(delim2);
                buf.append("\'sec\': ").append(cos.isZero() ? infinite : MathTableExporter.formatAN(cos.reciprocal())).append(delim2);
                buf.append("\'cot\': ").append(sin.isZero() ? infinite : MathTableExporter.formatAN(cos.dividedBy(sin)));
                buf.append(" }");
                if (n === 0){
                    buf.append(",\n");
                }
                v = bisector;
            };}
        }

        /*private*/ static formatAN(n: com.vzome.core.algebra.AlgebraicNumber): string {
            const buf: java.lang.StringBuilder = new java.lang.StringBuilder();
            buf.append("{ \'alg\': \'").append(n).append("\', \'dec\': ").append(n.evaluate()).append(", \'tdf\': [");
            let delim: string = "";
            {
                let array = n.toTrailingDivisor();
                for(let index = 0; index < array.length; index++) {
                    let term = array[index];
                    {
                        buf.append(delim);
                        delim = ", ";
                        buf.append(term);
                    }
                }
            }
            buf.append("] }");
            return buf.toString();
        }

        /**
         * 
         * @return {string}
         */
        public getFileExtension(): string {
            return "math.json";
        }

        /**
         * 
         * @return {string}
         */
        public getContentType(): string {
            return "application/json";
        }

        constructor() {
            super();
        }
    }
    MathTableExporter["__class"] = "com.vzome.core.exporters.MathTableExporter";
    MathTableExporter["__interfaces"] = ["com.vzome.core.render.RealZomeScaling"];


}


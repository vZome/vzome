/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.algebra {
    export class AlgebraicMatrix {
        /**
         * 
         * @return {number}
         */
        public hashCode(): number {
            const prime: number = 31;
            let result: number = 1;
            for(let index = 0; index < this.matrix.length; index++) {
                let m = this.matrix[index];
                {
                    result = prime * result + java.util.Arrays.hashCode(m);
                }
            }
            return result;
        }

        /**
         * 
         * @param {*} obj
         * @return {boolean}
         */
        public equals(obj: any): boolean {
            if (this === obj)return true;
            if (obj == null)return false;
            if ((<any>this.constructor) !== (<any>obj.constructor))return false;
            const other: AlgebraicMatrix = <AlgebraicMatrix>obj;
            return java.util.Arrays.deepEquals(this.matrix, other.matrix);
        }

        matrix: com.vzome.core.algebra.AlgebraicNumber[][];

        public getMatrix(): com.vzome.core.algebra.AlgebraicNumber[][] {
            return this.matrix;
        }

        public getRowMajorRealElements(): number[] {
            const result: number[] = [1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1];
            for(let i: number = 0; i < 3; i++) {{
                for(let j: number = 0; j < 3; j++) {{
                    result[i * 4 + j] = (<any>Math).fround(this.getElement(i, j).evaluate());
                };}
            };}
            return result;
        }

        public constructor(x?: any, y?: any, z?: any, w?: any) {
            if (((x != null && x instanceof <any>com.vzome.core.algebra.AlgebraicVector) || x === null) && ((y != null && y instanceof <any>com.vzome.core.algebra.AlgebraicVector) || y === null) && ((z != null && z instanceof <any>com.vzome.core.algebra.AlgebraicVector) || z === null) && ((w != null && w instanceof <any>com.vzome.core.algebra.AlgebraicVector) || w === null)) {
                let __args = arguments;
                {
                    let __args = arguments;
                    let columns: any = [x, y, z, w];
                    if (this.matrix === undefined) { this.matrix = null; } 
                    const rows: number = columns[0].dimension();
                    const cols: number = columns.length;
                    this.matrix = <any> (function(dims) { let allocate = function(dims) { if (dims.length === 0) { return null; } else { let array = []; for(let i = 0; i < dims[0]; i++) { array.push(allocate(dims.slice(1))); } return array; }}; return allocate(dims);})([rows, cols]);
                    for(let i: number = 0; i < rows; i++) {{
                        for(let j: number = 0; j < cols; j++) {{
                            this.matrix[i][j] = columns[j].getComponent(i);
                        };}
                    };}
                }
            } else if (((x != null && x instanceof <any>com.vzome.core.algebra.AlgebraicVector) || x === null) && ((y != null && y instanceof <any>com.vzome.core.algebra.AlgebraicVector) || y === null) && ((z != null && z instanceof <any>com.vzome.core.algebra.AlgebraicVector) || z === null) && w === undefined) {
                let __args = arguments;
                {
                    let __args = arguments;
                    let columns: any = [x, y, z];
                    if (this.matrix === undefined) { this.matrix = null; } 
                    const rows: number = columns[0].dimension();
                    const cols: number = columns.length;
                    this.matrix = <any> (function(dims) { let allocate = function(dims) { if (dims.length === 0) { return null; } else { let array = []; for(let i = 0; i < dims[0]; i++) { array.push(allocate(dims.slice(1))); } return array; }}; return allocate(dims);})([rows, cols]);
                    for(let i: number = 0; i < rows; i++) {{
                        for(let j: number = 0; j < cols; j++) {{
                            this.matrix[i][j] = columns[j].getComponent(i);
                        };}
                    };}
                }
            } else if (((x != null && (x.constructor != null && x.constructor["__interfaces"] != null && x.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicField") >= 0)) || x === null) && ((typeof y === 'number') || y === null) && ((typeof z === 'number') || z === null) && w === undefined) {
                let __args = arguments;
                let field: any = __args[0];
                let rows: any = __args[1];
                let cols: any = __args[2];
                if (this.matrix === undefined) { this.matrix = null; } 
                this.matrix = <any> (function(dims) { let allocate = function(dims) { if (dims.length === 0) { return null; } else { let array = []; for(let i = 0; i < dims[0]; i++) { array.push(allocate(dims.slice(1))); } return array; }}; return allocate(dims);})([rows, cols]);
                for(let i: number = 0; i < rows; i++) {{
                    for(let j: number = 0; j < cols; j++) {{
                        this.matrix[i][j] = field.zero();
                    };}
                };}
            } else if (((x != null && x instanceof <any>com.vzome.core.algebra.AlgebraicVector) || x === null) && ((y != null && y instanceof <any>com.vzome.core.algebra.AlgebraicVector) || y === null) && z === undefined && w === undefined) {
                let __args = arguments;
                {
                    let __args = arguments;
                    let columns: any = [x, y];
                    if (this.matrix === undefined) { this.matrix = null; } 
                    const rows: number = columns[0].dimension();
                    const cols: number = columns.length;
                    this.matrix = <any> (function(dims) { let allocate = function(dims) { if (dims.length === 0) { return null; } else { let array = []; for(let i = 0; i < dims[0]; i++) { array.push(allocate(dims.slice(1))); } return array; }}; return allocate(dims);})([rows, cols]);
                    for(let i: number = 0; i < rows; i++) {{
                        for(let j: number = 0; j < cols; j++) {{
                            this.matrix[i][j] = columns[j].getComponent(i);
                        };}
                    };}
                }
            } else if (((x != null && (x.constructor != null && x.constructor["__interfaces"] != null && x.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicField") >= 0)) || x === null) && ((typeof y === 'number') || y === null) && z === undefined && w === undefined) {
                let __args = arguments;
                let field: any = __args[0];
                let dim: any = __args[1];
                if (this.matrix === undefined) { this.matrix = null; } 
                this.matrix = <any> (function(dims) { let allocate = function(dims) { if (dims.length === 0) { return null; } else { let array = []; for(let i = 0; i < dims[0]; i++) { array.push(allocate(dims.slice(1))); } return array; }}; return allocate(dims);})([dim, dim]);
                for(let i: number = 0; i < dim; i++) {{
                    for(let j: number = 0; j < dim; j++) {{
                        if (i === j)this.matrix[i][j] = field.one(); else this.matrix[i][j] = field.zero();
                    };}
                };}
            } else if (((x != null && x instanceof <any>Array && (x.length == 0 || x[0] == null ||(x[0] != null && x[0] instanceof <any>com.vzome.core.algebra.AlgebraicVector))) || x === null) && y === undefined && z === undefined && w === undefined) {
                let __args = arguments;
                let columns: any = __args[0];
                if (this.matrix === undefined) { this.matrix = null; } 
                const rows: number = columns[0].dimension();
                const cols: number = columns.length;
                this.matrix = <any> (function(dims) { let allocate = function(dims) { if (dims.length === 0) { return null; } else { let array = []; for(let i = 0; i < dims[0]; i++) { array.push(allocate(dims.slice(1))); } return array; }}; return allocate(dims);})([rows, cols]);
                for(let i: number = 0; i < rows; i++) {{
                    for(let j: number = 0; j < cols; j++) {{
                        this.matrix[i][j] = columns[j].getComponent(i);
                    };}
                };}
            } else throw new Error('invalid overload');
        }

        /**
         * 
         * @return {string}
         */
        public toString(): string {
            const buf: java.lang.StringBuilder = new java.lang.StringBuilder();
            for(let index = 0; index < this.matrix.length; index++) {
                let m = this.matrix[index];
                {
                    buf.append(java.util.Arrays.toString(m));
                    buf.append(", ");
                }
            }
            return "[ " + buf.toString() + " ]";
        }

        public negate(): AlgebraicMatrix {
            const field: com.vzome.core.algebra.AlgebraicField = this.matrix[0][0].getField();
            const result: AlgebraicMatrix = new AlgebraicMatrix(field, this.matrix.length);
            for(let i: number = 0; i < this.matrix.length; i++) {{
                for(let j: number = 0; j < this.matrix[i].length; j++) {{
                    result.matrix[i][j] = this.matrix[i][j].negate();
                };}
            };}
            return result;
        }

        public inverse(): AlgebraicMatrix {
            if (!this.isSquare()){
                throw new java.lang.IllegalArgumentException("matrix is not square");
            }
            const field: com.vzome.core.algebra.AlgebraicField = this.matrix[0][0].getField();
            const result: AlgebraicMatrix = new AlgebraicMatrix(field, this.matrix.length);
            const rank: number = com.vzome.core.algebra.Fields.gaussJordanReduction$com_vzome_core_algebra_Fields_Element_A_A$com_vzome_core_algebra_Fields_Element_A_A(this.matrix, result.matrix);
            if (rank !== this.matrix.length){
                const message: string = "AlgebraicMatrix inverse expects matrix rank to be " + this.matrix.length + ", but it is " + rank + ".";
                console.error(message);
            }
            return result;
        }

        public transpose(): AlgebraicMatrix {
            const field: com.vzome.core.algebra.AlgebraicField = this.matrix[0][0].getField();
            const result: AlgebraicMatrix = new AlgebraicMatrix(field, this.matrix[0].length, this.matrix.length);
            for(let i: number = 0; i < result.matrix.length; i++) {{
                for(let j: number = 0; j < this.matrix.length; j++) {{
                    result.matrix[i][j] = this.matrix[j][i];
                };}
            };}
            return result;
        }

        public times(that: AlgebraicMatrix): AlgebraicMatrix {
            const field: com.vzome.core.algebra.AlgebraicField = this.matrix[0][0].getField();
            const result: AlgebraicMatrix = new AlgebraicMatrix(field, this.matrix.length, that.matrix[0].length);
            com.vzome.core.algebra.Fields.matrixMultiplication<any>(this.matrix, that.matrix, result.matrix);
            return result;
        }

        public timesRow(rowVector: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.algebra.AlgebraicVector {
            const colLength: number = rowVector.dimension();
            if (this.matrix.length !== colLength)throw new java.lang.IllegalArgumentException("vector length incorrect for this matrix: " + rowVector);
            const rowLength: number = this.matrix[0].length;
            const resultComponents: com.vzome.core.algebra.AlgebraicNumber[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(rowLength);
            const field: com.vzome.core.algebra.AlgebraicField = this.matrix[0][0].getField();
            for(let j: number = 0; j < rowLength; j++) {{
                resultComponents[j] = field.zero();
                for(let i: number = 0; i < colLength; i++) {{
                    const product: com.vzome.core.algebra.AlgebraicNumber = rowVector.getComponent(i)['times$com_vzome_core_algebra_AlgebraicNumber'](this.matrix[i][j]);
                    resultComponents[j] = resultComponents[j]['plus$com_vzome_core_algebra_AlgebraicNumber'](product);
                };}
            };}
            return new com.vzome.core.algebra.AlgebraicVector(resultComponents);
        }

        public timesColumn(columnVector: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.algebra.AlgebraicVector {
            const rowLength: number = columnVector.dimension();
            if (this.matrix[0].length !== rowLength)throw new java.lang.IllegalArgumentException("vector length incorrect for this matrix: " + columnVector);
            const colLength: number = this.matrix.length;
            const resultComponents: com.vzome.core.algebra.AlgebraicNumber[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(colLength);
            const field: com.vzome.core.algebra.AlgebraicField = this.matrix[0][0].getField();
            for(let i: number = 0; i < colLength; i++) {{
                resultComponents[i] = field.zero();
                for(let j: number = 0; j < rowLength; j++) {{
                    const product: com.vzome.core.algebra.AlgebraicNumber = columnVector.getComponent(j)['times$com_vzome_core_algebra_AlgebraicNumber'](this.matrix[i][j]);
                    resultComponents[i] = resultComponents[i]['plus$com_vzome_core_algebra_AlgebraicNumber'](product);
                };}
            };}
            return new com.vzome.core.algebra.AlgebraicVector(resultComponents);
        }

        public timesScalar(scalar: com.vzome.core.algebra.AlgebraicNumber): AlgebraicMatrix {
            const result: AlgebraicMatrix = new AlgebraicMatrix(scalar.getField(), this.matrix.length);
            for(let i: number = 0; i < this.matrix.length; i++) {{
                for(let j: number = 0; j < this.matrix[i].length; j++) {{
                    result.matrix[i][j] = this.matrix[i][j]['times$com_vzome_core_algebra_AlgebraicNumber'](scalar);
                };}
            };}
            return result;
        }

        public isSquare(): boolean {
            return this.matrix.length === this.matrix[0].length;
        }

        public trace(): com.vzome.core.algebra.AlgebraicNumber {
            if (!this.isSquare()){
                throw new java.lang.IllegalArgumentException("matrix is not square");
            }
            let trace: com.vzome.core.algebra.AlgebraicNumber = this.matrix[0][0].getField().zero();
            for(let i: number = 0; i < this.matrix.length; i++) {{
                trace = trace['plus$com_vzome_core_algebra_AlgebraicNumber'](this.matrix[i][i]);
            };}
            return trace;
        }

        public determinant(): com.vzome.core.algebra.AlgebraicNumber {
            return AlgebraicMatrix.laplaceDeterminant(this.matrix);
        }

        public static laplaceDeterminant(matrix: com.vzome.core.algebra.AlgebraicNumber[][]): com.vzome.core.algebra.AlgebraicNumber {
            if (matrix.length !== matrix[0].length){
                throw new java.lang.IllegalArgumentException("matrix is not square");
            }
            let determinant: com.vzome.core.algebra.AlgebraicNumber = null;
            switch((matrix.length)) {
            case 3:
                determinant = (matrix[0][0]['times$com_vzome_core_algebra_AlgebraicNumber'](matrix[1][1])['times$com_vzome_core_algebra_AlgebraicNumber'](matrix[2][2]))['plus$com_vzome_core_algebra_AlgebraicNumber'](matrix[0][1]['times$com_vzome_core_algebra_AlgebraicNumber'](matrix[1][2])['times$com_vzome_core_algebra_AlgebraicNumber'](matrix[2][0]))['plus$com_vzome_core_algebra_AlgebraicNumber'](matrix[0][2]['times$com_vzome_core_algebra_AlgebraicNumber'](matrix[1][0])['times$com_vzome_core_algebra_AlgebraicNumber'](matrix[2][1]))['minus$com_vzome_core_algebra_AlgebraicNumber'](matrix[0][2]['times$com_vzome_core_algebra_AlgebraicNumber'](matrix[1][1])['times$com_vzome_core_algebra_AlgebraicNumber'](matrix[2][0]))['minus$com_vzome_core_algebra_AlgebraicNumber'](matrix[0][0]['times$com_vzome_core_algebra_AlgebraicNumber'](matrix[1][2])['times$com_vzome_core_algebra_AlgebraicNumber'](matrix[2][1]))['minus$com_vzome_core_algebra_AlgebraicNumber'](matrix[0][1]['times$com_vzome_core_algebra_AlgebraicNumber'](matrix[1][0])['times$com_vzome_core_algebra_AlgebraicNumber'](matrix[2][2]));
                break;
            case 2:
                determinant = (matrix[0][0]['times$com_vzome_core_algebra_AlgebraicNumber'](matrix[1][1]))['minus$com_vzome_core_algebra_AlgebraicNumber'](matrix[0][1]['times$com_vzome_core_algebra_AlgebraicNumber'](matrix[1][0]));
                break;
            case 1:
                determinant = matrix[0][0];
                break;
            default:
                determinant = matrix[0][0].getField().zero();
                const auxLength: number = matrix.length - 1;
                let sign: com.vzome.core.algebra.AlgebraicNumber = matrix[0][0].getField().one();
                for(let i: number = 0; i < matrix.length; i++) {{
                    if (!matrix[0][i].isZero()){
                        const aux: com.vzome.core.algebra.AlgebraicNumber[][] = <any> (function(dims) { let allocate = function(dims) { if (dims.length === 0) { return null; } else { let array = []; for(let i = 0; i < dims[0]; i++) { array.push(allocate(dims.slice(1))); } return array; }}; return allocate(dims);})([auxLength, auxLength]);
                        let iAux: number = 0;
                        let jAux: number = 0;
                        for(let row: number = 1; row < matrix.length; row++) {{
                            for(let col: number = 0; col < matrix.length; col++) {{
                                if (col !== i){
                                    aux[iAux][jAux] = matrix[row][col];
                                    jAux++;
                                }
                            };}
                            iAux++;
                            jAux = 0;
                        };}
                        determinant = determinant['plus$com_vzome_core_algebra_AlgebraicNumber'](sign['times$com_vzome_core_algebra_AlgebraicNumber'](matrix[0][i])['times$com_vzome_core_algebra_AlgebraicNumber'](AlgebraicMatrix.laplaceDeterminant(aux)));
                    }
                    sign = sign.negate();
                };}
            }
            return determinant;
        }

        public setElement(i: number, j: number, value: com.vzome.core.algebra.AlgebraicNumber): AlgebraicMatrix {
            this.matrix[i][j] = value;
            return this;
        }

        public getElement(i: number, j: number): com.vzome.core.algebra.AlgebraicNumber {
            return this.matrix[i][j];
        }
    }
    AlgebraicMatrix["__class"] = "com.vzome.core.algebra.AlgebraicMatrix";

}


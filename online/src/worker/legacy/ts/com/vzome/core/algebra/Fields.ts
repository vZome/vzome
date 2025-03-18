/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.algebra {
    export class Fields {
        public static rows(matrix: any[][]): number {
            return matrix.length;
        }

        public static columns(matrix: any[][]): number {
            return matrix[0].length;
        }

        public static matrixMultiplication<T extends Fields.Element<T>>(left: T[][], right: T[][], product: T[][]) {
            if (Fields.rows(right) !== Fields.columns(left))throw new java.lang.IllegalArgumentException("matrices cannot be multiplied");
            if (Fields.rows(product) !== Fields.rows(left))throw new java.lang.IllegalArgumentException("product matrix has wrong number of rows");
            if (Fields.columns(right) !== Fields.columns(product))throw new java.lang.IllegalArgumentException("product matrix has wrong number of columns");
            for(let i: number = 0; i < Fields.rows(product); i++) {{
                for(let j: number = 0; j < Fields.columns(product); j++) {{
                    let sum: T = null;
                    for(let j2: number = 0; j2 < Fields.columns(left); j2++) {{
                        const prod: T = left[i][j2].times(right[j2][j]);
                        if (sum == null)sum = prod; else sum = sum.plus(prod);
                    };}
                    product[i][j] = sum;
                };}
            };}
        }

        public static gaussJordanReduction$com_vzome_core_algebra_Fields_Element_A_A<T extends Fields.Element<T>>(matrix: T[][]): number {
            return Fields.gaussJordanReduction$com_vzome_core_algebra_Fields_Element_A_A$com_vzome_core_algebra_Fields_Element_A_A(matrix, matrix);
        }

        public static gaussJordanReduction$com_vzome_core_algebra_Fields_Element_A_A$com_vzome_core_algebra_Fields_Element_A_A<T extends Fields.Element<T>>(immutableMatrix: T[][], adjoined: T[][]): number {
            const nRows: number = Fields.rows(immutableMatrix);
            const matrix: any[][] = Fields.copyOf<any>(immutableMatrix);
            let rank: number = 0;
            for(let col: number = 0; col < Fields.columns(matrix); col++) {{
                let pivotRow: number = -1;
                for(let row: number = rank; row < nRows; row++) {{
                    const element: T = <T><any>matrix[row][col];
                    if (!element.isZero()){
                        pivotRow = row;
                        break;
                    }
                };}
                if (pivotRow >= 0){
                    if (pivotRow !== rank){
                        Fields.swap(matrix, rank, pivotRow);
                        Fields.swap(adjoined, rank, pivotRow);
                        pivotRow = rank;
                    }
                    let scalar: T = <T><any>matrix[pivotRow][col];
                    if (!scalar.isOne()){
                        scalar = scalar.reciprocal();
                        Fields.scale<any>(<T[]>matrix[pivotRow], scalar);
                        Fields.scale<any>(adjoined[pivotRow], scalar);
                    }
                    for(let row: number = 0; row < nRows; row++) {{
                        if (row !== pivotRow){
                            scalar = (<T><any>matrix[row][col]);
                            if (!scalar.isZero()){
                                scalar = scalar.negate();
                                Fields.pivot<any>(matrix, row, scalar, pivotRow);
                                Fields.pivot<any>(adjoined, row, scalar, pivotRow);
                            }
                        }
                    };}
                    rank++;
                }
            };}
            return rank;
        }

        public static gaussJordanReduction<T0 = any>(immutableMatrix?: any, adjoined?: any): number {
            if (((immutableMatrix != null && immutableMatrix instanceof <any>Array && (immutableMatrix.length == 0 || immutableMatrix[0] == null ||immutableMatrix[0] instanceof Array)) || immutableMatrix === null) && ((adjoined != null && adjoined instanceof <any>Array && (adjoined.length == 0 || adjoined[0] == null ||adjoined[0] instanceof Array)) || adjoined === null)) {
                return <any>com.vzome.core.algebra.Fields.gaussJordanReduction$com_vzome_core_algebra_Fields_Element_A_A$com_vzome_core_algebra_Fields_Element_A_A(immutableMatrix, adjoined);
            } else if (((immutableMatrix != null && immutableMatrix instanceof <any>Array && (immutableMatrix.length == 0 || immutableMatrix[0] == null ||immutableMatrix[0] instanceof Array)) || immutableMatrix === null) && adjoined === undefined) {
                return <any>com.vzome.core.algebra.Fields.gaussJordanReduction$com_vzome_core_algebra_Fields_Element_A_A(immutableMatrix);
            } else throw new Error('invalid overload');
        }

        static copyOf<T extends Fields.Element<T>>(matrix: T[][]): any[][] {
            const nRows: number = Fields.rows(matrix);
            const nCols: number = Fields.columns(matrix);
            const copy: any[][] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(nRows);
            for(let i: number = 0; i < nRows; i++) {{
                copy[i] = java.util.Arrays.copyOf<any>(matrix[i], nCols);
            };}
            return copy;
        }

        /**
         * 
         * @param {java.lang.Object[]} array of elements to be swapped
         * @param {number} r index of the first element to be swapped
         * @param {number} s index of the second element to be swapped
         * <br/>
         * Note that since Java implements a multi-dimensional array as an array of arrays,
         * the {@code array} parameter can be an {@code Object[][]} in which case
         * entire rows are swapped rather than an element at a time.
         * Besides being more efficient at run time, this also means
         * that rows of multi-dimensional arrays do not necessarily have to be the same length.
         * @private
         */
        static swap(array: any[], r: number, s: number) {
            const temp: any = array[r];
            array[r] = array[s];
            array[s] = temp;
        }

        static scale<T extends Fields.Element<T>>(array: T[], scalar: T) {
            for(let col: number = 0; col < array.length; col++) {{
                array[col] = scalar.times(array[col]);
            };}
        }

        static pivot<T extends Fields.Element<T>>(matrix: any[][], row: number, scalar: T, rank: number) {
            for(let col: number = 0; col < Fields.columns(matrix); col++) {{
                matrix[row][col] = (<T><any>matrix[row][col]).plus((<T><any>matrix[rank][col]).times(scalar));
            };}
        }
    }
    Fields["__class"] = "com.vzome.core.algebra.Fields";


    export namespace Fields {

        export interface RationalElement<R, T> extends Fields.Element<T> {
            getNumerator(): R;

            getDenominator(): R;

            dividedBy(that: T): T;
        }

        export interface Element<T> {
            times(that: T): T;

            timesInt(that: number): T;

            plus(that: T): T;

            minus(that: T): T;

            reciprocal(): T;

            negate(): T;

            isZero(): boolean;

            isOne(): boolean;

            evaluate(): number;

            getMathML(): string;
        }
    }

}


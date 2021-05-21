
package com.vzome.core.algebra;

import java.util.Arrays;

public class Fields {
    public interface BigRationalElement<R, T> extends RationalElement<R, T> {
        boolean isBig();

        boolean notBig();
    }

    public interface RationalElement<R, T> extends Element<T> {
        R getNumerator();

        R getDenominator();

        T dividedBy(T that);

        double evaluate();
    }

    public interface Element<T> {
        T times(T that);

        T plus(T that);

        T minus(T that);

        T reciprocal();

        T negate();

        boolean isZero();

        boolean isOne();
    }

    public static final int rows(Object[][] matrix) {
        return matrix.length;
    }

    public static final int columns(Object[][] matrix) {
        return matrix[0].length;
    }

    public static <T extends Element<T>> void matrixMultiplication(T[][] left, T[][] right, T[][] product) {
        if (rows(right) != columns(left))
            throw new IllegalArgumentException("matrices cannot be multiplied");
        if (rows(product) != rows(left))
            throw new IllegalArgumentException("product matrix has wrong number of rows");
        if (columns(right) != columns(product))
            throw new IllegalArgumentException("product matrix has wrong number of columns");

        for (int i = 0; i < rows(product); i++) {
            for (int j = 0; j < columns(product); j++) {
                T sum = null;
                for (int j2 = 0; j2 < columns(left); j2++) {
                    T prod = left[i][j2].times(right[j2][j]);
                    if (sum == null)
                        sum = prod;
                    else
                        sum = sum.plus(prod);
                }
                product[i][j] = sum;
            }
        }
    }

    public static <T extends Element<T>> int gaussJordanReduction(T[][] matrix) {
        // Here, the matrix is modified in place so it will be accessible to the caller.
        return gaussJordanReduction(matrix, matrix);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Element<T>> int gaussJordanReduction(T[][] immutableMatrix, T[][] adjoined) {
        // matrices "immutableMatrix" and "adjoined" must have the same number of rows,
        // but do necessarily have the same number of columns and are not necessarily square.
        final int nRows = rows(immutableMatrix);

        // All of the work is done on a copy of immutableMatrix simply named matrix
        // so that immutableMatrix actually remains unchanged.
        // The "adjoined" array is modified in place and will be accessible to the caller.
        final Object[][] matrix = copyOf(immutableMatrix);

        int rank = 0;
        // find successive pivot columns, skipping columns that contain only zeroes
        for (int col = 0; col < columns(matrix); col++) {
            int pivotRow = -1;
            for (int row = rank; row < nRows; row++) {
                T element = (T) matrix[row][col];
                if (!element.isZero()) {
                    pivotRow = row;
                    break;
                }
            }
            if (pivotRow >= 0) {
                // swap pivot row and current rank row if necessary
                if (pivotRow != rank) {
                    swap(matrix, rank, pivotRow);
                    swap(adjoined, rank, pivotRow);
                    pivotRow = rank;
                }
                
                // scale the pivot row if necessary
                T scalar = (T) matrix[pivotRow][col];
                if (!scalar.isOne()) {
                    scalar = scalar.reciprocal();
                    scale((T[]) matrix[pivotRow], scalar);
                    scale(adjoined[pivotRow], scalar);
                }

                // use pivot operation to zero out all entries in the current column 
                // except for the pivot row
                for (int row = 0; row < nRows; row++) {
                    if (row != pivotRow) {
                        scalar = ((T) matrix[row][col]);
                        if (!scalar.isZero()) {
                            scalar = scalar.negate();
                            pivot(matrix, row, scalar, pivotRow);
                            pivot(adjoined, row, scalar, pivotRow);
                        }
                    }
                }
                rank++;
            }
        }
        // After being reduced to Row Echelon Form (ReREF),
        // rank is the number of matrix rows having a one in one of their elements
        return rank;   
    }

    // Elementary matrix operations used in Gauss Jordan Reduction are implemented as individual functions
    
    private static <T extends Element<T>> Object[][] copyOf(T[][] matrix) {
        final int nRows = rows(matrix);
        final int nCols = columns(matrix);
        final Object[][] copy = new Object[nRows][];
        for (int i = 0; i < nRows; i++) {
            copy[i] = Arrays.copyOf(matrix[i], nCols);
        }
        return copy;
    }

    /**
     * 
     * @param array of elements to be swapped 
     * @param r index of the first element to be swapped
     * @param s index of the second element to be swapped
     * <br/>
     * Note that since Java implements a multi-dimensional array as an array of arrays,
     * the {@code array} parameter can be an {@code Object[][]} in which case
     * entire rows are swapped rather than an element at a time. 
     * Besides being more efficient at run time, this also means 
     * that rows of multi-dimensional arrays do not necessarily have to be the same length.
     */
    private static void swap(Object[] array, int r, int s) {
        Object temp = array[r];
        array[r] = array[s];
        array[s] = temp;
    }

    private static <T extends Element<T>> void scale(T[] array, T scalar) {
        for (int col = 0; col < array.length; col++) {
            array[col] = scalar.times(array[col]);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Element<T>> void pivot(Object[][] matrix, int row, T scalar, int rank) {
        for (int col = 0; col < columns(matrix); col++) {
            matrix[row][col] = ((T) matrix[row][col]).plus(((T) matrix[rank][col]).times(scalar));
        }
    }
}

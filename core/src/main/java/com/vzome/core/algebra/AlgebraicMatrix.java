
//(c) Copyright 2015, Scott Vorthmann.

package com.vzome.core.algebra;

import java.lang.reflect.Array;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonValue;

public class AlgebraicMatrix
{
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
        for (AlgebraicNumber[] m : matrix) {
            result = prime * result + Arrays.hashCode(m);
        }
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AlgebraicMatrix other = (AlgebraicMatrix) obj;
		return Arrays.deepEquals(matrix, other.matrix);
	}

	final AlgebraicNumber[][] matrix;

    @JsonValue
    public AlgebraicNumber[][] getMatrix()
    {
        return this .matrix;
    }

    /**
     * Create a new nXn identity matrix.
     * @param field
     * @param dim
     */
    public AlgebraicMatrix( AlgebraicField field, int dim )
    {
        this .matrix = new AlgebraicNumber[ dim ][ dim ];
        for ( int i = 0; i < dim; i++ ) {
            for ( int j = 0; j < dim; j++ ) {
                if ( i == j )
                    this .matrix[ i ][ j ] = field .one();
                else
                    this .matrix[ i ][ j ] = field .zero();
            }
        }
    }
    
    /**
     * Create a new rows X cols zero matrix.
     * @param field
     * @param dim
     */
    public AlgebraicMatrix( AlgebraicField field, int rows, int cols )
    {
        this .matrix = new AlgebraicNumber[ rows ][ cols ];
        for ( int i = 0; i < rows; i++ ) {
            for ( int j = 0; j < cols; j++ ) {
                this .matrix[ i ][ j ] = field .zero();
            }
        }
    }
    
    public AlgebraicMatrix( AlgebraicVector... columns )
    {
        final int rows = columns[ 0 ] .dimension(); // all vectors must be of this same dimension
        final int cols = columns .length;
        this .matrix = new AlgebraicNumber[ rows ][ cols ];
        for ( int i = 0; i < rows; i++ ) {
            for ( int j = 0; j < cols; j++ ) {
                matrix[ i ][ j ] = columns[ j ] .getComponent( i ); // note the transpose,
                //  as we make row vectors rather than copying column vectors
            }
        }
    }

    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();
        for (AlgebraicNumber[] m : this .matrix) {
            buf.append(Arrays.toString(m));
            buf .append( ", " );
        }
        return "[ " + buf.toString() + " ]";
    }

    public AlgebraicMatrix negate()
    {
        AlgebraicField field = this .matrix[ 0 ][ 0 ] .getField();
        AlgebraicMatrix result = new AlgebraicMatrix( field, this .matrix .length );

        for ( int i = 0; i < this .matrix .length; i++ ) {
            for ( int j = 0; j < this .matrix[ i ] .length; j++ ) {
                result .matrix[ i ][ j ] = this .matrix[ i ][ j ] .negate();
            }
        }
        return result;
    }

	public AlgebraicMatrix inverse()
	{
        if(! this.isSquare()) {
            throw new IllegalArgumentException("matrix is not square");
        }
        AlgebraicField field = this .matrix[ 0 ][ 0 ] .getField();
        AlgebraicMatrix result = new AlgebraicMatrix( field, this .matrix .length );

        int rank = Fields .gaussJordanReduction( this .matrix, result .matrix );
        if(rank != matrix.length) {
            // TODO: What should we do here?
            System.err.println((new Throwable()).getStackTrace()[0].getMethodName() 
                    + " expects matrix rank to be " + matrix.length + ", but it is " + rank + "."); 
        }
        return result;
	}

    public AlgebraicMatrix transpose()
    {
        AlgebraicField field = this .matrix[ 0 ][ 0 ] .getField();
        AlgebraicMatrix result = new AlgebraicMatrix( field, this .matrix[0].length, this .matrix.length  );

        for ( int i = 0; i < result .matrix .length; i++ ) {
            for ( int j = 0; j < this .matrix .length; j++ ) {
                result .matrix[ i ][ j ] = this .matrix[ j ][ i ];
            }
        }
        return result;
    }

	public AlgebraicMatrix times( AlgebraicMatrix that )
	{
        AlgebraicField field = this .matrix[ 0 ][ 0 ] .getField();
        AlgebraicMatrix result = new AlgebraicMatrix( field, this .matrix .length, that.matrix[0].length );

        Fields .matrixMultiplication( this .matrix, that .matrix, result .matrix );
        return result;
	}

    // rowVector * matrix
    public AlgebraicVector timesRow( AlgebraicVector rowVector )
    {
        int colLength = rowVector .dimension();
        if ( this.matrix .length != colLength )
            throw new IllegalArgumentException( "vector length incorrect for this matrix: " + rowVector );
        int rowLength = this .matrix[ 0 ] .length;

        AlgebraicNumber[] resultComponents = new AlgebraicNumber[ rowLength ];
        AlgebraicField field = this .matrix[ 0 ][ 0 ] .getField();
        for ( int j = 0; j < rowLength; j++ ) {
            resultComponents[ j ] = field .zero();
            for ( int i = 0; i < colLength; i++ ) {
                AlgebraicNumber product = rowVector .getComponent( i ) .times( this .matrix[ i ][ j ] );
                resultComponents[ j ] = resultComponents[ j ] .plus( product );
            }
        }
        return new AlgebraicVector( resultComponents );
    }

    public AlgebraicVector timesColumn( AlgebraicVector columnVector )
    {
        int rowLength = columnVector .dimension();
        if ( this.matrix[ 0 ] .length != rowLength )
            throw new IllegalArgumentException( "vector length incorrect for this matrix: " + columnVector );
        int colLength = this .matrix .length;

        AlgebraicNumber[] resultComponents = new AlgebraicNumber[ colLength ];
        AlgebraicField field = this .matrix[ 0 ][ 0 ] .getField();
        for ( int i = 0; i < colLength; i++ ) {
            resultComponents[ i ] = field .zero();
            for ( int j = 0; j < rowLength; j++ ) {
                AlgebraicNumber product = columnVector .getComponent( j ) .times( this .matrix[ i ][ j ] );
                resultComponents[ i ] = resultComponents[ i ] .plus( product );
            }
        }
        return new AlgebraicVector( resultComponents );
    }

    public AlgebraicMatrix timesScalar( AlgebraicNumber scalar )
    {
        AlgebraicMatrix result = new AlgebraicMatrix( scalar .getField(), this .matrix .length );

        for ( int i = 0; i < this .matrix .length; i++ ) {
            for ( int j = 0; j < this .matrix[ i ] .length; j++ ) {
                result .matrix[ i ][ j ] = this .matrix[ i ][ j ] .times( scalar );
            }
        }
        return result;
    }
    
    public boolean isSquare() {
        return matrix.length == matrix[0].length;
    }

    public AlgebraicNumber trace() {
        if(! isSquare()) {
            throw new IllegalArgumentException("matrix is not square");
        }
        AlgebraicNumber trace = matrix[0][0].getField().zero();
        for(int i = 0; i < matrix.length; i++) {
            trace = trace.plus( matrix[i][i] );
        }
        return trace;
    }

    public AlgebraicNumber determinant() {
        // AlgebraicMatrix is NOT immutable 
        // so we can't safely cache the determinant.
        // It has to be calculated each time.
        return laplaceDeterminant(matrix);
    }

    public static AlgebraicNumber laplaceDeterminant(AlgebraicNumber[][] matrix) {
        if(matrix.length != matrix[0].length) {
            throw new IllegalArgumentException("matrix is not square");
        }
        AlgebraicNumber determinant = null;
        switch(matrix.length) {
        // 3D case is most common so list it first
        case 3:
            determinant = (matrix[0][0] .times(matrix[1][1]) .times(matrix[2][2]))
                    .plus (matrix[0][1] .times(matrix[1][2]) .times(matrix[2][0]))
                    .plus (matrix[0][2] .times(matrix[1][0]) .times(matrix[2][1]))
                    .minus(matrix[0][2] .times(matrix[1][1]) .times(matrix[2][0]))
                    .minus(matrix[0][0] .times(matrix[1][2]) .times(matrix[2][1]))
                    .minus(matrix[0][1] .times(matrix[1][0]) .times(matrix[2][2]));
            break;
            
        case 2:
            determinant = (matrix[0][0] .times(matrix[1][1])) 
                    .minus(matrix[0][1] .times(matrix[1][0]) );
            break;
            
        case 1:
            determinant = matrix[0][0];
            break;
            
        default:
            // 4D and higher are calculated recursively
            determinant = matrix[0][0].getField().zero();
            final int auxLength = matrix.length - 1;
            AlgebraicNumber sign = matrix[0][0].getField().one();
            for(int i = 0; i < matrix.length; i++) {
                if(!matrix[0][i].isZero()) {
                    AlgebraicNumber[][] aux = (AlgebraicNumber[][]) Array.newInstance(AlgebraicNumber.class, auxLength, auxLength);
                    int iAux = 0;
                    int jAux = 0;
                    for(int row = 1; row < matrix.length; row++){
                        for(int col = 0; col < matrix.length; col++) {
                            if(col != i) {
                                aux[iAux][jAux] = matrix[row][col];
                                jAux++;
                            }
                        }
                        iAux++;
                        jAux = 0;
                    }
                    determinant = determinant.plus( sign .times(matrix[0][i]) .times(laplaceDeterminant(aux)) );
                }
                sign = sign.negate();
            }
        }
        return determinant;
    }
    
    public AlgebraicMatrix setElement( int i, int j, AlgebraicNumber value )
    {
        this .matrix[ i ][ j ] = value;
        return this;
    }

    public AlgebraicNumber getElement( int i, int j )
    {
        return this .matrix[ i ][ j ];
    }
}

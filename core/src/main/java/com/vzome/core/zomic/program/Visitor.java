
package com.vzome.core.zomic.program;


import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.zomic.ZomicException;

public interface Visitor{

	void visitWalk( Walk walk ) throws ZomicException ;

	void visitLabel( String id );

	void visitNested( Nested compound ) throws ZomicException ;

	void visitRepeat( Repeat repeated, int repetitions ) throws ZomicException ;

	void visitRotate( Axis axis, int steps ) throws ZomicException ;

	void visitReflect( Axis blueAxis ) throws ZomicException ;

	void visitMove( Axis axis, AlgebraicNumber length ) throws ZomicException ;

	void visitSymmetry( final Symmetry model, Permute permute ) throws ZomicException ;

    void visitScale( AlgebraicNumber size ) throws ZomicException ;

    void visitSave( Save body, int state ) throws ZomicException ;

    void visitBuild( boolean build, boolean destroy ) throws ZomicException ;

	public class Default implements Visitor{

        @Override
		public  void visitWalk( Walk walk ) throws ZomicException {
            for (ZomicStatement stmt : walk) {
                stmt .accept( this );
            }
		}

        @Override
		public void visitLabel( String id ){}

        @Override
		public 
		void visitNested( Nested compound ) throws ZomicException {
			compound .getBody() .accept( this );
		}

        @Override
		public 
		void visitRepeat( Repeat repeated, int repetitions ) throws ZomicException {
			for ( int i = 0; i < repetitions; i++ ) {
				this .visitNested( repeated );
			}
		}

        @Override
		public 
		void visitRotate( Axis axis, int steps ){}

        @Override
		public 
		void visitReflect( Axis blueAxis ) {}

        @Override
		public 
		void visitMove( Axis axis, AlgebraicNumber length ) throws ZomicException {}

        @Override
		public 
		void visitSymmetry( final Symmetry model, Permute permute ) throws ZomicException {
			visitNested( model );
		}

        @Override
		public 
		void visitSave( Save stmt, int state ) throws ZomicException {
			this .visitNested( stmt );
		}

        @Override
        public 
        void visitScale( AlgebraicNumber size ) {}

        @Override
        public 
        void visitBuild( boolean build, boolean destroy ) {}
        
        @Override
        public
		void visitUntranslatable( String message ) {}


	}

    /**
     * @param untranslatable
     */
    void visitUntranslatable( String message );
}



package com.vzome.core.math.symmetry;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.AlgebraicVectors;
import com.vzome.core.algebra.VefVectorExporter;
import com.vzome.core.math.RealVector;

public class OrbitDotLocator
{
    private final AlgebraicVector worldTrianglePoint;

    private final AlgebraicVector worldTriangleNormal;

    private final AlgebraicMatrix dotTransform;

    private final RealVector orbitProbe;

    private final Symmetry symmetry;

    private VefVectorExporter debugger;

    private AlgebraicField field;

    private StringWriter vefDebugOutput;

    public OrbitDotLocator( Symmetry symmetry, AlgebraicVector[] worldTriangle )
    {
        super();
        this.symmetry = symmetry;
        this .field = symmetry .getField();
        vefDebugOutput = new StringWriter();
        debugger = new VefVectorExporter( vefDebugOutput, this .field, null );

        AlgebraicMatrix oldMatrix = new AlgebraicMatrix( worldTriangle );
        
        AlgebraicVector X = this .field .basisVector( 3, AlgebraicVector.X );
        AlgebraicVector Y = this .field .basisVector( 3, AlgebraicVector.Y );
        AlgebraicVector Z = this .field .basisVector( 3, AlgebraicVector.Z );
        AlgebraicVector[] viewTriangle = new AlgebraicVector[] { Z, X .plus( Z ), Y .plus( Z ) };
        AlgebraicMatrix newMatrix = new AlgebraicMatrix( viewTriangle );
        
        this .dotTransform = newMatrix .times( oldMatrix .inverse() );
        
        AlgebraicVector blueVertex = worldTriangle[ 0 ];
        AlgebraicVector redVertex = worldTriangle[ 1 ];
        AlgebraicVector yellowVertex = worldTriangle[ 2 ];

        this .orbitProbe = redVertex .plus( yellowVertex .plus( blueVertex ) ) .toRealVector();
        
        this .worldTrianglePoint = blueVertex;
        this .worldTriangleNormal = AlgebraicVectors .getNormal( Arrays.asList( worldTriangle ) );
        
        if ( debugger != null ) {
            debugger .exportSegment( this .field .origin(3), redVertex );
            debugger .exportPoint( redVertex );
            debugger .exportSegment( this .field .origin(3), yellowVertex );
            debugger .exportPoint( yellowVertex );
            debugger .exportSegment( this .field .origin(3), blueVertex );
            debugger .exportPoint( blueVertex );
            
            debugger .exportPolygon( Arrays.asList( worldTriangle ) );
            
            debugger .exportPolygon( Arrays.asList( viewTriangle ) );

            debugger .exportSegment( blueVertex, worldTriangleNormal );
        }
    }
    
    public void locateOrbitDot( Direction orbit )
    {
        // Find the zone that intersects the worldTriangle
        AlgebraicVector dotZone = this .symmetry .getAxis( this .orbitProbe, Collections.singleton( orbit ) ) .normal();
        
        // Find the intersection point
        AlgebraicVector lineStart = this .field .origin( 3 );
        AlgebraicVector worldDot = AlgebraicVectors .getLinePlaneIntersection( lineStart, dotZone, this .worldTrianglePoint, this .worldTriangleNormal );
        
        // Now map the intersection from worldTriangle to viewTriangle
        AlgebraicVector viewDot = this .dotTransform .timesColumn( worldDot );

        double dotX = viewDot .getComponent( AlgebraicVector.X ) .evaluate();
        double dotY = viewDot .getComponent( AlgebraicVector.Y ) .evaluate();
        orbit .setDotLocation( dotX, dotY );
        if ( debugger != null ) {
            debugger .exportSegment( this .field .origin(3), dotZone );
            debugger .exportPoint( worldDot );
            debugger .exportPoint( viewDot );            
        }
    }
    
    public String getDebuggerOutput()
    {
        debugger .finishExport();
        this .debugger = null;
        return this .vefDebugOutput .toString();
    }
}

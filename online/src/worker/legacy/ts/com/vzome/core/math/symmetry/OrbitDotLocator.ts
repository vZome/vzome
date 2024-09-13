/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.math.symmetry {
    export class OrbitDotLocator {
        /*private*/ worldTrianglePoint: com.vzome.core.algebra.AlgebraicVector;

        /*private*/ worldTriangleNormal: com.vzome.core.algebra.AlgebraicVector;

        /*private*/ dotTransform: com.vzome.core.algebra.AlgebraicMatrix;

        /*private*/ orbitProbe: com.vzome.core.math.RealVector;

        /*private*/ symmetry: com.vzome.core.math.symmetry.Symmetry;

        /*private*/ debugger: com.vzome.core.algebra.VefVectorExporter;

        /*private*/ field: com.vzome.core.algebra.AlgebraicField;

        /*private*/ vefDebugOutput: java.io.StringWriter;

        public constructor(symmetry: com.vzome.core.math.symmetry.Symmetry, worldTriangle: com.vzome.core.algebra.AlgebraicVector[]) {
            if (this.worldTrianglePoint === undefined) { this.worldTrianglePoint = null; }
            if (this.worldTriangleNormal === undefined) { this.worldTriangleNormal = null; }
            if (this.dotTransform === undefined) { this.dotTransform = null; }
            if (this.orbitProbe === undefined) { this.orbitProbe = null; }
            if (this.symmetry === undefined) { this.symmetry = null; }
            if (this.debugger === undefined) { this.debugger = null; }
            if (this.field === undefined) { this.field = null; }
            if (this.vefDebugOutput === undefined) { this.vefDebugOutput = null; }
            this.symmetry = symmetry;
            this.field = symmetry.getField();
            const oldMatrix: com.vzome.core.algebra.AlgebraicMatrix = new com.vzome.core.algebra.AlgebraicMatrix(worldTriangle);
            const X: com.vzome.core.algebra.AlgebraicVector = this.field.basisVector(3, com.vzome.core.algebra.AlgebraicVector.X);
            const Y: com.vzome.core.algebra.AlgebraicVector = this.field.basisVector(3, com.vzome.core.algebra.AlgebraicVector.Y);
            const Z: com.vzome.core.algebra.AlgebraicVector = this.field.basisVector(3, com.vzome.core.algebra.AlgebraicVector.Z);
            const viewTriangle: com.vzome.core.algebra.AlgebraicVector[] = [Z, X.plus(Z), Y.plus(Z)];
            const newMatrix: com.vzome.core.algebra.AlgebraicMatrix = new com.vzome.core.algebra.AlgebraicMatrix(viewTriangle);
            this.dotTransform = newMatrix.times(oldMatrix.inverse());
            const blueVertex: com.vzome.core.algebra.AlgebraicVector = worldTriangle[0];
            const redVertex: com.vzome.core.algebra.AlgebraicVector = worldTriangle[1];
            const yellowVertex: com.vzome.core.algebra.AlgebraicVector = worldTriangle[2];
            this.orbitProbe = redVertex.plus(yellowVertex.plus(blueVertex)).toRealVector();
            this.worldTrianglePoint = blueVertex;
            this.worldTriangleNormal = com.vzome.core.algebra.AlgebraicVectors.getNormal$java_util_Collection(java.util.Arrays.asList<any>(worldTriangle));
            if (this.debugger != null){
                this.debugger.exportSegment(this.field.origin(3), redVertex);
                this.debugger.exportPoint(redVertex);
                this.debugger.exportSegment(this.field.origin(3), yellowVertex);
                this.debugger.exportPoint(yellowVertex);
                this.debugger.exportSegment(this.field.origin(3), blueVertex);
                this.debugger.exportPoint(blueVertex);
                this.debugger.exportPolygon(java.util.Arrays.asList<any>(worldTriangle));
                this.debugger.exportPolygon(java.util.Arrays.asList<any>(viewTriangle));
                this.debugger.exportSegment(blueVertex, this.worldTriangleNormal);
            }
        }

        public enableDebugger() {
            this.vefDebugOutput = new java.io.StringWriter();
            this.debugger = new com.vzome.core.algebra.VefVectorExporter(this.vefDebugOutput, this.field);
        }

        public locateOrbitDot(orbit: com.vzome.core.math.symmetry.Direction) {
            const dotZone: com.vzome.core.algebra.AlgebraicVector = this.symmetry['getAxis$com_vzome_core_math_RealVector$java_util_Collection'](this.orbitProbe, java.util.Collections.singleton<any>(orbit)).normal();
            const lineStart: com.vzome.core.algebra.AlgebraicVector = this.field.origin(3);
            const worldDot: com.vzome.core.algebra.AlgebraicVector = com.vzome.core.algebra.AlgebraicVectors.getLinePlaneIntersection(lineStart, dotZone, this.worldTrianglePoint, this.worldTriangleNormal);
            const viewDot: com.vzome.core.algebra.AlgebraicVector = this.dotTransform.timesColumn(worldDot);
            const dotX: number = viewDot.getComponent(com.vzome.core.algebra.AlgebraicVector.X).evaluate();
            const dotY: number = viewDot.getComponent(com.vzome.core.algebra.AlgebraicVector.Y).evaluate();
            orbit.setDotLocation(dotX, dotY);
            if (this.debugger != null){
                this.debugger.exportSegment(this.field.origin(3), dotZone);
                this.debugger.exportPoint(worldDot);
                this.debugger.exportPoint(viewDot);
            }
        }

        public getDebuggerOutput(): string {
            this.debugger.finishExport();
            this.debugger = null;
            return this.vefDebugOutput.toString();
        }
    }
    OrbitDotLocator["__class"] = "com.vzome.core.math.symmetry.OrbitDotLocator";

}


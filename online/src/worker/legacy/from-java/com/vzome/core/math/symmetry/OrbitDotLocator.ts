import { java, javaemul } from "../../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicField } from "../../algebra/AlgebraicField.js";
import { AlgebraicMatrix } from "../../algebra/AlgebraicMatrix.js";
import { AlgebraicVector } from "../../algebra/AlgebraicVector.js";
import { AlgebraicVectors } from "../../algebra/AlgebraicVectors.js";
import { VefVectorExporter } from "../../algebra/VefVectorExporter.js";
import { RealVector } from "../RealVector.js";
import { Direction } from "./Direction.js";
import { Symmetry } from "./Symmetry.js";
import { StringWriter } from "../../../../../java/io/StringWriter.js";

export class OrbitDotLocator {
    /*private*/ worldTrianglePoint: AlgebraicVector;

    /*private*/ worldTriangleNormal: AlgebraicVector;

    /*private*/ dotTransform: AlgebraicMatrix;

    /*private*/ orbitProbe: RealVector;

    /*private*/ symmetry: Symmetry;

    /*private*/ debugger: VefVectorExporter;

    /*private*/ field: AlgebraicField;

    /*private*/ vefDebugOutput: StringWriter;

    public constructor(symmetry: Symmetry, worldTriangle: AlgebraicVector[]) {
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
        const oldMatrix: AlgebraicMatrix = new AlgebraicMatrix(worldTriangle);
        const X: AlgebraicVector = this.field.basisVector(3, AlgebraicVector.X);
        const Y: AlgebraicVector = this.field.basisVector(3, AlgebraicVector.Y);
        const Z: AlgebraicVector = this.field.basisVector(3, AlgebraicVector.Z);
        const viewTriangle: AlgebraicVector[] = [Z, X.plus(Z), Y.plus(Z)];
        const newMatrix: AlgebraicMatrix = new AlgebraicMatrix(viewTriangle);
        this.dotTransform = newMatrix.times(oldMatrix.inverse());
        const blueVertex: AlgebraicVector = worldTriangle[0];
        const redVertex: AlgebraicVector = worldTriangle[1];
        const yellowVertex: AlgebraicVector = worldTriangle[2];
        this.orbitProbe = redVertex.plus(yellowVertex.plus(blueVertex)).toRealVector();
        this.worldTrianglePoint = blueVertex;
        this.worldTriangleNormal = AlgebraicVectors.getNormal$java_util_Collection(java.util.Arrays.asList<any>(worldTriangle));
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
        this.vefDebugOutput = new StringWriter();
        this.debugger = new VefVectorExporter(this.vefDebugOutput, this.field);
    }

    public locateOrbitDot(orbit: Direction) {
        const dotZone: AlgebraicVector = this.symmetry['getAxis$com_vzome_core_math_RealVector$java_util_Collection'](this.orbitProbe, java.util.Collections.singleton<any>(orbit)).normal();
        const lineStart: AlgebraicVector = this.field.origin(3);
        const worldDot: AlgebraicVector = AlgebraicVectors.getLinePlaneIntersection(lineStart, dotZone, this.worldTrianglePoint, this.worldTriangleNormal);
        const viewDot: AlgebraicVector = this.dotTransform.timesColumn(worldDot);
        const dotX: number = viewDot.getComponent(AlgebraicVector.X).evaluate();
        const dotY: number = viewDot.getComponent(AlgebraicVector.Y).evaluate();
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

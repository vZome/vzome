/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.parts {
    /**
     * @author Scott Vorthmann
     * 
     * Since every field allows octahedral symmetry, we want the richest triangular
     * tiling of that symmetry, which means we want the fundamental triangles of
     * the reflection group, by definition.  Rather than rely on any symmetry orbits,
     * we can simply hard-code the vertices of a disdyakis dodecahedron, all
     * reflections and permutations of (8,0,0), (4,4,4), and (5,5,0).
     * (This is a simple approximation with integers.)
     * These vertices correspond to blue-like, yellow-like, and green-like axes,
     * in the usual Zome coloring.
     * @param {com.vzome.core.math.symmetry.Direction} dir
     * @class
     */
    export class FastDefaultStrutGeometry implements com.vzome.core.parts.StrutGeometry {
        /*private*/ g2_vector: com.vzome.core.algebra.AlgebraicVector;

        /*private*/ b2_vector: com.vzome.core.algebra.AlgebraicVector;

        /*private*/ y2_vector: com.vzome.core.algebra.AlgebraicVector;

        /*private*/ g2n_vector: com.vzome.core.algebra.AlgebraicVector;

        /*private*/ y2n_vector: com.vzome.core.algebra.AlgebraicVector;

        /*private*/ mAxis: com.vzome.core.math.symmetry.Axis;

        public getFast(): boolean {
            return true;
        }

        public constructor(dir: com.vzome.core.math.symmetry.Direction) {
            if (this.g2_vector === undefined) { this.g2_vector = null; }
            if (this.b2_vector === undefined) { this.b2_vector = null; }
            if (this.y2_vector === undefined) { this.y2_vector = null; }
            if (this.g2n_vector === undefined) { this.g2n_vector = null; }
            if (this.y2n_vector === undefined) { this.y2n_vector = null; }
            if (this.mAxis === undefined) { this.mAxis = null; }
            this.mAxis = dir.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, 0);
            const v: com.vzome.core.algebra.AlgebraicVector = this.mAxis.normal();
            let x: number = v.getComponent(com.vzome.core.algebra.AlgebraicVector.X).evaluate();
            let y: number = v.getComponent(com.vzome.core.algebra.AlgebraicVector.Y).evaluate();
            let z: number = v.getComponent(com.vzome.core.algebra.AlgebraicVector.Z).evaluate();
            let evenParity: boolean = true;
            const xNeg: boolean = x < 0.0;
            if (xNeg){
                x = -x;
                evenParity = !evenParity;
            }
            const yNeg: boolean = y < 0.0;
            if (yNeg){
                y = -y;
                evenParity = !evenParity;
            }
            const zNeg: boolean = z < 0.0;
            if (zNeg){
                z = -z;
                evenParity = !evenParity;
            }
            let first: number;
            let second: number;
            let third: number;
            let firstNeg: boolean;
            let secondNeg: boolean;
            let thirdNeg: boolean;
            if (x >= y){
                if (y >= z){
                    first = com.vzome.core.algebra.AlgebraicVector.X;
                    firstNeg = xNeg;
                    second = com.vzome.core.algebra.AlgebraicVector.Y;
                    secondNeg = yNeg;
                    third = com.vzome.core.algebra.AlgebraicVector.Z;
                    thirdNeg = zNeg;
                } else if (x >= z){
                    first = com.vzome.core.algebra.AlgebraicVector.X;
                    firstNeg = xNeg;
                    second = com.vzome.core.algebra.AlgebraicVector.Z;
                    secondNeg = zNeg;
                    third = com.vzome.core.algebra.AlgebraicVector.Y;
                    thirdNeg = yNeg;
                    evenParity = !evenParity;
                } else {
                    first = com.vzome.core.algebra.AlgebraicVector.Z;
                    firstNeg = zNeg;
                    second = com.vzome.core.algebra.AlgebraicVector.X;
                    secondNeg = xNeg;
                    third = com.vzome.core.algebra.AlgebraicVector.Y;
                    thirdNeg = yNeg;
                }
            } else {
                if (x >= z){
                    first = com.vzome.core.algebra.AlgebraicVector.Y;
                    firstNeg = yNeg;
                    second = com.vzome.core.algebra.AlgebraicVector.X;
                    secondNeg = xNeg;
                    third = com.vzome.core.algebra.AlgebraicVector.Z;
                    thirdNeg = zNeg;
                    evenParity = !evenParity;
                } else if (y >= z){
                    first = com.vzome.core.algebra.AlgebraicVector.Y;
                    firstNeg = yNeg;
                    second = com.vzome.core.algebra.AlgebraicVector.Z;
                    secondNeg = zNeg;
                    third = com.vzome.core.algebra.AlgebraicVector.X;
                    thirdNeg = xNeg;
                } else {
                    first = com.vzome.core.algebra.AlgebraicVector.Z;
                    firstNeg = zNeg;
                    second = com.vzome.core.algebra.AlgebraicVector.Y;
                    secondNeg = yNeg;
                    third = com.vzome.core.algebra.AlgebraicVector.X;
                    thirdNeg = xNeg;
                    evenParity = !evenParity;
                }
            }
            const field: com.vzome.core.algebra.AlgebraicField = v.getField();
            const eight: com.vzome.core.algebra.AlgebraicNumber = field['createRational$long$long'](8, 10);
            this.b2_vector = field.origin(3);
            this.b2_vector.setComponent(first, firstNeg ? eight.negate() : eight);
            const five: com.vzome.core.algebra.AlgebraicNumber = field['createRational$long$long'](5, 10);
            this.g2_vector = field.origin(3);
            this.g2_vector.setComponent(first, firstNeg ? five.negate() : five);
            this.g2_vector.setComponent(second, secondNeg ? five.negate() : five);
            const four: com.vzome.core.algebra.AlgebraicNumber = field['createRational$long$long'](4, 10);
            this.y2_vector = field.origin(3);
            this.y2_vector.setComponent(first, firstNeg ? four.negate() : four);
            this.y2_vector.setComponent(second, secondNeg ? four.negate() : four);
            this.y2_vector.setComponent(third, thirdNeg ? four.negate() : four);
            if (!evenParity){
                const swap: com.vzome.core.algebra.AlgebraicVector = this.y2_vector;
                this.y2_vector = this.g2_vector;
                this.g2_vector = swap;
            }
            const centroid: com.vzome.core.algebra.AlgebraicVector = com.vzome.core.algebra.AlgebraicVectors.getCentroid([this.b2_vector, this.g2_vector, this.y2_vector]);
            const b2g2: com.vzome.core.algebra.AlgebraicVector = this.g2_vector.minus(this.b2_vector);
            const y2g2: com.vzome.core.algebra.AlgebraicVector = this.g2_vector.minus(this.y2_vector);
            const normal: com.vzome.core.algebra.AlgebraicVector = com.vzome.core.algebra.AlgebraicVectors.getNormal$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector(b2g2, y2g2);
            const intersection: com.vzome.core.algebra.AlgebraicVector = com.vzome.core.algebra.AlgebraicVectors.getLinePlaneIntersection(field.origin(3), v, this.g2_vector, normal);
            const g2_offset: com.vzome.core.algebra.AlgebraicVector = this.g2_vector.minus(centroid);
            const y2_offset: com.vzome.core.algebra.AlgebraicVector = this.y2_vector.minus(centroid);
            this.g2_vector = intersection.plus(g2_offset);
            this.y2_vector = intersection.plus(y2_offset);
            this.g2n_vector = intersection.minus(g2_offset);
            this.y2n_vector = intersection.minus(y2_offset);
        }

        /**
         * 
         * @param {*} length
         * @return {com.vzome.core.math.Polyhedron}
         */
        public getStrutPolyhedron(length: com.vzome.core.algebra.AlgebraicNumber): com.vzome.core.math.Polyhedron {
            const field: com.vzome.core.algebra.AlgebraicField = this.mAxis.getDirection().getSymmetry().getField();
            const poly: com.vzome.core.math.Polyhedron = new com.vzome.core.math.Polyhedron(field);
            const strutVector: com.vzome.core.algebra.AlgebraicVector = this.mAxis.normal().scale(length);
            const g1_vector: com.vzome.core.algebra.AlgebraicVector = this.g2_vector.negate().plus(strutVector);
            const y1_vector: com.vzome.core.algebra.AlgebraicVector = this.y2_vector.negate().plus(strutVector);
            const g1n_vector: com.vzome.core.algebra.AlgebraicVector = this.g2n_vector.negate().plus(strutVector);
            const y1n_vector: com.vzome.core.algebra.AlgebraicVector = this.y2n_vector.negate().plus(strutVector);
            poly.addVertex(y1_vector);
            const y1: number = 0;
            poly.addVertex(g1_vector);
            const g1: number = 1;
            poly.addVertex(this.y2_vector);
            const y2: number = 2;
            poly.addVertex(this.g2_vector);
            const g2: number = 3;
            poly.addVertex(y1n_vector);
            const y1n: number = 4;
            poly.addVertex(g1n_vector);
            const g1n: number = 5;
            poly.addVertex(this.y2n_vector);
            const y2n: number = 6;
            poly.addVertex(this.g2n_vector);
            const g2n: number = 7;
            let face: com.vzome.core.math.Polyhedron.Face = poly.newFace();
            face.add(g2);
            face.add(y2);
            face.add(y1n);
            face.add(g1n);
            poly.addFace(face);
            face = poly.newFace();
            face.add(g2);
            face.add(g1n);
            face.add(y1);
            face.add(y2n);
            poly.addFace(face);
            face = poly.newFace();
            face.add(g2n);
            face.add(y2n);
            face.add(y1);
            face.add(g1);
            poly.addFace(face);
            face = poly.newFace();
            face.add(g2n);
            face.add(g1);
            face.add(y1n);
            face.add(y2);
            poly.addFace(face);
            face = poly.newFace();
            face.add(y2);
            face.add(g2);
            face.add(y2n);
            face.add(g2n);
            poly.addFace(face);
            face = poly.newFace();
            face.add(g1);
            face.add(y1);
            face.add(g1n);
            face.add(y1n);
            poly.addFace(face);
            return poly;
        }
    }
    FastDefaultStrutGeometry["__class"] = "com.vzome.core.parts.FastDefaultStrutGeometry";
    FastDefaultStrutGeometry["__interfaces"] = ["com.vzome.core.parts.StrutGeometry"];


}


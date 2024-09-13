/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.viewing {
    export class ExportedVEFStrutGeometry implements com.vzome.core.parts.StrutGeometry {
        static LOGGER: java.util.logging.Logger; public static LOGGER_$LI$(): java.util.logging.Logger { if (ExportedVEFStrutGeometry.LOGGER == null) { ExportedVEFStrutGeometry.LOGGER = java.util.logging.Logger.getLogger("com.vzome.core.viewing.ExportedVEFStrutGeometry"); }  return ExportedVEFStrutGeometry.LOGGER; }

        public prototypeVertices: java.util.List<com.vzome.core.algebra.AlgebraicVector>;

        public prototypeFaces: java.util.List<java.util.List<number>>;

        /*private*/ field: com.vzome.core.algebra.AlgebraicField;

        public prototypeVector: com.vzome.core.algebra.AlgebraicVector;

        public fullScaleVertices: java.util.Set<number>;

        public halfScaleVertices: java.util.Set<number>;

        public constructor(vertices?: any, faces?: any, prototype?: any, fullScaleVertices?: any, halfScaleVertices?: any, field?: any) {
            if (((vertices != null && (vertices.constructor != null && vertices.constructor["__interfaces"] != null && vertices.constructor["__interfaces"].indexOf("java.util.List") >= 0)) || vertices === null) && ((faces != null && (faces.constructor != null && faces.constructor["__interfaces"] != null && faces.constructor["__interfaces"].indexOf("java.util.List") >= 0)) || faces === null) && ((prototype != null && prototype instanceof <any>com.vzome.core.algebra.AlgebraicVector) || prototype === null) && ((fullScaleVertices != null && (fullScaleVertices.constructor != null && fullScaleVertices.constructor["__interfaces"] != null && fullScaleVertices.constructor["__interfaces"].indexOf("java.util.Set") >= 0)) || fullScaleVertices === null) && ((halfScaleVertices != null && (halfScaleVertices.constructor != null && halfScaleVertices.constructor["__interfaces"] != null && halfScaleVertices.constructor["__interfaces"].indexOf("java.util.Set") >= 0)) || halfScaleVertices === null) && ((field != null && (field.constructor != null && field.constructor["__interfaces"] != null && field.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicField") >= 0)) || field === null)) {
                let __args = arguments;
                if (this.prototypeVertices === undefined) { this.prototypeVertices = null; } 
                if (this.prototypeFaces === undefined) { this.prototypeFaces = null; } 
                if (this.field === undefined) { this.field = null; } 
                if (this.prototypeVector === undefined) { this.prototypeVector = null; } 
                if (this.fullScaleVertices === undefined) { this.fullScaleVertices = null; } 
                if (this.halfScaleVertices === undefined) { this.halfScaleVertices = null; } 
                this.prototypeVertices = vertices;
                this.prototypeFaces = faces;
                this.prototypeVector = prototype;
                this.fullScaleVertices = fullScaleVertices;
                this.halfScaleVertices = halfScaleVertices;
                this.field = field;
            } else if (((vertices != null && (vertices.constructor != null && vertices.constructor["__interfaces"] != null && vertices.constructor["__interfaces"].indexOf("java.util.List") >= 0)) || vertices === null) && ((faces != null && (faces.constructor != null && faces.constructor["__interfaces"] != null && faces.constructor["__interfaces"].indexOf("java.util.List") >= 0)) || faces === null) && ((prototype != null && prototype instanceof <any>com.vzome.core.algebra.AlgebraicVector) || prototype === null) && ((fullScaleVertices != null && (fullScaleVertices.constructor != null && fullScaleVertices.constructor["__interfaces"] != null && fullScaleVertices.constructor["__interfaces"].indexOf("java.util.Set") >= 0)) || fullScaleVertices === null) && ((halfScaleVertices != null && (halfScaleVertices.constructor != null && halfScaleVertices.constructor["__interfaces"] != null && halfScaleVertices.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicField") >= 0)) || halfScaleVertices === null) && field === undefined) {
                let __args = arguments;
                let field: any = __args[4];
                {
                    let __args = arguments;
                    let halfScaleVertices: any = null;
                    if (this.prototypeVertices === undefined) { this.prototypeVertices = null; } 
                    if (this.prototypeFaces === undefined) { this.prototypeFaces = null; } 
                    if (this.field === undefined) { this.field = null; } 
                    if (this.prototypeVector === undefined) { this.prototypeVector = null; } 
                    if (this.fullScaleVertices === undefined) { this.fullScaleVertices = null; } 
                    if (this.halfScaleVertices === undefined) { this.halfScaleVertices = null; } 
                    this.prototypeVertices = vertices;
                    this.prototypeFaces = faces;
                    this.prototypeVector = prototype;
                    this.fullScaleVertices = fullScaleVertices;
                    this.halfScaleVertices = halfScaleVertices;
                    this.field = field;
                }
            } else throw new Error('invalid overload');
        }

        public getTriangles(): java.util.List<com.vzome.core.math.Polyhedron.Face.Triangle> {
            return this.getStrutPolyhedron(this.field.one()).getTriangleFaces();
        }

        /**
         * 
         * @param {*} length
         * @return {com.vzome.core.math.Polyhedron}
         */
        public getStrutPolyhedron(length: com.vzome.core.algebra.AlgebraicNumber): com.vzome.core.math.Polyhedron {
            const tipVertex: com.vzome.core.algebra.AlgebraicVector = this.prototypeVector.scale(length);
            const midpoint: com.vzome.core.algebra.AlgebraicVector = tipVertex.scale(this.field['createRational$long$long'](1, 2));
            if ((this.field.getName() === ("snubDodec")) && ExportedVEFStrutGeometry.LOGGER_$LI$().isLoggable(java.util.logging.Level.FINE)){
                ExportedVEFStrutGeometry.LOGGER_$LI$().fine("proto length = " + this.prototypeVector.toRealVector().length());
                ExportedVEFStrutGeometry.LOGGER_$LI$().fine("strut length = " + length.evaluate());
                ExportedVEFStrutGeometry.LOGGER_$LI$().fine("tip length = " + tipVertex.toRealVector().length());
            }
            const result: com.vzome.core.math.Polyhedron = new com.vzome.core.math.Polyhedron(this.field);
            for(let i: number = 0; i < this.prototypeVertices.size(); i++) {{
                let vertex: com.vzome.core.algebra.AlgebraicVector = this.prototypeVertices.get(i);
                if (this.fullScaleVertices.contains(i)){
                    vertex = vertex.plus(tipVertex);
                } else if (this.halfScaleVertices != null && this.halfScaleVertices.contains(i)){
                    vertex = vertex.plus(midpoint);
                }
                result.addVertex(vertex);
            };}
            for(let index=this.prototypeFaces.iterator();index.hasNext();) {
                let prototypeFace = index.next();
                {
                    const face: com.vzome.core.math.Polyhedron.Face = result.newFace();
                    face.addAll(prototypeFace);
                    result.addFace(face);
                }
            }
            return result;
        }
    }
    ExportedVEFStrutGeometry["__class"] = "com.vzome.core.viewing.ExportedVEFStrutGeometry";
    ExportedVEFStrutGeometry["__interfaces"] = ["com.vzome.core.parts.StrutGeometry"];


}


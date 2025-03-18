/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.editor {
    export class Duplicator {
        /*private*/ vertexData: java.util.Map<com.vzome.core.algebra.AlgebraicVector, com.vzome.core.construction.Point>;

        /*private*/ edit: com.vzome.core.editor.api.ChangeManifestations;

        /*private*/ offset: com.vzome.core.algebra.AlgebraicVector;

        public constructor(edit: com.vzome.core.editor.api.ChangeManifestations, offset: com.vzome.core.algebra.AlgebraicVector) {
            this.vertexData = <any>(new java.util.HashMap<any, any>());
            if (this.edit === undefined) { this.edit = null; }
            if (this.offset === undefined) { this.offset = null; }
            this.edit = edit;
            this.offset = offset;
        }

        public duplicateManifestation(man: com.vzome.core.model.Manifestation) {
            const constr: com.vzome.core.construction.Construction = this.duplicateConstruction(man);
            this.edit.manifestConstruction(constr);
        }

        public duplicateConstruction(man: com.vzome.core.model.Manifestation): com.vzome.core.construction.Construction {
            if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                const vector: com.vzome.core.algebra.AlgebraicVector = (<com.vzome.core.model.Connector><any>man).getLocation();
                return this.getVertex(vector);
            } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                const strut: com.vzome.core.model.Strut = <com.vzome.core.model.Strut><any>man;
                const p1: com.vzome.core.construction.Point = this.getVertex(strut.getLocation());
                const p2: com.vzome.core.construction.Point = this.getVertex(strut.getEnd());
                return new com.vzome.core.construction.SegmentJoiningPoints(p1, p2);
            } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                const vs: java.util.List<com.vzome.core.construction.Point> = <any>(new java.util.ArrayList<any>());
                for(let index=(<com.vzome.core.model.Panel><any>man).iterator();index.hasNext();) {
                    let v = index.next();
                    {
                        vs.add(this.getVertex(v));
                    }
                }
                return new com.vzome.core.construction.PolygonFromVertices(vs.toArray<any>([]));
            }
            return null;
        }

        getVertex(vertexVector: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.construction.Point {
            let result: com.vzome.core.construction.Point = this.vertexData.get(vertexVector);
            if (result == null){
                const key: com.vzome.core.algebra.AlgebraicVector = vertexVector;
                if (this.offset != null)vertexVector = vertexVector.plus(this.offset);
                result = new com.vzome.core.construction.FreePoint(vertexVector);
                this.vertexData.put(key, result);
            }
            return result;
        }
    }
    Duplicator["__class"] = "com.vzome.core.editor.Duplicator";

}


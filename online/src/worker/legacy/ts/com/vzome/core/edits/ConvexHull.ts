/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export abstract class ConvexHull extends com.vzome.core.editor.api.ChangeManifestations {
        public constructor(editorModel: com.vzome.core.editor.api.EditorModel) {
            super(editorModel);
        }

        getSelectedVertexSet(unselectAll: boolean): java.util.Set<com.vzome.core.algebra.AlgebraicVector> {
            const vertexSet: java.util.Set<com.vzome.core.algebra.AlgebraicVector> = <any>(new java.util.HashSet<any>());
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                        vertexSet.add(man.getLocation());
                    } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                        vertexSet.add(man.getLocation());
                        vertexSet.add((<com.vzome.core.model.Strut><any>man).getEnd());
                    } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                        for(let index=(<com.vzome.core.model.Panel><any>man).iterator();index.hasNext();) {
                            let vertex = index.next();
                            {
                                vertexSet.add(vertex);
                            }
                        }
                    }
                    if (unselectAll){
                        this.unselect$com_vzome_core_model_Manifestation(man);
                    }
                }
            }
            return vertexSet;
        }
    }
    ConvexHull["__class"] = "com.vzome.core.edits.ConvexHull";

}


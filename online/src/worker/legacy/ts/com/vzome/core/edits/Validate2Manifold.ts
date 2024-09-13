/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class Validate2Manifold extends com.vzome.core.editor.api.ChangeManifestations {
        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
            super(editor);
        }

        showStrut(strut: com.vzome.core.model.Strut) {
            const a: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(strut.getLocation());
            this.manifestConstruction(a);
            const b: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(strut.getEnd());
            this.manifestConstruction(b);
            const segment: com.vzome.core.construction.Segment = new com.vzome.core.construction.SegmentJoiningPoints(a, b);
            this.manifestConstruction(segment);
        }

        /**
         * 
         */
        public perform() {
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                super.unselect$com_vzome_core_model_Manifestation$boolean(man, true)
            }
            this.hideConnectors();
            this.hideStruts();
            this.redo();
            const edges: Validate2Manifold.Edges = new Validate2Manifold.Edges();
            for(let index=com.vzome.core.editor.api.Manifestations.getVisiblePanels(this.mManifestations).iterator();index.hasNext();) {
                let panel = index.next();
                {
                    let v0: com.vzome.core.algebra.AlgebraicVector = null;
                    let prev: com.vzome.core.algebra.AlgebraicVector = null;
                    for(let index=panel.iterator();index.hasNext();) {
                        let vertex = index.next();
                        {
                            if (v0 == null){
                                v0 = vertex;
                                prev = vertex;
                            } else {
                                const strut: com.vzome.core.model.Strut = new com.vzome.core.model.StrutImpl(prev, vertex);
                                edges.addStrut(strut, panel);
                                prev = vertex;
                            }
                        }
                    }
                    const strut: com.vzome.core.model.Strut = new com.vzome.core.model.StrutImpl(prev, v0);
                    edges.addStrut(strut, panel);
                }
            }
            let invalid: boolean = false;
            for(let index=edges.entrySet().iterator();index.hasNext();) {
                let entry = index.next();
                {
                    if (entry.getValue().size() !== 2){
                        this.showStrut(entry.getKey());
                        invalid = true;
                    }
                }
            }
            if (invalid){
                this.hidePanels();
                this.redo();
                return;
            }
            for(let index=edges.entrySet().iterator();index.hasNext();) {
                let entry = index.next();
                {
                    const strut: com.vzome.core.model.Strut = entry.getKey();
                    const v1: com.vzome.core.algebra.AlgebraicVector = strut.getLocation();
                    const v2: com.vzome.core.algebra.AlgebraicVector = strut.getEnd();
                    const oriented: (p1: com.vzome.core.model.Panel) => boolean = ((v1,v2) => {
                        return (p) => {
                            let prev: com.vzome.core.algebra.AlgebraicVector = null;
                            for(let index=p.iterator();index.hasNext();) {
                                let v = index.next();
                                {
                                    if (v.equals(v2) && prev != null)return v1.equals(prev);
                                    prev = v;
                                }
                            }
                            return v1.equals(prev);
                        }
                    })(v1,v2);
                    const panels: com.vzome.core.model.Panel[] = [null, null];
                    entry.getValue().toArray<any>(panels);
                    const c1: boolean = (target => (typeof target === 'function') ? target(panels[0]) : (<any>target).apply(panels[0]))(oriented);
                    const c2: boolean = (target => (typeof target === 'function') ? target(panels[1]) : (<any>target).apply(panels[1]))(oriented);
                    if (c1 === c2){
                        invalid = true;
                        this.showStrut(strut);
                    }
                }
            }
            if (invalid){
                this.redo();
                return;
            }
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "Validate2Manifold";
        }
    }
    Validate2Manifold["__class"] = "com.vzome.core.edits.Validate2Manifold";


    export namespace Validate2Manifold {

        export class Edges extends java.util.HashMap<com.vzome.core.model.Strut, java.util.Collection<com.vzome.core.model.Panel>> {
            addStrut(strut: com.vzome.core.model.Strut, panel: com.vzome.core.model.Panel) {
                let existing: java.util.Collection<com.vzome.core.model.Panel> = this.get(strut);
                if (existing == null){
                    existing = <any>(new java.util.HashSet<com.vzome.core.model.Panel>());
                    this.put(strut, existing);
                }
                existing.add(panel);
            }

            constructor() {
                super();
            }
        }
        Edges["__class"] = "com.vzome.core.edits.Validate2Manifold.Edges";
        Edges["__interfaces"] = ["java.lang.Cloneable","java.util.Map","java.io.Serializable"];


    }

}


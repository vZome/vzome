import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { FreePoint } from "../construction/FreePoint.js";
import { Point } from "../construction/Point.js";
import { Segment } from "../construction/Segment.js";
import { SegmentJoiningPoints } from "../construction/SegmentJoiningPoints.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { Manifestations } from "../editor/api/Manifestations.js";
import { Panel } from "../model/Panel.js";
import { Strut } from "../model/Strut.js";
import { StrutImpl } from "../model/StrutImpl.js";

export class Validate2Manifold extends ChangeManifestations {
    public constructor(editor: EditorModel) {
        super(editor);
    }

    showStrut(strut: Strut) {
        const a: Point = new FreePoint(strut.getLocation());
        this.manifestConstruction(a);
        const b: Point = new FreePoint(strut.getEnd());
        this.manifestConstruction(b);
        const segment: Segment = new SegmentJoiningPoints(a, b);
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
        for(let index=Manifestations.getVisiblePanels(this.mManifestations).iterator();index.hasNext();) {
            let panel = index.next();
            {
                let v0: AlgebraicVector = null;
                let prev: AlgebraicVector = null;
                for(let index=panel.iterator();index.hasNext();) {
                    let vertex = index.next();
                    {
                        if (v0 == null){
                            v0 = vertex;
                            prev = vertex;
                        } else {
                            const strut: Strut = new StrutImpl(prev, vertex);
                            edges.addStrut(strut, panel);
                            prev = vertex;
                        }
                    }
                }
                const strut: Strut = new StrutImpl(prev, v0);
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
                const strut: Strut = entry.getKey();
                const v1: AlgebraicVector = strut.getLocation();
                const v2: AlgebraicVector = strut.getEnd();
                const oriented: (p1: Panel) => boolean = ((v1,v2) => {
                    return (p) => {
                        let prev: AlgebraicVector = null;
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
                const panels: Panel[] = [null, null];
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

    export class Edges extends java.util.HashMap<Strut, java.util.Collection<Panel>> {
        addStrut(strut: Strut, panel: Panel) {
            let existing: java.util.Collection<Panel> = this.get(strut);
            if (existing == null){
                existing = <any>(new java.util.HashSet<Panel>());
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

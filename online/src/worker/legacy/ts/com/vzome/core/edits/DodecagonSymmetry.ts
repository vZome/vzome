/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class DodecagonSymmetry extends com.vzome.core.editor.api.ChangeManifestations {
        /*private*/ center: com.vzome.core.construction.Point;

        /*private*/ symmetry: com.vzome.core.math.symmetry.Symmetry;

        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
            super(editor);
            if (this.center === undefined) { this.center = null; }
            if (this.symmetry === undefined) { this.symmetry = null; }
            this.center = (<com.vzome.core.editor.api.ImplicitSymmetryParameters><any>editor).getCenterPoint();
            this.symmetry = (<com.vzome.core.editor.api.SymmetryAware><any>editor)['getSymmetrySystem$']().getSymmetry();
        }

        /**
         * 
         */
        public perform() {
            const transform: com.vzome.core.construction.Transformation = new com.vzome.core.construction.SymmetryTransformation(this.symmetry, 1, this.center);
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    let c: com.vzome.core.construction.Construction = man.getFirstConstruction();
                    for(let i: number = 0; i < 11; i++) {{
                        c = transform.transform$com_vzome_core_construction_Construction(c);
                        if (c == null)continue;
                        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(c));
                    };}
                }
            }
            this.redo();
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "DodecagonSymmetry";
        }
    }
    DodecagonSymmetry["__class"] = "com.vzome.core.edits.DodecagonSymmetry";

}


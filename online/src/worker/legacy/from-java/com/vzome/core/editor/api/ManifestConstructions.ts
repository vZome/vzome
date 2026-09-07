import { java, javaemul } from "../../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Color } from "../../construction/Color.js";
import { Construction } from "../../construction/Construction.js";
import { ConstructionChanges } from "../../construction/ConstructionChanges.js";
import { ChangeManifestations } from "./ChangeManifestations.js";
import { Manifestation } from "../../model/Manifestation.js";

export class ManifestConstructions extends java.util.ArrayList<Construction> implements ConstructionChanges {
    /*private*/ edit: ChangeManifestations;

    public constructor(edit: ChangeManifestations) {
        super();
        if (this.edit === undefined) { this.edit = null; }
        this.edit = edit;
    }

    public constructionAdded$com_vzome_core_construction_Construction(c: Construction) {
        this.edit.manifestConstruction(c);
        this.edit.redo();
    }

    public constructionAdded$com_vzome_core_construction_Construction$com_vzome_core_construction_Color(c: Construction, color: Color) {
        const manifestation: Manifestation = this.edit.manifestConstruction(c);
        if (color != null)this.edit.colorManifestation(manifestation, color);
        this.edit.select$com_vzome_core_model_Manifestation(manifestation);
        this.edit.redo();
    }

    /**
     * 
     * @param {Construction} c
     * @param {Color} color
     */
    public constructionAdded(c?: any, color?: any) {
        if (((c != null && c instanceof <any>Construction) || c === null) && ((color != null && color instanceof <any>Color) || color === null)) {
            return <any>this.constructionAdded$com_vzome_core_construction_Construction$com_vzome_core_construction_Color(c, color);
        } else if (((c != null && c instanceof <any>Construction) || c === null) && color === undefined) {
            return <any>this.constructionAdded$com_vzome_core_construction_Construction(c);
        } else throw new Error('invalid overload');
    }
}
ManifestConstructions["__class"] = "com.vzome.core.editor.api.ManifestConstructions";
ManifestConstructions["__interfaces"] = ["java.util.RandomAccess","java.util.List","java.lang.Cloneable","com.vzome.core.construction.ConstructionChanges","java.util.Collection","java.lang.Iterable","java.io.Serializable"];

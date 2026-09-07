import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { VefToModel } from "../construction/VefToModel.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { ManifestConstructions } from "../editor/api/ManifestConstructions.js";
import { ImportMesh } from "./ImportMesh.js";

export class LoadVEF extends ImportMesh {
    public constructor(editor: EditorModel) {
        super(editor);
    }

    deselectInputs(): boolean {
        return false;
    }

    /**
     * 
     * @param {AlgebraicVector} offset
     * @param {ManifestConstructions} events
     * @param {*} registry
     */
    parseMeshData(offset: AlgebraicVector, events: ManifestConstructions, registry: AlgebraicField.Registry) {
        const v2m: VefToModel = new VefToModel(this.projection, events, this.scale, offset);
        v2m.parseVEF(this.meshData, this.mManifestations.getField());
    }

    /**
     * 
     * @return {string}
     */
    getXmlElementName(): string {
        return "LoadVEF";
    }
}
LoadVEF["__class"] = "com.vzome.core.edits.LoadVEF";

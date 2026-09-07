import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { XmlSaveFormat } from "../commands/XmlSaveFormat.js";
import { FreePoint } from "../construction/FreePoint.js";
import { Point } from "../construction/Point.js";
import { Segment } from "../construction/Segment.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { ImplicitSymmetryParameters } from "../editor/api/ImplicitSymmetryParameters.js";
import { SymmetryAware } from "../editor/api/SymmetryAware.js";
import { Projection } from "../math/Projection.js";
import { QuaternionProjection } from "../math/QuaternionProjection.js";
import { Direction } from "../math/symmetry/Direction.js";
import { Symmetry } from "../math/symmetry/Symmetry.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

export class GhostSymmetry24Cell extends ChangeManifestations {
    /*private*/ field: AlgebraicField;

    /*private*/ proj: Projection;

    /*private*/ symmAxis: Segment;

    /*private*/ symm: Symmetry;

    public constructor(editor: EditorModel) {
        super(editor);
        if (this.field === undefined) { this.field = null; }
        if (this.proj === undefined) { this.proj = null; }
        if (this.symmAxis === undefined) { this.symmAxis = null; }
        if (this.symm === undefined) { this.symm = null; }
        this.symm = (<SymmetryAware><any>editor)['getSymmetrySystem$']().getSymmetry();
        this.field = this.symm.getField();
        this.symmAxis = (<ImplicitSymmetryParameters><any>editor).getSymmetrySegment();
    }

    /**
     * 
     * @return {string}
     */
    getXmlElementName(): string {
        return "GhostSymmetry24Cell";
    }

    /**
     * 
     * @param {*} result
     */
    public getXmlAttributes(result: Element) {
        if (this.symmAxis != null)XmlSaveFormat.serializeSegment(result, "start", "end", this.symmAxis);
    }

    /**
     * 
     * @param {*} xml
     * @param {XmlSaveFormat} format
     */
    public setXmlAttributes(xml: Element, format: XmlSaveFormat) {
        this.symmAxis = format.parseSegment$org_w3c_dom_Element$java_lang_String$java_lang_String(xml, "start", "end");
    }

    /**
     * 
     */
    public perform() {
        if (this.symmAxis == null)this.proj = new Projection.Default(this.field); else this.proj = new QuaternionProjection(this.field, null, this.symmAxis.getOffset().scale(this.field['createPower$int'](-5)));
        const blue: Direction = this.symm.getDirection("blue");
        const green: Direction = this.symm.getDirection("green");
        for(let k: number = 0; k < 12; k++) {{
            const A1: AlgebraicVector = blue.getAxis$int$int(Symmetry.PLUS, (k + 2) % 12).normal();
            const A2: AlgebraicVector = green.getAxis$int$int(Symmetry.PLUS, (5 * k + 2) % 12).normal();
            const B1: AlgebraicVector = green.getAxis$int$int(Symmetry.PLUS, (k + 2) % 12).normal();
            const B2: AlgebraicVector = blue.getAxis$int$int(Symmetry.PLUS, (5 * k + 5) % 12).normal();
            let projected: AlgebraicVector = this.symm.getField().origin(4);
            projected.setComponent(0, A2.getComponent(0));
            projected.setComponent(1, A2.getComponent(1));
            projected.setComponent(2, A1.getComponent(0));
            projected.setComponent(3, A1.getComponent(1));
            if (this.proj != null)projected = this.proj.projectImage(projected, true);
            let p: Point = new FreePoint(projected.scale(this.field['createPower$int'](5)));
            p.setIndex(k);
            this.manifestConstruction(p);
            projected = this.symm.getField().origin(4);
            projected.setComponent(0, B2.getComponent(0));
            projected.setComponent(1, B2.getComponent(1));
            projected.setComponent(2, B1.getComponent(0));
            projected.setComponent(3, B1.getComponent(1));
            if (this.proj != null)projected = this.proj.projectImage(projected, true);
            p = new FreePoint(projected.scale(this.field['createPower$int'](5)));
            p.setIndex(12 + k);
            this.manifestConstruction(p);
        };}
        this.redo();
    }
}
GhostSymmetry24Cell["__class"] = "com.vzome.core.edits.GhostSymmetry24Cell";

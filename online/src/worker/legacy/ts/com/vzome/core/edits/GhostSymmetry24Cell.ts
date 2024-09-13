/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class GhostSymmetry24Cell extends com.vzome.core.editor.api.ChangeManifestations {
        /*private*/ field: com.vzome.core.algebra.AlgebraicField;

        /*private*/ proj: com.vzome.core.math.Projection;

        /*private*/ symmAxis: com.vzome.core.construction.Segment;

        /*private*/ symm: com.vzome.core.math.symmetry.Symmetry;

        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
            super(editor);
            if (this.field === undefined) { this.field = null; }
            if (this.proj === undefined) { this.proj = null; }
            if (this.symmAxis === undefined) { this.symmAxis = null; }
            if (this.symm === undefined) { this.symm = null; }
            this.symm = (<com.vzome.core.editor.api.SymmetryAware><any>editor)['getSymmetrySystem$']().getSymmetry();
            this.field = this.symm.getField();
            this.symmAxis = (<com.vzome.core.editor.api.ImplicitSymmetryParameters><any>editor).getSymmetrySegment();
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
        public getXmlAttributes(result: org.w3c.dom.Element) {
            if (this.symmAxis != null)com.vzome.core.commands.XmlSaveFormat.serializeSegment(result, "start", "end", this.symmAxis);
        }

        /**
         * 
         * @param {*} xml
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         */
        public setXmlAttributes(xml: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat) {
            this.symmAxis = format.parseSegment$org_w3c_dom_Element$java_lang_String$java_lang_String(xml, "start", "end");
        }

        /**
         * 
         */
        public perform() {
            if (this.symmAxis == null)this.proj = new com.vzome.core.math.Projection.Default(this.field); else this.proj = new com.vzome.core.math.QuaternionProjection(this.field, null, this.symmAxis.getOffset().scale(this.field['createPower$int'](-5)));
            const blue: com.vzome.core.math.symmetry.Direction = this.symm.getDirection("blue");
            const green: com.vzome.core.math.symmetry.Direction = this.symm.getDirection("green");
            for(let k: number = 0; k < 12; k++) {{
                const A1: com.vzome.core.algebra.AlgebraicVector = blue.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, (k + 2) % 12).normal();
                const A2: com.vzome.core.algebra.AlgebraicVector = green.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, (5 * k + 2) % 12).normal();
                const B1: com.vzome.core.algebra.AlgebraicVector = green.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, (k + 2) % 12).normal();
                const B2: com.vzome.core.algebra.AlgebraicVector = blue.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, (5 * k + 5) % 12).normal();
                let projected: com.vzome.core.algebra.AlgebraicVector = this.symm.getField().origin(4);
                projected.setComponent(0, A2.getComponent(0));
                projected.setComponent(1, A2.getComponent(1));
                projected.setComponent(2, A1.getComponent(0));
                projected.setComponent(3, A1.getComponent(1));
                if (this.proj != null)projected = this.proj.projectImage(projected, true);
                let p: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(projected.scale(this.field['createPower$int'](5)));
                p.setIndex(k);
                this.manifestConstruction(p);
                projected = this.symm.getField().origin(4);
                projected.setComponent(0, B2.getComponent(0));
                projected.setComponent(1, B2.getComponent(1));
                projected.setComponent(2, B1.getComponent(0));
                projected.setComponent(3, B1.getComponent(1));
                if (this.proj != null)projected = this.proj.projectImage(projected, true);
                p = new com.vzome.core.construction.FreePoint(projected.scale(this.field['createPower$int'](5)));
                p.setIndex(12 + k);
                this.manifestConstruction(p);
            };}
            this.redo();
        }
    }
    GhostSymmetry24Cell["__class"] = "com.vzome.core.edits.GhostSymmetry24Cell";

}


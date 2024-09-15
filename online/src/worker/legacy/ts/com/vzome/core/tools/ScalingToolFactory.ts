/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.tools {
    export class ScalingToolFactory extends com.vzome.core.editor.AbstractToolFactory {
        public constructor(tools: com.vzome.core.editor.ToolsModel, symmetry: com.vzome.core.math.symmetry.Symmetry) {
            super(tools, symmetry, com.vzome.core.tools.ScalingTool.ID, com.vzome.core.tools.ScalingTool.LABEL, com.vzome.core.tools.ScalingTool.TOOLTIP);
        }

        /**
         * 
         * @param {number} total
         * @param {number} balls
         * @param {number} struts
         * @param {number} panels
         * @return {boolean}
         */
        countsAreValid(total: number, balls: number, struts: number, panels: number): boolean {
            return (total === 3 && balls === 1 && struts === 2);
        }

        /**
         * 
         * @param {string} id
         * @return {com.vzome.core.editor.Tool}
         */
        public createToolInternal(id: string): com.vzome.core.editor.Tool {
            const tool: com.vzome.core.tools.ScalingTool = new com.vzome.core.tools.ScalingTool(id, this.getSymmetry(), this.getToolsModel());
            let scalePower: number = 0;
            switch((id)) {
            case "scaling.builtin/scale up":
                scalePower = 1;
                break;
            case "scaling.builtin/scale down":
                scalePower = -1;
                break;
            default:
                return tool;
            }
            const field: com.vzome.core.algebra.AlgebraicField = this.getToolsModel().getEditorModel().getRealizedModel().getField();
            tool.setScaleFactor(field['createPower$int'](scalePower));
            return tool;
        }

        /**
         * 
         * @param {*} selection
         * @return {boolean}
         */
        bindParameters(selection: com.vzome.core.editor.api.Selection): boolean {
            const symmetry: com.vzome.core.math.symmetry.Symmetry = this.getSymmetry();
            let offset1: com.vzome.core.algebra.AlgebraicVector = null;
            let offset2: com.vzome.core.algebra.AlgebraicVector = null;
            for(let index=selection.iterator();index.hasNext();) {
                let man = index.next();
                if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                    const strut: com.vzome.core.model.Strut = <com.vzome.core.model.Strut><any>man;
                    if (offset1 == null)offset1 = strut.getOffset(); else offset2 = strut.getOffset();
                }
            }
            const zone1: com.vzome.core.math.symmetry.Axis = symmetry['getAxis$com_vzome_core_algebra_AlgebraicVector'](offset1);
            const zone2: com.vzome.core.math.symmetry.Axis = symmetry['getAxis$com_vzome_core_algebra_AlgebraicVector'](offset2);
            if (zone1 == null || zone2 == null)return false;
            const orbit1: com.vzome.core.math.symmetry.Direction = zone1.getDirection();
            const orbit2: com.vzome.core.math.symmetry.Direction = zone2.getDirection();
            if (orbit1 !== orbit2)return false;
            if (orbit1.isAutomatic())return false;
            const l1: com.vzome.core.algebra.AlgebraicNumber = zone1.getLength(offset1);
            const l2: com.vzome.core.algebra.AlgebraicNumber = zone2.getLength(offset2);
            if (/* equals */(<any>((o1: any, o2: any) => { if (o1 && o1.equals) { return o1.equals(o2); } else { return o1 === o2; } })(l1,l2)))return false;
            return true;
        }
    }
    ScalingToolFactory["__class"] = "com.vzome.core.tools.ScalingToolFactory";
    ScalingToolFactory["__interfaces"] = ["com.vzome.core.editor.SelectionSummary.Listener","com.vzome.api.Tool.Factory"];


}


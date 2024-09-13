/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.tools {
    export class LinearMapToolFactory extends com.vzome.core.editor.AbstractToolFactory {
        /*private*/ originalScaling: boolean;

        public constructor(tools: com.vzome.core.editor.ToolsModel, symmetry: com.vzome.core.math.symmetry.Symmetry, originalScaling: boolean) {
            super(tools, symmetry, com.vzome.core.tools.LinearMapTool.CATEGORY, com.vzome.core.tools.LinearMapTool.LABEL, com.vzome.core.tools.LinearMapTool.TOOLTIP);
            if (this.originalScaling === undefined) { this.originalScaling = false; }
            this.originalScaling = originalScaling;
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
            return (total === 7 && balls === 1 && struts === 6) || (total === 4 && balls === 1 && struts === 3);
        }

        /**
         * 
         * @param {string} id
         * @return {com.vzome.core.editor.Tool}
         */
        public createToolInternal(id: string): com.vzome.core.editor.Tool {
            return new com.vzome.core.tools.LinearMapTool(id, this.getToolsModel(), this.originalScaling);
        }

        /**
         * 
         * @return {com.vzome.core.editor.Tool}
         */
        public createTool(): com.vzome.core.editor.Tool {
            const result: com.vzome.core.editor.Tool = super.createTool();
            result.setCopyColors(false);
            return result;
        }

        /**
         * 
         * @param {*} selection
         * @return {boolean}
         */
        bindParameters(selection: com.vzome.core.editor.api.Selection): boolean {
            let index: number = 0;
            const segments: com.vzome.core.construction.Segment[] = [null, null, null, null, null, null];
            for(let index1=selection.iterator();index1.hasNext();) {
                let man = index1.next();
                {
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0))segments[index++] = <com.vzome.core.construction.Segment>man.getFirstConstruction();
                }
            }
            let c1: com.vzome.core.algebra.AlgebraicVector = com.vzome.core.construction.ChangeOfBasis.findCommonVertex(segments[0], segments[1]);
            let c2: com.vzome.core.algebra.AlgebraicVector = com.vzome.core.construction.ChangeOfBasis.findCommonVertex(segments[2], segments[1]);
            if (c1 == null || c2 == null || !c1.equals(c2))return false;
            if (index === 3)return true;
            c1 = com.vzome.core.construction.ChangeOfBasis.findCommonVertex(segments[3], segments[4]);
            c2 = com.vzome.core.construction.ChangeOfBasis.findCommonVertex(segments[5], segments[4]);
            if (c1 == null || c2 == null || !c1.equals(c2))return false;
            return true;
        }
    }
    LinearMapToolFactory["__class"] = "com.vzome.core.tools.LinearMapToolFactory";
    LinearMapToolFactory["__interfaces"] = ["com.vzome.core.editor.SelectionSummary.Listener","com.vzome.api.Tool.Factory"];


}


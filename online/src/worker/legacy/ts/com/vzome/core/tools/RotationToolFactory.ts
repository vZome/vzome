/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.tools {
    export class RotationToolFactory extends com.vzome.core.editor.AbstractToolFactory {
        static ID: string = "rotation";

        static LABEL: string = "Create a rotation tool";

        static TOOLTIP: string = "<p>Each tool rotates the selected objects around an axis<br>of symmetry.  To create a tool, select a strut that<br>defines that axis.  You can also define the direction<br>and center independently, by selecting a ball for the<br>center and a strut for the axis.  Note: not all struts<br>correspond to rotational symmetries!<br><br>The direction of rotation depends on the strut<br>orientation, which is hard to discover, but easy to<br>control, by dragging out a new strut.<br><br>By default, the input selection will be moved to the new,<br>rotated orientation.  After creating a tool, you can<br>right-click to configure the tool to create a copy, instead.<br></p>";

        /*private*/ noStrut: boolean;

        public constructor(tools?: any, symmetry?: any, id?: any, label?: any, tooltip?: any) {
            if (((tools != null && tools instanceof <any>com.vzome.core.editor.ToolsModel) || tools === null) && ((symmetry != null && (symmetry.constructor != null && symmetry.constructor["__interfaces"] != null && symmetry.constructor["__interfaces"].indexOf("com.vzome.core.math.symmetry.Symmetry") >= 0)) || symmetry === null) && ((typeof id === 'string') || id === null) && ((typeof label === 'string') || label === null) && ((typeof tooltip === 'string') || tooltip === null)) {
                let __args = arguments;
                super(tools, symmetry, id, label, tooltip);
                if (this.noStrut === undefined) { this.noStrut = false; } 
            } else if (((tools != null && tools instanceof <any>com.vzome.core.editor.ToolsModel) || tools === null) && ((symmetry != null && (symmetry.constructor != null && symmetry.constructor["__interfaces"] != null && symmetry.constructor["__interfaces"].indexOf("com.vzome.core.math.symmetry.Symmetry") >= 0)) || symmetry === null) && ((typeof id === 'boolean') || id === null) && label === undefined && tooltip === undefined) {
                let __args = arguments;
                let useSymmetryName: any = __args[2];
                {
                    let __args = arguments;
                    let id: any = (useSymmetryName ? __args[1].getName() + ' ' : "") + RotationToolFactory.ID;
                    let label: any = RotationToolFactory.LABEL;
                    let tooltip: any = RotationToolFactory.TOOLTIP;
                    super(tools, symmetry, id, label, tooltip);
                    if (this.noStrut === undefined) { this.noStrut = false; } 
                }
            } else if (((tools != null && tools instanceof <any>com.vzome.core.editor.ToolsModel) || tools === null) && ((symmetry != null && (symmetry.constructor != null && symmetry.constructor["__interfaces"] != null && symmetry.constructor["__interfaces"].indexOf("com.vzome.core.math.symmetry.Symmetry") >= 0)) || symmetry === null) && id === undefined && label === undefined && tooltip === undefined) {
                let __args = arguments;
                {
                    let __args = arguments;
                    let useSymmetryName: any = false;
                    {
                        let __args = arguments;
                        let id: any = (useSymmetryName ? __args[1].getName() + ' ' : "") + RotationToolFactory.ID;
                        let label: any = RotationToolFactory.LABEL;
                        let tooltip: any = RotationToolFactory.TOOLTIP;
                        super(tools, symmetry, id, label, tooltip);
                        if (this.noStrut === undefined) { this.noStrut = false; } 
                    }
                }
            } else throw new Error('invalid overload');
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
            if (total === 1 && balls === 1){
                this.noStrut = true;
                return true;
            } else if ((total === 1 && struts === 1) || (total === 2 && balls === 1 && struts === 1)){
                this.noStrut = false;
                return true;
            }
            return false;
        }

        /**
         * 
         * @param {string} id
         * @return {com.vzome.core.editor.Tool}
         */
        public createToolInternal(id: string): com.vzome.core.editor.Tool {
            return new com.vzome.core.tools.RotationTool(id, this.getSymmetry(), this.getToolsModel(), false);
        }

        /**
         * 
         * @param {*} selection
         * @return {boolean}
         */
        bindParameters(selection: com.vzome.core.editor.api.Selection): boolean {
            const symmetry: com.vzome.core.math.symmetry.Symmetry = this.getSymmetry();
            for(let index=selection.iterator();index.hasNext();) {
                let man = index.next();
                if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                    const axisStrut: com.vzome.core.model.Strut = <com.vzome.core.model.Strut><any>man;
                    let vector: com.vzome.core.algebra.AlgebraicVector = axisStrut.getOffset();
                    vector = symmetry.getField().projectTo3d(vector, true);
                    const axis: com.vzome.core.math.symmetry.Axis = symmetry['getAxis$com_vzome_core_algebra_AlgebraicVector'](vector);
                    if (axis == null)return false;
                    const perm: com.vzome.core.math.symmetry.Permutation = axis.getRotationPermutation();
                    if (perm == null)return false;
                } else if (this.noStrut){
                    const axis: com.vzome.core.math.symmetry.Axis = symmetry.getPreferredAxis();
                    if (axis == null)return false;
                }
            }
            return true;
        }
    }
    RotationToolFactory["__class"] = "com.vzome.core.tools.RotationToolFactory";
    RotationToolFactory["__interfaces"] = ["com.vzome.core.editor.SelectionSummary.Listener","com.vzome.api.Tool.Factory"];


}


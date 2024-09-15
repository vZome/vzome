/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.tools {
    export class AxialStretchTool extends com.vzome.core.tools.TransformationTool {
        static TOOLTIP_REDSQUASH1: string = "<p>Each tool applies a \"squash\" transformation to the<br>selected objects, compressing along a red axis.  To create<br>a tool, select a ball as the center of the mapping, and a<br>red strut as the direction of the compression.  The ball and<br>strut need not be collinear.<br><br>The mapping comes from the usual Zome projection of the<br>120-cell.  It is the mapping that transforms the central,<br>blue dodecahedron into the compressed form in the next<br>layer outward.<br><br>By default, the input selection will be removed, and replaced<br>with the squashed equivalent.  If you want to keep the inputs,<br>you can right-click after creating the tool, to configure it.<br></p>";

        static TOOLTIP_REDSTRETCH1: string = "<p>Each tool applies a \"stretch\" transformation to the<br>selected objects, stretching along a red axis.  To create<br>a tool, select a ball as the center of the mapping, and a<br>red strut as the direction of the stretch.  The ball and<br>strut need not be collinear.<br><br>The mapping comes from the usual Zome projection of the<br>120-cell.  It is the inverse of the mapping that transforms<br>the central, blue dodecahedron into the compressed form in<br>the next layer outward.<br><br>By default, the input selection will be removed, and replaced<br>with the stretched equivalent.  If you want to keep the inputs,<br>you can right-click after creating the tool, to configure it.<br></p>";

        static TOOLTIP_YELLOWSQUASH: string = "<p>Each tool applies a \"squash\" transformation to the<br>selected objects, compressing along a yellow axis.  To create<br>a tool, select a ball as the center of the mapping, and a<br>yellow strut as the direction of the compression.  The ball and<br>strut need not be collinear.<br><br>The mapping comes from the usual Zome projection of the<br>120-cell.  It is the mapping that transforms the central,<br>blue dodecahedron into the compressed form along a yellow axis.<br><br>By default, the input selection will be removed, and replaced<br>with the squashed equivalent.  If you want to keep the inputs,<br>you can right-click after creating the tool, to configure it.<br></p>";

        static TOOLTIP_YELLOWSTRETCH: string = "<p>Each tool applies a \"stretch\" transformation to the<br>selected objects, stretching along a yellow axis.  To create<br>a tool, select a ball as the center of the mapping, and a<br>yellow strut as the direction of the stretch.  The ball and<br>strut need not be collinear.<br><br>The mapping comes from the usual Zome projection of the<br>120-cell.  It is the inverse of the mapping that transforms<br>the central, blue dodecahedron into the compressed form along<br>a yellow axis.<br><br>By default, the input selection will be removed, and replaced<br>with the stretched equivalent.  If you want to keep the inputs,<br>you can right-click after creating the tool, to configure it.<br></p>";

        static TOOLTIP_REDSQUASH2: string = "<p>Each tool applies a \"squash\" transformation to the<br>selected objects, compressing along a red axis.  To create<br>a tool, select a ball as the center of the mapping, and a<br>red strut as the direction of the compression.  The ball and<br>strut need not be collinear.<br><br>The mapping comes from the usual Zome projection of the<br>120-cell.  It is the mapping that transforms the central,<br>blue dodecahedron into the compressed form in the second<br>layer outward along a red axis.<br><br>By default, the input selection will be removed, and replaced<br>with the squashed equivalent.  If you want to keep the inputs,<br>you can right-click after creating the tool, to configure it.<br></p>";

        static TOOLTIP_REDSTRETCH2: string = "<p>Each tool applies a \"stretch\" transformation to the<br>selected objects, stretching along a red axis.  To create<br>a tool, select a ball as the center of the mapping, and a<br>red strut as the direction of the stretch.  The ball and<br>strut need not be collinear.<br><br>The mapping comes from the usual Zome projection of the<br>120-cell.  It is the inverse of the mapping that transforms<br>the central, blue dodecahedron into the compressed form in<br>the second layer outward along a red axis.<br><br>By default, the input selection will be removed, and replaced<br>with the stretched equivalent.  If you want to keep the inputs,<br>you can right-click after creating the tool, to configure it.<br></p>";

        /*private*/ symmetry: com.vzome.core.math.symmetry.IcosahedralSymmetry;

        /*private*/ stretch: boolean;

        /*private*/ red: boolean;

        /*private*/ first: boolean;

        /*private*/ __com_vzome_core_tools_AxialStretchTool_category: string;

        public constructor(id: string, symmetry: com.vzome.core.math.symmetry.IcosahedralSymmetry, tools: com.vzome.core.editor.ToolsModel, stretch: boolean, red: boolean, first: boolean, category: string) {
            super(id, tools);
            if (this.symmetry === undefined) { this.symmetry = null; }
            if (this.stretch === undefined) { this.stretch = false; }
            if (this.red === undefined) { this.red = false; }
            if (this.first === undefined) { this.first = false; }
            if (this.__com_vzome_core_tools_AxialStretchTool_category === undefined) { this.__com_vzome_core_tools_AxialStretchTool_category = null; }
            this.symmetry = symmetry;
            this.stretch = stretch;
            this.red = red;
            this.first = first;
            this.__com_vzome_core_tools_AxialStretchTool_category = category;
            this.setInputBehaviors(false, true);
        }

        /**
         * 
         * @param {boolean} prepareTool
         * @return {string}
         */
        checkSelection(prepareTool: boolean): string {
            let center: com.vzome.core.construction.Point = null;
            let axis: com.vzome.core.construction.Segment = null;
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    if (prepareTool)this.unselect$com_vzome_core_model_Manifestation(man);
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                        if (center != null)return "Only one ball may be selected";
                        center = <com.vzome.core.construction.Point>(<com.vzome.core.model.Connector><any>man).getFirstConstruction();
                    } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                        if (axis != null)return "Only one strut may be selected";
                        axis = <com.vzome.core.construction.Segment>(<com.vzome.core.model.Strut><any>man).getFirstConstruction();
                    } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                        return "Panels are not supported.";
                    }
                }
            }
            if (center == null)return "Exactly one ball must be selected.";
            if (axis == null)return "Exactly one strut must be selected.";
            const zone: com.vzome.core.math.symmetry.Axis = this.symmetry.getAxis$com_vzome_core_algebra_AlgebraicVector(axis.getOffset());
            if (zone == null)return "Selected alignment strut is not an appropriate axis.";
            let o0: com.vzome.core.algebra.AlgebraicVector;
            let o1: com.vzome.core.algebra.AlgebraicVector;
            let o2: com.vzome.core.algebra.AlgebraicVector;
            let n0: com.vzome.core.algebra.AlgebraicVector;
            let n1: com.vzome.core.algebra.AlgebraicVector;
            let n2: com.vzome.core.algebra.AlgebraicVector;
            switch((zone.getDirection().getName())) {
            case "yellow":
                {
                    if (this.red)return "A red axis strut must be selected.";
                    const blueOrbit: com.vzome.core.math.symmetry.Direction = this.symmetry.getDirection("blue");
                    const blueScale: com.vzome.core.algebra.AlgebraicNumber = blueOrbit.getUnitLength();
                    o0 = blueOrbit.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, 2).normal().scale(blueScale);
                    o1 = blueOrbit.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, 54).normal().scale(blueScale);
                    o2 = blueOrbit.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, 36).normal().scale(blueScale);
                    const redOrbit: com.vzome.core.math.symmetry.Direction = this.symmetry.getDirection("red");
                    const redScale: com.vzome.core.algebra.AlgebraicNumber = redOrbit.getUnitLength();
                    n0 = redOrbit.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, 2).normal().scale(redScale);
                    n1 = redOrbit.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, 46).normal().scale(redScale);
                    n2 = redOrbit.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, 16).normal().scale(redScale);
                    break;
                };
            case "red":
                {
                    if (!this.red)return "A yellow axis strut must be selected.";
                    const blueOrbit: com.vzome.core.math.symmetry.Direction = this.symmetry.getDirection("blue");
                    const blueScale: com.vzome.core.algebra.AlgebraicNumber = blueOrbit.getUnitLength();
                    const redOrbit: com.vzome.core.math.symmetry.Direction = this.symmetry.getDirection("red");
                    let redScale: com.vzome.core.algebra.AlgebraicNumber = redOrbit.getUnitLength();
                    if (this.first){
                        o0 = blueOrbit.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, 56).normal().scale(blueScale);
                        o1 = blueOrbit.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, 38).normal().scale(blueScale);
                        o2 = blueOrbit.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, 40).normal().scale(blueScale);
                        n0 = redOrbit.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, 46).normal().scale(redScale);
                        n1 = redOrbit.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, 1).normal().scale(redScale);
                        n2 = redOrbit.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, 2).normal().scale(redScale);
                    } else {
                        o0 = blueOrbit.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, 37).normal().scale(blueScale);
                        o1 = blueOrbit.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, 25).normal().scale(blueScale);
                        o2 = blueOrbit.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, 45).normal().scale(blueScale);
                        n0 = blueOrbit.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, 37).normal().scale(blueScale);
                        n1 = blueOrbit.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, 25).normal().scale(blueScale);
                        redScale = redScale['times$com_vzome_core_algebra_AlgebraicNumber'](this.symmetry.getField()['createPower$int'](-1));
                        n2 = redOrbit.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, 45).normal().scale(redScale);
                    }
                    break;
                };
            default:
                return "Selected alignment strut is not an appropriate axis.";
            }
            if (prepareTool){
                const orientation: com.vzome.core.algebra.AlgebraicMatrix = this.symmetry.getMatrix(zone.getOrientation());
                const inverse: com.vzome.core.algebra.AlgebraicMatrix = orientation.inverse();
                let oldBasis: com.vzome.core.algebra.AlgebraicMatrix = new com.vzome.core.algebra.AlgebraicMatrix(o0, o1, o2);
                let newBasis: com.vzome.core.algebra.AlgebraicMatrix = new com.vzome.core.algebra.AlgebraicMatrix(n0, n1, n2);
                if (this.stretch){
                    const temp: com.vzome.core.algebra.AlgebraicMatrix = oldBasis;
                    oldBasis = newBasis;
                    newBasis = temp;
                }
                const matrix: com.vzome.core.algebra.AlgebraicMatrix = orientation.times(newBasis.times(oldBasis.inverse()).times(inverse));
                this.transforms = [null];
                this.transforms[0] = new com.vzome.core.construction.MatrixTransformation(matrix, center.getLocation());
            }
            return null;
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "AxialStretchTool";
        }

        /**
         * 
         * @param {*} element
         */
        getXmlAttributes(element: org.w3c.dom.Element) {
            super.getXmlAttributes(element);
            if (this.stretch)element.setAttribute("stretch", "true");
            element.setAttribute("orbit", this.red ? "red" : "yellow");
            if (!this.first)element.setAttribute("first", "false");
        }

        /**
         * 
         * @param {*} element
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         */
        setXmlAttributes(element: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat) {
            let value: string = element.getAttribute("stretch");
            this.stretch = value != null && (value === ("true"));
            value = element.getAttribute("orbit");
            this.red = value === ("red");
            value = element.getAttribute("first");
            this.first = value == null || !(value === ("false"));
            this.__com_vzome_core_tools_AxialStretchTool_category = AxialStretchTool.Factory.getCategory(this.red, this.stretch, this.first);
            this.symmetry = <com.vzome.core.math.symmetry.IcosahedralSymmetry><any>(<com.vzome.core.commands.XmlSymmetryFormat>format).parseSymmetry("icosahedral");
            super.setXmlAttributes(element, format);
        }

        /**
         * 
         * @return {string}
         */
        public getCategory(): string {
            return this.__com_vzome_core_tools_AxialStretchTool_category;
        }
    }
    AxialStretchTool["__class"] = "com.vzome.core.tools.AxialStretchTool";
    AxialStretchTool["__interfaces"] = ["com.vzome.api.Tool"];



    export namespace AxialStretchTool {

        export class Factory extends com.vzome.core.editor.AbstractToolFactory {
            red: boolean;

            stretch: boolean;

            first: boolean;

            static getCategory(red: boolean, stretch: boolean, first: boolean): string {
                if (red)if (first)return stretch ? "redstretch1" : "redsquash1"; else return stretch ? "redstretch2" : "redsquash2"; else return stretch ? "yellowstretch" : "yellowsquash";
            }

            static getLabel(red: boolean, stretch: boolean, first: boolean): string {
                let label: string;
                if (red)if (first)label = stretch ? "weak red stretch" : "weak red squash"; else label = stretch ? "strong red stretch" : "strong red squash"; else label = stretch ? "yellow stretch" : "yellow squash";
                return "Create a " + label + " tool";
            }

            static getToolTip(red: boolean, stretch: boolean, first: boolean): string {
                if (red)if (first)return stretch ? com.vzome.core.tools.AxialStretchTool.TOOLTIP_REDSTRETCH1 : com.vzome.core.tools.AxialStretchTool.TOOLTIP_REDSQUASH1; else return stretch ? com.vzome.core.tools.AxialStretchTool.TOOLTIP_REDSTRETCH2 : com.vzome.core.tools.AxialStretchTool.TOOLTIP_REDSQUASH2; else return stretch ? com.vzome.core.tools.AxialStretchTool.TOOLTIP_YELLOWSTRETCH : com.vzome.core.tools.AxialStretchTool.TOOLTIP_YELLOWSQUASH;
            }

            public constructor(tools: com.vzome.core.editor.ToolsModel, symmetry: com.vzome.core.math.symmetry.IcosahedralSymmetry, red: boolean, stretch: boolean, first: boolean) {
                super(tools, symmetry, Factory.getCategory(red, stretch, first), Factory.getLabel(red, stretch, first), Factory.getToolTip(red, stretch, first));
                if (this.red === undefined) { this.red = false; }
                if (this.stretch === undefined) { this.stretch = false; }
                if (this.first === undefined) { this.first = false; }
                this.red = red;
                this.stretch = stretch;
                this.first = first;
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
                return (total === 2 && balls === 1 && struts === 1);
            }

            /**
             * 
             * @param {string} id
             * @return {com.vzome.core.editor.Tool}
             */
            public createToolInternal(id: string): com.vzome.core.editor.Tool {
                const category: string = Factory.getCategory(this.red, this.stretch, this.first);
                return new com.vzome.core.tools.AxialStretchTool(id, <com.vzome.core.math.symmetry.IcosahedralSymmetry><any>this.getSymmetry(), this.getToolsModel(), this.stretch, this.red, this.first, category);
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
                const symmetry: com.vzome.core.math.symmetry.IcosahedralSymmetry = <com.vzome.core.math.symmetry.IcosahedralSymmetry><any>this.getSymmetry();
                for(let index=selection.iterator();index.hasNext();) {
                    let man = index.next();
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                        const axisStrut: com.vzome.core.model.Strut = <com.vzome.core.model.Strut><any>man;
                        let vector: com.vzome.core.algebra.AlgebraicVector = axisStrut.getOffset();
                        vector = symmetry.getField().projectTo3d(vector, true);
                        const axis: com.vzome.core.math.symmetry.Axis = symmetry.getAxis$com_vzome_core_algebra_AlgebraicVector(vector);
                        if (axis == null)return false;
                        const orbitName: string = axis.getDirection().getName();
                        if (this.red)return orbitName === ("red"); else return orbitName === ("yellow");
                    }
                }
                return true;
            }
        }
        Factory["__class"] = "com.vzome.core.tools.AxialStretchTool.Factory";
        Factory["__interfaces"] = ["com.vzome.core.editor.SelectionSummary.Listener","com.vzome.api.Tool.Factory"];


    }

}


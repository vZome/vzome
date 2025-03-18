/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.exporters {
    /**
     * An exporter that produces a parametric OpenSCAD file,
     * to support generation of STL files for struts of arbitrary length.
     * This is based on Aaron Siegel's "zome-strut.scad" library.
     * 
     * @author vorth
     * @class
     * @extends com.vzome.core.exporters.DocumentExporter
     */
    export class OpenScadExporter extends com.vzome.core.exporters.DocumentExporter {
        /**
         * 
         * @param {*} doc
         * @param {java.io.File} file
         * @param {java.io.Writer} writer
         * @param {number} height
         * @param {number} width
         */
        public exportDocument(doc: com.vzome.core.exporters.DocumentIntf, file: java.io.File, writer: java.io.Writer, height: number, width: number) {
            const toolsModel: com.vzome.core.editor.ToolsModel = doc.getToolsModel();
            this.mModel = doc.getRenderedModel();
            const field: com.vzome.core.algebra.AbstractAlgebraicField = <com.vzome.core.algebra.AbstractAlgebraicField><any>this.mModel.getField();
            const tipBookmark: java.util.Optional<com.vzome.core.editor.Tool> = toolsModel.values().stream().filter((tool) => "tip vertex" === tool.getLabel()).findAny();
            if (!tipBookmark.isPresent())throw new com.vzome.core.commands.Command.Failure("You must have a bookmark named \"tip vertex\" for the strut endpoint.");
            const tipItems: java.util.List<com.vzome.core.construction.Construction> = tipBookmark.get().getParameters();
            const tipPoint: com.vzome.core.construction.Construction = tipItems.get(0);
            if (tipItems.size() > 1 || !(tipPoint != null && tipPoint instanceof <any>com.vzome.core.construction.Point))throw new com.vzome.core.commands.Command.Failure("The \"tip vertex\" bookmark must select a single ball.");
            const tipVertex: com.vzome.core.algebra.AlgebraicVector = (<com.vzome.core.construction.Point>tipPoint).getLocation();
            const floatingBookmark: java.util.Optional<com.vzome.core.editor.Tool> = toolsModel.values().stream().filter((tool) => "floating panels" === tool.getLabel()).findAny();
            let floatingVerticesSet: java.util.SortedSet<com.vzome.core.algebra.AlgebraicVector> = <any>(new java.util.TreeSet<any>());
            if (!floatingBookmark.isPresent())throw new com.vzome.core.commands.Command.Failure("You must have a bookmark named \"floating panels\".");
            for(let index=floatingBookmark.get().getParameters().iterator();index.hasNext();) {
                let polygon = index.next();
                {
                    if (!(polygon != null && polygon instanceof <any>com.vzome.core.construction.Polygon))throw new com.vzome.core.commands.Command.Failure("The \"floating panels\" bookmark must select only panels.");
                    {
                        let array = (<com.vzome.core.construction.Polygon>polygon).getVertices();
                        for(let index = 0; index < array.length; index++) {
                            let vertex = array[index];
                            {
                                floatingVerticesSet.add(vertex);
                            }
                        }
                    }
                }
            }
            let bottomFaceNormal: com.vzome.core.algebra.AlgebraicVector = null;
            const bottomFaceBookmark: java.util.Optional<com.vzome.core.editor.Tool> = toolsModel.values().stream().filter((tool) => "bottom face" === tool.getLabel()).findAny();
            if (bottomFaceBookmark.isPresent()){
                const bottomFaceItems: java.util.List<com.vzome.core.construction.Construction> = bottomFaceBookmark.get().getParameters();
                const bottomFacePanel: com.vzome.core.construction.Construction = bottomFaceItems.get(0);
                if (bottomFaceItems.size() > 1 || !(bottomFacePanel != null && bottomFacePanel instanceof <any>com.vzome.core.construction.Polygon))throw new com.vzome.core.commands.Command.Failure("The \"bottom face\" bookmark must select a single panel.");
                bottomFaceNormal = (<com.vzome.core.construction.Polygon>bottomFacePanel).getNormal();
            }
            let fixedVerticesSet: java.util.SortedSet<com.vzome.core.algebra.AlgebraicVector> = <any>(new java.util.TreeSet<any>());
            let orbitName: string = null;
            for(let index=this.mModel.iterator();index.hasNext();) {
                let rm = index.next();
                {
                    const man: com.vzome.core.model.Manifestation = rm.getManifestation();
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                        const panel: com.vzome.core.model.Panel = <com.vzome.core.model.Panel><any>man;
                        for(let index=panel.iterator();index.hasNext();) {
                            let vertex = index.next();
                            {
                                if (!floatingVerticesSet.contains(vertex))fixedVerticesSet.add(vertex);
                            }
                        }
                    } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                        if (orbitName != null)throw new com.vzome.core.commands.Command.Failure("The model must contain a single prototype strut.");
                        orbitName = rm.getStrutOrbit().getName();
                    }
                }
            }
            if (orbitName == null)throw new com.vzome.core.commands.Command.Failure("The model must contain a single prototype strut.");
            const sortedFixedVertexList: java.util.ArrayList<com.vzome.core.algebra.AlgebraicVector> = <any>(new java.util.ArrayList<any>(fixedVerticesSet));
            const sortedFloatingVertexList: java.util.ArrayList<com.vzome.core.algebra.AlgebraicVector> = <any>(new java.util.ArrayList<any>(floatingVerticesSet));
            fixedVerticesSet = null;
            floatingVerticesSet = null;
            this.output = new java.io.PrintWriter(writer);
            let prelude: string = super.getBoilerplate("com/vzome/core/exporters/zome-strut-prelude.scad");
            prelude = /* replaceAll */prelude.replace(new RegExp("%%ORBIT%%", 'g'),orbitName);
            this.output.println$java_lang_Object(prelude);
            this.output.println$java_lang_Object("  irrational = " + field.getCoefficients()[1] + ";");
            this.output.println$();
            this.output.println$java_lang_Object("module " + orbitName + "_strut( size, scalar=1.0, offsets=0 ) {");
            this.output.println$();
            if (bottomFaceNormal == null){
                this.output.println$java_lang_Object("  // WARNING: The vZome design contained no \"bottom face\" bookmark.");
                this.output.println$java_lang_Object("  bottom_face_normal = [ 0, 0, -1 ];");
            } else {
                const bottomFaceDirection: com.vzome.core.math.RealVector = this.mModel.renderVector(bottomFaceNormal).normalize();
                this.output.println$java_lang_Object("  bottom_face_normal = [ " + bottomFaceDirection.toString$() + " ];");
            }
            this.output.println$();
            const mmScaling: number = this.mModel.getCmScaling() * 10.0;
            const tipVertexString: string = this.mModel.renderVector(tipVertex).scale(mmScaling).toString$();
            this.output.println$java_lang_Object("  tip_vertex = [ " + tipVertexString + " ];");
            this.output.println$();
            this.output.println$java_lang_Object("  fixed_vertices = [ ");
            for(let index=sortedFixedVertexList.iterator();index.hasNext();) {
                let vertex = index.next();
                {
                    this.output.print("[ ");
                    this.output.print(this.mModel.renderVector(vertex).scale(mmScaling).toString$());
                    this.output.print(" ], ");
                }
            }
            this.output.println$java_lang_Object(" ];");
            this.output.println$java_lang_Object("  floating_vertices = [ ");
            for(let index=sortedFloatingVertexList.iterator();index.hasNext();) {
                let vertex = index.next();
                {
                    this.output.print("[ ");
                    this.output.print(this.mModel.renderVector(vertex).scale(mmScaling).toString$());
                    this.output.print(" ], ");
                }
            }
            this.output.println$java_lang_Object(" ];");
            this.output.println$java_lang_Object("  faces = [ ");
            for(let index=this.mModel.iterator();index.hasNext();) {
                let rm = index.next();
                {
                    const man: com.vzome.core.model.Manifestation = rm.getManifestation();
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                        this.output.print("[ ");
                        const panel: com.vzome.core.model.Panel = <com.vzome.core.model.Panel><any>man;
                        const stack: java.util.Stack<com.vzome.core.algebra.AlgebraicVector> = <any>(new java.util.Stack<com.vzome.core.algebra.AlgebraicVector>());
                        for(let index=panel.iterator();index.hasNext();) {
                            let vertex = index.next();
                            {
                                stack.push(vertex);
                            }
                        }
                        while((!stack.isEmpty())) {{
                            const vertex: com.vzome.core.algebra.AlgebraicVector = stack.pop();
                            let index: number = sortedFixedVertexList.indexOf(vertex);
                            if (index < 0){
                                index = sortedFixedVertexList.size() + sortedFloatingVertexList.indexOf(vertex);
                            }
                            this.output.print(index + ", ");
                        }};
                        this.output.print("], ");
                    }
                }
            }
            this.output.println$java_lang_Object(" ];");
            this.output.println$java_lang_Object("  zome_strut( tip_vertex, fixed_vertices, floating_vertices, faces, bottom_face_normal, size, scalar, offsets );");
            this.output.println$java_lang_Object("}");
            this.output.flush();
        }

        /**
         * 
         * @return {string}
         */
        public getFileExtension(): string {
            return "scad";
        }

        /**
         * 
         * @param {java.io.File} file
         * @param {java.io.Writer} writer
         * @param {number} height
         * @param {number} width
         */
        public doExport(file: java.io.File, writer: java.io.Writer, height: number, width: number) {
        }

        constructor() {
            super();
        }
    }
    OpenScadExporter["__class"] = "com.vzome.core.exporters.OpenScadExporter";
    OpenScadExporter["__interfaces"] = ["com.vzome.core.exporters.DocumentExporterIntf"];


}


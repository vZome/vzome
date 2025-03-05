/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.exporters {
    export class PartGeometryExporter extends com.vzome.core.exporters.VefExporter implements com.vzome.core.exporters.DocumentExporterIntf {
        /*private*/ selection: com.vzome.core.editor.api.Selection;

        public exportDocument(doc: com.vzome.core.exporters.DocumentIntf, file: java.io.File, writer: java.io.Writer, height: number, width: number) {
            this.mModel = doc.getRenderedModel();
            this.selection = doc.getEditorModel().getSelection();
            this.doExport(file, writer, height, width);
            this.selection = null;
            this.mModel = null;
        }

        /**
         * 
         * @param {java.io.File} directory
         * @param {java.io.Writer} writer
         * @param {number} height
         * @param {number} width
         */
        public doExport(directory: java.io.File, writer: java.io.Writer, height: number, width: number) {
            const field: com.vzome.core.algebra.AlgebraicField = this.mModel.getField();
            const exporter: com.vzome.core.model.VefModelExporter = new com.vzome.core.model.VefModelExporter(writer, field);
            for(let index=this.mModel.iterator();index.hasNext();) {
                let rm = index.next();
                {
                    exporter.exportManifestation(rm.getManifestation());
                }
            }
            exporter.finish();
            this.exportSelection(exporter);
        }

        /*private*/ exportSelection(exporter: com.vzome.core.model.VefModelExporter) {
            let tip: com.vzome.core.model.Connector = null;
            const arrayComparator: com.vzome.core.generic.ArrayComparator<com.vzome.core.algebra.AlgebraicVector> = <any>(new com.vzome.core.generic.ArrayComparator<any>());
            const panelVertices: java.util.SortedSet<com.vzome.core.algebra.AlgebraicVector[]> = <any>(new java.util.TreeSet<any>(<any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0, arg1) =>  (funcInst['compare'] ? funcInst['compare'] : funcInst) .call(funcInst, arg0, arg1)})(arrayComparator.getLengthFirstArrayComparator()))));
            const vertexArrayPanelMap: java.util.Map<com.vzome.core.algebra.AlgebraicVector[], com.vzome.core.model.Panel> = <any>(new java.util.HashMap<any, any>());
            for(let index=this.selection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                        if (tip == null){
                            tip = <com.vzome.core.model.Connector><any>man;
                        }
                    } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                        const panel: com.vzome.core.model.Panel = <com.vzome.core.model.Panel><any>man;
                        const corners: java.util.ArrayList<com.vzome.core.algebra.AlgebraicVector> = <any>(new java.util.ArrayList<any>(panel.getVertexCount()));
                        for(let index=panel.iterator();index.hasNext();) {
                            let vertex = index.next();
                            {
                                corners.add(vertex);
                            }
                        }
                        const cornerArray: com.vzome.core.algebra.AlgebraicVector[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(corners.size());
                        corners.toArray<any>(cornerArray);
                        panelVertices.add(cornerArray);
                        vertexArrayPanelMap.put(cornerArray, panel);
                    }
                }
            }
            if (tip != null){
                exporter.exportSelectedManifestation(null);
                exporter.exportSelectedManifestation(tip);
                if (!panelVertices.isEmpty()){
                    exporter.exportSelectedManifestation(null);
                    for(let index=panelVertices.iterator();index.hasNext();) {
                        let vertexArray = index.next();
                        {
                            const panel: com.vzome.core.model.Panel = vertexArrayPanelMap.get(vertexArray);
                            exporter.exportSelectedManifestation(panel);
                        }
                    }
                }
                exporter.exportSelectedManifestation(null);
            }
        }

        constructor() {
            super();
            if (this.selection === undefined) { this.selection = null; }
        }
    }
    PartGeometryExporter["__class"] = "com.vzome.core.exporters.PartGeometryExporter";
    PartGeometryExporter["__interfaces"] = ["com.vzome.core.exporters.DocumentExporterIntf"];


}


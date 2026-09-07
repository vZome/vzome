import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Selection } from "../editor/api/Selection.js";
import { DocumentExporterIntf } from "./DocumentExporterIntf.js";
import { DocumentIntf } from "./DocumentIntf.js";
import { VefExporter } from "./VefExporter.js";
import { ArrayComparator } from "../generic/ArrayComparator.js";
import { Connector } from "../model/Connector.js";
import { Panel } from "../model/Panel.js";
import { VefModelExporter } from "../model/VefModelExporter.js";
import { File } from "../../../../java/io/File.js";

export class PartGeometryExporter extends VefExporter implements DocumentExporterIntf {
    /*private*/ selection: Selection;

    public exportDocument(doc: DocumentIntf, file: File, writer: java.io.Writer, height: number, width: number) {
        this.mModel = doc.getRenderedModel();
        this.selection = doc.getEditorModel().getSelection();
        this.doExport(file, writer, height, width);
        this.selection = null;
        this.mModel = null;
    }

    /**
     * 
     * @param {File} directory
     * @param {java.io.Writer} writer
     * @param {number} height
     * @param {number} width
     */
    public doExport(directory: File, writer: java.io.Writer, height: number, width: number) {
        const field: AlgebraicField = this.mModel.getField();
        const exporter: VefModelExporter = new VefModelExporter(writer, field);
        for(let index=this.mModel.iterator();index.hasNext();) {
            let rm = index.next();
            {
                exporter.exportManifestation(rm.getManifestation());
            }
        }
        exporter.finish();
        this.exportSelection(exporter);
    }

    /*private*/ exportSelection(exporter: VefModelExporter) {
        let tip: Connector = null;
        const arrayComparator: ArrayComparator<AlgebraicVector> = <any>(new ArrayComparator<any>());
        const panelVertices: java.util.SortedSet<AlgebraicVector[]> = <any>(new java.util.TreeSet<any>(<any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0, arg1) =>  (funcInst['compare'] ? funcInst['compare'] : funcInst) .call(funcInst, arg0, arg1)})(arrayComparator.getLengthFirstArrayComparator()))));
        const vertexArrayPanelMap: java.util.Map<AlgebraicVector[], Panel> = <any>(new java.util.HashMap<any, any>());
        for(let index=this.selection.iterator();index.hasNext();) {
            let man = index.next();
            {
                if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                    if (tip == null){
                        tip = <Connector><any>man;
                    }
                } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                    const panel: Panel = <Panel><any>man;
                    const corners: java.util.ArrayList<AlgebraicVector> = <any>(new java.util.ArrayList<any>(panel.getVertexCount()));
                    for(let index=panel.iterator();index.hasNext();) {
                        let vertex = index.next();
                        {
                            corners.add(vertex);
                        }
                    }
                    const cornerArray: AlgebraicVector[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(corners.size());
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
                        const panel: Panel = vertexArrayPanelMap.get(vertexArray);
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

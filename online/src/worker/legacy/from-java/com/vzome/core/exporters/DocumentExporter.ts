import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { DocumentExporterIntf } from "./DocumentExporterIntf.js";
import { DocumentIntf } from "./DocumentIntf.js";
import { GeometryExporter } from "./GeometryExporter.js";
import { CameraIntf } from "../viewing/CameraIntf.js";
import { Lights } from "../viewing/Lights.js";
import { File } from "../../../../java/io/File.js";

export abstract class DocumentExporter extends GeometryExporter implements DocumentExporterIntf {
    mLights: Lights;

    mScene: CameraIntf;

    /**
     * Subclasses can override this if they need to export history, the lesson model, or the selection.
     * @param {*} doc
     * @param {File} file
     * @param {java.io.Writer} writer
     * @param {number} height
     * @param {number} width
     */
    public exportDocument(doc: DocumentIntf, file: File, writer: java.io.Writer, height: number, width: number) {
        this.mScene = doc.getCameraModel();
        this.mLights = doc.getSceneLighting();
        this.exportGeometry(doc.getRenderedModel(), file, writer, height, width);
        this.mScene = null;
        this.mLights = null;
    }

    constructor() {
        super();
        if (this.mLights === undefined) { this.mLights = null; }
        if (this.mScene === undefined) { this.mScene = null; }
    }
}
DocumentExporter["__class"] = "com.vzome.core.exporters.DocumentExporter";
DocumentExporter["__interfaces"] = ["com.vzome.core.exporters.DocumentExporterIntf"];

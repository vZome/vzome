/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.exporters {
    export abstract class DocumentExporter extends com.vzome.core.exporters.GeometryExporter implements com.vzome.core.exporters.DocumentExporterIntf {
        mLights: com.vzome.core.viewing.Lights;

        mScene: com.vzome.core.viewing.CameraIntf;

        /**
         * Subclasses can override this if they need to export history, the lesson model, or the selection.
         * @param {*} doc
         * @param {java.io.File} file
         * @param {java.io.Writer} writer
         * @param {number} height
         * @param {number} width
         */
        public exportDocument(doc: com.vzome.core.exporters.DocumentIntf, file: java.io.File, writer: java.io.Writer, height: number, width: number) {
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


}


/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.exporters {
    export interface DocumentExporterIntf {
        /**
         * Subclasses can override this if they need to export history, the lesson model, or the selection.
         * @param {*} doc
         * @param {java.io.File} file
         * @param {java.io.Writer} writer
         * @param {number} height
         * @param {number} width
         */
        exportDocument(doc: com.vzome.core.exporters.DocumentIntf, file: java.io.File, writer: java.io.Writer, height: number, width: number);
    }
}


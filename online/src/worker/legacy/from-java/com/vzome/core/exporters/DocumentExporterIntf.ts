import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { DocumentIntf } from "./DocumentIntf.js";
import { File } from "../../../../java/io/File.js";

export interface DocumentExporterIntf {
    /**
     * Subclasses can override this if they need to export history, the lesson model, or the selection.
     * @param {*} doc
     * @param {File} file
     * @param {java.io.Writer} writer
     * @param {number} height
     * @param {number} width
     */
    exportDocument(doc: DocumentIntf, file: File, writer: java.io.Writer, height: number, width: number);
}

import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Colors } from "../render/Colors.js";
import { RenderedModel } from "../render/RenderedModel.js";
import { ResourceLoader } from "../../xml/ResourceLoader.js";
import { File } from "../../../../java/io/File.js";
import { PrintWriter } from "../../../../java/io/PrintWriter.js";

export abstract class GeometryExporter {
    output: PrintWriter;

    mColors: Colors;

    mModel: RenderedModel;

    public constructor() {
        if (this.output === undefined) { this.output = null; }
        if (this.mColors === undefined) { this.mColors = null; }
        if (this.mModel === undefined) { this.mModel = null; }
    }

    /**
     * This is what most subclasses override.
     * @param {File} file
     * @param {java.io.Writer} writer
     * @param {number} height
     * @param {number} width
     */
    public abstract doExport(file: File, writer: java.io.Writer, height: number, width: number);

    public abstract getFileExtension(): string;

    public getContentType(): string {
        return "text/plain";
    }

    /**
     * Subclasses can override this if they don't rely on Manifestations and therefore can operate on article pages
     * See the comments below DocumentModel.getNaiveExporter() for a more complete explanation.
     * @return {boolean}
     */
    public needsManifestations(): boolean {
        return true;
    }

    getBoilerplate(resourcePath: string): string {
        return ResourceLoader.loadStringResource(resourcePath);
    }

    public exportGeometry(model: RenderedModel, file: File, writer: java.io.Writer, height: number, width: number) {
        this.mModel = model;
        this.doExport(file, writer, height, width);
        this.mModel = null;
    }
}
GeometryExporter["__class"] = "com.vzome.core.exporters.GeometryExporter";

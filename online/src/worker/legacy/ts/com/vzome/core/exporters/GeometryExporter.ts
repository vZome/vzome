/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.exporters {
    export abstract class GeometryExporter implements com.vzome.core.render.RealZomeScaling {
        output: java.io.PrintWriter;

        mColors: com.vzome.core.render.Colors;

        mModel: com.vzome.core.render.RenderedModel;

        public constructor() {
            if (this.output === undefined) { this.output = null; }
            if (this.mColors === undefined) { this.mColors = null; }
            if (this.mModel === undefined) { this.mModel = null; }
        }

        /**
         * This is what most subclasses override.
         * @param {java.io.File} file
         * @param {java.io.Writer} writer
         * @param {number} height
         * @param {number} width
         */
        public abstract doExport(file: java.io.File, writer: java.io.Writer, height: number, width: number);

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
            return com.vzome.xml.ResourceLoader.loadStringResource(resourcePath);
        }

        public exportGeometry(model: com.vzome.core.render.RenderedModel, file: java.io.File, writer: java.io.Writer, height: number, width: number) {
            this.mModel = model;
            this.doExport(file, writer, height, width);
            this.mModel = null;
        }
    }
    GeometryExporter["__class"] = "com.vzome.core.exporters.GeometryExporter";
    GeometryExporter["__interfaces"] = ["com.vzome.core.render.RealZomeScaling"];


}


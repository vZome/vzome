/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.exporters {
    export class SegExporter extends com.vzome.core.exporters.GeometryExporter {
        /**
         * 
         * @param {java.io.File} directory
         * @param {java.io.Writer} writer
         * @param {number} height
         * @param {number} width
         */
        public doExport(directory: java.io.File, writer: java.io.Writer, height: number, width: number) {
            this.field = this.mModel.getField();
            this.vertices = new java.lang.StringBuffer();
            this.struts = new java.lang.StringBuffer();
            if (this.format != null && this.format instanceof <any>java.text.DecimalFormat){
                (<java.text.DecimalFormat>this.format).applyPattern("0.0000");
            }
            for(let index=this.mModel.iterator();index.hasNext();) {
                let rm = index.next();
                {
                    const man: com.vzome.core.model.Manifestation = rm.getManifestation();
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                        const strut: com.vzome.core.model.Strut = <com.vzome.core.model.Strut><any>man;
                        this.struts.append("s ");
                        this.struts.append(this.getVertexIndex(strut.getLocation()));
                        this.struts.append(" ");
                        this.struts.append(this.getVertexIndex(strut.getEnd()));
                        this.struts.append("\n");
                    }
                }
            }
            writer.append(this.vertices.toString());
            writer.append(this.struts.toString());
            writer.close();
        }

        /**
         * 
         * @return {string}
         */
        public getFileExtension(): string {
            return "seg";
        }

        /*private*/ vertexData: java.util.Map<com.vzome.core.algebra.AlgebraicVector, number>;

        /*private*/ vertices: java.lang.StringBuffer;

        /*private*/ struts: java.lang.StringBuffer;

        field: com.vzome.core.algebra.AlgebraicField;

        /*private*/ format: java.text.NumberFormat;

        getVertexIndex(vertexVector: com.vzome.core.algebra.AlgebraicVector): number {
            let val: number = this.vertexData.get(vertexVector);
            if (val == null){
                const key: com.vzome.core.algebra.AlgebraicVector = vertexVector;
                const index: number = this.vertexData.size();
                val = index;
                this.vertexData.put(key, val);
                this.vertices.append("v ");
                const vertex: com.vzome.core.math.RealVector = this.mModel.renderVector(vertexVector);
                this.vertices.append(this.format.format(vertex.x) + " ");
                this.vertices.append(this.format.format(vertex.y) + " ");
                this.vertices.append(this.format.format(vertex.z) + " ");
                this.vertices.append("\n");
            }
            return val;
        }

        constructor() {
            super();
            this.vertexData = <any>(new java.util.HashMap<any, any>());
            if (this.vertices === undefined) { this.vertices = null; }
            if (this.struts === undefined) { this.struts = null; }
            if (this.field === undefined) { this.field = null; }
            this.format = java.text.NumberFormat.getNumberInstance(java.util.Locale.US);
        }
    }
    SegExporter["__class"] = "com.vzome.core.exporters.SegExporter";

}


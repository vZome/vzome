/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.model {
    export class VefModelExporter extends com.vzome.core.algebra.VefVectorExporter implements com.vzome.core.model.Exporter {
        public constructor(writer: java.io.Writer, field: com.vzome.core.algebra.AlgebraicField, scale: com.vzome.core.algebra.AlgebraicNumber = null, withOffset: boolean = false) {
            super(writer, field, scale, withOffset);
        }

        /**
         * 
         * @param {*} man
         */
        public exportManifestation(man: com.vzome.core.model.Manifestation) {
            if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                this.exportPoint(man.getLocation());
            } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                const strut: com.vzome.core.model.Strut = <com.vzome.core.model.Strut><any>man;
                this.exportSegment(strut.getLocation(), strut.getEnd());
            } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                const panel: com.vzome.core.model.Panel = <com.vzome.core.model.Panel><any>man;
                const corners: java.util.ArrayList<com.vzome.core.algebra.AlgebraicVector> = <any>(new java.util.ArrayList<any>(panel.getVertexCount()));
                for(let index=panel.iterator();index.hasNext();) {
                    let vertex = index.next();
                    {
                        corners.add(vertex);
                    }
                }
                this.exportPolygon(corners);
            }
        }

        /**
         * This is used only for vZome part geometry export
         * @param {*} man
         */
        public exportSelectedManifestation(man: com.vzome.core.model.Manifestation) {
            if (man == null){
                this.output.println$();
                this.output.flush();
            } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                if (this.strTip != null){
                    this.output.print(this.strTip);
                    this.strTip = null;
                }
                const loc: com.vzome.core.algebra.AlgebraicVector = man.getLocation();
                this.output.println$java_lang_Object(" " + this.sortedVertexList.indexOf(loc));
            } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                if (this.strMiddle != null){
                    this.output.println$java_lang_Object(this.strMiddle);
                    this.strMiddle = null;
                }
                const panel: com.vzome.core.model.Panel = <com.vzome.core.model.Panel><any>man;
                for(let index=panel.iterator();index.hasNext();) {
                    let corner = index.next();
                    {
                        this.output.print(this.sortedVertexList.indexOf(corner) + " ");
                    }
                }
                this.output.println$();
            }
        }

        /**
         * 
         */
        public finish() {
            super.finishExport();
        }
    }
    VefModelExporter["__class"] = "com.vzome.core.model.VefModelExporter";
    VefModelExporter["__interfaces"] = ["com.vzome.core.model.Exporter"];


}


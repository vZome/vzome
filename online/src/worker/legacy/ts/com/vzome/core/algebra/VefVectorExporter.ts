/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.algebra {
    export class VefVectorExporter {
        output: java.io.PrintWriter;

        field: com.vzome.core.algebra.AlgebraicField;

        sortedVertexList: java.util.ArrayList<com.vzome.core.algebra.AlgebraicVector>;

        /*private*/ vertices: java.util.SortedSet<com.vzome.core.algebra.AlgebraicVector>;

        /*private*/ ballLocations: java.util.SortedSet<com.vzome.core.algebra.AlgebraicVector>;

        strutEnds: java.util.SortedSet<com.vzome.core.algebra.AlgebraicVector[]>;

        panelVertices: java.util.SortedSet<com.vzome.core.algebra.AlgebraicVector[]>;

        scale: com.vzome.core.algebra.AlgebraicNumber;

        includeOffset: boolean;

        exportedOffset: com.vzome.core.algebra.AlgebraicVector;

        strTip: string;

        public constructor(writer?: any, field?: any, scale?: any, withOffset?: any) {
            if (((writer != null && writer instanceof <any>java.io.Writer) || writer === null) && ((field != null && (field.constructor != null && field.constructor["__interfaces"] != null && field.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicField") >= 0)) || field === null) && ((scale != null && (scale.constructor != null && scale.constructor["__interfaces"] != null && scale.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || scale === null) && ((typeof withOffset === 'boolean') || withOffset === null)) {
                let __args = arguments;
                if (this.output === undefined) { this.output = null; } 
                if (this.field === undefined) { this.field = null; } 
                if (this.strutEnds === undefined) { this.strutEnds = null; } 
                if (this.panelVertices === undefined) { this.panelVertices = null; } 
                if (this.scale === undefined) { this.scale = null; } 
                if (this.includeOffset === undefined) { this.includeOffset = false; } 
                this.sortedVertexList = null;
                this.vertices = <any>(new java.util.TreeSet<any>());
                this.ballLocations = <any>(new java.util.TreeSet<any>());
                this.exportedOffset = null;
                this.strTip = "tip";
                this.strMiddle = "middle";
                this.includeOffset = withOffset;
                this.scale = scale;
                this.output = new java.io.PrintWriter(writer);
                this.field = field;
                const arrayComparator: com.vzome.core.generic.ArrayComparator<com.vzome.core.algebra.AlgebraicVector> = <any>(new com.vzome.core.generic.ArrayComparator<any>());
                this.strutEnds = <any>(new java.util.TreeSet<any>(<any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0, arg1) =>  (funcInst['compare'] ? funcInst['compare'] : funcInst) .call(funcInst, arg0, arg1)})(arrayComparator.getContentFirstArrayComparator()))));
                this.panelVertices = <any>(new java.util.TreeSet<any>(<any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0, arg1) =>  (funcInst['compare'] ? funcInst['compare'] : funcInst) .call(funcInst, arg0, arg1)})(arrayComparator.getLengthFirstArrayComparator()))));
            } else if (((writer != null && writer instanceof <any>java.io.Writer) || writer === null) && ((field != null && (field.constructor != null && field.constructor["__interfaces"] != null && field.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicField") >= 0)) || field === null) && scale === undefined && withOffset === undefined) {
                let __args = arguments;
                {
                    let __args = arguments;
                    let scale: any = null;
                    let withOffset: any = false;
                    if (this.output === undefined) { this.output = null; } 
                    if (this.field === undefined) { this.field = null; } 
                    if (this.strutEnds === undefined) { this.strutEnds = null; } 
                    if (this.panelVertices === undefined) { this.panelVertices = null; } 
                    if (this.scale === undefined) { this.scale = null; } 
                    if (this.includeOffset === undefined) { this.includeOffset = false; } 
                    this.sortedVertexList = null;
                    this.vertices = <any>(new java.util.TreeSet<any>());
                    this.ballLocations = <any>(new java.util.TreeSet<any>());
                    this.exportedOffset = null;
                    this.strTip = "tip";
                    this.strMiddle = "middle";
                    this.includeOffset = withOffset;
                    this.scale = scale;
                    this.output = new java.io.PrintWriter(writer);
                    this.field = field;
                    const arrayComparator: com.vzome.core.generic.ArrayComparator<com.vzome.core.algebra.AlgebraicVector> = <any>(new com.vzome.core.generic.ArrayComparator<any>());
                    this.strutEnds = <any>(new java.util.TreeSet<any>(<any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0, arg1) =>  (funcInst['compare'] ? funcInst['compare'] : funcInst) .call(funcInst, arg0, arg1)})(arrayComparator.getContentFirstArrayComparator()))));
                    this.panelVertices = <any>(new java.util.TreeSet<any>(<any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0, arg1) =>  (funcInst['compare'] ? funcInst['compare'] : funcInst) .call(funcInst, arg0, arg1)})(arrayComparator.getLengthFirstArrayComparator()))));
                }
            } else throw new Error('invalid overload');
        }

        public exportPoint(pt: com.vzome.core.algebra.AlgebraicVector) {
            this.vertices.add(pt);
            this.ballLocations.add(pt);
            if (this.includeOffset){
                this.exportedOffset = pt;
            }
        }

        public exportSegment(start: com.vzome.core.algebra.AlgebraicVector, end: com.vzome.core.algebra.AlgebraicVector) {
            const ends: com.vzome.core.algebra.AlgebraicVector[] = [start, end];
            this.vertices.add(ends[0]);
            this.vertices.add(ends[1]);
            this.strutEnds.add(ends);
        }

        public exportPolygon(corners: java.util.List<com.vzome.core.algebra.AlgebraicVector>) {
            this.vertices.addAll(corners);
            const cornerArray: com.vzome.core.algebra.AlgebraicVector[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(corners.size());
            corners.toArray<any>(cornerArray);
            this.panelVertices.add(cornerArray);
        }

        strMiddle: string;

        /**
         * @param {java.lang.StringBuffer} buffer = Don't assume that buffer starts out empty. Results will be appended.
         * @param {com.vzome.core.algebra.AlgebraicVector} vector = Value to be converted to a zero-padded 4D String which will be
         * prefixed and/or padded with field specific zeroes
         * depending on the number of dimensions in vector as follows:
         * 1D : 0 X 0 0
         * 2D : 0 X Y 0
         * 3D : 0 X Y Z
         * 4D : W X Y Z
         * @param {*} scale
         */
        public static appendVector(buffer: java.lang.StringBuffer, vector: com.vzome.core.algebra.AlgebraicVector, scale: com.vzome.core.algebra.AlgebraicNumber) {
            const zeroString: string = vector.getField().zero().toString(com.vzome.core.algebra.AlgebraicField.VEF_FORMAT);
            const dims: number = vector.dimension();
            if (dims < 4){
                buffer.append(zeroString);
                buffer.append(" ");
            }
            if (scale != null)vector = vector.scale(scale);
            vector.getVectorExpression$java_lang_StringBuffer$int(buffer, com.vzome.core.algebra.AlgebraicField.VEF_FORMAT);
            for(let d: number = dims + 1; d < 4; d++) {{
                buffer.append(" ");
                buffer.append(zeroString);
            };}
        }

        public static exportPolyhedron(polyhedron: com.vzome.core.math.Polyhedron): string {
            const out: java.io.StringWriter = new java.io.StringWriter();
            const exporter: VefVectorExporter = new VefVectorExporter(out, polyhedron.getField());
            const vertexList: java.util.List<com.vzome.core.algebra.AlgebraicVector> = polyhedron.getVertexList();
            for(let index=polyhedron.getFaceSet().iterator();index.hasNext();) {
                let face = index.next();
                {
                    const vertices: java.util.List<com.vzome.core.algebra.AlgebraicVector> = <any>(new java.util.ArrayList<any>(face.size()));
                    for(let i: number = 0; i < face.size(); i++) {{
                        const vertexIndex: number = face.getVertex(i);
                        vertices.add(vertexList.get(vertexIndex));
                    };}
                    exporter.exportPolygon(vertices);
                }
            }
            exporter.finishExport();
            return out.toString();
        }

        public finishExport() {
            this.sortedVertexList = <any>(new java.util.ArrayList<any>(this.vertices));
            this.vertices = null;
            const version: number = (this.exportedOffset == null) ? com.vzome.core.math.VefParser.VERSION_EXPLICIT_BALLS : com.vzome.core.math.VefParser.VERSION_EXPLICIT_OFFSET;
            this.output.println$java_lang_Object("vZome VEF " + version + " field " + this.field.getName());
            if (this.exportedOffset != null){
                const buf: java.lang.StringBuffer = new java.lang.StringBuffer();
                buf.append("\noffset ");
                VefVectorExporter.appendVector(buf, this.exportedOffset.negate(), null);
                buf.append("\n");
                this.output.println$java_lang_Object(buf.toString());
            }
            this.output.println$java_lang_Object("\n" + this.sortedVertexList.size());
            {
                const buf: java.lang.StringBuffer = new java.lang.StringBuffer();
                for(let index=this.sortedVertexList.iterator();index.hasNext();) {
                    let vector = index.next();
                    {
                        VefVectorExporter.appendVector(buf, vector, this.scale);
                        buf.append("\n");
                    }
                }
                buf.append("\n");
                this.output.println$java_lang_Object(buf.toString());
            };
            this.output.println$java_lang_Object("\n" + this.strutEnds.size());
            for(let index=this.strutEnds.iterator();index.hasNext();) {
                let ends = index.next();
                {
                    this.output.print(this.sortedVertexList.indexOf(ends[0]) + " ");
                    this.output.println$java_lang_Object(this.sortedVertexList.indexOf(ends[1]));
                }
            }
            this.output.println$java_lang_Object("\n");
            this.output.println$java_lang_Object("\n" + this.panelVertices.size());
            for(let index=this.panelVertices.iterator();index.hasNext();) {
                let corners = index.next();
                {
                    this.output.print(corners.length + "  ");
                    for(let index = 0; index < corners.length; index++) {
                        let corner = corners[index];
                        {
                            this.output.print(this.sortedVertexList.indexOf(corner) + " ");
                        }
                    }
                    this.output.println$();
                }
            }
            this.output.println$java_lang_Object("\n");
            this.output.println$java_lang_Object("\n" + this.ballLocations.size());
            let i: number = 0;
            for(let index=this.ballLocations.iterator();index.hasNext();) {
                let ball = index.next();
                {
                    this.output.print(this.sortedVertexList.indexOf(ball) + " ");
                    if (++i % 10 === 0){
                        this.output.println$();
                    }
                }
            }
            this.output.println$java_lang_Object("\n");
            this.output.flush();
        }
    }
    VefVectorExporter["__class"] = "com.vzome.core.algebra.VefVectorExporter";

}


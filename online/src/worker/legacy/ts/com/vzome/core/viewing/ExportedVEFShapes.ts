/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.viewing {
    /**
     * @author vorth
     * @param {java.io.File} prefsFolder
     * @param {string} pkgName
     * @param {string} name
     * @param {string} alias
     * @param {*} symm
     * @param {com.vzome.core.viewing.AbstractShapes} fallback
     * @param {boolean} isSnub
     * @class
     * @extends com.vzome.core.viewing.AbstractShapes
     */
    export class ExportedVEFShapes extends com.vzome.core.viewing.AbstractShapes {
        static LOGGER: java.util.logging.Logger; public static LOGGER_$LI$(): java.util.logging.Logger { if (ExportedVEFShapes.LOGGER == null) { ExportedVEFShapes.LOGGER = java.util.logging.Logger.getLogger("com.vzome.core.viewing.shapes"); }  return ExportedVEFShapes.LOGGER; }

        public static MODEL_PREFIX: string = "com/vzome/core/parts/";

        static NODE_MODEL: string = "connector";

        /*private*/ fallback: com.vzome.core.viewing.AbstractShapes;

        /*private*/ colors: java.util.Properties;

        /*private*/ isSnub: boolean;

        public static injectShapeVEF(key: string, vef: string) {
        }

        public constructor(prefsFolder?: any, pkgName?: any, name?: any, alias?: any, symm?: any, fallback?: any, isSnub?: any) {
            if (((prefsFolder != null && prefsFolder instanceof <any>java.io.File) || prefsFolder === null) && ((typeof pkgName === 'string') || pkgName === null) && ((typeof name === 'string') || name === null) && ((typeof alias === 'string') || alias === null) && ((symm != null && (symm.constructor != null && symm.constructor["__interfaces"] != null && symm.constructor["__interfaces"].indexOf("com.vzome.core.math.symmetry.Symmetry") >= 0)) || symm === null) && ((fallback != null && fallback instanceof <any>com.vzome.core.viewing.AbstractShapes) || fallback === null) && ((typeof isSnub === 'boolean') || isSnub === null)) {
                let __args = arguments;
                super(pkgName, name, alias, symm);
                if (this.fallback === undefined) { this.fallback = null; } 
                if (this.isSnub === undefined) { this.isSnub = false; } 
                this.colors = new java.util.Properties();
                this.fallback = fallback;
                this.isSnub = isSnub;
                const colorProps: string = ExportedVEFShapes.MODEL_PREFIX + pkgName + "/colors.properties";
                const resource: string = com.vzome.xml.ResourceLoader.loadStringResource(colorProps);
                if (resource != null)try {
                    const inputStream: java.io.InputStream = new java.io.ByteArrayInputStream(/* getBytes */(resource).split('').map(s => s.charCodeAt(0)));
                    this.colors.load(inputStream);
                } catch(ioe) {
                    if (ExportedVEFShapes.LOGGER_$LI$().isLoggable(java.util.logging.Level.FINE))ExportedVEFShapes.LOGGER_$LI$().fine("problem with shape color properties: " + colorProps);
                }
            } else if (((prefsFolder != null && prefsFolder instanceof <any>java.io.File) || prefsFolder === null) && ((typeof pkgName === 'string') || pkgName === null) && ((typeof name === 'string') || name === null) && ((typeof alias === 'string') || alias === null) && ((symm != null && (symm.constructor != null && symm.constructor["__interfaces"] != null && symm.constructor["__interfaces"].indexOf("com.vzome.core.math.symmetry.Symmetry") >= 0)) || symm === null) && ((fallback != null && fallback instanceof <any>com.vzome.core.viewing.AbstractShapes) || fallback === null) && isSnub === undefined) {
                let __args = arguments;
                {
                    let __args = arguments;
                    let isSnub: any = false;
                    super(pkgName, name, alias, symm);
                    if (this.fallback === undefined) { this.fallback = null; } 
                    if (this.isSnub === undefined) { this.isSnub = false; } 
                    this.colors = new java.util.Properties();
                    this.fallback = fallback;
                    this.isSnub = isSnub;
                    const colorProps: string = ExportedVEFShapes.MODEL_PREFIX + pkgName + "/colors.properties";
                    const resource: string = com.vzome.xml.ResourceLoader.loadStringResource(colorProps);
                    if (resource != null)try {
                        const inputStream: java.io.InputStream = new java.io.ByteArrayInputStream(/* getBytes */(resource).split('').map(s => s.charCodeAt(0)));
                        this.colors.load(inputStream);
                    } catch(ioe) {
                        if (ExportedVEFShapes.LOGGER_$LI$().isLoggable(java.util.logging.Level.FINE))ExportedVEFShapes.LOGGER_$LI$().fine("problem with shape color properties: " + colorProps);
                    }
                }
            } else if (((prefsFolder != null && prefsFolder instanceof <any>java.io.File) || prefsFolder === null) && ((typeof pkgName === 'string') || pkgName === null) && ((typeof name === 'string') || name === null) && ((typeof alias === 'string') || alias === null) && ((symm != null && (symm.constructor != null && symm.constructor["__interfaces"] != null && symm.constructor["__interfaces"].indexOf("com.vzome.core.math.symmetry.Symmetry") >= 0)) || symm === null) && fallback === undefined && isSnub === undefined) {
                let __args = arguments;
                {
                    let __args = arguments;
                    let fallback: any = new com.vzome.core.viewing.OctahedralShapes(__args[1], __args[2], __args[4]);
                    {
                        let __args = arguments;
                        let isSnub: any = false;
                        super(pkgName, name, alias, symm);
                        if (this.fallback === undefined) { this.fallback = null; } 
                        if (this.isSnub === undefined) { this.isSnub = false; } 
                        this.colors = new java.util.Properties();
                        this.fallback = fallback;
                        this.isSnub = isSnub;
                        const colorProps: string = ExportedVEFShapes.MODEL_PREFIX + pkgName + "/colors.properties";
                        const resource: string = com.vzome.xml.ResourceLoader.loadStringResource(colorProps);
                        if (resource != null)try {
                            const inputStream: java.io.InputStream = new java.io.ByteArrayInputStream(/* getBytes */(resource).split('').map(s => s.charCodeAt(0)));
                            this.colors.load(inputStream);
                        } catch(ioe) {
                            if (ExportedVEFShapes.LOGGER_$LI$().isLoggable(java.util.logging.Level.FINE))ExportedVEFShapes.LOGGER_$LI$().fine("problem with shape color properties: " + colorProps);
                        }
                    }
                }
            } else if (((prefsFolder != null && prefsFolder instanceof <any>java.io.File) || prefsFolder === null) && ((typeof pkgName === 'string') || pkgName === null) && ((typeof name === 'string') || name === null) && ((alias != null && (alias.constructor != null && alias.constructor["__interfaces"] != null && alias.constructor["__interfaces"].indexOf("com.vzome.core.math.symmetry.Symmetry") >= 0)) || alias === null) && ((symm != null && symm instanceof <any>com.vzome.core.viewing.AbstractShapes) || symm === null) && fallback === undefined && isSnub === undefined) {
                let __args = arguments;
                let symm: any = __args[3];
                let fallback: any = __args[4];
                {
                    let __args = arguments;
                    let alias: any = null;
                    {
                        let __args = arguments;
                        let isSnub: any = false;
                        super(pkgName, name, alias, symm);
                        if (this.fallback === undefined) { this.fallback = null; } 
                        if (this.isSnub === undefined) { this.isSnub = false; } 
                        this.colors = new java.util.Properties();
                        this.fallback = fallback;
                        this.isSnub = isSnub;
                        const colorProps: string = ExportedVEFShapes.MODEL_PREFIX + pkgName + "/colors.properties";
                        const resource: string = com.vzome.xml.ResourceLoader.loadStringResource(colorProps);
                        if (resource != null)try {
                            const inputStream: java.io.InputStream = new java.io.ByteArrayInputStream(/* getBytes */(resource).split('').map(s => s.charCodeAt(0)));
                            this.colors.load(inputStream);
                        } catch(ioe) {
                            if (ExportedVEFShapes.LOGGER_$LI$().isLoggable(java.util.logging.Level.FINE))ExportedVEFShapes.LOGGER_$LI$().fine("problem with shape color properties: " + colorProps);
                        }
                    }
                }
            } else if (((prefsFolder != null && prefsFolder instanceof <any>java.io.File) || prefsFolder === null) && ((typeof pkgName === 'string') || pkgName === null) && ((typeof name === 'string') || name === null) && ((alias != null && (alias.constructor != null && alias.constructor["__interfaces"] != null && alias.constructor["__interfaces"].indexOf("com.vzome.core.math.symmetry.Symmetry") >= 0)) || alias === null) && ((typeof symm === 'boolean') || symm === null) && fallback === undefined && isSnub === undefined) {
                let __args = arguments;
                let symm: any = __args[3];
                let useZomic: any = __args[4];
                {
                    let __args = arguments;
                    let alias: any = null;
                    let fallback: any = new com.vzome.core.viewing.OctahedralShapes(__args[1], __args[2], __args[4]);
                    {
                        let __args = arguments;
                        let isSnub: any = false;
                        super(pkgName, name, alias, symm);
                        if (this.fallback === undefined) { this.fallback = null; } 
                        if (this.isSnub === undefined) { this.isSnub = false; } 
                        this.colors = new java.util.Properties();
                        this.fallback = fallback;
                        this.isSnub = isSnub;
                        const colorProps: string = ExportedVEFShapes.MODEL_PREFIX + pkgName + "/colors.properties";
                        const resource: string = com.vzome.xml.ResourceLoader.loadStringResource(colorProps);
                        if (resource != null)try {
                            const inputStream: java.io.InputStream = new java.io.ByteArrayInputStream(/* getBytes */(resource).split('').map(s => s.charCodeAt(0)));
                            this.colors.load(inputStream);
                        } catch(ioe) {
                            if (ExportedVEFShapes.LOGGER_$LI$().isLoggable(java.util.logging.Level.FINE))ExportedVEFShapes.LOGGER_$LI$().fine("problem with shape color properties: " + colorProps);
                        }
                    }
                }
            } else if (((prefsFolder != null && prefsFolder instanceof <any>java.io.File) || prefsFolder === null) && ((typeof pkgName === 'string') || pkgName === null) && ((typeof name === 'string') || name === null) && ((alias != null && (alias.constructor != null && alias.constructor["__interfaces"] != null && alias.constructor["__interfaces"].indexOf("com.vzome.core.math.symmetry.Symmetry") >= 0)) || alias === null) && symm === undefined && fallback === undefined && isSnub === undefined) {
                let __args = arguments;
                let symm: any = __args[3];
                {
                    let __args = arguments;
                    let alias: any = null;
                    {
                        let __args = arguments;
                        let fallback: any = new com.vzome.core.viewing.OctahedralShapes(__args[1], __args[2], __args[4]);
                        {
                            let __args = arguments;
                            let isSnub: any = false;
                            super(pkgName, name, alias, symm);
                            if (this.fallback === undefined) { this.fallback = null; } 
                            if (this.isSnub === undefined) { this.isSnub = false; } 
                            this.colors = new java.util.Properties();
                            this.fallback = fallback;
                            this.isSnub = isSnub;
                            const colorProps: string = ExportedVEFShapes.MODEL_PREFIX + pkgName + "/colors.properties";
                            const resource: string = com.vzome.xml.ResourceLoader.loadStringResource(colorProps);
                            if (resource != null)try {
                                const inputStream: java.io.InputStream = new java.io.ByteArrayInputStream(/* getBytes */(resource).split('').map(s => s.charCodeAt(0)));
                                this.colors.load(inputStream);
                            } catch(ioe) {
                                if (ExportedVEFShapes.LOGGER_$LI$().isLoggable(java.util.logging.Level.FINE))ExportedVEFShapes.LOGGER_$LI$().fine("problem with shape color properties: " + colorProps);
                            }
                        }
                    }
                }
            } else throw new Error('invalid overload');
        }

        /**
         * 
         * @param {string} pkgName
         * @return {com.vzome.core.math.Polyhedron}
         */
        buildConnectorShape(pkgName: string): com.vzome.core.math.Polyhedron {
            const vefData: string = this.loadVefData(ExportedVEFShapes.NODE_MODEL);
            if (vefData != null){
                const parser: ExportedVEFShapes.VefToShape = new ExportedVEFShapes.VefToShape(this);
                parser.invertSnubBall = this.isSnub;
                parser.parseVEF(vefData, this.mSymmetry.getField());
                return parser.getConnectorPolyhedron();
            }
            const logLevel: java.util.logging.Level = java.util.logging.Level.FINE;
            if (ExportedVEFShapes.LOGGER_$LI$().isLoggable(logLevel)){
                ExportedVEFShapes.LOGGER_$LI$().log(logLevel, this.toString() + " has no VEF data for " + ExportedVEFShapes.NODE_MODEL + " at " + pkgName);
            }
            if (this.fallback != null){
                if (ExportedVEFShapes.LOGGER_$LI$().isLoggable(logLevel)){
                    ExportedVEFShapes.LOGGER_$LI$().log(logLevel, "\t" + ExportedVEFShapes.NODE_MODEL + " --> fallback to " + this.fallback.toString());
                }
                return this.fallback.buildConnectorShape(pkgName);
            }
            throw new java.lang.IllegalStateException("missing connector shape: " + pkgName);
        }

        /**
         * 
         * @param {com.vzome.core.math.symmetry.Direction} dir
         * @return {*}
         */
        createStrutGeometry(dir: com.vzome.core.math.symmetry.Direction): com.vzome.core.parts.StrutGeometry {
            if (!dir.isAutomatic()){
                const vefData: string = this.loadVefData(dir.getName());
                if (vefData != null){
                    const parser: ExportedVEFShapes.VefToShape = new ExportedVEFShapes.VefToShape(this);
                    parser.parseVEF(vefData, this.mSymmetry.getField());
                    return parser.getStrutGeometry(dir.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, 0).normal());
                }
                const logLevel: java.util.logging.Level = java.util.logging.Level.FINER;
                if (ExportedVEFShapes.LOGGER_$LI$().isLoggable(logLevel)){
                    ExportedVEFShapes.LOGGER_$LI$().log(logLevel, this.toString() + " has no VEF data for strut: " + dir.getName());
                }
                if (this.fallback != null){
                    if (ExportedVEFShapes.LOGGER_$LI$().isLoggable(logLevel)){
                        ExportedVEFShapes.LOGGER_$LI$().log(logLevel, "\t" + dir.getName() + " strut --> fallback to " + this.fallback.toString());
                    }
                    return this.fallback.createStrutGeometry(dir);
                }
            }
            return super.createStrutGeometry(dir);
        }

        loadVefData(name: string): string {
            const script: string = this.mPkgName + "/" + name + ".vef";
            return com.vzome.xml.ResourceLoader.loadStringResource(ExportedVEFShapes.MODEL_PREFIX + script);
        }

        /**
         * 
         * @return {boolean}
         */
        public hasColors(): boolean {
            return !this.colors.isEmpty();
        }

        /**
         * 
         * @param {com.vzome.core.math.symmetry.Direction} dir
         * @return {com.vzome.core.construction.Color}
         */
        public getColor(dir: com.vzome.core.math.symmetry.Direction): com.vzome.core.construction.Color {
            if (this.colors.isEmpty())return null;
            const dirName: string = (dir == null) ? ExportedVEFShapes.NODE_MODEL : dir.getName();
            const colorString: string = this.colors.getProperty(dirName);
            if (colorString == null)return null;
            return com.vzome.core.render.Colors.parseColor(colorString);
        }
    }
    ExportedVEFShapes["__class"] = "com.vzome.core.viewing.ExportedVEFShapes";
    ExportedVEFShapes["__interfaces"] = ["com.vzome.core.editor.api.Shapes"];



    export namespace ExportedVEFShapes {

        export class VefToShape extends com.vzome.core.math.VefParser {
            public __parent: any;
            tipVertexIndices: java.util.Set<number>;

            midpointVertexIndices: java.util.Set<number>;

            tipVertex: com.vzome.core.algebra.AlgebraicVector;

            vertices: java.util.List<com.vzome.core.algebra.AlgebraicVector>;

            faces: java.util.List<java.util.List<number>>;

            invertSnubBall: boolean;

            public getStrutGeometry(prototype: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.parts.StrutGeometry {
                const tipAxis: com.vzome.core.math.symmetry.Axis = this.__parent.mSymmetry['getAxis$com_vzome_core_algebra_AlgebraicVector'](this.tipVertex);
                const midpoint: com.vzome.core.algebra.AlgebraicVector = this.tipVertex.scale(this.__parent.mSymmetry.getField()['createRational$long$long'](1, 2));
                const orientation: number = this.__parent.mSymmetry.inverse(tipAxis.getOrientation());
                const adjustment: com.vzome.core.algebra.AlgebraicMatrix = this.__parent.mSymmetry.getMatrix(orientation);
                const newVertices: java.util.List<com.vzome.core.algebra.AlgebraicVector> = <any>(new java.util.ArrayList<any>());
                for(let i: number = 0; i < this.vertices.size(); i++) {{
                    let originalVertex: com.vzome.core.algebra.AlgebraicVector = this.vertices.get(i);
                    if (this.tipVertexIndices.contains(i))originalVertex = originalVertex.minus(this.tipVertex); else if (this.midpointVertexIndices.contains(i))originalVertex = originalVertex.minus(midpoint);
                    const adjustedVertex: com.vzome.core.algebra.AlgebraicVector = adjustment.timesColumn(originalVertex);
                    newVertices.add(adjustedVertex);
                };}
                return new com.vzome.core.viewing.ExportedVEFStrutGeometry(newVertices, this.faces, prototype, this.tipVertexIndices, this.midpointVertexIndices, this.__parent.mSymmetry.getField());
            }

            public getConnectorPolyhedron(): com.vzome.core.math.Polyhedron {
                const result: com.vzome.core.math.Polyhedron = new com.vzome.core.math.Polyhedron(this.__parent.mSymmetry.getField());
                for(let index=this.vertices.iterator();index.hasNext();) {
                    let vertex = index.next();
                    {
                        result.addVertex(vertex);
                    }
                }
                for(let index=this.faces.iterator();index.hasNext();) {
                    let prototypeFace = index.next();
                    {
                        const face: com.vzome.core.math.Polyhedron.Face = result.newFace();
                        face.addAll(prototypeFace);
                        result.addFace(face);
                    }
                }
                return result;
            }

            /**
             * 
             * @param {number} index
             * @param {int[]} verts
             */
            addFace(index: number, verts: number[]) {
                const face: java.util.List<number> = <any>(new java.util.ArrayList<any>());
                for(let i: number = 0; i < verts.length; i++) {{
                    const n: number = this.invertSnubBall ? verts.length - 1 - i : i;
                    const j: number = verts[n];
                    face.add(j);
                };}
                this.faces.add(face);
            }

            /**
             * 
             * @param {number} index
             * @param {com.vzome.core.algebra.AlgebraicVector} location
             */
            addVertex(index: number, location: com.vzome.core.algebra.AlgebraicVector) {
                let vertex: com.vzome.core.algebra.AlgebraicVector = this.__parent.mSymmetry.getField().projectTo3d(location, this.wFirst());
                if (this.invertSnubBall){
                    vertex = vertex.negate();
                }
                this.vertices.add(vertex);
            }

            /**
             * 
             * @param {number} index
             * @param {number} vertex
             */
            addBall(index: number, vertex: number) {
                this.tipVertexIndices.add(vertex);
            }

            /**
             * 
             * @param {java.util.StringTokenizer} tokens
             */
            endFile(tokens: java.util.StringTokenizer) {
                if (!tokens.hasMoreTokens())return;
                let token: string = tokens.nextToken();
                if (!("tip" === token))throw new java.lang.IllegalStateException("VEF format error: token after face list (\"" + token + "\" should be \"tip\"");
                try {
                    token = tokens.nextToken();
                } catch(e1) {
                    throw new java.lang.IllegalStateException("VEF format error: no tokens after \"tip\"");
                }
                let tipIndex: number;
                try {
                    tipIndex = javaemul.internal.IntegerHelper.parseInt(token);
                } catch(e) {
                    throw new java.lang.RuntimeException("VEF format error: strut tip vertex index (\"" + token + "\") must be an integer", e);
                }
                this.tipVertex = this.vertices.get(tipIndex);
                if (!tokens.hasMoreTokens())return;
                token = tokens.nextToken();
                if (!("middle" === token))throw new java.lang.IllegalStateException("VEF format error: token after tip vertex (\"" + token + "\" should be \"middle\"");
                while((tokens.hasMoreTokens())) {{
                    token = tokens.nextToken();
                    let vertexIndex: number;
                    try {
                        vertexIndex = javaemul.internal.IntegerHelper.parseInt(token);
                    } catch(e) {
                        throw new java.lang.RuntimeException("VEF format error: middle vertex index (\"" + token + "\") must be an integer", e);
                    }
                    this.midpointVertexIndices.add(vertexIndex);
                }};
            }

            /**
             * 
             * @param {number} numVertices
             */
            startBalls(numVertices: number) {
            }

            /**
             * 
             * @param {number} numEdges
             */
            startEdges(numEdges: number) {
            }

            /**
             * 
             * @param {number} index
             * @param {number} v1
             * @param {number} v2
             */
            addEdge(index: number, v1: number, v2: number) {
            }

            /**
             * 
             * @param {number} numFaces
             */
            startFaces(numFaces: number) {
            }

            /**
             * 
             * @param {number} numVertices
             */
            startVertices(numVertices: number) {
            }

            constructor(__parent: any) {
                super();
                this.__parent = __parent;
                this.tipVertexIndices = <any>(new java.util.HashSet<any>());
                this.midpointVertexIndices = <any>(new java.util.HashSet<any>());
                if (this.tipVertex === undefined) { this.tipVertex = null; }
                this.vertices = <any>(new java.util.ArrayList<any>());
                this.faces = <any>(new java.util.ArrayList<any>());
                this.invertSnubBall = false;
            }
        }
        VefToShape["__class"] = "com.vzome.core.viewing.ExportedVEFShapes.VefToShape";

    }

}


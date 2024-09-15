/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.math {
    export abstract class VefParser {
        public static VERSION_EXPLICIT_OFFSET: number = 10;

        public static VERSION_EXPLICIT_DIMENSION: number = 9;

        public static VERSION_SCALE_VECTOR: number = 8;

        public static VERSION_RATIONAL_ACTUAL_SCALE: number = 7;

        public static VERSION_EXPLICIT_BALLS: number = 6;

        public static VERSION_ANY_FIELD: number = 5;

        public static VERSION_W_FIRST: number = 4;

        /*private*/ mVersion: number;

        /*private*/ dimension: number;

        /*private*/ __isRational: boolean;

        /*private*/ field: com.vzome.core.algebra.AlgebraicField;

        parsedOffset: com.vzome.core.algebra.AlgebraicVector;

        abstract startVertices(numVertices: number);

        abstract addVertex(index: number, location: com.vzome.core.algebra.AlgebraicVector);

        endVertices() {
        }

        abstract startEdges(numEdges: number);

        abstract addEdge(index: number, v1: number, v2: number);

        endEdges() {
        }

        abstract startFaces(numFaces: number);

        abstract addFace(index: number, verts: number[]);

        endFaces() {
        }

        abstract startBalls(numVertices: number);

        abstract addBall(index: number, vertex: number);

        endBalls() {
        }

        getVersion(): number {
            return this.mVersion;
        }

        getField(): com.vzome.core.algebra.AlgebraicField {
            return this.field;
        }

        isRational(): boolean {
            return this.__isRational;
        }

        wFirst(): boolean {
            return this.mVersion >= VefParser.VERSION_W_FIRST;
        }

        public parseVEF(vefData: string, field: com.vzome.core.algebra.AlgebraicField) {
            this.field = field;
            const tokens: java.util.StringTokenizer = new java.util.StringTokenizer(vefData);
            let token: string = null;
            try {
                token = tokens.nextToken();
            } catch(e1) {
                throw new java.lang.IllegalStateException("VEF format error: no tokens in file data: \"" + vefData + "\"");
            }
            this.mVersion = 0;
            this.__isRational = false;
            if (token === ("vZome")){
                try {
                    token = tokens.nextToken();
                } catch(e1) {
                    throw new java.lang.IllegalStateException("VEF format error: no tokens after \"vZome\"");
                }
                if (!("VEF" === token))throw new java.lang.IllegalStateException("VEF format error: token after \"vZome\" (\"" + token + "\" should be \"VEF\"");
                try {
                    token = tokens.nextToken();
                } catch(e1) {
                    throw new java.lang.IllegalStateException("VEF format error: no tokens after \"VEF\"");
                }
                try {
                    this.mVersion = javaemul.internal.IntegerHelper.parseInt(token);
                } catch(e) {
                    throw new java.lang.RuntimeException("VEF format error: VEF version number (\"" + token + "\") must be an integer", e);
                }
                token = tokens.nextToken();
            }
            if (token === ("field")){
                try {
                    token = tokens.nextToken();
                } catch(e1) {
                    throw new java.lang.IllegalStateException("VEF format error: no tokens after \"field\"");
                }
                if (token === ("rational")){
                    this.__isRational = true;
                    token = field.getName();
                }
                if (!field.supportsSubfield(token)){
                    throw new java.lang.IllegalStateException("VEF field mismatch error: VEF field name (\"" + token + "\") does not match current model field name (\"" + field.getName() + "\").");
                }
                token = tokens.nextToken();
            }
            if (token === ("actual")){
                try {
                    token = tokens.nextToken();
                } catch(e1) {
                    throw new java.lang.IllegalStateException("VEF format error: no tokens after \"actual\"");
                }
            }
            if (token === ("dimension")){
                try {
                    token = tokens.nextToken();
                } catch(e1) {
                    throw new java.lang.IllegalStateException("VEF format error: no tokens after \"dimension\"");
                }
                try {
                    this.dimension = javaemul.internal.IntegerHelper.parseInt(token);
                } catch(e) {
                    throw new java.lang.RuntimeException("VEF format error: dimension number (\"" + token + "\") must be an integer", e);
                }
                try {
                    token = tokens.nextToken();
                } catch(e) {
                    throw new java.lang.IllegalStateException("VEF format error: no tokens after \"dimension\"");
                }
            }
            const scaleVector: com.vzome.core.algebra.AlgebraicVector = new com.vzome.core.algebra.AlgebraicVector(field, this.dimension);
            if (token === ("scale")){
                try {
                    token = tokens.nextToken();
                    if (token === ("vector")){
                        try {
                            for(let tokNum: number = 0; tokNum < this.dimension; tokNum++) {{
                                token = tokens.nextToken();
                                const coord: com.vzome.core.algebra.AlgebraicNumber = this.field.parseVefNumber(token, this.__isRational);
                                scaleVector.setComponent(tokNum, coord);
                            };}
                        } catch(e) {
                            throw new java.lang.IllegalStateException("VEF format error: scale vector requires " + this.dimension + " coordinates");
                        }
                    } else {
                        const scale: com.vzome.core.algebra.AlgebraicNumber = this.field.parseVefNumber(token, this.__isRational);
                        for(let i: number = 0; i < this.dimension; i++) {{
                            scaleVector.setComponent(i, scale);
                        };}
                    }
                    token = tokens.nextToken();
                } catch(e) {
                    throw new java.lang.IllegalStateException("VEF format error: no tokens after \"scale\"");
                }
            } else {
                for(let i: number = 0; i < this.dimension; i++) {{
                    scaleVector.setComponent(i, field.one());
                };}
            }
            this.parsedOffset = new com.vzome.core.algebra.AlgebraicVector(field, this.dimension);
            if (token === ("offset")){
                try {
                    for(let tokNum: number = 0; tokNum < this.dimension; tokNum++) {{
                        token = tokens.nextToken();
                        const coord: com.vzome.core.algebra.AlgebraicNumber = this.field.parseVefNumber(token, this.__isRational);
                        this.parsedOffset.setComponent(tokNum, coord);
                    };}
                } catch(e) {
                    throw new java.lang.IllegalStateException("VEF format error: offset vector requires " + this.dimension + " coordinates");
                }
                try {
                    token = tokens.nextToken();
                } catch(e) {
                    throw new java.lang.IllegalStateException("VEF format error: no tokens after \"offset\"");
                }
            }
            let numVertices: number;
            try {
                numVertices = javaemul.internal.IntegerHelper.parseInt(token);
            } catch(e) {
                throw new java.lang.RuntimeException("VEF format error: number of vertices (\"" + token + "\") must be an integer", e);
            }
            this.startVertices(numVertices);
            const hasOffset: boolean = !this.parsedOffset.isOrigin();
            for(let i: number = 0; i < numVertices; i++) {{
                let v: com.vzome.core.algebra.AlgebraicVector = field.origin(this.dimension);
                for(let tokNum: number = 0; tokNum < this.dimension; tokNum++) {{
                    try {
                        token = tokens.nextToken();
                    } catch(e1) {
                        throw new java.lang.IllegalStateException("VEF format error: not enough vertices in list");
                    }
                    const coord: com.vzome.core.algebra.AlgebraicNumber = this.field.parseVefNumber(token, this.__isRational)['times$com_vzome_core_algebra_AlgebraicNumber'](scaleVector.getComponent(tokNum));
                    v.setComponent(tokNum, coord);
                };}
                if (hasOffset){
                    v = v.plus(this.parsedOffset);
                }
                this.addVertex(i, v);
            };}
            this.endVertices();
            if (tokens.hasMoreTokens()){
                token = tokens.nextToken();
                let numEdges: number;
                try {
                    numEdges = javaemul.internal.IntegerHelper.parseInt(token);
                } catch(e) {
                    throw new java.lang.RuntimeException("VEF format error: number of edges (\"" + token + "\") must be an integer", e);
                }
                this.startEdges(numEdges);
                for(let i: number = 0; i < numEdges; i++) {{
                    try {
                        token = tokens.nextToken();
                    } catch(e1) {
                        throw new java.lang.IllegalStateException("VEF format error: not enough edges in list");
                    }
                    const v1: number = javaemul.internal.IntegerHelper.parseInt(token);
                    try {
                        token = tokens.nextToken();
                    } catch(e1) {
                        throw new java.lang.IllegalStateException("VEF format error: 2nd vertex index of last edge is missing");
                    }
                    const v2: number = javaemul.internal.IntegerHelper.parseInt(token);
                    this.addEdge(i, v1, v2);
                };}
                this.endEdges();
            }
            if (tokens.hasMoreTokens()){
                token = tokens.nextToken();
                let numFaces: number;
                try {
                    numFaces = javaemul.internal.IntegerHelper.parseInt(token);
                } catch(e) {
                    throw new java.lang.RuntimeException("VEF format error: number of faces (\"" + token + "\") must be an integer", e);
                }
                this.startFaces(numFaces);
                for(let i: number = 0; i < numFaces; i++) {{
                    try {
                        token = tokens.nextToken();
                    } catch(e1) {
                        throw new java.lang.IllegalStateException("VEF format error: not enough faces in list");
                    }
                    const order: number = javaemul.internal.IntegerHelper.parseInt(token);
                    const verts: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(order);
                    for(let j: number = 0; j < order; j++) {{
                        try {
                            token = tokens.nextToken();
                        } catch(e1) {
                            throw new java.lang.IllegalStateException("VEF format error: not enough vertices in last face");
                        }
                        verts[j] = javaemul.internal.IntegerHelper.parseInt(token);
                    };}
                    this.addFace(i, verts);
                };}
                this.endFaces();
            }
            if (tokens.hasMoreTokens()){
                token = tokens.nextToken();
                let numBalls: number;
                try {
                    numBalls = javaemul.internal.IntegerHelper.parseInt(token);
                } catch(e) {
                    throw new java.lang.RuntimeException("VEF format error: number of balls (\"" + token + "\") must be an integer", e);
                }
                this.startBalls(numBalls);
                for(let i: number = 0; i < numBalls; i++) {{
                    try {
                        token = tokens.nextToken();
                    } catch(e1) {
                        throw new java.lang.IllegalStateException("VEF format error: not enough balls in list");
                    }
                    const v1: number = javaemul.internal.IntegerHelper.parseInt(token);
                    this.addBall(i, v1);
                };}
                this.endBalls();
            }
            this.endFile(tokens);
        }

        endFile(tokens: java.util.StringTokenizer) {
        }

        constructor() {
            this.mVersion = 0;
            this.dimension = 4;
            this.__isRational = false;
            if (this.field === undefined) { this.field = null; }
            if (this.parsedOffset === undefined) { this.parsedOffset = null; }
        }
    }
    VefParser["__class"] = "com.vzome.core.math.VefParser";

}


/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    export abstract class Polygon extends com.vzome.core.construction.Construction {
        /**
         * 
         * @return {string}
         */
        public toString(): string {
            return "polygon " + java.util.Arrays.toString(this.mVertices);
        }

        /*private*/ mVertices: com.vzome.core.algebra.AlgebraicVector[];

        public constructor(field: com.vzome.core.algebra.AlgebraicField) {
            super(field);
            if (this.mVertices === undefined) { this.mVertices = null; }
            this.STRING_COMPARATOR = (s1, s2) => /* compareTo */s1.localeCompare(s2);
        }

        /*private*/ STRING_COMPARATOR: java.util.Comparator<string>;

        public getSignature(): string {
            const strArray: string[] = java.util.Arrays.stream<any>(this.mVertices).map<any>((av) => av.projectTo3d(true).toString()).toArray<any>((arg0) => { return new Array<string>(arg0) });
            java.util.Arrays.sort<any>(strArray, 0, strArray.length, <any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0, arg1) =>  (funcInst['compare'] ? funcInst['compare'] : funcInst) .call(funcInst, arg0, arg1)})(this.STRING_COMPARATOR)));
            return java.util.Arrays.toString(strArray);
        }

        setStateVariable(vertices: com.vzome.core.algebra.AlgebraicVector[], impossible: boolean): boolean {
            if (impossible){
                if (this.isImpossible())return false;
                this.setImpossible(true);
                return true;
            }
            this.mVertices = vertices;
            this.setImpossible(false);
            return true;
        }

        public getXml$org_w3c_dom_Document(doc: org.w3c.dom.Document): org.w3c.dom.Element {
            const result: org.w3c.dom.Element = doc.createElement("polygon");
            this.getXml$org_w3c_dom_Element$java_lang_String(result, "vertex");
            return result;
        }

        public getXml$org_w3c_dom_Element$java_lang_String(result: org.w3c.dom.Element, vertexChildName: string) {
            for(let index = 0; index < this.mVertices.length; index++) {
                let vertex = this.mVertices[index];
                {
                    const child: org.w3c.dom.Element = result.getOwnerDocument().createElement(vertexChildName);
                    com.vzome.xml.DomUtils.addAttribute(child, "at", vertex.getVectorExpression$int(com.vzome.core.algebra.AlgebraicField.ZOMIC_FORMAT));
                    result.appendChild(child);
                }
            }
        }

        public getXml(result?: any, vertexChildName?: any) {
            if (((result != null && (result.constructor != null && result.constructor["__interfaces"] != null && result.constructor["__interfaces"].indexOf("org.w3c.dom.Element") >= 0)) || result === null) && ((typeof vertexChildName === 'string') || vertexChildName === null)) {
                return <any>this.getXml$org_w3c_dom_Element$java_lang_String(result, vertexChildName);
            } else if (((result != null && (result.constructor != null && result.constructor["__interfaces"] != null && result.constructor["__interfaces"].indexOf("org.w3c.dom.Document") >= 0)) || result === null) && vertexChildName === undefined) {
                return <any>this.getXml$org_w3c_dom_Document(result);
            } else throw new Error('invalid overload');
        }

        /**
         * 
         * @return {boolean}
         */
        public is3d(): boolean {
            for(let index = 0; index < this.mVertices.length; index++) {
                let algebraicVector = this.mVertices[index];
                {
                    if (algebraicVector.dimension() !== 3)return false;
                }
            }
            return true;
        }

        public getVertexCount(): number {
            return this.mVertices.length;
        }

        public getVertex(i: number): com.vzome.core.algebra.AlgebraicVector {
            return this.mVertices[i];
        }

        public getNormal(): com.vzome.core.algebra.AlgebraicVector {
            return com.vzome.core.algebra.AlgebraicVectors.getNormal$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector(this.mVertices[0], this.mVertices[1], this.mVertices[2]);
        }

        public getCentroid(): com.vzome.core.algebra.AlgebraicVector {
            return com.vzome.core.algebra.AlgebraicVectors.getCentroid(this.mVertices);
        }

        public getVertices(): com.vzome.core.algebra.AlgebraicVector[] {
            return java.util.Arrays.copyOf<any>(this.mVertices, this.mVertices.length);
        }
    }
    Polygon["__class"] = "com.vzome.core.construction.Polygon";

}


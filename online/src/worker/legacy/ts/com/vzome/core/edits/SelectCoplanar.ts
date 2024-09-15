/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class SelectCoplanar extends com.vzome.core.edits.SelectByBoundary {
        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
            super(editor);
            this.pickedVectors = <any>(new java.util.ArrayList<any>());
            this.vectors = <any>(new java.util.HashSet<any>());
            this.pointOnPlane = null;
            this.normal = null;
        }

        /**
         * 
         * @return {string}
         */
        public usage(): string {
            return "Select any combination of connectors, struts or panels to specify\n3 or more points that are all coplanar, but not simply collinear.\n\nAll parts that are completely on the corresponding plane will be selected.\n";
        }

        pickedVectors: java.util.List<com.vzome.core.algebra.AlgebraicVector>;

        vectors: java.util.Set<com.vzome.core.algebra.AlgebraicVector>;

        pointOnPlane: com.vzome.core.algebra.AlgebraicVector;

        normal: com.vzome.core.algebra.AlgebraicVector;

        /**
         * 
         * @param {*} props
         */
        public configure(props: java.util.Map<string, any>) {
            const panel: com.vzome.core.model.Panel = <com.vzome.core.model.Panel><any>props.get("picked");
            if (panel != null){
                for(let index=panel.iterator();index.hasNext();) {
                    let v = index.next();
                    {
                        this.pickedVectors.add(v);
                    }
                }
            }
        }

        /**
         * 
         * @return {string}
         */
        setBoundary(): string {
            if (this.pickedVectors.isEmpty()){
                for(let index=this.mSelection.iterator();index.hasNext();) {
                    let man = index.next();
                    {
                        if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                            this.vectors.add(man.getLocation());
                        } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                            this.vectors.add(man.getLocation());
                            this.vectors.add((<com.vzome.core.model.Strut><any>man).getEnd());
                        } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                            for(let index=(<com.vzome.core.model.Panel><any>man).iterator();index.hasNext();) {
                                let v = index.next();
                                {
                                    this.vectors.add(v);
                                }
                            }
                        } else {
                            throw new java.lang.IllegalStateException("Unknown manifestation: " + /* getSimpleName */(c => typeof c === 'string' ? (<any>c).substring((<any>c).lastIndexOf('.')+1) : c["__class"] ? c["__class"].substring(c["__class"].lastIndexOf('.')+1) : c["name"].substring(c["name"].lastIndexOf('.')+1))((<any>man.constructor)));
                        }
                    }
                }
                if (this.vectors.size() < 3){
                    return "Additional connectors, struts or panels must be selected to define a plane.";
                }
                if (com.vzome.core.algebra.AlgebraicVectors.areCollinear$java_util_Collection(this.vectors)){
                    return "Selected items are collinear. Select another non-collinear ball to specify the plane.";
                }
                if (!com.vzome.core.algebra.AlgebraicVectors.areCoplanar(this.vectors)){
                    return "Selected items are not coplanar.";
                }
            } else {
                this.vectors.addAll(this.pickedVectors);
                this.unselectAll();
                this.redo();
            }
            for(let index=this.vectors.iterator();index.hasNext();) {
                let v = index.next();
                {
                    this.pointOnPlane = v;
                    break;
                }
            }
            this.normal = com.vzome.core.algebra.AlgebraicVectors.getNormal$java_util_Collection(this.vectors);
            return null;
        }

        public boundaryContains$com_vzome_core_algebra_AlgebraicVector(v: com.vzome.core.algebra.AlgebraicVector): boolean {
            return this.vectors.contains(v) || this.pointOnPlane.minus(v).dot(this.normal).isZero();
        }

        /**
         * 
         * @param {com.vzome.core.algebra.AlgebraicVector} v
         * @return {boolean}
         */
        public boundaryContains(v?: any): boolean {
            if (((v != null && v instanceof <any>com.vzome.core.algebra.AlgebraicVector) || v === null)) {
                return <any>this.boundaryContains$com_vzome_core_algebra_AlgebraicVector(v);
            } else if (((v != null && (v.constructor != null && v.constructor["__interfaces"] != null && v.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)) || v === null)) {
                return super.boundaryContains(v);
            } else if (((v != null && (v.constructor != null && v.constructor["__interfaces"] != null && v.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)) || v === null)) {
                return <any>this.boundaryContains$com_vzome_core_model_Strut(v);
            } else if (((v != null && (v.constructor != null && v.constructor["__interfaces"] != null && v.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)) || v === null)) {
                return <any>this.boundaryContains$com_vzome_core_model_Panel(v);
            } else throw new Error('invalid overload');
        }

        public static NAME: string = "SelectCoplanar";

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return SelectCoplanar.NAME;
        }

        /**
         * 
         * @param {*} element
         */
        getXmlAttributes(element: org.w3c.dom.Element) {
            if (!this.pickedVectors.isEmpty()){
                com.vzome.xml.DomUtils.addAttribute(element, "nPickedVectors", /* toString */(''+(this.pickedVectors.size())));
                for(let i: number = 0; i < this.pickedVectors.size(); i++) {{
                    com.vzome.xml.DomUtils.addAttribute(element, "vector" + i, this.pickedVectors.get(i).toParsableString());
                };}
            }
        }

        /**
         * 
         * @param {*} xml
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         */
        setXmlAttributes(xml: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat) {
            const nPickedVectors: string = xml.getAttribute("nPickedVectors");
            if (nPickedVectors != null && !/* isEmpty */(nPickedVectors.length === 0)){
                const nPicked: number = javaemul.internal.IntegerHelper.parseInt(nPickedVectors);
                for(let i: number = 0; i < nPicked; i++) {{
                    this.pickedVectors.add(format.parseRationalVector(xml, "vector" + i));
                };}
            }
        }
    }
    SelectCoplanar["__class"] = "com.vzome.core.edits.SelectCoplanar";

}


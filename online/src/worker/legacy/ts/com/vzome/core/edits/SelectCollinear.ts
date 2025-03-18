/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    /**
     * @author David Hall
     * @param {*} editor
     * @class
     * @extends com.vzome.core.editor.api.ChangeManifestations
     */
    export class SelectCollinear extends com.vzome.core.editor.api.ChangeManifestations {
        /*private*/ vector1: com.vzome.core.algebra.AlgebraicVector;

        /*private*/ vector2: com.vzome.core.algebra.AlgebraicVector;

        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
            super(editor);
            this.vector1 = null;
            this.vector2 = null;
        }

        /**
         * 
         * @param {*} props
         */
        public configure(props: java.util.Map<string, any>) {
            const strut: com.vzome.core.model.Strut = <com.vzome.core.model.Strut><any>props.get("picked");
            if (strut != null){
                this.vector1 = strut.getLocation();
                this.vector2 = strut.getEnd();
            }
        }

        /**
         * 
         */
        public perform() {
            if (this.vector1 == null || this.vector2 == null){
                const lastStrut: com.vzome.core.model.Strut = this.getLastSelectedStrut();
                if (lastStrut != null){
                    this.vector1 = lastStrut.getLocation();
                    this.vector2 = lastStrut.getEnd();
                } else {
                    for(let index=this.getSelectedConnectors().iterator();index.hasNext();) {
                        let ball = index.next();
                        {
                            this.vector1 = this.vector2;
                            this.vector2 = ball.getLocation();
                        }
                    }
                }
            }
            if (this.vector1 == null || this.vector2 == null){
                throw new com.vzome.core.commands.Command.Failure("select a strut or two balls as a reference.");
            }
            this.unselectAll();
            const balls: java.util.Set<com.vzome.core.model.Connector> = <any>(new java.util.TreeSet<any>());
            for(let index=this.getVisibleConnectors$java_util_function_Predicate((ball) => { return this.isCollinearWith(ball) }).iterator();index.hasNext();) {
                let ball = index.next();
                {
                    balls.add(ball);
                }
            }
            const struts: java.util.Set<com.vzome.core.model.Strut> = <any>(new java.util.TreeSet<any>());
            for(let index=this.getVisibleStruts$().iterator();index.hasNext();) {
                let strut = index.next();
                {
                    if (this.isCollinearWith$com_vzome_core_model_Strut(strut)){
                        struts.add(strut);
                    }
                }
            }
            for(let index=struts.iterator();index.hasNext();) {
                let strut = index.next();
                {
                    this.select$com_vzome_core_model_Manifestation(strut);
                }
            }
            for(let index=balls.iterator();index.hasNext();) {
                let ball = index.next();
                {
                    this.select$com_vzome_core_model_Manifestation(ball);
                }
            }
            const level: java.util.logging.Level = java.util.logging.Level.FINER;
            if (com.vzome.core.editor.api.ChangeSelection.logger_$LI$().isLoggable(level)){
                const sb: java.lang.StringBuilder = new java.lang.StringBuilder("Selected:\n");
                const indent: string = "  ";
                for(let index=struts.iterator();index.hasNext();) {
                    let strut = index.next();
                    {
                        sb.append(indent).append(strut.toString()).append("\n");
                    }
                }
                for(let index=balls.iterator();index.hasNext();) {
                    let ball = index.next();
                    {
                        sb.append(indent).append(ball.toString()).append("\n");
                    }
                }
                com.vzome.core.editor.api.ChangeSelection.logger_$LI$().log(level, sb.toString());
            }
            super.perform();
        }

        public isCollinearWith$com_vzome_core_model_Connector(ball: com.vzome.core.model.Connector): boolean {
            return this.isCollinear(ball.getLocation());
        }

        public isCollinearWith(ball?: any): boolean {
            if (((ball != null && (ball.constructor != null && ball.constructor["__interfaces"] != null && ball.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)) || ball === null)) {
                return <any>this.isCollinearWith$com_vzome_core_model_Connector(ball);
            } else if (((ball != null && (ball.constructor != null && ball.constructor["__interfaces"] != null && ball.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)) || ball === null)) {
                return <any>this.isCollinearWith$com_vzome_core_model_Strut(ball);
            } else throw new Error('invalid overload');
        }

        /*private*/ isCollinearWith$com_vzome_core_model_Strut(strut: com.vzome.core.model.Strut): boolean {
            return this.isCollinear(strut.getLocation()) && this.isCollinear(strut.getEnd());
        }

        /*private*/ isCollinear(vec: com.vzome.core.algebra.AlgebraicVector): boolean {
            return com.vzome.core.algebra.AlgebraicVectors.areCollinear$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector(vec, this.vector1, this.vector2);
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "SelectCollinear";
        }

        /**
         * 
         * @param {*} element
         */
        getXmlAttributes(element: org.w3c.dom.Element) {
            com.vzome.xml.DomUtils.addAttribute(element, "vector1", this.vector1.toParsableString());
            com.vzome.xml.DomUtils.addAttribute(element, "vector2", this.vector2.toParsableString());
        }

        /**
         * 
         * @param {*} xml
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         */
        setXmlAttributes(xml: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat) {
            this.vector1 = format.parseRationalVector(xml, "vector1");
            this.vector2 = format.parseRationalVector(xml, "vector2");
        }
    }
    SelectCollinear["__class"] = "com.vzome.core.edits.SelectCollinear";

}


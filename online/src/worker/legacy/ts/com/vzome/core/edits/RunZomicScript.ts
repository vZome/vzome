/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class RunZomicScript extends com.vzome.core.editor.api.ChangeManifestations {
        /*private*/ programText: string;

        /*private*/ origin: com.vzome.core.construction.Point;

        /*private*/ symm: com.vzome.core.math.symmetry.IcosahedralSymmetry;

        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
            super(editor);
            if (this.programText === undefined) { this.programText = null; }
            if (this.origin === undefined) { this.origin = null; }
            if (this.symm === undefined) { this.symm = null; }
            this.origin = (<com.vzome.core.editor.api.ImplicitSymmetryParameters><any>editor).getCenterPoint();
            this.symm = <com.vzome.core.math.symmetry.IcosahedralSymmetry><any>(<com.vzome.core.editor.api.SymmetryAware><any>editor)['getSymmetrySystem$']().getSymmetry();
        }

        /**
         * 
         * @param {*} props
         */
        public configure(props: java.util.Map<string, any>) {
            this.programText = <string>props.get("script");
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "RunZomicScript";
        }

        getScriptDialect(): string {
            return "zomic";
        }

        /**
         * 
         * @param {*} element
         */
        getXmlAttributes(element: org.w3c.dom.Element) {
            element.setTextContent(com.vzome.core.commands.XmlSaveFormat.escapeNewlines(this.programText));
        }

        /**
         * 
         * @param {*} xml
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         */
        setXmlAttributes(xml: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat) {
            this.programText = xml.getTextContent();
            this.symm = <com.vzome.core.math.symmetry.IcosahedralSymmetry><any>(<com.vzome.core.commands.XmlSymmetryFormat>format).parseSymmetry("icosahedral");
        }

        /**
         * 
         */
        public perform() {
            let offset: com.vzome.core.construction.Point = null;
            let pointFound: boolean = false;
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                        const nextPoint: com.vzome.core.construction.Point = <com.vzome.core.construction.Point>(<com.vzome.core.model.Connector><any>man).getFirstConstruction();
                        if (!pointFound){
                            pointFound = true;
                            offset = nextPoint;
                        } else {
                            offset = null;
                        }
                    }
                }
            }
            if (offset == null)offset = this.origin;
            try {
                this.symm.interpretScript(this.programText, this.getScriptDialect(), offset, this.symm, new com.vzome.core.editor.api.ManifestConstructions(this));
            } catch(e) {
                throw new com.vzome.core.commands.Command.Failure(e.message, e);
            }
            this.redo();
        }
    }
    RunZomicScript["__class"] = "com.vzome.core.edits.RunZomicScript";

}


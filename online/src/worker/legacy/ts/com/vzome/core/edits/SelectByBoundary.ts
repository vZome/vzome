/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export abstract class SelectByBoundary extends com.vzome.core.editor.api.ChangeManifestations {
        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
            super(editor);
        }

        public abstract usage(): string;

        /**
         * 
         * @param {string} message
         */
        fail(message: string) {
            const errorMsg: java.lang.StringBuilder = new java.lang.StringBuilder();
            const usage: string = this.usage();
            if (usage != null){
                errorMsg.append(usage);
            }
            if (message != null){
                if (errorMsg.length() > 0){
                    errorMsg.append("\n");
                }
                errorMsg.append(message);
            }
            super.fail(errorMsg.toString());
        }

        /**
         * Sets the boundary criteria based on the selection
         * @return {string} null if successful, otherwise a string describing the error.
         */
        abstract setBoundary(): string;

        /**
         * 
         */
        public perform() {
            const errMsg: string = this.setBoundary();
            if (errMsg != null){
                this.fail(errMsg);
            }
            this.unselectAll();
            this.selectBoundedManifestations();
            this.redo();
        }

        selectBoundedManifestations() {
            for(let index=this.getConnectors().iterator();index.hasNext();) {
                let connector = index.next();
                {
                    if (this.boundaryContains$com_vzome_core_model_Connector(connector)){
                        this.select$com_vzome_core_model_Manifestation(connector);
                    }
                }
            }
            for(let index=this.getStruts().iterator();index.hasNext();) {
                let strut = index.next();
                {
                    if (this.boundaryContains$com_vzome_core_model_Strut(strut)){
                        this.select$com_vzome_core_model_Manifestation(strut);
                    }
                }
            }
            for(let index=this.getPanels().iterator();index.hasNext();) {
                let panel = index.next();
                {
                    if (this.boundaryContains$com_vzome_core_model_Panel(panel)){
                        this.select$com_vzome_core_model_Manifestation(panel);
                    }
                }
            }
        }

        public boundaryContains$com_vzome_core_model_Connector(connector: com.vzome.core.model.Connector): boolean {
            return this.boundaryContains$com_vzome_core_algebra_AlgebraicVector(connector.getLocation());
        }

        public boundaryContains(connector?: any): boolean {
            if (((connector != null && (connector.constructor != null && connector.constructor["__interfaces"] != null && connector.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)) || connector === null)) {
                return <any>this.boundaryContains$com_vzome_core_model_Connector(connector);
            } else if (((connector != null && (connector.constructor != null && connector.constructor["__interfaces"] != null && connector.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)) || connector === null)) {
                return <any>this.boundaryContains$com_vzome_core_model_Strut(connector);
            } else if (((connector != null && (connector.constructor != null && connector.constructor["__interfaces"] != null && connector.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)) || connector === null)) {
                return <any>this.boundaryContains$com_vzome_core_model_Panel(connector);
            } else if (((connector != null && connector instanceof <any>com.vzome.core.algebra.AlgebraicVector) || connector === null)) {
                return <any>this.boundaryContains$com_vzome_core_algebra_AlgebraicVector(connector);
            } else throw new Error('invalid overload');
        }

        /*private*/ boundaryContains$com_vzome_core_model_Strut(strut: com.vzome.core.model.Strut): boolean {
            return this.boundaryContains$com_vzome_core_algebra_AlgebraicVector(strut.getLocation()) && this.boundaryContains$com_vzome_core_algebra_AlgebraicVector(strut.getEnd());
        }

        /*private*/ boundaryContains$com_vzome_core_model_Panel(panel: com.vzome.core.model.Panel): boolean {
            for(let index=panel.iterator();index.hasNext();) {
                let vertex = index.next();
                {
                    if (!this.boundaryContains$com_vzome_core_algebra_AlgebraicVector(vertex)){
                        return false;
                    }
                }
            }
            return true;
        }

        boundaryContains$com_vzome_core_algebra_AlgebraicVector(v: com.vzome.core.algebra.AlgebraicVector): boolean { throw new Error('cannot invoke abstract overloaded method... check your argument(s) type(s)'); }
    }
    SelectByBoundary["__class"] = "com.vzome.core.edits.SelectByBoundary";

}


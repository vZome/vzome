/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class SelectNeighbors extends com.vzome.core.editor.api.ChangeSelection {
        /*private*/ editor: com.vzome.core.editor.api.EditorModel;

        /*private*/ withPanels: boolean;

        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
            super(editor.getSelection());
            if (this.editor === undefined) { this.editor = null; }
            this.withPanels = false;
            this.editor = editor;
        }

        public perform() {
            const model: com.vzome.core.model.RealizedModel = this.editor.getRealizedModel();
            const panels: java.util.Set<com.vzome.core.model.Panel> = <any>(new java.util.LinkedHashSet<any>());
            const struts: java.util.Set<com.vzome.core.model.Strut> = <any>(new java.util.LinkedHashSet<any>());
            const balls: java.util.Set<com.vzome.core.model.Connector> = <any>(new java.util.LinkedHashSet<any>());
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0))struts.add(<com.vzome.core.model.Strut><any>man); else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0))balls.add(<com.vzome.core.model.Connector><any>man); else if (this.withPanels && (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)))panels.add(<com.vzome.core.model.Panel><any>man);
                }
            }
            for(let index=balls.iterator();index.hasNext();) {
                let ball = index.next();
                {
                    const loc: com.vzome.core.algebra.AlgebraicVector = ball.getLocation();
                    for(let index=model.iterator();index.hasNext();) {
                        let man = index.next();
                        {
                            if (!man.isRendered())continue;
                            if ((man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)) && !struts.contains(man)){
                                const strut: com.vzome.core.model.Strut = <com.vzome.core.model.Strut><any>man;
                                if (loc.equals(strut.getLocation()) || loc.equals(strut.getEnd()))this.select$com_vzome_core_model_Manifestation(strut);
                            } else if (this.withPanels && (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)) && !panels.contains(man)){
                                const panel: com.vzome.core.model.Panel = <com.vzome.core.model.Panel><any>man;
                                for(let index=panel.iterator();index.hasNext();) {
                                    let vertex = index.next();
                                    {
                                        if (loc.equals(vertex)){
                                            this.select$com_vzome_core_model_Manifestation(panel);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            for(let index=struts.iterator();index.hasNext();) {
                let strut = index.next();
                {
                    const loc: com.vzome.core.algebra.AlgebraicVector = strut.getLocation();
                    const end: com.vzome.core.algebra.AlgebraicVector = strut.getEnd();
                    for(let index=model.iterator();index.hasNext();) {
                        let man = index.next();
                        {
                            if (!man.isRendered())continue;
                            if ((man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)) && !balls.contains(man)){
                                const bloc: com.vzome.core.algebra.AlgebraicVector = man.getLocation();
                                if (bloc.equals(loc) || bloc.equals(end))this.select$com_vzome_core_model_Manifestation(man);
                            }
                        }
                    }
                }
            }
            if (this.withPanels){
                for(let index=panels.iterator();index.hasNext();) {
                    let panel = index.next();
                    {
                        for(let index=panel.iterator();index.hasNext();) {
                            let loc = index.next();
                            {
                                for(let index=model.iterator();index.hasNext();) {
                                    let man = index.next();
                                    {
                                        if (man.isRendered()){
                                            if ((man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)) && !balls.contains(man)){
                                                const bloc: com.vzome.core.algebra.AlgebraicVector = man.getLocation();
                                                if (bloc.equals(loc)){
                                                    this.select$com_vzome_core_model_Manifestation(man);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            super.perform();
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "SelectNeighbors";
        }
    }
    SelectNeighbors["__class"] = "com.vzome.core.edits.SelectNeighbors";

}


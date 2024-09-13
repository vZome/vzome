/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.editor.api {
    export abstract class ChangeManifestations extends com.vzome.core.editor.api.ChangeSelection {
        mManifestations: com.vzome.core.model.RealizedModel;

        public constructor(editorModel: com.vzome.core.editor.api.EditorModel) {
            super(editorModel.getSelection());
            if (this.mManifestations === undefined) { this.mManifestations = null; }
            this.mManifestations = editorModel.getRealizedModel();
            this.mManifestations.clearPerEditManifestations();
        }

        /**
         * 
         */
        public redo() {
            this.mManifestations.clearPerEditManifestations();
            super.redo();
        }

        /**
         * 
         */
        public undo() {
            this.mManifestations.clearPerEditManifestations();
            super.undo();
        }

        getManifestation(c: com.vzome.core.construction.Construction): com.vzome.core.model.Manifestation {
            return this.mManifestations.getManifestation(c);
        }

        public manifestConstruction(c: com.vzome.core.construction.Construction): com.vzome.core.model.Manifestation {
            const sig: string = c.getSignature();
            const m: com.vzome.core.model.Manifestation = this.mManifestations.findConstruction(c);
            if (m == null)return null;
            const made: com.vzome.core.model.Manifestation = this.mManifestations.findPerEditManifestation(sig);
            if (made != null)return made;
            if (m.isUnnecessary()){
                this.mManifestations.addPerEditManifestation(sig, m);
                this.plan(new ChangeManifestations.ManifestConstruction(this, c, m, true));
            } else {
                if (!m.isRendered())this.plan(new ChangeManifestations.RenderManifestation(this, m, true));
            }
            return m;
        }

        unmanifestConstruction(c: com.vzome.core.construction.Construction): com.vzome.core.model.Manifestation {
            const m: com.vzome.core.model.Manifestation = this.mManifestations.removeConstruction(c);
            if (m == null)return null;
            this.plan(new ChangeManifestations.ManifestConstruction(this, c, m, false));
            return m;
        }

        deleteManifestation(man: com.vzome.core.model.Manifestation) {
            this.plan(new ChangeManifestations.DeleteManifestation(this, man));
        }

        showManifestation(m: com.vzome.core.model.Manifestation) {
            this.plan(new ChangeManifestations.RenderManifestation(this, m, true));
        }

        hideManifestation(m: com.vzome.core.model.Manifestation) {
            this.plan(new ChangeManifestations.RenderManifestation(this, m, false));
        }

        public colorManifestation(m: com.vzome.core.model.Manifestation, color: com.vzome.core.construction.Color) {
            this.plan(new ChangeManifestations.ColorManifestation(this, m, color));
        }

        public labelManifestation(m: com.vzome.core.model.Manifestation, label: string) {
            this.plan(new ChangeManifestations.LabelManifestation(this, m, label));
        }

        hideConnectors() {
            for(let index=com.vzome.core.editor.api.Manifestations.getVisibleConnectors(this.mManifestations).iterator();index.hasNext();) {
                let connector = index.next();
                this.hideManifestation(connector)
            }
        }

        showConnectors() {
            for(let index=com.vzome.core.editor.api.Manifestations.getHiddenConnectors(this.mManifestations).iterator();index.hasNext();) {
                let connector = index.next();
                this.showManifestation(connector)
            }
        }

        hideStruts() {
            for(let index=com.vzome.core.editor.api.Manifestations.getVisibleStruts(this.mManifestations).iterator();index.hasNext();) {
                let strut = index.next();
                this.hideManifestation(strut)
            }
        }

        showStruts() {
            for(let index=com.vzome.core.editor.api.Manifestations.getHiddenStruts(this.mManifestations).iterator();index.hasNext();) {
                let strut = index.next();
                this.showManifestation(strut)
            }
        }

        hidePanels() {
            for(let index=com.vzome.core.editor.api.Manifestations.getVisiblePanels(this.mManifestations).iterator();index.hasNext();) {
                let panel = index.next();
                this.hideManifestation(panel)
            }
        }

        showPanels() {
            for(let index=com.vzome.core.editor.api.Manifestations.getHiddenPanels(this.mManifestations).iterator();index.hasNext();) {
                let panel = index.next();
                this.showManifestation(panel)
            }
        }

        public showsManifestation(man: com.vzome.core.model.Manifestation): boolean {
            for(const iterator: java.util.Iterator<com.vzome.core.editor.api.SideEffect> = this.getEffects(); iterator.hasNext(); ) {{
                const effect: com.vzome.core.editor.api.SideEffect = iterator.next();
                if (effect != null && effect instanceof <any>com.vzome.core.editor.api.ChangeManifestations.ManifestConstruction){
                    const show: ChangeManifestations.ManifestConstruction = <ChangeManifestations.ManifestConstruction><any>effect;
                    if (show.showsManifestation(man))return true;
                } else if (effect != null && effect instanceof <any>com.vzome.core.editor.api.ChangeManifestations.RenderManifestation){
                    const show: ChangeManifestations.RenderManifestation = <ChangeManifestations.RenderManifestation><any>effect;
                    if (show.showsManifestation(man))return true;
                }
            };}
            return false;
        }

        getRenderedSelection(): com.vzome.core.editor.api.Manifestations.ManifestationIterator {
            return com.vzome.core.editor.api.Manifestations.visibleManifestations$java_lang_Iterable$java_util_function_Predicate(this.mSelection, (man) => { return com.vzome.core.editor.api.Manifestations.Filters.isRendered(man) });
        }

        getConnectors(): com.vzome.core.editor.api.Manifestations.ConnectorIterator {
            return com.vzome.core.editor.api.Manifestations.getConnectors$java_lang_Iterable(this.mManifestations);
        }

        getStruts(): com.vzome.core.editor.api.Manifestations.StrutIterator {
            return com.vzome.core.editor.api.Manifestations.getStruts$java_lang_Iterable(this.mManifestations);
        }

        getPanels(): com.vzome.core.editor.api.Manifestations.PanelIterator {
            return com.vzome.core.editor.api.Manifestations.getPanels$java_lang_Iterable(this.mManifestations);
        }

        getVisibleConnectors$(): com.vzome.core.editor.api.Manifestations.ConnectorIterator {
            return com.vzome.core.editor.api.Manifestations.getVisibleConnectors(this.mManifestations);
        }

        getVisibleStruts$(): com.vzome.core.editor.api.Manifestations.StrutIterator {
            return com.vzome.core.editor.api.Manifestations.getVisibleStruts(this.mManifestations);
        }

        getVisiblePanels$(): com.vzome.core.editor.api.Manifestations.PanelIterator {
            return com.vzome.core.editor.api.Manifestations.getVisiblePanels(this.mManifestations);
        }

        public getVisibleConnectors$java_util_function_Predicate(postFilter: (p1: com.vzome.core.model.Connector) => boolean): com.vzome.core.editor.api.Manifestations.ConnectorIterator {
            return com.vzome.core.editor.api.Manifestations.getVisibleConnectors(this.mManifestations, <any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(postFilter)));
        }

        public getVisibleConnectors(postFilter?: any): com.vzome.core.editor.api.Manifestations.ConnectorIterator {
            if (((typeof postFilter === 'function' && (<any>postFilter).length === 1) || postFilter === null)) {
                return <any>this.getVisibleConnectors$java_util_function_Predicate(postFilter);
            } else if (postFilter === undefined) {
                return <any>this.getVisibleConnectors$();
            } else throw new Error('invalid overload');
        }

        public getVisibleStruts$java_util_function_Predicate(postFilter: (p1: com.vzome.core.model.Strut) => boolean): com.vzome.core.editor.api.Manifestations.StrutIterator {
            return com.vzome.core.editor.api.Manifestations.getVisibleStruts(this.mManifestations, <any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(postFilter)));
        }

        public getVisibleStruts(postFilter?: any): com.vzome.core.editor.api.Manifestations.StrutIterator {
            if (((typeof postFilter === 'function' && (<any>postFilter).length === 1) || postFilter === null)) {
                return <any>this.getVisibleStruts$java_util_function_Predicate(postFilter);
            } else if (postFilter === undefined) {
                return <any>this.getVisibleStruts$();
            } else throw new Error('invalid overload');
        }

        public getVisiblePanels$java_util_function_Predicate(postFilter: (p1: com.vzome.core.model.Panel) => boolean): com.vzome.core.editor.api.Manifestations.PanelIterator {
            return com.vzome.core.editor.api.Manifestations.getVisiblePanels(this.mManifestations, <any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(postFilter)));
        }

        public getVisiblePanels(postFilter?: any): com.vzome.core.editor.api.Manifestations.PanelIterator {
            if (((typeof postFilter === 'function' && (<any>postFilter).length === 1) || postFilter === null)) {
                return <any>this.getVisiblePanels$java_util_function_Predicate(postFilter);
            } else if (postFilter === undefined) {
                return <any>this.getVisiblePanels$();
            } else throw new Error('invalid overload');
        }
    }
    ChangeManifestations["__class"] = "com.vzome.core.editor.api.ChangeManifestations";


    export namespace ChangeManifestations {

        export class ManifestConstruction implements com.vzome.core.editor.api.SideEffect {
            public __parent: any;
            mManifestation: com.vzome.core.model.Manifestation;

            mConstruction: com.vzome.core.construction.Construction;

            mShowing: boolean;

            public constructor(__parent: any, construction: com.vzome.core.construction.Construction, manifestation: com.vzome.core.model.Manifestation, showing: boolean) {
                this.__parent = __parent;
                if (this.mManifestation === undefined) { this.mManifestation = null; }
                if (this.mConstruction === undefined) { this.mConstruction = null; }
                if (this.mShowing === undefined) { this.mShowing = false; }
                this.mConstruction = construction;
                this.mManifestation = manifestation;
                this.mShowing = showing;
            }

            /**
             * 
             */
            public redo() {
                if (this.mShowing){
                    if (this.mManifestation.isUnnecessary()){
                        this.mManifestation.addConstruction(this.mConstruction);
                        this.__parent.mManifestations.add(this.mManifestation);
                    }
                    this.__parent.mManifestations.show(this.mManifestation);
                } else {
                    this.mManifestation.removeConstruction(this.mConstruction);
                    if (this.mManifestation.isUnnecessary()){
                        this.__parent.mManifestations.hide(this.mManifestation);
                        this.__parent.mManifestations.remove(this.mManifestation);
                    }
                }
            }

            /**
             * 
             */
            public undo() {
                if (this.mShowing){
                    this.mManifestation.removeConstruction(this.mConstruction);
                    if (this.mManifestation.isUnnecessary()){
                        this.__parent.mManifestations.hide(this.mManifestation);
                        this.__parent.mManifestations.remove(this.mManifestation);
                    }
                } else {
                    if (this.mManifestation.isUnnecessary())this.__parent.mManifestations.add(this.mManifestation);
                    this.__parent.mManifestations.show(this.mManifestation);
                    this.mManifestation.addConstruction(this.mConstruction);
                }
            }

            /**
             * 
             * @param {*} doc
             * @return {*}
             */
            public getXml(doc: org.w3c.dom.Document): org.w3c.dom.Element {
                const result: org.w3c.dom.Element = this.mShowing ? doc.createElement("mshow") : doc.createElement("mhide");
                const man: org.w3c.dom.Element = this.mConstruction.getXml(doc);
                result.appendChild(man);
                return result;
            }

            public showsManifestation(man: com.vzome.core.model.Manifestation): boolean {
                return this.mShowing && /* equals */(<any>((o1: any, o2: any) => { if (o1 && o1.equals) { return o1.equals(o2); } else { return o1 === o2; } })(this.mManifestation,man));
            }
        }
        ManifestConstruction["__class"] = "com.vzome.core.editor.api.ChangeManifestations.ManifestConstruction";
        ManifestConstruction["__interfaces"] = ["com.vzome.core.editor.api.SideEffect"];



        export class RenderManifestation implements com.vzome.core.editor.api.SideEffect {
            public __parent: any;
            mManifestation: com.vzome.core.model.Manifestation;

            mShowing: boolean;

            public constructor(__parent: any, manifestation: com.vzome.core.model.Manifestation, showing: boolean) {
                this.__parent = __parent;
                if (this.mManifestation === undefined) { this.mManifestation = null; }
                if (this.mShowing === undefined) { this.mShowing = false; }
                this.mManifestation = manifestation;
                this.mShowing = showing;
            }

            /**
             * 
             */
            public redo() {
                this.mManifestation.setHidden(!this.mShowing);
                if (this.mShowing)this.__parent.mManifestations.show(this.mManifestation); else this.__parent.mManifestations.hide(this.mManifestation);
            }

            /**
             * 
             */
            public undo() {
                this.mManifestation.setHidden(this.mShowing);
                if (this.mShowing)this.__parent.mManifestations.hide(this.mManifestation); else this.__parent.mManifestations.show(this.mManifestation);
            }

            /**
             * 
             * @param {*} doc
             * @return {*}
             */
            public getXml(doc: org.w3c.dom.Document): org.w3c.dom.Element {
                const result: org.w3c.dom.Element = this.mShowing ? doc.createElement("show") : doc.createElement("hide");
                const man: org.w3c.dom.Element = this.mManifestation.getXml(doc);
                result.appendChild(man);
                return result;
            }

            public showsManifestation(man: com.vzome.core.model.Manifestation): boolean {
                return this.mShowing && /* equals */(<any>((o1: any, o2: any) => { if (o1 && o1.equals) { return o1.equals(o2); } else { return o1 === o2; } })(this.mManifestation,man));
            }
        }
        RenderManifestation["__class"] = "com.vzome.core.editor.api.ChangeManifestations.RenderManifestation";
        RenderManifestation["__interfaces"] = ["com.vzome.core.editor.api.SideEffect"];



        export class DeleteManifestation implements com.vzome.core.editor.api.SideEffect {
            public __parent: any;
            mManifestation: com.vzome.core.model.Manifestation;

            public constructor(__parent: any, manifestation: com.vzome.core.model.Manifestation) {
                this.__parent = __parent;
                if (this.mManifestation === undefined) { this.mManifestation = null; }
                this.mManifestation = manifestation;
            }

            /**
             * 
             */
            public redo() {
                this.mManifestation.setHidden(true);
                this.__parent.mManifestations.hide(this.mManifestation);
                this.__parent.mManifestations.remove(this.mManifestation);
            }

            /**
             * 
             */
            public undo() {
                this.__parent.mManifestations.add(this.mManifestation);
                this.__parent.mManifestations.show(this.mManifestation);
                this.mManifestation.setHidden(false);
            }

            /**
             * 
             * @param {*} doc
             * @return {*}
             */
            public getXml(doc: org.w3c.dom.Document): org.w3c.dom.Element {
                const result: org.w3c.dom.Element = doc.createElement("delete");
                const man: org.w3c.dom.Element = this.mManifestation.getXml(doc);
                result.appendChild(man);
                return result;
            }
        }
        DeleteManifestation["__class"] = "com.vzome.core.editor.api.ChangeManifestations.DeleteManifestation";
        DeleteManifestation["__interfaces"] = ["com.vzome.core.editor.api.SideEffect"];



        export class ColorManifestation implements com.vzome.core.editor.api.SideEffect {
            public __parent: any;
            mManifestation: com.vzome.core.model.Manifestation;

            oldColor: com.vzome.core.construction.Color;

            newColor: com.vzome.core.construction.Color;

            public constructor(__parent: any, manifestation: com.vzome.core.model.Manifestation, color: com.vzome.core.construction.Color) {
                this.__parent = __parent;
                if (this.mManifestation === undefined) { this.mManifestation = null; }
                if (this.oldColor === undefined) { this.oldColor = null; }
                if (this.newColor === undefined) { this.newColor = null; }
                this.mManifestation = manifestation;
                this.newColor = color;
                this.oldColor = manifestation.getColor();
            }

            /**
             * 
             */
            public redo() {
                this.__parent.mManifestations.setColor(this.mManifestation, this.newColor);
            }

            /**
             * 
             */
            public undo() {
                this.__parent.mManifestations.setColor(this.mManifestation, this.oldColor);
            }

            /**
             * 
             * @param {*} doc
             * @return {*}
             */
            public getXml(doc: org.w3c.dom.Document): org.w3c.dom.Element {
                const result: org.w3c.dom.Element = doc.createElement("color");
                com.vzome.xml.DomUtils.addAttribute(result, "rgb", this.newColor.toString());
                const man: org.w3c.dom.Element = this.mManifestation.getXml(doc);
                result.appendChild(man);
                return result;
            }
        }
        ColorManifestation["__class"] = "com.vzome.core.editor.api.ChangeManifestations.ColorManifestation";
        ColorManifestation["__interfaces"] = ["com.vzome.core.editor.api.SideEffect"];



        export class LabelManifestation implements com.vzome.core.editor.api.SideEffect {
            public __parent: any;
            mManifestation: com.vzome.core.model.Manifestation;

            oldLabel: string;

            newLabel: string;

            public constructor(__parent: any, m: com.vzome.core.model.Manifestation, label: string) {
                this.__parent = __parent;
                if (this.mManifestation === undefined) { this.mManifestation = null; }
                if (this.oldLabel === undefined) { this.oldLabel = null; }
                if (this.newLabel === undefined) { this.newLabel = null; }
                this.mManifestation = m;
                this.newLabel = label;
                this.oldLabel = m.getLabel();
            }

            /**
             * 
             */
            public undo() {
                this.__parent.mManifestations.setLabel(this.mManifestation, this.oldLabel);
            }

            /**
             * 
             * @param {*} doc
             * @return {*}
             */
            public getXml(doc: org.w3c.dom.Document): org.w3c.dom.Element {
                const result: org.w3c.dom.Element = doc.createElement("label");
                com.vzome.xml.DomUtils.addAttribute(result, "text", this.newLabel);
                const man: org.w3c.dom.Element = this.mManifestation.getXml(doc);
                result.appendChild(man);
                return result;
            }

            /**
             * 
             */
            public redo() {
                this.__parent.mManifestations.setLabel(this.mManifestation, this.newLabel);
            }
        }
        LabelManifestation["__class"] = "com.vzome.core.editor.api.ChangeManifestations.LabelManifestation";
        LabelManifestation["__interfaces"] = ["com.vzome.core.editor.api.SideEffect"];


    }

}


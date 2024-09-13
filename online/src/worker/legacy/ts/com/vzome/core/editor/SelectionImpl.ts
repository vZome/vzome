/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.editor {
    /**
     * @author Scott Vorthmann
     * @class
     */
    export class SelectionImpl implements com.vzome.core.editor.api.Selection {
        /*private*/ mManifestations: java.util.Collection<com.vzome.core.model.Manifestation>;

        /*private*/ mListeners: java.util.List<com.vzome.core.model.ManifestationChanges>;

        /*private*/ mSelectedGroup: com.vzome.core.model.Group;

        static logger: java.util.logging.Logger; public static logger_$LI$(): java.util.logging.Logger { if (SelectionImpl.logger == null) { SelectionImpl.logger = java.util.logging.Logger.getLogger("com.vzome.core.editor.selection"); }  return SelectionImpl.logger; }

        /**
         * 
         * @param {*} target
         */
        public copy(target: java.util.List<com.vzome.core.model.Manifestation>) {
            target.addAll(this.mManifestations);
        }

        public addListener(listener: com.vzome.core.model.ManifestationChanges) {
            this.mListeners.add(listener);
        }

        public removeListener(listener: com.vzome.core.model.ManifestationChanges) {
            this.mListeners.remove(listener);
        }

        /**
         * 
         * @param {*} m
         * @return {boolean}
         */
        public manifestationSelected(m: com.vzome.core.model.Manifestation): boolean {
            return this.mManifestations.contains(m);
        }

        public isEmpty(): boolean {
            return this.mManifestations.isEmpty();
        }

        /**
         * 
         * @return {*}
         */
        public iterator(): java.util.Iterator<com.vzome.core.model.Manifestation> {
            return this.mManifestations.iterator();
        }

        /**
         * 
         * @param {*} m
         */
        public select(m: com.vzome.core.model.Manifestation) {
            if (this.mManifestations.contains(m))return;
            this.mManifestations.add(m);
            if (SelectionImpl.logger_$LI$().isLoggable(java.util.logging.Level.FINER))SelectionImpl.logger_$LI$().finer("  select: " + m.toString());
            for(let index=this.mListeners.iterator();index.hasNext();) {
                let mc = index.next();
                {
                    mc.manifestationAdded(m);
                }
            }
        }

        /**
         * 
         * @param {*} m
         */
        public unselect(m: com.vzome.core.model.Manifestation) {
            if (this.mManifestations.remove(m)){
                if (SelectionImpl.logger_$LI$().isLoggable(java.util.logging.Level.FINER))SelectionImpl.logger_$LI$().finer("deselect: " + m.toString());
                for(let index=this.mListeners.iterator();index.hasNext();) {
                    let mc = index.next();
                    {
                        mc.manifestationRemoved(m);
                    }
                }
            }
        }

        /**
         * 
         * @param {*} m
         */
        public selectWithGrouping(m: com.vzome.core.model.Manifestation) {
            if (this.mManifestations.contains(m))return;
            if (m == null)return;
            const group: com.vzome.core.model.Group = com.vzome.core.editor.api.Selection.biggestGroup(m);
            if (group == null)this.add(m); else this.selectGroup(group);
            this.mSelectedGroup = group;
        }

        /**
         * 
         * @param {*} m
         */
        public unselectWithGrouping(m: com.vzome.core.model.Manifestation) {
            if (this.mManifestations.contains(m)){
                const group: com.vzome.core.model.Group = com.vzome.core.editor.api.Selection.biggestGroup(m);
                if (group == null)this.remove(m); else this.unselectGroup(group);
                this.mSelectedGroup = null;
            }
        }

        /*private*/ add(m: com.vzome.core.model.Manifestation) {
            this.mManifestations.add(m);
            if (SelectionImpl.logger_$LI$().isLoggable(java.util.logging.Level.FINER))SelectionImpl.logger_$LI$().finer("  select: " + m.toString());
            for(let index=this.mListeners.iterator();index.hasNext();) {
                let mc = index.next();
                {
                    mc.manifestationAdded(m);
                }
            }
        }

        /*private*/ remove(m: com.vzome.core.model.Manifestation) {
            if (this.mManifestations.remove(m)){
                if (SelectionImpl.logger_$LI$().isLoggable(java.util.logging.Level.FINER))SelectionImpl.logger_$LI$().finer("deselect: " + m.toString());
                for(let index=this.mListeners.iterator();index.hasNext();) {
                    let mc = index.next();
                    {
                        mc.manifestationRemoved(m);
                    }
                }
            }
        }

        /*private*/ selectGroup(group: com.vzome.core.model.Group) {
            for(let index=group.iterator();index.hasNext();) {
                let next = index.next();
                {
                    if (next != null && next instanceof <any>com.vzome.core.model.Group)this.selectGroup(<com.vzome.core.model.Group><any>next); else this.add(<com.vzome.core.model.Manifestation><any>next);
                }
            }
        }

        /*private*/ unselectGroup(group: com.vzome.core.model.Group) {
            for(let index=group.iterator();index.hasNext();) {
                let next = index.next();
                {
                    if (next != null && next instanceof <any>com.vzome.core.model.Group)this.unselectGroup(<com.vzome.core.model.Group><any>next); else this.remove(<com.vzome.core.model.Manifestation><any>next);
                }
            }
        }

        /**
         * 
         * @param {java.lang.Class} kind
         * @return {*}
         */
        public getSingleSelection(kind: any): com.vzome.core.model.Manifestation {
            let count: number = 0;
            let result: com.vzome.core.model.Manifestation = null;
            for(let index=this.mManifestations.iterator();index.hasNext();) {
                let next = index.next();
                {
                    if ((kind === "com.vzome.core.model.Connector" && (next != null && (next.constructor != null && next.constructor["__interfaces"] != null && next.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0))) || (kind === "com.vzome.core.model.Strut" && (next != null && (next.constructor != null && next.constructor["__interfaces"] != null && next.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0))) || (kind === "com.vzome.core.model.Panel" && (next != null && (next.constructor != null && next.constructor["__interfaces"] != null && next.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)))){
                        ++count;
                        result = next;
                    }
                }
            }
            if (count === 1)return result; else return null;
        }

        /**
         * 
         * @return {boolean}
         */
        public isSelectionAGroup(): boolean {
            return this.getSelectedGroup(false) != null;
        }

        /*private*/ getSelectedGroup(onlyOne: boolean): com.vzome.core.model.Group {
            let selectedGroup: com.vzome.core.model.Group = null;
            for(let index=this.mManifestations.iterator();index.hasNext();) {
                let m = index.next();
                {
                    if (onlyOne && (selectedGroup != null))return selectedGroup;
                    const group: com.vzome.core.model.Group = com.vzome.core.editor.api.Selection.biggestGroup(m);
                    if (group == null)return null; else if (selectedGroup == null)selectedGroup = group; else if (group !== selectedGroup)return null;
                }
            }
            return selectedGroup;
        }

        /**
         * 
         */
        public gatherGroup() {
            const newGroup: com.vzome.core.model.Group = new com.vzome.core.model.Group();
            for(let index=this.mManifestations.iterator();index.hasNext();) {
                let m = index.next();
                {
                    const group: com.vzome.core.model.Group = com.vzome.core.editor.api.Selection.biggestGroup(m);
                    if (group === newGroup); else if (group == null){
                        newGroup.add(m);
                        m.setContainer(newGroup);
                    } else {
                        newGroup.add(group);
                        group.setContainer(newGroup);
                    }
                }
            }
        }

        /**
         * 
         */
        public scatterGroup() {
            const selectedGroup: com.vzome.core.model.Group = this.getSelectedGroup(true);
            if (selectedGroup == null)return;
            for(const ms: java.util.Iterator<com.vzome.core.model.GroupElement> = selectedGroup.iterator(); ms.hasNext(); ) {{
                const next: com.vzome.core.model.GroupElement = ms.next();
                ms.remove();
                next.setContainer(null);
            };}
        }

        /**
         * 
         */
        public gatherGroup211() {
            if (this.mSelectedGroup != null)return;
            this.mSelectedGroup = new com.vzome.core.model.Group();
            for(let index=this.mManifestations.iterator();index.hasNext();) {
                let m = index.next();
                {
                    const group: com.vzome.core.model.Group = com.vzome.core.editor.api.Selection.biggestGroup(m);
                    if (group == null){
                        this.mSelectedGroup.add(m);
                    } else {
                        this.mSelectedGroup.add(group);
                    }
                }
            }
            for(let index=this.mSelectedGroup.iterator();index.hasNext();) {
                let next = index.next();
                {
                    next.setContainer(this.mSelectedGroup);
                }
            }
        }

        /**
         * 
         */
        public scatterGroup211() {
            if (this.mSelectedGroup == null)return;
            for(const ms: java.util.Iterator<com.vzome.core.model.GroupElement> = this.mSelectedGroup.iterator(); ms.hasNext(); ) {{
                const next: com.vzome.core.model.GroupElement = ms.next();
                ms.remove();
                next.setContainer(null);
            };}
        }

        public refresh(on: boolean, otherSelection: SelectionImpl) {
            for(let index=this.mManifestations.iterator();index.hasNext();) {
                let m = index.next();
                {
                    if (otherSelection == null || !otherSelection.mManifestations.contains(m)){
                        if (on){
                            for(let index=this.mListeners.iterator();index.hasNext();) {
                                let mc = index.next();
                                {
                                    mc.manifestationAdded(m);
                                }
                            }
                        } else {
                            for(let index=this.mListeners.iterator();index.hasNext();) {
                                let mc = index.next();
                                {
                                    mc.manifestationRemoved(m);
                                }
                            }
                        }
                    }
                }
            }
        }

        /**
         * 
         * @return {number}
         */
        public size(): number {
            return this.mManifestations.size();
        }

        /**
         * 
         */
        public clear() {
            if (!this.mManifestations.isEmpty()){
                if (SelectionImpl.logger_$LI$().isLoggable(java.util.logging.Level.FINER)){
                    SelectionImpl.logger_$LI$().finer("clearing selection");
                }
                const temp: java.util.Collection<com.vzome.core.model.Manifestation> = <any>(new java.util.LinkedHashSet<any>(this.mManifestations));
                for(let index=temp.iterator();index.hasNext();) {
                    let m = index.next();
                    {
                        this.unselect(m);
                    }
                }
            }
        }

        constructor() {
            this.mManifestations = <any>(new java.util.LinkedHashSet<any>());
            this.mListeners = <any>(new java.util.ArrayList<any>());
            this.mSelectedGroup = null;
        }
    }
    SelectionImpl["__class"] = "com.vzome.core.editor.SelectionImpl";
    SelectionImpl["__interfaces"] = ["com.vzome.core.editor.api.Selection","java.lang.Iterable"];


}


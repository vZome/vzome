import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Selection } from "./api/Selection.js";
import { Group } from "../model/Group.js";
import { GroupElement } from "../model/GroupElement.js";
import { Manifestation } from "../model/Manifestation.js";
import { ManifestationChanges } from "../model/ManifestationChanges.js";

/**
 * @author Scott Vorthmann
 * @class
 */
export class SelectionImpl implements Selection {
    /*private*/ mManifestations: java.util.Collection<Manifestation>;

    /*private*/ mListeners: java.util.List<ManifestationChanges>;

    /*private*/ mSelectedGroup: Group;

    static logger: java.util.logging.Logger; public static logger_$LI$(): java.util.logging.Logger { if (SelectionImpl.logger == null) { SelectionImpl.logger = java.util.logging.Logger.getLogger("com.vzome.core.editor.selection"); }  return SelectionImpl.logger; }

    /**
     * 
     * @param {*} target
     */
    public copy(target: java.util.List<Manifestation>) {
        target.addAll(this.mManifestations);
    }

    public addListener(listener: ManifestationChanges) {
        this.mListeners.add(listener);
    }

    public removeListener(listener: ManifestationChanges) {
        this.mListeners.remove(listener);
    }

    /**
     * 
     * @param {*} m
     * @return {boolean}
     */
    public manifestationSelected(m: Manifestation): boolean {
        return this.mManifestations.contains(m);
    }

    public isEmpty(): boolean {
        return this.mManifestations.isEmpty();
    }

    /**
     * 
     * @return {*}
     */
    public iterator(): java.util.Iterator<Manifestation> {
        return this.mManifestations.iterator();
    }

    /**
     * 
     * @param {*} m
     */
    public select(m: Manifestation) {
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
    public unselect(m: Manifestation) {
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
    public selectWithGrouping(m: Manifestation) {
        if (this.mManifestations.contains(m))return;
        if (m == null)return;
        const group: Group = Selection.biggestGroup(m);
        if (group == null)this.add(m); else this.selectGroup(group);
        this.mSelectedGroup = group;
    }

    /**
     * 
     * @param {*} m
     */
    public unselectWithGrouping(m: Manifestation) {
        if (this.mManifestations.contains(m)){
            const group: Group = Selection.biggestGroup(m);
            if (group == null)this.remove(m); else this.unselectGroup(group);
            this.mSelectedGroup = null;
        }
    }

    /*private*/ add(m: Manifestation) {
        this.mManifestations.add(m);
        if (SelectionImpl.logger_$LI$().isLoggable(java.util.logging.Level.FINER))SelectionImpl.logger_$LI$().finer("  select: " + m.toString());
        for(let index=this.mListeners.iterator();index.hasNext();) {
            let mc = index.next();
            {
                mc.manifestationAdded(m);
            }
        }
    }

    /*private*/ remove(m: Manifestation) {
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

    /*private*/ selectGroup(group: Group) {
        for(let index=group.iterator();index.hasNext();) {
            let next = index.next();
            {
                if (next != null && next instanceof <any>Group)this.selectGroup(<Group><any>next); else this.add(<Manifestation><any>next);
            }
        }
    }

    /*private*/ unselectGroup(group: Group) {
        for(let index=group.iterator();index.hasNext();) {
            let next = index.next();
            {
                if (next != null && next instanceof <any>Group)this.unselectGroup(<Group><any>next); else this.remove(<Manifestation><any>next);
            }
        }
    }

    /**
     * 
     * @param {java.lang.Class} kind
     * @return {*}
     */
    public getSingleSelection(kind: any): Manifestation {
        let count: number = 0;
        let result: Manifestation = null;
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

    /*private*/ getSelectedGroup(onlyOne: boolean): Group {
        let selectedGroup: Group = null;
        for(let index=this.mManifestations.iterator();index.hasNext();) {
            let m = index.next();
            {
                if (onlyOne && (selectedGroup != null))return selectedGroup;
                const group: Group = Selection.biggestGroup(m);
                if (group == null)return null; else if (selectedGroup == null)selectedGroup = group; else if (group !== selectedGroup)return null;
            }
        }
        return selectedGroup;
    }

    /**
     * 
     */
    public gatherGroup() {
        const newGroup: Group = new Group();
        for(let index=this.mManifestations.iterator();index.hasNext();) {
            let m = index.next();
            {
                const group: Group = Selection.biggestGroup(m);
                if (group === newGroup) {} else if (group == null){
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
        const selectedGroup: Group = this.getSelectedGroup(true);
        if (selectedGroup == null)return;
        for(const ms: java.util.Iterator<GroupElement> = selectedGroup.iterator(); ms.hasNext(); ) {{
            const next: GroupElement = ms.next();
            ms.remove();
            next.setContainer(null);
        };}
    }

    /**
     * 
     */
    public gatherGroup211() {
        if (this.mSelectedGroup != null)return;
        this.mSelectedGroup = new Group();
        for(let index=this.mManifestations.iterator();index.hasNext();) {
            let m = index.next();
            {
                const group: Group = Selection.biggestGroup(m);
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
        for(const ms: java.util.Iterator<GroupElement> = this.mSelectedGroup.iterator(); ms.hasNext(); ) {{
            const next: GroupElement = ms.next();
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
            const temp: java.util.Collection<Manifestation> = <any>(new java.util.LinkedHashSet<any>(this.mManifestations));
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

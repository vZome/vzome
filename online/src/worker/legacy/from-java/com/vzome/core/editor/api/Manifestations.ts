import { java, javaemul } from "../../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicVector } from "../../algebra/AlgebraicVector.js";
import { FilteredIterator } from "../../generic/FilteredIterator.js";
import { Connector } from "../../model/Connector.js";
import { Manifestation } from "../../model/Manifestation.js";
import { Panel } from "../../model/Panel.js";
import { Strut } from "../../model/Strut.js";

/**
 * @author David Hall
 * @class
 */
export class Manifestations {
    public static visibleManifestations$java_lang_Iterable(manifestations: java.lang.Iterable<Manifestation>): Manifestations.ManifestationIterator {
        return new Manifestations.ManifestationIterator((man) => { return Manifestations.Filters.isVisible(man) }, manifestations, <any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(null)));
    }

    public static visibleManifestations$java_util_function_Predicate$java_lang_Iterable(preTest: (p1: Manifestation) => boolean, manifestations: java.lang.Iterable<Manifestation>): Manifestations.ManifestationIterator {
        return new Manifestations.ManifestationIterator(<any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(preTest)), manifestations, (man) => { return Manifestations.Filters.isVisible(man) });
    }

    public static visibleManifestations(preTest?: any, manifestations?: any): Manifestations.ManifestationIterator {
        if (((typeof preTest === 'function' && (<any>preTest).length === 1) || preTest === null) && ((manifestations != null && (manifestations.constructor != null && manifestations.constructor["__interfaces"] != null && manifestations.constructor["__interfaces"].indexOf("java.lang.Iterable") >= 0)) || manifestations === null)) {
            return <any>Manifestations.visibleManifestations$java_util_function_Predicate$java_lang_Iterable(preTest, manifestations);
        } else if (((preTest != null && (preTest.constructor != null && preTest.constructor["__interfaces"] != null && preTest.constructor["__interfaces"].indexOf("java.lang.Iterable") >= 0)) || preTest === null) && ((typeof manifestations === 'function' && (<any>manifestations).length === 1) || manifestations === null)) {
            return <any>Manifestations.visibleManifestations$java_lang_Iterable$java_util_function_Predicate(preTest, manifestations);
        } else if (((preTest != null && (preTest.constructor != null && preTest.constructor["__interfaces"] != null && preTest.constructor["__interfaces"].indexOf("java.lang.Iterable") >= 0)) || preTest === null) && manifestations === undefined) {
            return <any>Manifestations.visibleManifestations$java_lang_Iterable(preTest);
        } else throw new Error('invalid overload');
    }

    public static visibleManifestations$java_lang_Iterable$java_util_function_Predicate(manifestations: java.lang.Iterable<Manifestation>, postTest: (p1: Manifestation) => boolean): Manifestations.ManifestationIterator {
        return new Manifestations.ManifestationIterator((man) => { return Manifestations.Filters.isVisible(man) }, manifestations, <any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(postTest)));
    }

    public static getConnectors$java_lang_Iterable(manifestations: java.lang.Iterable<Manifestation>): Manifestations.ConnectorIterator {
        return new Manifestations.ConnectorIterator(<any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(null)), manifestations, <any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(null)));
    }

    public static getConnectors$java_lang_Iterable$java_util_function_Predicate(manifestations: java.lang.Iterable<Manifestation>, postFilter: (p1: Connector) => boolean): Manifestations.ConnectorIterator {
        return new Manifestations.ConnectorIterator(<any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(null)), manifestations, <any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(postFilter)));
    }

    public static getConnectors$java_util_function_Predicate$java_lang_Iterable$java_util_function_Predicate(preFilter: (p1: Manifestation) => boolean, manifestations: java.lang.Iterable<Manifestation>, postFilter: (p1: Connector) => boolean): Manifestations.ConnectorIterator {
        return new Manifestations.ConnectorIterator(<any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(preFilter)), manifestations, <any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(postFilter)));
    }

    public static getConnectors(preFilter?: any, manifestations?: any, postFilter?: any): Manifestations.ConnectorIterator {
        if (((typeof preFilter === 'function' && (<any>preFilter).length === 1) || preFilter === null) && ((manifestations != null && (manifestations.constructor != null && manifestations.constructor["__interfaces"] != null && manifestations.constructor["__interfaces"].indexOf("java.lang.Iterable") >= 0)) || manifestations === null) && ((typeof postFilter === 'function' && (<any>postFilter).length === 1) || postFilter === null)) {
            return <any>Manifestations.getConnectors$java_util_function_Predicate$java_lang_Iterable$java_util_function_Predicate(preFilter, manifestations, postFilter);
        } else if (((preFilter != null && (preFilter.constructor != null && preFilter.constructor["__interfaces"] != null && preFilter.constructor["__interfaces"].indexOf("java.lang.Iterable") >= 0)) || preFilter === null) && ((typeof manifestations === 'function' && (<any>manifestations).length === 1) || manifestations === null) && postFilter === undefined) {
            return <any>Manifestations.getConnectors$java_lang_Iterable$java_util_function_Predicate(preFilter, manifestations);
        } else if (((preFilter != null && (preFilter.constructor != null && preFilter.constructor["__interfaces"] != null && preFilter.constructor["__interfaces"].indexOf("java.lang.Iterable") >= 0)) || preFilter === null) && manifestations === undefined && postFilter === undefined) {
            return <any>Manifestations.getConnectors$java_lang_Iterable(preFilter);
        } else throw new Error('invalid overload');
    }

    public static getVisibleConnectors(manifestations: java.lang.Iterable<Manifestation>, postFilter: (p1: Connector) => boolean = null): Manifestations.ConnectorIterator {
        return new Manifestations.ConnectorIterator((man) => { return Manifestations.Filters.isVisible(man) }, manifestations, <any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(postFilter)));
    }

    public static getHiddenConnectors(manifestations: java.lang.Iterable<Manifestation>, postFilter: (p1: Connector) => boolean = null): Manifestations.ConnectorIterator {
        return new Manifestations.ConnectorIterator((man) => { return Manifestations.Filters.isHidden(man) }, manifestations, <any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(postFilter)));
    }

    public static getStruts$java_lang_Iterable(manifestations: java.lang.Iterable<Manifestation>): Manifestations.StrutIterator {
        return new Manifestations.StrutIterator(<any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(null)), manifestations, <any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(null)));
    }

    public static getStruts$java_lang_Iterable$java_util_function_Predicate(manifestations: java.lang.Iterable<Manifestation>, postFilter: (p1: Strut) => boolean): Manifestations.StrutIterator {
        return new Manifestations.StrutIterator(<any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(null)), manifestations, <any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(postFilter)));
    }

    public static getStruts$java_util_function_Predicate$java_lang_Iterable$java_util_function_Predicate(preFilter: (p1: Manifestation) => boolean, manifestations: java.lang.Iterable<Manifestation>, postFilter: (p1: Strut) => boolean): Manifestations.StrutIterator {
        return new Manifestations.StrutIterator(<any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(preFilter)), manifestations, <any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(postFilter)));
    }

    public static getStruts(preFilter?: any, manifestations?: any, postFilter?: any): Manifestations.StrutIterator {
        if (((typeof preFilter === 'function' && (<any>preFilter).length === 1) || preFilter === null) && ((manifestations != null && (manifestations.constructor != null && manifestations.constructor["__interfaces"] != null && manifestations.constructor["__interfaces"].indexOf("java.lang.Iterable") >= 0)) || manifestations === null) && ((typeof postFilter === 'function' && (<any>postFilter).length === 1) || postFilter === null)) {
            return <any>Manifestations.getStruts$java_util_function_Predicate$java_lang_Iterable$java_util_function_Predicate(preFilter, manifestations, postFilter);
        } else if (((preFilter != null && (preFilter.constructor != null && preFilter.constructor["__interfaces"] != null && preFilter.constructor["__interfaces"].indexOf("java.lang.Iterable") >= 0)) || preFilter === null) && ((typeof manifestations === 'function' && (<any>manifestations).length === 1) || manifestations === null) && postFilter === undefined) {
            return <any>Manifestations.getStruts$java_lang_Iterable$java_util_function_Predicate(preFilter, manifestations);
        } else if (((preFilter != null && (preFilter.constructor != null && preFilter.constructor["__interfaces"] != null && preFilter.constructor["__interfaces"].indexOf("java.lang.Iterable") >= 0)) || preFilter === null) && manifestations === undefined && postFilter === undefined) {
            return <any>Manifestations.getStruts$java_lang_Iterable(preFilter);
        } else throw new Error('invalid overload');
    }

    public static getVisibleStruts(manifestations: java.lang.Iterable<Manifestation>, postFilter: (p1: Strut) => boolean = null): Manifestations.StrutIterator {
        return new Manifestations.StrutIterator((man) => { return Manifestations.Filters.isVisible(man) }, manifestations, <any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(postFilter)));
    }

    public static getHiddenStruts(manifestations: java.lang.Iterable<Manifestation>, postFilter: (p1: Strut) => boolean = null): Manifestations.StrutIterator {
        return new Manifestations.StrutIterator((man) => { return Manifestations.Filters.isHidden(man) }, manifestations, <any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(postFilter)));
    }

    public static getPanels$java_lang_Iterable(manifestations: java.lang.Iterable<Manifestation>): Manifestations.PanelIterator {
        return new Manifestations.PanelIterator(<any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(null)), manifestations, <any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(null)));
    }

    public static getPanels$java_lang_Iterable$java_util_function_Predicate(manifestations: java.lang.Iterable<Manifestation>, postFilter: (p1: Panel) => boolean): Manifestations.PanelIterator {
        return new Manifestations.PanelIterator(<any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(null)), manifestations, <any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(postFilter)));
    }

    public static getPanels$java_util_function_Predicate$java_lang_Iterable$java_util_function_Predicate(preFilter: (p1: Manifestation) => boolean, manifestations: java.lang.Iterable<Manifestation>, postFilter: (p1: Panel) => boolean): Manifestations.PanelIterator {
        return new Manifestations.PanelIterator(<any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(preFilter)), manifestations, <any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(postFilter)));
    }

    public static getPanels(preFilter?: any, manifestations?: any, postFilter?: any): Manifestations.PanelIterator {
        if (((typeof preFilter === 'function' && (<any>preFilter).length === 1) || preFilter === null) && ((manifestations != null && (manifestations.constructor != null && manifestations.constructor["__interfaces"] != null && manifestations.constructor["__interfaces"].indexOf("java.lang.Iterable") >= 0)) || manifestations === null) && ((typeof postFilter === 'function' && (<any>postFilter).length === 1) || postFilter === null)) {
            return <any>Manifestations.getPanels$java_util_function_Predicate$java_lang_Iterable$java_util_function_Predicate(preFilter, manifestations, postFilter);
        } else if (((preFilter != null && (preFilter.constructor != null && preFilter.constructor["__interfaces"] != null && preFilter.constructor["__interfaces"].indexOf("java.lang.Iterable") >= 0)) || preFilter === null) && ((typeof manifestations === 'function' && (<any>manifestations).length === 1) || manifestations === null) && postFilter === undefined) {
            return <any>Manifestations.getPanels$java_lang_Iterable$java_util_function_Predicate(preFilter, manifestations);
        } else if (((preFilter != null && (preFilter.constructor != null && preFilter.constructor["__interfaces"] != null && preFilter.constructor["__interfaces"].indexOf("java.lang.Iterable") >= 0)) || preFilter === null) && manifestations === undefined && postFilter === undefined) {
            return <any>Manifestations.getPanels$java_lang_Iterable(preFilter);
        } else throw new Error('invalid overload');
    }

    public static getVisiblePanels(manifestations: java.lang.Iterable<Manifestation>, postFilter: (p1: Panel) => boolean = null): Manifestations.PanelIterator {
        return new Manifestations.PanelIterator((man) => { return Manifestations.Filters.isVisible(man) }, manifestations, <any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(postFilter)));
    }

    public static getHiddenPanels(manifestations: java.lang.Iterable<Manifestation>, postFilter: (p1: Panel) => boolean = null): Manifestations.PanelIterator {
        return new Manifestations.PanelIterator((man) => { return Manifestations.Filters.isHidden(man) }, manifestations, <any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(postFilter)));
    }

    /**
     * 
     * @param {*} manifestations
     * @param {*} output
     * @return {AlgebraicVector} last selected Connector location, or last selected Strut location, or last vertex of last selected Panel
     */
    public static sortVertices(manifestations: java.lang.Iterable<Manifestation>, output: java.util.SortedSet<AlgebraicVector>): AlgebraicVector {
        let lastBall: AlgebraicVector = null;
        let lastVertex: AlgebraicVector = null;
        for(let index=manifestations.iterator();index.hasNext();) {
            let man = index.next();
            {
                if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                    lastBall = man.getLocation();
                    output.add(lastBall);
                } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                    lastVertex = man.getLocation();
                    output.add(lastVertex);
                    output.add((<Strut><any>man).getEnd());
                } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                    for(let index=(<Panel><any>man).iterator();index.hasNext();) {
                        let vertex = index.next();
                        {
                            lastVertex = vertex;
                            output.add(vertex);
                        }
                    }
                }
            }
        }
        return (lastBall != null) ? lastBall : lastVertex;
    }
}
Manifestations["__class"] = "com.vzome.core.editor.api.Manifestations";


export namespace Manifestations {

    export class ManifestationIterator extends FilteredIterator<Manifestation, Manifestation> {
        public constructor(preTest: (p1: Manifestation) => boolean, manifestations: java.lang.Iterable<Manifestation>, postTest: (p1: Manifestation) => boolean) {
            super(<any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(preTest)), manifestations, <any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(postTest)));
        }

        public apply$com_vzome_core_model_Manifestation(element: Manifestation): Manifestation {
            return element;
        }

        /**
         * 
         * @param {*} element
         * @return {*}
         */
        public apply(element?: any): any {
            if (((element != null && (element.constructor != null && element.constructor["__interfaces"] != null && element.constructor["__interfaces"].indexOf("com.vzome.core.model.Manifestation") >= 0)) || element === null)) {
                return <any>this.apply$com_vzome_core_model_Manifestation(element);
            } else if (((element != null) || element === null)) {
                 throw new Error('cannot invoke abstract overloaded method... check your argument(s) type(s)'); 
            } else throw new Error('invalid overload');
        }
    }
    ManifestationIterator["__class"] = "com.vzome.core.editor.api.Manifestations.ManifestationIterator";
    ManifestationIterator["__interfaces"] = ["java.util.Iterator","java.lang.Iterable"];



    export class ConnectorIterator extends FilteredIterator<Manifestation, Connector> {
        public preFilter$com_vzome_core_model_Manifestation(element: Manifestation): boolean {
            return (element != null && (element != null && (element.constructor != null && element.constructor["__interfaces"] != null && element.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0))) ? super.preFilter(element) : false;
        }

        /**
         * 
         * @param {*} element
         * @return {boolean}
         */
        public preFilter(element?: any): boolean {
            if (((element != null && (element.constructor != null && element.constructor["__interfaces"] != null && element.constructor["__interfaces"].indexOf("com.vzome.core.model.Manifestation") >= 0)) || element === null)) {
                return <any>this.preFilter$com_vzome_core_model_Manifestation(element);
            } else if (((element != null) || element === null)) {
                return super.preFilter(element);
            } else throw new Error('invalid overload');
        }

        public apply$com_vzome_core_model_Manifestation(element: Manifestation): Connector {
            return <Connector><any>element;
        }

        /**
         * 
         * @param {*} element
         * @return {*}
         */
        public apply(element?: any): any {
            if (((element != null && (element.constructor != null && element.constructor["__interfaces"] != null && element.constructor["__interfaces"].indexOf("com.vzome.core.model.Manifestation") >= 0)) || element === null)) {
                return <any>this.apply$com_vzome_core_model_Manifestation(element);
            } else if (((element != null) || element === null)) {
                 throw new Error('cannot invoke abstract overloaded method... check your argument(s) type(s)'); 
            } else throw new Error('invalid overload');
        }

        constructor(preFilter: (p1: Manifestation) => boolean, manifestations: java.lang.Iterable<Manifestation>, postFilter: (p1: Connector) => boolean) {
            super(<any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(preFilter)), manifestations, <any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(postFilter)));
        }
    }
    ConnectorIterator["__class"] = "com.vzome.core.editor.api.Manifestations.ConnectorIterator";
    ConnectorIterator["__interfaces"] = ["java.util.Iterator","java.lang.Iterable"];



    export class StrutIterator extends FilteredIterator<Manifestation, Strut> {
        public preFilter$com_vzome_core_model_Manifestation(element: Manifestation): boolean {
            return (element != null && (element != null && (element.constructor != null && element.constructor["__interfaces"] != null && element.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0))) ? super.preFilter(element) : false;
        }

        /**
         * 
         * @param {*} element
         * @return {boolean}
         */
        public preFilter(element?: any): boolean {
            if (((element != null && (element.constructor != null && element.constructor["__interfaces"] != null && element.constructor["__interfaces"].indexOf("com.vzome.core.model.Manifestation") >= 0)) || element === null)) {
                return <any>this.preFilter$com_vzome_core_model_Manifestation(element);
            } else if (((element != null) || element === null)) {
                return super.preFilter(element);
            } else throw new Error('invalid overload');
        }

        public apply$com_vzome_core_model_Manifestation(element: Manifestation): Strut {
            return <Strut><any>element;
        }

        /**
         * 
         * @param {*} element
         * @return {*}
         */
        public apply(element?: any): any {
            if (((element != null && (element.constructor != null && element.constructor["__interfaces"] != null && element.constructor["__interfaces"].indexOf("com.vzome.core.model.Manifestation") >= 0)) || element === null)) {
                return <any>this.apply$com_vzome_core_model_Manifestation(element);
            } else if (((element != null) || element === null)) {
                 throw new Error('cannot invoke abstract overloaded method... check your argument(s) type(s)'); 
            } else throw new Error('invalid overload');
        }

        constructor(preFilter: (p1: Manifestation) => boolean, manifestations: java.lang.Iterable<Manifestation>, postFilter: (p1: Strut) => boolean) {
            super(<any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(preFilter)), manifestations, <any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(postFilter)));
        }
    }
    StrutIterator["__class"] = "com.vzome.core.editor.api.Manifestations.StrutIterator";
    StrutIterator["__interfaces"] = ["java.util.Iterator","java.lang.Iterable"];



    export class PanelIterator extends FilteredIterator<Manifestation, Panel> {
        public preFilter$com_vzome_core_model_Manifestation(element: Manifestation): boolean {
            return (element != null && (element != null && (element.constructor != null && element.constructor["__interfaces"] != null && element.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0))) ? super.preFilter(element) : false;
        }

        /**
         * 
         * @param {*} element
         * @return {boolean}
         */
        public preFilter(element?: any): boolean {
            if (((element != null && (element.constructor != null && element.constructor["__interfaces"] != null && element.constructor["__interfaces"].indexOf("com.vzome.core.model.Manifestation") >= 0)) || element === null)) {
                return <any>this.preFilter$com_vzome_core_model_Manifestation(element);
            } else if (((element != null) || element === null)) {
                return super.preFilter(element);
            } else throw new Error('invalid overload');
        }

        public apply$com_vzome_core_model_Manifestation(element: Manifestation): Panel {
            return <Panel><any>element;
        }

        /**
         * 
         * @param {*} element
         * @return {*}
         */
        public apply(element?: any): any {
            if (((element != null && (element.constructor != null && element.constructor["__interfaces"] != null && element.constructor["__interfaces"].indexOf("com.vzome.core.model.Manifestation") >= 0)) || element === null)) {
                return <any>this.apply$com_vzome_core_model_Manifestation(element);
            } else if (((element != null) || element === null)) {
                 throw new Error('cannot invoke abstract overloaded method... check your argument(s) type(s)'); 
            } else throw new Error('invalid overload');
        }

        constructor(preFilter: (p1: Manifestation) => boolean, manifestations: java.lang.Iterable<Manifestation>, postFilter: (p1: Panel) => boolean) {
            super(<any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(preFilter)), manifestations, <any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(postFilter)));
        }
    }
    PanelIterator["__class"] = "com.vzome.core.editor.api.Manifestations.PanelIterator";
    PanelIterator["__interfaces"] = ["java.util.Iterator","java.lang.Iterable"];



    export class Filters {
        constructor() {
        }

        public static isRendered(man: Manifestation): boolean {
            return man.isRendered();
        }

        public static isVisible(man: Manifestation): boolean {
            return !man.isHidden();
        }

        public static isHidden(man: Manifestation): boolean {
            return man.isHidden();
        }

        public static is(man: Manifestation): boolean {
            return true;
        }
    }
    Filters["__class"] = "com.vzome.core.editor.api.Manifestations.Filters";

}

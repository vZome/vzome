/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.desktop.controller {
    export class SymmetryController extends com.vzome.desktop.controller.DefaultController {
        /**
         * 
         * @param {string} string
         * @return {string}
         */
        public getProperty(string: string): string {
            switch((string)) {
            case "label":
                return this.label;
            case "name":
                return this.symmetrySystem.getName();
            case "renderingStyle":
                return this.symmetrySystem.getStyle$().getName();
            case "modelResourcePath":
                return this.symmetrySystem.getModelResourcePath();
            default:
                if (/* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(string, "orbitColor.")){
                    const name: string = string.substring("orbitColor.".length);
                    const dir: com.vzome.core.math.symmetry.Direction = this.symmetrySystem.getOrbits().getDirection(name);
                    const color: com.vzome.core.construction.Color = this.getColor(dir);
                    return color.toString();
                }
                return super.getProperty(string);
            }
        }

        /*private*/ symmetrySystem: com.vzome.core.editor.SymmetrySystem;

        public availableOrbits: com.vzome.core.math.symmetry.OrbitSet;

        public snapOrbits: com.vzome.core.math.symmetry.OrbitSet;

        public buildOrbits: com.vzome.core.math.symmetry.OrbitSet;

        public renderOrbits: com.vzome.core.math.symmetry.OrbitSet;

        /*private*/ snapper: com.vzome.desktop.controller.OrbitSnapper;

        public availableController: com.vzome.desktop.controller.OrbitSetController;

        public snapController: com.vzome.desktop.controller.OrbitSetController;

        public buildController: com.vzome.desktop.controller.OrbitSetController;

        public renderController: com.vzome.desktop.controller.OrbitSetController;

        public orbitLengths: java.util.Map<com.vzome.core.math.symmetry.Direction, com.vzome.desktop.api.Controller>;

        /*private*/ symmetryToolFactories: java.util.Map<string, com.vzome.desktop.api.Controller>;

        /*private*/ transformToolFactories: java.util.Map<string, com.vzome.desktop.api.Controller>;

        /*private*/ linearMapToolFactories: java.util.Map<string, com.vzome.desktop.api.Controller>;

        /*private*/ renderedModel: com.vzome.core.render.RenderedModel;

        /*private*/ label: string;

        public getSymmetry(): com.vzome.core.math.symmetry.Symmetry {
            return this.symmetrySystem.getSymmetry();
        }

        public getSnapper(): com.vzome.desktop.controller.OrbitSnapper {
            return this.snapper;
        }

        public constructor(label: string, parent: com.vzome.desktop.api.Controller, model: com.vzome.core.editor.SymmetrySystem, mRenderedModel: com.vzome.core.render.RenderedModel) {
            super();
            if (this.symmetrySystem === undefined) { this.symmetrySystem = null; }
            if (this.availableOrbits === undefined) { this.availableOrbits = null; }
            if (this.snapOrbits === undefined) { this.snapOrbits = null; }
            if (this.buildOrbits === undefined) { this.buildOrbits = null; }
            if (this.renderOrbits === undefined) { this.renderOrbits = null; }
            if (this.snapper === undefined) { this.snapper = null; }
            if (this.availableController === undefined) { this.availableController = null; }
            if (this.snapController === undefined) { this.snapController = null; }
            if (this.buildController === undefined) { this.buildController = null; }
            if (this.renderController === undefined) { this.renderController = null; }
            this.orbitLengths = <any>(new java.util.HashMap<any, any>());
            this.symmetryToolFactories = <any>(new java.util.LinkedHashMap<any, any>());
            this.transformToolFactories = <any>(new java.util.LinkedHashMap<any, any>());
            this.linearMapToolFactories = <any>(new java.util.LinkedHashMap<any, any>());
            if (this.renderedModel === undefined) { this.renderedModel = null; }
            if (this.label === undefined) { this.label = null; }
            this.label = label;
            this.symmetrySystem = model;
            this.renderedModel = mRenderedModel;
            const symmetry: com.vzome.core.math.symmetry.Symmetry = model.getSymmetry();
            this.availableOrbits = new com.vzome.core.math.symmetry.OrbitSet(symmetry);
            this.snapOrbits = new com.vzome.core.math.symmetry.OrbitSet(symmetry);
            this.buildOrbits = new com.vzome.core.math.symmetry.OrbitSet(symmetry);
            this.renderOrbits = new com.vzome.core.math.symmetry.OrbitSet(symmetry);
            this.snapper = new com.vzome.desktop.controller.SymmetrySnapper(this.snapOrbits);
            for(let index=symmetry.getOrbitSet().getDirections().iterator();index.hasNext();) {
                let orbit = index.next();
                {
                    if (model.orbitIsStandard(orbit)){
                        this.availableOrbits.add(orbit);
                        this.snapOrbits.add(orbit);
                        if (model.orbitIsBuildDefault(orbit)){
                            this.buildOrbits.add(orbit);
                        }
                    }
                    this.renderOrbits.add(orbit);
                }
            }
            this.availableController = new com.vzome.desktop.controller.OrbitSetController(this.availableOrbits, this.symmetrySystem.getOrbits(), this.symmetrySystem);
            this.addSubController("availableOrbits", this.availableController);
            this.snapController = new com.vzome.desktop.controller.OrbitSetController(this.snapOrbits, this.availableOrbits, this.symmetrySystem);
            this.addSubController("snapOrbits", this.snapController);
            this.buildController = new com.vzome.desktop.controller.OrbitSetController(this.buildOrbits, this.availableOrbits, this.symmetrySystem);
            this.addSubController("buildOrbits", this.buildController);
            if (parent.propertyIsTrue("single.orbit"))try {
                this.buildController.doAction("oneAtATime");
            } catch(e) {
                console.error(e.message, e);
            }
            this.renderController = new com.vzome.desktop.controller.OrbitSetController(this.renderOrbits, this.symmetrySystem.getOrbits(), this.symmetrySystem);
            this.addSubController("renderOrbits", this.renderController);
            for(let index=this.symmetrySystem.getOrbits().getDirections().iterator();index.hasNext();) {
                let orbit = index.next();
                {
                    const unitLength: com.vzome.core.algebra.AlgebraicNumber = this.symmetrySystem.getOrbitUnitLength(orbit);
                    const field: com.vzome.core.algebra.AlgebraicField = symmetry.getField();
                    const lengthModel: com.vzome.desktop.api.Controller = new com.vzome.desktop.controller.LengthController(unitLength, field.one(), field);
                    this.buildController.addSubController("length." + orbit.getName(), lengthModel);
                    this.orbitLengths.put(orbit, lengthModel);
                }
            }
            if (parent.propertyIsTrue("disable.known.directions"))this.symmetrySystem.disableKnownDirection();
            this.availableController.addPropertyListener(this.buildController);
            this.availableController.addPropertyListener(this.snapController);
            this.availableController.addPropertyListener(new SymmetryController.SymmetryController$0(this));
            const presetStyle: string = parent.getProperty("defaultShapes." + model.getSymmetry().getField().getName() + "." + model.getName());
            if (presetStyle != null)this.symmetrySystem.setStyle(presetStyle);
        }

        /**
         * 
         * @param {string} listName
         * @return {java.lang.String[]}
         */
        public getCommandList(listName: string): string[] {
            switch((listName)) {
            case "styles":
                return this.symmetrySystem.getStyleNames();
            case "orbitNames":
                const list: java.util.ArrayList<string> = <any>(new java.util.ArrayList<any>());
                for(let index=this.symmetrySystem.getSymmetry().getDirections().iterator();index.hasNext();) {
                    let dir = index.next();
                    {
                        if (!dir.isAutomatic())list.add(dir.getCanonicalName());
                    }
                }
                return list.toArray<any>([]);
            case "orbits":
                const result: string[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(this.symmetrySystem.getOrbits().size());
                let i: number = 0;
                for(let index=this.symmetrySystem.getOrbits().getDirections().iterator();index.hasNext();) {
                    let orbit = index.next();
                    {
                        result[i] = orbit.getName();
                        i++;
                    }
                }
                return result;
            case "symmetryToolFactories":
                if (this.symmetryToolFactories.isEmpty()){
                    for(let index=this.symmetrySystem.getToolFactories(com.vzome.api.Tool.Kind.SYMMETRY).iterator();index.hasNext();) {
                        let factory = index.next();
                        this.symmetryToolFactories.put(factory.getId(), new com.vzome.desktop.controller.ToolFactoryController(factory))
                    }
                }
                return this.symmetryToolFactories.keySet().toArray<any>([]);
            case "transformToolFactories":
                if (this.transformToolFactories.isEmpty()){
                    for(let index=this.symmetrySystem.getToolFactories(com.vzome.api.Tool.Kind.TRANSFORM).iterator();index.hasNext();) {
                        let factory = index.next();
                        this.transformToolFactories.put(factory.getId(), new com.vzome.desktop.controller.ToolFactoryController(factory))
                    }
                }
                return this.transformToolFactories.keySet().toArray<any>([]);
            case "linearMapToolFactories":
                if (this.linearMapToolFactories.isEmpty()){
                    for(let index=this.symmetrySystem.getToolFactories(com.vzome.api.Tool.Kind.LINEAR_MAP).iterator();index.hasNext();) {
                        let factory = index.next();
                        this.linearMapToolFactories.put(factory.getId(), new com.vzome.desktop.controller.ToolFactoryController(factory))
                    }
                }
                return this.linearMapToolFactories.keySet().toArray<any>([]);
            case "builtInSymmetryTools":
                const toolNames: java.util.List<string> = <any>(new java.util.ArrayList<any>());
                for(let index=this.symmetrySystem.getPredefinedTools(com.vzome.api.Tool.Kind.SYMMETRY).iterator();index.hasNext();) {
                    let tool = index.next();
                    toolNames.add(tool.getId())
                }
                return toolNames.toArray<any>([]);
            case "builtInTransformTools":
                const transformToolNames: java.util.List<string> = <any>(new java.util.ArrayList<any>());
                for(let index=this.symmetrySystem.getPredefinedTools(com.vzome.api.Tool.Kind.TRANSFORM).iterator();index.hasNext();) {
                    let tool = index.next();
                    transformToolNames.add(tool.getId())
                }
                return transformToolNames.toArray<any>([]);
            default:
                return super.getCommandList(listName);
            }
        }

        /**
         * 
         * @param {string} name
         * @return {*}
         */
        public getSubController(name: string): com.vzome.desktop.api.Controller {
            switch((name)) {
            case "availableOrbits":
                return this.availableController;
            case "snapOrbits":
                return this.snapController;
            case "buildOrbits":
                return this.buildController;
            case "renderOrbits":
                return this.renderController;
            default:
                if (/* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(name, "length.")){
                    const dirName: string = name.substring("length.".length);
                    const dir: com.vzome.core.math.symmetry.Direction = this.symmetrySystem.getOrbits().getDirection(dirName);
                    return this.getLengthController(dir);
                }
                let result: com.vzome.desktop.api.Controller = this.symmetryToolFactories.get(name);
                if (result != null)return result;
                result = this.transformToolFactories.get(name);
                if (result != null)return result;
                result = this.linearMapToolFactories.get(name);
                if (result != null)return result;
                return super.getSubController(name);
            }
        }

        /**
         * 
         * @param {string} action
         */
        public doAction(action: string) {
            switch((action)) {
            case "rZomeOrbits":
            case "predefinedOrbits":
            case "setAllDirections":
                this.availableController.doAction(action);
                break;
            case "ReplaceWithShape":
                action += "/" + this.symmetrySystem.getName() + ":" + this.symmetrySystem.getStyle$().getName();
                super.doAction(action);
                break;
            case "resetOrbitColors":
                this.symmetrySystem.resetColors();
                this.availableController.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object("orbits", true, false);
                break;
            default:
                if (/* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(action, "setStyle.")){
                    const styleName: string = action.substring("setStyle.".length);
                    this.symmetrySystem.setStyle(styleName);
                    this.renderedModel.setShapes(this.symmetrySystem.getShapes());
                    this.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object("renderingStyle", null, styleName);
                } else {
                    const handled: boolean = this.symmetrySystem.doAction(action);
                    if (!handled)super.doAction(action);
                }
                break;
            }
        }

        /*private*/ getLengthController(orbit: com.vzome.core.math.symmetry.Direction): com.vzome.desktop.api.Controller {
            let result: com.vzome.desktop.api.Controller = this.orbitLengths.get(orbit);
            if (result == null && orbit != null){
                const unitLength: com.vzome.core.algebra.AlgebraicNumber = this.symmetrySystem.getOrbitUnitLength(orbit);
                const field: com.vzome.core.algebra.AlgebraicField = this.symmetrySystem.getSymmetry().getField();
                result = new com.vzome.desktop.controller.LengthController(unitLength, field.one(), field);
                this.buildController.addSubController("length." + orbit.getName(), result);
                this.orbitLengths.put(orbit, result);
                this.renderOrbits.add(orbit);
                this.availableOrbits.add(orbit);
            }
            return result;
        }

        public getOrbits(): com.vzome.core.math.symmetry.OrbitSet {
            return this.symmetrySystem.getOrbits();
        }

        public getOrbitSource(): com.vzome.core.editor.api.OrbitSource {
            return this.symmetrySystem;
        }

        public getZone(offset: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.math.symmetry.Axis {
            return this.symmetrySystem.getAxis(offset);
        }

        public getColor(orbit: com.vzome.core.math.symmetry.Direction): com.vzome.core.construction.Color {
            return this.symmetrySystem.getColor(orbit);
        }

        public getBuildOrbits(): com.vzome.core.math.symmetry.OrbitSet {
            return this.buildOrbits;
        }

        public getRenderingStyle(): com.vzome.core.editor.api.Shapes {
            return this.symmetrySystem.getRenderingStyle();
        }

        public parseOrbitLength(string: string, props: java.util.Map<string, any>) {
            const sections: string[] = string.split("/", 3);
            const mode: string = sections[0];
            props.put("mode", mode);
            const orbitStr: string = sections[1];
            const orbit: com.vzome.core.math.symmetry.Direction = this.getOrbits().getDirection(orbitStr);
            props.put("orbit", orbit);
            if (/* endsWith */((str, searchString) => { let pos = str.length - searchString.length; let lastIndex = str.indexOf(searchString, pos); return lastIndex !== -1 && lastIndex === pos; })(mode, "Struts")){
                const lengthStr: string = sections[2];
                const unitScalar: com.vzome.core.algebra.AlgebraicNumber = orbit.getLengthInUnits(this.getSymmetry().getField().one());
                const length: com.vzome.core.algebra.AlgebraicNumber = unitScalar.getField().parseNumber(lengthStr);
                const rawLength: com.vzome.core.algebra.AlgebraicNumber = length.dividedBy(unitScalar);
                if (rawLength != null)props.put("length", rawLength);
            }
        }
    }
    SymmetryController["__class"] = "com.vzome.desktop.controller.SymmetryController";
    SymmetryController["__interfaces"] = ["com.vzome.desktop.api.Controller"];



    export namespace SymmetryController {

        export class SymmetryController$0 implements java.beans.PropertyChangeListener {
            public __parent: any;
            /**
             * 
             * @param {java.beans.PropertyChangeEvent} event
             */
            public propertyChange(event: java.beans.PropertyChangeEvent) {
                if ("orbits" === event.getPropertyName()){
                }
            }

            constructor(__parent: any) {
                this.__parent = __parent;
            }
        }
        SymmetryController$0["__interfaces"] = ["java.util.EventListener","java.beans.PropertyChangeListener"];


    }

}


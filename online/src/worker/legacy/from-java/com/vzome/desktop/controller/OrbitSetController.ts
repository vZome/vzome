import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicVector } from "../../core/algebra/AlgebraicVector.js";
import { Color } from "../../core/construction/Color.js";
import { OrbitSource } from "../../core/editor/api/OrbitSource.js";
import { RealVector } from "../../core/math/RealVector.js";
import { Axis } from "../../core/math/symmetry/Axis.js";
import { Direction } from "../../core/math/symmetry/Direction.js";
import { DodecagonalSymmetry } from "../../core/math/symmetry/DodecagonalSymmetry.js";
import { OctahedralSymmetry } from "../../core/math/symmetry/OctahedralSymmetry.js";
import { OrbitSet } from "../../core/math/symmetry/OrbitSet.js";
import { Symmetry } from "../../core/math/symmetry/Symmetry.js";
import { Controller } from "../api/Controller.js";
import { DefaultController } from "./DefaultController.js";
import { PropertyChangeEvent } from "../../../../java/beans/PropertyChangeEvent.js";
import { PropertyChangeListener } from "../../../../java/beans/PropertyChangeListener.js";

export class OrbitSetController extends DefaultController implements PropertyChangeListener {
    /*private*/ colorSource: OrbitSource;

    /*private*/ orbits: OrbitSet;

    /*private*/ allOrbits: OrbitSet;

    /*private*/ lastOrbit: Direction;

    /*private*/ mOneAtATime: boolean;

    /*private*/ orbitDots: java.util.Map<Direction, OrbitSetController.OrbitState>;

    public constructor(orbits: OrbitSet, allOrbits: OrbitSet, colorSource: OrbitSource) {
        super();
        if (this.colorSource === undefined) { this.colorSource = null; }
        if (this.orbits === undefined) { this.orbits = null; }
        if (this.allOrbits === undefined) { this.allOrbits = null; }
        this.lastOrbit = null;
        this.mOneAtATime = true;
        this.orbitDots = <any>(new java.util.HashMap<any, any>());
        this.orbits = orbits;
        this.allOrbits = allOrbits;
        this.colorSource = colorSource;
        this.mOneAtATime = orbits.size() === 1;
        this.recalculateDots();
    }

    recalculateDots() {
        this.orbits.retainAll(this.allOrbits);
        const symmetry: Symmetry = this.allOrbits.getSymmetry();
        let test: RealVector = new RealVector(0.1, 0.1, 1.0);
        if (symmetry != null && symmetry instanceof <any>OctahedralSymmetry)test = new RealVector(2.0, 1.0, 4.0); else if (symmetry != null && symmetry instanceof <any>DodecagonalSymmetry)test = new RealVector(10.0, 1.0, 1.0);
        symmetry.computeOrbitDots();
        this.orbitDots.clear();
        let lastOrbitChanged: boolean = false;
        for(let index=this.allOrbits.getDirections().iterator();index.hasNext();) {
            let dir = index.next();
            {
                if (this.lastOrbit == null){
                    this.lastOrbit = dir;
                    lastOrbitChanged = true;
                }
                const orbit: OrbitSetController.OrbitState = new OrbitSetController.OrbitState();
                this.orbitDots.put(dir, orbit);
                orbit.color = this.colorSource.getColor(dir);
                orbit.dotX = dir.getDotX();
                if (orbit.dotX >= -90.0){
                    orbit.dotY = dir.getDotY();
                } else {
                    const axis: Axis = symmetry['getAxis$com_vzome_core_math_RealVector$java_util_Collection'](test, java.util.Collections.singleton<any>(dir));
                    const v: AlgebraicVector = axis.normal();
                    let z: number = v.getComponent(2).evaluate();
                    if (z === 0.0){
                        z = 1.0;
                    }
                    orbit.dotX = v.getComponent(0).evaluate();
                    orbit.dotX = orbit.dotX / z;
                    orbit.dotY = v.getComponent(1).evaluate();
                    orbit.dotY = orbit.dotY / z;
                }
            }
        }
        if ((this.lastOrbit == null) || (!this.allOrbits.contains(this.lastOrbit))){
            lastOrbitChanged = true;
            if (!this.orbits.isEmpty())this.lastOrbit = this.orbits.last(); else if (!this.orbitDots.isEmpty())this.lastOrbit = this.orbitDots.keySet().iterator().next(); else this.lastOrbit = null;
        }
        if (lastOrbitChanged)this.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object("selectedOrbit", null, this.lastOrbit == null ? null : this.lastOrbit.getName());
    }

    /**
     * 
     * @param {string} action
     */
    public doAction(action: string) {
        if (action === ("refreshDots")){
            this.recalculateDots();
            return;
        }
        if ((action === ("toggleHalf")) || (action === ("reset")) || (action === ("short")) || (action === ("medium")) || (action === ("long")) || /* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(action, "adjustScale.") || (action === ("scaleUp")) || (action === ("scaleDown"))){
            this.getSubController("currentLength").actionPerformed(null, action);
            return;
        }
        if (action === ("setNoDirections")){
            this.orbits.clear();
        } else if (action === ("setAllDirections")){
            this.mOneAtATime = false;
            this.orbits.addAll(this.allOrbits);
        } else if (action === ("rZomeOrbits")){
            this.mOneAtATime = false;
            this.orbits.clear();
            for(let index=this.allOrbits.getDirections().iterator();index.hasNext();) {
                let dir = index.next();
                {
                    if (dir.isStandard()){
                        this.orbits.add(dir);
                    }
                }
            }
        } else if (action === ("predefinedOrbits")){
            this.mOneAtATime = false;
            this.orbits.clear();
            for(let index=this.allOrbits.getDirections().iterator();index.hasNext();) {
                let dir = index.next();
                {
                    if (!dir.isAutomatic()){
                        this.orbits.add(dir);
                    }
                }
            }
        } else if (action === ("oneAtATime")){
            this.mOneAtATime = !this.mOneAtATime;
            if (!this.mOneAtATime)return;
            this.orbits.clear();
            if (this.lastOrbit != null)this.orbits.add(this.lastOrbit);
        } else if (/* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(action, "setSingleOrbit.")){
            const lastValue: boolean = this.mOneAtATime;
            const value: string = action.substring("setSingleOrbit.".length);
            this.mOneAtATime = javaemul.internal.BooleanHelper.parseBoolean(value);
            if (this.mOneAtATime){
                this.orbits.clear();
                if (this.lastOrbit != null)this.orbits.add(this.lastOrbit);
            }
            this.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object("oneAtATime", lastValue, this.mOneAtATime);
        } else if (/* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(action, "enableDirection.")){
            const dirName: string = action.substring("enableDirection.".length);
            const dir: Direction = this.allOrbits.getDirection(dirName);
            if (dir != null && !this.orbits.contains(dir))this.toggleOrbit(dir);
        } else if (/* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(action, "toggleDirection.")){
            const dirName: string = action.substring("toggleDirection.".length);
            const dir: Direction = this.allOrbits.getDirection(dirName);
            this.toggleOrbit(dir);
        } else if (/* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(action, "setSingleDirection.")){
            this.mOneAtATime = true;
            const dirName: string = action.substring("setSingleDirection.".length);
            const dir: Direction = this.allOrbits.getDirection(dirName);
            this.toggleOrbit(dir);
        }
        this.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object("orbits", true, false);
    }

    /**
     * 
     * @param {PropertyChangeEvent} evt
     */
    public propertyChange(evt: PropertyChangeEvent) {
        if (("length" === evt.getPropertyName()) && evt.getSource() === this.getSubController("currentLength"))this.firePropertyChange$java_beans_PropertyChangeEvent(evt);
        if ("orbits" === evt.getPropertyName()){
            this.recalculateDots();
            this.firePropertyChange$java_beans_PropertyChangeEvent(evt);
        }
    }

    toggleOrbit(dir: Direction) {
        if (this.mOneAtATime)this.orbits.clear();
        if (this.orbits.add(dir)){
            this.lastOrbit = dir;
            this.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object("selectedOrbit", null, dir.getName());
        } else if (this.orbits.remove(dir)){
        } else throw new java.lang.IllegalStateException("could not toggle direction " + dir.getName());
    }

    /**
     * 
     * @param {string} name
     * @return {*}
     */
    public getSubController(name: string): Controller {
        if ("currentLength" === name)return super.getSubController("length." + this.getProperty("selectedOrbit"));
        return super.getSubController(name);
    }

    /**
     * 
     * @param {string} string
     * @return {string}
     */
    public getProperty(string: string): string {
        if ("oneAtATime" === string)return javaemul.internal.BooleanHelper.toString(this.mOneAtATime);
        if ("reverseOrbitTriangle" === string)return javaemul.internal.BooleanHelper.toString(this.allOrbits.getSymmetry().reverseOrbitTriangle());
        if ("selectedOrbit" === string)if (this.lastOrbit != null)return this.lastOrbit.getName(); else return null;
        if ("halfSizes" === string)if (this.lastOrbit != null && this.lastOrbit.hasHalfSizes())return "true"; else return "false";
        if ("scaleName.superShort" === string)return (this.lastOrbit == null) ? null : this.lastOrbit.getScaleName(0);
        if ("scaleName.short" === string)return (this.lastOrbit == null) ? null : this.lastOrbit.getScaleName(1);
        if ("scaleName.medium" === string)return (this.lastOrbit == null) ? null : this.lastOrbit.getScaleName(2);
        if ("scaleName.long" === string)return (this.lastOrbit == null) ? null : this.lastOrbit.getScaleName(3);
        if ("color" === string){
            const color: Color = this.colorSource.getColor(this.lastOrbit);
            if (color == null)return null;
            const rgb: number = color.getRGB();
            return "0x" + javaemul.internal.IntegerHelper.toHexString(rgb);
        }
        if (((lhs, rhs) => lhs || rhs)(((lhs, rhs) => lhs || rhs)("half" === string, "unitText" === string), "multiplierText" === string))return this.getSubController("currentLength").getProperty(string);
        if (/* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(string, "orbitDot.")){
            const orbitName: string = string.substring("orbitDot.".length);
            const orbit: Direction = this.allOrbits.getDirection(orbitName);
            const dot: OrbitSetController.OrbitState = this.orbitDots.get(orbit);
            if (dot == null)return "0xffffff/0/0";
            return "0x" + javaemul.internal.IntegerHelper.toHexString(dot.color.getRGB()) + "/" + dot.dotX + "/" + dot.dotY;
        }
        if (/* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(string, "orbitEnabled.")){
            const orbitName: string = string.substring("orbitEnabled.".length);
            const orbit: Direction = this.orbits.getDirection(orbitName);
            return javaemul.internal.BooleanHelper.toString(orbit != null);
        }
        return super.getProperty(string);
    }

    /**
     * 
     * @param {string} cmd
     * @param {*} value
     */
    public setModelProperty(cmd: string, value: any) {
        if ("oneAtATime" === cmd)this.mOneAtATime = /* equals */(<any>((o1: any, o2: any) => o1 && o1.equals ? o1.equals(o2) : o1 === o2)("true",value)); else if (((lhs, rhs) => lhs || rhs)("multiplier" === cmd, "half" === cmd))this.getSubController("currentLength").setProperty(cmd, value); else super.setModelProperty(cmd, value);
    }

    /**
     * 
     * @param {string} listName
     * @return {java.lang.String[]}
     */
    public getCommandList(listName: string): string[] {
        if (listName === ("orbits")){
            const result: string[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(this.orbits.size());
            let i: number = 0;
            for(let index=this.orbits.getDirections().iterator();index.hasNext();) {
                let dir = index.next();
                {
                    result[i] = dir.getName();
                    i++;
                }
            }
            return result;
        }
        if (listName === ("allOrbits")){
            const result: string[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(this.allOrbits.size());
            let i: number = 0;
            for(let index=this.allOrbits.getDirections().iterator();index.hasNext();) {
                let dir = index.next();
                {
                    result[i] = dir.getName();
                    i++;
                }
            }
            return result;
        }
        return super.getCommandList(listName);
    }
}
OrbitSetController["__class"] = "com.vzome.desktop.controller.OrbitSetController";
OrbitSetController["__interfaces"] = ["java.util.EventListener","java.beans.PropertyChangeListener","com.vzome.desktop.api.Controller"];



export namespace OrbitSetController {

    export class OrbitState {
        dotX: number;

        dotY: number;

        color: Color;

        constructor() {
            if (this.dotX === undefined) { this.dotX = 0; }
            if (this.dotY === undefined) { this.dotY = 0; }
            if (this.color === undefined) { this.color = null; }
        }
    }
    OrbitState["__class"] = "com.vzome.desktop.controller.OrbitSetController.OrbitState";

}

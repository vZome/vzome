import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Color } from "../construction/Color.js";
import { RealVector } from "../math/RealVector.js";
import { DomUtils } from "../../xml/DomUtils.js";
import { PropertyChangeListener } from "../../../../java/beans/PropertyChangeListener.js";
import { PropertyChangeSupport } from "../../../../java/beans/PropertyChangeSupport.js";
import { Document } from "../../../../org/w3c/dom/Document.js";
import { Element } from "../../../../org/w3c/dom/Element.js";
import { Node } from "../../../../org/w3c/dom/Node.js";
import { NodeList } from "../../../../org/w3c/dom/NodeList.js";

/**
 * This is really a SceneModel
 * @author Scott Vorthmann
 * @param {Lights} prototype
 * @class
 */
export class Lights implements java.lang.Iterable<Lights.DirectionalLight> {
    /*private*/ pcs: PropertyChangeSupport;

    public addPropertyListener(listener: PropertyChangeListener) {
        this.pcs.addPropertyChangeListener$java_beans_PropertyChangeListener(listener);
    }

    public removePropertyListener(listener: PropertyChangeListener) {
        this.pcs.removePropertyChangeListener$java_beans_PropertyChangeListener(listener);
    }

    public setProperty(cmd: string, value: any) {
        if ("backgroundColor" === cmd){
            this.backgroundColor = new Color(javaemul.internal.IntegerHelper.parseInt(<string>value, 16));
            this.pcs.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object(cmd, null, value);
        }
    }

    directionalLights: java.util.List<Lights.DirectionalLight>;

    mAmbientLightColor: Color;

    /*private*/ backgroundColor: Color;

    public constructor(prototype?: any) {
        if (((prototype != null && prototype instanceof <any>Lights) || prototype === null)) {
            let __args = arguments;
            {
                let __args = arguments;
                if (this.mAmbientLightColor === undefined) { this.mAmbientLightColor = null; } 
                if (this.backgroundColor === undefined) { this.backgroundColor = null; } 
                this.pcs = new PropertyChangeSupport(this);
                this.directionalLights = <any>(new java.util.ArrayList<any>(3));
            }
            (() => {
                this.backgroundColor = prototype.backgroundColor;
                this.mAmbientLightColor = prototype.mAmbientLightColor;
                for(let i: number = 0; i < prototype.directionalLights.size(); i++) {{
                    this.addDirectionLight$com_vzome_core_viewing_Lights_DirectionalLight(prototype.directionalLights.get(i));
                };}
            })();
        } else if (((prototype != null && (prototype.constructor != null && prototype.constructor["__interfaces"] != null && prototype.constructor["__interfaces"].indexOf("org.w3c.dom.Element") >= 0)) || prototype === null)) {
            let __args = arguments;
            let element: any = __args[0];
            {
                let __args = arguments;
                if (this.mAmbientLightColor === undefined) { this.mAmbientLightColor = null; } 
                if (this.backgroundColor === undefined) { this.backgroundColor = null; } 
                this.pcs = new PropertyChangeSupport(this);
                this.directionalLights = <any>(new java.util.ArrayList<any>(3));
            }
            (() => {
                let str: string = element.getAttribute("background");
                this.backgroundColor = Color.parseColor(str);
                str = element.getAttribute("ambientLight");
                this.mAmbientLightColor = Color.parseColor(str);
                const nodes: NodeList = element.getChildNodes();
                for(let i: number = 0; i < nodes.getLength(); i++) {{
                    const node: Node = nodes.item(i);
                    if (node != null && (node.constructor != null && node.constructor["__interfaces"] != null && node.constructor["__interfaces"].indexOf("org.w3c.dom.Element") >= 0)){
                        const viewElem: Element = <Element><any>node;
                        str = viewElem.getAttribute("color");
                        const color: Color = Color.parseColor(str);
                        const pos: RealVector = new RealVector(javaemul.internal.FloatHelper.parseFloat(viewElem.getAttribute("x")), javaemul.internal.FloatHelper.parseFloat(viewElem.getAttribute("y")), javaemul.internal.FloatHelper.parseFloat(viewElem.getAttribute("z")));
                        this.addDirectionLight$com_vzome_core_viewing_Lights_DirectionalLight(new Lights.DirectionalLight(pos, color));
                    }
                };}
            })();
        } else if (prototype === undefined) {
            let __args = arguments;
            if (this.mAmbientLightColor === undefined) { this.mAmbientLightColor = null; } 
            if (this.backgroundColor === undefined) { this.backgroundColor = null; } 
            this.pcs = new PropertyChangeSupport(this);
            this.directionalLights = <any>(new java.util.ArrayList<any>(3));
        } else throw new Error('invalid overload');
    }

    public size(): number {
        return this.directionalLights.size();
    }

    public addDirectionLight$com_vzome_core_viewing_Lights_DirectionalLight(light: Lights.DirectionalLight) {
        this.directionalLights.add(light);
    }

    public setAmbientColor(color: Color) {
        this.mAmbientLightColor = color;
    }

    public getAmbientColor(): Color {
        return this.mAmbientLightColor;
    }

    public getAmbientColorWeb(): string {
        return this.mAmbientLightColor.toWebString();
    }

    public getDirectionalLights() {
    }

    public getBackgroundColor(): Color {
        return this.backgroundColor;
    }

    public getBackgroundColorWeb(): string {
        return this.backgroundColor.toWebString();
    }

    public setBackgroundColor(color: Color) {
        this.backgroundColor = color;
    }

    public getXml(doc: Document): Element {
        const result: Element = doc.createElement("sceneModel");
        DomUtils.addAttribute(result, "ambientLight", this.mAmbientLightColor.toString());
        DomUtils.addAttribute(result, "background", this.backgroundColor.toString());
        for(let i: number = 0; i < this.directionalLights.size(); i++) {{
            const light: Lights.DirectionalLight = this.directionalLights.get(i);
            const child: Element = doc.createElement("directionalLight");
            DomUtils.addAttribute(child, "x", /* toString */(''+(light.direction.x)));
            DomUtils.addAttribute(child, "y", /* toString */(''+(light.direction.y)));
            DomUtils.addAttribute(child, "z", /* toString */(''+(light.direction.z)));
            DomUtils.addAttribute(child, "color", light.color.toString());
            result.appendChild(child);
        };}
        return result;
    }

    /**
     * 
     * @return {*}
     */
    public iterator(): java.util.Iterator<Lights.DirectionalLight> {
        return this.directionalLights.iterator();
    }

    public addDirectionLight$com_vzome_core_construction_Color$com_vzome_core_math_RealVector(color: Color, dir: RealVector) {
        this.addDirectionLight$com_vzome_core_viewing_Lights_DirectionalLight(new Lights.DirectionalLight(dir, color));
    }

    public addDirectionLight(color?: any, dir?: any) {
        if (((color != null && color instanceof <any>Color) || color === null) && ((dir != null && dir instanceof <any>RealVector) || dir === null)) {
            return <any>this.addDirectionLight$com_vzome_core_construction_Color$com_vzome_core_math_RealVector(color, dir);
        } else if (((color != null && color instanceof <any>Lights.DirectionalLight) || color === null) && dir === undefined) {
            return <any>this.addDirectionLight$com_vzome_core_viewing_Lights_DirectionalLight(color);
        } else throw new Error('invalid overload');
    }

    public getDirectionalLightVector(i: number): RealVector {
        const light: Lights.DirectionalLight = this.directionalLights.get(i);
        return new RealVector(light.direction.x, light.direction.y, light.direction.z);
    }

    public getDirectionalLightColor(i: number): Color {
        const light: Lights.DirectionalLight = this.directionalLights.get(i);
        return light.color;
    }
}
Lights["__class"] = "com.vzome.core.viewing.Lights";
Lights["__interfaces"] = ["java.lang.Iterable"];



export namespace Lights {

    export class DirectionalLight {
        public constructor(direction: RealVector, color: Color) {
            if (this.direction === undefined) { this.direction = null; }
            if (this.color === undefined) { this.color = null; }
            this.direction = direction;
            this.color = color;
        }

        public direction: RealVector;

        color: Color;

        public getColor(): string {
            return this.color.toWebString();
        }

        public getDirection(): number[] {
            return [this.direction.x, this.direction.y, this.direction.z];
        }
    }
    DirectionalLight["__class"] = "com.vzome.core.viewing.Lights.DirectionalLight";

}

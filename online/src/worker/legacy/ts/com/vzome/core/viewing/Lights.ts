/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.viewing {
    /**
     * This is really a SceneModel
     * @author Scott Vorthmann
     * @param {com.vzome.core.viewing.Lights} prototype
     * @class
     */
    export class Lights implements java.lang.Iterable<Lights.DirectionalLight> {
        /*private*/ pcs: java.beans.PropertyChangeSupport;

        public addPropertyListener(listener: java.beans.PropertyChangeListener) {
            this.pcs.addPropertyChangeListener$java_beans_PropertyChangeListener(listener);
        }

        public removePropertyListener(listener: java.beans.PropertyChangeListener) {
            this.pcs.removePropertyChangeListener$java_beans_PropertyChangeListener(listener);
        }

        public setProperty(cmd: string, value: any) {
            if ("backgroundColor" === cmd){
                this.backgroundColor = new com.vzome.core.construction.Color(javaemul.internal.IntegerHelper.parseInt(<string>value, 16));
                this.pcs.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object(cmd, null, value);
            }
        }

        directionalLights: java.util.List<Lights.DirectionalLight>;

        mAmbientLightColor: com.vzome.core.construction.Color;

        /*private*/ backgroundColor: com.vzome.core.construction.Color;

        public constructor(prototype?: any) {
            if (((prototype != null && prototype instanceof <any>com.vzome.core.viewing.Lights) || prototype === null)) {
                let __args = arguments;
                {
                    let __args = arguments;
                    if (this.mAmbientLightColor === undefined) { this.mAmbientLightColor = null; } 
                    if (this.backgroundColor === undefined) { this.backgroundColor = null; } 
                    this.pcs = new java.beans.PropertyChangeSupport(this);
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
                    this.pcs = new java.beans.PropertyChangeSupport(this);
                    this.directionalLights = <any>(new java.util.ArrayList<any>(3));
                }
                (() => {
                    let str: string = element.getAttribute("background");
                    this.backgroundColor = com.vzome.core.construction.Color.parseColor(str);
                    str = element.getAttribute("ambientLight");
                    this.mAmbientLightColor = com.vzome.core.construction.Color.parseColor(str);
                    const nodes: org.w3c.dom.NodeList = element.getChildNodes();
                    for(let i: number = 0; i < nodes.getLength(); i++) {{
                        const node: org.w3c.dom.Node = nodes.item(i);
                        if (node != null && (node.constructor != null && node.constructor["__interfaces"] != null && node.constructor["__interfaces"].indexOf("org.w3c.dom.Element") >= 0)){
                            const viewElem: org.w3c.dom.Element = <org.w3c.dom.Element><any>node;
                            str = viewElem.getAttribute("color");
                            const color: com.vzome.core.construction.Color = com.vzome.core.construction.Color.parseColor(str);
                            const pos: com.vzome.core.math.RealVector = new com.vzome.core.math.RealVector(javaemul.internal.FloatHelper.parseFloat(viewElem.getAttribute("x")), javaemul.internal.FloatHelper.parseFloat(viewElem.getAttribute("y")), javaemul.internal.FloatHelper.parseFloat(viewElem.getAttribute("z")));
                            this.addDirectionLight$com_vzome_core_viewing_Lights_DirectionalLight(new Lights.DirectionalLight(pos, color));
                        }
                    };}
                })();
            } else if (prototype === undefined) {
                let __args = arguments;
                if (this.mAmbientLightColor === undefined) { this.mAmbientLightColor = null; } 
                if (this.backgroundColor === undefined) { this.backgroundColor = null; } 
                this.pcs = new java.beans.PropertyChangeSupport(this);
                this.directionalLights = <any>(new java.util.ArrayList<any>(3));
            } else throw new Error('invalid overload');
        }

        public size(): number {
            return this.directionalLights.size();
        }

        public addDirectionLight$com_vzome_core_viewing_Lights_DirectionalLight(light: Lights.DirectionalLight) {
            this.directionalLights.add(light);
        }

        public setAmbientColor(color: com.vzome.core.construction.Color) {
            this.mAmbientLightColor = color;
        }

        public getAmbientColor(): com.vzome.core.construction.Color {
            return this.mAmbientLightColor;
        }

        public getAmbientColorWeb(): string {
            return this.mAmbientLightColor.toWebString();
        }

        public getDirectionalLights() {
        }

        public getBackgroundColor(): com.vzome.core.construction.Color {
            return this.backgroundColor;
        }

        public getBackgroundColorWeb(): string {
            return this.backgroundColor.toWebString();
        }

        public setBackgroundColor(color: com.vzome.core.construction.Color) {
            this.backgroundColor = color;
        }

        public getXml(doc: org.w3c.dom.Document): org.w3c.dom.Element {
            const result: org.w3c.dom.Element = doc.createElement("sceneModel");
            com.vzome.xml.DomUtils.addAttribute(result, "ambientLight", this.mAmbientLightColor.toString());
            com.vzome.xml.DomUtils.addAttribute(result, "background", this.backgroundColor.toString());
            for(let i: number = 0; i < this.directionalLights.size(); i++) {{
                const light: Lights.DirectionalLight = this.directionalLights.get(i);
                const child: org.w3c.dom.Element = doc.createElement("directionalLight");
                com.vzome.xml.DomUtils.addAttribute(child, "x", /* toString */(''+(light.direction.x)));
                com.vzome.xml.DomUtils.addAttribute(child, "y", /* toString */(''+(light.direction.y)));
                com.vzome.xml.DomUtils.addAttribute(child, "z", /* toString */(''+(light.direction.z)));
                com.vzome.xml.DomUtils.addAttribute(child, "color", light.color.toString());
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

        public addDirectionLight$com_vzome_core_construction_Color$com_vzome_core_math_RealVector(color: com.vzome.core.construction.Color, dir: com.vzome.core.math.RealVector) {
            this.addDirectionLight$com_vzome_core_viewing_Lights_DirectionalLight(new Lights.DirectionalLight(dir, color));
        }

        public addDirectionLight(color?: any, dir?: any) {
            if (((color != null && color instanceof <any>com.vzome.core.construction.Color) || color === null) && ((dir != null && dir instanceof <any>com.vzome.core.math.RealVector) || dir === null)) {
                return <any>this.addDirectionLight$com_vzome_core_construction_Color$com_vzome_core_math_RealVector(color, dir);
            } else if (((color != null && color instanceof <any>com.vzome.core.viewing.Lights.DirectionalLight) || color === null) && dir === undefined) {
                return <any>this.addDirectionLight$com_vzome_core_viewing_Lights_DirectionalLight(color);
            } else throw new Error('invalid overload');
        }

        public getDirectionalLightVector(i: number): com.vzome.core.math.RealVector {
            const light: Lights.DirectionalLight = this.directionalLights.get(i);
            return new com.vzome.core.math.RealVector(light.direction.x, light.direction.y, light.direction.z);
        }

        public getDirectionalLightColor(i: number): com.vzome.core.construction.Color {
            const light: Lights.DirectionalLight = this.directionalLights.get(i);
            return light.color;
        }
    }
    Lights["__class"] = "com.vzome.core.viewing.Lights";
    Lights["__interfaces"] = ["java.lang.Iterable"];



    export namespace Lights {

        export class DirectionalLight {
            public constructor(direction: com.vzome.core.math.RealVector, color: com.vzome.core.construction.Color) {
                if (this.direction === undefined) { this.direction = null; }
                if (this.color === undefined) { this.color = null; }
                this.direction = direction;
                this.color = color;
            }

            public direction: com.vzome.core.math.RealVector;

            color: com.vzome.core.construction.Color;

            public getColor(): string {
                return this.color.toWebString();
            }

            public getDirection(): number[] {
                return [this.direction.x, this.direction.y, this.direction.z];
            }
        }
        DirectionalLight["__class"] = "com.vzome.core.viewing.Lights.DirectionalLight";

    }

}


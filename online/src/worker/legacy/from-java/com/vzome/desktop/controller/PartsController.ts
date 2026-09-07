import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicNumber } from "../../core/algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../../core/algebra/AlgebraicVector.js";
import { Color } from "../../core/construction/Color.js";
import { OrbitSource } from "../../core/editor/api/OrbitSource.js";
import { Shapes } from "../../core/editor/api/Shapes.js";
import { Axis } from "../../core/math/symmetry/Axis.js";
import { Direction } from "../../core/math/symmetry/Direction.js";
import { Connector } from "../../core/model/Connector.js";
import { Manifestation } from "../../core/model/Manifestation.js";
import { Panel } from "../../core/model/Panel.js";
import { Strut } from "../../core/model/Strut.js";
import { StrutImpl } from "../../core/model/StrutImpl.js";
import { RenderedManifestation } from "../../core/render/RenderedManifestation.js";
import { RenderingChanges } from "../../core/render/RenderingChanges.js";
import { DefaultController } from "./DefaultController.js";

export class PartsController extends DefaultController implements RenderingChanges {
    /*private*/ oldOrbits: OrbitSource;

    /*private*/ newOrbits: OrbitSource;

    public constructor(orbits: OrbitSource) {
        super();
        if (this.oldOrbits === undefined) { this.oldOrbits = null; }
        if (this.newOrbits === undefined) { this.newOrbits = null; }
        this.oldOrbits = orbits;
        this.newOrbits = orbits;
    }

    public startSwitch(switchTo: OrbitSource) {
        this.newOrbits = switchTo;
    }

    public endSwitch() {
        this.oldOrbits = this.newOrbits;
    }

    /**
     * 
     * @param {RenderedManifestation} rendered
     */
    public manifestationAdded(rendered: RenderedManifestation) {
        this.fireManifestationCountChanged("add", rendered, this.newOrbits);
    }

    /**
     * 
     * @param {RenderedManifestation} rendered
     */
    public manifestationRemoved(rendered: RenderedManifestation) {
        this.fireManifestationCountChanged("remove", rendered, this.oldOrbits);
    }

    fireManifestationCountChanged(action: string, rendered: RenderedManifestation, orbitSource: OrbitSource) {
        const man: Manifestation = rendered.getManifestation();
        let partTypeName: string = null;
        let partInfo: PartsController.PartInfo = null;
        if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
            partTypeName = "Ball";
            partInfo = new PartsController.PartInfo(<Connector><any>man);
        } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
            partTypeName = "Strut";
            const length: AlgebraicNumber = rendered.getShape().getLength();
            partInfo = new PartsController.PartInfo(<Strut><any>man, orbitSource, length);
        } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
            partTypeName = "Panel";
            partInfo = new PartsController.PartInfo(<Panel><any>man, orbitSource);
        }
        if (partTypeName != null && partInfo != null){
            const propertyName: string = action + partTypeName;
            switch((action)) {
            case "add":
                this.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object(propertyName, null, partInfo);
                break;
            case "remove":
                this.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object(propertyName, partInfo, null);
                break;
            default:
                throw new java.lang.IllegalArgumentException("Unsupported action: " + (action == null ? "<null>" : action) + ".");
            }
        }
    }

    /**
     * 
     */
    public reset() {
    }

    /**
     * 
     * @param {RenderedManifestation} from
     * @param {RenderedManifestation} to
     */
    public manifestationSwitched(from: RenderedManifestation, to: RenderedManifestation) {
    }

    /**
     * 
     * @param {RenderedManifestation} manifestation
     */
    public glowChanged(manifestation: RenderedManifestation) {
    }

    /**
     * 
     * @param {RenderedManifestation} manifestation
     */
    public colorChanged(manifestation: RenderedManifestation) {
    }

    /**
     * 
     * @param {RenderedManifestation} manifestation
     */
    public labelChanged(manifestation: RenderedManifestation) {
    }

    /**
     * 
     * @param {RenderedManifestation} manifestation
     */
    public locationChanged(manifestation: RenderedManifestation) {
    }

    /**
     * 
     * @param {RenderedManifestation} manifestation
     */
    public orientationChanged(manifestation: RenderedManifestation) {
    }

    /**
     * 
     * @param {RenderedManifestation} manifestation
     */
    public shapeChanged(manifestation: RenderedManifestation) {
    }

    /**
     * 
     * @param {*} shapes
     * @return {boolean}
     */
    public shapesChanged(shapes: Shapes): boolean {
        return false;
    }
}
PartsController["__class"] = "com.vzome.desktop.controller.PartsController";
PartsController["__interfaces"] = ["com.vzome.core.render.RenderingChanges","com.vzome.desktop.api.Controller"];



export namespace PartsController {

    /**
     * PartInfo is passed to the PartsPanel in the PropertyChangeEvent.
     * @param {string} name
     * @param {java.lang.Class} partType
     * @class
     */
    export class PartInfo {
        public orbitStr: string;

        public rgbColor: number;

        public sizeNameStr: string;

        public lengthStr: string;

        public strutLength: AlgebraicNumber;

        public automaticDirectionIndex: number;

        public realLength: number;

        public partClass: any;

        public constructor(strut?: any, orbits?: any, length?: any) {
            if (((strut != null && (strut.constructor != null && strut.constructor["__interfaces"] != null && strut.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)) || strut === null) && ((orbits != null && (orbits.constructor != null && orbits.constructor["__interfaces"] != null && orbits.constructor["__interfaces"].indexOf("com.vzome.core.editor.api.OrbitSource") >= 0)) || orbits === null) && ((length != null && (length.constructor != null && length.constructor["__interfaces"] != null && length.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || length === null)) {
                let __args = arguments;
                if (this.orbitStr === undefined) { this.orbitStr = null; } 
                if (this.rgbColor === undefined) { this.rgbColor = 0; } 
                if (this.sizeNameStr === undefined) { this.sizeNameStr = null; } 
                if (this.lengthStr === undefined) { this.lengthStr = null; } 
                if (this.strutLength === undefined) { this.strutLength = null; } 
                if (this.automaticDirectionIndex === undefined) { this.automaticDirectionIndex = null; } 
                if (this.realLength === undefined) { this.realLength = null; } 
                if (this.partClass === undefined) { this.partClass = null; } 
                const orbit: Direction = (<RenderedManifestation><any>(<StrutImpl><any>strut).getRenderedObject()).getStrutOrbit();
                this.orbitStr = orbit.getName();
                this.rgbColor = orbits.getColor(orbit).getRGB();
                const buf: java.lang.StringBuffer = new java.lang.StringBuffer();
                orbit.getLengthExpression(buf, length);
                const lengthExpression: string = buf.toString();
                const tokens: java.util.StringTokenizer = new java.util.StringTokenizer(lengthExpression, ":");
                this.sizeNameStr = tokens.nextToken();
                this.lengthStr = tokens.nextToken();
                this.strutLength = length;
                this.automaticDirectionIndex = orbit.isAutomatic() ? javaemul.internal.IntegerHelper.parseInt(this.orbitStr) : -1;
                this.realLength = length.evaluate();
                this.partClass = (<any>strut.constructor);
            } else if (((typeof strut === 'string') || strut === null) && ((orbits != null && (orbits["__class"] != null || ((t) => { try { new t; return true; } catch { return false; } })(orbits))) || orbits === null) && length === undefined) {
                let __args = arguments;
                let name: any = __args[0];
                let partType: any = __args[1];
                if (this.orbitStr === undefined) { this.orbitStr = null; } 
                if (this.rgbColor === undefined) { this.rgbColor = 0; } 
                if (this.sizeNameStr === undefined) { this.sizeNameStr = null; } 
                if (this.lengthStr === undefined) { this.lengthStr = null; } 
                if (this.strutLength === undefined) { this.strutLength = null; } 
                if (this.automaticDirectionIndex === undefined) { this.automaticDirectionIndex = null; } 
                if (this.realLength === undefined) { this.realLength = null; } 
                if (this.partClass === undefined) { this.partClass = null; } 
                this.orbitStr = "";
                this.rgbColor = Color.WHITE_$LI$().getRGB();
                this.sizeNameStr = name;
                this.lengthStr = "";
                this.strutLength = null;
                this.automaticDirectionIndex = -1;
                this.realLength = 0.0;
                this.partClass = partType;
            } else if (((strut != null && (strut.constructor != null && strut.constructor["__interfaces"] != null && strut.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)) || strut === null) && ((orbits != null && (orbits.constructor != null && orbits.constructor["__interfaces"] != null && orbits.constructor["__interfaces"].indexOf("com.vzome.core.editor.api.OrbitSource") >= 0)) || orbits === null) && length === undefined) {
                let __args = arguments;
                let panel: any = __args[0];
                if (this.orbitStr === undefined) { this.orbitStr = null; } 
                if (this.rgbColor === undefined) { this.rgbColor = 0; } 
                if (this.sizeNameStr === undefined) { this.sizeNameStr = null; } 
                if (this.lengthStr === undefined) { this.lengthStr = null; } 
                if (this.strutLength === undefined) { this.strutLength = null; } 
                if (this.automaticDirectionIndex === undefined) { this.automaticDirectionIndex = null; } 
                if (this.realLength === undefined) { this.realLength = null; } 
                if (this.partClass === undefined) { this.partClass = null; } 
                let orbitName: string = "";
                let color: Color = Color.WHITE_$LI$();
                let autoDirIdx: number = -1;
                const normal: AlgebraicVector = panel['getNormal$']();
                if (!normal.isOrigin()){
                    const axis: Axis = orbits.getAxis(normal);
                    if (axis != null){
                        const orbit: Direction = axis.getDirection();
                        orbitName = orbit.getName();
                        color = orbits.getColor(orbit);
                        if (orbit.isAutomatic()){
                            autoDirIdx = javaemul.internal.IntegerHelper.parseInt(orbitName);
                        }
                    }
                }
                this.orbitStr = orbitName;
                this.rgbColor = color.getRGB();
                this.sizeNameStr = "";
                this.lengthStr = "";
                this.strutLength = null;
                this.automaticDirectionIndex = autoDirIdx;
                this.realLength = 0.0;
                this.partClass = (<any>panel.constructor);
            } else if (((strut != null && (strut.constructor != null && strut.constructor["__interfaces"] != null && strut.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)) || strut === null) && orbits === undefined && length === undefined) {
                let __args = arguments;
                let ball: any = __args[0];
                if (this.orbitStr === undefined) { this.orbitStr = null; } 
                if (this.rgbColor === undefined) { this.rgbColor = 0; } 
                if (this.sizeNameStr === undefined) { this.sizeNameStr = null; } 
                if (this.lengthStr === undefined) { this.lengthStr = null; } 
                if (this.strutLength === undefined) { this.strutLength = null; } 
                if (this.automaticDirectionIndex === undefined) { this.automaticDirectionIndex = null; } 
                if (this.realLength === undefined) { this.realLength = null; } 
                if (this.partClass === undefined) { this.partClass = null; } 
                this.orbitStr = "";
                this.rgbColor = Color.WHITE_$LI$().getRGB();
                this.sizeNameStr = "";
                this.lengthStr = "";
                this.strutLength = null;
                this.automaticDirectionIndex = -1;
                this.realLength = 0.0;
                this.partClass = (<any>ball.constructor);
            } else throw new Error('invalid overload');
        }
    }
    PartInfo["__class"] = "com.vzome.desktop.controller.PartsController.PartInfo";

}

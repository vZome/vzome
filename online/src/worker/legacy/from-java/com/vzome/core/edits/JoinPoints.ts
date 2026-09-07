import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Command } from "../commands/Command.js";
import { XmlSaveFormat } from "../commands/XmlSaveFormat.js";
import { Point } from "../construction/Point.js";
import { Segment } from "../construction/Segment.js";
import { SegmentJoiningPoints } from "../construction/SegmentJoiningPoints.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { Connector } from "../model/Connector.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

export class JoinPoints extends ChangeManifestations {
    joinMode: JoinPoints.JoinModeEnum;

    public constructor(editor: EditorModel) {
        super(editor);
        this.joinMode = JoinPoints.JoinModeEnum.CLOSED_LOOP;
    }

    /**
     * 
     * @return {boolean}
     */
    groupingAware(): boolean {
        return true;
    }

    /**
     * 
     * @param {*} props
     */
    public configure(props: java.util.Map<string, any>) {
        const mode: string = <string>props.get("mode");
        if (mode != null)this.joinMode = /* Enum.valueOf */<any>JoinPoints.JoinModeEnum[mode];
    }

    public static ATTRNAME_CLOSEDLOOP: string = "closedLoop";

    public static ATTRNAME_JOINMODE: string = "joinMode";

    /**
     * 
     * @param {*} element
     */
    getXmlAttributes(element: Element) {
        if (this.joinMode === JoinPoints.JoinModeEnum.CLOSED_LOOP){
        } else {
            element.setAttribute(JoinPoints.ATTRNAME_CLOSEDLOOP, "false");
            if (this.joinMode !== JoinPoints.JoinModeEnum.CHAIN_BALLS)element.setAttribute(JoinPoints.ATTRNAME_JOINMODE, /* Enum.name */JoinPoints.JoinModeEnum[this.joinMode]);
        }
    }

    /**
     * 
     * @param {*} xml
     * @param {XmlSaveFormat} format
     */
    setXmlAttributes(xml: Element, format: XmlSaveFormat) {
        let attr: string = xml.getAttribute(JoinPoints.ATTRNAME_JOINMODE);
        if (attr != null && !/* isEmpty */(attr.length === 0)){
            this.joinMode = /* Enum.valueOf */<any>JoinPoints.JoinModeEnum[attr];
        } else {
            attr = xml.getAttribute(JoinPoints.ATTRNAME_CLOSEDLOOP);
            if (attr != null && !/* isEmpty */(attr.length === 0)){
                this.joinMode = javaemul.internal.BooleanHelper.parseBoolean(attr) ? JoinPoints.JoinModeEnum.CLOSED_LOOP : JoinPoints.JoinModeEnum.CHAIN_BALLS;
            } else {
                this.joinMode = JoinPoints.JoinModeEnum.CLOSED_LOOP;
            }
        }
    }

    /**
     * 
     */
    public perform() {
        const inputs: java.util.ArrayList<Point> = <any>(new java.util.ArrayList<any>());
        if (this.joinMode !== JoinPoints.JoinModeEnum.ALL_POSSIBLE)this.setOrderedSelection(true);
        for(let index=this.mSelection.iterator();index.hasNext();) {
            let man = index.next();
            {
                if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                    inputs.add(<Point>(<Connector><any>man).getFirstConstruction());
                }
                this.unselect$com_vzome_core_model_Manifestation(man);
            }
        }
        this.redo();
        const last: number = inputs.size() - 1;
        if (last > 0){
            const points: Point[] = inputs.toArray<any>([]);
            switch((this.joinMode)) {
            case JoinPoints.JoinModeEnum.CHAIN_BALLS:
                for(let i: number = 0; i < last; i++) {{
                    this.addSegment(points, i, i + 1);
                };}
                break;
            case JoinPoints.JoinModeEnum.CLOSED_LOOP:
                for(let i: number = 0; i < last; i++) {{
                    this.addSegment(points, i, i + 1);
                };}
                if (last > 1){
                    this.addSegment(points, last, 0);
                }
                break;
            case JoinPoints.JoinModeEnum.ALL_TO_FIRST:
                for(let i: number = 1; i <= last; i++) {{
                    this.addSegment(points, 0, i);
                };}
                break;
            case JoinPoints.JoinModeEnum.ALL_TO_LAST:
                for(let i: number = 0; i < last; i++) {{
                    this.addSegment(points, i, last);
                };}
                break;
            case JoinPoints.JoinModeEnum.ALL_POSSIBLE:
                for(let start: number = 0; start < last; start++) {{
                    for(let end: number = start + 1; end <= last; end++) {{
                        this.addSegment(points, start, end);
                    };}
                };}
                break;
            default:
                throw new Command.Failure("Unsupported JoinModeEnum: " + /* Enum.name */JoinPoints.JoinModeEnum[this.joinMode]);
            }
            this.redo();
        }
    }

    addSegment(points: Point[], start: number, end: number) {
        if ((start !== end) && !(points[start].getLocation().equals(points[end].getLocation()))){
            const segment: Segment = new SegmentJoiningPoints(points[start], points[end]);
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(segment));
        }
    }

    /**
     * 
     * @return {string}
     */
    getXmlElementName(): string {
        return "JoinPoints";
    }
}
JoinPoints["__class"] = "com.vzome.core.edits.JoinPoints";


export namespace JoinPoints {

    export enum JoinModeEnum {
        CHAIN_BALLS, CLOSED_LOOP, ALL_TO_FIRST, ALL_TO_LAST, ALL_POSSIBLE
    }
}

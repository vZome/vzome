import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { AlgebraicVectors } from "../algebra/AlgebraicVectors.js";
import { Command } from "../commands/Command.js";
import { XmlSaveFormat } from "../commands/XmlSaveFormat.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { ChangeSelection } from "../editor/api/ChangeSelection.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { Connector } from "../model/Connector.js";
import { Strut } from "../model/Strut.js";
import { DomUtils } from "../../xml/DomUtils.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

/**
 * @author David Hall
 * @param {*} editor
 * @class
 * @extends ChangeManifestations
 */
export class SelectCollinear extends ChangeManifestations {
    /*private*/ vector1: AlgebraicVector;

    /*private*/ vector2: AlgebraicVector;

    public constructor(editor: EditorModel) {
        super(editor);
        this.vector1 = null;
        this.vector2 = null;
    }

    /**
     * 
     * @param {*} props
     */
    public configure(props: java.util.Map<string, any>) {
        const strut: Strut = <Strut><any>props.get("picked");
        if (strut != null){
            this.vector1 = strut.getLocation();
            this.vector2 = strut.getEnd();
        }
    }

    /**
     * 
     */
    public perform() {
        if (this.vector1 == null || this.vector2 == null){
            const lastStrut: Strut = this.getLastSelectedStrut();
            if (lastStrut != null){
                this.vector1 = lastStrut.getLocation();
                this.vector2 = lastStrut.getEnd();
            } else {
                for(let index=this.getSelectedConnectors().iterator();index.hasNext();) {
                    let ball = index.next();
                    {
                        this.vector1 = this.vector2;
                        this.vector2 = ball.getLocation();
                    }
                }
            }
        }
        if (this.vector1 == null || this.vector2 == null){
            throw new Command.Failure("select a strut or two balls as a reference.");
        }
        this.unselectAll();
        const balls: java.util.Set<Connector> = <any>(new java.util.TreeSet<any>());
        for(let index=this.getVisibleConnectors$java_util_function_Predicate((ball) => { return this.isCollinearWith(ball) }).iterator();index.hasNext();) {
            let ball = index.next();
            {
                balls.add(ball);
            }
        }
        const struts: java.util.Set<Strut> = <any>(new java.util.TreeSet<any>());
        for(let index=this.getVisibleStruts$().iterator();index.hasNext();) {
            let strut = index.next();
            {
                if (this.isCollinearWith$com_vzome_core_model_Strut(strut)){
                    struts.add(strut);
                }
            }
        }
        for(let index=struts.iterator();index.hasNext();) {
            let strut = index.next();
            {
                this.select$com_vzome_core_model_Manifestation(strut);
            }
        }
        for(let index=balls.iterator();index.hasNext();) {
            let ball = index.next();
            {
                this.select$com_vzome_core_model_Manifestation(ball);
            }
        }
        const level: java.util.logging.Level = java.util.logging.Level.FINER;
        if (ChangeSelection.logger_$LI$().isLoggable(level)){
            const sb: java.lang.StringBuilder = new java.lang.StringBuilder("Selected:\n");
            const indent: string = "  ";
            for(let index=struts.iterator();index.hasNext();) {
                let strut = index.next();
                {
                    sb.append(indent).append(strut.toString()).append("\n");
                }
            }
            for(let index=balls.iterator();index.hasNext();) {
                let ball = index.next();
                {
                    sb.append(indent).append(ball.toString()).append("\n");
                }
            }
            ChangeSelection.logger_$LI$().log(level, sb.toString());
        }
        super.perform();
    }

    public isCollinearWith$com_vzome_core_model_Connector(ball: Connector): boolean {
        return this.isCollinear(ball.getLocation());
    }

    public isCollinearWith(ball?: any): boolean {
        if (((ball != null && (ball.constructor != null && ball.constructor["__interfaces"] != null && ball.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)) || ball === null)) {
            return <any>this.isCollinearWith$com_vzome_core_model_Connector(ball);
        } else if (((ball != null && (ball.constructor != null && ball.constructor["__interfaces"] != null && ball.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)) || ball === null)) {
            return <any>this.isCollinearWith$com_vzome_core_model_Strut(ball);
        } else throw new Error('invalid overload');
    }

    /*private*/ isCollinearWith$com_vzome_core_model_Strut(strut: Strut): boolean {
        return this.isCollinear(strut.getLocation()) && this.isCollinear(strut.getEnd());
    }

    /*private*/ isCollinear(vec: AlgebraicVector): boolean {
        return AlgebraicVectors.areCollinear$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector(vec, this.vector1, this.vector2);
    }

    /**
     * 
     * @return {string}
     */
    getXmlElementName(): string {
        return "SelectCollinear";
    }

    /**
     * 
     * @param {*} element
     */
    getXmlAttributes(element: Element) {
        DomUtils.addAttribute(element, "vector1", this.vector1.toParsableString());
        DomUtils.addAttribute(element, "vector2", this.vector2.toParsableString());
    }

    /**
     * 
     * @param {*} xml
     * @param {XmlSaveFormat} format
     */
    setXmlAttributes(xml: Element, format: XmlSaveFormat) {
        this.vector1 = format.parseRationalVector(xml, "vector1");
        this.vector2 = format.parseRationalVector(xml, "vector2");
    }
}
SelectCollinear["__class"] = "com.vzome.core.edits.SelectCollinear";

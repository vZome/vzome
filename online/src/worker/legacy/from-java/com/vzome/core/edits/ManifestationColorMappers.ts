import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { AlgebraicVectors } from "../algebra/AlgebraicVectors.js";
import { Command } from "../commands/Command.js";
import { Color } from "../construction/Color.js";
import { Manifestations } from "../editor/api/Manifestations.js";
import { OrbitSource } from "../editor/api/OrbitSource.js";
import { ColorMappers } from "./ColorMappers.js";
import { Axis } from "../math/symmetry/Axis.js";
import { Direction } from "../math/symmetry/Direction.js";
import { SpecialOrbit } from "../math/symmetry/SpecialOrbit.js";
import { Connector } from "../model/Connector.js";
import { Manifestation } from "../model/Manifestation.js";
import { Panel } from "../model/Panel.js";
import { Strut } from "../model/Strut.js";
import { DomUtils } from "../../xml/DomUtils.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

/**
 * @author David Hall
 * @class
 */
export class ManifestationColorMappers {
    static __static_initialized: boolean = false;
    static __static_initialize() { if (!ManifestationColorMappers.__static_initialized) { ManifestationColorMappers.__static_initialized = true; ManifestationColorMappers.__static_initializer_0(); } }

    static colorMappers: java.util.Map<string, ManifestationColorMappers.ManifestationColorMapper>; public static colorMappers_$LI$(): java.util.Map<string, ManifestationColorMappers.ManifestationColorMapper> { ManifestationColorMappers.__static_initialize(); if (ManifestationColorMappers.colorMappers == null) { ManifestationColorMappers.colorMappers = <any>(new java.util.HashMap<any, any>()); }  return ManifestationColorMappers.colorMappers; }

    static  __static_initializer_0() {
        ManifestationColorMappers.RegisterMapper(new ManifestationColorMappers.RadialCentroidColorMap());
        ManifestationColorMappers.RegisterMapper(new ManifestationColorMappers.RadialStandardBasisColorMap());
        ManifestationColorMappers.RegisterMapper(new ManifestationColorMappers.CanonicalOrientationColorMap());
        ManifestationColorMappers.RegisterMapper(new ManifestationColorMappers.NormalPolarityColorMap());
        ManifestationColorMappers.RegisterMapper(new ManifestationColorMappers.CentroidByOctantAndDirectionColorMap());
        ManifestationColorMappers.RegisterMapper(new ManifestationColorMappers.CoordinatePlaneColorMap());
        ManifestationColorMappers.RegisterMapper(new ManifestationColorMappers.Identity());
        ManifestationColorMappers.RegisterMapper(new ManifestationColorMappers.ColorComplementor());
        ManifestationColorMappers.RegisterMapper(new ManifestationColorMappers.ColorInverter());
        ManifestationColorMappers.RegisterMapper(new ManifestationColorMappers.ColorMaximizer());
        ManifestationColorMappers.RegisterMapper(new ManifestationColorMappers.ColorSoftener());
    }

    public static RegisterMapper(mapper: ManifestationColorMappers.ManifestationColorMapper) {
        if (mapper != null){
            ManifestationColorMappers.colorMappers_$LI$().put(mapper.getName(), mapper);
            if (mapper.getName() === ("ColorComplementor"))ManifestationColorMappers.colorMappers_$LI$().put("ColorComplimentor", mapper);
        }
    }

    static getColorMapper$java_lang_String(mapperName: string): ManifestationColorMappers.ManifestationColorMapper {
        const strTransparency: string = "TransparencyMapper@";
        if (/* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(mapperName, strTransparency)){
            const strAlpha: string = mapperName.substring(strTransparency.length);
            const alpha: number = javaemul.internal.IntegerHelper.parseInt(strAlpha);
            return new ManifestationColorMappers.TransparencyMapper(alpha);
        }
        switch((mapperName)) {
        case "TransparencyMapper":
            return new ManifestationColorMappers.TransparencyMapper(255);
        case "DarkenWithDistance":
            return new ManifestationColorMappers.DarkenWithDistance();
        case "DarkenNearOrigin":
            return new ManifestationColorMappers.DarkenNearOrigin();
        case "CopyLastSelectedColor":
            return new ManifestationColorMappers.CopyLastSelectedColor();
        }
        return ManifestationColorMappers.colorMappers_$LI$().get(mapperName);
    }

    public static getColorMapper$java_lang_String$com_vzome_core_editor_api_OrbitSource(mapperName: string, symmetry: OrbitSource): ManifestationColorMappers.ManifestationColorMapper {
        let colorMapper: ManifestationColorMappers.ManifestationColorMapper = mapperName === ("SystemColorMap") ? new ManifestationColorMappers.SystemColorMap(symmetry) : mapperName === ("SystemCentroidColorMap") ? new ManifestationColorMappers.SystemCentroidColorMap(symmetry) : mapperName === ("NearestSpecialOrbitColorMap") ? new ManifestationColorMappers.NearestSpecialOrbitColorMap(symmetry) : mapperName === ("CentroidNearestSpecialOrbitColorMap") ? new ManifestationColorMappers.CentroidNearestSpecialOrbitColorMap(symmetry) : mapperName === ("NearestPredefinedOrbitColorMap") ? new ManifestationColorMappers.NearestPredefinedOrbitColorMap(symmetry) : mapperName === ("CentroidNearestPredefinedOrbitColorMap") ? new ManifestationColorMappers.CentroidNearestPredefinedOrbitColorMap(symmetry) : ManifestationColorMappers.getColorMapper$java_lang_String(mapperName);
        if (colorMapper == null){
            colorMapper = new ManifestationColorMappers.Identity();
        }
        return colorMapper;
    }

    public static getColorMapper(mapperName?: any, symmetry?: any): ManifestationColorMappers.ManifestationColorMapper {
        if (((typeof mapperName === 'string') || mapperName === null) && ((symmetry != null && (symmetry.constructor != null && symmetry.constructor["__interfaces"] != null && symmetry.constructor["__interfaces"].indexOf("com.vzome.core.editor.api.OrbitSource") >= 0)) || symmetry === null)) {
            return <any>ManifestationColorMappers.getColorMapper$java_lang_String$com_vzome_core_editor_api_OrbitSource(mapperName, symmetry);
        } else if (((typeof mapperName === 'string') || mapperName === null) && symmetry === undefined) {
            return <any>ManifestationColorMappers.getColorMapper$java_lang_String(mapperName);
        } else throw new Error('invalid overload');
    }

    static mapPolarity(vector: AlgebraicVector, alpha: number): Color {
        const polarity: number = vector.compareTo(vector.negate());
        const mid: number = 128;
        const diff: number = 64;
        const shade: number = polarity < 0 ? mid - diff : polarity > 0 ? mid + diff : mid;
        return new Color(shade, shade, shade, alpha);
    }

    /**
     * @param {AlgebraicVector} vector could be midpoint, start, end, normal, or any basis for mapping to a color
     * @param {number} alpha the transparency component of the resulting color.
     * @return
     * @return {Color}
     */
    static mapRadially(vector: AlgebraicVector, alpha: number): Color {
        const midPoint: number = 127;
        const rgb: number[] = [midPoint, midPoint, midPoint];
        const parts: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(rgb.length);
        const dimensions: number = Math.min(rgb.length, vector.dimension());
        let whole: number = 0.0;
        for(let i: number = 0; i < dimensions; i++) {{
            const component: AlgebraicNumber = vector.getComponent(i);
            parts[i] = component.evaluate();
            whole += Math.abs(parts[i]);
        };}
        if (whole !== 0.0){
            for(let i: number = 0; i < parts.length; i++) {{
                const part: number = (parts[i] / whole);
                const contribution: number = part * midPoint;
                rgb[i] = /* intValue */(contribution|0) + midPoint;
                rgb[i] = Math.min(255, rgb[i]);
                rgb[i] = Math.max(0, rgb[i]);
            };}
        }
        return new Color(rgb[0], rgb[1], rgb[2], alpha);
    }

    /**
     * @param {AlgebraicVector} vector could be midpoint, start, end, normal, or any basis for mapping to a color
     * @param {number} alpha the transparency component of the resulting color.
     * @param {number} neg the R, G or B level of vectors with a negative value in the corresponding X, Y, or Z dimension.
     * @param {number} zero the R, G or B level of vectors with a zero value in the corresponding X, Y, or Z dimension.
     * @param {number} pos the R, G or B level of vectors with a positive value in the corresponding X, Y, or Z dimension.
     * @return
     * @return {Color}
     */
    static mapToOctant(vector: AlgebraicVector, alpha: number, neg: number, zero: number, pos: number): Color {
        const src: number[] = [neg, zero, pos];
        const rgb: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(src.length);
        const dimensions: number = Math.min(rgb.length, vector.dimension());
        for(let i: number = 0; i < dimensions; i++) {{
            const component: AlgebraicNumber = vector.getComponent(i);
            const dir: number = /* signum */(f => { if (f > 0) { return 1; } else if (f < 0) { return -1; } else { return 0; } })(component.evaluate());
            const index: number = /* intValue */(dir|0) + 1;
            rgb[i] = src[index];
        };}
        return new Color(rgb[0], rgb[1], rgb[2], alpha);
    }

    static mapToMagnitude(vector: AlgebraicVector, offset: number, fullScaleSquared: number, initialColor: Color): Color {
        if (vector == null || initialColor == null){
            return initialColor;
        }
        const magnitudeSquared: number = AlgebraicVectors.getMagnitudeSquared(vector).evaluate();
        const denominator: number = (fullScaleSquared === 0.0) ? 1.0E-4 : fullScaleSquared;
        const scale: number = Math.abs(offset - magnitudeSquared) / denominator;
        return Color.getScaledTo(initialColor, scale);
    }
}
ManifestationColorMappers["__class"] = "com.vzome.core.edits.ManifestationColorMappers";


export namespace ManifestationColorMappers {

    /**
     * Common abstract base class adds xml persistence
     * and late loading of criteria based on selection and/or model
     * @class
     */
    export abstract class ManifestationColorMapper implements ColorMappers.ColorMapper<Manifestation> {
        /* Default method injected from ColorMappers.ColorMapper */
        public requiresOrderedSelection(): boolean {
            return false;
        }
        constructor() {
        }

        /**
         * Optional opportunity to initialize parameters that were not available at time of the constructor
         * but are determined based on the selection or model iterator
         * just before apply is called on each individual manifestation.
         * @param selection
         * @param model
         * @param {Manifestations.ManifestationIterator} manifestations
         */
        public initialize(manifestations: Manifestations.ManifestationIterator) {
        }

        public apply$com_vzome_core_model_Manifestation(man: Manifestation): Color {
            return (man == null || !man.isRendered()) ? null : this.applyTo(man);
        }

        /**
         * 
         * @param {*} man
         * @return {Color}
         */
        public apply(man?: any): Color {
            if (((man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Manifestation") >= 0)) || man === null)) {
                return <any>this.apply$com_vzome_core_model_Manifestation(man);
            } else throw new Error('invalid overload');
        }

        applyTo(manifestation: Manifestation): Color {
            let color: Color = manifestation.getColor();
            if (color == null){
                color = Color.WHITE_$LI$();
            }
            return color;
        }

        /**
         * subclasses should call {@code result.setAttribute()} if they have any parameters to persist
         * @param {*} result
         */
        getXmlAttributes(result: Element) {
        }

        /**
         * subclasses should call {@code xml.getAttribute()} to retrieve any persisted parameters
         * @param {*} xml
         */
        setXmlAttributes(xml: Element) {
        }

        public abstract getName(): any;        }
    ManifestationColorMapper["__class"] = "com.vzome.core.edits.ManifestationColorMappers.ManifestationColorMapper";
    ManifestationColorMapper["__interfaces"] = ["com.vzome.core.edits.ColorMappers.ColorMapper","java.util.function.Function"];



    /**
     * returns current color
     * @class
     * @extends ManifestationColorMappers.ManifestationColorMapper
     */
    export class Identity extends ManifestationColorMappers.ManifestationColorMapper {
        /**
         * 
         * @return {string}
         */
        public getName(): string {
            return "Identity";
        }

        /**
         * 
         * @param {*} rendered
         * @return {Color}
         */
        applyTo(rendered: Manifestation): Color {
            return rendered.getColor();
        }

        constructor() {
            super();
        }
    }
    Identity["__class"] = "com.vzome.core.edits.ManifestationColorMappers.Identity";
    Identity["__interfaces"] = ["com.vzome.core.edits.ColorMappers.ColorMapper","java.util.function.Function"];



    /**
     * returns complementary color
     * @class
     * @extends ManifestationColorMappers.ManifestationColorMapper
     */
    export class ColorComplementor extends ManifestationColorMappers.ManifestationColorMapper {
        /**
         * 
         * @return {string}
         */
        public getName(): string {
            return "ColorComplementor";
        }

        /**
         * 
         * @param {*} rendered
         * @return {Color}
         */
        applyTo(rendered: Manifestation): Color {
            return Color.getComplement(super.applyTo(rendered));
        }

        constructor() {
            super();
        }
    }
    ColorComplementor["__class"] = "com.vzome.core.edits.ManifestationColorMappers.ColorComplementor";
    ColorComplementor["__interfaces"] = ["com.vzome.core.edits.ColorMappers.ColorMapper","java.util.function.Function"];



    /**
     * returns inverted color
     * @class
     * @extends ManifestationColorMappers.ManifestationColorMapper
     */
    export class ColorInverter extends ManifestationColorMappers.ManifestationColorMapper {
        /**
         * 
         * @return {string}
         */
        public getName(): string {
            return "ColorInverter";
        }

        /**
         * 
         * @param {*} rendered
         * @return {Color}
         */
        applyTo(rendered: Manifestation): Color {
            return Color.getInverted(super.applyTo(rendered));
        }

        constructor() {
            super();
        }
    }
    ColorInverter["__class"] = "com.vzome.core.edits.ManifestationColorMappers.ColorInverter";
    ColorInverter["__interfaces"] = ["com.vzome.core.edits.ColorMappers.ColorMapper","java.util.function.Function"];



    /**
     * returns maximized color
     * @class
     * @extends ManifestationColorMappers.ManifestationColorMapper
     */
    export class ColorMaximizer extends ManifestationColorMappers.ManifestationColorMapper {
        /**
         * 
         * @return {string}
         */
        public getName(): string {
            return "ColorMaximizer";
        }

        /**
         * 
         * @param {*} rendered
         * @return {Color}
         */
        applyTo(rendered: Manifestation): Color {
            return Color.getMaximum(super.applyTo(rendered));
        }

        constructor() {
            super();
        }
    }
    ColorMaximizer["__class"] = "com.vzome.core.edits.ManifestationColorMappers.ColorMaximizer";
    ColorMaximizer["__interfaces"] = ["com.vzome.core.edits.ColorMappers.ColorMapper","java.util.function.Function"];



    /**
     * returns pastel of current color
     * @class
     * @extends ManifestationColorMappers.ManifestationColorMapper
     */
    export class ColorSoftener extends ManifestationColorMappers.ManifestationColorMapper {
        /**
         * 
         * @return {string}
         */
        public getName(): string {
            return "ColorSoftener";
        }

        /**
         * 
         * @param {*} rendered
         * @return {Color}
         */
        applyTo(rendered: Manifestation): Color {
            return Color.getPastel(super.applyTo(rendered));
        }

        constructor() {
            super();
        }
    }
    ColorSoftener["__class"] = "com.vzome.core.edits.ManifestationColorMappers.ColorSoftener";
    ColorSoftener["__interfaces"] = ["com.vzome.core.edits.ColorMappers.ColorMapper","java.util.function.Function"];



    export class TransparencyMapper extends ManifestationColorMappers.ManifestationColorMapper {
        /**
         * 
         * @return {string}
         */
        public getName(): string {
            return "TransparencyMapper";
        }

        alpha: number;

        public constructor(alpha: number) {
            super();
            if (this.alpha === undefined) { this.alpha = 0; }
            this.setAlpha(alpha);
        }

        setAlpha(value: number) {
            this.alpha = Math.min(255, Math.max(1, value));
        }

        static ALPHA_ATTR_NAME: string = "alpha";

        /**
         * 
         * @param {*} xml
         */
        setXmlAttributes(xml: Element) {
            this.alpha = javaemul.internal.IntegerHelper.parseInt(xml.getAttribute(TransparencyMapper.ALPHA_ATTR_NAME));
        }

        /**
         * 
         * @param {*} result
         */
        getXmlAttributes(result: Element) {
            result.setAttribute(TransparencyMapper.ALPHA_ATTR_NAME, /* toString */(''+(this.alpha)));
        }

        /**
         * 
         * @param {*} rendered
         * @return {Color}
         */
        applyTo(rendered: Manifestation): Color {
            const color: Color = super.applyTo(rendered);
            return new Color(color.getRed(), color.getGreen(), color.getBlue(), this.alpha);
        }
    }
    TransparencyMapper["__class"] = "com.vzome.core.edits.ManifestationColorMappers.TransparencyMapper";
    TransparencyMapper["__interfaces"] = ["com.vzome.core.edits.ColorMappers.ColorMapper","java.util.function.Function"];



    export class CopyLastSelectedColor extends ManifestationColorMappers.ManifestationColorMapper {
        /**
         * 
         * @return {string}
         */
        public getName(): string {
            return "CopyLastSelectedColor";
        }

        color: Color;

        /**
         * 
         * @return {boolean}
         */
        public requiresOrderedSelection(): boolean {
            return true;
        }

        /**
         * 
         * @param {Manifestations.ManifestationIterator} selection
         */
        public initialize(selection: Manifestations.ManifestationIterator) {
            if (this.color == null){
                let last: Manifestation = null;
                for(let index=selection.iterator();index.hasNext();) {
                    let man = index.next();
                    {
                        if (man != null && man.isRendered()){
                            last = man;
                        }
                    }
                }
                if (last != null){
                    this.color = last.getColor();
                }
            }
            if (this.color == null){
                throw new Command.Failure("select a ball, strut or panel as the color to be copied.");
            }
        }

        /**
         * 
         * @param {*} xml
         */
        setXmlAttributes(xml: Element) {
            const red: string = xml.getAttribute("red");
            const green: string = xml.getAttribute("green");
            const blue: string = xml.getAttribute("blue");
            const alphaStr: string = xml.getAttribute("alpha");
            const alpha: number = (alphaStr == null || /* isEmpty */(alphaStr.length === 0)) ? 255 : javaemul.internal.IntegerHelper.parseInt(alphaStr);
            this.color = new Color(javaemul.internal.IntegerHelper.parseInt(red), javaemul.internal.IntegerHelper.parseInt(green), javaemul.internal.IntegerHelper.parseInt(blue), alpha);
        }

        /**
         * 
         * @param {*} result
         */
        getXmlAttributes(result: Element) {
            result.setAttribute("red", "" + this.color.getRed());
            result.setAttribute("green", "" + this.color.getGreen());
            result.setAttribute("blue", "" + this.color.getBlue());
            const alpha: number = this.color.getAlpha();
            if (alpha < 255)result.setAttribute("alpha", "" + alpha);
        }

        /**
         * 
         * @param {*} rendered
         * @return {Color}
         */
        applyTo(rendered: Manifestation): Color {
            return this.color;
        }

        constructor() {
            super();
            if (this.color === undefined) { this.color = null; }
        }
    }
    CopyLastSelectedColor["__class"] = "com.vzome.core.edits.ManifestationColorMappers.CopyLastSelectedColor";
    CopyLastSelectedColor["__interfaces"] = ["com.vzome.core.edits.ColorMappers.ColorMapper","java.util.function.Function"];



    /**
     * Handles getting the centroid and calling overloaded methods to map the subClass specific AlgebraicVector
     * @extends ManifestationColorMappers.ManifestationColorMapper
     * @class
     */
    export abstract class CentroidColorMapper extends ManifestationColorMappers.ManifestationColorMapper {
        constructor() {
            super();
        }

        public applyTo$com_vzome_core_model_Manifestation(rendered: Manifestation): Color {
            const color: Color = rendered.getColor();
            const alpha: number = color == null ? 255 : color.getAlpha();
            return this.applyTo$com_vzome_core_algebra_AlgebraicVector$int(rendered.getCentroid(), alpha);
        }

        public applyTo$com_vzome_core_algebra_AlgebraicVector$int(centroid: AlgebraicVector, alpha: number): Color { throw new Error('cannot invoke abstract overloaded method... check your argument(s) type(s)'); }

        public applyTo(centroid?: any, alpha?: any): Color {
            if (((centroid != null && centroid instanceof <any>AlgebraicVector) || centroid === null) && ((typeof alpha === 'number') || alpha === null)) {
                return <any>this.applyTo$com_vzome_core_algebra_AlgebraicVector$int(centroid, alpha);
            } else if (((centroid != null && (centroid.constructor != null && centroid.constructor["__interfaces"] != null && centroid.constructor["__interfaces"].indexOf("com.vzome.core.model.Manifestation") >= 0)) || centroid === null) && alpha === undefined) {
                return <any>this.applyTo$com_vzome_core_model_Manifestation(centroid);
            } else throw new Error('invalid overload');
        }
    }
    CentroidColorMapper["__class"] = "com.vzome.core.edits.ManifestationColorMappers.CentroidColorMapper";
    CentroidColorMapper["__interfaces"] = ["com.vzome.core.edits.ColorMappers.ColorMapper","java.util.function.Function"];



    /**
     * Scales the intensity of the current color of each Manifestation
     * based on the distance of its centroid from the origin.
     * A position ranging from the origin to the fullScale vector position
     * adjusts the intensity of the current color from darkest to lightest.
     * @class
     * @extends ManifestationColorMappers.ManifestationColorMapper
     */
    export class DarkenNearOrigin extends ManifestationColorMappers.ManifestationColorMapper {
        /**
         * 
         * @return {string}
         */
        public getName(): string {
            return "DarkenNearOrigin";
        }

        offset: number;

        fullScaleSquared: number;

        /**
         * 
         * @param {Manifestations.ManifestationIterator} manifestations
         */
        public initialize(manifestations: Manifestations.ManifestationIterator) {
            if (this.fullScaleSquared === 0){
                const fullScale: AlgebraicVector = DarkenNearOrigin.getMostDistantPoint(manifestations);
                if (fullScale == null){
                    throw new Command.Failure("unable to determine most distant point");
                }
                if (fullScale.isOrigin()){
                    throw new Command.Failure("select at least one point other than the origin");
                }
                this.fullScaleSquared = AlgebraicVectors.getMagnitudeSquared(fullScale).evaluate();
            }
        }

        static getMostDistantPoint(manifestations: Manifestations.ManifestationIterator): AlgebraicVector {
            const centroids: java.util.List<AlgebraicVector> = <any>(new java.util.ArrayList<any>());
            for(let index=manifestations.iterator();index.hasNext();) {
                let man = index.next();
                {
                    centroids.add(man.getCentroid());
                }
            }
            if (centroids.isEmpty()){
                return null;
            }
            const mostDistant: java.util.TreeSet<AlgebraicVector> = AlgebraicVectors.getMostDistantFromOrigin(centroids);
            return mostDistant.isEmpty() ? null : mostDistant.first();
        }

        /**
         * 
         * @param {*} rendered
         * @return {Color}
         */
        applyTo(rendered: Manifestation): Color {
            const centroid: AlgebraicVector = rendered.getCentroid();
            const initialColor: Color = super.applyTo(rendered);
            return ManifestationColorMappers.mapToMagnitude(centroid, this.offset, this.fullScaleSquared, initialColor);
        }

        FULLSCALESQUARED_ATTR_NAME: string;

        /**
         * 
         * @param {*} result
         */
        getXmlAttributes(result: Element) {
            result.setAttribute(this.FULLSCALESQUARED_ATTR_NAME, /* toString */(''+(this.fullScaleSquared)));
        }

        /**
         * 
         * @param {*} xml
         */
        setXmlAttributes(xml: Element) {
            const attr: string = xml.getAttribute(this.FULLSCALESQUARED_ATTR_NAME);
            this.fullScaleSquared = javaemul.internal.DoubleHelper.parseDouble(attr);
        }

        constructor() {
            super();
            this.offset = 0;
            this.fullScaleSquared = 0;
            this.FULLSCALESQUARED_ATTR_NAME = "fullScaleSquared";
        }
    }
    DarkenNearOrigin["__class"] = "com.vzome.core.edits.ManifestationColorMappers.DarkenNearOrigin";
    DarkenNearOrigin["__interfaces"] = ["com.vzome.core.edits.ColorMappers.ColorMapper","java.util.function.Function"];



    /**
     * Abstract base class which calls subclass specific abstract overloads for all known subtypes.
     * @class
     * @extends ManifestationColorMappers.ManifestationColorMapper
     */
    export abstract class ManifestationSubclassColorMapper extends ManifestationColorMappers.ManifestationColorMapper {
        /**
         * 
         * @param {*} man
         * @return {Color}
         */
        applyTo(man: Manifestation): Color {
            const color: Color = man.getColor();
            const alpha: number = color == null ? 255 : color.getAlpha();
            if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                return this.applyToBall(<Connector><any>man, alpha);
            } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                return this.applyToStrut(<Strut><any>man, alpha);
            } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                return this.applyToPanel(<Panel><any>man, alpha);
            }
            return null;
        }

        abstract applyToBall(ball: Connector, alpha: number): Color;

        abstract applyToStrut(strut: Strut, alpha: number): Color;

        abstract applyToPanel(panel: Panel, alpha: number): Color;

        constructor() {
            super();
        }
    }
    ManifestationSubclassColorMapper["__class"] = "com.vzome.core.edits.ManifestationColorMappers.ManifestationSubclassColorMapper";
    ManifestationSubclassColorMapper["__interfaces"] = ["com.vzome.core.edits.ColorMappers.ColorMapper","java.util.function.Function"];



    /**
     * Maps vector XYZ components to RGB
     * such that each RGB component is weighted by the contribution
     * of the corresponding XYZ component
     * and offset by half of the color range so that a
     * + directions map between 0x7F and 0xFF color element
     * 0 direction maps to a midrange    0x7F color element
     * - directions map between 0x00 and 0x7F color element
     * 
     * Polarity info IS retained by this mapping.
     * @class
     * @extends ManifestationColorMappers.CentroidColorMapper
     */
    export class RadialCentroidColorMap extends ManifestationColorMappers.CentroidColorMapper {
        /**
         * 
         * @return {string}
         */
        public getName(): string {
            return "RadialCentroidColorMap";
        }

        public applyTo$com_vzome_core_algebra_AlgebraicVector$int(centroid: AlgebraicVector, alpha: number): Color {
            return ManifestationColorMappers.mapRadially(centroid, alpha);
        }

        /**
         * 
         * @param {AlgebraicVector} centroid
         * @param {number} alpha
         * @return {Color}
         */
        public applyTo(centroid?: any, alpha?: any): Color {
            if (((centroid != null && centroid instanceof <any>AlgebraicVector) || centroid === null) && ((typeof alpha === 'number') || alpha === null)) {
                return <any>this.applyTo$com_vzome_core_algebra_AlgebraicVector$int(centroid, alpha);
            } else if (((centroid != null && (centroid.constructor != null && centroid.constructor["__interfaces"] != null && centroid.constructor["__interfaces"].indexOf("com.vzome.core.model.Manifestation") >= 0)) || centroid === null) && alpha === undefined) {
                return <any>this.applyTo$com_vzome_core_model_Manifestation(centroid);
            } else throw new Error('invalid overload');
        }

        constructor() {
            super();
        }
    }
    RadialCentroidColorMap["__class"] = "com.vzome.core.edits.ManifestationColorMappers.RadialCentroidColorMap";
    RadialCentroidColorMap["__interfaces"] = ["com.vzome.core.edits.ColorMappers.ColorMapper","java.util.function.Function"];



    /**
     * Maps vector XYZ components to RGB by Octant
     * 
     * Polarity info IS retained by this mapping.
     * @class
     * @extends ManifestationColorMappers.CentroidColorMapper
     */
    export class CentroidByOctantAndDirectionColorMap extends ManifestationColorMappers.CentroidColorMapper {
        /**
         * 
         * @return {string}
         */
        public getName(): string {
            return "CentroidByOctantAndDirectionColorMap";
        }

        public applyTo$com_vzome_core_algebra_AlgebraicVector$int(vector: AlgebraicVector, alpha: number): Color {
            return Color.getMaximum(ManifestationColorMappers.mapToOctant(vector, alpha, 0, 127, 255));
        }

        /**
         * 
         * @param {AlgebraicVector} vector
         * @param {number} alpha
         * @return {Color}
         */
        public applyTo(vector?: any, alpha?: any): Color {
            if (((vector != null && vector instanceof <any>AlgebraicVector) || vector === null) && ((typeof alpha === 'number') || alpha === null)) {
                return <any>this.applyTo$com_vzome_core_algebra_AlgebraicVector$int(vector, alpha);
            } else if (((vector != null && (vector.constructor != null && vector.constructor["__interfaces"] != null && vector.constructor["__interfaces"].indexOf("com.vzome.core.model.Manifestation") >= 0)) || vector === null) && alpha === undefined) {
                return <any>this.applyTo$com_vzome_core_model_Manifestation(vector);
            } else throw new Error('invalid overload');
        }

        constructor() {
            super();
        }
    }
    CentroidByOctantAndDirectionColorMap["__class"] = "com.vzome.core.edits.ManifestationColorMappers.CentroidByOctantAndDirectionColorMap";
    CentroidByOctantAndDirectionColorMap["__interfaces"] = ["com.vzome.core.edits.ColorMappers.ColorMapper","java.util.function.Function"];



    /**
     * Maps vector XYZ components to RGB
     * corresponding to the X, Y or Z coordinate plane.
     * 
     * Polarity info IS NOT retained by this mapping.
     * @class
     * @extends ManifestationColorMappers.CentroidColorMapper
     */
    export class CoordinatePlaneColorMap extends ManifestationColorMappers.CentroidColorMapper {
        /**
         * 
         * @return {string}
         */
        public getName(): string {
            return "CoordinatePlaneColorMap";
        }

        public applyTo$com_vzome_core_algebra_AlgebraicVector$int(vector: AlgebraicVector, alpha: number): Color {
            return Color.getInverted(ManifestationColorMappers.mapToOctant(vector, alpha, 0, 255, 0));
        }

        /**
         * 
         * @param {AlgebraicVector} vector
         * @param {number} alpha
         * @return {Color}
         */
        public applyTo(vector?: any, alpha?: any): Color {
            if (((vector != null && vector instanceof <any>AlgebraicVector) || vector === null) && ((typeof alpha === 'number') || alpha === null)) {
                return <any>this.applyTo$com_vzome_core_algebra_AlgebraicVector$int(vector, alpha);
            } else if (((vector != null && (vector.constructor != null && vector.constructor["__interfaces"] != null && vector.constructor["__interfaces"].indexOf("com.vzome.core.model.Manifestation") >= 0)) || vector === null) && alpha === undefined) {
                return <any>this.applyTo$com_vzome_core_model_Manifestation(vector);
            } else throw new Error('invalid overload');
        }

        constructor() {
            super();
        }
    }
    CoordinatePlaneColorMap["__class"] = "com.vzome.core.edits.ManifestationColorMappers.CoordinatePlaneColorMap";
    CoordinatePlaneColorMap["__interfaces"] = ["com.vzome.core.edits.ColorMappers.ColorMapper","java.util.function.Function"];



    /**
     * Maps standard SymmetrySystem colors
     * to the Manifestation's Centroid instead of the normal vector
     * @extends ManifestationColorMappers.CentroidColorMapper
     * @class
     */
    export class SystemCentroidColorMap extends ManifestationColorMappers.CentroidColorMapper {
        /**
         * 
         * @return {string}
         */
        public getName(): string {
            return "SystemCentroidColorMap";
        }

        symmetrySystem: OrbitSource;

        constructor(symmetry: OrbitSource) {
            super();
            if (this.symmetrySystem === undefined) { this.symmetrySystem = null; }
            this.symmetrySystem = symmetry;
        }

        public applyTo$com_vzome_core_algebra_AlgebraicVector$int(centroid: AlgebraicVector, alpha: number): Color {
            return this.symmetrySystem.getVectorColor(centroid);
        }

        /**
         * 
         * @param {AlgebraicVector} centroid
         * @param {number} alpha
         * @return {Color}
         */
        public applyTo(centroid?: any, alpha?: any): Color {
            if (((centroid != null && centroid instanceof <any>AlgebraicVector) || centroid === null) && ((typeof alpha === 'number') || alpha === null)) {
                return <any>this.applyTo$com_vzome_core_algebra_AlgebraicVector$int(centroid, alpha);
            } else if (((centroid != null && (centroid.constructor != null && centroid.constructor["__interfaces"] != null && centroid.constructor["__interfaces"].indexOf("com.vzome.core.model.Manifestation") >= 0)) || centroid === null) && alpha === undefined) {
                return <any>this.applyTo$com_vzome_core_model_Manifestation(centroid);
            } else throw new Error('invalid overload');
        }

        /**
         * 
         * @param {*} element
         */
        getXmlAttributes(element: Element) {
            if (this.symmetrySystem != null){
                DomUtils.addAttribute(element, "symmetry", this.symmetrySystem.getName());
            }
        }
    }
    SystemCentroidColorMap["__class"] = "com.vzome.core.edits.ManifestationColorMappers.SystemCentroidColorMap";
    SystemCentroidColorMap["__interfaces"] = ["com.vzome.core.edits.ColorMappers.ColorMapper","java.util.function.Function"];



    /**
     * Same as {@code DarkenNearOrigin} except that
     * the color mapping is reversed from lightest to darkest
     * @class
     * @extends ManifestationColorMappers.DarkenNearOrigin
     */
    export class DarkenWithDistance extends ManifestationColorMappers.DarkenNearOrigin {
        /**
         * 
         * @return {string}
         */
        public getName(): string {
            return "DarkenWithDistance";
        }

        /**
         * 
         * @param {Manifestations.ManifestationIterator} manifestations
         */
        public initialize(manifestations: Manifestations.ManifestationIterator) {
            super.initialize(manifestations);
            this.offset = this.fullScaleSquared;
        }

        constructor() {
            super();
        }
    }
    DarkenWithDistance["__class"] = "com.vzome.core.edits.ManifestationColorMappers.DarkenWithDistance";
    DarkenWithDistance["__interfaces"] = ["com.vzome.core.edits.ColorMappers.ColorMapper","java.util.function.Function"];



    /**
     * Polarity info is retained by this mapping
     * so that inverted struts and panels will be mapped to inverted colors.
     * @class
     * @extends ManifestationColorMappers.ManifestationSubclassColorMapper
     */
    export class RadialStandardBasisColorMap extends ManifestationColorMappers.ManifestationSubclassColorMapper {
        /**
         * 
         * @return {string}
         */
        public getName(): string {
            return "RadialStandardBasisColorMap";
        }

        /**
         * 
         * @param {*} ball
         * @param {number} alpha
         * @return {Color}
         */
        applyToBall(ball: Connector, alpha: number): Color {
            return this.applyTo$com_vzome_core_algebra_AlgebraicVector$int(ball.getLocation(), alpha);
        }

        /**
         * 
         * @param {*} strut
         * @param {number} alpha
         * @return {Color}
         */
        applyToStrut(strut: Strut, alpha: number): Color {
            return this.applyTo$com_vzome_core_algebra_AlgebraicVector$int(strut.getOffset(), alpha);
        }

        /**
         * 
         * @param {*} panel
         * @param {number} alpha
         * @return {Color}
         */
        applyToPanel(panel: Panel, alpha: number): Color {
            return this.applyTo$com_vzome_core_algebra_AlgebraicVector$int(panel['getNormal$'](), alpha);
        }

        public applyTo$com_vzome_core_algebra_AlgebraicVector$int(vector: AlgebraicVector, alpha: number): Color {
            return ManifestationColorMappers.mapRadially(vector, alpha);
        }

        public applyTo(vector?: any, alpha?: any): Color {
            if (((vector != null && vector instanceof <any>AlgebraicVector) || vector === null) && ((typeof alpha === 'number') || alpha === null)) {
                return <any>this.applyTo$com_vzome_core_algebra_AlgebraicVector$int(vector, alpha);
            } else if (((vector != null && (vector.constructor != null && vector.constructor["__interfaces"] != null && vector.constructor["__interfaces"].indexOf("com.vzome.core.model.Manifestation") >= 0)) || vector === null) && alpha === undefined) {
                return super.applyTo(vector);
            } else throw new Error('invalid overload');
        }

        constructor() {
            super();
        }
    }
    RadialStandardBasisColorMap["__class"] = "com.vzome.core.edits.ManifestationColorMappers.RadialStandardBasisColorMap";
    RadialStandardBasisColorMap["__interfaces"] = ["com.vzome.core.edits.ColorMappers.ColorMapper","java.util.function.Function"];



    /**
     * Gets standard color mapping from the OrbitSource
     * @extends ManifestationColorMappers.ManifestationSubclassColorMapper
     * @class
     */
    export class SystemColorMap extends ManifestationColorMappers.ManifestationSubclassColorMapper {
        /**
         * 
         * @return {string}
         */
        public getName(): string {
            return "SystemColorMap";
        }

        symmetrySystem: OrbitSource;

        constructor(symmetry: OrbitSource) {
            super();
            if (this.symmetrySystem === undefined) { this.symmetrySystem = null; }
            this.symmetrySystem = symmetry;
        }

        /**
         * 
         * @param {*} ball
         * @param {number} alpha
         * @return {Color}
         */
        applyToBall(ball: Connector, alpha: number): Color {
            return this.symmetrySystem.getVectorColor(null);
        }

        /**
         * 
         * @param {*} strut
         * @param {number} alpha
         * @return {Color}
         */
        applyToStrut(strut: Strut, alpha: number): Color {
            return this.applyToVector(strut.getOffset());
        }

        /**
         * 
         * @param {*} panel
         * @param {number} alpha
         * @return {Color}
         */
        applyToPanel(panel: Panel, alpha: number): Color {
            return this.applyToVector(panel['getNormal$']()).getPastel();
        }

        applyToVector(vector: AlgebraicVector): Color {
            return this.symmetrySystem.getVectorColor(vector);
        }

        /**
         * 
         * @param {*} element
         */
        getXmlAttributes(element: Element) {
            if (this.symmetrySystem != null){
                DomUtils.addAttribute(element, "symmetry", this.symmetrySystem.getName());
            }
        }
    }
    SystemColorMap["__class"] = "com.vzome.core.edits.ManifestationColorMappers.SystemColorMap";
    SystemColorMap["__interfaces"] = ["com.vzome.core.edits.ColorMappers.ColorMapper","java.util.function.Function"];



    /**
     * Polarity info is intentionally removed by this mapping for struts and panels, but not balls
     * so that parallel struts and the panels normal to them will be the same color.
     * @class
     * @extends ManifestationColorMappers.RadialStandardBasisColorMap
     */
    export class CanonicalOrientationColorMap extends ManifestationColorMappers.RadialStandardBasisColorMap {
        /**
         * 
         * @return {string}
         */
        public getName(): string {
            return "CanonicalOrientationColorMap";
        }

        /**
         * 
         * @param {*} ball
         * @param {number} alpha
         * @return {Color}
         */
        applyToBall(ball: Connector, alpha: number): Color {
            return super.applyToBall(ball, alpha);
        }

        public applyTo$com_vzome_core_algebra_AlgebraicVector$int(vector: AlgebraicVector, alpha: number): Color {
            return super.applyTo$com_vzome_core_algebra_AlgebraicVector$int(AlgebraicVectors.getCanonicalOrientation(vector), alpha);
        }

        /**
         * 
         * @param {AlgebraicVector} vector
         * @param {number} alpha
         * @return {Color}
         */
        public applyTo(vector?: any, alpha?: any): Color {
            if (((vector != null && vector instanceof <any>AlgebraicVector) || vector === null) && ((typeof alpha === 'number') || alpha === null)) {
                return <any>this.applyTo$com_vzome_core_algebra_AlgebraicVector$int(vector, alpha);
            } else if (((vector != null && (vector.constructor != null && vector.constructor["__interfaces"] != null && vector.constructor["__interfaces"].indexOf("com.vzome.core.model.Manifestation") >= 0)) || vector === null) && alpha === undefined) {
                return super.applyTo(vector);
            } else throw new Error('invalid overload');
        }

        constructor() {
            super();
        }
    }
    CanonicalOrientationColorMap["__class"] = "com.vzome.core.edits.ManifestationColorMappers.CanonicalOrientationColorMap";
    CanonicalOrientationColorMap["__interfaces"] = ["com.vzome.core.edits.ColorMappers.ColorMapper","java.util.function.Function"];



    /**
     * Polarity info is the ONLY basis for this mapping
     * @class
     * @extends ManifestationColorMappers.RadialStandardBasisColorMap
     */
    export class NormalPolarityColorMap extends ManifestationColorMappers.RadialStandardBasisColorMap {
        /**
         * 
         * @return {string}
         */
        public getName(): string {
            return "NormalPolarityColorMap";
        }

        public applyTo$com_vzome_core_algebra_AlgebraicVector$int(vector: AlgebraicVector, alpha: number): Color {
            return ManifestationColorMappers.mapPolarity(vector, alpha);
        }

        /**
         * 
         * @param {AlgebraicVector} vector
         * @param {number} alpha
         * @return {Color}
         */
        public applyTo(vector?: any, alpha?: any): Color {
            if (((vector != null && vector instanceof <any>AlgebraicVector) || vector === null) && ((typeof alpha === 'number') || alpha === null)) {
                return <any>this.applyTo$com_vzome_core_algebra_AlgebraicVector$int(vector, alpha);
            } else if (((vector != null && (vector.constructor != null && vector.constructor["__interfaces"] != null && vector.constructor["__interfaces"].indexOf("com.vzome.core.model.Manifestation") >= 0)) || vector === null) && alpha === undefined) {
                return super.applyTo(vector);
            } else throw new Error('invalid overload');
        }

        constructor() {
            super();
        }
    }
    NormalPolarityColorMap["__class"] = "com.vzome.core.edits.ManifestationColorMappers.NormalPolarityColorMap";
    NormalPolarityColorMap["__interfaces"] = ["com.vzome.core.edits.ColorMappers.ColorMapper","java.util.function.Function"];



    /**
     * Gets standard color of the nearest special orbit using the standard color basis
     * @extends ManifestationColorMappers.SystemColorMap
     * @class
     */
    export class NearestSpecialOrbitColorMap extends ManifestationColorMappers.SystemColorMap {
        /**
         * 
         * @return {string}
         */
        public getName(): string {
            return "NearestSpecialOrbitColorMap";
        }

        specialOrbits: java.util.Set<Direction>;

        constructor(symm: OrbitSource) {
            super(symm);
            this.specialOrbits = <any>(new java.util.LinkedHashSet<any>());
            this.specialOrbits.add(symm.getSymmetry().getSpecialOrbit(SpecialOrbit.BLUE));
            this.specialOrbits.add(symm.getSymmetry().getSpecialOrbit(SpecialOrbit.YELLOW));
            this.specialOrbits.add(symm.getSymmetry().getSpecialOrbit(SpecialOrbit.RED));
        }

        /**
         * 
         * @param {*} ball
         * @param {number} alpha
         * @return {Color}
         */
        applyToBall(ball: Connector, alpha: number): Color {
            return this.applyToVector(ball.getLocation());
        }

        /**
         * 
         * @param {*} strut
         * @param {number} alpha
         * @return {Color}
         */
        applyToStrut(strut: Strut, alpha: number): Color {
            return this.applyToVector(strut.getOffset());
        }

        /**
         * 
         * @param {*} panel
         * @param {number} alpha
         * @return {Color}
         */
        applyToPanel(panel: Panel, alpha: number): Color {
            return this.applyToVector(panel['getNormal$']()).getPastel();
        }

        /**
         * 
         * @param {AlgebraicVector} vector
         * @return {Color}
         */
        applyToVector(vector: AlgebraicVector): Color {
            if (vector.isOrigin()){
                return this.symmetrySystem.getVectorColor(null);
            }
            const nearestSpecialOrbit: Axis = this.symmetrySystem.getSymmetry()['getAxis$com_vzome_core_math_RealVector$java_util_Collection'](vector.toRealVector(), this.specialOrbits);
            const normal: AlgebraicVector = nearestSpecialOrbit.normal();
            return this.symmetrySystem.getVectorColor(normal);
        }
    }
    NearestSpecialOrbitColorMap["__class"] = "com.vzome.core.edits.ManifestationColorMappers.NearestSpecialOrbitColorMap";
    NearestSpecialOrbitColorMap["__interfaces"] = ["com.vzome.core.edits.ColorMappers.ColorMapper","java.util.function.Function"];



    /**
     * Gets standard color of the nearest special orbit based on the Centroid
     * @extends ManifestationColorMappers.NearestSpecialOrbitColorMap
     * @class
     */
    export class CentroidNearestSpecialOrbitColorMap extends ManifestationColorMappers.NearestSpecialOrbitColorMap {
        /**
         * 
         * @return {string}
         */
        public getName(): string {
            return "CentroidNearestSpecialOrbitColorMap";
        }

        constructor(symm: OrbitSource) {
            super(symm);
        }

        /**
         * 
         * @param {*} ball
         * @param {number} alpha
         * @return {Color}
         */
        applyToBall(ball: Connector, alpha: number): Color {
            return this.applyToVector(ball.getCentroid());
        }

        /**
         * 
         * @param {*} strut
         * @param {number} alpha
         * @return {Color}
         */
        applyToStrut(strut: Strut, alpha: number): Color {
            return this.applyToVector(strut.getCentroid());
        }

        /**
         * 
         * @param {*} panel
         * @param {number} alpha
         * @return {Color}
         */
        applyToPanel(panel: Panel, alpha: number): Color {
            return this.applyToVector(panel.getCentroid()).getPastel();
        }
    }
    CentroidNearestSpecialOrbitColorMap["__class"] = "com.vzome.core.edits.ManifestationColorMappers.CentroidNearestSpecialOrbitColorMap";
    CentroidNearestSpecialOrbitColorMap["__interfaces"] = ["com.vzome.core.edits.ColorMappers.ColorMapper","java.util.function.Function"];



    /**
     * Gets standard color of the nearest predefined orbit using the symmetry's standard color scheme
     * @extends ManifestationColorMappers.NearestSpecialOrbitColorMap
     * @class
     */
    export class NearestPredefinedOrbitColorMap extends ManifestationColorMappers.NearestSpecialOrbitColorMap {
        /**
         * 
         * @return {string}
         */
        public getName(): string {
            return "NearestPredefinedOrbitColorMap";
        }

        constructor(symm: OrbitSource) {
            super(symm);
            this.specialOrbits = null;
        }
    }
    NearestPredefinedOrbitColorMap["__class"] = "com.vzome.core.edits.ManifestationColorMappers.NearestPredefinedOrbitColorMap";
    NearestPredefinedOrbitColorMap["__interfaces"] = ["com.vzome.core.edits.ColorMappers.ColorMapper","java.util.function.Function"];



    /**
     * Gets standard color of the nearest predefined orbit based on the centroid of each manifestation
     * @extends ManifestationColorMappers.CentroidNearestSpecialOrbitColorMap
     * @class
     */
    export class CentroidNearestPredefinedOrbitColorMap extends ManifestationColorMappers.CentroidNearestSpecialOrbitColorMap {
        /**
         * 
         * @return {string}
         */
        public getName(): string {
            return "CentroidNearestPredefinedOrbitColorMap";
        }

        constructor(symm: OrbitSource) {
            super(symm);
            this.specialOrbits = null;
        }
    }
    CentroidNearestPredefinedOrbitColorMap["__class"] = "com.vzome.core.edits.ManifestationColorMappers.CentroidNearestPredefinedOrbitColorMap";
    CentroidNearestPredefinedOrbitColorMap["__interfaces"] = ["com.vzome.core.edits.ColorMappers.ColorMapper","java.util.function.Function"];


}

//  Run the Java static initializer eagerly at module load, as the monolithic
//  bundle did.  The lazy _$LI$ accessors are not enough for these classes:
//  e.g. XmlSymmetryFormat registers every format in FORMATS here, and
//  getFormat() reads that map without touching any accessor.
ManifestationColorMappers.__static_initialize();

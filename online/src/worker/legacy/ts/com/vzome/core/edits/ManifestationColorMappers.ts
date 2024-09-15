/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
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

        public static getColorMapper$java_lang_String$com_vzome_core_editor_api_OrbitSource(mapperName: string, symmetry: com.vzome.core.editor.api.OrbitSource): ManifestationColorMappers.ManifestationColorMapper {
            let colorMapper: ManifestationColorMappers.ManifestationColorMapper = mapperName === ("SystemColorMap") ? new ManifestationColorMappers.SystemColorMap(symmetry) : mapperName === ("SystemCentroidColorMap") ? new ManifestationColorMappers.SystemCentroidColorMap(symmetry) : mapperName === ("NearestSpecialOrbitColorMap") ? new ManifestationColorMappers.NearestSpecialOrbitColorMap(symmetry) : mapperName === ("CentroidNearestSpecialOrbitColorMap") ? new ManifestationColorMappers.CentroidNearestSpecialOrbitColorMap(symmetry) : mapperName === ("NearestPredefinedOrbitColorMap") ? new ManifestationColorMappers.NearestPredefinedOrbitColorMap(symmetry) : mapperName === ("CentroidNearestPredefinedOrbitColorMap") ? new ManifestationColorMappers.CentroidNearestPredefinedOrbitColorMap(symmetry) : ManifestationColorMappers.getColorMapper$java_lang_String(mapperName);
            if (colorMapper == null){
                colorMapper = new ManifestationColorMappers.Identity();
            }
            return colorMapper;
        }

        public static getColorMapper(mapperName?: any, symmetry?: any): ManifestationColorMappers.ManifestationColorMapper {
            if (((typeof mapperName === 'string') || mapperName === null) && ((symmetry != null && (symmetry.constructor != null && symmetry.constructor["__interfaces"] != null && symmetry.constructor["__interfaces"].indexOf("com.vzome.core.editor.api.OrbitSource") >= 0)) || symmetry === null)) {
                return <any>com.vzome.core.edits.ManifestationColorMappers.getColorMapper$java_lang_String$com_vzome_core_editor_api_OrbitSource(mapperName, symmetry);
            } else if (((typeof mapperName === 'string') || mapperName === null) && symmetry === undefined) {
                return <any>com.vzome.core.edits.ManifestationColorMappers.getColorMapper$java_lang_String(mapperName);
            } else throw new Error('invalid overload');
        }

        static mapPolarity(vector: com.vzome.core.algebra.AlgebraicVector, alpha: number): com.vzome.core.construction.Color {
            const polarity: number = vector.compareTo(vector.negate());
            const mid: number = 128;
            const diff: number = 64;
            const shade: number = polarity < 0 ? mid - diff : polarity > 0 ? mid + diff : mid;
            return new com.vzome.core.construction.Color(shade, shade, shade, alpha);
        }

        /**
         * @param {com.vzome.core.algebra.AlgebraicVector} vector could be midpoint, start, end, normal, or any basis for mapping to a color
         * @param {number} alpha the transparency component of the resulting color.
         * @return
         * @return {com.vzome.core.construction.Color}
         */
        static mapRadially(vector: com.vzome.core.algebra.AlgebraicVector, alpha: number): com.vzome.core.construction.Color {
            const midPoint: number = 127;
            const rgb: number[] = [midPoint, midPoint, midPoint];
            const parts: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(rgb.length);
            const dimensions: number = Math.min(rgb.length, vector.dimension());
            let whole: number = 0.0;
            for(let i: number = 0; i < dimensions; i++) {{
                const component: com.vzome.core.algebra.AlgebraicNumber = vector.getComponent(i);
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
            return new com.vzome.core.construction.Color(rgb[0], rgb[1], rgb[2], alpha);
        }

        /**
         * @param {com.vzome.core.algebra.AlgebraicVector} vector could be midpoint, start, end, normal, or any basis for mapping to a color
         * @param {number} alpha the transparency component of the resulting color.
         * @param {number} neg the R, G or B level of vectors with a negative value in the corresponding X, Y, or Z dimension.
         * @param {number} zero the R, G or B level of vectors with a zero value in the corresponding X, Y, or Z dimension.
         * @param {number} pos the R, G or B level of vectors with a positive value in the corresponding X, Y, or Z dimension.
         * @return
         * @return {com.vzome.core.construction.Color}
         */
        static mapToOctant(vector: com.vzome.core.algebra.AlgebraicVector, alpha: number, neg: number, zero: number, pos: number): com.vzome.core.construction.Color {
            const src: number[] = [neg, zero, pos];
            const rgb: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(src.length);
            const dimensions: number = Math.min(rgb.length, vector.dimension());
            for(let i: number = 0; i < dimensions; i++) {{
                const component: com.vzome.core.algebra.AlgebraicNumber = vector.getComponent(i);
                const dir: number = /* signum */(f => { if (f > 0) { return 1; } else if (f < 0) { return -1; } else { return 0; } })(component.evaluate());
                const index: number = /* intValue */(dir|0) + 1;
                rgb[i] = src[index];
            };}
            return new com.vzome.core.construction.Color(rgb[0], rgb[1], rgb[2], alpha);
        }

        static mapToMagnitude(vector: com.vzome.core.algebra.AlgebraicVector, offset: number, fullScaleSquared: number, initialColor: com.vzome.core.construction.Color): com.vzome.core.construction.Color {
            if (vector == null || initialColor == null){
                return initialColor;
            }
            const magnitudeSquared: number = com.vzome.core.algebra.AlgebraicVectors.getMagnitudeSquared(vector).evaluate();
            const denominator: number = (fullScaleSquared === 0.0) ? 1.0E-4 : fullScaleSquared;
            const scale: number = Math.abs(offset - magnitudeSquared) / denominator;
            return com.vzome.core.construction.Color.getScaledTo(initialColor, scale);
        }
    }
    ManifestationColorMappers["__class"] = "com.vzome.core.edits.ManifestationColorMappers";


    export namespace ManifestationColorMappers {

        /**
         * Common abstract base class adds xml persistence
         * and late loading of criteria based on selection and/or model
         * @class
         */
        export abstract class ManifestationColorMapper implements com.vzome.core.edits.ColorMappers.ColorMapper<com.vzome.core.model.Manifestation> {
            /* Default method injected from com.vzome.core.edits.ColorMappers.ColorMapper */
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
             * @param {com.vzome.core.editor.api.Manifestations.ManifestationIterator} manifestations
             */
            public initialize(manifestations: com.vzome.core.editor.api.Manifestations.ManifestationIterator) {
            }

            public apply$com_vzome_core_model_Manifestation(man: com.vzome.core.model.Manifestation): com.vzome.core.construction.Color {
                return (man == null || !man.isRendered()) ? null : this.applyTo(man);
            }

            /**
             * 
             * @param {*} man
             * @return {com.vzome.core.construction.Color}
             */
            public apply(man?: any): com.vzome.core.construction.Color {
                if (((man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Manifestation") >= 0)) || man === null)) {
                    return <any>this.apply$com_vzome_core_model_Manifestation(man);
                } else throw new Error('invalid overload');
            }

            applyTo(manifestation: com.vzome.core.model.Manifestation): com.vzome.core.construction.Color {
                let color: com.vzome.core.construction.Color = manifestation.getColor();
                if (color == null){
                    color = com.vzome.core.construction.Color.WHITE_$LI$();
                }
                return color;
            }

            /**
             * subclasses should call {@code result.setAttribute()} if they have any parameters to persist
             * @param {*} result
             */
            getXmlAttributes(result: org.w3c.dom.Element) {
            }

            /**
             * subclasses should call {@code xml.getAttribute()} to retrieve any persisted parameters
             * @param {*} xml
             */
            setXmlAttributes(xml: org.w3c.dom.Element) {
            }

            public abstract getName(): any;        }
        ManifestationColorMapper["__class"] = "com.vzome.core.edits.ManifestationColorMappers.ManifestationColorMapper";
        ManifestationColorMapper["__interfaces"] = ["com.vzome.core.edits.ColorMappers.ColorMapper","java.util.function.Function"];



        /**
         * returns current color
         * @class
         * @extends com.vzome.core.edits.ManifestationColorMappers.ManifestationColorMapper
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
             * @return {com.vzome.core.construction.Color}
             */
            applyTo(rendered: com.vzome.core.model.Manifestation): com.vzome.core.construction.Color {
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
         * @extends com.vzome.core.edits.ManifestationColorMappers.ManifestationColorMapper
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
             * @return {com.vzome.core.construction.Color}
             */
            applyTo(rendered: com.vzome.core.model.Manifestation): com.vzome.core.construction.Color {
                return com.vzome.core.construction.Color.getComplement(super.applyTo(rendered));
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
         * @extends com.vzome.core.edits.ManifestationColorMappers.ManifestationColorMapper
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
             * @return {com.vzome.core.construction.Color}
             */
            applyTo(rendered: com.vzome.core.model.Manifestation): com.vzome.core.construction.Color {
                return com.vzome.core.construction.Color.getInverted(super.applyTo(rendered));
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
         * @extends com.vzome.core.edits.ManifestationColorMappers.ManifestationColorMapper
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
             * @return {com.vzome.core.construction.Color}
             */
            applyTo(rendered: com.vzome.core.model.Manifestation): com.vzome.core.construction.Color {
                return com.vzome.core.construction.Color.getMaximum(super.applyTo(rendered));
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
         * @extends com.vzome.core.edits.ManifestationColorMappers.ManifestationColorMapper
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
             * @return {com.vzome.core.construction.Color}
             */
            applyTo(rendered: com.vzome.core.model.Manifestation): com.vzome.core.construction.Color {
                return com.vzome.core.construction.Color.getPastel(super.applyTo(rendered));
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
            setXmlAttributes(xml: org.w3c.dom.Element) {
                this.alpha = javaemul.internal.IntegerHelper.parseInt(xml.getAttribute(TransparencyMapper.ALPHA_ATTR_NAME));
            }

            /**
             * 
             * @param {*} result
             */
            getXmlAttributes(result: org.w3c.dom.Element) {
                result.setAttribute(TransparencyMapper.ALPHA_ATTR_NAME, /* toString */(''+(this.alpha)));
            }

            /**
             * 
             * @param {*} rendered
             * @return {com.vzome.core.construction.Color}
             */
            applyTo(rendered: com.vzome.core.model.Manifestation): com.vzome.core.construction.Color {
                const color: com.vzome.core.construction.Color = super.applyTo(rendered);
                return new com.vzome.core.construction.Color(color.getRed(), color.getGreen(), color.getBlue(), this.alpha);
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

            color: com.vzome.core.construction.Color;

            /**
             * 
             * @return {boolean}
             */
            public requiresOrderedSelection(): boolean {
                return true;
            }

            /**
             * 
             * @param {com.vzome.core.editor.api.Manifestations.ManifestationIterator} selection
             */
            public initialize(selection: com.vzome.core.editor.api.Manifestations.ManifestationIterator) {
                if (this.color == null){
                    let last: com.vzome.core.model.Manifestation = null;
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
                    throw new com.vzome.core.commands.Command.Failure("select a ball, strut or panel as the color to be copied.");
                }
            }

            /**
             * 
             * @param {*} xml
             */
            setXmlAttributes(xml: org.w3c.dom.Element) {
                const red: string = xml.getAttribute("red");
                const green: string = xml.getAttribute("green");
                const blue: string = xml.getAttribute("blue");
                const alphaStr: string = xml.getAttribute("alpha");
                const alpha: number = (alphaStr == null || /* isEmpty */(alphaStr.length === 0)) ? 255 : javaemul.internal.IntegerHelper.parseInt(alphaStr);
                this.color = new com.vzome.core.construction.Color(javaemul.internal.IntegerHelper.parseInt(red), javaemul.internal.IntegerHelper.parseInt(green), javaemul.internal.IntegerHelper.parseInt(blue), alpha);
            }

            /**
             * 
             * @param {*} result
             */
            getXmlAttributes(result: org.w3c.dom.Element) {
                result.setAttribute("red", "" + this.color.getRed());
                result.setAttribute("green", "" + this.color.getGreen());
                result.setAttribute("blue", "" + this.color.getBlue());
                const alpha: number = this.color.getAlpha();
                if (alpha < 255)result.setAttribute("alpha", "" + alpha);
            }

            /**
             * 
             * @param {*} rendered
             * @return {com.vzome.core.construction.Color}
             */
            applyTo(rendered: com.vzome.core.model.Manifestation): com.vzome.core.construction.Color {
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
         * @extends com.vzome.core.edits.ManifestationColorMappers.ManifestationColorMapper
         * @class
         */
        export abstract class CentroidColorMapper extends ManifestationColorMappers.ManifestationColorMapper {
            constructor() {
                super();
            }

            public applyTo$com_vzome_core_model_Manifestation(rendered: com.vzome.core.model.Manifestation): com.vzome.core.construction.Color {
                const color: com.vzome.core.construction.Color = rendered.getColor();
                const alpha: number = color == null ? 255 : color.getAlpha();
                return this.applyTo$com_vzome_core_algebra_AlgebraicVector$int(rendered.getCentroid(), alpha);
            }

            public applyTo$com_vzome_core_algebra_AlgebraicVector$int(centroid: com.vzome.core.algebra.AlgebraicVector, alpha: number): com.vzome.core.construction.Color { throw new Error('cannot invoke abstract overloaded method... check your argument(s) type(s)'); }

            public applyTo(centroid?: any, alpha?: any): com.vzome.core.construction.Color {
                if (((centroid != null && centroid instanceof <any>com.vzome.core.algebra.AlgebraicVector) || centroid === null) && ((typeof alpha === 'number') || alpha === null)) {
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
         * @extends com.vzome.core.edits.ManifestationColorMappers.ManifestationColorMapper
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
             * @param {com.vzome.core.editor.api.Manifestations.ManifestationIterator} manifestations
             */
            public initialize(manifestations: com.vzome.core.editor.api.Manifestations.ManifestationIterator) {
                if (this.fullScaleSquared === 0){
                    const fullScale: com.vzome.core.algebra.AlgebraicVector = DarkenNearOrigin.getMostDistantPoint(manifestations);
                    if (fullScale == null){
                        throw new com.vzome.core.commands.Command.Failure("unable to determine most distant point");
                    }
                    if (fullScale.isOrigin()){
                        throw new com.vzome.core.commands.Command.Failure("select at least one point other than the origin");
                    }
                    this.fullScaleSquared = com.vzome.core.algebra.AlgebraicVectors.getMagnitudeSquared(fullScale).evaluate();
                }
            }

            static getMostDistantPoint(manifestations: com.vzome.core.editor.api.Manifestations.ManifestationIterator): com.vzome.core.algebra.AlgebraicVector {
                const centroids: java.util.List<com.vzome.core.algebra.AlgebraicVector> = <any>(new java.util.ArrayList<any>());
                for(let index=manifestations.iterator();index.hasNext();) {
                    let man = index.next();
                    {
                        centroids.add(man.getCentroid());
                    }
                }
                if (centroids.isEmpty()){
                    return null;
                }
                const mostDistant: java.util.TreeSet<com.vzome.core.algebra.AlgebraicVector> = com.vzome.core.algebra.AlgebraicVectors.getMostDistantFromOrigin(centroids);
                return mostDistant.isEmpty() ? null : mostDistant.first();
            }

            /**
             * 
             * @param {*} rendered
             * @return {com.vzome.core.construction.Color}
             */
            applyTo(rendered: com.vzome.core.model.Manifestation): com.vzome.core.construction.Color {
                const centroid: com.vzome.core.algebra.AlgebraicVector = rendered.getCentroid();
                const initialColor: com.vzome.core.construction.Color = super.applyTo(rendered);
                return ManifestationColorMappers.mapToMagnitude(centroid, this.offset, this.fullScaleSquared, initialColor);
            }

            FULLSCALESQUARED_ATTR_NAME: string;

            /**
             * 
             * @param {*} result
             */
            getXmlAttributes(result: org.w3c.dom.Element) {
                result.setAttribute(this.FULLSCALESQUARED_ATTR_NAME, /* toString */(''+(this.fullScaleSquared)));
            }

            /**
             * 
             * @param {*} xml
             */
            setXmlAttributes(xml: org.w3c.dom.Element) {
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
         * @extends com.vzome.core.edits.ManifestationColorMappers.ManifestationColorMapper
         */
        export abstract class ManifestationSubclassColorMapper extends ManifestationColorMappers.ManifestationColorMapper {
            /**
             * 
             * @param {*} man
             * @return {com.vzome.core.construction.Color}
             */
            applyTo(man: com.vzome.core.model.Manifestation): com.vzome.core.construction.Color {
                const color: com.vzome.core.construction.Color = man.getColor();
                const alpha: number = color == null ? 255 : color.getAlpha();
                if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                    return this.applyToBall(<com.vzome.core.model.Connector><any>man, alpha);
                } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                    return this.applyToStrut(<com.vzome.core.model.Strut><any>man, alpha);
                } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                    return this.applyToPanel(<com.vzome.core.model.Panel><any>man, alpha);
                }
                return null;
            }

            abstract applyToBall(ball: com.vzome.core.model.Connector, alpha: number): com.vzome.core.construction.Color;

            abstract applyToStrut(strut: com.vzome.core.model.Strut, alpha: number): com.vzome.core.construction.Color;

            abstract applyToPanel(panel: com.vzome.core.model.Panel, alpha: number): com.vzome.core.construction.Color;

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
         * @extends com.vzome.core.edits.ManifestationColorMappers.CentroidColorMapper
         */
        export class RadialCentroidColorMap extends ManifestationColorMappers.CentroidColorMapper {
            /**
             * 
             * @return {string}
             */
            public getName(): string {
                return "RadialCentroidColorMap";
            }

            public applyTo$com_vzome_core_algebra_AlgebraicVector$int(centroid: com.vzome.core.algebra.AlgebraicVector, alpha: number): com.vzome.core.construction.Color {
                return ManifestationColorMappers.mapRadially(centroid, alpha);
            }

            /**
             * 
             * @param {com.vzome.core.algebra.AlgebraicVector} centroid
             * @param {number} alpha
             * @return {com.vzome.core.construction.Color}
             */
            public applyTo(centroid?: any, alpha?: any): com.vzome.core.construction.Color {
                if (((centroid != null && centroid instanceof <any>com.vzome.core.algebra.AlgebraicVector) || centroid === null) && ((typeof alpha === 'number') || alpha === null)) {
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
         * @extends com.vzome.core.edits.ManifestationColorMappers.CentroidColorMapper
         */
        export class CentroidByOctantAndDirectionColorMap extends ManifestationColorMappers.CentroidColorMapper {
            /**
             * 
             * @return {string}
             */
            public getName(): string {
                return "CentroidByOctantAndDirectionColorMap";
            }

            public applyTo$com_vzome_core_algebra_AlgebraicVector$int(vector: com.vzome.core.algebra.AlgebraicVector, alpha: number): com.vzome.core.construction.Color {
                return com.vzome.core.construction.Color.getMaximum(ManifestationColorMappers.mapToOctant(vector, alpha, 0, 127, 255));
            }

            /**
             * 
             * @param {com.vzome.core.algebra.AlgebraicVector} vector
             * @param {number} alpha
             * @return {com.vzome.core.construction.Color}
             */
            public applyTo(vector?: any, alpha?: any): com.vzome.core.construction.Color {
                if (((vector != null && vector instanceof <any>com.vzome.core.algebra.AlgebraicVector) || vector === null) && ((typeof alpha === 'number') || alpha === null)) {
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
         * @extends com.vzome.core.edits.ManifestationColorMappers.CentroidColorMapper
         */
        export class CoordinatePlaneColorMap extends ManifestationColorMappers.CentroidColorMapper {
            /**
             * 
             * @return {string}
             */
            public getName(): string {
                return "CoordinatePlaneColorMap";
            }

            public applyTo$com_vzome_core_algebra_AlgebraicVector$int(vector: com.vzome.core.algebra.AlgebraicVector, alpha: number): com.vzome.core.construction.Color {
                return com.vzome.core.construction.Color.getInverted(ManifestationColorMappers.mapToOctant(vector, alpha, 0, 255, 0));
            }

            /**
             * 
             * @param {com.vzome.core.algebra.AlgebraicVector} vector
             * @param {number} alpha
             * @return {com.vzome.core.construction.Color}
             */
            public applyTo(vector?: any, alpha?: any): com.vzome.core.construction.Color {
                if (((vector != null && vector instanceof <any>com.vzome.core.algebra.AlgebraicVector) || vector === null) && ((typeof alpha === 'number') || alpha === null)) {
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
         * @extends com.vzome.core.edits.ManifestationColorMappers.CentroidColorMapper
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

            symmetrySystem: com.vzome.core.editor.api.OrbitSource;

            constructor(symmetry: com.vzome.core.editor.api.OrbitSource) {
                super();
                if (this.symmetrySystem === undefined) { this.symmetrySystem = null; }
                this.symmetrySystem = symmetry;
            }

            public applyTo$com_vzome_core_algebra_AlgebraicVector$int(centroid: com.vzome.core.algebra.AlgebraicVector, alpha: number): com.vzome.core.construction.Color {
                return this.symmetrySystem.getVectorColor(centroid);
            }

            /**
             * 
             * @param {com.vzome.core.algebra.AlgebraicVector} centroid
             * @param {number} alpha
             * @return {com.vzome.core.construction.Color}
             */
            public applyTo(centroid?: any, alpha?: any): com.vzome.core.construction.Color {
                if (((centroid != null && centroid instanceof <any>com.vzome.core.algebra.AlgebraicVector) || centroid === null) && ((typeof alpha === 'number') || alpha === null)) {
                    return <any>this.applyTo$com_vzome_core_algebra_AlgebraicVector$int(centroid, alpha);
                } else if (((centroid != null && (centroid.constructor != null && centroid.constructor["__interfaces"] != null && centroid.constructor["__interfaces"].indexOf("com.vzome.core.model.Manifestation") >= 0)) || centroid === null) && alpha === undefined) {
                    return <any>this.applyTo$com_vzome_core_model_Manifestation(centroid);
                } else throw new Error('invalid overload');
            }

            /**
             * 
             * @param {*} element
             */
            getXmlAttributes(element: org.w3c.dom.Element) {
                if (this.symmetrySystem != null){
                    com.vzome.xml.DomUtils.addAttribute(element, "symmetry", this.symmetrySystem.getName());
                }
            }
        }
        SystemCentroidColorMap["__class"] = "com.vzome.core.edits.ManifestationColorMappers.SystemCentroidColorMap";
        SystemCentroidColorMap["__interfaces"] = ["com.vzome.core.edits.ColorMappers.ColorMapper","java.util.function.Function"];



        /**
         * Same as {@code DarkenNearOrigin} except that
         * the color mapping is reversed from lightest to darkest
         * @class
         * @extends com.vzome.core.edits.ManifestationColorMappers.DarkenNearOrigin
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
             * @param {com.vzome.core.editor.api.Manifestations.ManifestationIterator} manifestations
             */
            public initialize(manifestations: com.vzome.core.editor.api.Manifestations.ManifestationIterator) {
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
         * @extends com.vzome.core.edits.ManifestationColorMappers.ManifestationSubclassColorMapper
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
             * @return {com.vzome.core.construction.Color}
             */
            applyToBall(ball: com.vzome.core.model.Connector, alpha: number): com.vzome.core.construction.Color {
                return this.applyTo$com_vzome_core_algebra_AlgebraicVector$int(ball.getLocation(), alpha);
            }

            /**
             * 
             * @param {*} strut
             * @param {number} alpha
             * @return {com.vzome.core.construction.Color}
             */
            applyToStrut(strut: com.vzome.core.model.Strut, alpha: number): com.vzome.core.construction.Color {
                return this.applyTo$com_vzome_core_algebra_AlgebraicVector$int(strut.getOffset(), alpha);
            }

            /**
             * 
             * @param {*} panel
             * @param {number} alpha
             * @return {com.vzome.core.construction.Color}
             */
            applyToPanel(panel: com.vzome.core.model.Panel, alpha: number): com.vzome.core.construction.Color {
                return this.applyTo$com_vzome_core_algebra_AlgebraicVector$int(panel['getNormal$'](), alpha);
            }

            public applyTo$com_vzome_core_algebra_AlgebraicVector$int(vector: com.vzome.core.algebra.AlgebraicVector, alpha: number): com.vzome.core.construction.Color {
                return ManifestationColorMappers.mapRadially(vector, alpha);
            }

            public applyTo(vector?: any, alpha?: any): com.vzome.core.construction.Color {
                if (((vector != null && vector instanceof <any>com.vzome.core.algebra.AlgebraicVector) || vector === null) && ((typeof alpha === 'number') || alpha === null)) {
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
         * @extends com.vzome.core.edits.ManifestationColorMappers.ManifestationSubclassColorMapper
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

            symmetrySystem: com.vzome.core.editor.api.OrbitSource;

            constructor(symmetry: com.vzome.core.editor.api.OrbitSource) {
                super();
                if (this.symmetrySystem === undefined) { this.symmetrySystem = null; }
                this.symmetrySystem = symmetry;
            }

            /**
             * 
             * @param {*} ball
             * @param {number} alpha
             * @return {com.vzome.core.construction.Color}
             */
            applyToBall(ball: com.vzome.core.model.Connector, alpha: number): com.vzome.core.construction.Color {
                return this.symmetrySystem.getVectorColor(null);
            }

            /**
             * 
             * @param {*} strut
             * @param {number} alpha
             * @return {com.vzome.core.construction.Color}
             */
            applyToStrut(strut: com.vzome.core.model.Strut, alpha: number): com.vzome.core.construction.Color {
                return this.applyToVector(strut.getOffset());
            }

            /**
             * 
             * @param {*} panel
             * @param {number} alpha
             * @return {com.vzome.core.construction.Color}
             */
            applyToPanel(panel: com.vzome.core.model.Panel, alpha: number): com.vzome.core.construction.Color {
                return this.applyToVector(panel['getNormal$']()).getPastel();
            }

            applyToVector(vector: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.construction.Color {
                return this.symmetrySystem.getVectorColor(vector);
            }

            /**
             * 
             * @param {*} element
             */
            getXmlAttributes(element: org.w3c.dom.Element) {
                if (this.symmetrySystem != null){
                    com.vzome.xml.DomUtils.addAttribute(element, "symmetry", this.symmetrySystem.getName());
                }
            }
        }
        SystemColorMap["__class"] = "com.vzome.core.edits.ManifestationColorMappers.SystemColorMap";
        SystemColorMap["__interfaces"] = ["com.vzome.core.edits.ColorMappers.ColorMapper","java.util.function.Function"];



        /**
         * Polarity info is intentionally removed by this mapping for struts and panels, but not balls
         * so that parallel struts and the panels normal to them will be the same color.
         * @class
         * @extends com.vzome.core.edits.ManifestationColorMappers.RadialStandardBasisColorMap
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
             * @return {com.vzome.core.construction.Color}
             */
            applyToBall(ball: com.vzome.core.model.Connector, alpha: number): com.vzome.core.construction.Color {
                return super.applyToBall(ball, alpha);
            }

            public applyTo$com_vzome_core_algebra_AlgebraicVector$int(vector: com.vzome.core.algebra.AlgebraicVector, alpha: number): com.vzome.core.construction.Color {
                return super.applyTo$com_vzome_core_algebra_AlgebraicVector$int(com.vzome.core.algebra.AlgebraicVectors.getCanonicalOrientation(vector), alpha);
            }

            /**
             * 
             * @param {com.vzome.core.algebra.AlgebraicVector} vector
             * @param {number} alpha
             * @return {com.vzome.core.construction.Color}
             */
            public applyTo(vector?: any, alpha?: any): com.vzome.core.construction.Color {
                if (((vector != null && vector instanceof <any>com.vzome.core.algebra.AlgebraicVector) || vector === null) && ((typeof alpha === 'number') || alpha === null)) {
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
         * @extends com.vzome.core.edits.ManifestationColorMappers.RadialStandardBasisColorMap
         */
        export class NormalPolarityColorMap extends ManifestationColorMappers.RadialStandardBasisColorMap {
            /**
             * 
             * @return {string}
             */
            public getName(): string {
                return "NormalPolarityColorMap";
            }

            public applyTo$com_vzome_core_algebra_AlgebraicVector$int(vector: com.vzome.core.algebra.AlgebraicVector, alpha: number): com.vzome.core.construction.Color {
                return ManifestationColorMappers.mapPolarity(vector, alpha);
            }

            /**
             * 
             * @param {com.vzome.core.algebra.AlgebraicVector} vector
             * @param {number} alpha
             * @return {com.vzome.core.construction.Color}
             */
            public applyTo(vector?: any, alpha?: any): com.vzome.core.construction.Color {
                if (((vector != null && vector instanceof <any>com.vzome.core.algebra.AlgebraicVector) || vector === null) && ((typeof alpha === 'number') || alpha === null)) {
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
         * @extends com.vzome.core.edits.ManifestationColorMappers.SystemColorMap
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

            specialOrbits: java.util.Set<com.vzome.core.math.symmetry.Direction>;

            constructor(symm: com.vzome.core.editor.api.OrbitSource) {
                super(symm);
                this.specialOrbits = <any>(new java.util.LinkedHashSet<any>());
                this.specialOrbits.add(symm.getSymmetry().getSpecialOrbit(com.vzome.core.math.symmetry.SpecialOrbit.BLUE));
                this.specialOrbits.add(symm.getSymmetry().getSpecialOrbit(com.vzome.core.math.symmetry.SpecialOrbit.YELLOW));
                this.specialOrbits.add(symm.getSymmetry().getSpecialOrbit(com.vzome.core.math.symmetry.SpecialOrbit.RED));
            }

            /**
             * 
             * @param {*} ball
             * @param {number} alpha
             * @return {com.vzome.core.construction.Color}
             */
            applyToBall(ball: com.vzome.core.model.Connector, alpha: number): com.vzome.core.construction.Color {
                return this.applyToVector(ball.getLocation());
            }

            /**
             * 
             * @param {*} strut
             * @param {number} alpha
             * @return {com.vzome.core.construction.Color}
             */
            applyToStrut(strut: com.vzome.core.model.Strut, alpha: number): com.vzome.core.construction.Color {
                return this.applyToVector(strut.getOffset());
            }

            /**
             * 
             * @param {*} panel
             * @param {number} alpha
             * @return {com.vzome.core.construction.Color}
             */
            applyToPanel(panel: com.vzome.core.model.Panel, alpha: number): com.vzome.core.construction.Color {
                return this.applyToVector(panel['getNormal$']()).getPastel();
            }

            /**
             * 
             * @param {com.vzome.core.algebra.AlgebraicVector} vector
             * @return {com.vzome.core.construction.Color}
             */
            applyToVector(vector: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.construction.Color {
                if (vector.isOrigin()){
                    return this.symmetrySystem.getVectorColor(null);
                }
                const nearestSpecialOrbit: com.vzome.core.math.symmetry.Axis = this.symmetrySystem.getSymmetry()['getAxis$com_vzome_core_math_RealVector$java_util_Collection'](vector.toRealVector(), this.specialOrbits);
                const normal: com.vzome.core.algebra.AlgebraicVector = nearestSpecialOrbit.normal();
                return this.symmetrySystem.getVectorColor(normal);
            }
        }
        NearestSpecialOrbitColorMap["__class"] = "com.vzome.core.edits.ManifestationColorMappers.NearestSpecialOrbitColorMap";
        NearestSpecialOrbitColorMap["__interfaces"] = ["com.vzome.core.edits.ColorMappers.ColorMapper","java.util.function.Function"];



        /**
         * Gets standard color of the nearest special orbit based on the Centroid
         * @extends com.vzome.core.edits.ManifestationColorMappers.NearestSpecialOrbitColorMap
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

            constructor(symm: com.vzome.core.editor.api.OrbitSource) {
                super(symm);
            }

            /**
             * 
             * @param {*} ball
             * @param {number} alpha
             * @return {com.vzome.core.construction.Color}
             */
            applyToBall(ball: com.vzome.core.model.Connector, alpha: number): com.vzome.core.construction.Color {
                return this.applyToVector(ball.getCentroid());
            }

            /**
             * 
             * @param {*} strut
             * @param {number} alpha
             * @return {com.vzome.core.construction.Color}
             */
            applyToStrut(strut: com.vzome.core.model.Strut, alpha: number): com.vzome.core.construction.Color {
                return this.applyToVector(strut.getCentroid());
            }

            /**
             * 
             * @param {*} panel
             * @param {number} alpha
             * @return {com.vzome.core.construction.Color}
             */
            applyToPanel(panel: com.vzome.core.model.Panel, alpha: number): com.vzome.core.construction.Color {
                return this.applyToVector(panel.getCentroid()).getPastel();
            }
        }
        CentroidNearestSpecialOrbitColorMap["__class"] = "com.vzome.core.edits.ManifestationColorMappers.CentroidNearestSpecialOrbitColorMap";
        CentroidNearestSpecialOrbitColorMap["__interfaces"] = ["com.vzome.core.edits.ColorMappers.ColorMapper","java.util.function.Function"];



        /**
         * Gets standard color of the nearest predefined orbit using the symmetry's standard color scheme
         * @extends com.vzome.core.edits.ManifestationColorMappers.NearestSpecialOrbitColorMap
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

            constructor(symm: com.vzome.core.editor.api.OrbitSource) {
                super(symm);
                this.specialOrbits = null;
            }
        }
        NearestPredefinedOrbitColorMap["__class"] = "com.vzome.core.edits.ManifestationColorMappers.NearestPredefinedOrbitColorMap";
        NearestPredefinedOrbitColorMap["__interfaces"] = ["com.vzome.core.edits.ColorMappers.ColorMapper","java.util.function.Function"];



        /**
         * Gets standard color of the nearest predefined orbit based on the centroid of each manifestation
         * @extends com.vzome.core.edits.ManifestationColorMappers.CentroidNearestSpecialOrbitColorMap
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

            constructor(symm: com.vzome.core.editor.api.OrbitSource) {
                super(symm);
                this.specialOrbits = null;
            }
        }
        CentroidNearestPredefinedOrbitColorMap["__class"] = "com.vzome.core.edits.ManifestationColorMappers.CentroidNearestPredefinedOrbitColorMap";
        CentroidNearestPredefinedOrbitColorMap["__interfaces"] = ["com.vzome.core.edits.ColorMappers.ColorMapper","java.util.function.Function"];


    }

}


com.vzome.core.edits.ManifestationColorMappers.__static_initialize();
